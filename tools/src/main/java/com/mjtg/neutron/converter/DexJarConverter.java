package com.mjtg.neutron.converter;

import com.googlecode.d2j.dex.Dex2jar;
import com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DexJarConverter {

    public static void dex2jar(Path dexFile, Path jarFilePath) {
        try {
            Dex2jar.from(dexFile.toFile())
                    .withExceptionHandler(new BaksmaliBaseDexExceptionHandler())
                    .reUseReg(false)
                    .topoLogicalSort()
                    .skipDebug(true)
                    .optimizeSynchronized(false)
                    .printIR(false)
                    .noCode(false)
                    .to(jarFilePath)
            ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void jar2dex(Path jarFile, Path dexFilePath) {
        try {
            Class<?> c = Class.forName("com.android.dx.command.Main");
            Method m = c.getMethod("main", String[].class);

            List<String> ps = new ArrayList<>(Arrays.asList(
                    "--dex", "--no-strict",
                    "--output=" + dexFilePath.toAbsolutePath().toString(),
                    jarFile.toAbsolutePath().toString()
            ));
            m.invoke(null, new Object[] { ps.toArray(new String[0]) });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
