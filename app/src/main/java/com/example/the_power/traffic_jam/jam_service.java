package com.example.the_power.traffic_jam;

import android.app.Service;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

public class jam_service extends Service {
    public static BroadcastReceiver broadcastReceiver = new myBroadcastReceiver();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter(myBroadcastReceiver.BroadcastTypes.METADATA_CHANGED);
        //IntentFilter intentFilter2 = new IntentFilter(myBroadcastReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        //intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(broadcastReceiver, intentFilter);
        return START_STICKY;
    }
}