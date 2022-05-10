package net.orbyfied.carbon.content;

import net.orbyfied.carbon.content.pack.ResourcePackBuilder;

/**
 * A service which prepares assets
 * for a resource pack.
 */
public interface AssetPreparingService {

    void prepareAssets(ResourcePackBuilder builder);

}
