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
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityCreateAreaBinding;
import com.example.agroproject.model.agro_api.HttpRequest;
import com.example.agroproject.model.agro_api.JsonBuilder;
import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.file.KmlFileParser;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    /** checkBox state */
    private boolean checkBoxIsChecked = false;

    /** Polyline */
    private Polyline polyline = null;

    /** Networkutil */
    private NetworkUtil networkUtil;

    /** KmlLocalStorageProvider */
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    /** KmlFileParser */
    private KmlFileParser kmlFileParser;

    /** This Map has a List with placemarks objects, the key is the kml file name for each List */
    private Map<KmlFile, List<Placemark>> kmlFileMap = new HashMap<>();

    /** List with Placemark objects */
    private List<Placemark> placemarkList = new ArrayList<>();

    /** Auxiliary variable for the inner area detection */
    private boolean detectInnerArea = false;

    /** Auxiliary variable for the inner area detection */
    private String currentOuterArea = null;

    /** View binding for the insert file pop up */
   // private InsertFilePopupStyleBinding insertFilePopupBinding;

    /** Uri */
    private Uri uri;

    private String fileName;

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
        // Load kmlFileMap from shared preferences storage
        kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
        // Set click listener for buttons
        binding.drawPolygon.setOnClickListener(buttonClickListener);
        binding.clearMap.setOnClickListener(buttonClickListener);
        // Set checked listener for checkbox
        binding.checkBox.setOnCheckedChangeListener(checkBoxListener);
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
            // Detect if current click is inner in other area
            detectInnerArea = AreaUtilities
                    .detectInnerArea(latLng, placemarkList);

            if(currentOuterArea == null){
                // Get the name from current outer area only in the first time
                currentOuterArea = AreaUtilities.getOutsiderArea().getName();
            }
             // If checkBox state is false
            if (!checkBoxIsChecked) {
                // if current click is inside in other area
                if(detectInnerArea){
                    // If the current marker has the same outer area as the previous marker
                    if(currentOuterArea.equals(AreaUtilities.getOutsiderArea().getName())){
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
                    }else{
                        // Show message
                        Toast.makeText(CreateAreaActivity.this,
                                "Please keep your marks in the same area or clean the map.",Toast.LENGTH_LONG).show();
                    }
                }else{
                    // Show message
                    Toast.makeText(CreateAreaActivity.this,
                            "You can not create a mark outside from areas ",Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CreateAreaActivity.this,
                                "You need four markers at least to draw an area",Toast.LENGTH_LONG).show();
                    }else{
                        // Show message
                        Toast.makeText(CreateAreaActivity.this,
                                "Tap in the map and mark your area first", Toast.LENGTH_LONG).show();
                    }
                break;

                case "clear":
                    currentOuterArea = null;
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
                        //showSaveAreaPopup();
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
                // Set file name in TextView of inertFilePopUp
                //EditText name = insertFilePopupBinding.fileName;
                //name.setText(fileName);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Initiating Menu XML file (activity_map_menu.xml)
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_create_area_menu, menu);
        // Enable back button in menu
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set title alignment for each item is center
        int positionOfMenuItem0 = 0; //or any other postion
        MenuItem item = menu.getItem(positionOfMenuItem0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return true;
    }

    /** TODO EDW EIMAIeeeeee */
//    /**
//     * Event Handling for Individual menu item selected
//     * Identify single menu item by it's id
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.insert_file:
//                // Initialize a view for InsertFilePopup
//                insertFilePopupBinding = InsertFilePopupStyleBinding.inflate(getLayoutInflater());
//                View popupView = insertFilePopupBinding.getRoot();
//
//                // Instantiate a Dialog
//                Dialog popupDialog = new Dialog(this);
//                popupDialog.setContentView(popupView);
//                popupDialog.setCanceledOnTouchOutside(false);
//                    // Farm name TextView
//                    AutoCompleteTextView farmName = insertFilePopupBinding.AutoCompleteTextView;
//                    // Initialize a new List
//                    List<String> dropDownData = new ArrayList<>();
//                    for(KmlFile kmlFile : kmlFileMap.keySet()){
//                        if(!dropDownData.contains(kmlFile.getFarmName())){
//                            dropDownData.add(kmlFile.getFarmName());
//                        }
//                    }
//                    // Set data in the dropDownAdapter
//                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
//                            android.R.layout.simple_list_item_1, dropDownData);
//                    // Set dara in farmName TextView
//                    farmName.setAdapter(spinnerAdapter);
//                    // Close image
//                    ImageView imageViewClose = insertFilePopupBinding.btnCLose;
//                    imageViewClose.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Close dialog
//                            popupDialog.dismiss();
//                        }
//                    });
//                    // Choose file button
//                    Button chooseFileBtn = insertFilePopupBinding.chooseFileBtn;
//                    chooseFileBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Open file explorer
//                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                            intent.setType("*/*");
//                            //intent.setType("text/xml");
//                            startActivityForResult(intent, FILE_SELECTION_CODE);
//                        }
//                    });
//                    // Submit button
//                    Button submit = insertFilePopupBinding.saveFileBtn;
//                    submit.setOnClickListener(new View.OnClickListener() {
//                        @RequiresApi(api = Build.VERSION_CODES.R)
//                        @Override
//                        public void onClick(View view) {
//                            // Get the text from farmName TextView in String type
//                            String farmNameText = farmName.getText().toString();
//                            if (!farmNameText.isEmpty() && fileName != null) {
//                                // Instantiate a KmlFileParse object
//                                kmlFileParser = new KmlFileParser(CreateAreaActivity.this);
//                                // Open file and get the data in String type
//                                String dataFromFile = kmlFileParser.getFileData(uri);
//                                // Parse data from the file and create a List with Placemark objects
//                                List<Placemark> placemarks = kmlFileParser.parseDataFromFile(dataFromFile);
//                                // Create a JSONObject for Agro Api request,
//                                // List jsonObjectList has the data in JSON type of the current file
//                                List<JSONObject> jsonObjectList = JsonBuilder.build(placemarks);
//                                // Post data in Agro Api TODO EINAI SXOLIO TO POST GIA NA MHN TREXEI SUNEXEIA
//                                //HttpRequest.postRequest(jsonObjectList);
//                                // Create a new KmlFile object
//                                KmlFile kmlFile = new KmlFile(fileName, uri.getPath(), dataFromFile, farmNameText);
//                                // Check if the kmlFile is exists
//                                if(fileExistsCheck(kmlFile) == false){
//                                    // Put data into Map
//                                    kmlFileMap.put(kmlFile, placemarks);
//                                    // Save the kmlFileMap in shared preferences.
//                                    kmlLocalStorageProvider.saveKmlFileMap(kmlFileMap);
//                                    // Show message
//                                    Toast.makeText(CreateAreaActivity.this,
//                                    "The file "+fileName+" was successfully added",Toast.LENGTH_LONG).show();
//                                    // Get the center area of first placemark witch contained this file
//                                    LatLng center = AreaUtilities.getAreaCenterPoint(placemarks.get(0).getLatLngList());
//                                    // Move the camera in current location
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13f));
//                                    // Add the existing polygons in the map
//                                    addTheExistingAreasInMap();
//                                    // Close dialog
//                                    popupDialog.dismiss();
//                                }
//                            }else{
//                                // Show message
//                                Toast.makeText(CreateAreaActivity.this,
//                                        "Fill all the fields please", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                // Show dialog
//                popupDialog.show();
//            return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    /**
     * @param kmlFile has the new kml file witch user wants to add
     * @return true if file exists false if file is not exists
     */
    private boolean fileExistsCheck(KmlFile kmlFile) {
        for(KmlFile file : kmlFileMap.keySet()){
            if(file.getData().equals(kmlFile.getData())){
                    // Show message
                    Toast.makeText(CreateAreaActivity.this, "The file " + file.getName()
                            + " is already exists in the " + file.getFarmName(), Toast.LENGTH_LONG).show();
                    return true;
            }
        }
        return false;
    }
    /** TODO EDW EIMAIeeeeee 2222*/
