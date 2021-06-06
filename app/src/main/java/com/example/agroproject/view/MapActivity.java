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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityMapBinding;
import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.agro_api.HttpRequest;
import com.example.agroproject.model.agro_api.JsonParser;
import com.example.agroproject.model.agro_api.StringBuildForRequest;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.services.NetworkUtil;
import com.example.agroproject.view.fragments.AreaClickFragment;
import com.example.agroproject.view.fragments.InsertFileFragment;
import com.example.agroproject.view.fragments.SaveAreaFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        InsertFileFragment.InsertFileEventListener, SaveAreaFragment.CreateAreaEventListener, AreaClickFragment.AreaPopUpEventListener {

    /** Class TAG */
    private final String TAG = "MapActivity";

    /** GoogleMap object */
    private GoogleMap mMap;

    /** Marker */
    private Marker marker;

    /** PolygonOptions */
    private PolygonOptions polygonOptions;

    /** Activity view binding */
    private ActivityMapBinding binding;

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

    /** Auxiliary variable for the inner area detection */
    private String currentOuterArea = null;

    /** Auxiliary variable for the inner area detection */
    private boolean detectInnerArea = false;

    /** This variable declares the visibility state of bottom layout */
    private boolean bottomLayoutIsEnable = false;

    /** JSON data from GET request in agro api */
    private JSONArray jsonArray;

    /** List with GroundOverlay objects, takes the ndvi ground overlays after ndvi request*/
    private Map<Placemark, GroundOverlayOptions> groundOverlaysList = new HashMap<>();

    /** button for Boom Menu **/
    private BoomMenuButton boomButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Hide Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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
        // We are registering an observer (responseReceiver) with action name GetRequestData to receive Intents after http Get request in Agro api.
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, new IntentFilter("GetRequestData"));
        // Get request on endpoint polygons of Agro api
        HttpRequest.getRequest(this, StringBuildForRequest.polygonsRequestLink(), "Get all polygons");
        // Set bottom menu visibility false
        binding.linearLayout.setVisibility(View.GONE);
        // Set click listener for buttons
        binding.drawPolygon.setOnClickListener(buttonClickListener);
        binding.clearMap.setOnClickListener(buttonClickListener);
        binding.closeLayout.setOnClickListener(buttonClickListener);
        //Initialize button for Boom Menu
        boomButton = binding.bmb;
        CreateBoomMenu();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapV2);
        mapFragment.getMapAsync(this);
    }

    /**
     * TODO DESCRIPTION
     */
    private void CreateBoomMenu(){
      boomButton.setNormalColor(R.color.purple_500);
      boomButton.setDraggable(true);
            // TODO COMMENTS
            TextOutsideCircleButton.Builder builder = new TextOutsideCircleButton.Builder()
                    .normalImageRes(R.drawable.createnew)
                    .normalText("CREATE AREA").textSize(15).rotateText(true)
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            // Disable visibility for zoom controls buttons
                            mMap.getUiSettings().setZoomControlsEnabled(false);
                            // Set bottom menu visibility true
                            binding.linearLayout.setVisibility(View.VISIBLE);
                            // Set the property clickable false for each polygon
                            addTheExistingAreas(false);
                            //
                            currentOuterArea = null;
                            // bottom layout is enable
                            bottomLayoutIsEnable = true;
                    }
                });
        boomButton.addBuilder(builder);
        // TODO COMMENTS
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.insertfile)
                .normalText("INSERT FILE").textSize(15)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // Instantiate a InsertFileFragment object
                        InsertFileFragment insertFileFragment = new InsertFileFragment();
                        // Start fragment activity
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.insert_file_fragment_container, insertFileFragment, insertFileFragment.getClass()
                                        .getSimpleName()).addToBackStack(null).commit();
                        // Set bottom menu visibility true
                        binding.linearLayout.setVisibility(View.GONE);
                        // Disable map click
                        mMap.setOnMapClickListener(null);
                        // Disable zoom option on touch
                        mMap.getUiSettings().setZoomGesturesEnabled(false);
                        // Disable Polygon click listener
                        mMap.setOnPolygonClickListener(null);
                    }
                });
        boomButton.addBuilder(builder2);
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


    /**
     * Calling after closed a pop up fragment
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Enable map click
        mMap.setOnMapClickListener(mapClickListener);
        // Enable zoom option on touch
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        // Enable Polygon click listener
        mMap.setOnPolygonClickListener(polygonClickListener);
    }

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
                        // Clean the lists
                        markerList.clear();
                        latLngList.clear();
                    }else if(!markerList.isEmpty() && markerList.size() <= 3 ){
                        // Show message
                        Toast.makeText(MapActivity.this,
                                "You need three markers at least to draw an area",Toast.LENGTH_LONG).show();
                    }else{
                        // Show message
                        Toast.makeText(MapActivity.this,
                                "Tap in the map and mark your area first", Toast.LENGTH_LONG).show();
                    }
                break;

                case "clear":
                    currentOuterArea = null;
                    latLngList.clear();
                    markerList.clear();
                    mMap.clear();
                    currentOuterArea = null;
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
                    currentOuterArea = null;
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
        new AlertDialog.Builder(MapActivity.this)
                .setIcon(R.drawable.ic_baseline_save)
                .setTitle("Save")
                .setMessage("You want to save this area?")
                .setCancelable(false) // Set cancelable on touch outside
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Instantiate a SaveAreaFragment object
                        SaveAreaFragment saveAreaFragment = new SaveAreaFragment();
                        // Start fragment activity
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.save_area_fragment_container, saveAreaFragment, saveAreaFragment.getClass()
                                        .getSimpleName()).addToBackStack(null).commit();
                        // Close bottom layout
                        binding.linearLayout.setVisibility(View.GONE);
                        // bottom layout is disable
                        bottomLayoutIsEnable = false;
                        // Enable visibility for zoom controls buttons
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        //
                        addTheExistingAreas(false);
                        // Disable map click
                        mMap.setOnMapClickListener(null);
                        // Disable zoom option on touch
                        mMap.getUiSettings().setZoomGesturesEnabled(false);
                        // Disable Polygon click listener
                        mMap.setOnPolygonClickListener(null);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
            for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
                for (Placemark placemark : entry.getValue()) {
                    if (polygon.getTag().equals(placemark.getName())) {
                        // Get id for the clicked polygon
                        String polygonId = JsonParser.getId(placemark.getName(), jsonArray);
                        // Instantiate a AreaClickFragment object
                        AreaClickFragment areaClickFragment = new AreaClickFragment(placemark, polygonId);
                        // Start fragment activity
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.area_click_fragment_container, areaClickFragment, areaClickFragment.getClass()
                                        .getSimpleName()).addToBackStack(null).commit();
                        // Disable map click
                        mMap.setOnMapClickListener(null);
                        // Disable zoom option on touch
                        mMap.getUiSettings().setZoomGesturesEnabled(false);
                        // Disable Polygon click listener
                        mMap.setOnPolygonClickListener(null);
                        break;
                    }
                }
            }
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
            boolean areaExists =  detectInnerArea = AreaUtilities
                    .detectInnerArea(latLng, placemarkList);
            if(areaExists) {
                if (currentOuterArea == null) {
                    // Get the name from current outer area only in the first time
                    currentOuterArea = AreaUtilities.getOutsiderArea().getName();
                }
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
                        Toast.makeText(MapActivity.this,
                                "Please keep your marks in the same area or clean the map.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    // Show message
                    Toast.makeText(MapActivity.this,
                            "You can not create a mark outside from areas ",Toast.LENGTH_LONG).show();
                }
            }else{
                // Show message
                Toast.makeText(MapActivity.this,
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

        // Add overlays in the map
        for(Map.Entry<Placemark,GroundOverlayOptions> entry : groundOverlaysList.entrySet()){
            // Add overlay in the map
            mMap.addGroundOverlay(entry.getValue());
        }
    }

    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "GetRequestData".
     *  TODO MORE DESCRIPTION
     */
    private BroadcastReceiver responseReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get response data and request type that are included in the Intent
            String responseData = intent.getStringExtra("Response data");
            String requestType = intent.getStringExtra("Request type");

            if(requestType.equals("Get all polygons")){
                // Parse response data
                jsonArray = JsonParser.parseResponse(responseData);
            }
            Log.d(TAG,"receive response data here "+responseData);
        }
    };

    /**
     * InsertFileEventListener implementation
     *
     * Puts a new kml files in the kmlFileMap and save the changes on shared preferences storage.
     * Called after user click in the save button on insert file pop up.
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
        // Disable bottom layout
        binding.linearLayout.setVisibility(View.GONE);
    }


    /**
     * CreateAreaEventListener implementation
     *
     * Put a new value in the kmlFileMap and save the changes on shared preferences storage.
     * Called after user click in the save button on save area pop up.
     *
     * @param areaName takes the name of new area
     * @param areaDescription takes the description of new area
     * @param outsiderArea is the outsider area of new area
     */
    @Override
    public void createAreaEvent(String areaName, String areaDescription, Placemark outsiderArea) {
        for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
            if (entry.getValue().contains(outsiderArea)) {
                // add new value on this entry
                entry.getValue().add(new Placemark
                        (areaName, areaDescription, polygonOptions.getPoints()));
                // Save the kmlFileMap in shared preferences.
                kmlLocalStorageProvider.saveKmlFileMap(kmlFileMap);
            }
        }
        // Add areas in the map  set property clickable  true
        addTheExistingAreas(true);
    }

    /**
     *
     * @param placemark
     */
    @Override
    public void deleteAreaEvent(Placemark placemark) {
        for(Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()){
            if(entry.getValue().remove(placemark)){
                // If this entry don't have values
                if(entry.getValue().size() == 0){
                    // Remove all record
                    kmlFileMap.remove(entry.getKey());
                }
                // Save the changes
                kmlLocalStorageProvider.saveKmlFileMap(kmlFileMap);
                break;
            }
        }
        // Remove ground overlay from groundOverlaysList
        groundOverlaysList.remove(placemark);
        // Add areas in the map  set property clickable  true
        addTheExistingAreas(true);

    }

    /**
     *
     * @param placemark
     */
    @Override
    public void loadNdvi(Placemark placemark, BitmapDescriptor descriptor) {
        // Create LatLng bounds for the location of placemark
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : placemark.getLatLngList()) {
                 builder.include(latLng);
        }

        // Create GroundOverlayOptions for the ndv image
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .positionFromBounds(builder.build()).image(descriptor).zIndex(100);
        // Add overlay in the map
        mMap.addGroundOverlay(groundOverlayOptions);
        // Add GroundOverlayOptions in the groundOverlaysList
        groundOverlaysList.put(placemark, groundOverlayOptions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume executed");
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
        unregisterReceiver(networkUtil);
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
    }
}
