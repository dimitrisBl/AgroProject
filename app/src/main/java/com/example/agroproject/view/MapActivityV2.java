package com.example.agroproject.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.agroproject.R;

import com.example.agroproject.databinding.ActivityMapV2Binding;

import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.services.LocationService;
import com.example.agroproject.services.NetworkUtil;
import com.example.agroproject.view.fragments.InsertFileFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivityV2 extends AppCompatActivity implements OnMapReadyCallback, InsertFileFragment.InsertFileEventListener {

    /** Class TAG */
    private final String TAG = "MapActivity";

    /** GoogleMap object */
    private GoogleMap mMap;

    /** Marker */
    private Marker marker;

    /** Activity view binding */
    private ActivityMapV2Binding binding;

    /** The current geographic latitude of the device */
    private double latitude;

    /** The current geographic longitude of the device */
    private double longitude;

    /** LatLng object for the current location  */
    private LatLng currentLocation;

    /** KmlLocalStorageProvider */
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    /** NetworkUtil control the internet connection */
    private NetworkUtil networkUtil;

    /** KmlFile Map */
    private Map<KmlFile, List<Placemark>> kmlFileMap = new HashMap<>();

    /** List with Placemark objects */
    private List<Placemark> placemarkList = new ArrayList<>();

    /** Initialize List with Marker objects */
    private List<Marker> markerList = new ArrayList<>();

    /** Initialize List with LatLng objects */
    private List<LatLng> latLngList = new ArrayList<>();

    /** Polyline */
    private Polyline polyline = null;

    /** Auxiliary variable for the inner area detection */
    private String currentOuterArea = null;

    /** Auxiliary variable for the inner area detection */
    private boolean detectInnerArea = false;

    /** This variable declares the visibility state of bottom layout */
    private boolean bottomLayoutIsEnable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapV2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Instantiate a NetworkUtil object
        networkUtil = new NetworkUtil(this);
        // Get extras from intent
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude",0.0);
        // Create the LatLng object of the current location
        currentLocation = new LatLng(latitude, longitude);
        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        // Load the kmlFile Map from shared preferences storage
        kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
        // Set bottom menu visibility false
        binding.linearLayout.setVisibility(View.GONE);
        // Set click listener for buttons
        binding.drawPolygon.setOnClickListener(buttonClickListener);
        binding.clearMap.setOnClickListener(buttonClickListener);
        binding.closeLayout.setOnClickListener(buttonClickListener);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapV2);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Initialize map
        mMap = googleMap;
        // Enable visibility for zoom controls buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Setup satellite map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Create MarkerOption for current location
        MarkerOptions markerOptions = new MarkerOptions()
                .position(currentLocation).title("Your location: "+latitude+"  "+longitude);
        // Add Marker in the map
        mMap.addMarker(markerOptions);
        // Move the camera in current location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f));
        // Set Map click listener method
        mMap.setOnMapClickListener(mapClickListener);
        // Polygon click listener
        mMap.setOnPolygonClickListener(polygonClickListener);
        // Put the existing monitoring areas in the map
        addTheExistingAreas(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Initiating Menu XML file (activity_map_menu.xml)
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_map_v2_menu, menu);
        // Enable back button in menu
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set text alignment for each item in center
        int positionOfMenuItem0 = 0; //or any other position
        MenuItem item = menu.getItem(positionOfMenuItem0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        // set text alignment for each item in center
        int positionOfMenuItem1 = 1; //or any other position
        MenuItem item1 = menu.getItem(positionOfMenuItem1);
        SpannableString s1 = new SpannableString(item1.getTitle());
        s1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s1.length(), 0);
        item1.setTitle(s1);

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
        switch (item.getItemId()){
            case R.id.createArea_item:
                // Disable visibility for zoom controls buttons
                mMap.getUiSettings().setZoomControlsEnabled(false);
                // Set bottom menu visibility true
                binding.linearLayout.setVisibility(View.VISIBLE);
                // Set the property clickable false for each polygon
                addTheExistingAreas(false);
                // bottom layout is enable
                bottomLayoutIsEnable = true;
            return true;

            case R.id.insert_file:
                // Instantiate a InsertFileFragment object
                InsertFileFragment insertFileFragment = new InsertFileFragment();
                // Start fragment activity
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.insert_file_fragment_container, insertFileFragment, insertFileFragment.getClass()
                                .getSimpleName()).addToBackStack(null).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    /**
//     * This method called if you want to close a pop up InsertFileFragment
//     */
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Toast.makeText(this,"erxomai ewdww",Toast.LENGTH_LONG).show();
//        kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
//        // Add areas in the map
//        addTheExistingAreas();
//    }

    /**
     *  TODO description
     */
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Get current button click
            Button currentButton = (Button) view;
            // Get text from current button
            String currentButtonText =
                    String.valueOf(currentButton.getText());
            switch (currentButtonText){
                case "draw area":
                    if(!latLngList.isEmpty() && markerList.size() >= 3 ) {
                        // Do null the currentOuterArea after area creation
                        currentOuterArea = null;
                        // Create PolygonOptions
                        PolygonOptions polygonOptions = new PolygonOptions()
                                .strokeWidth(5f).addAll(latLngList).strokeColor(Color.RED)
                                .fillColor(Color.argb(70, 50, 255, 0));
                        // Draw area on the Map
                        mMap.addPolygon(polygonOptions);
                        // Remove markers from the map
                        for (Marker marker : markerList) {
                            marker.remove();
                        }
                        // Show dialog
                        showSaveAlertDialog();
                        // Clean the lists
                        markerList.clear();
                        latLngList.clear();
                    }else if(!markerList.isEmpty() && markerList.size() <= 3 ){
                        // Show message
                        Toast.makeText(MapActivityV2.this,
                                "You need three markers at least to draw an area",Toast.LENGTH_LONG).show();
                    }else{
                        // Show message
                        Toast.makeText(MapActivityV2.this,
                                "Tap in the map and mark your area first", Toast.LENGTH_LONG).show();
                    }
                break;

                case "clear":
                    currentOuterArea = null;
                    latLngList.clear();
                    markerList.clear();
                    mMap.clear();
                    polyline = null;
                    addTheExistingAreas(true);
                break;

                case "close":
                    // Disable bottom layout visibility
                    binding.linearLayout.setVisibility(View.GONE);
                    // bottom layout is disable
                    bottomLayoutIsEnable = false;
                    // Enable visibility for map zoom controls buttons
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    currentOuterArea = null;
                    latLngList.clear();
                    markerList.clear();
                    mMap.clear();
                    polyline = null;
                    addTheExistingAreas(true);
                 break;
            }
        }
    };

    /**
     * TODO method description
     */
    @SuppressLint("NewApi")
    private void showSaveAlertDialog(){
        new AlertDialog.Builder(MapActivityV2.this)
                .setIcon(R.drawable.ic_baseline_save)
                .setTitle("Save")
                .setMessage("You want to save this area?")
                .setCancelable(false) // Set cancelable on touch outside
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If answer is yes show save area popup.
                        //showSaveAreaPopup();/** TODO */
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // If answer is no, clean the map and add the existing polygons.
                        mMap.clear();
                        //Add the existing polygons in the map
                        addTheExistingAreas(false);
                    }
                })
       .show();
    }


    /**
     * Polygon click listener
     */
    private GoogleMap.OnPolygonClickListener polygonClickListener = new GoogleMap.OnPolygonClickListener() {
        @Override
        public void onPolygonClick(Polygon polygon) {
                Toast.makeText(MapActivityV2.this," Polygon click event ",Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Map click listener
     */
    private GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            Log.d(TAG, "OnMapClickListener function running");
            // Detect if current click is inner in other area
            detectInnerArea = AreaUtilities
                    .detectInnerArea(latLng, placemarkList);
            if(currentOuterArea == null){
                // Get the name from current outer area only in the first time
                currentOuterArea = AreaUtilities.getOutsiderArea().getName();
            }
            // If bottom layout is enable
            if(bottomLayoutIsEnable) {
                // if current click is inside in other area
                if (detectInnerArea) {
                    // If the current marker has the same outer area as the previous marker
                    if (currentOuterArea.equals(AreaUtilities.getOutsiderArea().getName())) {
                        // Create MarkerOptions
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng).title("" + latLng.latitude + " " + latLng.longitude);
                        if (marker != null) {
                            // Remove the previous marker
                            marker.remove();
                        }
                        // Create Marker in the map
                        marker = mMap.addMarker(markerOptions);
                        // Add LatLng in latLngList
                        latLngList.add(latLng);
                        // Add Marker in markerList
                        markerList.add(marker);
                        if (markerList.size() > 1) {
                            latitude = latLng.latitude;
                            longitude = latLng.longitude;
                            marker.setPosition(new LatLng(latitude, longitude));
                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .addAll(latLngList).color(Color.RED);
                            mMap.addPolyline(polylineOptions);
                        }
                    } else {
                        // Show message
                        Toast.makeText(MapActivityV2.this,
                                "Please keep your marks in the same area or clean the map.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    // Show message
                    Toast.makeText(MapActivityV2.this,
                            "You can not create a mark outside from areas ",Toast.LENGTH_LONG).show();
                }
            }else{
                // Show message
                Toast.makeText(MapActivityV2.this,
                        "If you want to create area click create area option in the menu on top right first",Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * This method put the existing monitoring areas in the map
     * loads the existing areas data from the shared preferences storage.
     *
     * @param isClickable declares the property clickable for each polygon
     */
    private void addTheExistingAreas(boolean isClickable) {
        mMap.clear();
        for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
            for (Placemark placemark : entry.getValue()) {
                // Create PolygonOptions object for each placemark
                PolygonOptions polygonOptions = new PolygonOptions()
                        .strokeWidth(5f).addAll(placemark.getLatLngList()).strokeColor(Color.RED)
                        .fillColor(Color.argb(70, 50, 255, 0)).clickable(isClickable);
                // Add polygon in the map
                Polygon polygon = mMap.addPolygon(polygonOptions);
                // Set in the tag of polygon the name of placemark
                polygon.setTag(placemark.getName());
                // Fill the placemarkList
                placemarkList.add(placemark);
            }
        }
    }

    /**
     * InsertFileEventListener implementation
     *
     * Puts a new kml files in the kmlFileMap
     * and save the changes on shared preferences storage.
     *
     * @param center have the central location of the area from the file which added from the user.
     * @param kmlFile is a new kml file selected by the user from the file explorer.
     * @param placemarks takes the each placemark from the new kml file
     */
    @Override
    public void inertFileEvent(LatLng center, KmlFile kmlFile, List<Placemark> placemarks) {
        // Add a new record in the kmlFileMap
        kmlFileMap.put(kmlFile, placemarks);
        // Save changes on shared preferences storage
        kmlLocalStorageProvider.saveKmlFileMap(kmlFileMap);
        // Add areas in the map
        addTheExistingAreas(true);
        // Move the camera in center location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13f));
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume executed");
//        // Receive messages about current location.
//        // We are registering an observer (locationReceiver) to receive Intents with actions named "LocationUpdates".
//        registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_NAME));
//        // Receive messages about GPS status.
//        // We are registering an observer (GpsStatusReceiver) to receive intents with action name "android.location.PROVIDERS_CHANGED".
//        registerReceiver(GpsStatusReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
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
//        unregisterReceiver(locationReceiver);
//        unregisterReceiver(GpsStatusReceiver);
        unregisterReceiver(networkUtil);
    }
}
