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

/**
 * TODO CLASS DESCRIPTION
 * AUTO TO SERVICE EKTELEITE STO BACKGROUND H EKKIHNSH GINETAI APO THN MAINACTIVITY,
 * ME THN BOITHEIA TOU LOCALBROADCASTMANAGER STELNONTAI DEDOMENO STO UI THREAD ME SKOPO
 * NA GINOUN ALLAGES SXETIKA ME THN TOPOTHESIA PX H THESI TOU MARKER EPANW STON XARTI.
 */
public class LocationService extends Service implements Executor {

    /** Class TAG */
    private final String TAG = "LocationService";

    /** Intent action name */
    public static final String ACTION_NAME = "LocationUpdates";

    /** Intent */
    private Intent intent;

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

    /**
     * This method initialize a location request, using
     * the GPS and wifi provider for high accuracy.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Instantiate FusedLocationProviderClient object
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);

        // Define the callBack method
        locationCallBackExecute();

        // Initialize a location request
        locationRequest = new LocationRequest();

        //PRIORITY_HIGH_ACCURACY uses the gps
        locationRequest.setInterval(5000); // 5 second
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(1); //1 metro
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start request for location
        startLocationService();
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     *  This method get current location with with FusedLocationProviderClient Google API.
     *  If last location is null performs a request location updates to gets the current location.
     */
    @SuppressLint("MissingPermission")
    public void startLocationService(){
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
                            // Send old location in Activity
                            sendMessageToActivity(latitude,longitude);
                        }
                    // Performs location request for newest location.
                    fusedLocationProviderClient.requestLocationUpdates
                            (locationRequest, locationCallback, Looper.getMainLooper());
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
        intent = new Intent(ACTION_NAME);

        // Include extra data
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        // LocalBroadcastManager used to send the  in foreground.
        sendBroadcast(intent);
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
        // Remove location updates
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
