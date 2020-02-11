package com.mjtg.neutron.runtime.loader;

import android.content.Context;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.mjtg.neutron.api.NeutronMod;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.StringJoiner;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import dalvik.system.DexClassLoader;

import static com.google.common.base.Joiner.on;

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
        return Lists.newArrayList(loader.iterator());
    }

    private List<File> downloadMods(String modsPath) {
        try {
            protocolClient.connectBlocking(10, TimeUnit.SECONDS);
            final List<String> modList = protocolClient.fetchModList().get(10, TimeUnit.SECONDS);
            final List<File> jarFiles = new ArrayList<>();
            for(String mod: modList) {
                final ByteBuffer buffer = protocolClient.fetchMod(mod).get();
                final File file = new File(modsPath+File.separator+mod);
                Files.write(buffer.array(), file);
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
        return new DexClassLoader(
                on(File.pathSeparator)
                        .join(
                                modJars.stream().map(File::getAbsolutePath).collect(Collectors.toList())
                        ),
                context.getCodeCacheDir().getAbsolutePath(),
                null,
                getClass().getClassLoader()
        );
    }

}
