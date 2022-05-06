package net.orbyfied.carbon.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Represents a handle to a locatable
 * resource.
 */
public interface ResourceHandle {

    /**
     * Returns the resolver associated
     * with this handle. Is usually the
     * {@link ResourceResolver#inModule}
     * for internal resources.
     * @return The resolver.
     */
    ResourceResolver getResolver();

    /**
     * Gets the URL associated with
     * this resource. With internal
     * resources this is usually the
     * result of {@link Class#getResource(String)}
     * @return The URL.
     */
    URL getUrl();

    /**
     * If the resource exists and
     * is available.
     * @return If it does.
     */
    default boolean exists() {
        return getResolver().existsSafe(getUrl());
    }

    /**
     * If the resource is a
     * directory.
     * @return If it is.
     */
    default boolean isDirectory() {
        return getResolver().isDirectorySafe(getUrl());
    }

    /**
     * Opens an input stream to the resource.
     * Won't work for obscure resources
     * that can't be read.
     * @return The input stream.
     */
    default InputStream in() throws IOException {
        return getUrl().openStream();
    }

    /**
     * Opens an output stream to the resource.
     * Won't work for resources that can't be
     * written to like module resources.
     * @return The output stream.
     */
    default OutputStream out() throws IOException {
        return getResolver().openOutputStream(getUrl());
    }

    ////////////////////////////////////////

    static ResourceHandle ofModuleResource(String path) {
        StackTraceElement[] elem;
        try {
            throw new Exception();
        } catch (Exception e) {
            elem = e.getStackTrace();
        }
        try {
            Class<?> klass = Class.forName(elem[1].getClassName());
            return ofModuleResource(klass, path);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static ResourceHandle ofModuleResource(Class<?> klass, String path) {
        final ClassResourceResolver res = new ClassResourceResolver(klass);
        return new ResourceHandle() {
            private final URL url = res.getResourceFromPath(path);

            @Override
            public ResourceResolver getResolver() {
                return res;
            }

            @Override
            public URL getUrl() {
                return url;
            }
        };
    }

    static ResourceHandle ofFile(String path) {
        URL url;
        try {
            url = ResourceResolver.FILE.getResourceFromPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new ResourceHandle() {

            @Override
            public ResourceResolver getResolver() {
                return ResourceResolver.FILE;
            }

            @Override
            public URL getUrl() {
                return url;
            }
        };
    }

}
