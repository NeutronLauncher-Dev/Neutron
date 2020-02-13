package com.mjtg.neutron.runtime.mixin;

import android.util.Log;


public class MCMainActivityMixin {

    private Object mcActivity;

    public MCMainActivityMixin() {}

    public void onCreate(Object mcActivity) {
        Log.i("Neutron-Mixin", "intercepted MinecraftActivity::onCreate!");
        this.mcActivity = mcActivity;
    }

}
