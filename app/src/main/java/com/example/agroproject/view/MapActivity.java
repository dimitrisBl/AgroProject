package com.example.agroproject.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityMapBinding;
import com.example.agroproject.databinding.AreaClickPopupStyleBinding;
import com.example.agroproject.model.agro_api.HttpRequest;
import com.example.agroproject.model.agro_api.JsonParser;
import com.example.agroproject.model.agro_api.StringBuildForRequest;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.model.Placemark;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import org.json.JSONArray;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    /** KmlLocalStorageProvider */
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    /** KmlFile Map */
    private Map<KmlFile, List<Placemark>> kmlFileMap = new HashMap<>();

    /** JSON data from GET request in agro api */
    private JSONArray jsonArray;

    /** BitmapDescriptor has a ndvi image after image request */
    private BitmapDescriptor bitmapDescriptor;

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
        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        // Load the kmlFile Map from shared preferences storage
        kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
        // Get request on endpoint polygons of Agro api
        HttpRequest.getRequest(this, StringBuildForRequest.polygonsRequestLink(), "Get all polygons");
        // We are registering an observer (responseReceiver) with action name GetRequestData to receive Intents after http Get request in Agro api.
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, new IntentFilter("GetRequestData"));
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
            for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
                for (Placemark placemark : entry.getValue()) {
                   if (polygon.getTag().equals(placemark.getName())) {
                       showAreaPopUp(placemark);
                        /** TODO edw kamia fora xtupaei null pointer gia to jsonArray, tha prepei na ginetai handle kai ksana to arxiko request sta polygons */
                       // Get id for the clicked polygon
                       String polygonId = JsonParser.getId(placemark.getName(), jsonArray);
                       // Create a url for sentinel Get request of agro api for specific polygon and date
                       String sentinelRequestLink = StringBuildForRequest.sentinelRequestLink(polygonId,"1609501337","1617277337");
                       // Get sentinel data from agro api
                       HttpRequest.getRequest( MapActivity.this, sentinelRequestLink,  "Get sentinel data");
                    }
                }
            }
        }
    };
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
            }else if (requestType.equals("Get sentinel data")){
                // Get image url
                String imageUrl = JsonParser.getImage(responseData);
                // Get image from Agro api
                new getImageAsync().execute(imageUrl);
            }
            Log.d(TAG,"receive response heree"+responseData);
        }
    };



    @SuppressLint("NewApi")
    private void showAreaPopUp(Placemark placemarkParam) {
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
            farmAreaName.setText(placemarkParam.getName());
            // description
            TextView farmAreaDescription = popupBinding.areaDescription;
            farmAreaDescription.setText(placemarkParam.getDescription());
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
                // Close area pop up
                popUpDialog.dismiss();
                // Show new pop up for the delete question
                new AlertDialog.Builder(MapActivity.this)
                       .setIcon(R.drawable.ic_baseline_delete_24)
                       .setTitle("Delete")
                       .setMessage("You want to delete this area?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               for(Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()){
                                   if(entry.getValue().remove(placemarkParam)){
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
                               // Add the existing polygons in the map
                               addTheExistingAreas();
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
                               // Add the existing polygons in the map
                               addTheExistingAreas();
                          }
                       })
                .show();
                }
            });
            // Ndvi button
            Button ndviButton = popupBinding.ndviBtn;
            ndviButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create LatLng object for this location
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng latLng : placemarkParam.getLatLngList()) {
                        builder.include(latLng);
                    }
                    // Add ground overlay in the map
                    mMap.addGroundOverlay(new GroundOverlayOptions()
                            .positionFromBounds(builder.build())
                            .image(bitmapDescriptor)
                            .zIndex(100)
                    );
                    //Close dialog
                    popUpDialog.dismiss();
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
        for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
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


    @Override
    protected void onStop() {
        super.onStop();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
    }

    /**
     * TODO CLASS DESCRIPTION
     */
    private class getImageAsync extends AsyncTask<String, Void, BitmapDescriptor>{
        @Override
        protected BitmapDescriptor doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                input.close();
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(myBitmap);
                return  bitmapDescriptor;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d(TAG,"MalformedURLException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"io exception");
            }
            return null;
        }
        @Override
        protected void onPostExecute(BitmapDescriptor descriptor) {
            super.onPostExecute(descriptor);
            bitmapDescriptor = descriptor;
        }
    }
}
