package com.github.orbyfied.carbon.content.pack.asset;

import com.github.orbyfied.carbon.content.pack.PackAssetBuilder;
import com.github.orbyfied.carbon.content.pack.PackResource;
import com.github.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.github.orbyfied.carbon.util.resource.ResourceHandle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TextureAssetBuilder extends PackAssetBuilder {

    private ResourceHandle source;

    public TextureAssetBuilder(ResourcePackBuilder parent, PackResource loc) {
        super(parent, loc);
    }

    public TextureAssetBuilder setSource(ResourceHandle handle) {
        this.source = handle;
        return this;
    }

    public ResourceHandle getSource() {
        return source;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        InputStream in = source.in();
        in.transferTo(out);
        in.close();
    }

    @Override
    public void read(InputStream in) throws IOException {

    }

}
