package com.mjtg.neutron.runtime.loader;

import android.content.Context;

import com.mjtg.neutron.api.NeutronMod;
import com.mjtg.neutron.runtime.loader.protocol.client.NeutronProtocolClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.StringJoiner;

import dalvik.system.DexClassLoader;

public class NeutronModLoader {

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
        final File dir = new File(modsPath);
        new NeutronProtocolClient().downloadMods(dir);

        String[] jarList = dir.list();

        List<File> mods = new ArrayList<>();
        for (String s : jarList) {
            mods.add(new File(s));
        }

        return mods;
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
