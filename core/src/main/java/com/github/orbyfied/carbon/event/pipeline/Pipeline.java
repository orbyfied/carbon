package com.github.orbyfied.carbon.event.pipeline;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A list of handlers capable of processing events.
 * @param <E> The type of the event.
 */
public class Pipeline<E> implements PipelineAccess<E> {

    /**
     * The handler list.
     */
    private ArrayList<Handler<E>> handlers = new ArrayList<>();

    /* Getters. */

    public ArrayList<Handler<E>> handlers() {
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
        int l = handlers.size();
        if (l == 0) return this;

        for (int i = 0; i < l; i++) {
            Handler<E> handler = handlers.get(i);
            handler.handle(event);
        }

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
        handlers.add(handler);
        return this;
    }

}
