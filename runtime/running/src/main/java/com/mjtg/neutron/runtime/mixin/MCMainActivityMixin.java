package com.mjtg.neutron.runtime.mixin;

import android.app.Activity;
import android.util.Log;

import com.mjtg.neutron.runtime.NeutronRuntime;

public class MCMainActivityMixin {

    private Object mcActivity;

    private NeutronRuntime runtime;

    public MCMainActivityMixin() {}

    public void onCreate(Object mcActivity) {
        Log.i("Neutron-Mixin", "intercepted MinecraftActivity::onCreate!");
        this.mcActivity = mcActivity;
        runtime = new NeutronRuntime((Activity)mcActivity);
        runtime.start();
    }

}
