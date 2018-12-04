package com.example.the_power.traffic_jam;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CustomAlertDialog extends AppCompatActivity {
    public Context context;


    public void showAlertDialogButtonClicked() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("AlertDialog");
        builder.setMessage("Could not access device location. Try opening Google Maps " +
                "to ensure device has a location.");

        // add the buttons
        builder.setPositiveButton("Got it!", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }
    public void AlreadyHostingError() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Already hosting!");
        builder.setMessage("You cannot subscribe to another station while you are hosting.");

        // add the buttons
        builder.setPositiveButton("Got it!", null);
        // create and show the alert dialog
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public void setContext(Context context){
        this.context = context;
    }
}
