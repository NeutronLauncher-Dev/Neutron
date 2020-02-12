package com.mjtg.neutron.patcher;

import com.googlecode.d2j.dex.Dex2jar;
import com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler;
import com.googlecode.dex2jar.tools.BaseCmd;
import com.googlecode.dex2jar.tools.Jar2Dex;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

import static com.googlecode.dex2jar.tools.BaseCmd.createZip;
import static com.googlecode.dex2jar.tools.BaseCmd.walkJarOrDir;
import static com.mjtg.neutron.patcher.ZipUtil.zipFile;

public class DexPatcher {

    /**
     * perform transformation on dex, inject the neutron runtime
     * @param unpackedDir the directory containing the unpacked apk
     * @param dexTmpDir the directory for temp use by dex transformer, must exist before calling
     * @param runtimeJar the path to the jar that contains the neutron runtime
     */
    public static void transformDex(Path unpackedDir, Path dexTmpDir, Path runtimeJar) {
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

    private static void insertJarClasses(Path unpackedDexDir, Path jar) {
        System.out.println("Inserting classes from rtjar...");
        ZipUtil.unzip(jar.toAbsolutePath().toString(), unpackedDexDir.toAbsolutePath());
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
                Path realJar = Files.createTempFile("d2j", ".jar");
                tmp = realJar;
                ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(realJar.toAbsolutePath().toString())));
                zipFile(sourceDir.toFile(), zos);
                zos.close();

                Class<?> c = Class.forName("com.android.dx.command.Main");
                Method m = c.getMethod("main", String[].class);

                List<String> ps = new ArrayList<>(Arrays.asList(
                        "--dex", "--no-strict",
                        "--output=" + dexPath.toAbsolutePath().toString(),
                        realJar.toAbsolutePath().toString()
                ));
                m.invoke(null, new Object[] { ps.toArray(new String[0]) });
            } finally {
                if (tmp != null) {
                    Files.deleteIfExists(tmp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void copy(Path from,Path to) {
        try {
            com.google.common.io.Files.copy(from.toFile(), to.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
