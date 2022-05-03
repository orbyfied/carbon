package net.orbyfied.carbon.platform;

/**
 * Class for feature proxies to make
 * it cross-version compatible. To add
 * a new version you will just have to
 * create a new platform proxy for it.
 * This is specified at bootstrap.
 */
public interface PlatformProxy {

    ModLoaderProxy getModLoaderProxy();

    ResourcePackProxy getResourcePackProxy();

    NetworkProxy getNetworkProxy();

}
