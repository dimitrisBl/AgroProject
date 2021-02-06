package com.example.agroproject.ui;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.agroproject.R;
import com.example.agroproject.databinding.SaveAreaPopupStyleBinding;
import com.example.agroproject.model.MonitoringAreaManager;
import com.example.agroproject.databinding.ActivityCreateAreaBinding;
import com.example.agroproject.model.MonitoringArea;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO description for this class
 * TODO logic for save areas
 */


public class CreateAreaActivity extends FragmentActivity implements OnMapReadyCallback {

    // Class TAG
    private final String TAG = "CreateAreaActivity";

    // ActivityCreateAreaBinding Binding
    private ActivityCreateAreaBinding binding;

    // Google Map
    private GoogleMap mMap;

    // Marker
    private Marker marker = null;

    // Device coordinates
    private double latitude;
    private double longitude;

    //----------- For Polygon - Polyline --------//
    private PolygonOptions polygonOptions = null;
    
    // Initialize Marker arrayList
    private List<Marker> markerList = new ArrayList<>();

    // Initialize LatLng arrayList
    private List<LatLng> latLngList = new ArrayList<>();

    // MonitoringAreas
    private MonitoringAreaManager monitoringAreaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set click listener for buttons
        binding.drawPolygon.setOnClickListener(buttonClickListener);
        binding.clearMap.setOnClickListener(buttonClickListener);

        Intent intent = getIntent();
        // Get extras from intent
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude",0.0);

        // Instantiate the PolygonModel object.
        monitoringAreaManager = new MonitoringAreaManager(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Initialize map
        mMap = googleMap;

        // Setup satellite map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Set Map click listener method
        mMap.setOnMapClickListener(mapClickListener);

        // Move the camera in current location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(latitude,  longitude), 18.5f));

        //Add the existing polygons in the map
        addPolygonsInTheMap();
    }

    /**
     * TODO description
     */
    public GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            Log.d(TAG,"OnMapClickListener function running");

            // Create MarkerOptions
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng).title(""+latLng.latitude+" "+latLng.longitude);

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

            if(markerList.size() > 1){

                latitude = latLng.latitude;
                longitude =latLng.longitude;

                marker.setPosition(new LatLng(latitude,longitude));

                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(latLngList).color(Color.RED).jointType(JointType.BEVEL);

                mMap.addPolyline(polylineOptions);
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
                    if(!latLngList.isEmpty() && markerList.size() >= 3) {
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

                    }else if(!markerList.isEmpty() && markerList.size() <= 2){
                        // Show message
                         Toast.makeText(CreateAreaActivity.this,
                                "You need three markers at least to draw an area",Toast.LENGTH_LONG).show();
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
                    monitoringAreaManager.clearSharedPreferencesFile();
                break;
            }
        }
    };

    /**
     * TODO method description
     */
    private void showSaveAlertDialog(){
        new AlertDialog.Builder(CreateAreaActivity.this)
                .setIcon(R.drawable.ic_baseline_save)
                .setTitle("Save")
                .setMessage("You want to save this area?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If answer is show popup alert form.
                         showSavePopupAlert();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // If answer is no, clean the map and add the existing polygons.
                        mMap.clear();
                        //Add the existing polygons in the map
                        addPolygonsInTheMap();
                    }
                })
        .show();
    }


    /**
     * TODO method description
     */
    private void showSavePopupAlert(){
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
                popupDialog.dismiss();
            }
        });
        // Submit Button ClickEvent
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Submit button pressed");
                String areaNameText = areaName.getText().toString();
                String areaDescriptionText = areaDescription.getText().toString();


                // Create the monitoring area.
                monitoringAreaManager.createMonitoringArea(
                        new MonitoringArea(areaNameText, areaDescriptionText, polygonOptions));

                // Close dialog
                popupDialog.dismiss();
            }
        });
        popupDialog.show();
    }

    /**
     * TODO METHOD DESCRIPTION
     *
     */
    private void addPolygonsInTheMap(){
        if(!monitoringAreaManager.loadMonitoringArea().isEmpty()){
            for(MonitoringArea monitoringArea : monitoringAreaManager.loadMonitoringArea()){
                mMap.addPolygon(monitoringArea.getPolygonOptions());
            }
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause method executed");
        super.onPause();
        // Save monitoring area in shared preferences.
        monitoringAreaManager.saveMonitoringArea();
    }
}