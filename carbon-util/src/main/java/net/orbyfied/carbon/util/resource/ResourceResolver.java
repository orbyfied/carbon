package net.orbyfied.carbon.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public interface ResourceResolver {

    URL getResourceFromPath(String path) throws MalformedURLException;

    boolean existsSafe(URL url);

    boolean isDirectorySafe(URL url);

    OutputStream openOutputStream(URL url) throws IOException;

    ///////////////////////////

    static ResourceResolver inModule(Class<?> klass) {
        return new ClassResourceResolver(klass);
    }

    ResourceResolver FILE = new ResourceResolver() {
        @Override
        public URL getResourceFromPath(String path) throws MalformedURLException {
            return new File(path).toURL();
        }

        private Path pathOf(URL url) {
            return Path.of(url.getPath());
        }

        @Override
        public boolean existsSafe(URL url) {
            return Files.exists(pathOf(url));
        }

        @Override
        public boolean isDirectorySafe(URL url) {
            return Files.isDirectory(pathOf(url));
        }

        @Override
        public OutputStream openOutputStream(URL url) throws IOException {
            return Files.newOutputStream(pathOf(url));
        }
    };

}
