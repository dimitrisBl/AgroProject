package com.example.agroproject.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityFarmDetailsBinding;
import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.DatePicker;
import com.example.agroproject.model.HistoricalNdviGraphModel;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.WeatherModel;
import com.example.agroproject.model.agro_api.HttpRequest;
import com.example.agroproject.model.agro_api.JsonParser;
import com.example.agroproject.model.agro_api.StringBuildForRequest;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.services.NetworkUtil;
import com.example.agroproject.view.adapters.DetailsListViewAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;


import org.json.JSONArray;
import org.json.JSONException;


import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.graphics.Color.rgb;


public class FarmDetailsActivity extends AppCompatActivity {

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

    /**
     * The key is outer placemark
     * The values its the inner placemarks
     */
    private Map<Placemark, List<Placemark>> placemarkMap =  new HashMap<>();

    /** Has the outer placemarks only */
    private List<Placemark> outerPlacemarks;

    /** This JSONArray has the response data of agro api
     * after HTTP GET request in the polygons entry and */
    private JSONArray jsonArray;

    /** Placemark selected by user */
    private Placemark selectedPlacemark;

    /** The id of placemark selected by user */
    private String  selectedPlacemarkID;

    /** Used for the HTTP GET request
     * at the sentinel entry of agro api */
    private String oneDayPreviousDate;
    private String thirtyDayspreviousDate;

    /** Historical ndvi plot */
    private XYPlot ndviPlot;

    /** Weather image loader */
    private WeatherModel.WeatherImageLoader weatherImageLoader;

