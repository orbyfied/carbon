package net.orbyfied.carbon.content;

import net.orbyfied.carbon.content.pack.ResourcePackBuilder;

/**
 * A service which prepares assets
 * for a resource pack.
 */
public interface AssetPreparingService {

    /**
     * Prepares assets for the resource pack.
     * @param builder The resource pack.
     */
    void prepareAssets(ResourcePackBuilder builder);

}
