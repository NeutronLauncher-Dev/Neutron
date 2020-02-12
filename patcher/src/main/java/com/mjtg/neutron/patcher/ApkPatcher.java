package com.mjtg.neutron.patcher;


import com.google.common.io.Files;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class ApkPatcher {

    public static void start(Path apkPath, List<Path> runtimeJarsPath, List<Path> librariesToPatch) {
        System.out.println("Performing Minecraft APK Injection");
        System.out.println(String.format(Locale.US, "have %d native libraries to inject, %d jars to inject", librariesToPatch.size(), runtimeJarsPath.size()));

        //准备临时dex临时文件
        Path dexTmpPath = apkPath.getParent().resolve("dexTemp");
        dexTmpPath.toFile().mkdir();

        Path unpackedDir = unpackApk(apkPath);

        DexPatcher.transformDex(unpackedDir, dexTmpPath, runtimeJarsPath);

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
        ZipUtil.unzipDirectory(apk, unpackedDir);
        return unpackedDir;
    }

    private static void injectLibraries(Path libDir, List<Path> toInjectLibs) {
        System.out.println("Injecting libs...");
        for (Path p : toInjectLibs) {
            System.out.println("injecting "+p.getFileName());
            copy(p, libDir.resolve(p.getFileName()));
        }
    }

    private static void repackApk(Path unpackedDir, Path newApk) {
        System.out.println("Repacking Apk...");
        ZipUtil.zipDirectory(unpackedDir, newApk);
    }

    private static void copy(Path from,Path to) {
        try {
            Files.copy(from.toFile(), to.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}