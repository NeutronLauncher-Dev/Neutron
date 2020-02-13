package com.mjtg.neutron.runtime;

import android.content.Context;
import android.util.Log;

import com.mjtg.neutron.api.NeutronMod;
import com.mjtg.neutron.runtime.loader.NeutronModLoader;

import java.util.ArrayList;
import java.util.List;

public class NeutronRuntime {

    private Context context;

    private List<NeutronMod> mods = new ArrayList<>();

    public NeutronRuntime(Context context) {
        this.context = context;
    }

    public void loadMods() {
        Log.i("Neutron", "loading mods");
        NeutronModLoader loader = new NeutronModLoader(context);
        mods = loader.loadMods();
        Log.i("Neutron-ModLoader", String.format("loaded %d mods", mods.size()));
        Log.i("Neutron-ModLoader", String.format("running onLoad..."));
        for (NeutronMod mod : mods) {
            mod.onLoad();
        }
    }

    public void start() {
        Log.i("Neutron", "starting neutron runtime...");
        loadMods();
    }

}
