package com.github.orbyfied.carbon.event.service;

import com.github.orbyfied.carbon.event.EventBus;

/**
 * A service that can be applied
 * to an event bus.
 */
public interface EventService {

    EventBus getBus();

}
