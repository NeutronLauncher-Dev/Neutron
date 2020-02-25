package com.mjtg.neutron.patcher;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mjtg.neutron.util.ZipUtil.unzipFiles;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatchApkFromRuntimeAARCommand {

    public static void run(Path apkPath, Path aarPath, Path aarTemp) {
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
        System.out.println("patching...");
        ApkPatcher.start(apkPath, suffixes.get("jar"), suffixes.get("so"));
    }

}
