package com.github.orbyfied.carbon.event;

import com.github.orbyfied.carbon.event.pipeline.Event;
import com.github.orbyfied.carbon.event.pipeline.Handler;
import com.github.orbyfied.carbon.event.pipeline.Pipeline;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;
import com.github.orbyfied.carbon.event.pipeline.impl.BasicPipeline;

/**
 * An event that is invoked by
 * an event bus. Contains more information
 * about the bus and invocation.
 *
 * Most importantly: it processes it's
 * own pipeline for more functionality.
 */
public class BusEvent extends Event {

    /**
     * This has to be implemented by
     * ALL events, and it must not
     * be obfuscated. It creates it's
     * own pipeline for processing.
     * @param bus The event bus.
     * @return The pipeline.
     */
    public static PipelineAccess<BusEvent> getPipeline(EventBus bus) { return null; }

}
