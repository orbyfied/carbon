package com.github.orbyfied.carbon.event.pipeline;

/**
 * An event handler in a pipeline.
 * @param <E> The event type.
 */
public interface Handler<E> {

    /**
     * Handles the event.
     * @param event The event.
     */
    void handle(E event);

}
