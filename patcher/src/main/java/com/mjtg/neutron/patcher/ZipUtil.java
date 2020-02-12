package com.mjtg.neutron.patcher;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ZipUtil {

    public static void unzipDirectory(Path zipPath, Path intoDirectoryPath) {
        try {
            new ZipFile(zipPath.toAbsolutePath().toString()).extractAll(intoDirectoryPath.toAbsolutePath().toString());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectory(Path directoryPath, Path zipPath) {
        try (FileOutputStream fis = new FileOutputStream(zipPath.toAbsolutePath().toString())){
            try(ZipOutputStream zos = new ZipOutputStream(fis)) {
                java.nio.file.Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        ZipParameters zipParameters = new ZipParameters();
                        String fileNameWithPath = directoryPath.relativize(file).toString();
                        //bug: you need to have \ as pathSeparator to please android!
                        if(File.separatorChar != '/') {
                            if(File.separatorChar == '\\') {
                                fileNameWithPath.replace('\\','/');
                            } else {
                                //we dont know how to fix it for you, so we fail to tell you about it
                                throw new AssertionError("error in translating path separator, please add it here!");
                            }
                        }
                        zipParameters.setFileNameInZip(fileNameWithPath);
                        zos.putNextEntry(zipParameters);
                        java.nio.file.Files.copy(file, zos);
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
