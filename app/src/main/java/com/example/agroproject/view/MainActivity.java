package com.example.agroproject.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityMainBinding;
import com.example.agroproject.model.agro_api.HttpRequest;
import com.example.agroproject.model.agro_api.JsonParser;
import com.example.agroproject.model.agro_api.StringBuildForRequest;
import com.example.agroproject.services.LocationService;
import com.example.agroproject.services.NetworkUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;


public class MainActivity extends AppCompatActivity {

    /** Class TAG */
    private final String TAG = "MainActivity";

    /** Permission request code */
    private static final int LOCATION_PERMISSION_CODE = 1;

    /** Location source settings intent code */
    private static final int LOCATION_SOURCE_SETTINGS_CODE = 2;

    /** LocationManager */
    private LocationManager locationManager;

    /** Activity view binding */
    private ActivityMainBinding binding;

    /** NetworkUtil */
    private NetworkUtil networkUtil;

    /** Device coordinates */
    private double latitude;
    private double longitude;



    /** JSON data from GET request in agro api */
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Hide Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // Initialize a LocationManager object
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        // Permission check service
        checkPermissions();
        //Open Bottom Navigation Menu
        bottomNavigationMenu();
        // Instantiate a NetworkUtil object
        networkUtil = new NetworkUtil(this);
        // We are registering an observer (responseReceiver) with action name GetRequestData to receive Intents after http Get request in Agro api.
        LocalBroadcastManager.getInstance(this).registerReceiver(agroApiResponseReceiver, new IntentFilter("GetRequestData"));
    }

    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "GetRequestData".
     *  TODO MORE DESCRIPTION
     */
    private BroadcastReceiver agroApiResponseReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get response data and request type that are included in the Intent
            String responseData = intent.getStringExtra("Response data");
            String requestType = intent.getStringExtra("Request type");

            if(requestType.equals("Get all polygons")){
                // Parse response data
                jsonArray = JsonParser.parseResponse(responseData);
                Log.d(TAG,"response data "+responseData);
            }
        }
    };

    /**
     * TODO:DESCRIPTIOn
     */
    private void bottomNavigationMenu(){
        binding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        if(isGpsEnable()){
                            // Open MapActivity Class
                            Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
                            mapIntent.setAction("Get coordinates from main");
                            mapIntent.putExtra("latitude",latitude);
                            mapIntent.putExtra("longitude",longitude);
                            startActivity(mapIntent);
                        }
                        return true;
                    case R.id.navigation_farms:
                        // Open ListViewActivity Class
                        Intent recyclerViewIntent = new Intent(MainActivity.this, FarmListViewActivity.class);
                        startActivity(recyclerViewIntent);
                        return true;
                    case R.id.navigation_analytics:
                        if (jsonArray != null){
                            // Open farmDetailsActivity
                            Intent farmDetailsActivityIntent = new Intent(MainActivity.this, FarmDetailsActivity.class);
                            farmDetailsActivityIntent.putExtra("ALL POLYGONS",jsonArray.toString());
                            startActivity(farmDetailsActivityIntent);
                            return true;
                        }
                }
                return false;
            }
        });
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
                    {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        } else {
            // Start the location service
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
                // Start the location service
                startLocationService();
            }else{
                // Location permission not granted
                Toast.makeText(MainActivity.this,
                        "Accept this permission for use map and other services",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If i returns from location source settings intent
        if(requestCode == LOCATION_SOURCE_SETTINGS_CODE){
            // Check status of the GPS
            if(isGpsEnable()){
                Intent locationServiceIntent = new Intent(this, LocationService.class);
                startService(locationServiceIntent);
            }
        }
    }

    /**
     * This method check the GPS status, if GPS state is off
     * displays a alert to GPS turn on, if GPS state is on he don't any something.
     *
     * @return true if gps status is enable or false if gps status is disable.
     */
    @SuppressLint("MissingPermission")
    private boolean isGpsEnable(){
        // Get GPS provider status
        boolean providerEnable = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(providerEnable){
            // GPS is enable
            return  true;
        }else{
            // GPS is not enable
            // Show alert for GPS turn on
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("GPS permission")
                    .setMessage("The GPS is required for this app, go to location source settings to turn on GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, LOCATION_SOURCE_SETTINGS_CODE);
                    }))
                    .setNegativeButton("No", ((dialogInterface, i) -> {
                            Toast.makeText(MainActivity.this,
                                    "GPS is required for use map and other services. " +
                                            "Please enable GPS.",Toast.LENGTH_LONG).show();
                            finish();
                    }))
                    .setOnCancelListener(((dialogInterface) -> {
                            Toast.makeText(MainActivity.this, "GPS is required for use map and other services. " +
                                "Please enable GPS.", Toast.LENGTH_LONG).show();
                    }))
            .show();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume executed");
        // Receive messages about current location.
        // We are registering an observer (locationReceiver) to receive intents with action name "LocationUpdates".
        registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_NAME));
        // Receive messages about Network status.
        // We are registering an observer from NetworkUtil class which extends BroadCast Receiver class
        // to receive intents with action name "CONNECTIVITY_ACTION".
        registerReceiver(networkUtil, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (jsonArray == null){
            Log.d(TAG,"ALL POLYGONS GET REQUEST EXECUTED");
            // Get request on endpoint polygons of Agro api
            HttpRequest.getRequest(this, StringBuildForRequest.polygonsRequestLink(), "Get all polygons");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause executed");
        // Unregister since the activity is about to be closed.
        unregisterReceiver(locationReceiver);
        unregisterReceiver(networkUtil);
    }

    /**
     *  This method starts an intent service
     *  in LocationService class.
     *
     */
    @SuppressLint("MissingPermission")
    public void startLocationService(){
        // Check status of the GPS
        if(isGpsEnable()){
           Intent locationServiceIntent = new Intent(this, LocationService.class);
           startService(locationServiceIntent);
        }
    }

    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "LocationUpdates". Receives the current latitude
     *  and longitude of the device.
     */
    private BroadcastReceiver locationReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this,
                    "Receive coordinates in main activity: "
                            +intent.getDoubleExtra("latitude",0.0),
                    Toast.LENGTH_SHORT).show();

            // Get extra data included in the Intent
            latitude = intent.getDoubleExtra("latitude",0.0);
            longitude = intent.getDoubleExtra("longitude",0.0);

            Log.d(TAG, "receive coordinates: " +latitude+" "+longitude);
        }
    };
}