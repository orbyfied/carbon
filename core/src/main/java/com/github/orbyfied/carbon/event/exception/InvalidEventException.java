package com.github.orbyfied.carbon.event.exception;

import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.pipeline.Event;

public class InvalidEventException extends EventException {

    final Class<? extends Event> eventClass;

    public InvalidEventException(EventBus bus, Class<? extends Event> eventClass, String message) {
        super(bus, message);
        this.eventClass = eventClass;
    }

    public InvalidEventException(EventBus bus, Class<? extends Event> eventClass, Exception e) {
        super(bus, e);
        this.eventClass = eventClass;
    }

    public InvalidEventException(EventBus bus, Class<? extends Event> eventClass, String msg, Exception e) {
        super(bus, msg, e);
        this.eventClass = eventClass;
    }

    public Class<? extends Event> getEventClass() {
        return eventClass;
    }

    @Override
    public String getMessage() {
        return "for event type " + eventClass.getName() + ": " + super.getMessage();
    }

}
