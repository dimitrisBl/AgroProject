package com.example.agroproject.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.agroproject.MainActivity;
import com.example.agroproject.R;
import com.example.agroproject.services.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.agroproject.databinding.ActivityMapBinding;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 *  TODO CLASS DESCRIPTION
 *
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "MapActivity";

    // Location permission request code
    private final int LOCATION_PERMISSION_CODE = 1;

    private static final int CREATE_AREA_ACTIVITY_INTENT_CODE = 2;

    // Google Map
    private GoogleMap mMap;

    // Marker
    private Marker marker;

    // Binding
    private ActivityMapBinding binding;

    // current view
    private View currentView;

    // Device coordinates
    private double latitude;
    private double longitude;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Receive messages about current location.
        // We are registering an observer (gpsLocationReceiver) to receive Intents with actions named "LocationService".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationReceiver, new IntentFilter("LocationService"));

        // Current view
        currentView = findViewById(android.R.id.content);

        // Permission check service
        checkPermissions();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
            //Start the location service
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
     *  This method starts an intent service
     *  in LocationService class
     */
    private void startLocationService(){
        Intent locationService = new Intent(this, LocationService.class);
        startService(locationService);
    }

    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "LocationService".
     *  TODO MORE DESCRIPTION
     */
    private BroadcastReceiver locationReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"receive latitude: "+latitude+" and longitude: "
                    +longitude+" from location service");

            // Get extra data included in the Intent
            latitude = intent.getDoubleExtra("latitude",0.0);
            longitude = intent.getDoubleExtra("longitude",0.0);

            // Create LatLng
            currentLocation = new LatLng(latitude, longitude);

            // Create MarkerOption for current location
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(currentLocation).title("Your location: "+latitude+"  "+longitude);

            // Add Marker in the map
            marker = mMap.addMarker(markerOptions);

            // Move the camera in current location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f));
        }
    };
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This method Adds a marker in current location, sets zoom in the camera and defines the satellite map type.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Enable visibility for zoom controls buttons
        googleMap.getUiSettings().setZoomControlsEnabled(true);


        // auta tha mpoun otan kanw ton location live tracker
        //googleMap.setMyLocationEnabled(true);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//            @Override
//            public boolean onMyLocationButtonClick() {
//                Toast.makeText(MapActivity.this, " im over heree", Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });

        // Initialize map
        mMap = googleMap;

        // Setup satellite map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Move the in default location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(38.2749497,  23.8102717), 18f));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Initiating Menu XML file (activity_map_menu.xml)
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_map_menu, menu);

        // Enable back button in menu
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set title alignment for each item is center
        int positionOfMenuItem0 = 0; //or any other postion
        MenuItem item = menu.getItem(positionOfMenuItem0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            // Back button case
            case android.R.id.home:
                /**
                 * TODO BUG FIX IN THIS CASE
                 */
                finish();

            case R.id.create_area:
                Intent intent = new Intent(this,CreateAreaActivity.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivityForResult(intent, CREATE_AREA_ACTIVITY_INTENT_CODE);
            return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * TODO DESCRIPTION
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == CREATE_AREA_ACTIVITY_INTENT_CODE){
            if(resultCode == RESULT_OK){
                Toast.makeText(MapActivity.this,
                        "Pressing save area button!!",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
        super.onDestroy();
    }
}