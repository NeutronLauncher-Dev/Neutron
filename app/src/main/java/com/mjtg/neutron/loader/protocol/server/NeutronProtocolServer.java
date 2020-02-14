package com.mjtg.neutron.loader.protocol.server;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NeutronProtocolServer {

    private static final int PORT = 32770;

    private LoadingWebsocketProtocolServer server;

    public NeutronProtocolServer(Context context) {
        String modsDirPath = context.getExternalFilesDir(null).toString()+File.separator+"mods";

        String runtimeDirPath = context.getExternalFilesDir(null).toString()+File.separator+"runtime";
        String nativeLibPath = runtimeDirPath + File.separator + "libs";
        String jarsPath = runtimeDirPath + File.separator + "jars";

        server = new LoadingWebsocketProtocolServer(PORT, new ProtocolRequestHandler() {
            @Override
            public Runtime getRuntime() {
                Runtime rt = new Runtime();
                Log.d("Neutron-ModServer", "reading runtime jar files from "+jarsPath);
                Log.d("Neutron-ModServer", "reading runtime native libraries from "+nativeLibPath);
                rt.nativeLibs = readAllFilesInDirectory(new File(nativeLibPath));
                rt.jars = readAllFilesInDirectory(new File(jarsPath));
                Log.d("Neutron-ModServer", "read "+ rt.nativeLibs.size()+" native libraries, "+ rt.jars.size()+" jars");
                return rt;
            }

            @Override
            public Map<String, File> getMods() {
                Log.d("Neutron-ModServer", "fetching mods from "+ modsDirPath);
                Map<String, File> mods = readAllFilesInDirectory(new File(modsDirPath));
                Log.d("Neutron-ModServer", "read "+ mods.size()+" mods");
                return mods;
            }
        });
    }

    public void start() {
        Log.d("Neutron-ModServer", "starting server");
        server.start();
    }

    public void stop() {
        try {
            server.stop();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, File> readAllFilesInDirectory(File dir) {
        Map<String, File> files = new HashMap<>();
        final File[] listFiles = dir.listFiles();
        if(listFiles == null) {
            return files;
        }
        for (File file : listFiles) {
            if(!file.isDirectory()) {
                files.put(file.getName(), file);
            }
        }
        return files;
    }

}
