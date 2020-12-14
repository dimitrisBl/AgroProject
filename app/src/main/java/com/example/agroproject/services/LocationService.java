package com.example.agroproject.services;

/**
 *  TODO CLASS DESCRIPTION
 *
 */
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

public class LocationService extends Service implements Executor{

    private final String TAG = "LocationService";

    // Google's API for location service
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Location callback
    private LocationCallback locationCallback;

    // Location request
    private LocationRequest locationRequest;

    // Coordinates
    private double latitude;
    private double longitude;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start request for location
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * TODO DESCRIPTION
     */
    @SuppressLint("MissingPermission")
    private void requestLocation(){

        // Initialize FusedLocationProviderClient object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Location callBack method
        locationCallBackExecute();

        // Initialize a location request
        locationRequest = new LocationRequest();

        //PRIORITY_HIGH_ACCURACY using from the gps
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Start location service
        getLocation();
    }

    /**
     *  TODO DESCRIPTION
     */
    @SuppressLint("MissingPermission")
    public void getLocation(){
        Log.d(TAG, "Location service is performed");
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener( this, new OnSuccessListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            // Get the latitude
                            latitude = location.getLatitude();

                            // Get the longitude
                            longitude = location.getLongitude();

                        }else{
                            //performs location request if the last location is null
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                        }

                        // Send coordinates in MapActivity
                        sendMessageToActivity(latitude,longitude);
                    }

                });
    }

    /**
     * TODO description
     * @param latitude
     * @param longitude
     */
    private  void sendMessageToActivity(double latitude, double longitude) {

        // Instantiate an intent
        Intent intent = new Intent("GPSLocation");

        // Include extra data
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     This method handle the requestLocationUpdates.
     Used for receiving notifications from the FusedLocationProviderApi
     when the device location has changed or can no longer be determined.
     */
    private void locationCallBackExecute(){
        Log.d(TAG, "Location CallBack service is performed");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {

                    // Get the latitude
                    latitude = location.getLatitude();

                    // Get the longitude
                    longitude = location.getLongitude();
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stop requestLocationUpdates method from FusedLocationProviderClient service
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
