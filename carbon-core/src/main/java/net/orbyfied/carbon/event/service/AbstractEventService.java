package net.orbyfied.carbon.event.service;

import net.orbyfied.carbon.event.EventBus;

public class AbstractEventService implements EventService {

    protected final EventBus bus;

    public AbstractEventService(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public EventBus getBus() {
        return bus;
    }

}
