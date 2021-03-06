package net.orbyfied.carbon.event.handler;

import net.orbyfied.carbon.event.BusHandler;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.event.pipeline.Pipeline;

import java.lang.annotation.Annotation;

public interface HandlerProvider<A extends Annotation> {

    void configure(BusHandler handler, A desc);

    void register(EventBus bus, BusHandler handler);

    void unregister(EventBus bus, BusHandler handler);

    //////////////////////////////////////////////////

    static HandlerProvider<?> forBasicPipeline(Pipeline pipeline) {
        return new HandlerProvider<>() {
            @Override
            public void configure(BusHandler handler, Annotation desc) { }

            @Override
            public void register(EventBus bus, BusHandler handler) {
                pipeline
                        .handler(handler)
                        .register();
            }

            @Override
            public void unregister(EventBus bus, BusHandler handler) {

            }
        };
    }

}
