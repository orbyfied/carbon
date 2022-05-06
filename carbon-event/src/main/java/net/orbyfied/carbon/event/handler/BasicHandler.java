package net.orbyfied.carbon.event.handler;

import net.orbyfied.carbon.event.BusHandler;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.event.RegisteredListener;

import java.lang.annotation.*;

/**
 * Denotes a basic event handler method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@HandlerDescriptor
public @interface BasicHandler {

    HandlerProvider<BasicHandler> PROVIDER = new Provider();

    class Provider implements HandlerProvider<BasicHandler> {

        @Override
        public void configure(BusHandler handler, BasicHandler desc) { }

        @Override
        public void register(EventBus bus, BusHandler handler) {
            bus.getPipelineFor(handler.getProperty(RegisteredListener.HANDLER_PROPERTY_EVENT_CLASS))
                    .base()
                    .handler(handler).register();
        }

        @Override
        public void unregister(EventBus bus, BusHandler handler) {
            bus.getPipelineFor(handler.getProperty(RegisteredListener.HANDLER_PROPERTY_EVENT_CLASS))
                    .base()
                    .handler(handler).unregister();
        }

    }

}
