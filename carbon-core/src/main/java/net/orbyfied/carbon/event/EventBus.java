package net.orbyfied.carbon.event;

import net.orbyfied.carbon.event.exception.EventInvocationException;
import net.orbyfied.carbon.event.exception.InternalBusException;
import net.orbyfied.carbon.event.exception.InvalidEventException;
import net.orbyfied.carbon.event.pipeline.BusPipelineFactory;
import net.orbyfied.carbon.event.pipeline.PipelineAccess;
import net.orbyfied.carbon.event.service.EventService;
import net.orbyfied.carbon.event.service.FunctionalEventService;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Method;
import java.util.*;

/**
 * The main event system class.
 */
public class EventBus {

    /**
     * Listeners mapped by class.
     */
    private final Object2ObjectOpenHashMap<Class<? extends net.orbyfied.carbon.event.EventListener>, RegisteredListener> listenersByClass = new Object2ObjectOpenHashMap<>();

    /**
     * Listeners stored linearly.
     */
    private final ArrayList<RegisteredListener> listeners = new ArrayList<>();

    /**
     * Cache for event pipelines.
     */
    private final Object2ObjectOpenHashMap<Class<?>, PipelineAccess<?>> eventPipelineCache = new Object2ObjectOpenHashMap<>();

    /**
     * The default pipeline factory for events
     * that don't explicitly specify a pipeline.
     */
    private BusPipelineFactory defaultPipelineFactory;

    /* Services. */
    private final ArrayList<EventService> servicesLinear = new ArrayList<>();
    private final HashMap<Class<? extends EventService>, EventService> servicesMapped = new HashMap<>();

    /**
     * Registers a listener instance by creating
     * a {@link RegisteredListener} and adding it
     * to the registrations in this event bus.
     * @param listener The listener instance.
     * @return The new {@link RegisteredListener} instance.
     */
    public RegisteredListener register(net.orbyfied.carbon.event.EventListener listener) {
        // create and add registered listener
        RegisteredListener rl = new RegisteredListener(this, listener);
        listeners.add(rl);
        listenersByClass.put(rl.klass, rl);

        // parse and register
        rl.parse().register();

        // call services
        {
            int l = servicesLinear.size();
            for (int i = 0; i < l; i++) {
                EventService service = servicesLinear.get(i);
                if (service instanceof FunctionalEventService fes) // functional
                    fes.registered(rl);
            }
        }

        // return
        return rl;
    }

    /**
     * Checks if the listeners class
     * was registered already.
     * @param listener The listener object.
     * @return True/false.
     */
    public boolean isRegistered(net.orbyfied.carbon.event.EventListener listener) {
        return listenersByClass.containsKey(listener.getClass());
    }

    /**
     * Checks if the supplied class
     * was registered already.
     * @param listener The listener object.
     * @return True/false.
     */
    public boolean isRegistered(Class<? extends net.orbyfied.carbon.event.EventListener> listener) {
        return listenersByClass.containsKey(listener);
    }

    /**
     * Gets the latest registered listener
     * with the specified class.
     * @param klass The class.
     * @return The last registered listener.
     */
    public RegisteredListener getRegistered(Class<? extends net.orbyfied.carbon.event.EventListener> klass) {
        return listenersByClass.get(klass);
    }

    /**
     * Gets all registered listeners for
     * the specified class.
     * @param klass The class.
     * @return An unmodifiable list of listeners.
     */
    public List<RegisteredListener> getAllRegistered(Class<? extends net.orbyfied.carbon.event.EventListener> klass) {
        List<RegisteredListener> list = new ArrayList<>();
        for (RegisteredListener l : listeners)
            if (l.klass == klass) list.add(l);
        return Collections.unmodifiableList(list);
    }

    /**
     * Gets all registered listeners.
     * @return An unmodifiable list of listeners.
     */
    public List<RegisteredListener> getAllRegistered() {
        return Collections.unmodifiableList(listeners);
    }

    /**
     * Unregisters a registered listener.
     * @param listener The registered listener.
     * @return This.
     */
    public EventBus unregister(RegisteredListener listener) {
        if (listener == null)
            return this;

        // destroy listener
        listener.destroy();

        // remove from lists
        listeners.remove(listener);
        listenersByClass.remove(listener.klass, listener);

        // call services
        {
            int l = servicesLinear.size();
            for (int i = 0; i < l; i++) {
                EventService service = servicesLinear.get(i);
                if (service instanceof FunctionalEventService fes) // functional
                    fes.unregistered(listener);
            }
        }

        // return
        return this;
    }

    /**
     * Unregisters the last listener of
     * the listeners type.
     * @param listener The listener.
     * @return This.
     */
    public EventBus unregisterLast(net.orbyfied.carbon.event.EventListener listener) {
        unregister(listenersByClass.get(listener.getClass()));
        return this;
    }

