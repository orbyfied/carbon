package com.github.orbyfied.carbon.platform;

import java.nio.file.Path;

/**
 * Platform proxy interface for
 * building resource packs.
 */
public interface ResourcePackProxy {

    /**
     * The pack format to use in
     * the pack.mcmeta file.
     * @return The format.
     */
    int getPackFormat();

    String getMinecraftJarUrl();

    Path getMinecraftJarFile(Path rp);

    Path getMinecraftAssetsFolder(Path rp);

    int getAssetsRevision();

}
