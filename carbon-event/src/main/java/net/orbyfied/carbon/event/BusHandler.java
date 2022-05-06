package net.orbyfied.carbon.event;

import net.orbyfied.carbon.event.handler.HandlerProvider;
import net.orbyfied.carbon.event.pipeline.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a handler bound to a
 * specific event bus and registered
 * listener.
 *
 * It delegates the events to another
 * handler if available.
 */
public class BusHandler implements Handler<Object> {

    // the handler is explicitly disabled
    public static final int STATUS_DISABLED = -1;
    // the handler takes its status from its parent listener
    public static final int STATUS_DELEGATE = 0;
    // the handler is explicitly enabled
    public static final int STATUS_ENABLED  = 1;

    ///////////////////////////////////////

    /**
     * The registered listener this
     * handler is bound to.
     */
    final RegisteredListener listener;

    /**
     * The bus this handler belongs to.
     */
    final EventBus bus;

    /**
     * The handler provider.
     */
    final HandlerProvider<?> provider;

    /**
     * The properties on this object.
     */
    Map<Object, Object> properties;

    public BusHandler(EventBus bus, RegisteredListener listener, HandlerProvider<?> provider) {
        this.bus = bus;
        this.listener = listener;
        this.provider = provider;
    }

    /**
     * The delegate handler.
     */
    Handler<Object> delegate;

    /**
     * The toggle status of this handler.
     */
    int toggleStatus = 0;

    /**
     * If this handler has been registered.
     */
    boolean registered = false;

    /* ----- Handler ----- */

    @Override
    public void handle(Object event) {
        if (!isEnabled()) return;
        if (delegate != null)
            delegate.handle(event);
    }

    /**
     * Registers it to it's assigned
     * pipeline.
     */
    public void register() {
        provider.register(bus, this);
        registered = true;
    }

    public void destroy() {
        provider.unregister(bus, this);
        // TODO: implement
    }

    public BusHandler withProperty(Object key, Object val) {
        if (properties == null)
            properties = new HashMap<>();
        properties.put(key, val);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Object key) {
        return properties == null ? null : (T) properties.get(key);
    }

    /* Getters and setters. */

    public boolean isEnabled() {
        if (toggleStatus == 0) return listener.enabled;
        return toggleStatus != -1;
    }

    public boolean isRegistered() {
        return registered;
    }

    public int getStatus() {
        return toggleStatus;
    }

    public void setStatus(int s) {
        this.toggleStatus = s;
    }

    public Handler<Object> getDelegate() {
        return this.delegate;
    }

    public BusHandler setDelegate(Handler<Object> handler) {
        this.delegate = handler;
        return this;
    }

    public HandlerProvider<?> getProvider() {
        return provider;
    }

    public EventBus getBus() {
        return bus;
    }

    public RegisteredListener getListener() {
        return listener;
    }

    public Map<Object, Object> getProperties() {
        return properties;
    }

}
