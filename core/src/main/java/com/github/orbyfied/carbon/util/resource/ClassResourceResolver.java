package com.github.orbyfied.carbon.util.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class ClassResourceResolver implements ResourceResolver {

    private final Class<?> of;

    public ClassResourceResolver(Class<?> of) {
        this.of = of;
    }

    public String fixPath(String path) {
        if (!path.startsWith("/"))
            path = "/" + path;
        return path;
    }

    @Override
    public URL getResourceFromPath(String path) {
        path = fixPath(path);
        return of.getResource(path);
    }

    @Override
    public boolean existsSafe(URL url) {
        return true;
    }

    @Override
    public boolean isDirectorySafe(URL url) {
        return false;
    }

    @Override
    public OutputStream openOutputStream(URL url) throws IOException {
        throw new UnsupportedOperationException();
    }

}
