package com.example.agroproject.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityFarmDetailsBinding;
import com.example.agroproject.databinding.ActivityMainBinding;
import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.agro_api.HttpRequest;
import com.example.agroproject.model.agro_api.JsonParser;
import com.example.agroproject.model.agro_api.StringBuildForRequest;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.view.adapters.DetailsListViewAdapter;
import com.example.agroproject.view.adapters.FarmListViewAdapter;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FarmDetailsActivity extends AppCompatActivity {

    // Historical NDVI by polygon -> https://agromonitoring.com/api/history-ndvi

    /** Activity view binding */
    private ActivityFarmDetailsBinding activityFarmDetailsBinding;

    /** ListView */
    private ListView listView;

    /** Adapter for ListView */
    private DetailsListViewAdapter detailsListViewAdapter;
    /** KmlLocalStorageProvider */
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    /** kmlFile Map */
    private Map<KmlFile, List<Placemark>> kmlFileMap = new HashMap<>();



    //private KmlFile currentKmlFile;

    //private Placemark outerPlacemark;
    //private List<Placemark> innerPlacemarks = new ArrayList<>();


    //private List<Placemark> outerPlacemarkAreas = new ArrayList<>();


    /**
     * The key is outer placemark
     * The values its the inner placemarks
     */
    private Map<Placemark, List<Placemark>> placemarkMap =  new HashMap<>();

    private List<Placemark> outerPlacemarks;

    // Response data of agro api
    private JSONArray jsonArray;

    private String currentDate;
    private String previousDate;
    private Placemark currentPlacemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_details);
        activityFarmDetailsBinding  = ActivityFarmDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityFarmDetailsBinding.getRoot());
        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        //  Load the kmlFile Map from shared preferences storage
        kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();

        initialize();
        // Get the current date - time
        currentDate = String.valueOf(System.currentTimeMillis()/1000);
        // Get the previous date -time
        previousDate = getCalculatedDate(-30);
        /** TODO Pernw to Serres.kml Kanonika tha tou dinoume to prwto placemark ths listas */
        currentPlacemark = outerPlacemarks.get(1);

       // Get the center LatLng of current outer area
        LatLng outerAreaCenter = AreaUtilities.getAreaCenterPoint(currentPlacemark.getLatLngList());

        // We are registering an observer (responseReceiver) with action name GetRequestData to receive Intents after http Get request in Agro api.
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, new IntentFilter("GetRequestData"));
       // Get request to receive the weather data of the current placemark from agro api
        HttpRequest.getRequest(this, StringBuildForRequest.weatherRequestLink(outerAreaCenter.latitude,outerAreaCenter.longitude), "Get weather");
        // Get request on endpoint polygons of Agro api
        HttpRequest.getRequest(this, StringBuildForRequest.polygonsRequestLink(), "Get all polygons");


//        // Get data from intent
//        Intent intent  = getIntent();
//        String currentPlacemarkName = intent.getStringExtra("placemark name");
//        String dateToAdded = intent.getStringExtra("date to added");
//        String currentPlacemarkID = intent.getStringExtra("polygon id");
//        // Get the current date - time
//        String today = String.valueOf(System.currentTimeMillis()/1000);
//
//
//        Log.d("DATE TO ADDED",dateToAdded);
//        Log.d("TODAY",today);
//        Log.d("POLY ID ",currentPlacemarkID);
//        Log.d("HISTORICAL URL",StringBuildForRequest.historicalNdviLink(currentPlacemarkID,dateToAdded,today));
//
//
//        for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
//            for (Placemark placemark : entry.getValue()) {
//               if(placemark.getName().equals(currentPlacemarkName)){
//                   outerPlacemark = placemark;
//                   innerPlacemarks = entry.getValue();
//                   currentKmlFile = entry.getKey();
//                }
//            }
//        }
//
//
//
//        // Set outer placemark name
//        activityFarmDetailsBinding.outerPlacemarkName.setText(outerPlacemark.getName());
//        // Set description
//        activityFarmDetailsBinding.areaDescription.setText(outerPlacemark.getDescription());
//
//        // Get the center LatLng of outer area
//        LatLng outerAreaCenter = AreaUtilities.getAreaCenterPoint(outerPlacemark.getLatLngList());
//
//        // We are registering an observer (responseReceiver) with action name GetRequestData to receive Intents after http Get request in Agro api.
//        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, new IntentFilter("GetRequestData"));


