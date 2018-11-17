package com.example.the_power.traffic_jam;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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



public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
    public static Bitmap cover;
    public FirebaseConnect c;
    public static String song;
    public static GoogleMap mMap;
    private static final String REDIRECT_URI = "extremobemotrafficjam://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    public Dialog dialog;
    public RelativeLayout popupbg;
    public double latitude;
    public double longitude;
    public LocationManager lm;
    public MarkerOptions markerOptions;
    public Marker myMark;
    public PlayerApi playerApi;
    public final String user = "extremobemo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("songname");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren() ){
                    System.out.println(child.child("location").child("lat").getValue());
                    Number lat = (Number) child.child("location").child("lat").getValue(); // Long or Double
                    Number lon = (Number) child.child("location").child("lon").getValue();
                    double latf = lat.floatValue();
                    double lonf = lon.floatValue();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latf, lonf)));
                    //TODO ALWAYS RETURNING 0 :/
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

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
                        hostStation();
                        LatLng location = getLocation();
                        c.writeLocation(location.latitude, location.longitude);
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

    public void subscribe(String user){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("songname");
        ref.child(user).child("track").child("songname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    LatLng getLocation() {
        try {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            Location location = lm.getLastKnownLocation(LOCATION_SERVICE);
            Double lat = (Double) location.getLatitude();
            Double lon = (Double) location.getLongitude();
            LatLng result = new LatLng(lat,lon);
            return result;
        }
        catch(SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(myMark != null){

        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        c.writeLocation(location.getLatitude(), location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude()));
        myMark = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        System.out.println("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
        lm = null;
    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("Please Enable GPS and Internet");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public void hostStation(){
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
                                        c.writeLocation(latitude,longitude);
                                        cover = bitmap;
                                        Drawable j = new BitmapDrawable(getResources(), bitmap);
                                        BitmapDescriptor d = BitmapDescriptorFactory.fromBitmap(cover);
                                        markerOptions = new MarkerOptions().position(new LatLng(40,-96))
                                                .title(playerState.track.name)
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
                        System.out.print("BEN");
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
                                        i.setImageDrawable(j);
                                        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(getDominantColor(bitmap))));
                                        TextView song_n = (TextView) dialog.findViewById(R.id.song_name);
                                        TextView song_a = (TextView) dialog.findViewById(R.id.artist);
                                        song_n.setText(playerState.track.name);
                                        song_a.setText(playerState.track.artist.name);
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

    }
}


