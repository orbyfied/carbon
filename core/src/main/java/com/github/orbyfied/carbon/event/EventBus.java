package com.github.orbyfied.carbon.event;

import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;

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
    private final HashMap<Class<? extends Listener>, RegisteredListener> listenersByClass = new HashMap<>();

    /**
     * Listeners stored linearly.
     */
    private final ArrayList<RegisteredListener> listeners = new ArrayList<>();

    /**
     * Cache for event pipelines.
     */
    private final HashMap<Class<? extends BusEvent>, PipelineAccess<BusEvent>> eventPipelineCache = new HashMap<>();

    /**
     * Registers a listener instance by creating
     * a {@link RegisteredListener} and adding it
     * to the registrations in this event bus.
     * @param listener The listener instance.
     * @return The new {@link RegisteredListener} instance.
     */
    public RegisteredListener register(Listener listener) {
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
    public boolean isRegistered(Listener listener) {
        return listenersByClass.containsKey(listener.getClass());
    }

    /**
     * Checks if the supplied class
     * was registered already.
     * @param listener The listener object.
     * @return True/false.
     */
    public boolean isRegistered(Class<? extends Listener> listener) {
        return listenersByClass.containsKey(listener);
    }

    /**
     * Gets the latest registered listener
     * with the specified class.
     * @param klass The class.
     * @return The last registered listener.
     */
    public RegisteredListener getRegistered(Class<? extends Listener> klass) {
        return listenersByClass.get(klass);
    }

    /**
     * Gets all registered listeners for
     * the specified class.
     * @param klass The class.
     * @return An unmodifiable list of listeners.
     */
    public List<RegisteredListener> getAllRegistered(Class<? extends Listener> klass) {
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
     */
    public void unregister(RegisteredListener listener) {
        if (listener == null)
            return;

        // destroy listener
        listener.destroy();

        // remove from lists
        listeners.remove(listener);
        listenersByClass.remove(listener.klass, listener);
    }

    /**
     * Unregisters the last listener of
     * the listeners type.
     * @param listener The listener.
     */
    public void unregisterLast(Listener listener) {
        unregister(listenersByClass.get(listener.getClass()));
    }

    /**
     * Unregisters the last listener of
     * the specified type.
     * @param klass The listener class.
     */
    public void unregisterLast(Class<? extends Listener> klass) {
        unregister(listenersByClass.get(klass));
    }

    /**
     * Unregisters all registered listeners
     * of the specified type.
     * @param klass The type.
     */
    public void unregisterAll(Class<? extends Listener> klass) {
        for (RegisteredListener rl : listeners)
            if (rl.klass == klass) unregister(rl);
    }

    /**
     * Posts an event to the event bus.
     * Uses the events class as the pipeline provider.
     * @param event The event.
     */
    public void post(BusEvent event) {
        post(event.getClass(), event);
    }

    /**
     * Posts an event to the event bus
     * through the pipeline supplied by
     * the supplied class.
     * @param fclass The pipeline provider class.
     * @param event The event.
     */
    public void post(Class<? extends BusEvent> fclass, BusEvent event) {
        getPipelineFor(fclass).push(event);
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
        if (pipeline != null) return pipeline;

        System.out.println("not cached: " + event);

        // retrieve and cache
        try {
            Method getPipeline;
            try {
                getPipeline = event.getDeclaredMethod("getPipeline", EventBus.class);
            } catch (NoSuchMethodException e) { return null; }
            pipeline = (PipelineAccess<BusEvent>) getPipeline.invoke(null, this);
            eventPipelineCache.put(event, pipeline);
            return pipeline;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
