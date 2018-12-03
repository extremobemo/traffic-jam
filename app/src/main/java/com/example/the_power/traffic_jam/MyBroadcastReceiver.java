package com.example.the_power.traffic_jam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public String user;
    public MyBroadcastReceiver(){
    }
    @Override
    public void onReceive(Context context, Intent intent) {
       deleteUser(UserMapActivity.user);

    }
    public void setUser(String user){
        this.user = user;
    }

    public void deleteUser(String user){
        FirebaseConnect c = new FirebaseConnect();
        c.deleteInstance(user);
        UserMapActivity.hostNotification.stopHosting();
    }
}
