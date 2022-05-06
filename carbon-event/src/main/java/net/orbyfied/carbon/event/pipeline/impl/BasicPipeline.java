package net.orbyfied.carbon.event.pipeline.impl;

import net.orbyfied.carbon.event.pipeline.Handler;
import net.orbyfied.carbon.event.pipeline.Pipeline;
import net.orbyfied.carbon.event.pipeline.PipelineConverter;
import net.orbyfied.carbon.event.pipeline.PipelineHandlerAction;

import java.util.ArrayList;

/**
 * A list of handlers capable of processing events.
 * @param <E> The type of the event.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BasicPipeline<E> implements Pipeline<E, PipelineHandlerAction> {

    /**
     * The handler list.
     */
    private ArrayList<Handler<E>> handlers = new ArrayList<>();

    /* Getters. */

    public ArrayList<Handler<E>> handlers() {
        return handlers;
    }

    public int size() {
        return handlers.size();
    }

    /**
     * Publishes/pushes an event to
     * the pipeline for it to be
     * handled.
     * @param event The event.
     * @return This.
     */
    @Override
    public BasicPipeline<E> push(E event) {
        int l = handlers.size();
        if (l == 0) return this;

        for (int i = 0; i < l; i++) {
            Handler<E> handler = handlers.get(i);
            handler.handle(event);
        }

        return this;
    }

    @Override
    public PipelineHandlerAction<PipelineHandlerAction, E> handler(Handler handler) {
        return new PipelineHandlerAction<>() {
            @Override
            public Handler<E> get() {
                return handler;
            }

            @Override
            public Pipeline<E, PipelineHandlerAction> back() {
                return BasicPipeline.this;
            }

            @Override
            public Pipeline<E, PipelineHandlerAction> register() {
                addLast(handler);
                return BasicPipeline.this;
            }

            @Override
            public Pipeline<E, PipelineHandlerAction> unregister() {
                handlers.remove(handler);
                return BasicPipeline.this;
            }
        };
    }

    @Override
    public BasicPipeline<E> base() {
        return this;
    }

    @Override
    public <T extends Pipeline<E, ? extends PipelineHandlerAction>> T base(PipelineConverter<T> converter) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a handler to the tail of
     * the pipeline.
     * @param handler The handler.
     * @return This.
     */
    public BasicPipeline<E> addLast(Handler<E> handler) {
        handlers.add(handler);
        return this;
    }

}
