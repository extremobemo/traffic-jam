package com.example.the_power.traffic_jam;
import android.Manifest;
import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;

import java.io.IOException;


public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public static Bitmap cover;
    public FirebaseConnect c;
    public static String song;
    public static GoogleMap mMap;
    private static final String REDIRECT_URI = "extremobemotrafficjam://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    public Dialog dialog;
    public RelativeLayout popupbg;
    public LocationManager lm;
    public MarkerOptions markerOptions;
    public Marker myMark;
    public PlayerApi playerApi;
    public final String user = "christopher";
    public boolean dialog_open = false;
    public String prospect;
    private RelativeLayout layoutButtons;
    private RelativeLayout layoutMain;
    private boolean hostviewOpen = false;
    public double myLat;
    public double myLong;
    public boolean amHosting = false;
    FloatingActionButton fab;
    LocationManager locationManager;
    LocationListener locationListener;
    FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        layoutButtons = (RelativeLayout) findViewById(R.id.layoutButtons);
        layoutMain = (RelativeLayout) findViewById(R.id.main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button host = (Button) findViewById(R.id.host);
                host.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        hostStation();
                        viewHostMenu();

                    }
                });
                viewHostMenu();
            }
        });
        Button host = (Button) findViewById(R.id.quit);
        host.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                c.deleteInstance(user);
                viewHostMenu();

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("songname");
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        CameraPosition.Builder builder = new CameraPosition.Builder();
                        builder.tilt(30);
                        builder.zoom(18f);
                        builder.target(new LatLng(location.getLatitude(), location.getLongitude()));
                        CameraPosition cameraPosition = builder.build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            });
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if(child.getKey() == user){
                            try{
                                double lat = (double) child.child("location").child("lat").getValue(); // Long or Double
                                double lon = (double) child.child("location").child("lon").getValue();
                                dropPinEffect(mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(child.getKey())));
                            }catch(Exception e){
                                continue; //Users location has not updated yet, waiting for database to get my location.
                            }

                        }
                        else{
                            try{
                                double lat = (double) child.child("location").child("lat").getValue(); // Long or Double
                                double lon = (double) child.child("location").child("lon").getValue();
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(child.getKey()));
                            }catch(Exception e){
                                continue;
                                //c.deleteInstance(user);
                            }

                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates

        // Register the listener with the Location Manager to receive location updates
    }

    @Override
    protected void onStart() {
        dialog = new Dialog(UserMapActivity.this, R.style.DialogSlideAnim);
        dialog.getWindow().setGravity(Gravity.CENTER);

        TextView song = (TextView) findViewById(R.id.song_name);

        c = new FirebaseConnect(user,"0");
        //subscribe("extremobemo"); subscribes to a user in Firebase


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
                        playerApi  = mSpotifyAppRemote.getPlayerApi();
                        drawHostScreen();
                        //hostStation();
                        //LatLng location = getLocation();
                        //c.writeLocation(location.latitude, location.longitude);
                        //TODO GET LAT/LON HERE AND PUSH TO FIREBASE
                        // Now you can start interacting with App Remote
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("Something went wrong trying to connect to Spotify! " + throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

    }
    private void viewHostMenu() {
        if(hostviewOpen == false){
            hostviewOpen = true;
            int x = layoutMain.getRight();
            int y = layoutMain.getBottom();

            int startRadius = 0;
            int endRadius = (int) Math.hypot(layoutMain.getWidth(), layoutMain.getHeight());


            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);

            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    //layoutButtons.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

        }
           else{
            hostviewOpen = false;
            int x = layoutMain.getRight();
            int y = layoutMain.getBottom();

            int startRadius = (int) Math.hypot(layoutMain.getWidth(), layoutMain.getHeight());
            int endRadius = 0;


            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);

            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    layoutButtons.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }

    @Override
    protected void onDestroy(){
        c.deleteInstance(user);
        super.onDestroy();
        //unregisterReceiver(jam_service.broadcastReceiver);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(dialog_open == true){
            dialog.dismiss();
            dialog_open = false;
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
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
        LocationManager service = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }else{
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                myLat = location.getLatitude();
                                myLong = location.getLongitude();
                               // c.writeLocation(user, location.getLatitude(), location.getLongitude());
                            }
                            else{
                                c.writeLocation(user,5.0,5.0);
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onMarkerClick(final Marker m) {
        prospect = m.getTitle();
        if(dialog_open == false){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("songname");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren() ){
                        if(child.getKey().equals(m.getTitle())){
                            ImageUri currentImg = new ImageUri((String) child.child("track").child("imageuri").child("raw").getValue());
                            mSpotifyAppRemote.getImagesApi().getImage(currentImg)
                                    .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                        @Override
                                        public void onResult(Bitmap bitmap) {
                                            //c.writeLocation(latitude,longitude);
                                            cover = bitmap;
                                            Drawable j = new BitmapDrawable(getResources(), bitmap);
                                            dialog.setContentView(R.layout.user_popup);
                                            ImageView i = dialog.findViewById(R.id.CoverArt);
                                            cover = Bitmap.createScaledBitmap(bitmap,  300 ,300, true);
                                            i.setImageDrawable(j);
                                            dialog.getWindow().setBackgroundDrawable((new ColorDrawable(getDominantColor(bitmap))));
                                            Button cancel = (Button) dialog.findViewById(R.id.cancel);
                                            cancel.setOnClickListener(new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    dialog.dismiss();
                                                    dialog_open = false;

                                                }
                                            });

                                            Button sub = (Button) dialog.findViewById(R.id.sub);
                                            sub.setOnClickListener(new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    subscribe(prospect);
                                                    dialog.dismiss();
                                                    dialog_open = false;
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
                        }
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
            dialog_open = true;
            dialog.show();

        }
        return true;
    }


    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }
    public void subscribe(String user){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("songname");
        ref.child(user).child("track").child("trackuri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               mSpotifyAppRemote.getPlayerApi().play(dataSnapshot.getValue().toString());
                System.out.println(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 15);
                } else {
                    marker.showInfoWindow();

                }
            }
        });
    }
    public void drawHostScreen(){
        playerApi  = mSpotifyAppRemote.getPlayerApi();
        playerApi.subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(final PlayerState playerState) {
                        c.writeNewUser(user, playerState.track.name, playerState.track.imageUri,
                                playerState.track.uri);
                        ImageUri currentImg = playerState.track.imageUri;
                        mSpotifyAppRemote.getImagesApi().getImage(currentImg)
                                .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                    @Override
                                    public void onResult(Bitmap bitmap) {
                                        cover = bitmap;
                                        Drawable j = new BitmapDrawable(getResources(), bitmap);
                                        ImageView i = findViewById(R.id.art);
                                        BitmapDescriptor d = BitmapDescriptorFactory.fromBitmap(cover);
                                        i.setImageDrawable(j);
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
    }
    public void hostStation(){
        c.writeLocation(user, myLat, myLong);
        amHosting = true;
        playerApi  = mSpotifyAppRemote.getPlayerApi();
        playerApi.getPlayerState()
                .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(final PlayerState playerState) {
                        ImageUri currentImg = playerState.track.imageUri;
                        mSpotifyAppRemote.getImagesApi().getImage(currentImg)
                                .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                    @Override
                                    public void onResult(Bitmap bitmap) {
                                        c.writeNewUser(user, playerState.track.name, playerState.track.imageUri,
                                                playerState.track.uri);
                                        cover = bitmap;
                                        Drawable j = new BitmapDrawable(getResources(), bitmap);
                                        BitmapDescriptor d = BitmapDescriptorFactory.fromBitmap(cover);
                                        markerOptions = new MarkerOptions()
                                                .title(user)
                                                .snippet(playerState.track.artist.name);
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
                        System.out.print(throwable.toString());
                    }
                });
        playerApi.subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(final PlayerState playerState) {
                        c.writeNewUser(user, playerState.track.name, playerState.track.imageUri,
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
                                        //i.setImageDrawable(j);
                                        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(getDominantColor(bitmap))));
                                        TextView song_n = (TextView) dialog.findViewById(R.id.song_name);
                                        TextView song_a = (TextView) dialog.findViewById(R.id.artist);
                                        song_n.setText(playerState.track.name);
                                        song_a.setText(playerState.track.artist.name);
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
    }
}


