package net.orbyfied.carbon.content.pack;

import net.orbyfied.carbon.platform.ResourcePackProxy;

import java.nio.file.Path;

public class ResourcePackProxyImpl implements ResourcePackProxy {

    @Override
    public int getPackFormat() {
        return 8;
    }

    @Override
    public String getMinecraftJarUrl() {
        // from .minecraft/versions/1.18.2/1.18.2.json
        return "https://launcher.mojang.com/v1/objects/2e9a3e3107cca00d6bc9c97bf7d149cae163ef21/client.jar";
    }

    @Override
    public Path getMinecraftJarFile(Path rp) {
        return rp.resolve("mcv/1.18.2-temp.jar");
    }

    @Override
    public Path getMinecraftAssetsFolder(Path rp) {
        return rp.resolve("mcv/1.18.2-assets");
    }

    @Override
    public int getAssetsRevision() {
        return 0;
    }

}
