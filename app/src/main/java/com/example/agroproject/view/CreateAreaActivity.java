package com.example.agroproject.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityCreateAreaBinding;
import com.example.agroproject.databinding.SaveAreaPopupStyleBinding;
import com.example.agroproject.model.MonitoringArea;
import com.example.agroproject.model.MonitoringAreaManager;
import com.example.agroproject.model.SingletonFilePath;
import com.example.agroproject.services.LocationService;
import com.example.agroproject.services.NetworkUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;


/**
 * TODO description for this class
 *
 */
public class CreateAreaActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    /** checkBox state */
    private boolean checkBoxIsChecked = false;

    /** Polyline */
    private Polyline polyline = null;

    /** Networkutil */
    private NetworkUtil networkUtil;

    /** Singleton object to keep track of the filepath **/
    SingletonFilePath filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Instantiate a NetworkUtil object
        networkUtil = new NetworkUtil(this);
        // Get extras from intent
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude",0.0);

        // Create the LatLng object of the current location
        currentLocation = new LatLng(latitude, longitude);

        // Instantiate a MonitoringAreaManager object
        monitoringAreaManager = new MonitoringAreaManager(this);

        // Set click listener for buttons
        binding.drawPolygon.setOnClickListener(buttonClickListener);
        binding.clearMap.setOnClickListener(buttonClickListener);
        // Set checked listener for checkbox
        binding.checkBox.setOnCheckedChangeListener(checkBoxListener);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.create_area_map);
        mapFragment.getMapAsync(this);

        //Initialize Singleton Object passing the existing instance
        filepath = SingletonFilePath.getInstance();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This method Adds a marker in current location, sets zoom in the camera and defines the satellite map type.
     * TODO MORE COMMENTS
     */
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
             // If checkBox state is false
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
                            .addAll(latLngList).color(Color.RED);

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
            // Get current button click
            Button currentButton = (Button) view;

            // Get text from current button
            String currentButtonText =
                    String.valueOf(currentButton.getText());

            switch (currentButtonText){
                case "draw area":
                    if(!latLngList.isEmpty() && markerList.size() >= 4 ) {
                        // Create PolygonOptions
                        polygonOptions = new PolygonOptions()
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

                        markerList.clear();
                        latLngList.clear();

                    }else if(!markerList.isEmpty() && markerList.size() <= 3 ){
                        // Show message
                        Toast.makeText(CreateAreaActivity.this,
                                "You need four markers at least to draw an area",Toast.LENGTH_LONG).show();
                    }else{
                        // Show message
                        Toast.makeText(CreateAreaActivity.this,
                                "Tap in the map and mark your area first", Toast.LENGTH_LONG).show();
                    }
                break;
                case "clear":
                    latLngList.clear();
                    markerList.clear();
                    mMap.clear();
                    polyline = null;
                    addTheExistingAreasInMap();
                break;
            }
        }
    };

    /**
     *  TODO description
     */
    public CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @SuppressLint("MissingPermission")
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
            // get the checkBox state
            checkBoxIsChecked = state;

            if (checkBoxIsChecked) {
                marker = null;
                markerList.clear();
                latLngList.clear();

            }else{
                polyline = null;
                latLngList.clear();
                markerList.clear();
                mMap.clear();
                //Add the existing monitoring areas in the map
                addTheExistingAreasInMap();
            }
        }
    };


    /**
     * TODO METHOD DESCRIPTION
     * LOGIKA THA UPARXEI KAPOU MIA LEPTOMERIA
     * POU THA DIMOURGEI PROBLHMA - BUG, NA KANW DEBUG GIA NA ENTOPISW PITHANA SFALMATA
     *
     **/
    public void refreshMapForNewLocation(){

        if(checkBoxIsChecked){

            // Add LatLng in latLngList
            latLngList.add(currentLocation);

            if(polyline!=null){

                polyline.setPoints(latLngList);

            }else{
                // Create MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(currentLocation).title("Your location: " +latitude + " " + longitude);

                if (marker != null) {
                    // Remove the previous marker
                    marker.remove();
                }
                // Create Marker in the map
                marker = mMap.addMarker(markerOptions);
                // Add Marker in markerList
                markerList.add(marker);

                if (markerList.size() > 1){

                    marker.setPosition(new LatLng(latitude, longitude));

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(latLngList).color(Color.RED);

                    mMap.addPolyline(polylineOptions);
                }
            }
            // If location tracking checkBox is checked move the camera in new location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f));
        }
    }

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
                        // If answer is yes show save area popup.
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
        createLayer();
    }

    private void createLayer() {

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume executed");
        // Receive messages about current location.
        // We are registering an observer (locationReceiver) to receive Intents with actions named "LocationUpdates".
        registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_NAME));
        // Receive messages about GPS status.
        // We are registering an observer (GpsStatusReceiver) to receive intents with action name "android.location.PROVIDERS_CHANGED".
        registerReceiver(GpsStatusReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
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
        unregisterReceiver(GpsStatusReceiver);
        unregisterReceiver(networkUtil);
    }

    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "LocationUpdates". Receives the current latitude
     *  and longitude of the device.
     */
    private BroadcastReceiver locationReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(CreateAreaActivity.this,
                    "Receive coordinates in activity create area: "
                            + intent.getDoubleExtra("latitude", 0.0),
                    Toast.LENGTH_SHORT).show();

            // Receive extra data included in the Intent
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);

            // Instantiate a new LatLng for current location
            currentLocation = new LatLng(latitude, longitude);

            // Refresh ui for the location changes
            refreshMapForNewLocation();
        }
    };

    /**
     * Receives the GPS status change from enabled to disabled.
     */
    public BroadcastReceiver GpsStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.location.PROVIDERS_CHANGED")){
                finish();
            }
        }
    };
}