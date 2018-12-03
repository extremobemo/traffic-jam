package com.example.the_power.traffic_jam;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class HostNotification {
    private Context context;
    public NotificationCompat.Builder mBuilder;
    NotificationManagerCompat notificationManager;
    public HostNotification(Context context){
        this.context = context;
    }
    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("Channel Name");
            String description =("Description");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1001", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void host(String song, String user){
        BroadcastReceiver b = new MyBroadcastReceiver();
        ((MyBroadcastReceiver) b).setUser(user);
        Intent testIntent = new Intent(context, MyBroadcastReceiver.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0, testIntent, 0);
        mBuilder = new NotificationCompat.Builder(context, "1001")
                .setSmallIcon(R.drawable.oval_button)
                .setContentTitle("radio.fi is hosting")
                .setContentText("now playing " + song)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true)
                .addAction(R.drawable.oval_button, "QUIT", pendIntent);
        notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(01, mBuilder.build());

    }

    public void stopHosting(){
        notificationManager.cancel(01);
        UserMapActivity.amHosting = false;
    }
}
