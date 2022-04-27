package com.github.orbyfied.carbon.event.pipeline;

// S = self
public interface PipelineHandlerAction<S extends PipelineHandlerAction, E> {

    Handler<E>     get();

    Pipeline<E, S> back();

    Pipeline<E, S> register();

    Pipeline<E, S> unregister();

}
