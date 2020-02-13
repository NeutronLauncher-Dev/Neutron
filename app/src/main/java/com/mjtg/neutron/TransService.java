package com.mjtg.neutron;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.collect.Lists;
import com.mjtg.neutron.loader.ModLoadingProtocolServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

public class TransService extends Service {

    private Handler handler = new Handler();

    private ModLoadingProtocolServer protocolServer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Neutron-ModServer", "starting mod server");
        protocolServer = new ModLoadingProtocolServer(
                () -> Lists.newArrayList("a.jar"),
                this::fetchMod
        );
        protocolServer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private InputStream fetchMod(String modName) {
        File externalDir = new File(getExternalFilesDir(null), modName);
        Log.d("Neutron-ModServer", String.format("loading %s from %s", modName, externalDir.getAbsolutePath()));
        try {
            final FileInputStream stream = new FileInputStream(externalDir);
            return stream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
