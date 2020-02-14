package com.mjtg.neutron.runtime.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PESORegistry {

    private static final Logger logger = LoggerFactory.getLogger(PESORegistry.class);

    //the libminecraftpe.so
    public static long PE_ADDRESS = 0;

    private static native long getPEAddress();

    public static void setupPEAddress() {
        logger.debug("getting libminecraftpe.so address");
        System.loadLibrary("stub");
        PE_ADDRESS = getPEAddress();
        logger.info("got libminecraftpe.so address: {}", PE_ADDRESS);
    }

}
