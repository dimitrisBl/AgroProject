package com.example.agroproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    // Location permission request code
    private static final int LOCATION_PERMISSION_CODE = 1;

    // FusedLocationProviderClient
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Location callback
    private LocationCallback locationCallback;

    // Location request
    private LocationRequest locationRequest;

    //-------------------------------------------


    // current view
    private View currentView;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Current view
        currentView = findViewById(android.R.id.content);

        // Initialize FusedLocationProviderClient object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize a location request
        locationRequest = new LocationRequest();

        //PRIORITY_HIGH_ACCURACY using from the gps
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Location callBack method
        locationCallBackExecute();

        if(Build.VERSION.SDK_INT >= 23){

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
               fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
               //Start the location service
               //startLocationService();
           }


        }else{

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            //Start the location service
            //startLocationService();

        }

    }

    public void startLocationService(){
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
    }




    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == LOCATION_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            }else{
                Snackbar.make(currentView, "Permission is not accepted", Snackbar.LENGTH_LONG).show();
            }
        }


    }

    /*
       This method handle the requestLocationUpdates.
       Used for receiving notifications from the FusedLocationProviderApi
       when the device location has changed or can no longer be determined.
       */
    private void locationCallBackExecute(){
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    //set coordinates
                    Toast.makeText(MainActivity.this,"latitude from locationCallBackExecute: "
                            +location.getLatitude(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

}