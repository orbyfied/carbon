package com.github.orbyfied.carbon.event;

import com.github.orbyfied.carbon.event.pipeline.Handler;
import com.github.orbyfied.carbon.event.pipeline.Pipeline;

/**
 * Represents a handler bound to a
 * specific event bus and registered
 * listener.
 *
 * It delegates the events to another
 * handler if available.
 */
public class BusHandler implements Handler<BusEvent> {

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
     * The pipeline that this handler
     * is part of.
     */
    final Pipeline<BusEvent> pipeline;

    public BusHandler(EventBus bus, RegisteredListener listener, Pipeline<BusEvent> pipeline) {
        this.bus = bus;
        this.listener = listener;
        this.pipeline = pipeline;
    }

    /**
     * The delegate handler.
     */
    Handler<BusEvent> delegate;

    /**
     * The toggle status of this handler.
     */
    int toggleStatus = 0;

    /* ----- Handler ----- */

    @Override
    public void handle(BusEvent event) {
        if (!isEnabled()) return;
        if (delegate != null)
            delegate.handle(event);
    }

    /**
     * Registers it to it's assigned
     * pipeline.
     */
    public void register() {
        pipeline.addLast(this);
    }

    public void destroy() {
        // TODO: implement
    }

    /* Getters and setters. */

    public boolean isEnabled() {
        if (toggleStatus == 0) return listener.enabled;
        return toggleStatus != -1;
    }

    public int getStatus() {
        return toggleStatus;
    }

    public void setStatus(int s) {
        this.toggleStatus = s;
    }

    public Handler<BusEvent> getDelegate() {
        return this.delegate;
    }

    public void setDelegate(Handler<BusEvent> handler) {
        this.delegate = handler;
    }

}