    /**
     * Unregisters the last listener of
     * the specified type.
     * @param klass The listener class.
     * @return This.
     */
    public EventBus unregisterLast(Class<? extends net.orbyfied.carbon.event.EventListener> klass) {
        unregister(listenersByClass.get(klass));
        return this;
    }

    /**
     * Unregisters all registered listeners
     * of the specified type.
     * @param klass The type.
     * @return This.
     */
    public EventBus unregisterAll(Class<? extends EventListener> klass) {
        for (RegisteredListener rl : listeners)
            if (rl.klass == klass) unregister(rl);
        return this;
    }

    /**
     * Bakes the event; prepares it.
     * Pre-caches the pipeline for an event.
     * This can significantly improve performance
     * on the first call.
     * @param event The event type.
     * @return This.
     */
    public EventBus bake(Class<?> event) {
        // cache pipeline
        getPipelineFor(event);

        // return
        return this;
    }

    /**
     * Posts an event to the event bus.
     * Uses the events class as the pipeline provider.
     * @param event The event.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    public <E extends BusEvent> EventBus post(E event) {
        return post((Class<E>) event.getClass(), event);
    }

    /**
     * Posts an event to the event bus
     * through the pipeline supplied by
     * the supplied class.
     * NOTE: Doesn't call any events, and
     * does not catch any errors.
     * @param fclass The pipeline provider class.
     * @param event The event.
     */
    @SuppressWarnings("unchecked")
    public <E> void postUnsafe(Class fclass, E event) {
        // get pipeline for event
        final PipelineAccess<E> acc = (PipelineAccess<E>) getPipelineFor(fclass);

        // post event
        acc.push(event);
    }

    /**
     * Posts an event to the event bus
     * through the pipeline supplied by
     * the supplied class. Calls all
     * functional event services before
     * posting.
     * @param fclass The pipeline provider class.
     * @param event The event.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    public <E> EventBus post(Class<E> fclass, E event) {
        // get pipeline for event
        final PipelineAccess<E> acc = (PipelineAccess<E>) getPipelineFor(fclass);

        // call services
        {
            int l = servicesLinear.size();
            for (int i = 0; i < l; i++) {
                EventService service = servicesLinear.get(i);
                if (service instanceof FunctionalEventService fes) // functional
                    fes.prePublish(event, acc);
            }
        }

        try {
            // post event
            acc.push(event);
        } catch (Exception e) {
            // throw invocation exception
            throw new EventInvocationException(this, "error occurred in event handler", e);
        }

        // return
        return this;
    }

    /**
     * Retrieves the pipeline of an event
     * for this event bus from either the cache, the
     * {@link BusEvent#getPipeline(EventBus)} method or
     * the {@link EventBus#defaultPipelineFactory}.
     * @param event The event class.
     * @return The pipeline or null if the event type
     *         is invalid or an error occurred.
     */
    @SuppressWarnings("unchecked")
    public PipelineAccess<?> getPipelineFor(Class<?> event) {
        // try to get from cache
        PipelineAccess<?> pipeline = eventPipelineCache.get(event);
        if (pipeline != null)
            return pipeline;

        // retrieve and cache
        try {
            try {
                // try to use pipeline getter method
                Method getPipeline = event.getDeclaredMethod("getPipeline", EventBus.class);
                pipeline = (PipelineAccess<BusEvent>) getPipeline.invoke(null, this);
            } catch (NoSuchMethodException e) {
                if (defaultPipelineFactory == null)
                    // throw exception
                    throw new InvalidEventException(this, event, "pipeline provider { PipelineAccess<E> getPipeline(EventBus); } not implemented");
                // create default pipeline
                pipeline = defaultPipelineFactory.createPipeline(this, event);
            }

            eventPipelineCache.put(event, pipeline); // cache
            return pipeline;
        } catch (InvalidEventException e) {
            throw e; // dont catch these
        } catch (Exception e) {
            // throw internal exception
            throw new InternalBusException(this, "internal exception while retrieving event pipeline from '" +
                    event.getName() + "'", e);
        }
    }

    public PipelineAccess<?> getPipelineOrNull(Class<?> event) {
        return eventPipelineCache.get(event);
    }

    public EventBus withDefaultPipelineFactory(BusPipelineFactory factory) {
        this.defaultPipelineFactory = factory;
        return this;
    }

    public List<EventService> getServicesLinear() {
        return Collections.unmodifiableList(servicesLinear);
    }

    public Map<Class<? extends EventService>, EventService> getServicesMapped() {
        return Collections.unmodifiableMap(servicesMapped);
    }

    @SuppressWarnings("unchecked")
    public <S extends EventService> S getService(Class<S> klass) {
        return (S) servicesMapped.get(klass);
    }

    public EventBus addService(EventService s) {
        servicesLinear.add(s);
        servicesMapped.put(s.getClass(), s);
        return this;
    }

    public EventBus removeService(EventService s) {
        servicesLinear.remove(s);
        servicesMapped.remove(s.getClass());
        return this;
    }

}
