package com.mjtg.neutron.patcher;

import com.googlecode.d2j.dex.Dex2jar;
import com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler;
import com.mjtg.neutron.converter.DexJarConverter;
import com.mjtg.neutron.util.ZipUtil;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;


public class DexPatcher {

    /**
     * perform transformation on dex, inject the neutron runtime
     * @param unpackedDir the directory containing the unpacked apk
     * @param dexTmpDir the directory for temp use by dex transformer, must exist before calling
     * @param runtimeJar the path to the jar that contains the neutron runtime
     */
    public static void transformDex(Path unpackedDir, Path dexTmpDir, List<Path> runtimeJar) {
        System.out.println("Injecting dex...");

        Path dexPath =  unpackedDir.resolve("classes.dex");

        //build the tmp directory for exploded values
        Path unpackedDexDir = dexTmpDir.resolve("unpacked");
        unpackedDexDir.toFile().mkdir();

        performDex2Jar(unpackedDexDir, dexPath);

        insertJarClasses(unpackedDexDir, runtimeJar);

        instrumentMainActivity(unpackedDexDir);

        dexPath.toFile().delete();
        performJar2Dex(unpackedDexDir, dexPath);
    }

    private static void instrumentMainActivity(Path unpackedDexDir) {
        System.out.println("Instrumenting MainActivity...");
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(unpackedDexDir.toString());

            CtClass clazz = pool.get("com.mojang.minecraftpe.MainActivity");

            clazz.addField(CtField.make( "public com.mjtg.neutron.runtime.mixin.MCMainActivityMixin $neutron_mixin;", clazz));

            CtConstructor ctor = clazz.getConstructor("()V");
            ctor.insertBefore("$0.$neutron_mixin = new com.mjtg.neutron.runtime.mixin.MCMainActivityMixin();");

            CtMethod onCreate = clazz.getMethod("onCreate", "(Landroid/os/Bundle;)V");
            onCreate.insertBefore("$0.$neutron_mixin.onCreate((Object)$0);");

            Path clazzFile = unpackedDexDir.resolve(Paths.get("com", "mojang", "minecraftpe").resolve("MainActivity.class"));
            try(DataOutputStream fos = new DataOutputStream(new FileOutputStream(clazzFile.toAbsolutePath().toString()))) {
                clazz.toBytecode(fos);
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private static void insertJarClasses(Path unpackedDexDir, List<Path> jars) {
        System.out.println("Inserting java classes in jar");
        for (Path jar : jars) {
            System.out.println("inserting "+jar.getFileName());
            ZipUtil.unzipDirectory(jar, unpackedDexDir);
        }
    }

    private static void performDex2Jar(Path resultDir, Path dexPath) {
        System.out.println("Performing Dex2Jar...");
        try {
            Dex2jar.from(dexPath.toFile())
                    .withExceptionHandler(new BaksmaliBaseDexExceptionHandler())
                    .reUseReg(false)
                    .topoLogicalSort()
                    .skipDebug(true)
                    .optimizeSynchronized(false)
                    .printIR(false)
                    .noCode(false)
                    .to(resultDir)
            ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void performJar2Dex(Path sourceDir, Path dexPath) {
        System.out.println("Performing Jar2Dex...");
        Path tmp = null;
        try {
            try {
                final Path d2jDir = Files.createTempDirectory("d2j");
                Path realJar = d2jDir.resolve("d2j-"+ Instant.now().getEpochSecond()+".jar");
                tmp = realJar;
                ZipUtil.zipDirectory(sourceDir, realJar.toAbsolutePath());

                DexJarConverter.jar2dex(realJar, dexPath);
            } finally {
                if (tmp != null) {
                    Files.deleteIfExists(tmp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
