package com.example.agroproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.agroproject.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "MapActivity";

    // Location permission request code
    private final int LOCATION_PERMISSION_CODE = 1;

    // Google's API for location service
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Location callback
    private LocationCallback locationCallback;

    // Location request
    private LocationRequest locationRequest;
    //-------------------------------------------

    // Google Map
    private GoogleMap mMap;

    // Binding
    private ActivityMapsBinding binding;

    // current view
    private View currentView;

    // Device coordinates
    private double latitude;
    private double longitude;
    private LatLng currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Receive this Broadcast GPSLocationUpdates message from LocationService
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));

        // Current view
        currentView = findViewById(android.R.id.content);

        // Initialize FusedLocationProviderClient object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize a location request
        locationRequest = new LocationRequest();

        //PRIORITY_HIGH_ACCURACY using from the gps
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(05); //0.5 metro
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Location callBack method
        //locationCallBackExecute();

        // Permission check service
        checkPermissions();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void checkPermissions(){
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
            // Start location service
            //getLocation();

            //Start the location service
            startLocationService();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == LOCATION_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "location permission granted!");
                // Start location service
                //getLocation();

                //Start the location service
                startLocationService();

            }else{
                // Location permission not granted
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    /**
     *  TODO description
     */
    public void startLocationService(){
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
    }

    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "GPSLocation" is broadcasted.
     */
    private BroadcastReceiver mMessageReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            // Get extra data included in the Intent
            latitude = intent.getDoubleExtra("latitude",0.0);
            longitude = intent.getDoubleExtra("longitude",0.0);

            Log.d("Message from receiver", "Got latitude: " + latitude);

            // Refresh map
            onMapReady(mMap);
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Enable visibility for zoom controls buttons
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Initialize map
        mMap = googleMap;

        // Setup satellite map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Create LatLng
        currentLocation = new LatLng(latitude, longitude);

        // Add a marker in current location and move the camera
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your location: "+latitude+"  "+longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Initiating Menu XML file (activity_map_menu.xml)
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_map_menu, menu);

        // set title alignment for each item is center
        int positionOfMenuItem0 = 0; //or any other postion
        MenuItem item = menu.getItem(positionOfMenuItem0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        int positionOfMenuItem1 = 1; //or any other postion
        MenuItem item1 = menu.getItem(positionOfMenuItem1);
        SpannableString s1 = new SpannableString(item1.getTitle());
        s1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s1.length(), 0);
        item1.setTitle(s1);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return true;
    }


//    @SuppressLint("MissingPermission")
//    public void getLocation(){
//        Log.d(TAG, "Location service is performed");
//        fusedLocationProviderClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @SuppressLint("MissingPermission")
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//
//                            // Get the latitude
//                            latitude = location.getLatitude();
//
//                            // Get the longitude
//                            longitude = location.getLongitude();
//
//                            // Refresh map for this location
//                            onMapReady(mMap);
//
//                        }else{
//                            //performs location request if the last location is null
//                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//                        }
//                    }
//                });
//    }

//    /**
//     This method handle the requestLocationUpdates.
//     Used for receiving notifications from the FusedLocationProviderApi
//     when the device location has changed or can no longer be determined.
//     */
//    private void locationCallBackExecute(){
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//
//                for (Location location : locationResult.getLocations()) {
//
//                    // Get the latitude
//                    latitude = location.getLatitude();
//
//                    // Get the longitude
//                    longitude = location.getLongitude();
//
//                }
//                // Refresh map for this location
//                onMapReady(mMap);
//            }
//        };
//    }
    
    @Override
    protected void onStop() {
        super.onStop();
        //stop requestLocationUpdates method from FusedLocationProviderClient service
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}