package com.example.the_power.traffic_jam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class myBroadcastReceiver extends BroadcastReceiver {
    static final class BroadcastTypes {
        public static final String SPOTIFY_PACKAGE = "com.spotify.music";
        public static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        public static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        public static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       UserMapActivity.mMap.clear();
       UserMapActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(-34, 151)).title(intent.getStringExtra("track")));
       System.out.print(intent.getStringExtra("album"));
        // This is sent with all broadcasts, regardless of type. The value is taken from
        // System.currentTimeMillis(), which you can compare to in order to determine how
        // old the event is.

        //long timeSentInMs = intent.getLongExtra("timeSent", 0L);
        //String action = intent.getAction();

        //if (action.equals(BroadcastTypes.METADATA_CHANGED)) {
        // String trackId = intent.getStringExtra("id");
        // String artistName = intent.getStringExtra("artist");
        // String albumName = intent.getStringExtra("album");
        // String trackName = intent.getStringExtra("track");
        //  int trackLengthInSec = intent.getIntExtra("length", 0);
        // Do something with extracted information...
        //  MarkerOptions syd = new MarkerOptions().position(new LatLng(-34, 151)).title(trackName);
        //  UserMapActivity.mMap.addMarker(syd);
    }
}


