package com.mjtg.neutron.patcher;

import com.mjtg.neutron.packer.JarPacker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mjtg.neutron.util.ZipUtil.unzipFiles;

public class PrepareRuntimeDirFromAAR {

    public static void run(Path destDirPath, Path aarPath, Path aarTemp) {
        List<Path> paths = unzipFiles(aarPath, aarTemp, p->{
            return p.startsWith("/libs")
                    || p.getFileName().toString().endsWith(".so")
                    || p.getFileName().toString().equals("classes.jar")
                    ;
        });

        final Map<String, List<Path>> suffixes = paths.stream()
                .collect(Collectors.groupingBy(k -> {
                    final String filename = k.getFileName().toString();
                    return filename.substring(filename.lastIndexOf('.')+1);
                }));

        System.out.println("scanned aar file:"+aarPath.toString());
        System.out.println("got "+suffixes.get("so").size()+" .so");
        suffixes.get("so").forEach(System.out::println);
        System.out.println("got "+suffixes.get("jar").size()+" .jar");
        suffixes.get("jar").forEach(System.out::println);

        System.out.println("packing jars");
        final Path packerTmp = aarTemp.resolve("packTmp");
        packerTmp.toFile().mkdir();
        final Path jarsPath = destDirPath.resolve("jars");
        jarsPath.toFile().mkdir();
        JarPacker.packJar(packerTmp, suffixes.get("jar"), jarsPath.resolve("packed.jar"));

        System.out.println("copying so");
        final Path libsPath = destDirPath.resolve("libs");
        libsPath.toFile().mkdir();
        for (Path path : suffixes.get("so")) {
            try {
                Files.copy(path, libsPath.resolve(path.getFileName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
