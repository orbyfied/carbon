package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.util.resource.ResourceHandle;

public class SourcedAsset {

    private String name;
    private ResourceHandle source;
    private PackResource   resource;

    public SourcedAsset(ResourceHandle source, PackResource resource) {
        this.source   = source;
        this.resource = resource;
    }

    public SourcedAsset setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public PackResource getResource() {
        return resource;
    }

    public ResourceHandle getSource() {
        return source;
    }

}
