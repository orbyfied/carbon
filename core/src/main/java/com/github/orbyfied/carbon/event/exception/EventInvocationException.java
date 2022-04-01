package com.github.orbyfied.carbon.event.exception;

import com.github.orbyfied.carbon.event.EventBus;

public class EventInvocationException extends EventException {

    public EventInvocationException(EventBus bus, String message) {
        super(bus, message);
    }

    public EventInvocationException(EventBus bus, Exception e) {
        super(bus, e);
    }

    public EventInvocationException(EventBus bus, String msg, Exception e) {
        super(bus, msg, e);
    }

}
