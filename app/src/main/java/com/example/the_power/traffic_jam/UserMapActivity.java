package com.example.the_power.traffic_jam;

import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import android.content.Context;
import android.content.Intent;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback {
    public static String song;
    public static MarkerOptions syd = new MarkerOptions().position(new LatLng(-34, 151)).title(song);
    public static GoogleMap mMap;
    private static final String REDIRECT_URI = "extremobemotrafficjam://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "Traffic Jam",
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    @Override
    protected void onStart() {
        startService(new Intent(this, jam_service.class));
        super.onStart();
            ConnectionParams connectionParams =
                            new ConnectionParams.Builder("7cc32309fd9e44638285bfb50fdc5482")
                                    .setRedirectUri(REDIRECT_URI)
                                    .showAuthView(true)
                                    .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {


                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        System.out.println("MainActivity Connected! Yay!");
                        // Now you can start interacting with App Remote
                        connected();
                        //mMap.addMarker(syd);
                        //mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("FSDFHSDFJSLDFJSDLKFJLSDKFJSLDKFJL" + throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

    }

    private void connected() {
        Timer timer = new Timer();
        timer.schedule(new SpotifyListener(), 0, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(jam_service.broadcastReceiver);
    }

    @Override
    protected void onPause(){
        super.onPause();
        }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    class SpotifyListener extends TimerTask {

        public void run(){
            //mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
            //System.out.println(executeCommand("curl -X GET \"https://api.spotify.com/v1/me/player/currently-playing\" -H \"Authorization: Bearer 7cc32309fd9e44638285bfb50fdc5482\""));

        }
    }

}



