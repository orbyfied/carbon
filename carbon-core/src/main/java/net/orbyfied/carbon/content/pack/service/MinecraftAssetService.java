package net.orbyfied.carbon.content.pack.service;

import net.orbyfied.carbon.content.pack.ResourcePackBuilder;

import java.nio.file.Path;
import java.util.HashSet;

public class MinecraftAssetService extends PackService {

    HashSet<String> extract = new HashSet<>();

    Path assetsPath;

    public MinecraftAssetService(ResourcePackBuilder builder) {
        super(builder);
    }

    public boolean shouldExtract(String n) {
        if (!n.startsWith("assets/minecraft"))
            return false;
        return true;
//        return extract.contains(n);
    }

    public MinecraftAssetService setAssetsPath(Path assetsPath) {
        this.assetsPath = assetsPath;
        return this;
    }

    public Path getAssetsPath() {
        return assetsPath;
    }

    public MinecraftAssetService addToExtract(String n) {
        extract.add(n);
        return this;
    }

}
