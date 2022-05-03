package net.orbyfied.carbon.content.pack.service;

import net.orbyfied.carbon.content.pack.ResourcePackBuilder;

public abstract class PackService {

    protected final ResourcePackBuilder builder;

    public PackService(ResourcePackBuilder builder) {
        this.builder = builder;
    }

    public ResourcePackBuilder getBuilder() {
        return builder;
    }

}
