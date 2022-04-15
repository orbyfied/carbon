package com.github.orbyfied.carbon.event;

import com.github.orbyfied.carbon.event.pipeline.Pipeline;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A listener that has been registered.
 */
public class RegisteredListener {

    /**
     * The lookup to use when parsing methods.
     */
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * The event bus this listener belongs to.
     */
    final EventBus bus;

    /**
     * The handlers this listener holds.
     */
    final List<BusHandler> handlers = new ArrayList<>();

    /**
     * The listener object.
     */
    final EventListener obj;

    /**
     * The listener objects class.
     */
    final Class<? extends EventListener> klass;

    public RegisteredListener(EventBus bus, EventListener obj) {
        Objects.requireNonNull(bus, "the event bus cannot be null");
        Objects.requireNonNull(obj, "the object cannot be null");
        this.bus = bus;
        this.obj = obj;

        this.klass = obj.getClass();
    }

    /**
     * Is this listener enabled?
     */
    boolean enabled = true;

    /**
     * Has this listener been registered?
     */
    boolean registered = false;

    /**
     * Unregisters and destroys this listener.
     */
    public void destroy() {
        for (BusHandler handler : handlers)
            handler.destroy();
    }

    /**
     * Parses the class to find all
     * event handler methods and creates
     * handlers for them.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    public RegisteredListener parse() {
        try {

            // get and iterate methods
            Method[] methods = klass.getDeclaredMethods();
            for (Method method : methods) {
                // check if it is a valid event method
                if (!method.isAnnotationPresent(EventHandler.class)) continue;
                if (method.getParameterCount() == 0) continue;
                if (!BusEvent.class.isAssignableFrom(method.getParameterTypes()[0])) continue;

                // get event type and pipeline
                Class<? extends BusEvent> eventType = (Class<? extends BusEvent>) method.getParameterTypes()[0];
                Pipeline<? extends BusEvent> pipeline = bus.getPipelineFor(eventType).base();

                // get method handle for quick invocation
                final MethodHandle handle = LOOKUP.unreflect(method);

                // create and add handler
                BusHandler handler = new BusHandler(bus, this, (Pipeline<BusEvent>) pipeline);
                handler.setDelegate(event -> {
                    try { handle.invoke(obj, event); }
                    catch (Throwable e) { e.printStackTrace(); }
                });
                handlers.add(handler);
            }

        } catch (Exception e) { e.printStackTrace(); }
        return this;
    }

    /**
     * Registers all handlers.
     * @return This.
     */
    public RegisteredListener register() {
        // check and set registered
        if (registered) return this;
        registered = true;

        // register handlers
        for (BusHandler handler : handlers)
            handler.register();

        // return
        return this;
    }

    /* Getters and setters. */

    public void setEnabled(boolean b) {
        this.enabled = b;
    }


    public boolean isEnabled() {
        return enabled;
    }

}
