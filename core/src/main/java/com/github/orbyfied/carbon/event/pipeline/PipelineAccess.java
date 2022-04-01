package com.github.orbyfied.carbon.event.pipeline;

/**
 * Interface for virtual pipeline access.
 * Allows for non-linear pipelines and complex
 * joining of pipeline without extra overhead.
 */
public interface PipelineAccess<E extends Event> {

    /**
     * Pushes an event to the pipeline.
     * @param event The event.
     * @return This.
     */
    PipelineAccess<E> push(E event);

    /**
     * Registers a new handler to the pipeline.
     * Usually registers it to the base pipeline.
     * @param handler The handler to register.
     * @return This.
     */
    PipelineAccess<E> register(Handler<E> handler);

    /**
     * Gets the base pipeline. Expected to be
     * the direct pipeline owned by the owner
     * of this virtual pipeline, but can be different.
     * @return The base pipeline.
     */
    Pipeline<E> base();

}
