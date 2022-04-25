package com.github.orbyfied.carbon.event;

import com.github.orbyfied.carbon.event.pipeline.Event;
import com.github.orbyfied.carbon.event.pipeline.Handler;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;
import com.github.orbyfied.carbon.event.pipeline.Pipeline;

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

    /* ----------- Pipelines ------------- */

    public static PipelineAccess<BusEvent> createJoiningPipeline(EventBus bus, Class<? extends BusEvent> childClass) {
        return new PipelineAccess<>() {

            final Pipeline<BusEvent> my = new Pipeline<>();
            final PipelineAccess child = bus.getPipelineFor(childClass);

            @Override
            public PipelineAccess<BusEvent> push(BusEvent event) {
                my.push(event);
                child.push(event);
                return this;
            }

            @Override
            public PipelineAccess<BusEvent> register(Handler<BusEvent> handler) {
                my.register(handler);
                return this;
            }

            @Override
            public Pipeline<BusEvent> base() {
                return my;
            }
        };
    }

}
