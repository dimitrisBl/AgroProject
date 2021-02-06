package com.example.agroproject.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.agroproject.ui.MainActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Collections;
import java.util.concurrent.Executor;

/**
 *  TODO CLASS DESCRIPTION
 */

public class LocationService extends Service implements Executor {

    // Class TAG
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

        // Instantiate  FusedLocationProviderClient object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize a location request
        locationRequest = new LocationRequest();

        //PRIORITY_HIGH_ACCURACY uses the gps
        locationRequest.setInterval(1000); // 1 second
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Start location service
        getCurrentLocation();

        // Location callBack method
        locationCallBackExecute();
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

                    // Get the latitude
                    latitude = location.getLatitude();

                    // Get the longitude
                    longitude = location.getLongitude();
                }

                // Send coordinates in MainActivity
                sendMessageToActivity(latitude,longitude);
            }
        };
    }
}
