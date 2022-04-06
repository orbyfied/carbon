package com.github.orbyfied.carbon.core;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.registry.Identifier;

import java.util.*;

public class ServiceManager {

    protected final ArrayList<Service> servicesLinear = new ArrayList<>();

    protected final HashMap<Class<? extends Service>, Service> servicesMapped = new HashMap<>();

    protected final Carbon main;

    protected boolean isInit = false;

    public ServiceManager(Carbon main) {
        this.main = main;
    }

    public Carbon getMain() {
        return main;
    }

    public List<Service> getServices() {
        return Collections.unmodifiableList(servicesLinear);
    }

    public Map<Class<? extends Service>, Service> getServicesById() {
        return Collections.unmodifiableMap(servicesMapped);
    }

    public Service getService(Class<? extends Service> id) {
        return servicesMapped.get(id);
    }

    public boolean hasService(Class<? extends Service> id) {
        return servicesMapped.containsKey(id);
    }

    public void initialized() {
        isInit = true;
        for (Service s : servicesLinear)
            s.initialize();
    }

    public ServiceManager addService(Service service) {
        servicesLinear.add(service);
        servicesMapped.put(service.getClass(), service);
        if (isInit)
            service.initialize();
        return this;
    }

}
