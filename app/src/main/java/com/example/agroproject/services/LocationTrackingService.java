package com.example.agroproject.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 *  TODO CLASS DESCRIPTION
 *
 *  */
public class LocationTrackingService extends Service {

    /** Class TAG */
    private final String TAG = "LocationTrackingService";

    /** Google's API for location service */
    private FusedLocationProviderClient fusedLocationProviderClient;

    /** Location callback */
    private LocationCallback locationCallback;

    /** Location request*/
    private LocationRequest locationRequest;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // Instantiate  FusedLocationProviderClient object
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);
        // Location callBack method
        locationCallBackExecute();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"service started");
        // Start request for location
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * This method initialize and execute
     * a location request using the GPS.
     */
    @SuppressLint("MissingPermission")
    private void requestLocation(){
        // Instantiate LocationRequest object
        locationRequest = new LocationRequest();

        // For high accuracy location
        locationRequest.setInterval(5000); //5 second
        locationRequest.setFastestInterval(5000);
        //locationRequest.setSmallestDisplacement(1); //1 metro
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //performs request location updates
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    /**
     * This method send an intent with an action "GPSLocationUpdates".
     * The intent sent is received by the Activity.
     *
     * @param location receives the current device location.
     *
     */
    private  void sendMessageToActivity(LocationResult location) {
        Log.d(TAG, "sending message on activity");
        // Instantiate an intent
        Intent intent = new Intent("GPSLocationUpdates");

        // Include the device coordinates in intent
        intent.putExtra("latitude", location.getLastLocation().getLatitude());
        intent.putExtra("longitude", location.getLastLocation().getLongitude());

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
                // Send coordinates in MainActivity
                sendMessageToActivity(locationResult);
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