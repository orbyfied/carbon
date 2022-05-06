package net.orbyfied.carbon.event;

import net.orbyfied.carbon.event.pipeline.Event;
import net.orbyfied.carbon.event.pipeline.PipelineAccess;

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
