package net.orbyfied.carbon.event.pipeline.impl;

import net.orbyfied.carbon.event.handler.priority.HandlerPriority;
import net.orbyfied.carbon.event.pipeline.PipelineHandlerAction;

public interface OrderedPipelineHandlerAction<S extends PipelineHandlerAction, E> extends PipelineHandlerAction<S, E> {

    OrderedPipelineHandlerAction<S, E> prioritized(HandlerPriority priority);

}
