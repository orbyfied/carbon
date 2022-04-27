package com.github.orbyfied.carbon.event.exception;

import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.pipeline.Event;

public class InvalidEventException extends EventException {

    final Class<?> eventClass;

    public InvalidEventException(EventBus bus, Class<?> eventClass, String message) {
        super(bus, message);
        this.eventClass = eventClass;
    }

    public InvalidEventException(EventBus bus, Class<?> eventClass, Exception e) {
        super(bus, e);
        this.eventClass = eventClass;
    }

    public InvalidEventException(EventBus bus, Class<?> eventClass, String msg, Exception e) {
        super(bus, msg, e);
        this.eventClass = eventClass;
    }

    public Class<?> getEventClass() {
        return eventClass;
    }

    @Override
    public String getMessage() {
        return "for event type " + eventClass.getSimpleName() + ": " + super.getMessage();
    }

}
