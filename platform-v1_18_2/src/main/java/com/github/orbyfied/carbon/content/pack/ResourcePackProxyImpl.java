package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.platform.ResourcePackProxy;

import java.nio.file.Path;

public class ResourcePackProxyImpl implements ResourcePackProxy {

    @Override
    public int getPackFormat() {
        return 8;
    }

    @Override
    public String getMinecraftJarUrl() {
        // from .minecraft/versions/1.18.1/1.18.1.json
        return "https://launcher.mojang.com/v1/objects/7e46fb47609401970e2818989fa584fd467cd036/client.jar";
    }

    @Override
    public Path getMinecraftJarFile(Path rp) {
        return rp.resolve("mcv/1.18.1-temp.jar");
    }

    @Override
    public Path getMinecraftAssetsFolder(Path rp) {
        return rp.resolve("mcv/1.18.1-assets");
    }

    @Override
    public int getAssetsRevision() {
        return 0;
    }

}
