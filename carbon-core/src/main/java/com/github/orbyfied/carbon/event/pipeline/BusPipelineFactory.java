package com.github.orbyfied.carbon.event.pipeline;

import com.github.orbyfied.carbon.event.EventBus;

public interface BusPipelineFactory {

    PipelineAccess createPipeline(
            EventBus bus,
            Class eventClass
    );

}
