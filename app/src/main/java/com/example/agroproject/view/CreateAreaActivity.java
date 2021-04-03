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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityCreateAreaBinding;
import com.example.agroproject.databinding.SaveAreaPopupStyleBinding;
import com.example.agroproject.databinding.SaveFilePopupStyleBinding;
import com.example.agroproject.model.file.Placemark;
import com.example.agroproject.model.file.KmlFileParser;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.model.MonitoringAreaManager;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TODO description for this class
 *
 */
public class CreateAreaActivity extends AppCompatActivity implements OnMapReadyCallback {

    /** Class TAG */
    private final String TAG = "CreateAreaActivity";


    /** Intent code for file selection */
    private final int FILE_SELECTION_CODE = 4;

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

    /** KmlLocalStorageProvider */
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    /** Insert file PopUp View binding */
    private SaveFilePopupStyleBinding showSaveFilePopUpBinding;


    private List<Placemark> myKmlLayerList = new ArrayList<>();

    private Map<String, List<Placemark>> kmlFileMap = new HashMap<>();

    private String fileName;
    private KmlFileParser kmlFileParser;
    private String dataFromFile;
    private Uri uri;

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

        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        // Load the Map from shared preferences
        kmlFileMap = kmlLocalStorageProvider.loadLayers();
        // Instantiate a KmlFileParse object
        kmlFileParser = new KmlFileParser(this);

        // Set click listener for buttons
        binding.drawPolygon.setOnClickListener(buttonClickListener);
        binding.clearMap.setOnClickListener(buttonClickListener);
        binding.insertFile.setOnClickListener(buttonClickListener);
        // Set checked listener for checkbox
        //binding.checkBox.setOnCheckedChangeListener(checkBoxListener);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.create_area_map);
        mapFragment.getMapAsync(this);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 5f));
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

            Toast.makeText(CreateAreaActivity.this,"name "+currentButton.getText(),Toast.LENGTH_SHORT).show();
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

                case "insert file":
                    showSaveFilePopUp();
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
            //Coordinates
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
                .setCancelable(false) // Set cancelable on touch outside
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
                })
        .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_SELECTION_CODE){
            if(resultCode == RESULT_OK){
                // Create a Uri path
                uri = Uri.parse(data.getDataString());
                // Get the name of the file
                fileName = uri.getPath();
                int cut = fileName.lastIndexOf('/');
                if(cut != -1){
                    fileName = fileName.substring(cut + 1);
                }
                // Set text
                showSaveFilePopUpBinding.filePath.setText(fileName);
            }
        }
    }



    /**
     * AUTH KALEITAI OTAN PATAW TO KOUMPI INSERT FILE
     */
    private void showSaveFilePopUp(){
        showSaveFilePopUpBinding = SaveFilePopupStyleBinding.inflate(getLayoutInflater());
        View popUpView = showSaveFilePopUpBinding.getRoot();

        // Instantiate a Dialog
        Dialog popupDialog = new Dialog(this);
        popupDialog.setContentView(popUpView);
        popupDialog.setCanceledOnTouchOutside(false);
            // Close btn
            ImageView closeBtn = showSaveFilePopUpBinding.btnCLose;
            closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                //Add the existing polygons in the map
                addTheExistingAreasInMap();
                // Close dialog
                popupDialog.dismiss();
            }
        });
            // TextView
            TextView name = showSaveFilePopUpBinding.areaName;
            TextView description = showSaveFilePopUpBinding.areaDescription;
            // Choose file btn
            Button chooseFileButton = showSaveFilePopUpBinding.chooseFileBtn;
            chooseFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("*/*");
                    //intent.setType("text/xml");
                    startActivityForResult(intent, FILE_SELECTION_CODE);
                }
            });

            // Save btn
            Button saveBtn = showSaveFilePopUpBinding.saveFileBtn;
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Open file and get the data in String type
                    dataFromFile = kmlFileParser.parseFile(uri);
                    // Parse data from the file and create a List with Placemark objects
                    List<Placemark> placemarks = kmlFileParser.parseDataFromFile(dataFromFile);
                    // Put data into Map
                    kmlFileMap.put(uri.getPath(), placemarks);
                    //Add the existing polygons in the map
                    addTheExistingAreasInMap();
                    // Close dialog
                    popupDialog.dismiss();
                }
            });
        // Show dialog
        popupDialog.show();
    }



    /**
     * TODO method description
     * AUTH KALEITAI OTAN KANEIS POLYGONO PANW APO TON XARTI
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
        popupDialog.setCanceledOnTouchOutside(false);

        // Initialize ui components
        imageViewClose = popupBinding.btnCLose;
        areaName = popupBinding.areaName;
        areaDescription = popupBinding.areaDescription;
        submitBtn = popupBinding.btnSubmit;

        // Close Button ClickEvent
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                //Add the existing polygons in the map
                addTheExistingAreasInMap();
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

//                // Create the monitoring area.
//                monitoringAreaManager.createMonitoringArea(
//                        new MonitoringArea(areaNameText, areaDescriptionText, polygonOptions));
                // Save monitoring area in shared preferences.
                monitoringAreaManager.saveMonitoringArea();
                //Add the existing polygons in the map
                addTheExistingAreasInMap();
                // Close dialog
                popupDialog.dismiss();
            }
        });
        // Show dialog
        popupDialog.show();
    }


    /**
     * TODO METHOD DESCRIPTION
     *
     */
    private void addTheExistingAreasInMap() {
        for(Map.Entry<String, List<Placemark>> entry : kmlFileMap.entrySet()){
            for(Placemark placemark : entry.getValue()){
                polygonOptions = new PolygonOptions()
                    .strokeWidth(5f).addAll(placemark.getLatLngList()).strokeColor(Color.RED)
                    .fillColor(Color.argb(70, 50, 255, 0));
                mMap.addPolygon(polygonOptions);
            }
        }
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
        // Save the kmlFileMap in shared preferences.
        kmlLocalStorageProvider.saveLayers(kmlFileMap);
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