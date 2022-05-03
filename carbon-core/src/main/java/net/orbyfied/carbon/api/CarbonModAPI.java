package net.orbyfied.carbon.api;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.core.mod.LoadedMod;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.registry.Registry;

import java.util.Objects;

public abstract class CarbonModAPI {

    /**
     * Constructor.
     * @param mod The mod reference.
     * @param main The Carbon main instance.
     */
    public CarbonModAPI(LoadedMod mod, Carbon main) {
        Objects.requireNonNull(mod, "mod cannot be null");
        Objects.requireNonNull(main, "carbon instance cannot be null");
        if (mod.getApi() != null)
            throw new IllegalStateException("mod has already been assigned an API");
        this.mod = mod;
        this.main = main;
    }

    /**
     * The mod reference.
     */
    protected final LoadedMod mod;

    /**
     * The Carbon main instance.
     */
    protected final Carbon main;

    /**
     * Gets the environment API {@link CarbonAPI}
     * @return The API object.
     */
    public CarbonAPI getMainAPI() {
        return main.getAPI();
    }

    /**
     * Get all registries.
     * @return The registry of registries.
     * @see CarbonAPI#getRegistries()
     */
    public Registry<Registry<?>> getRegistries() {
        return main.getRegistries();
    }

    @SuppressWarnings("unchecked")
    public <R extends Registry<T>, T extends Identifiable> R getRegistry(Identifier id) {
        return (R) main.getRegistries().getByIdentifier(id);
    }

    public <R extends Registry<T>, T extends Identifiable> R getRegistry(String id) {
        return getRegistry(Identifier.of(id));
    }

    /**
     * Gets the loaded mod reference.
     * @return The mod reference.
     */
    public LoadedMod getMod() {
        return mod;
    }

    /**
     * Internal method for factories.
     * Casts this object to A.
     * @param <A> The type.
     * @return The casted object.
     */
    @SuppressWarnings("unchecked")
    public <A extends CarbonModAPI> A as() {
        return (A) this;
    }

    /**
     * Internal method for factories.
     * Casts this object to A.
     * @param <A> The type.
     * @return The casted object.
     */
    @SuppressWarnings("unchecked")
    public <A extends CarbonModAPI> A as(Class<A> _klass) {
        return (A) this;
    }

}
