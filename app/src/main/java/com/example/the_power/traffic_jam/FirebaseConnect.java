package com.example.the_power.traffic_jam;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
    public void writeNewUser(String userId, String name, String song_id) {
        FirebaseConnect user = new FirebaseConnect(name, song_id);
        myRef.child("extremobemo").child("songname").setValue(song_id);
    }
}