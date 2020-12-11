package com.example.agroproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.example.agroproject.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    // Location permission request code
    private static final int LOCATION_PERMISSION_CODE = 1;

    // Google's API for location service
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Location callback
    private LocationCallback locationCallback;

    // Location request
    private LocationRequest locationRequest;
    //-------------------------------------------

    //Google Map
    private GoogleMap mMap;

    //Binding
    private ActivityMapsBinding binding;

    // current view
    private View currentView;

    // Device coordinates
    private LatLng currentLocation;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Current view
        currentView = findViewById(android.R.id.content);

        // Initialize FusedLocationProviderClient object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Location callBack method
        locationCallBackExecute();

        // Permission check service
        checkLocationPermissions();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @SuppressLint("MissingPermission")
    private void requestLocation(){

        // Initialize a location request
        locationRequest = new LocationRequest();

        //PRIORITY_HIGH_ACCURACY using from the gps
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        //locationRequest.setSmallestDisplacement(1); //1 metro
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //performs location request
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    public void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        } else {
            //start the location service
            requestLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                //start the location service
                requestLocation();

            } else {
                Snackbar.make(currentView, "Permission is not accepted", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Enable visibility for zoom controls buttons
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        
        // Initialize map
        mMap = googleMap;

        // Setup satellite map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
            Toast.makeText(this,"over here", Toast.LENGTH_SHORT).show();
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

                Log.d("location result ", " "+locationResult.getLastLocation().getLatitude());

                // Get the latitude
                latitude = locationResult.getLastLocation().getLatitude();
                // Get the longitude
                longitude = locationResult.getLastLocation().getLongitude();

                // Instantiate the class LatLng
                currentLocation = new LatLng(latitude,longitude);


                //instantiate the class Geocoder
                Geocoder geocoder = new Geocoder(MapActivity.this.getApplicationContext());

                try {

                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                    // Add a marker in current location and move the camera
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title(addressList.get(0).getAddressLine(0)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 19.2f));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

}