package com.example.agroproject.ui;



import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;
import com.example.agroproject.R;
import com.example.agroproject.model.MonitoringAreaManager;
import com.example.agroproject.model.MonitoringArea;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.agroproject.databinding.ActivityMapBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    /** Class TAG */
    private final String TAG = "MapActivity";

    /** GoogleMap object */
    private GoogleMap mMap;

    /** Activity view binding */
    private ActivityMapBinding binding;

    /** The current geographic latitude of the device */
    private double latitude;

    /** The current geographic longitude of the device */
    private double longitude;

    /** LatLng object for the current location  */
    private LatLng currentLocation;

    /** Polygon object */
    private Polygon polygon;

    /** MonitoringAreaManager object */
    private MonitoringAreaManager monitoringAreaManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Instantiate the monitoringAreas object.
        monitoringAreaManager = new MonitoringAreaManager(this);

        // Get extras from intent
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude",0.0);

        // Create the LatLng object of the current location
        currentLocation = new LatLng(latitude, longitude);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This method Adds a marker in current location, sets zoom in the camera and defines the satellite map type.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Enable visibility for zoom controls buttons
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // Initialize map
        mMap = googleMap;
        // Setup satellite map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Create MarkerOption for current location
        MarkerOptions markerOptions = new MarkerOptions()
                    .position(currentLocation).title("Your location: "+latitude+"  "+longitude);
        // Add Marker in the map
        mMap.addMarker(markerOptions);
        // Move the camera in current location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f));
        // Polygon click listener
        mMap.setOnPolygonClickListener(polygonClickListener);
        // Put the existing monitoring areas in the map
        addTheExistingAreas();
    }


    public GoogleMap.OnPolygonClickListener polygonClickListener = new GoogleMap.OnPolygonClickListener() {
        @Override
        public void onPolygonClick(Polygon polygon) {

            Map<String, MonitoringArea> areaMap = new HashMap<>();

            for(MonitoringArea monitoringArea : monitoringAreaManager.loadMonitoringArea()){
                areaMap.put(monitoringArea.getName(), monitoringArea);
            }

            for(Map.Entry<String, MonitoringArea> kapa : areaMap.entrySet()){
                if(polygon.getTag().equals(kapa.getKey())){
                    Toast.makeText(MapActivity.this, "name: "+kapa.getKey()+
                            " description: "+kapa.getValue().getDescription()+
                            " area compute: "+ SphericalUtil.computeArea(kapa.getValue().getPolygonOptions().getPoints()), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Initiating Menu XML file (activity_map_menu.xml)
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_map_menu, menu);
        // Enable back button in menu
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return true;
    }

    /**
     * This method put the existing monitoring areas in the map
     * loads the existing areas data from the shared preferences file.
     */
    private void addTheExistingAreas(){
        if(!monitoringAreaManager.loadMonitoringArea().isEmpty()){
            for(MonitoringArea monitoringArea : monitoringAreaManager.loadMonitoringArea()){
                // Put the area in the map
                polygon = mMap.addPolygon(monitoringArea.getPolygonOptions());
                // Set tag in the polygon
                polygon.setTag(monitoringArea.getName());
                // Get the center location of the area
                LatLng centerLatLng = monitoringArea
                        .getPolygonCenterPoint(monitoringArea.getPolygonOptions().getPoints());
                // Instantiate a IconGenerator object
                IconGenerator iconFactory = new IconGenerator(this);
                // Add Marker on map  in the center location of area
                mMap.addMarker(new MarkerOptions().position(centerLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(monitoringArea.getName())))
                        .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
            }
        }
    }

    /**
     * TODO METHOD DESCRIPTION
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart method executed");
        // Add the existing polygons in the map
        addTheExistingAreas();
    }
}