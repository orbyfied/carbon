package com.github.orbyfied.carbon.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class IOUtil {

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
     */
    public static void zipFilesInFolder(String folder, Path resultFile){
        try {
            if (!Files.exists(resultFile))
                Files.createFile(resultFile);
            OutputStream fos = Files.newOutputStream(resultFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            Path sourcePath = Paths.get(folder);
            // using WalkFileTree to traverse directory
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                    // it starts with the source folder so skipping that
                    if(!sourcePath.equals(dir)){
                        //System.out.println("DIR   " + dir);
                        zos.putNextEntry(new ZipEntry(sourcePath.relativize(dir).toString() + "/"));
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(sourcePath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });

            zos.close();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

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

}
