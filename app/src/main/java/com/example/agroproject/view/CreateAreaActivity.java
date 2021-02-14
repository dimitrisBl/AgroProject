package com.example.agroproject.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityCreateAreaBinding;
import com.example.agroproject.model.MonitoringArea;
import com.example.agroproject.model.MonitoringAreaManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
}