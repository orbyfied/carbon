package net.orbyfied.carbon.content.pack;

import net.orbyfied.carbon.util.resource.ResourceHandle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyAssetBuilder extends PackAssetBuilder {

    private ResourceHandle source;

    public CopyAssetBuilder(ResourcePackBuilder parent, PackResource loc) {
        super(parent, loc);
    }

    public CopyAssetBuilder setSource(ResourceHandle handle) {
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
