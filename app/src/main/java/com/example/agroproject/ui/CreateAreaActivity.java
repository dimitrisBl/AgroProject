package com.example.agroproject.ui;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.agroproject.R;
import com.example.agroproject.databinding.SaveAreaPopupStyleBinding;
import com.example.agroproject.model.MonitoringAreaManager;
import com.example.agroproject.databinding.ActivityCreateAreaBinding;
import com.example.agroproject.model.MonitoringArea;
import com.example.agroproject.model.MyLocation;
import com.example.agroproject.services.LocationService;
import com.example.agroproject.services.LocationTrackingService;
import com.example.agroproject.services.MyLocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO description for this class
 *
 */

public class CreateAreaActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    /** Class TAG */
    private final String TAG = "CreateAreaActivity";

    /** Activity view binding */
    private ActivityCreateAreaBinding binding;

    /** GoogleMap object */
    private GoogleMap mMap;

    /** Marker object */
    private Marker marker = null;

    /** The current geographic latitude of the device */
    private double latitude;

    /** The current geographic longitude of the device */
    private double longitude;

    /** LatLng object for the current location  */
    private LatLng currentLocation;

    /** PolygonOptions object */
    private PolygonOptions polygonOptions = null;

    /** Initialize List with Marker objects */
    private List<Marker> markerList = new ArrayList<>();

    /** Initialize List with LatLng objects */
    private List<LatLng> latLngList = new ArrayList<>();

    /** MonitoringAreaManager object */
    private MonitoringAreaManager monitoringAreaManager;

    private Polyline polyline = null;

//    /** Firebase database */
//    private DatabaseReference dbReference;
//
//    /** LocationManager object */
//    private LocationManager locationManager;

    /** checkBox state */
    private boolean checkBoxIsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        // User-101 is a demo user
//        dbReference = FirebaseDatabase.getInstance().getReference().child("user-101");
//
//        // Initialize a locationManager object
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        // Receive messages about current location.
        // We are registering an observer (locationTrackingReceiver) to receive Intents with actions named "LocationTrackingService".
        LocalBroadcastManager.getInstance(CreateAreaActivity.this).registerReceiver(
                locationTrackingReceiver, new IntentFilter("LocationTrackingService"));

        // Set click listener for buttons
        binding.drawPolygon.setOnClickListener(buttonClickListener);
        binding.clearMap.setOnClickListener(buttonClickListener);
        // Set checked listener for checkbox
        binding.checkBox.setOnCheckedChangeListener(checkBoxListener);

        // Get extras from intent
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude",0.0);

        // Create the LatLng object of the current location
        currentLocation = new LatLng(latitude, longitude);

        // Instantiate the PolygonModel object.
        monitoringAreaManager = new MonitoringAreaManager(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        readChanges();
    }



