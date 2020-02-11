package com.mjtg.neutron.runtime.mixin;

import android.os.Bundle;

public class MCMainActivityMixin {

    private Object mcActivity;

    public MCMainActivityMixin(Object mcActivity) {
        this.mcActivity = mcActivity;
    }

    public void onCreate() {
        //do nothing
    }

}
