package com.mjtg.neutron.runtime;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.mjtg.neutron.hook.NeutronHooking;
import com.mjtg.neutron.runtime.loader.protocol.client.NeutronProtocolClient;

import java.io.File;

import static java.lang.System.loadLibrary;

public class NeutronRuntime {

    private Context context;

    public void start(Activity activity, NeutronProtocolClient client) {
        context = activity;
        Log.i("Neutron", "starting neutron runtime...");

        Log.i("Neutron", "performing hooking...");
        NeutronHooking.performHooking();

        final File modDir = new File(activity.getCacheDir().toString() + File.separator + "mods");
        modDir.mkdir();
        Log.i("Neutron", "fetching mods...");
        client.downloadMods(modDir);
        Log.i("Neutron", "fetched " + modDir.list().length + " mods...");
        client.close();
    }

}
