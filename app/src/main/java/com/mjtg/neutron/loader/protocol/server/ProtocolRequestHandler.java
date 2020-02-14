package com.mjtg.neutron.loader.protocol.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public interface ProtocolRequestHandler {

    public static class Runtime {

        public Map<String, File> jars = new HashMap<>();

        public Map<String, File> nativeLibs = new HashMap<>();

    }

    Runtime getRuntime();

    Map<String, File> getMods();

}
