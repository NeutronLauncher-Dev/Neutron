package com.mjtg.neutron.packer;

import com.mjtg.neutron.converter.DexJarConverter;
import com.mjtg.neutron.util.PathUtil;
import com.mjtg.neutron.util.ZipUtil;

import net.lingala.zip4j.ZipFile;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;


public class JarPacker {

    public static void packJar(Path tmpDir, List<Path> jarsToPack, Path resultJar) {
        Path unpackDir = tmpDir.resolve("unpacked");
        final Path dexPath = tmpDir.resolve("classes.dex");

        PathUtil.ensureDirExists(tmpDir);
        PathUtil.ensureDirExists(unpackDir);

        unpackJars(jarsToPack, unpackDir);

        packClassesToDex(tmpDir, dexPath);

        repackJarWithDexInstead(unpackDir, dexPath, resultJar);
    }

    private static void repackJarWithDexInstead(Path unpackDir, Path dexPath, Path resultJar) {
        System.out.println("repacking .dex and other than .class into .jar");
        try {
            FileUtils.copyFile(dexPath.toFile(), unpackDir.resolve(dexPath.getFileName()).toFile());
            ZipUtil.zipDirectory(
                    unpackDir, resultJar.toAbsolutePath(),
                    file->!file.getFileName().toString().endsWith(".class")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void packClassesToDex(Path tmpDir, Path dexPath) {
        System.out.println("packing classes into dex");
        Path tmpJar = null;
        try {
            try {
                final Path d2jDir = Files.createTempDirectory("d2j");
                tmpJar = d2jDir.resolve("d2j-"+ Instant.now().getEpochSecond()+".jar");
                ZipUtil.zipDirectory(
                        tmpDir, tmpJar.toAbsolutePath(),
                        file-> file.getFileName().toString().endsWith(".class")
                );

                DexJarConverter.jar2dex(tmpJar, dexPath);

            } finally {
                if (tmpJar != null) {
                    Files.deleteIfExists(tmpJar);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void unpackJars(List<Path> jarsToPack, Path unpackDir) {
        System.out.println("unpacking jars");
        for (Path jar : jarsToPack) {
            try {
                new ZipFile(jar.toFile()).extractAll(unpackDir.toAbsolutePath().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
