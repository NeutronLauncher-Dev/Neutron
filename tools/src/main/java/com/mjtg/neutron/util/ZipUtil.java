package com.mjtg.neutron.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

public class ZipUtil {

    public static void unzipDirectory(Path zipPath, Path intoDirectoryPath) {
        try {
            new ZipFile(zipPath.toAbsolutePath().toString()).extractAll(intoDirectoryPath.toAbsolutePath().toString());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectory(Path directoryPath, Path zipPath) {
        zipDirectory(directoryPath, zipPath, path -> true);
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
