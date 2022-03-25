package com.github.orbyfied.carbon.content.pack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ResourcePackBuilder {

    protected final ResourcePackManager manager;
    protected Path srcDir;

    protected List<PackAssetBuilder> assets = new ArrayList<>();
    protected Map<PackResource, PackAssetBuilder> assetsByResource = new HashMap<>();

    public ResourcePackBuilder(ResourcePackManager manager,
                               Path srcDir) {
        this.manager = manager;
        this.srcDir  = srcDir;
    }

    public ResourcePackManager getManager() {
        return manager;
    }

    public Path getSourceDirectory() {
        return srcDir;
    }

    public <T extends PackAssetBuilder> T asset(
            Function<ResourcePackBuilder, T> constructor) {
        return asset((b, __) -> constructor.apply(b), null);
    }

    public <T extends PackAssetBuilder> T asset(
            T asset
            ) {
        assets.add(asset);
        assetsByResource.put(asset.getResource(), asset);
        return asset;
    }

    public <T extends PackAssetBuilder> T asset(
            BiFunction<ResourcePackBuilder, PackResource, T> constructor,
            PackResource resource
            ) {
        T ab;
        PackAssetBuilder b;
        if ((b = assetsByResource.get(resource)) != null) {
            try {
                return (T) b;
            } catch (Exception e) {
                return null;
            }
        }

        ab = constructor.apply(this, resource);
        assets.add(ab);
        assetsByResource.put(resource, ab);
        return ab;
    }

    public <T extends PackAssetBuilder> ResourcePackBuilder asset(
            BiFunction<ResourcePackBuilder, PackResource, T> constructor,
            PackResource resource,
            Consumer<T> consumer
    ) {
        consumer.accept(asset(constructor, resource));
        return this;
    }

}
