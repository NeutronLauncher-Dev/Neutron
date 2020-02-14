package com.mjtg.neutron.api.hooks;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeutronHooks {

    private static Logger logger = LoggerFactory.getLogger(NeutronHooks.class);

    public static final NeutronHooks INSTANCE = new NeutronHooks();

    public static native void displayClientMessage(String str);

    public void onUseItem(long itemStackPtr, long itemUseMethodPtr, boolean bool) {
        logger.info("An item has been used!");
    }

}
