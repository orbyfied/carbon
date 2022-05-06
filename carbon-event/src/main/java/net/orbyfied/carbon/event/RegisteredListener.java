package net.orbyfied.carbon.event;

import net.orbyfied.carbon.event.handler.HandlerDescriptor;
import net.orbyfied.carbon.event.handler.HandlerProvider;
import net.orbyfied.carbon.event.pipeline.Handler;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
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

    /**
     * If this listener is dynamic.
     */
    boolean dynamic;

    public RegisteredListener(EventBus bus, EventListener obj) {
        Objects.requireNonNull(bus, "the event bus cannot be null");
        this.bus = bus;
        this.obj = obj;

        this.klass = obj == null ? null : obj.getClass();
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
                if (method.getParameterCount() == 0) continue;

                // search for handler descriptor
                Annotation desc = null;
                for (Annotation a : method.getDeclaredAnnotations()) {
                    if (a.annotationType().isAnnotationPresent(HandlerDescriptor.class)) {
                        desc = a;
                        break;
                    }
                }
                if (desc == null)
                    continue; // method isnt an event handler

                // get handler provider
                HandlerProvider provider;
                try {
                    Class<?> descClass = desc.annotationType();
                    Field field = descClass.getDeclaredField("PROVIDER");
                    field.setAccessible(true);
                    provider = (HandlerProvider) field.get(null);
                    if (provider == null)
                        throw new IllegalArgumentException("No provider set");
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid handler descriptor " + desc.getClass().getSimpleName(), e);
                }

                // get event type
                Class<? extends BusEvent> eventType = (Class<? extends BusEvent>) method.getParameterTypes()[0];

                // get method handle for quick invocation
                final MethodHandle handle = LOOKUP.unreflect(method);

                // create, configure and add handler
                BusHandler handler = new BusHandler(bus, this, provider) {

                    @Override
                    public void handle(Object event) {
                        if (!isEnabled()) return;

                        try { handle.invoke(obj, event); }
                        catch (Throwable e) { e.printStackTrace(); }
                    }

                };

                handler.withProperty(HANDLER_PROPERTY_EVENT_CLASS, eventType);
                provider.configure(handler, desc);

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

    /**
     * Configure if this listener is dynamic.
     * @param b True/false.
     * @return This.
     */
    public RegisteredListener dynamic(boolean b) {
        this.dynamic = b;
        return this;
    }

    /**
     * Adds any generic behaviour handler to this
     * listener. If dynamic, registers the handlers
     * on the fly.
     * @param klass The event class.
     * @param handler The event handler.
     * @param <E> The generic event type.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    public <E> RegisteredListener handle(Class<E> klass, Handler handler) {
        // create handler
        BusHandler h = new BusHandler(bus, this, HandlerProvider.forBasicPipeline(bus.getPipelineFor(klass).base()))
                .setDelegate(handler);

        // register dynamically if needed
        if (dynamic)
            h.register();

        // add handler to list
        handlers.add(h);

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

    public boolean isRegistered() {
        return registered;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    /////////////////////////////////////////////////

    public static final Object HANDLER_PROPERTY_EVENT_CLASS = new Object();

}
