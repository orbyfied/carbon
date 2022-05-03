package net.orbyfied.carbon.event.pipeline;

public interface Pipeline<E, A extends PipelineHandlerAction> extends PipelineAccess<E> {

    Pipeline<E, A> push(E event);

    A handler(Handler handler);

}
