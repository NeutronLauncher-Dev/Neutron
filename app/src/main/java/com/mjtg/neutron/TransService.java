package com.mjtg.neutron;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class TransService extends Service {
    private Handler handler = new Handler();
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                //the function in this area will join a loop

                handler.postDelayed(this, 500);
            }
        });
        return super.onStartCommand(intent, flags, startId);


    }

}
