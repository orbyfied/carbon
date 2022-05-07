package net.orbyfied.carbon.event;

import net.orbyfied.carbon.event.pipeline.PipelineAccess;
import net.orbyfied.carbon.event.service.EventService;
import net.orbyfied.carbon.event.service.FunctionalEventService;

import java.util.*;

public class ComplexEventBus extends EventBus {

    /* Services. */
    private final ArrayList<EventService> servicesLinear = new ArrayList<>();
    private final HashMap<Class<? extends EventService>, EventService> servicesMapped = new HashMap<>();

    @Override
    public RegisteredListener register(EventListener listener) {
        RegisteredListener rl = super.register(listener);

        // call services
        {
            int l = servicesLinear.size();
            for (int i = 0; i < l; i++) {
                EventService service = servicesLinear.get(i);
                if (service instanceof FunctionalEventService fes) // functional
                    fes.registered(rl);
            }
        }

        return rl;
    }

    @Override
    public ComplexEventBus unregister(RegisteredListener listener) {
        super.unregister(listener);

        // call services
        {
            int l = servicesLinear.size();
            for (int i = 0; i < l; i++) {
                EventService service = servicesLinear.get(i);
                if (service instanceof FunctionalEventService fes) // functional
                    fes.unregistered(listener);
            }
        }

        return this;
    }

    @Override
    public <E> EventBus post(Class<E> fclass, E event) {
        // get pipeline for event
        final PipelineAccess<E> acc = (PipelineAccess<E>) getPipelineFor(fclass);

        // call services
        {
            int l = servicesLinear.size();
            for (int i = 0; i < l; i++) {
                EventService service = servicesLinear.get(i);
                if (service instanceof FunctionalEventService fes) // functional
                    fes.prePublish(event, acc);
            }
        }

        // post
        pushSafe(event, acc);

        return this;
    }

    public List<EventService> getServicesLinear() {
        return Collections.unmodifiableList(servicesLinear);
    }

    public Map<Class<? extends EventService>, EventService> getServicesMapped() {
        return Collections.unmodifiableMap(servicesMapped);
    }

    @SuppressWarnings("unchecked")
    public <S extends EventService> S getService(Class<S> klass) {
        return (S) servicesMapped.get(klass);
    }

    public ComplexEventBus addService(EventService s) {
        servicesLinear.add(s);
        servicesMapped.put(s.getClass(), s);
        return this;
    }

    public ComplexEventBus removeService(EventService s) {
        servicesLinear.remove(s);
        servicesMapped.remove(s.getClass());
        return this;
    }

}
