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

import java.util.concurrent.Executor;

public class LocationTrackingService extends Service implements Executor {

    /** Class TAG */
    private final String TAG = "LocationTrackingService";

    /** Google's API for location service */
    private FusedLocationProviderClient fusedLocationProviderClient;

    /** LocationRequest object */
    private LocationRequest locationRequest;

    /** LocationCallback object */
    private LocationCallback locationCallback;

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand method executed");

        // Location callBack method
        //locationCallBackExecute();

        // Start request for location
        //startLocationTrackingService();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    public void startLocationTrackingService(){
        Log.d(TAG, "startLocationTrackingService method executed");
        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);

        // Initialize a location request
        locationRequest = new LocationRequest();

        // Location request setup
        locationRequest.setInterval(1000); //5 seconds
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(1); //1 metro
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Performs location request if the last location is null
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
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
                Log.d(TAG, "onLocationResult location tracker");


                latitude = locationResult.getLastLocation().getLatitude();
                longitude =locationResult.getLastLocation().getLongitude();

                // Send coordinates in MainActivity
                sendMessageToActivity(latitude,longitude);
            }
        };
    }

    /**
     * This method send an intent with an action "LocationTrackingService".
     * The intent sent is received by the CreateAreaActivity.
     *
     * @param latitude takes the current device latitude
     * @param longitude takes the current device longitude
     */
    private  void sendMessageToActivity(double latitude, double longitude) {
        Log.d(TAG, "sending message to CreateAreaActivity");
        // Instantiate an intent
        Intent intent = new Intent("LocationTrackingService");
        // Include extra data
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
