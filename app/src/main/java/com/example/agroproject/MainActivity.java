package com.example.agroproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    /** Class TAG */
    private final String TAG = "MainActivity";

    /** Permission request code */
    private static final int LOCATION_PERMISSION_CODE = 1;


    private double latitude;

    private double longitude;

    // current view
    private View currentView;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate method");

        // Current view
        currentView = findViewById(android.R.id.content);

        button = findViewById(R.id.activity_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent secondActivity = new Intent(
                        MainActivity.this, SecondActivity.class);
                startActivity(secondActivity);

            }
        });



        if(Build.VERSION.SDK_INT >= 16){

           if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //permission question
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);

           }else{
               //Start the location service
               startLocationService();
           }

        }else{
            //Start the location service
            startLocationService();
        }
    }

    public void startLocationService(){
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
    }




    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this,
                    "Receive coordinates in main activity: "+latitude,Toast.LENGTH_SHORT).show();

            // Get extra data included in the Intent
            // Get extra data included in the Intent
             latitude = intent.getDoubleExtra("latitude",0.0);
             longitude = intent.getDoubleExtra("longitude",0.0);

            Log.d("main activity receiver", "receive coordinates: " +latitude+" "+longitude);
        }
    };


//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        Toast.makeText(this," im over here", Toast.LENGTH_LONG).show();
//    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == LOCATION_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                startLocationService();
            }else{
                Snackbar.make(currentView, "Permission is not accepted", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Receive messages about current location.
        // We are registering an observer (GPSLocationUpdates) to receive Intents with actions named "LocationService".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Unregister since the activity is about to be closed.
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
//    }
}