package com.github.orbyfied.carbon.event.pipeline;

import java.util.LinkedList;

/**
 * A list of handlers capable of processing events.
 * @param <E> The type of the event.
 */
public class Pipeline<E extends Event> implements PipelineAccess<E> {

    /**
     * The handler list.
     */
    private LinkedList<Handler<E>> handlers = new LinkedList<>();

    /* Getters. */

    public LinkedList<Handler<E>> handlers() {
        return handlers;
    }

    public int size() {
        return handlers.size();
    }

    /**
     * Publishes/pushes an event to
     * the pipeline for it to be
     * handled.
     * @param event The event.
     * @return This.
     */
    @Override
    public Pipeline<E> push(E event) {
        if (handlers.size() == 0) return this;
        for (Handler<E> handler : handlers)
            handler.handle(event);
        return this;
    }

    @Override
    public Pipeline<E> register(Handler<E> handler) {
        return addLast(handler);
    }

    @Override
    public Pipeline<E> base() {
        return this;
    }

    /**
     * Adds a handler to the tail of
     * the pipeline.
     * @param handler The handler.
     * @return This.
     */
    public Pipeline<E> addLast(Handler<E> handler) {
        handlers.addLast(handler);
        return this;
    }

}
