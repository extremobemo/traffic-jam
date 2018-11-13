package com.example.the_power.traffic_jam;

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
        //FirebaseConnect user = new FirebaseConnect(name, song_id);
        myRef.child(userID).child("track").child("songname").setValue(song_id);
        myRef.child(userID).child("track").child("imageuri").setValue(image);
        myRef.child(userID).child("track").child("trackuri").setValue(uri);

    }
}