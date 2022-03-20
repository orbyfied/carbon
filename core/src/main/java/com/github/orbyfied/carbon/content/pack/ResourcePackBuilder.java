package com.github.orbyfied.carbon.content.pack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ResourcePackBuilder {

    private final ResourcePackManager manager;
    private Path srcDir;
    private List<PackAssetBuilder> assets = new ArrayList<>();

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

    public ResourcePackBuilder build() {
        try {

            // create directory
            if (!Files.exists(srcDir))
                Files.createDirectories(srcDir);

            //

        } catch (IOException e) {
            e.printStackTrace();
        }

        // return
        return this;
    }

}
