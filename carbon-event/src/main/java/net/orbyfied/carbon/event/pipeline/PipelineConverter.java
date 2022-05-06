package net.orbyfied.carbon.event.pipeline;

// TODO: better name lmao
public interface PipelineConverter<T extends Pipeline> {

    T convert(Pipeline in);

}
