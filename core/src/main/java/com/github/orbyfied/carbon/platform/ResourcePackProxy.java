package com.github.orbyfied.carbon.platform;

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

}
