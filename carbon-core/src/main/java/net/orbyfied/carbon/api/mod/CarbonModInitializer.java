package net.orbyfied.carbon.api.mod;

import net.orbyfied.carbon.api.CarbonModAPI;
import net.orbyfied.carbon.core.mod.LoadedMod;
import net.orbyfied.carbon.api.annotation.PreInitialization;

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
     * {@link PreInitialization}
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

    /**
     * Called to disable/unload all mods.
     * Happens when the Carbon plugin is
     * disabled, which is usually at server
     * shutdown or when a reload happens.
     * @param api The mod API reference.
     */
    default void modDisable(CarbonModAPI api) { }

}
