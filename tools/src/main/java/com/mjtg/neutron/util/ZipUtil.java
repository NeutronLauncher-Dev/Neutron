package com.mjtg.neutron.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.function.Predicate;

public class ZipUtil {

    public static List<Path> unzipFiles(Path zipPath, Path intoDirectoryPath, Predicate<Path> toUnzip) {
        ArrayList<Path> paths = new ArrayList<>();
        try (FileSystem zipFileSystem = FileSystems.newFileSystem(zipPath, null)) {
            final Path root = zipFileSystem.getPath("/");
            //walk the zip file tree and copy files to the destination
            Files.walkFileTree(root, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path o, BasicFileAttributes basicFileAttributes) throws IOException {
                    if(!toUnzip.test(o)) {
                        //if reject, ignore
                        return FileVisitResult.CONTINUE;
                    } else {
                        String cleanedPath = o.toString().substring(1).replace('\\', File.pathSeparatorChar);
                        Path path = intoDirectoryPath.resolve(cleanedPath).normalize().toAbsolutePath();
                        paths.add(path);
                        System.out.println(path);
                        final File file = path.toFile();
                        file.getParentFile().mkdirs();
                        Files.copy(o, path, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paths;
    }

    public static void unzipDirectory(Path zipPath, Path intoDirectoryPath) {
        try {
            new ZipFile(zipPath.toAbsolutePath().toString()).extractAll(intoDirectoryPath.toAbsolutePath().toString());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectory(Path directoryPath, Path zipPath) {
        zipDirectory(directoryPath, zipPath, p->true);
    }

    public static void zipDirectory(Path directoryPath, Path zipPath, Predicate<Path> toZipInto) {
        try (FileOutputStream fis = new FileOutputStream(zipPath.toAbsolutePath().toString())){
            try(ZipOutputStream zos = new ZipOutputStream(fis)) {
                Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if(!toZipInto.test(file)) {
                            //if reject, ignore
                            return FileVisitResult.CONTINUE;
                        }

                        ZipParameters zipParameters = new ZipParameters();
                        String fileNameWithPath = directoryPath.relativize(file).toString();
                        //bug: you need to have \ as pathSeparator to please android!
                        if(File.separatorChar != '/') {
                            if(File.separatorChar == '\\') {
                                fileNameWithPath = fileNameWithPath.replace('\\','/');
                            } else {
                                //we dont know how to fix it for you, so we fail to tell you about it
                                throw new AssertionError("error in translating path separator, please add it here!");
                            }
                        }
                        zipParameters.setFileNameInZip(fileNameWithPath);
                        zos.putNextEntry(zipParameters);
                        Files.copy(file, zos);
                        zos.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
