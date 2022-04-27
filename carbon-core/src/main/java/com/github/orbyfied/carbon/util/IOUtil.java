package com.github.orbyfied.carbon.util;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class IOUtil {

    public static String toIOFriendlyString(Date date) {
        return date.getMonth() + "-" + date.getDay() + "-" +
                date.getYear() + "_" + date.getHours() + "-" +
                date.getMinutes() + "-" + date.getSeconds();
    }

    public static String readFileToUtf8(Path path) {
        if (!Files.exists(path))
            return null;
        try {
            InputStream is = Files.newInputStream(path);
            String str = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            is.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path))
            return;

        Files.walkFileTree(path, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

        });

    }

    /**
     * From: https://www.netjstech.com/2016/06/zipping-files-in-java.html#ZipMultipleFileJava
     * TODO: fix lol
     */
    @Deprecated
    public static void zipFilesInFolder(Path sourcePath, Path resultFile){
        try {
            // create zip file
            if (!Files.exists(resultFile))
                Files.createFile(resultFile);

            // create output stream to zip file
            OutputStream fos = Files.newOutputStream(resultFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // traverse directory
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>(){

                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(sourcePath.relativize(file).toString()));

                    // write file
                    InputStream fis = Files.newInputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    fis.close();

                    zos.closeEntry();

                    return FileVisitResult.CONTINUE;
                }

            });

            // close resources
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts one file/entry from a ZIP stream.
     * @param is The input stream.
     * @param os The destination output stream.
     * @throws IOException It does this.
     */
    public static void extractFile(InputStream is,
                                   OutputStream os)
            throws IOException {
        try {
            final byte[] buf = new byte[1024];
            int read = 0;
            int length;
            while ((length = is.read(buf, 0, buf.length)) >= 0) {
                os.write(buf, 0, length);
            }
            os.close();
        } catch (IOException ioex) {
            os.close();
        }
    }

    /**
     * Copies resources in the folder specified
     * resolved by the class into another folder.
     * @param klass The class to resolve from.
     * @param source The source folder in the JAR.
     * @param target The destination folder.
     */
    public static void copyFromJar(Class<?> klass, String source, final Path target) {
        try {
            URI resource = klass.getResource("").toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(
                    resource,
                    Collections.<String, String>emptyMap()
            );


            final Path jarPath = fileSystem.getPath(source);

            Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {

                private Path currentTarget;

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    currentTarget = target.resolve(jarPath.relativize(dir).toString());
                    Files.createDirectories(currentTarget);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(jarPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String escapeUchar(char c) {
        String hex = Integer.toHexString(c).toUpperCase(Locale.ROOT);
        if (hex.length() < 4)
            hex = "0".repeat(4 - hex.length()) + hex;
        return "\\u" + hex;
    }

    public static String escapeUstr(String src) {
        StringBuilder b = new StringBuilder();
        int l = src.length();
        for (int i = 0; i < l; i++) {
            char c = src.charAt(i);
            if (c <= 128) { // not unicode
                b.append(c);
            } else {
                b.append(escapeUchar(c));
            }
        }

        return b.toString();
    }

}
