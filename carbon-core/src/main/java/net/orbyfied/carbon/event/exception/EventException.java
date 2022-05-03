package net.orbyfied.carbon.event.exception;

import net.orbyfied.carbon.event.EventBus;

public class EventException extends RuntimeException {

    final EventBus bus;

    public EventException(EventBus bus, String message) {
        super(message);
        this.bus = bus;
    }

    public EventException(EventBus bus, Exception e) {
        super(e);
        this.bus = bus;
    }

    public EventException(EventBus bus, String msg, Exception e) {
        super(msg, e);
        this.bus = bus;
    }

    public EventBus getBus() {
        return bus;
    }

}
