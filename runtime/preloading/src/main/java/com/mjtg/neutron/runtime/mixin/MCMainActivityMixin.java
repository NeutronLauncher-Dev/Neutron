package com.mjtg.neutron.runtime.mixin;

import android.app.Activity;
import android.util.Log;

import com.mjtg.neutron.runtime.hook.PESORegistry;
import com.mjtg.neutron.runtime.loader.protocol.client.NeutronProtocolClient;
import com.mjtg.neutron.runtime.loader.protocol.client.NeutronWebSocketClient;

import java.io.File;
import java.lang.reflect.Method;
import java.util.StringJoiner;

import dalvik.system.DexClassLoader;


public class MCMainActivityMixin {

    private Object mcActivity;

    private Object neutronRuntime;

    public MCMainActivityMixin() {}

    public void onCreate(Object mcActivity) {
        Log.i("Neutron-Mixin", "intercepted MinecraftActivity::onCreate!");
        this.mcActivity = mcActivity;

        //prepare the hooking address
        PESORegistry.setupPEAddress();

        //start runtime
        startRuntime();

    }

    private void startRuntime() {
        Log.i("Neutron-Mixin", "loading runtime from launcher");
        final File cacheDir = ((Activity) mcActivity).getCacheDir();
        final File runtimeDir = new File(cacheDir.toString()+File.separator+"runtime");
        final File nativeLibDir = new File(runtimeDir.toString()+File.separator+"libs");
        final File jarsDir = new File(runtimeDir.toString()+File.separator+"jars");

        NeutronProtocolClient client = new NeutronProtocolClient();
        try {
            client.downloadRuntime(jarsDir, nativeLibDir);

            //the jars path
            final StringBuilder dexes = new StringBuilder();
            for (File jar : jarsDir.listFiles()) {
                if(dexes.length()!=0) {
                    dexes.append(File.pathSeparator);
                }
                dexes.append(jar.getAbsolutePath());
            }

            //setup the class loader
            final DexClassLoader cl = new DexClassLoader(
                    dexes.toString(),
                    ((Activity) mcActivity).getCodeCacheDir().toString(),
                    nativeLibDir.getAbsolutePath(),
                    getClass().getClassLoader()
            );

            //load the class and reflectively start it
            Log.i("Neutron-Mixin", "reflectively starting the runtime");
            final Class<?> clazz = cl.loadClass("com.mjtg.neutron.runtime.NeutronRuntime");
            neutronRuntime = clazz.newInstance();
            final Method start = clazz.getMethod("start", Activity.class, NeutronProtocolClient.class);
            start.invoke(neutronRuntime, (Activity)mcActivity, client);
            client = null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if( client != null ) {
                client.close();
            }
            if(runtimeDir.exists()) {
                if(!runtimeDir.delete()) {
                    Log.w("Neutron-Mixin", "unable to erase runtime tmp directory, this might become a source of issue!");
                }
            }
        }
    }

}