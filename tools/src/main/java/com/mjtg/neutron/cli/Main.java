package com.mjtg.neutron.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.mjtg.neutron.packer.JarPacker;
import com.mjtg.neutron.patcher.ApkPatcher;
import com.mjtg.neutron.patcher.PatchApkFromRuntimeAARCommand;
import com.mjtg.neutron.patcher.PrepareRuntimeDirFromAAR;
import com.mjtg.neutron.util.PathUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static class PackRuntimeFromAARCommandArgs {

        @Parameter(names = "-aar", description = "path to the aar that is built by runtime/running")
        public String aarFile;

    }

    public static class PatchFromAARCommandArgs {

        @Parameter(names = "-aar", description = "path to the aar that is built by runtime/preloading")
        public String aarFile;

        @Parameter(names = "-apk", description = "path to the apk to patch")
        public String apkFile;

    }

    public static class PatcherCommandArgs {

        @Parameter(names = "-rtjar", description = "path to the jars that contains the java classes to put in")
        public List<String> rtJars = new ArrayList<>();

        @Parameter(names = "-lib", description = "path to native library to put in")
        public List<String> libraries = new ArrayList<>();

        @Parameter(names = "-apk", description = "path to the apk to patch")
        public String apkFile;

    }

    public static class PackerCommandArgs {

        @Parameter(names = "-jar", description = "path to the jars that needs to be packed into one jar with all java classes put into classes.dex")
        public List<String> jars = new ArrayList<>();

    }

    public static void main(String[] args) {
        final PackRuntimeFromAARCommandArgs packRTArgs = new PackRuntimeFromAARCommandArgs();
        final PatchFromAARCommandArgs aarCommandArgs = new PatchFromAARCommandArgs();
        PatcherCommandArgs patchArgs = new PatcherCommandArgs();
        PackerCommandArgs packArgs = new PackerCommandArgs();
        JCommander cmder = JCommander.newBuilder()
                .addCommand("patch", patchArgs)
                .addCommand("pack", packArgs)
                .addCommand("patch-aar", aarCommandArgs)
                .addCommand("pack-runtime", packRTArgs)
                .build()
                ;

        cmder.parse(args);

        if(cmder.getParsedCommand().equals("patch")) {
            Path apkPath = Paths.get(new File(patchArgs.apkFile).getAbsolutePath());
            List<Path> rtJars = patchArgs.rtJars.stream().map(i -> Paths.get(new File(i).getAbsolutePath())).collect(Collectors.toList());
            List<Path> libs = patchArgs.libraries.stream().map(i -> Paths.get(new File(i).getAbsolutePath())).collect(Collectors.toList());
            ApkPatcher.start(apkPath, rtJars, libs);
        } else if(cmder.getParsedCommand().equals("pack")) {
            final Path tmpPath = Paths.get("./packerTemp");
            PathUtil.ensureDirExists(tmpPath);
            List<Path> libs = packArgs.jars.stream().map(i -> Paths.get(new File(i).getAbsolutePath())).collect(Collectors.toList());
            JarPacker.packJar(tmpPath, libs, Paths.get("./packed.jar"));
            System.out.println("packed to packed.jar");
        } else if(cmder.getParsedCommand().equals("patch-aar")) {
            final Path tmpPath = Paths.get("./aarTemp");
            PathUtil.ensureDirExists(tmpPath);
            PatchApkFromRuntimeAARCommand.run(
                    Paths.get(new File(aarCommandArgs.apkFile).getAbsolutePath()),
                    Paths.get(new File(aarCommandArgs.aarFile).getAbsolutePath()),
                    tmpPath
            );
        } else if(cmder.getParsedCommand().equals("pack-runtime")) {
            final Path aarTmpPath = Paths.get("./aarTemp2");
            PathUtil.ensureDirExists(aarTmpPath);
            final Path runtimePath = Paths.get("./runtime");
            runtimePath.toFile().mkdir();
            PrepareRuntimeDirFromAAR.run(
                    runtimePath,
                    Paths.get(new File(packRTArgs.aarFile).getAbsolutePath()),
                    aarTmpPath
            );
        }
    }

}
