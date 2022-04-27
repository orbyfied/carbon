package com.github.orbyfied.carbon.event.pipeline.impl;

import com.github.orbyfied.carbon.event.pipeline.Handler;
import com.github.orbyfied.carbon.event.pipeline.Pipeline;
import com.github.orbyfied.carbon.event.pipeline.PipelineHandlerAction;

public class BasicPipelineHandlerAction<E> implements PipelineHandlerAction<BasicPipelineHandlerAction, E> {

    protected final BasicPipeline<E> pipeline;
    protected final Handler<E> handler;

    public BasicPipelineHandlerAction(BasicPipeline<E> pipeline,
                                      Handler<E> handler) {
        this.pipeline = pipeline;
        this.handler  = handler;
    }

    @Override
    public Handler<E> get() {
        return handler;
    }

    @Override
    public BasicPipeline<E> back() {
        return pipeline;
    }

    @Override
    public BasicPipeline<E> register() {
        pipeline.addLast(handler);
        return pipeline;
    }

    @Override
    public BasicPipeline<E> unregister() {
        pipeline.handlers().remove(handler);
        return pipeline;
    }

}
