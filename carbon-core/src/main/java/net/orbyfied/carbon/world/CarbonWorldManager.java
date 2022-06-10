package net.orbyfied.carbon.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.Level;
import net.orbyfied.carbon.Carbon;

import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * Manager for the Carbon worlds and linking
 * them to the Minecraft worlds.
 */
public class CarbonWorldManager {

    /**
     * Carbon main instance handle.
     */
    final Carbon main;

    /**
     * The loaded worlds by string name.
     */
    final Object2ObjectOpenHashMap<String, CarbonWorld> worldsByName = new Object2ObjectOpenHashMap<>();

    /**
     * The loaded worlds by NMS level handle.
     * NOTE: /!\ MUST be synced with loading and unloading,
     * otherwise handles won't correspond correctly.
     */
    final Object2ObjectOpenHashMap<Level, CarbonWorld> worldsByHandle = new Object2ObjectOpenHashMap<>();

    /**
     * The worlds in order of when they were loaded.
     */
    final ArrayList<CarbonWorld> worlds = new ArrayList<>();

    public CarbonWorldManager(Carbon main) {
        this.main = main;
    }

    public CarbonWorld getWorld(String name) {
        return worldsByName.get(name);
    }

    public CarbonWorld getWorld(Level handle) {
        return worldsByHandle.get(handle);
    }

    public CarbonWorld loadWorld(Level level) {
        CarbonWorld world = new CarbonWorld(this, level);
        worlds.add(world);
        worldsByHandle.put(level, world);
        worldsByName.put(level.getWorld().getName(), world);
        world.isLoaded = true;
        return world;
    }

    public CarbonWorldManager loadWorld(Level level, BiConsumer<CarbonWorldManager, CarbonWorld> consumer) {
        CarbonWorld world = loadWorld(level);
        if (consumer != null)
            consumer.accept(this, world);
        return this;
    }

    public CarbonWorldManager unloadWorld(CarbonWorld world) {
        worlds.remove(world);
        worldsByHandle.remove(world.level);
        worldsByName.remove(world.world.getName());
        world.isLoaded = false;
        return this;
    }

    public CarbonWorldManager unloadWorld(Level level) {
        return unloadWorld(getWorld(level));
    }

    public CarbonWorldManager unloadWorld(String name) {
        return unloadWorld(getWorld(name));
    }

    /* Getters. */

    public Object2ObjectOpenHashMap<String, CarbonWorld> getWorldsByName() {
        return worldsByName;
    }

    public Object2ObjectOpenHashMap<Level, CarbonWorld> getWorldsByHandle() {
        return worldsByHandle;
    }

    public ArrayList<CarbonWorld> getWorlds() {
        return worlds;
    }
}
