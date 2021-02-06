package com.example.agroproject.ui;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.agroproject.databinding.ActivityMapBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Class TAG
    private final String TAG = "MapActivity";
    // Google Map
    private GoogleMap mMap;
    // Binding
    private ActivityMapBinding binding;
    // Device coordinates
    private double latitude;
    private double longitude;
    private LatLng currentLocation;

    // PolygonModel
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

        // Create LatLng
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
        // Add the existing polygons in the map
        addPolygonsInTheMap();
    }

    public GoogleMap.OnPolygonClickListener polygonClickListener = new GoogleMap.OnPolygonClickListener() {
        @Override
        public void onPolygonClick(Polygon polygon) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .strokeWidth(polygon.getStrokeWidth()).addAll(polygon.getPoints()).strokeColor(polygon.getStrokeColor())
                    .fillColor(polygon.getFillColor()).clickable(true);

            for(int i =0 ; i < monitoringAreaManager.loadMonitoringArea().size(); i++){
                if(polygonOptions.getStrokeColor() == monitoringAreaManager.loadMonitoringArea().get(i).getPolygonOptions().getStrokeColor()){
                    Toast.makeText(MapActivity.this,"eimai edwww mesa", Toast.LENGTH_LONG).show();
                }
            }





//                for(MonitoringArea monitoringArea : monitoringAreaManage.){
//
//
//
//                    if(monitoringArea.getPolygonOptions().getPoints().equals(polygonOptions.getPoints())){
//                        Toast.makeText(MapActivity.this,"eimai edwww mesa", Toast.LENGTH_LONG).show();
//                    }
//                }

//            if(polygonOptions.equals(monitoringArea.getPolygonOptions())){
//
//                Toast.makeText(MapActivity.this,"eimai edwww", Toast.LENGTH_LONG).show();
//
//                Log.d(TAG," Area name is "+monitoringArea.getName());
//            }

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
     * TODO METHOD DESCRIPTION
     */
    private void addPolygonsInTheMap(){
        if(!monitoringAreaManager.loadMonitoringArea().isEmpty()){
            for(MonitoringArea monitoringArea : monitoringAreaManager.loadMonitoringArea()){
                mMap.addPolygon(monitoringArea.getPolygonOptions());
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
        addPolygonsInTheMap();
    }
}