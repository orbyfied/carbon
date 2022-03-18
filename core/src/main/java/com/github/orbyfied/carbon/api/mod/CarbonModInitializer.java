package com.github.orbyfied.carbon.api.mod;

import com.github.orbyfied.carbon.api.CarbonModAPI;
import com.github.orbyfied.carbon.core.mod.LoadedMod;

/**
 * A mod entry point for Carbon.
 */
public interface CarbonModInitializer {

    /**
     * Called when the mod is loaded.
     * At the loading stage no mod API has
     * been constructed and assigned yet.
     * Only certain components of the Carbon
     * environment API will be available,
     * those components are annotated by
     * {@link com.github.orbyfied.carbon.api.annotation.PreInitialization}
     * @param mod The mod object.
     */
    default void modLoaded(LoadedMod mod) { }

    /**
     * Called when the mod is initialized.
     * At the initialization stage the mod
     * API has been initialized and linked.
     * That's the reason for passing the API
     * instead of the mod reference.
     * @param api The mod API reference.
     */
    void modInitialize(CarbonModAPI api);

}
