package com.github.orbyfied.carbon.event.service;

import com.github.orbyfied.carbon.event.RegisteredListener;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;

public interface FunctionalEventService extends EventService {

    default void prePublish(Object event, PipelineAccess<?> pipeline) { }

    default void bake(Class<?> eventClass) { }

    default void registered(RegisteredListener listener) { }

    default void unregistered(RegisteredListener listener) { }

}
