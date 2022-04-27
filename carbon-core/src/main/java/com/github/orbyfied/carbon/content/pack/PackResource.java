package com.github.orbyfied.carbon.content.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * The location of a file in a
 * resource pack.
 */
public interface PackResource {

    /**
     * Gets the name of this
     * resource.
     * @return The name.
     */
    String getName();

    /**
     * Gets the path of the
     * resource relative to
     * a parent resource pack
     * directory provided.
     * @param packDir The pack root.
     * @return The path.
     */
    Path getPath(Path packDir);

    default boolean exists(Path packDir) {
        return Files.exists(getPath(packDir));
    }

    default boolean isDirectory(Path packDir) {
        return Files.isDirectory(getPath(packDir));
    }

    default InputStream in(Path packDir) {
        Path p = getPath(packDir);
        try {
            return Files.newInputStream(p);
        } catch (IOException e) {
            throw new RuntimeException("failed to open input stream to " +
                    "pack resource " + getName() + " (" + p + "): ", e);
        }
    }

    default OutputStream out(Path packDir) {
        Path p = getPath(packDir);
        try {
            if (!Files.exists(p))
                Files.createFile(p);
            return Files.newOutputStream(p);
        } catch (IOException e) {
            throw new RuntimeException("failed to open output stream to " +
                    "pack resource " + getName() + " (" + p + "): ", e);
        }
    }

    ///////////////////////////////

    static PackResource of(final String name,
                           final Function<Path, Path> pathResolver) {
        return new PackResource() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Path getPath(Path packDir) {
                return pathResolver.apply(packDir);
            }
        };
    }

}
