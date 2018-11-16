package com.example.the_power.traffic_jam;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;



public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public static Bitmap cover;
    public FirebaseConnect c;
    public static String song;
    public static GoogleMap mMap;
    private static final String REDIRECT_URI = "extremobemotrafficjam://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    public Dialog dialog;
    public RelativeLayout popupbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        dialog = new Dialog(UserMapActivity.this, R.style.DialogSlideAnim);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        TextView song = (TextView) findViewById(R.id.song_name);

        c = new FirebaseConnect();

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
                        final PlayerApi playerApi  = mSpotifyAppRemote.getPlayerApi();
                        playerApi.getPlayerState()
                                .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                                    @Override
                                    public void onResult(final PlayerState playerState) {
                                        ImageUri currentImg = playerState.track.imageUri;
                                        mSpotifyAppRemote.getImagesApi().getImage(currentImg)
                                                .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                                    @Override
                                                    public void onResult(Bitmap bitmap) {
                                                       cover = bitmap;
                                                       Drawable j = new BitmapDrawable(getResources(), bitmap);
                                                        BitmapDescriptor d = BitmapDescriptorFactory.fromBitmap(cover);
                                                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(40,-96))
                                                                .title(playerState.track.name)
                                                                .snippet(playerState.track.artist.name);
                                                        mMap.addMarker(markerOptions);
                                                        dialog.setContentView(R.layout.user_popup);
                                                        ImageView i = dialog.findViewById(R.id.CoverArt);
                                                        cover = Bitmap.createScaledBitmap(bitmap,  300 ,300, true);
                                                        i.setImageDrawable(j);


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
                        playerApi.subscribeToPlayerState()
                                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                                    @Override
                                    public void onEvent(final PlayerState playerState) {
                                        mMap.clear();
                                        c.writeNewUser("extremobemo", playerState.track.name, playerState.track.imageUri,
                                                playerState.track.uri);
                                        ImageUri currentImg = playerState.track.imageUri;
                                        mSpotifyAppRemote.getImagesApi().getImage(currentImg)
                                                .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                                    @Override
                                                    public void onResult(Bitmap bitmap) {
                                                        cover = bitmap;
                                                        Drawable j = new BitmapDrawable(getResources(), bitmap);
                                                        ImageView i = dialog.findViewById(R.id.CoverArt);
                                                        BitmapDescriptor d = BitmapDescriptorFactory.fromBitmap(cover);
                                                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(40,-96))
                                                                .title(playerState.track.name)
                                                                .snippet(playerState.track.artist.name);
                                                        mMap.addMarker(markerOptions);
                                                        i.setImageDrawable(j);
                                                        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(getDominantColor(bitmap))));
                                                        //popupbg.setBackgroundColor(Color.parseColor("#"+Integer.toString(getDominantColor(bitmap))));
                                                        //ColorDrawable background_color = new ColorDrawable(color);
                                                        //ImageView back = dialog.findViewById(R.id.background);
                                                       // System.out.println(color);

                                                    }
                                                })
                                                .setErrorCallback(new ErrorCallback() {
                                                    @Override
                                                    public void onError(Throwable throwable) {
                                                        System.out.println(throwable.toString());
                                                    }
                                                });
                                    }
                                })
                                .setErrorCallback(new ErrorCallback() {
                                    @Override
                                    public void onError(Throwable throwable) {
                                        // =( =( =(
                                    }
                                });
                        System.out.println("MainActivity Connected! Yay!");
                        // Now you can start interacting with App Remote
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("Something went wrong trying to connect to Spotify! " + throwable);

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
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener)this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(50, -120)));
    }

    @Override
    public boolean onMarkerClick(Marker m) {
        dialog.show();
        return true;
    }

    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

}


