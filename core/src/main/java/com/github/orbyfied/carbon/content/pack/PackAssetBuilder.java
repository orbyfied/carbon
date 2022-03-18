package com.github.orbyfied.carbon.content.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class PackAssetBuilder {

    protected final ResourcePackBuilder parent;

    protected final PackResource resource;

    public PackAssetBuilder(ResourcePackBuilder parent,
                            PackResource loc) {
        this.parent   = parent;
        this.resource = loc;
    }

    public ResourcePackBuilder getParent() {
        return parent;
    }

    public PackResource getResource() {
        return resource;
    }

    public abstract void write(OutputStream out) throws IOException;

    public abstract void read(InputStream in) throws IOException;

}
