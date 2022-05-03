package net.orbyfied.carbon.event.service;

import net.orbyfied.carbon.event.EventBus;

/**
 * A service that can be applied
 * to an event bus.
 */
public interface EventService {

    EventBus getBus();

}
