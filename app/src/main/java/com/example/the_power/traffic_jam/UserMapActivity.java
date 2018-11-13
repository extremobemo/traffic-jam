package com.example.the_power.traffic_jam;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.graphics.Bitmap;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import android.content.Context;
import android.content.Intent;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback {
    public static Bitmap cover;
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
        //createNotificationChannel();

    }

    @Override
    protected void onStart() {
        startService(new Intent(this, jam_service.class));
        ContextCompat.startForegroundService(this, new Intent(this, jam_service.class));

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
                        PlayerApi playerApi  = mSpotifyAppRemote.getPlayerApi();
                        playerApi.getPlayerState()
                                .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                                    @Override
                                    public void onResult(PlayerState playerState) {
                                        ImageUri currentImg = playerState.track.imageUri;
                                        mSpotifyAppRemote.getImagesApi().getImage(currentImg)
                                                .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                                    @Override
                                                    public void onResult(Bitmap bitmap) {
                                                       cover = bitmap;
                                                        BitmapDescriptor d = BitmapDescriptorFactory.fromBitmap(cover);
                                                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(40,-151))
                                                                .title("Current Location")
                                                                .snippet("Thinking of finding some thing...")
                                                                .icon(d);
                                                        mMap.addMarker(markerOptions);
                                                    }
                                                })
                                                .setErrorCallback(new ErrorCallback() {
                                                    @Override
                                                    public void onError(Throwable throwable) {
                                                        System.out.print("BEN");
                                                    }
                                                });

                                    }
                                })
                                .setErrorCallback(new ErrorCallback() {
                                    @Override
                                    public void onError(Throwable throwable) {
                                        System.out.print("BEN");
                                    }
                                });
                        System.out.println("MainActivity Connected! Yay!");
                        // Now you can start interacting with App Remote
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

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //unregisterReceiver(jam_service.broadcastReceiver);
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
        LatLng sydney = new LatLng(40, -151);
        Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title("DUFHSDUFHDSUF"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}


