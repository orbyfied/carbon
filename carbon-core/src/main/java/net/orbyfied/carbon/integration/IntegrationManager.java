package net.orbyfied.carbon.integration;

import net.orbyfied.carbon.Carbon;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IntegrationManager {

    protected final Carbon main;

    public IntegrationManager(Carbon main) {
        this.main = main;
    }

    public Carbon getMain() {
        return main;
    }

    ///////////////////////////////////////////////

    protected final List<Integration> integrationsLinear = new ArrayList<>();

    protected final Map<String, Integration> integrationsMapped = new HashMap<>();

    public List<Integration> getIntegrationsLinear() {
        return Collections.unmodifiableList(integrationsLinear);
    }

    public Map<String, Integration> getIntegrationsMapped() {
        return Collections.unmodifiableMap(integrationsMapped);
    }

    public Integration getById(String id) {
        return integrationsMapped.get(id);
    }

    public <T extends Integration> IntegrationManager constructAndRegister(Class<T> klass,
                                                                           BiConsumer<T, Boolean> consumer) {
        try {
            Constructor<T> constructor = klass.getDeclaredConstructor(
                    Carbon.class, IntegrationManager.class
            );

            T it = constructor.newInstance(main, this);
            boolean registered = false;
            if (it.isAvailable()) {
                this.register(it);
                it.load();
                registered = true;
            }

            if (consumer != null)
                consumer.accept(it, registered);

            return this;
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public <T extends Integration> IntegrationManager constructAndRegister(Class<T> klass) {
        return constructAndRegister(klass, null);
    }

        public IntegrationManager register(Integration integration) {
        integrationsMapped.put(integration.id, integration);
        integrationsLinear.add(integration);
        return this;
    }

    public IntegrationManager unregister(Integration integration) {
        integrationsMapped.remove(integration.id);
        integrationsLinear.remove(integration);
        return this;
    }

    public IntegrationManager enable() {
        for (Integration integration : integrationsLinear) {
            try {
                integration.enable();
                integration.enabled = true;
            } catch (Exception e) {
                System.out.println("Error while enabling integration " + integration.id + " (" + integration.name + "):");
                e.printStackTrace();
            }
        }

        return this;
    }

    public IntegrationManager disable() {
        for (Integration integration : integrationsLinear) {
            try {
                integration.disable();
                integration.enabled = false;
            } catch (Exception e) {
                System.out.println("Error while disabling integration " + integration.id + " (" + integration.name + "):");
                e.printStackTrace();
            }
        }

        return this;
    }

}
