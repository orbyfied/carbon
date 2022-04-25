package com.github.orbyfied.carbon.event.util;

import com.github.orbyfied.carbon.event.BusEvent;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.pipeline.Handler;
import com.github.orbyfied.carbon.event.pipeline.Pipeline;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;

public class Pipelines {

    private Pipelines() { }

    @SuppressWarnings("unchecked")
    public static PipelineAccess parental(EventBus bus,
                                          Class<?> klass) {
        final Class<?>[] parents = klass.getClasses();
        return new PipelineAccess() {
            Pipeline pipeline = new Pipeline<>();

            @Override
            public PipelineAccess push(Object event) {
                pipeline.push(event);
                for (Class<?> parent : parents) {
                    PipelineAccess p = bus.getPipelineOrNull(parent);
                    if (p == null)
                        continue;
                    p.push(event);
                }
                return this;
            }

            @Override
            public PipelineAccess register(Handler handler) {
                return this;
            }

            @Override
            public Pipeline base() {
                return pipeline;
            }
        };
    }

    public static PipelineAccess<BusEvent> mono(EventBus bus) {
        return new PipelineAccess<>() {
            final Pipeline<BusEvent> pipeline = new Pipeline<>();

            @Override
            public PipelineAccess<BusEvent> push(BusEvent event) {
                pipeline.push(event);
                return this;
            }

            @Override
            public PipelineAccess<BusEvent> register(Handler<BusEvent> handler) {
                pipeline.register(handler);
                return this;
            }

            @Override
            public Pipeline<BusEvent> base() {
                return pipeline;
            }
        };
    }
}
