package com.github.orbyfied.carbon.event.exception;

import com.github.orbyfied.carbon.event.EventBus;

public class InternalBusException extends EventException {

    public InternalBusException(EventBus bus, String message) {
        super(bus, message);
    }

    public InternalBusException(EventBus bus, Exception e) {
        super(bus, e);
    }

    public InternalBusException(EventBus bus, String msg, Exception e) {
        super(bus, msg, e);
    }

}
