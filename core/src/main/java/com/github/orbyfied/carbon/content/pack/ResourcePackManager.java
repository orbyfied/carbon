package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.logging.BukkitLogger;

import java.nio.file.Path;

public class ResourcePackManager {

    /**
     * The main Carbon instance.
     */
    private final Carbon main;

    /**
     * The logger.
     */
    private final BukkitLogger logger;

    public final Path packSrcDirectory;

    public final Path packPkgFile;

    public ResourcePackManager(Carbon main) {
        this.main   = main;
        this.logger = main.getLogger("ResourcePack");
        this.packSrcDirectory = main.getFileInDirectory("resource_pack/src/");
        this.packPkgFile      = main.getFileInDirectory("resource_pack/pack.zip");
    }

    public Carbon getMain() {
        return main;
    }

    public BukkitLogger getLogger() {
        return logger;
    }

    public void build() {
        // collect stuff

    }

}
