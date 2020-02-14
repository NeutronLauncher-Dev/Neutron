package com.mjtg.neutron.hook;

import com.mjtg.neutron.runtime.hook.PESORegistry;

import static java.lang.System.loadLibrary;

public class NeutronHooking {

    private static native int performHooking(long PESOAddress);

    public static void performHooking() {
        loadLibrary("substrate");
        loadLibrary("neutron");
        performHooking(PESORegistry.PE_ADDRESS);
    }

}
