package com.github.orbyfied.carbon.content.pack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ResourcePackBuilder {

    private Path srcDir;
    private List<PackAssetBuilder> assets = new ArrayList<>();

    public ResourcePackBuilder(Path srcDir) {
        this.srcDir = srcDir;
    }

    public Path getSourceDirectory() {
        return srcDir;
    }

    public <T extends PackAssetBuilder> T asset(
            BiFunction<ResourcePackBuilder, PackResource, T> constructor,
            PackResource resource
            ) {
        T ab = constructor.apply(this, resource);
        assets.add(ab);
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
