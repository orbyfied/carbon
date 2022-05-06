package net.orbyfied.carbon.event.pipeline.impl;

import net.orbyfied.carbon.event.pipeline.Pipeline;

public interface OrderedPipeline<E, A extends OrderedPipelineHandlerAction> extends Pipeline<E, A> {

}
