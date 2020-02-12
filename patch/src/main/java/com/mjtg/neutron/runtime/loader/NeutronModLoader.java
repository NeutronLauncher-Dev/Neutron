package com.mjtg.neutron.runtime.loader;

import android.content.Context;

import com.mjtg.neutron.api.NeutronMod;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import dalvik.system.DexClassLoader;

public class NeutronModLoader {

    private ModLoadingProtocolClient protocolClient;
    {
        try {
            protocolClient = new ModLoadingProtocolClient(new URI("ws://localhost:32770"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private final Context context;

    private DexClassLoader loader;

    public NeutronModLoader(Context context) {
        this.context = context;
    }

    public List<NeutronMod> loadMods() {
        final File modsDir = new File(context.getCacheDir().getPath() + File.separator + "mods");
        if(modsDir.exists()) {
            modsDir.delete();
        }

        final List<File> modJars = downloadMods(modsDir.getAbsolutePath());
        loader = classLoadMods(modJars);

        final ServiceLoader<NeutronMod> loader = ServiceLoader.load(NeutronMod.class, this.loader);
        List<NeutronMod> mod = new ArrayList<>();
        for (NeutronMod neutronMod : loader) {
            mod.add(neutronMod);
        }
        return mod;
    }

    private List<File> downloadMods(String modsPath) {
        try {
            protocolClient.connectBlocking(10, TimeUnit.SECONDS);
            final List<String> modList = protocolClient.fetchModList().get(10, TimeUnit.SECONDS);
            final List<File> jarFiles = new ArrayList<>();
            for(String mod: modList) {
                final ByteBuffer buffer = protocolClient.fetchMod(mod).get();
                final File file = new File(modsPath+File.separator+mod);
                FileUtils.writeByteArrayToFile(file, buffer.array());
                jarFiles.add(file);
            }
            return jarFiles;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(protocolClient!=null) {
                protocolClient.close();
            }
        }
    }

    private DexClassLoader classLoadMods(List<File> modJars) {
        StringJoiner joiner = new StringJoiner(File.pathSeparator);
        for (File modJar : modJars) {
            joiner.add(modJar.getAbsolutePath());
        }

        return new DexClassLoader(
                joiner.toString(),
                context.getCodeCacheDir().getAbsolutePath(),
                null,
                getClass().getClassLoader()
        );
    }

}
