package com.example.agroproject.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityMapBinding;
import com.example.agroproject.model.FarmArea;
import com.example.agroproject.model.FarmAreaLocalStorage;
import com.example.agroproject.model.FarmComposer;
import com.example.agroproject.model.InnerFarmArea;
import com.example.agroproject.model.InnerFarmAreaLocalStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;

import java.util.List;
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

    /** FarmAreaLocalStorage object */
    private FarmAreaLocalStorage farmAreaLocalStorage;

    /** InnerFarmAreaLocalStorage object */
    private InnerFarmAreaLocalStorage innerFarmAreaLocalStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get extras from intent
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude",0.0);

        // Create the LatLng object of the current location
        currentLocation = new LatLng(latitude, longitude);

        // Instantiate the farmAreaLocalStorage object.
        farmAreaLocalStorage = new FarmAreaLocalStorage(this);

        // Instantiate the innerFarmAreaLocalStorage object.
        innerFarmAreaLocalStorage = new InnerFarmAreaLocalStorage(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This method Adds a marker in current location, sets zoom in the camera and defines the satellite map type.
     * TODO MORE COMMENTS
     */
    @SuppressLint("MissingPermission")
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
        // Polygon click listener
        mMap.setOnPolygonClickListener(polygonClickListener);
        // Put the existing monitoring areas in the map
        addTheExistingAreas();
    }

    /**
     * TODO DESCRIPTION
     */
    public GoogleMap.OnPolygonClickListener polygonClickListener = new GoogleMap.OnPolygonClickListener() {
        @Override
        public void onPolygonClick(Polygon polygon) {


            FarmComposer.fillMap(farmAreaLocalStorage.loadFarmArea(), innerFarmAreaLocalStorage.loadInnerFarmArea());

            Map<FarmArea, List<InnerFarmArea>> farmMap = FarmComposer.getFarmMap();

            for(Map.Entry<FarmArea, List<InnerFarmArea>> entry : farmMap.entrySet()){
                for(int i = 0; i < entry.getValue().size(); i++){
                    if(polygon.getTag().equals(entry.getValue().get(i).getName())){

                        Toast.makeText(MapActivity.this, "inner area: "+entry.getValue().get(i).getName()
                                +" in the farm "+entry.getValue().get(i).getFarmArea().getName(),Toast.LENGTH_SHORT).show();

                    }else if (polygon.getTag().equals(entry.getKey().getName())){

                        Toast.makeText(MapActivity.this, "farm area: "+entry.getKey().getName()
                                +" with inner areas"+entry.getValue().get(i).getName(),Toast.LENGTH_SHORT).show();
                    }
                }
                for(InnerFarmArea element : entry.getValue()){



                }
            }
        }
    };

    /**
     * This method put the existing monitoring areas in the map
     * loads the existing areas data from the shared preferences file.
     */
    private void addTheExistingAreas(){

        if(!farmAreaLocalStorage.loadFarmArea().isEmpty()){
            for(FarmArea area : farmAreaLocalStorage.loadFarmArea()){
                polygon = mMap.addPolygon(area.getPolygonOptions().clickable(true));
                polygon.setTag(area.getName());
            }
        }

        if(!innerFarmAreaLocalStorage.loadInnerFarmArea().isEmpty()){
            for(InnerFarmArea innerFarmArea : innerFarmAreaLocalStorage.loadInnerFarmArea()){
                polygon = mMap.addPolygon(innerFarmArea.getPolygonOptions().clickable(true));
                polygon.setTag(innerFarmArea.getName());
            }
        }

    }

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
}