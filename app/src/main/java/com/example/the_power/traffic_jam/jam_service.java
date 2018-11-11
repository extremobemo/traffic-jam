package com.example.the_power.traffic_jam;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
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
    public void onCreate() {
        super.onCreate();
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = ("Traffic-Jam");// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
// Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle("New Message")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(CHANNEL_ID)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, notification);

        }
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter(myBroadcastReceiver.BroadcastTypes.METADATA_CHANGED);
        //IntentFilter intentFilter2 = new IntentFilter(myBroadcastReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        //intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(broadcastReceiver, intentFilter);
        return START_NOT_STICKY;
    }
}