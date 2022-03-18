package com.github.orbyfied.carbon.core.mod;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.logging.BukkitLogger;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ModLoader {

    private final ArrayList<LoadedMod> mods = new ArrayList<>();

    private final HashMap<Class<? extends Plugin>, LoadedMod> modsByClass = new HashMap<>();

    private final Carbon main;

    private final BukkitLogger logger;

    public ModLoader(Carbon main) {
        this.main = main;
        this.logger = main.getLogger("ModLoader");
    }

    public List<LoadedMod> getMods() {
        return Collections.unmodifiableList(mods);
    }

    public Map<Class<? extends Plugin>, LoadedMod> getModsByClass() {
        return Collections.unmodifiableMap(modsByClass);
    }

    public LoadedMod getByClass(Class<? extends Plugin> klass) {
        return modsByClass.get(klass);
    }

    public boolean load(Plugin plugin) {
        try {
            LoadedMod mod = new LoadedMod(main, plugin);
            mod.getDescriptor();

            logger.info("Loading " + ChatColor.AQUA + mod.getId() + ChatColor.BLUE +
                    " v" + mod.getVersion() + ChatColor.RESET + " of plugin " + ChatColor.GREEN +
                    plugin + ". "
//                    + ChatColor.DARK_GRAY + "( " + ChatColor.GRAY + mod.asAvailableString() + ChatColor.GRAY + " )"
            );

            mod.load();
            mods.add(mod);
            modsByClass.put(plugin.getClass(), mod);
            return true;
        } catch (Exception e) {
            logger.err("Error while loading mod of " + plugin + ": " + e);
            e.printStackTrace();
            return false;
        }
    }

    public void loadAll() {
        long t1 = System.currentTimeMillis();
        logger.stage("Load").info("Instantiating mod loading process, resolving.");

        // resolve all plugins
        List<Plugin> plugins = resolveAllModdedPlugins();
        logger.info("Loading " + ChatColor.AQUA + plugins.size()
                + ChatColor.RESET + " mods.");

        // load all plugins
        int failed  = 0;
        int success = 0;
        for (Plugin pl : plugins) {
            if (load(pl)) {
                success++;
            } else {
                failed++;
            }
        }
        long t2 = System.currentTimeMillis();
        logger.ok("Loaded " + ChatColor.GREEN + success + ChatColor.RESET +
                " mods successfully, " + ChatColor.RED + failed + ChatColor.RESET +
                " failed. Time elapsed: " + ChatColor.YELLOW + (t2 - t1) + "ms");
    }

    public List<Plugin> resolveAllModdedPlugins() {
        List<Plugin> list = new ArrayList<>();
        main.getPlatform().getModLoaderProxy().resolveModPlugins(list);
        return list;
    }

    public void initializeAll() {
        logger.stage("Init").info("Initializing " + ChatColor.AQUA + mods.size() +
                ChatColor.RESET + " mods");
        long t1 = System.currentTimeMillis();
        int success = 0;
        int failed  = 0;
        for (LoadedMod mod : mods) {
            logger.info("Initializing " + ChatColor.AQUA + mod.getId() +
                    ChatColor.BLUE + " v" + mod.getVersion());
            try {
                mod.initialize();
                success++;
            } catch (Exception e) {
                logger.err("Error while initializing " + mod.getId() + ";");
                e.printStackTrace();
                failed++;
            }
        }
        long t2 = System.currentTimeMillis();
        logger.ok("Successfully initialized " + ChatColor.GREEN + success +
                ChatColor.RESET + " mods, failed to initialize " + ChatColor.RED + failed +
                ChatColor.RESET + ". Time elapsed: " + ChatColor.YELLOW +
                (t2 - t1) + "ms");
    }

}
