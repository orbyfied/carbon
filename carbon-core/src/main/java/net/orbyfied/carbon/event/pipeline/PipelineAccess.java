package net.orbyfied.carbon.event.pipeline;

/**
 * Interface for virtual pipeline access.
 * Allows for non-linear pipelines and complex
 * joining of pipeline without extra overhead.
 */
public interface PipelineAccess<E> {

    /**
     * Pushes an event to the pipeline.
     * @param event The event.
     * @return This.
     */
    PipelineAccess<E> push(E event);

    /**
     * Gets the base pipeline. Expected to be
     * the direct pipeline owned by the owner
     * of this virtual pipeline, but can be different.
     * @return The base pipeline.
     */
    Pipeline<E, ? extends PipelineHandlerAction> base();

    <T extends Pipeline<E, ? extends PipelineHandlerAction>> T base(PipelineConverter<T> converter);

}
