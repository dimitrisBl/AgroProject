package com.example.agroproject.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityMainBinding;


import com.example.agroproject.model.file.KmlFileWriter;

import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.services.LocationService;
import com.example.agroproject.services.NetworkUtil;

import java.io.File;


/**
 * TODO IS NETWORK ENABLE METHOD
 */

public class MainActivity extends AppCompatActivity {

    /** Class TAG */
    private final String TAG = "MainActivity";

    /** Permission request code */
    private static final int LOCATION_PERMISSION_CODE = 1;

    /** location source settings intent code */
    private static final int LOCATION_SOURCE_SETTINGS_CODE = 2;

    /** CreateAreaActivity intent code */
    private static final int CREATE_AREA_ACTIVITY_CODE = 3;

    /** LocationManager */
    private LocationManager locationManager;

    /** Activity view binding */
    private ActivityMainBinding binding;

    /** NetworkUtil */
    private NetworkUtil networkUtil;

    /** Device coordinates */
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Instantiate a NetworkUtil object
        networkUtil = new NetworkUtil(this);
        // Initialize a LocationManager object
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        // Permission check service
        checkPermissions();

        KmlLocalStorageProvider kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        if(!kmlLocalStorageProvider.loadFarmMap().isEmpty()){
            kmlLocalStorageProvider.loadFarmMap();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        KmlFileWriter kmlFileWriter = new KmlFileWriter(this);
            kmlFileWriter.fileToWrite();
        File file = new File("test5.kml");
        if (file.exists()) {
            Toast.makeText(this, "FILE EXISTS", Toast.LENGTH_SHORT).show();

        } else {
           // Toast.makeText(this, "FILE not not!! EXISTS", Toast.LENGTH_SHORT).show();
        }
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
                // Start the location service
                Intent locationServiceIntent = new Intent(this, LocationService.class);
                startService(locationServiceIntent);
            }
        }// If i returns from CreateAreaActivity intent
        else if (requestCode == CREATE_AREA_ACTIVITY_CODE){
            // Check status of the GPS
            isGpsEnable();
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
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS permission")
                    .setMessage("The GPS is required for this app, go to location source settings to turn on GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent,LOCATION_SOURCE_SETTINGS_CODE);
                    }))
                    .setNegativeButton("No", ((dialogInterface, i) -> {
                            Toast.makeText(MainActivity.this,
                                    "GPS is required for use map and other services. " +
                                            "Please enable GPS.",Toast.LENGTH_LONG).show();
                            finish();
                    }))
                    .setOnCancelListener(((dialogInterface) -> {
                            Toast.makeText(this, "GPS is required for use map and other services. " +
                                "Please enable GPS.", Toast.LENGTH_LONG).show();
                    }))
            .show();
        }
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Initiating Menu XML file (activity_main_top_menu.xml)
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        // set title alignment for each item is center
        int positionOfMenuItem0 = 0; //or any other postion
        MenuItem item = menu.getItem(positionOfMenuItem0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        // set title alignment for each item is center
        int positionOfMenuItem1 = 1; //or any other postion
        MenuItem item1 = menu.getItem(positionOfMenuItem1);
        SpannableString s1 = new SpannableString(item1.getTitle());
        s1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s1.length(), 0);
        item1.setTitle(s1);

        // set title alignment for each item is center
        int positionOfMenuItem2 = 2; //or any other postion
        MenuItem item2 = menu.getItem(positionOfMenuItem2);
        SpannableString s2 = new SpannableString(item2.getTitle());
        s2.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s2.length(), 0);
        item2.setTitle(s2);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.yourMap_item:
                if(isGpsEnable()){
                    Intent yourMapIntent = new Intent(this, MapActivity.class);
                    yourMapIntent.putExtra("latitude",latitude);
                    yourMapIntent.putExtra("longitude",longitude);
                    startActivity(yourMapIntent);
                }
             return true;

            case R.id.createArea_item:
                if(isGpsEnable()){
                    Intent createAreaIntent = new Intent(this, CreateAreaActivity.class);
                    createAreaIntent.putExtra("latitude", latitude);
                    createAreaIntent.putExtra("longitude", longitude);
                    startActivityForResult(createAreaIntent,CREATE_AREA_ACTIVITY_CODE);
                }
            return true;
            case R.id.yourFarms_item:
                Intent recyclerViewIntent = new Intent(this, ListViewActivity.class);
                startActivity(recyclerViewIntent);
            default:
                return super.onOptionsItemSelected(item);
        }

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
            // Start the location service
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