    /** NetworkUtil */
    private NetworkUtil networkUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFarmDetailsBinding  = ActivityFarmDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityFarmDetailsBinding.getRoot());
        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        //  Load the kmlFile Map from shared preferences storage
        kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
        // We are registering an observer (responseReceiver) with action name GetRequestData to receive Intents after http Get request in Agro api.
        LocalBroadcastManager.getInstance(this).registerReceiver(agroApiResponseReceiver, new IntentFilter("GetRequestData"));
        // Get one day previous date - time
        oneDayPreviousDate = calculateDate(-1);
        // Get 30 days previous date -time
        thirtyDayspreviousDate = calculateDate(-30);
        // Get data from intent
        Intent myIntent = getIntent();
        String allPolygonsAgroApi = myIntent.getStringExtra("ALL POLYGONS");
        initialize(allPolygonsAgroApi);
        // Initialize weatherImageLoader
        weatherImageLoader =  new WeatherModel.WeatherImageLoader(this);
        // Instantiate a NetworkUtil object
        networkUtil = new NetworkUtil(this);
    }

    /**
     * Initialize ui components
     *
     * @param allPolygonsAgroApi has all outer polygons of the application
     */
    private void initialize(String allPolygonsAgroApi){
        placemarkMap = AreaUtilities.placemarkClassification(kmlFileMap);
        // Save the outer placemarks in this list
        outerPlacemarks = new ArrayList<>(placemarkMap.keySet());
        // Set data to the listViewAdapter from shared preferences
        detailsListViewAdapter = new DetailsListViewAdapter(new ArrayList<>(placemarkMap.keySet()));
        //----- ListView ----- //
        listView = activityFarmDetailsBinding.detailsListView;
        listView.setAdapter(detailsListViewAdapter);
        listView.setOnItemClickListener(listViewItemClickListener);
        if (outerPlacemarks.size() > 0 ) {
            try {
                jsonArray = new JSONArray(allPolygonsAgroApi);
                // Select the first item of listview
                // when activity opens first time
                listView.setItemChecked(0,true);
                listViewItemClickListener.onItemClick(listView,listView.getChildAt(0),listView.getSelectedItemPosition(),listView.getSelectedItemId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Initialize ndvi plot
        ndviPlot = activityFarmDetailsBinding.ndviplot;
        ndviPlot.setRenderMode(Plot.RenderMode.USE_MAIN_THREAD);
        //this removes the vertical lines
        ndviPlot.getGraph().setDomainGridLinePaint(null);
        // Set button click listener
        activityFarmDetailsBinding.chooseDateBtn.setOnClickListener(buttonClickListener);
    }

    /**
     * HTTP Get request to the sentinel entry of agro api
     * to get the historical ndvi data of selected placemark.
     *
     * @param selectedPlacemark has the selected placemark.
     */
    private void historicalNdviRequest(Placemark selectedPlacemark){
        // Get id of the clicked polygon
        selectedPlacemarkID = JsonParser.getId(selectedPlacemark.getName(), jsonArray);
        //Get request to receive the historical ndvi data of the selected placemark from Agro api
        HttpRequest.getRequest(FarmDetailsActivity.this,
                StringBuildForRequest.historicalNdviLink(selectedPlacemarkID,thirtyDayspreviousDate,oneDayPreviousDate), "Get historical ndvi");
    }

    /**
     * HTTP Get request to the weather entry of agro api
     * to get the weather data of seleceted placemark.
     *
     * @param selectedPlacemark has the selected placemark.
     */
    private void weatherRequest(Placemark selectedPlacemark){
        // Get the center LatLng of current outer area
        LatLng selectedPlacemarkCenter= AreaUtilities.getAreaCenterPoint(selectedPlacemark.getLatLngList());
        // Get request to receive the weather data of the current placemark from agro api
        HttpRequest.getRequest(this, StringBuildForRequest.weatherRequestLink(selectedPlacemarkCenter.latitude,selectedPlacemarkCenter.longitude), "Get weather");
    }



    /**
     * Event handler to handle the item click of list view
     */
    private AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
            if(index == -1){
                // Default placemark selection from the list view
                // at first time the activity opens
                selectedPlacemark = outerPlacemarks.get(0);
            }else{
                selectedPlacemark = detailsListViewAdapter.getItem(index);
            }
            // Get weather data for the clicked placemark
            weatherRequest(selectedPlacemark);
            // Get historical ndvi data for the clicked placemark and refresh the plot
            historicalNdviRequest(selectedPlacemark);
        }
    };


    /**
     * Button click listener
     */
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            // Get current button click
            Button currentButton = (Button) view;
            // Get text from current button
            String currentButtonText = String.valueOf(currentButton.getText()).toUpperCase();

            Log.d("EVENT CLICK HANDLER",currentButtonText);
            switch (currentButtonText) {
                case "CHOOSE DATE":
                    // Instantiate a new DatePicker object
                    DatePicker datePicker = new DatePicker();
                    datePicker.Init();
                    MaterialDatePicker materialDatePicker = datePicker.getMaterialDatePicker();
                    // Show calendar
                    materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
                    // Set click listener for save button of calendar
                    materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                        @Override
                        public void onPositiveButtonClick(Pair<Long, Long>  selection) {
                            // Edit time before add the time in sentinel url of agro api
                            // This processing is done only for time zone of the agro api (UnixTimeStamp)
                            int dateFromLength = selection.first.toString().length();
                            int dateToLength = selection.second.toString().length();
                            String dateFrom = selection.first.toString().substring(0, dateFromLength-3);
                            String dateTo = selection.second.toString().substring(0, dateToLength-3);
                            // Get request to receive the historical ndvi data of the selected placemark from Agro api
                            HttpRequest.getRequest(FarmDetailsActivity.this,
                                    StringBuildForRequest.historicalNdviLink(selectedPlacemarkID,dateFrom,dateTo), "Get historical ndvi");
                        }
                    });
                break;
            }

        }
    };


    /**
     * READ NDVI history chart
     * https://agromonitoring.com/dashboard/dashboard-satellite#ndvi
     */
    private BroadcastReceiver agroApiResponseReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get response data and request type that are included in the Intent
            String responseData = intent.getStringExtra("Response data");
            String requestType = intent.getStringExtra("Request type");

            if(requestType.equals("Get weather")){
                Log.d("POLYLGON WEATHER",responseData);
                // Parse the weather response data of agro api
                // and instantiate a WeatherModel object
                WeatherModel weatherModel = JsonParser.getWeatherData(responseData);
                // Set image loader
                weatherImageLoader.setImageLoader();
               // int[] imageViews = {R.drawable.weather_image_morning,};
                Calendar calendar = Calendar.getInstance();
                int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
                if(hour24hrs <= 24){
                    activityFarmDetailsBinding.topLayout.setBackgroundResource(R.drawable.weather_image_morning);//ImageView img = (ImageView) findViewById(imageViews[pindex]) ;
                   // img.setImageResource(R.drawable.progressgreen);
                }
                // Set data in the ui
                activityFarmDetailsBinding.min.setText(weatherModel.getTempMin());
                activityFarmDetailsBinding.max.setText(weatherModel.getTempMax());
                activityFarmDetailsBinding.temp.setText(weatherModel.getTemp());
                activityFarmDetailsBinding.humidity.setText(weatherModel.getHumidity());
                activityFarmDetailsBinding.description.setText(weatherModel.getDescription());
                activityFarmDetailsBinding.wind.setText(weatherModel.getWindSpeed());
                activityFarmDetailsBinding.area.setText(JsonParser.getArea(selectedPlacemark.getName(),jsonArray)+" ha");
                activityFarmDetailsBinding.weatherImage.setImageUrl(WeatherModel.getImage(weatherModel.getIcon()), weatherImageLoader.getmImageLoader());

            }else if(requestType.equals("Get historical ndvi")){
                Log.d("HISTORICAL NDVI",responseData);

                List<String> dates = new ArrayList<>();
                List<Double> maxValuesOfEachDate = new ArrayList<>();
                List<Double> meanValuesOfEachDate = new ArrayList<>();
                List<Double> minValuesOfEachDate = new ArrayList<>();
                // Parse the historical ndvi response data of agro api
                // and instantiate a List with HistoricalNdviGraphModel objects for each value of the historical ndvi
                List<HistoricalNdviGraphModel> historicalNdviGraphModelList = JsonParser.getHistoricalNdvi(responseData);

                // Reverse iteration
                for (int i = historicalNdviGraphModelList.size() - 1; i >= 0; i--) {
                    dates.add(historicalNdviGraphModelList.get(i).getDt());
                    maxValuesOfEachDate.add(historicalNdviGraphModelList.get(i).getMax());
                    meanValuesOfEachDate.add(historicalNdviGraphModelList.get(i).getMean());
                    minValuesOfEachDate.add(historicalNdviGraphModelList.get(i).getMin());
                }
                // Draw plot
                drawHistoricalNdviGraph(dates,maxValuesOfEachDate,meanValuesOfEachDate,minValuesOfEachDate);
            }
        }
    };


    /**
     * Draw historical ndvi plot.
     *
     * @param dates at the x-axis of plot.
     * @param maxNdviValuesOfEachDate the values for the line max vegetation.
     * @param meanNdviValueOfEachDate the values for the line mean vegetation.
     * @param minNdviValueOfEachDate the values for the line min vegetation.
     */
    private void drawHistoricalNdviGraph(List<String> dates,List<Double> maxNdviValuesOfEachDate, List<Double> meanNdviValueOfEachDate, List<Double> minNdviValueOfEachDate){

        ndviPlot.clear();

        double minVegetationIndex  = 999999999;
        double maxVegetetaionIndex = 0;

        // Find the max vegetation index
        for (Double element : maxNdviValuesOfEachDate){
            if (element > maxVegetetaionIndex){ maxVegetetaionIndex = element;  }
        }
        // Find The min vegetation index
        for (Double element : minNdviValueOfEachDate){
            if (element < minVegetationIndex){ minVegetationIndex = element;  }
        }

        final Number[] maxVegetationValues = maxNdviValuesOfEachDate.toArray(new Double[maxNdviValuesOfEachDate.size()]);
        final Number[] meanVegetationValues = meanNdviValueOfEachDate.toArray(new Double[meanNdviValueOfEachDate.size()]);
        final Number[] minVegetationValues =  minNdviValueOfEachDate.toArray(new Double[minNdviValueOfEachDate.size()]);

        final List<String> myDates = new ArrayList<>();
        for (String element :dates){
            // Concert date from UnixTimeStamp to simple date
            long dv = Long.valueOf(element)*1000;// its need to be in milisecond
            Date df = new java.util.Date(dv);
            String vv = new SimpleDateFormat("dd-MM-yy").format(df);
            myDates.add(vv);
        }

        // ~~~~~~~~~~ LINE FOR THE MAX VEGETATION ~~~~~~~~~~~~~~ //
        XYSeries maxVegetationSeries = new SimpleXYSeries(Arrays.asList(maxVegetationValues),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"MAX"); //Arrays.asList(series1Numbers)
        LineAndPointFormatter maxVegetationLine = new LineAndPointFormatter(rgb(0,128,0),Color.BLACK,null,null);
        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        maxVegetationLine.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        // ~~~~~~~~~~ LINE FOR THE MEAN VEGETATION ~~~~~~~~~~~~~~ //
        XYSeries meanVegetationSeries = new SimpleXYSeries(Arrays.asList(meanVegetationValues),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"MEAN");
        LineAndPointFormatter meanVegetationLine = new LineAndPointFormatter(rgb(76, 187, 23),Color.BLACK,null,null);
        meanVegetationLine.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        // ~~~~~~~~~~ LINE FOR THE MIN VEGETATION ~~~~~~~~~~~~~~ //
        XYSeries minVegetationSeries = new SimpleXYSeries(Arrays.asList(minVegetationValues),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"MIN");
        LineAndPointFormatter minVegetationLine = new LineAndPointFormatter(rgb(144, 238, 144),Color.BLACK,null,null);
        minVegetationLine.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        // ADD LINES TO THE PLOT
        ndviPlot.addSeries(minVegetationSeries,minVegetationLine);
        ndviPlot.addSeries(meanVegetationSeries,meanVegetationLine);
        ndviPlot.addSeries(maxVegetationSeries,maxVegetationLine);

        // Set the minimum and maximum range of Y axis
        ndviPlot.setRangeBoundaries(minVegetationIndex,BoundaryMode.FIXED ,maxVegetetaionIndex+0.1, BoundaryMode.FIXED);

        ndviPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");

            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition fieldPosition) {
                int i =  (int) Math.round(((Number) obj).floatValue());
                // Concert date from UnixTimeStamp to simple date
                long dv = Long.valueOf(dates.get(i))*1000;// its need to be in milisecond
                Date df = new java.util.Date(dv);
                String vv = new SimpleDateFormat("dd-MM-yy").format(df);

                return  toAppendTo.append(vv);
            }

            @Override
            public Object parseObject(String s, ParsePosition parsePosition) {
                return null;
            }
        });
        //PanZoom.attach(ndviPlot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);
        //PanZoom.attach(ndviPlot);
        ndviPlot.redraw();
    }

    /**
     * Pass the number of days for minus from current
     * If you want to get previous date then pass days with minus sign
     * else you can pass as it is for next date.
     * If you want the current date then give zero to the days param.
     * @param days
     * @return Date in UnixTimeStamp type
     */
    private static String calculateDate(int days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(System.currentTimeMillis());
        Calendar cal = dateFormat.getCalendar();
        cal.add(Calendar.DATE,days);
        // Return date as UnixTimeStamp type
        return String.valueOf(cal.getTimeInMillis()/1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Receive messages about Network status.
        // We are registering an observer from NetworkUtil class which extends BroadCast Receiver class
        // to receive intents with action name "CONNECTIVITY_ACTION".
         registerReceiver(networkUtil, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister since the activity is about to be closed.
        unregisterReceiver(networkUtil);
    }
}