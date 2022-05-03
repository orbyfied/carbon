package net.orbyfied.carbon.event.util;

import net.orbyfied.carbon.event.BusEvent;
import net.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.pipeline.*;
import net.orbyfied.carbon.event.pipeline.PipelineAccess;
import net.orbyfied.carbon.event.pipeline.PipelineConverter;
import net.orbyfied.carbon.event.pipeline.impl.BasicPipeline;
import net.orbyfied.carbon.event.pipeline.Pipeline;
import net.orbyfied.carbon.event.pipeline.PipelineHandlerAction;

public class Pipelines {

    private Pipelines() { }

    @SuppressWarnings("unchecked")
    public static PipelineAccess parental(EventBus bus,
                                          Class<?> klass) {
        final Class<?>[] parents = klass.getClasses();
        return new PipelineAccess() {
            Pipeline pipeline = new BasicPipeline<>();

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
            public Pipeline base() {
                return pipeline;
            }

            @Override
            public Pipeline base(PipelineConverter converter) {
                pipeline = converter.convert(pipeline);
                return null;
            }
        };
    }

    public static PipelineAccess<BusEvent> mono(EventBus bus) {
        return new PipelineAccess<>() {
            Pipeline pipeline = new BasicPipeline<>();

            @Override
            public PipelineAccess<BusEvent> push(BusEvent event) {
                pipeline.push(event);
                return this;
            }

            @Override
            public Pipeline<BusEvent, ? extends PipelineHandlerAction> base() {
                return pipeline;
            }

            @Override
            public Pipeline base(PipelineConverter converter) {
                pipeline = converter.convert(pipeline);
                return null;
            }
        };
    }
}
