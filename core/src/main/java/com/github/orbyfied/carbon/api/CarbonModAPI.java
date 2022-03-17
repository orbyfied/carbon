package com.github.orbyfied.carbon.api;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.core.mod.LoadedMod;

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
    public CarbonAPI getEnvironmentAPI() {
        return main.getAPI();
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
