package com.mjtg.neutron;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.collect.Lists;
import com.mjtg.neutron.loader.ModLoadingProtocolServer;
import com.mjtg.neutron.loader.protocol.server.NeutronProtocolServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

public class TransService extends Service {

    private NeutronProtocolServer server;

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
        Log.d("Neutron-ModServer", "starting protocol server");
        server = new NeutronProtocolServer(this);
        server.start();
        return super.onStartCommand(intent, flags, startId);
    }

}
