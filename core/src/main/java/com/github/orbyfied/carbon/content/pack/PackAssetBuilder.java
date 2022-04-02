package com.github.orbyfied.carbon.content.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class PackAssetBuilder extends PackElementBuilder {

    protected final PackResource resource;

    public PackAssetBuilder(ResourcePackBuilder parent,
                            PackResource loc) {
        super(parent);
        this.resource = loc;
        this.name = resource.getName();
    }

    public ResourcePackBuilder getParent() {
        return parent;
    }

    public PackResource getResource() {
        return resource;
    }

    public abstract void write(OutputStream out) throws IOException;

    public abstract void read(InputStream in) throws IOException;

    @Override
    public void write() {
        Path rp = getFile();
        try {
            // create file
            if (!Files.exists(rp.getParent()))
                Files.createDirectories(rp.getParent());
            if (!Files.exists(rp))
                Files.createFile(rp);

            // open output stream and write file, then close
            OutputStream s = Files.newOutputStream(rp);
            write(s);
            s.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void read() {
        Path rp = getFile();
        try {
            // create file
            if (!Files.exists(rp))
                return;

            // open output stream and write file, then close
            InputStream s = Files.newInputStream(rp);
            read(s);
            s.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Path getFile() {
        return resource.getPath(parent.srcDir);
    }

}
