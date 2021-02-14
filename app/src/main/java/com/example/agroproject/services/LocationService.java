package com.example.agroproject.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

public class LocationService extends Service implements Executor {

    /** Class TAG */
    private final String TAG = "LocationService";

    /** Google's API for location service */
    private FusedLocationProviderClient fusedLocationProviderClient;

    /** LocationCallback object */
    private LocationCallback locationCallback;

    /** LocationRequest object */
    private LocationRequest locationRequest;

    /** The current geographic latitude of the device */
    private double latitude;

    /** The current geographic longitude of the device */
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
    public void onCreate() {
        super.onCreate();
        // Instantiate  FusedLocationProviderClient object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Location callBack method
        locationCallBackExecute();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start request for location
        startLocationService();
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * This method initialize a location request using the GPS,
     * then calls the getCurrentLocation method
     * to starts a location service.
     */
    @SuppressLint("MissingPermission")
    private void startLocationService(){

        // Initialize a location request
        locationRequest = new LocationRequest();

        //PRIORITY_HIGH_ACCURACY uses the gps
        locationRequest.setInterval(5000); // 1 second
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Start location service
        getCurrentLocation();

        // Location callBack method
        //locationCallBackExecute();
    }

    /**
     *  This method get current location with with FusedLocationProviderClient Google API.
     *  If last location is null performs a request location updates to gets the current location.
     */
    @SuppressLint("MissingPermission")
    public void getCurrentLocation(){
        Log.d(TAG, "Location service is performed");
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener( this, new OnSuccessListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Get the current latitude
                            latitude = location.getLatitude();
                            // Get the current longitude
                            longitude = location.getLongitude();
                            // Send coordinates in MapActivity
                            sendMessageToActivity(latitude,longitude);
                        }else{
                            // Performs location request if the last location is null
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                        }
                    }

                });
    }

    /**
     * This method send an intent with an action "LocationService".
     * The intent sent is received by the MainActivity.
     *
     * @param latitude takes the current device latitude
     * @param longitude takes the current device longitude
     */
    private  void sendMessageToActivity(double latitude, double longitude) {
        Log.d(TAG, "sending message to MainActivity");
        // Instantiate an intent
        Intent intent = new Intent("LocationService");
        // Include extra data
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * This callback method handle the location Update request, is triggered
     * when called the method requestLocationUpdates of FusedLocationProviderApi.
     * Used for receiving notifications from the FusedLocationProviderApi
     * when the device location has changed or can no longer be determined.
     */
    private void locationCallBackExecute(){
        Log.d(TAG, "Location CallBack service is performed");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Get the current latitude
                    latitude = location.getLatitude();
                    // Get the current longitude
                    longitude = location.getLongitude();
                }
                // Send coordinates in MainActivity
                sendMessageToActivity(latitude,longitude);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy executed, location updates is stopped");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
