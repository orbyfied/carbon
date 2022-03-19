package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.util.resource.ResourceHandle;

public class SourcedAsset {

    private ResourceHandle source;
    private PackResource   resource;

    public SourcedAsset(ResourceHandle source, PackResource resource) {
        this.source   = source;
        this.resource = resource;
    }

    public PackResource getResource() {
        return resource;
    }

    public ResourceHandle getSource() {
        return source;
    }

}
