package com.example.agroproject.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityMapBinding;
import com.example.agroproject.databinding.AreaClickPopupStyleBinding;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.model.file.Placemark;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.HashMap;
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

    private KmlLocalStorageProvider kmlLocalStorageProvider;

    private Map<String, List<Placemark>> kmlFileMap = new HashMap<>();

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

        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        kmlFileMap = kmlLocalStorageProvider.loadLayers();

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
            for (Map.Entry<String, List<Placemark>> entry : kmlLocalStorageProvider.loadLayers().entrySet()) {
                for (Placemark placemark : entry.getValue()) {
                   if (polygon.getTag().equals(placemark.getName())) {
                        showAreaPopUp(placemark.getName());
                    }
                }
            }
            }
        };

    @SuppressLint("NewApi")
    private void showAreaPopUp(String areaName) {
        // Binding
        AreaClickPopupStyleBinding popupBinding;
        // Initialize popup view
        popupBinding = AreaClickPopupStyleBinding.inflate(getLayoutInflater());
        // Get view
        View popupView = popupBinding.getRoot();

        // Instantiate a Dialog
        Dialog popUpDialog = new Dialog(this);
        popUpDialog.setContentView(popupView);
        popUpDialog.setCancelable(true);
            // title
            TextView farmAreaName = popupBinding.farmName;
            farmAreaName.setText(areaName);
            // description
            //TextView farmAreaDescription = popupBinding.areaDescription;
            // farmAreaDescription.setText(areaDescription);
            // close image
            ImageView btnClose = popupBinding.btnCLose;
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popUpDialog.dismiss();
                }
            });
            // delete button
            Button deleteButton = popupBinding.deleteBtn;
            deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MapActivity.this)
                       .setIcon(R.drawable.ic_baseline_delete_24)
                       .setTitle("Delete")
                       .setMessage("You want to delete this area?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Placemark tempPlaceMark=null;
                               for (List<Placemark> entry : kmlFileMap.values()) {
                                   for (Placemark placemark : entry) {
                                        if(placemark.getName().equals(areaName)){
                                            tempPlaceMark = placemark;
                                        }
                                   }
                                   entry.remove(tempPlaceMark);
                               }


                               kmlLocalStorageProvider.saveLayers(kmlFileMap);
                               addTheExistingAreas();
                               popUpDialog.dismiss();
                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                               // do nothing
                               popUpDialog.dismiss();
                          }
                       })
                       .setOnDismissListener(new DialogInterface.OnDismissListener() {
                           @Override
                           public void onDismiss(DialogInterface dialogInterface) {
                                mMap.clear();
                                //Add the existing polygons in the map
                                addTheExistingAreas();
                           }
                       })
                .show();
                }
            });
        // Show popUp
        popUpDialog.show();
        }

    /**
    * This method put the existing monitoring areas in the map
    * loads the existing areas data from the shared preferences file.
    */
    private void addTheExistingAreas() {
        mMap.clear();
        for (Map.Entry<String, List<Placemark>> entry : kmlFileMap.entrySet()) {
            for (Placemark placemark : entry.getValue()) {
                PolygonOptions polygonOptions = new PolygonOptions()
                       .strokeWidth(5f).addAll(placemark.getLatLngList()).strokeColor(Color.RED)
                       .fillColor(Color.argb(70, 50, 255, 0)).clickable(true);
                polygon = mMap.addPolygon(polygonOptions);
                polygon.setTag(placemark.getName());
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
