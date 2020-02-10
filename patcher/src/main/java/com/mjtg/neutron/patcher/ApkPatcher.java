package com.mjtg.neutron.patcher;

import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;


public class ApkPatcher {

    public static void start(Path apkPath, Path newDexPath, List<Path> librariesToPatch) {

        Path unpackedDir = unpackApk(apkPath);

        transformDex(unpackedDir, newDexPath);

        injectLibraries(
                unpackedDir.resolve("lib").resolve("armeabi-v7a"),
                librariesToPatch
        );

        repackApk(
                unpackedDir,
                apkPath.getParent().resolve("mc-patched.apk")
        );

        System.out.println("Patched successfully");
    }

    private static Path unpackApk(Path apk) {
        System.out.println("Unpacking Apk...");
        Path unpackedDir = apk.getParent().resolve("temp");
        ZipUtil.unzip(apk.toString(), unpackedDir);
        return unpackedDir;
    }

    private static void transformDex(Path unpackedDir, Path newDexFilePath) {
        System.out.println("Injecting dex...");
        Path dexPath= unpackedDir.resolve("classes.dex");
        dexPath.toFile().delete();
        copy(newDexFilePath, dexPath);
    }

    private static void injectLibraries(Path libDir, List<Path> toInjectLibsPath) {
        System.out.println("Injecting libs...");
        for (Path p : toInjectLibsPath) {
            copy(p, libDir.resolve(p.getFileName()));
        }
    }

    private static void repackApk(Path unpackedDir, Path newApkPath) {
        try {
            System.out.println("Repacking Apk...");
            newApkPath.toFile().createNewFile();
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(newApkPath.toFile()));
            ZipUtil.zipFile(unpackedDir.toFile(), zos);
            zos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void copy(Path from,Path to) {
        try {
            Files.copy(from.toFile(), to.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}