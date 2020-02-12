package com.mjtg.neutron.patcher;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static class CommandArgs {

        @Parameter(names = "-rtjar", description = "path to the jars that contains the java classes to put in")
        public List<String> rtJars = new ArrayList<>();

        @Parameter(names = "-lib", description = "path to native library to put in")
        public List<String> libraries = new ArrayList<>();

        @Parameter(names = "-apk", description = "path to the apk to patch")
        public String apkFile;

    }

    public static void main(String[] args) {
        CommandArgs cmdArgs = new CommandArgs();
        JCommander.newBuilder()
                .addObject(cmdArgs)
                .build()
                .parse(args);

        Path apkPath = Paths.get(cmdArgs.apkFile);
        List<Path> rtJars = cmdArgs.rtJars.stream().map(i -> Paths.get(i)).collect(Collectors.toList());
        List<Path> libs = cmdArgs.libraries.stream().map(i -> Paths.get(i)).collect(Collectors.toList());

        ApkPatcher.start(apkPath, rtJars, libs);
    }

}
