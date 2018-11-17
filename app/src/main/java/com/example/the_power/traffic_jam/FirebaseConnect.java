package com.example.the_power.traffic_jam;

import android.app.Notification;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.protocol.types.ImageUri;


public class FirebaseConnect {
    public String username;
    public String song_id;
    public FirebaseConnect(){}

    public FirebaseConnect(String username, String song_id) {
        this.username = username;
        this.song_id = song_id;
    }
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef = database.getReference("songname");
    public void writeNewUser(String userID, String song_id, ImageUri image, String uri) {
        myRef.child(userID).child("track").child("songname").setValue(song_id);
        myRef.child(userID).child("track").child("imageuri").setValue(image);
        myRef.child(userID).child("track").child("trackuri").setValue(uri);
    }

    public void writeLocation(Double lat, Double lon){
        myRef.child(username).child("location").child("lat").setValue(lat);
        myRef.child(username).child("location").child("lon").setValue(lon);
    }

}