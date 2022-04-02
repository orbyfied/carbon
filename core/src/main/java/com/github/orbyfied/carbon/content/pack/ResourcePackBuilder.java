package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.content.pack.service.JsonPackService;
import com.github.orbyfied.carbon.content.pack.service.PackService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ResourcePackBuilder {

    protected final ResourcePackManager manager;
    protected Path srcDir;

    protected List<PackElementBuilder> assets = new ArrayList<>();
    protected Map<PackResource, PackElementBuilder> assetsByResource = new HashMap<>();

    protected Map<Class<? extends PackService>, PackService> services = new HashMap<>();

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

    public <T extends PackElementBuilder> T asset(
            Function<ResourcePackBuilder, T> constructor) {
        return asset((b, __) -> constructor.apply(b), null);
    }

    public <T extends PackElementBuilder> T asset(
            T asset
            ) {
        assets.add(asset);
        if (asset instanceof PackAssetBuilder pab)
            assetsByResource.put(pab.getResource(), asset);
        return asset;
    }

    public <T extends PackElementBuilder> T asset(
            BiFunction<ResourcePackBuilder, PackResource, T> constructor,
            PackResource resource
            ) {
        T ab;
        PackElementBuilder b;
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

    public <T extends PackElementBuilder> ResourcePackBuilder asset(
            BiFunction<ResourcePackBuilder, PackResource, T> constructor,
            PackResource resource,
            Consumer<T> consumer
    ) {
        consumer.accept(asset(constructor, resource));
        return this;
    }

    public <T extends PackService> ResourcePackBuilder addService(T serv) {
        Objects.requireNonNull(serv);
        services.put(serv.getClass(), serv);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends PackService> T getService(Class<T> serv) {
        Objects.requireNonNull(serv);
        return (T) services.get(serv);
    }

}
