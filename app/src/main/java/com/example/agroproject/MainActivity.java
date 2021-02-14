package com.example.agroproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.agroproject.databinding.ActivityMainBinding;
import com.example.agroproject.services.LocationService;

public class MainActivity extends AppCompatActivity {

    /** Class TAG */
    private final String TAG = "MainActivity";

    /** Permission request code */
    private static final int LOCATION_PERMISSION_CODE = 1;

    /** Device coordinates */
    private double latitude;
    private double longitude;

    /** Binding */
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG,"onCreate method");

        Button button = binding.activityBtn;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent secondActivity = new Intent(
                        MainActivity.this, SecondActivity.class);
                startActivity(secondActivity);

            }
        });

        // Permission check service
        checkPermissions();
    }

    /**
     *  This method check if location permission granted.
     *  If the permission has been granted calls the startLocationService method to start a location service.
     *  If the permission has not been granted displays a request for the missing permissions and asks the permission.
     */
    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission.

            //permission question
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        } else {
            // Start the location service
            startLocationService();
        }
    }

    /**
     * TODO DESCRIPTION
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == LOCATION_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted!");
                // Start the location service
                startLocationService();
            }else{
                // Location permission not granted
                Toast.makeText(MainActivity.this,
                        "Accept this permission for use map and other services",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /**
     * This method ask for gps enable if the gps of the device is off, else
     * if the gps is enable  he don't any something.
     * TODO MORE DESCRIPTION
     * @return boolean, true if gps status is enable or false if gps status is disable.
     */
    private boolean isGpsEnable(){
        // Initialize a LocationManager object
        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        // Get GPS provider status
        boolean providerEnable = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(providerEnable){
            // GPS is enable
            return true;
        }else{
            // GPS is not enable
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS permission")
                    .setMessage("GPS is required for use map and other services. Please enable GPS")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }))
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this,
                                    "GPS is required for use map and other services. Please enable GPS",Toast.LENGTH_LONG).show();
                        }
                    })
            .show();
        }
        return  false;
    }



    /**
     *  This method starts an intent service
     *  in LocationService class.
     */
    public void startLocationService(){
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
    }

    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "GPSLocationUpdates".
     *  TODO MORE DESCRIPTION
     */
    private BroadcastReceiver locationReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this,
                    "Receive coordinates in main activity: "+latitude,Toast.LENGTH_SHORT).show();

            // Get extra data included in the Intent
             latitude = intent.getDoubleExtra("latitude",0.0);
             longitude = intent.getDoubleExtra("longitude",0.0);

            Log.d(TAG, "receive coordinates: " +latitude+" "+longitude);
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        // Receive messages about current location.
        // We are registering an observer (GPSLocationUpdates) to receive Intents with actions named "LocationService".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationReceiver, new IntentFilter("GPSLocationUpdates"));

        // GPS status
        isGpsEnable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
    }
}