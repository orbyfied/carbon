package net.orbyfied.carbon.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.Level;
import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.config.AbstractConfiguration;
import net.orbyfied.carbon.config.Configurable;
import net.orbyfied.carbon.config.Configuration;
import net.orbyfied.carbon.config.Configure;
import net.orbyfied.carbon.logging.BukkitLogger;
import org.bukkit.World;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * Manager for the Carbon worlds and linking
 * them to the Minecraft worlds.
 */
public class CarbonWorldManager implements Configurable {

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

    /**
     * Internal logger.
     */
    final BukkitLogger logger;

    /**
     * Executor for asynchronous chunk loading.
     */
    Executor asyncChunkLoadExecutor;

    public CarbonWorldManager(Carbon main) {
        this.main   = main;
        this.logger = main.getLogger("CarbonWorldManager");

        main.getConfigurationHelper()
                .addConfigurable(this);
    }

    public CarbonWorldManager initialize() {
        // init async chunk executor
        asyncChunkLoadExecutor = Executors.newFixedThreadPool(
                config.asyncChunkLoadConfig.numThreads
        );

        return this;
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

    /* Chunk Loading */

    Path getDataDirectory(CarbonWorld world) {
        Path p = world.getWorld().getWorldFolder().toPath()
                .resolve("carbon_data");
        if (!Files.exists(p)) {
            try {
                Files.createDirectory(p);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return p;
    }

    Path getChunkNBTFile(CarbonChunk chunk) {
        return getDataDirectory(chunk.world).resolve(chunk.cx + "#" + chunk.cz + ".ccnbt");
    }

    void loadChunkAsync(CarbonChunk chunk) {
        asyncChunkLoadExecutor.execute(chunk::load);
    }

    void saveChunkAsync(CarbonChunk chunk) {
        asyncChunkLoadExecutor.execute(chunk::save);
    }

    /* Getters */

    public Object2ObjectOpenHashMap<String, CarbonWorld> getWorldsByName() {
        return worldsByName;
    }

    public Object2ObjectOpenHashMap<Level, CarbonWorld> getWorldsByHandle() {
        return worldsByHandle;
    }

    public ArrayList<CarbonWorld> getWorlds() {
        return worlds;
    }

    //////////////////////////////////

    @Override
    public String getConfigurationPath() {
        return "world-manager";
    }

    Config config = new Config(this);

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    static class Config extends AbstractConfiguration {

        public Config(Configurable<?> configurable) {
            super(configurable);
        }

        @Configure(name = "async-chunk-loading")
        AsyncChunkLoadConfig asyncChunkLoadConfig = new AsyncChunkLoadConfig();

    }

    static class AsyncChunkLoadConfig extends AbstractConfiguration {

        public AsyncChunkLoadConfig() {
            super(null);
        }

        @Configure(name = "threads")
        int numThreads = 4;

    }

}
