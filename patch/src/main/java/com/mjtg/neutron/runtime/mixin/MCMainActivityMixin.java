package com.mjtg.neutron.runtime.mixin;

import android.app.Activity;
import android.util.Log;

import com.mjtg.neutron.runtime.NeutronRuntime;

public class MCMainActivityMixin {

    private Object mcActivity;

    private NeutronRuntime runtime;

    public MCMainActivityMixin(Object mcActivity) {
        this.mcActivity = mcActivity;
        runtime = new NeutronRuntime((Activity)mcActivity);
    }

    public void onCreate() {
        Log.i("Neutron-Mixin", "intercepted MinecraftActivity::onCreate!");
        runtime.start();
    }

}