//    /**
//     * TODO method description
//     * AUTH KALEITAI OTAN KANEIS POLYGONO PANW APO TON XARTI
//     */
//    private void showSaveAreaPopup(){
//        // Binding
//        SaveAreaPopupStyleBinding popupBinding;
//        // Initialize a view for saveAreaPopup
//        popupBinding = SaveAreaPopupStyleBinding.inflate(getLayoutInflater());
//        View popupView = popupBinding.getRoot();
//
//        // Instantiate a Dialog
//        Dialog popupDialog = new Dialog(this);
//        popupDialog.setContentView(popupView);
//        popupDialog.setCanceledOnTouchOutside(false);
//
//            // Initialize ui components
//            ImageView imageViewClose = popupBinding.btnCLose;
//            EditText areaName = popupBinding.areaName;
//            EditText areaDescription = popupBinding.areaDescription;
//            Button submitBtn = popupBinding.btnSubmit;
//
//            // Close Button ClickEvent
//            imageViewClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mMap.clear();
//                    //Add the existing polygons in the map
//                    addTheExistingAreasInMap();
//                    // Close dialog
//                    popupDialog.dismiss();
//                }
//            });
//            // Save Button ClickEvent
//            submitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG,"Submit button pressed");
//                // Get text from areaName textView in String type
//                String areaNameText = areaName.getText().toString();
//                // Get text from areaDescription textView in String type
//                String areaDescriptionText = areaDescription.getText().toString();
//                if(!areaNameText.isEmpty() && !areaDescriptionText.isEmpty()) {
//                    // If this new area is inner in other area
//                    if (detectInnerArea) {
//                        // Get the outsider area
//                        Placemark outsiderArea = AreaUtilities.getOutsiderArea();
//                        for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
//                            if (entry.getValue().contains(outsiderArea)) {
//                                entry.getValue().add(new Placemark
//                                        (areaNameText, areaDescriptionText, polygonOptions.getPoints()));
//                                // Save the kmlFileMap in shared preferences.
//                                kmlLocalStorageProvider.saveKmlFileMap(kmlFileMap);
//                            }
//                        }
//                        Log.d(TAG, "create inner area in " + AreaUtilities.getOutsiderArea().getName());
//                    } else {
//                        // Show message
//                        Toast.makeText(CreateAreaActivity.this,
//                                "Please draw you area inside in other area", Toast.LENGTH_LONG).show();
//                    }
//                    //Add the existing polygons in the map
//                    addTheExistingAreasInMap();
//                    // Close dialog
//                    popupDialog.dismiss();
//                }else{
//                    // Show message
//                    Toast.makeText(CreateAreaActivity.this,
//                            "Fill all the fields please", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//        // Show dialog
//        popupDialog.show();
//    }


    /**
     * TODO METHOD DESCRIPTION
     *
     */
    private void addTheExistingAreasInMap() {
        // Clear the map
        mMap.clear();
        for(KmlFile key : kmlFileMap.keySet()){
            for(Placemark placemark : kmlFileMap.get(key)){
               // Create new polygonOptions for each placemark
               polygonOptions = new PolygonOptions()
                    .strokeWidth(5f).addAll(placemark.getLatLngList())
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(70, 50, 255, 0));
               // Add placemark in the map
               mMap.addPolygon(polygonOptions);
               // Fill the placemarkList
               placemarkList.add(placemark);
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