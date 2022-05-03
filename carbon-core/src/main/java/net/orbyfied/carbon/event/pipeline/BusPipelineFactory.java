package net.orbyfied.carbon.event.pipeline;

import net.orbyfied.carbon.event.EventBus;

public interface BusPipelineFactory {

    PipelineAccess createPipeline(
            EventBus bus,
            Class eventClass
    );

}
