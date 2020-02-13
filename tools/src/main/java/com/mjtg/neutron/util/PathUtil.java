package com.mjtg.neutron.util;

import java.nio.file.Path;

public class PathUtil {

    public static void ensureDirExists(Path path) {
        path = path.toAbsolutePath();
        if(!path.toFile().exists()) {
            if (!path.toFile().mkdirs()) {
                throw new RuntimeException("unable to make directory for :" + path.toString());
            }
        }
    }

}