//    public void readChanges(){
//        dbReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.exists()){
//                        MyLocation location = dataSnapshot.getValue(MyLocation.class);
//                        try{
//                            if(location!=null){
//                                LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
//
//
//                                latLngList.add(currentLocation);
//
//                                if(polyline!=null){
//                                    polyline.setPoints(latLngList);
//                                }else{
//                                    polyline = mMap.addPolyline(new PolylineOptions()
//                                            .addAll(latLngList).color(Color.MAGENTA).jointType(JointType.ROUND).width(3.0f));
//                                }
//                            }
//                        }catch (Exception e){
//
//                        }
//                    }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


    public CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @SuppressLint("MissingPermission")
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
            // get the checkBox state
            checkBoxIsChecked = state;

            if (checkBoxIsChecked) {
                // Enable location button
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Start the location tracking
                //startLocationTrackingService();
                MyLocationService myLocation = new MyLocationService(getApplicationContext());

                myLocation.startLocationService();



                // Location button click event
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        // Move the camera in current location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18.5f));
                        return true;
                    }
                });
            }else{

                // Enable location button
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

//                // Unregister since the activity is about to be closed.
//                LocalBroadcastManager.getInstance(CreateAreaActivity.this).unregisterReceiver(locationTrackingReceiver);
            }

        }
    };

    /**
     *  This method starts an intent service
     *  in LocationTrackingService class.
     */


    @SuppressLint("MissingPermission")
    private void startLocationTrackingService() {
       //FirebaseDatabase.getInstance().getReference().setValue("my first message");
//            if(locationManager!=null){
//
//                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                            1000, 1000,this);
//                }else{
//                    Toast.makeText(this,
//                            "No provider enable", Toast.LENGTH_SHORT).show();
//                }
//
//            }

//        Intent locationTrackingService = new Intent(this, LocationTrackingService.class);
//        startService(locationTrackingService);
    }

    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "LocationTrackingService".
     *  TODO MORE DESCRIPTION
     */

    private BroadcastReceiver locationTrackingReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Receive message from " +
                    "LocationTrackingService class about the location tracking");
            // Get extra data included in the Intent
            latitude = intent.getDoubleExtra("latitude",0.0);
            longitude = intent.getDoubleExtra("longitude",0.0);

            LatLng currentLocation = new LatLng(latitude,longitude);


            latLngList.add(currentLocation);

           if(polyline!=null){
               polyline.setPoints(latLngList);
           }else{
               polyline = mMap.addPolyline(new PolylineOptions()
                       .addAll(latLngList).color(Color.MAGENTA).jointType(JointType.ROUND).width(3.0f));
           }
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Initialize map
        mMap = googleMap;
        // Setup satellite map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Set Map click listener method
        mMap.setOnMapClickListener(mapClickListener);
        // Move the camera in current location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18.5f));
        //Add the existing monitoring areas in the map
        addTheExistingAreasInMap();
    }

    /**
     * TODO description
     */
    public GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            Log.d(TAG, "OnMapClickListener function running");
            if (!checkBoxIsChecked) {
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
                            .addAll(latLngList).color(Color.RED).jointType(JointType.BEVEL);

                    mMap.addPolyline(polylineOptions);
                }
            }
        }
    };

    /**
     *  TODO description
     */
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Get current button
            Button currentButton = (Button) view;

            // Get text from current button
            String currentButtonText = String.valueOf(currentButton.getText());

            switch (currentButtonText){
                case "draw area":
                    if(!latLngList.isEmpty() && markerList.size() >= 4 ) {
                        // Create PolygonOptions
                        polygonOptions = new PolygonOptions()
                                .strokeWidth(5.2f).addAll(latLngList).strokeColor(Color.RED)
                                .fillColor(Color.rgb(204, 255, 204)).clickable(true);
                        // Draw area on the Map
                        mMap.addPolygon(polygonOptions);

                        // Remove markers from the map
                        for (Marker marker : markerList) {
                            marker.remove();
                        }
                        // Show dialog
                        showSaveAlertDialog();

                        markerList.clear();
                        latLngList.clear();

                    }else if(!markerList.isEmpty() && markerList.size() <= 3 ){
                        // Show message
                         Toast.makeText(CreateAreaActivity.this,
                                "You need four markers at least to draw an area",Toast.LENGTH_LONG).show();
                    }else{
                        // Show message
                        Toast.makeText(CreateAreaActivity.this,
                                "Tap in the map and mark your area first",Toast.LENGTH_LONG).show();
                    }
                break;
                case "clear":
                    latLngList.clear();
                    markerList.clear();
                    mMap.clear();
                    //prepei na bgei apo edw to afhnw gia na mn gemizw to arxeio malakies
                    //monitoringAreaManager.clearSharedPreferencesFile();
                    addTheExistingAreasInMap();
                break;
            }
        }
    };


    /**
     * TODO method description
     */

    @SuppressLint("NewApi")
    private void showSaveAlertDialog(){
        new AlertDialog.Builder(CreateAreaActivity.this)
                .setIcon(R.drawable.ic_baseline_save)
                .setTitle("Save")
                .setMessage("You want to save this area?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If answer is yes show popup alert form.
                        showSaveAreaPopup();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // If answer is no, clean the map and add the existing polygons.
                        mMap.clear();
                        //Add the existing polygons in the map
                        addTheExistingAreasInMap();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        // If dismiss Popup, clean the map and add the existing polygons.
                        mMap.clear();
                        //Add the existing polygons in the map
                        addTheExistingAreasInMap();
                    }
                })
        .show();
    }


    /**
     * TODO method description
     */
    private void showSaveAreaPopup(){
        // Binding
        SaveAreaPopupStyleBinding popupBinding;

        popupBinding = SaveAreaPopupStyleBinding.inflate(getLayoutInflater());
        View popupView = popupBinding.getRoot();

        ImageView imageViewClose;
        EditText areaName;
        EditText areaDescription;
        Button submitBtn;

        // Instantiate a Dialog
        Dialog popupDialog = new Dialog(this);
        popupDialog.setContentView(popupView);

        // Initialize ui components
        imageViewClose = popupBinding.btnCLose;
        areaName = popupBinding.areaName;
        areaDescription = popupBinding.areaDescription;
        submitBtn = popupBinding.btnSubmit;

        // Close Button ClickEvent
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close dialog
                popupDialog.dismiss();
            }
        });// Save Button ClickEvent
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Submit button pressed");
                String areaNameText = areaName.getText().toString();
                String areaDescriptionText = areaDescription.getText().toString();

                // Create the monitoring area.
                monitoringAreaManager.createMonitoringArea(
                        new MonitoringArea(areaNameText, areaDescriptionText, polygonOptions));

                // Save monitoring area in shared preferences.
                monitoringAreaManager.saveMonitoringArea();

                // Close dialog
                popupDialog.dismiss();
            }
        });// Dismiss Popup Event
        popupDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mMap.clear();
                //Add the existing polygons in the map
                addTheExistingAreasInMap();
            }
        });
        popupDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Enable back button in menu
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * TODO METHOD DESCRIPTION
     *
     */
    private void addTheExistingAreasInMap(){
        if(!monitoringAreaManager.loadMonitoringArea().isEmpty()){
            for(MonitoringArea monitoringArea : monitoringAreaManager.loadMonitoringArea()){
                mMap.addPolygon(monitoringArea.getPolygonOptions());
                // Get the center location of the area
                LatLng centerLatLng = monitoringArea
                        .getPolygonCenterPoint(monitoringArea.getPolygonOptions().getPoints());
                // Add Marker on map  in the center location of area
                mMap.addMarker(new MarkerOptions()
                        .position(centerLatLng).title(monitoringArea.getName()));
            }
        }
    }

//    @Override
//    protected void onRestart() {
//        Log.d(TAG,"onRestart method executed");
//        super.onRestart();
//        // Receive messages about current location.
//        // We are registering an observer (gpsLocationReceiver) to receive Intents with actions named "LocationTrackingService".
//        LocalBroadcastManager.getInstance(CreateAreaActivity.this).registerReceiver(
//                locationTrackingReceiver, new IntentFilter("LocationTrackingService"));
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG,"onPause method executed");
//        // Unregister since the activity is about to be closed.
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationTrackingReceiver);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG,"onDestroy method executed");
//        // Unregister since the activity is about to be closed.
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationTrackingReceiver);
//    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null){
            saveLocation(location);
        }else{
            Toast.makeText(CreateAreaActivity.this,
                    "no location",Toast.LENGTH_SHORT).show();
        }
    }


    public void saveLocation(Location location){
      //      dbReference.setValue(location);
    }
}