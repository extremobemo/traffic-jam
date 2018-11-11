package com.example.the_power.traffic_jam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.sql.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.media.ToneGenerator;
import android.media.AudioManager;
public class myBroadcastReceiver extends BroadcastReceiver {
    FirebaseConnect c = new FirebaseConnect();
    static final class BroadcastTypes {
        public static final String SPOTIFY_PACKAGE = "com.spotify.music";
        public static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        public static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        public static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        c.writeNewUser(intent.getStringExtra("track"),"extremobemo", intent.getStringExtra("track"));
        UserMapActivity.mMap.clear();
        UserMapActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(-34, 151)).title(intent.getStringExtra("track")));
        System.out.print(intent.getStringExtra("album"));
    }
}


