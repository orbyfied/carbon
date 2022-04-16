package com.github.orbyfied.carbon.event;

import com.github.orbyfied.carbon.event.exception.EventInvocationException;
import com.github.orbyfied.carbon.event.exception.InternalBusException;
import com.github.orbyfied.carbon.event.exception.InvalidEventException;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The main event system class.
 */
public class EventBus {

    /**
     * Listeners mapped by class.
     */
    private final Object2ObjectOpenHashMap<Class<? extends EventListener>, RegisteredListener> listenersByClass = new Object2ObjectOpenHashMap<>();

    /**
     * Listeners stored linearly.
     */
    private final ArrayList<RegisteredListener> listeners = new ArrayList<>();

    /**
     * Cache for event pipelines.
     */
    private final Object2ObjectOpenHashMap<Class<? extends BusEvent>, PipelineAccess<BusEvent>> eventPipelineCache = new Object2ObjectOpenHashMap<>();

    /**
     * Registers a listener instance by creating
     * a {@link RegisteredListener} and adding it
     * to the registrations in this event bus.
     * @param listener The listener instance.
     * @return The new {@link RegisteredListener} instance.
     */
    public RegisteredListener register(EventListener listener) {
        // create and add registered listener
        RegisteredListener rl = new RegisteredListener(this, listener);
        listeners.add(rl);
        listenersByClass.put(rl.klass, rl);

        // parse and register
        rl.parse().register();

        // return
        return rl;
    }

    /**
     * Checks if the listeners class
     * was registered already.
     * @param listener The listener object.
     * @return True/false.
     */
    public boolean isRegistered(EventListener listener) {
        return listenersByClass.containsKey(listener.getClass());
    }

    /**
     * Checks if the supplied class
     * was registered already.
     * @param listener The listener object.
     * @return True/false.
     */
    public boolean isRegistered(Class<? extends EventListener> listener) {
        return listenersByClass.containsKey(listener);
    }

    /**
     * Gets the latest registered listener
     * with the specified class.
     * @param klass The class.
     * @return The last registered listener.
     */
    public RegisteredListener getRegistered(Class<? extends EventListener> klass) {
        return listenersByClass.get(klass);
    }

    /**
     * Gets all registered listeners for
     * the specified class.
     * @param klass The class.
     * @return An unmodifiable list of listeners.
     */
    public List<RegisteredListener> getAllRegistered(Class<? extends EventListener> klass) {
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

        // return
        return this;
    }

    /**
     * Unregisters the last listener of
     * the listeners type.
     * @param listener The listener.
     * @return This.
     */
    public EventBus unregisterLast(EventListener listener) {
        unregister(listenersByClass.get(listener.getClass()));
        return this;
    }

    /**
     * Unregisters the last listener of
     * the specified type.
     * @param klass The listener class.
     * @return This.
     */
    public EventBus unregisterLast(Class<? extends EventListener> klass) {
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
    public EventBus bake(Class<? extends BusEvent> event) {
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
     * @param fclass The pipeline provider class.
     * @param event The event.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    public <E extends BusEvent> EventBus post(Class<E> fclass, E event) {
        // get pipeline for event
        final PipelineAccess<E> acc = (PipelineAccess<E>) getPipelineFor(fclass);

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
     * for this event bus from either the cache
     * or the {@link BusEvent#getPipeline(EventBus)} method.
     * @param event The event class.
     * @return The pipeline or null if the event type
     *         is invalid or an error occurred.
     */
    @SuppressWarnings("unchecked")
    public PipelineAccess<BusEvent> getPipelineFor(Class<? extends BusEvent> event) {
        // try to get from cache
        PipelineAccess<BusEvent> pipeline = eventPipelineCache.get(event);
        if (pipeline != null)
            return pipeline;

        // retrieve and cache
        try {
            Method getPipeline;
            try {
                getPipeline = event.getDeclaredMethod("getPipeline", EventBus.class);
            } catch (NoSuchMethodException e) {
                // throw exception
                throw new InvalidEventException(this, event, "pipeline provider { PipelineAccess<E> getPipeline(EventBus); } not implemented");
            }

            pipeline = (PipelineAccess<BusEvent>) getPipeline.invoke(null, this);
            eventPipelineCache.put(event, pipeline); // cache
            return pipeline;
        } catch (Exception e) {
            // throw internal exception
            throw new InternalBusException(this, "internal exception while retrieving event pipeline from '" +
                    event.getName() + "'", e);
        }
    }

}
