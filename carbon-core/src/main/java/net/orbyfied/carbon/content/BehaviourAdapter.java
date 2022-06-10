package net.orbyfied.carbon.content;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.api.CarbonAPI;
import net.orbyfied.carbon.element.RegistrableElement;
import net.orbyfied.carbon.event.BusEvent;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.event.EventListener;
import net.orbyfied.carbon.event.RegisteredListener;
import net.orbyfied.carbon.event.pipeline.Handler;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BehaviourAdapter<S extends BehaviourAdapter, C extends RegistrableElement> {

    /**
     * The Carbon main instance.
     */
    private static Carbon main;

    static void initializeApi(CarbonAPI api) {
        main = api.getMain();
    }

    static void disableApi(CarbonAPI api) {
        main = null;
    }

    //////////////////////////////////////

    /**
     * The behaviour event bus.
     */
    protected final EventBus bus = new EventBus();

    /**
     * The generic event listener.
     */
    protected RegisteredListener genericListener;

    /**
     * If it should cancel the default event.
     */
    protected boolean cancelDefault = true;

    /**
     * Get the behaviour event bus.
     * @return The event bus.
     */
    public EventBus getBus() {
        return bus;
    }

    /**
     * Post a behaviour event to the bus.
     * @param event The behaviour event.
     * @return This.
     */
    public S post(BusEvent event) {
        bus.post(event);
        return (S) this;
    }

    /**
     * Get the generic event listener or
     * create it if it doesn't exist.
     * @return The listener.
     */
    public RegisteredListener getOrCreateGenericListener() {
        if (genericListener == null)
            genericListener = new RegisteredListener(bus, null)
                    .dynamic(true)
                    .register();
        return genericListener;
    }

    /**
     * Get the generic event listener or
     * null if it doesn't exist.
     * @return The listener or null.
     */
    public RegisteredListener getGenericListenerOrNull() {
        return genericListener;
    }

    /**
     * Registers the given behaviour event
     * listener to the behaviour bus.
     * @param o The event listener.
     * @return This.
     */
    public S addBehaviour(EventListener o) {
        bus.register(o);
        return (S) this;
    }

    /**
     * Registers a handler for the behaviour event
     * to the generic listener.
     * @param eClass The behaviour event class.
     * @param handler The handler.
     * @param <E> The behaviour event type.
     * @return This.
     */
    public <E> S behaviour(Class<E> eClass, Handler<E> handler) {
        getOrCreateGenericListener().handle(eClass, handler);
        return (S) this;
    }

}