//
//        // Remove outer placemark
//        innerPlacemarks.remove(outerPlacemark);
    }


    private void initialize(){

      for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
          // Get the current KmlFile object
          KmlFile currentKmlFile = entry.getKey();

            for (Placemark placemark : entry.getValue()) {
                if ((placemark.getName() + ".kml").equals(currentKmlFile.getName())) {
                    placemarkMap.put(placemark, entry.getValue());
                }
            }
        }

        for (Map.Entry<Placemark, List<Placemark>> entry : placemarkMap.entrySet()) {

            Placemark placemark = null;
            for (Placemark element : entry.getValue()) {
                if (element.getName().equals(entry.getKey().getName())){
                    placemark = element;
                }
            }
            if (placemark != null){
                entry.getValue().remove(placemark);
            }
        }

        // Save the outer placemarks in this list
        outerPlacemarks = new ArrayList<>(placemarkMap.keySet());

        // Set data to the listViewAdapter from shared preferences
        detailsListViewAdapter = new DetailsListViewAdapter(new ArrayList<>(placemarkMap.keySet()));
        //----- ListView ----- //
        listView = activityFarmDetailsBinding.detailsListView;
        listView.setAdapter(detailsListViewAdapter);
    }



    /**
     * READ NDVI history chart
     * https://agromonitoring.com/dashboard/dashboard-satellite#ndvi
     */
    private BroadcastReceiver responseReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get response data and request type that are included in the Intent
            String responseData = intent.getStringExtra("Response data");
            String requestType = intent.getStringExtra("Request type");

            if (requestType.equals("Get all polygons")){
                // Parse response data
                jsonArray = JsonParser.parseResponse(responseData);
                // Get id of the clicked polygon
                String currentPlacemarkID = JsonParser.getId(currentPlacemark.getName(), jsonArray);


                ///Log.d("CURRENT PLACEMARK",currentPlacemark.getName());
                Log.d("HISTORICAL URL",StringBuildForRequest.historicalNdviLink(currentPlacemarkID,previousDate,currentDate));

               // Get request to receive the historical ndvi data of the current placemark from agro api
               HttpRequest.getRequest(FarmDetailsActivity.this, StringBuildForRequest.historicalNdviLink(currentPlacemarkID,previousDate,currentDate), "Get historical ndvi");



            }else if(requestType.equals("Get weather")){
                /** TODO WEATHER PARSER */
                Log.d("POLYLGON WEATHER",responseData);


            }else if(requestType.equals("Get historical ndvi")){
                List<String> dates = new ArrayList<>();
                List<String> maxValuesOfEachDate = new ArrayList<>();
                List<String> meanValuesOfEachDate = new ArrayList<>();
                List<String> minValuesOfEachDate = new ArrayList<>();

                // Get only the JSONObject with the sentinel type LANDSAT 8 (l8)
                List<JSONObject> historicalNdviData =  JsonParser.getHistoricalNdvi(responseData);

               for (int i = 0; i < historicalNdviData.size() ; i++) {
                   try {
                       dates.add(historicalNdviData.get(i).getString("dt"));
                       maxValuesOfEachDate.add(historicalNdviData.get(i).getString("max"));
                       meanValuesOfEachDate.add(historicalNdviData.get(i).getString("mean"));
                       minValuesOfEachDate.add(historicalNdviData.get(i).getString("min"));


                       Log.d("POSES FORES", "date " + dates.get(i) + " max " + maxValuesOfEachDate.get(i) + " mean " + meanValuesOfEachDate.get(i) + " min " + minValuesOfEachDate.get(i));

                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
            }
                /** TODO REQUEST IN stats URL TO GET NDVI MEAN, NDVI MAX, NDVI MIN VALUES FOR HISTORICAL CHART
                 * https://samples.agromonitoring.com/agro/1.0/ndvi/history?polyid=5aaa8052cbbbb5000b73ff66&start=1530336000&end=1534976000&appid=b1b15e88fa797225412429c1c50c122a1
                 * https://agromonitoring.com/api/history-ndvi
                 *
                 * QUERY AGRO API https://api.agromonitoring.com/agro/1.0/ndvi/history?polyid=60743b8ab5d88980ce029554&start=1618230154&end=1649529582&appid=6285cde775f088c749b4f1201829658a
                 * */
        }
    };

    /**
     * Pass your date format and no of days for minus from current
     * If you want to get previous date then pass days with minus sign
     * else you can pass as it is for next date
     * @param days
     * @return Calculated Date
     */
    public static String getCalculatedDate( int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,days);
        return String.valueOf(cal.getTimeInMillis()/1000);

    }
}