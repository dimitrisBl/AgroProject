package com.example.agroproject.model.agro_api;

import android.util.Log;

import com.example.agroproject.model.HistoricalNdviGraphModel;
import com.example.agroproject.model.WeatherModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    /** Class TAG */
    private final static String TAG = "JsonParser";

    /**
     * TODO DESCRIPTION
     * @param data
     * @return
     */
    public static JSONArray parseResponse(String data){
        JSONArray jsondata = new JSONArray();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length() ; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", jsonArray.getJSONObject(i).getString("name"));
                jsonObject.put("id", jsonArray.getJSONObject(i).getString("id"));
                jsonObject.put("center", jsonArray.getJSONObject(i).getJSONArray("center"));
                jsonObject.put("created_at",jsonArray.getJSONObject(i).getString("created_at"));
                jsonObject.put("area",jsonArray.getJSONObject(i).getString("area"));
                jsondata.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "JSON DATA: " + jsondata);
        return jsondata;
    }

    /**
     * TODO DESCRIPTION
     * @param name
     * @param jsonArray
     * @return
     */
    public static String getId(String name, JSONArray jsonArray){
        String id = null;
        for(int i=0;i<jsonArray.length();i++){
            try {
                if(name.equals(jsonArray.getJSONObject(i).getString("name"))){
                    id = jsonArray.getJSONObject(i).getString("id");
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return id;
    }


    public static String getArea(String name, JSONArray jsonArray){
        String area = null;
        for(int i=0;i<jsonArray.length();i++){
            try {
                if(name.equals(jsonArray.getJSONObject(i).getString("name"))){
                    area = jsonArray.getJSONObject(i).getString("area");
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return area;
    }


    /**
     * TODO DESCRIPTION
     * @param data
     * @return
     */
    public static String getImage(String data) {
        String ndvi = null;
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONObject image = jsonObject.getJSONObject("image");
            ndvi = image.getString("ndvi");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ndvi;
    }


    /***
     * TODO DESCRIPTION
     *
     * @param jsonData
     * @return
     */
    public static  List<HistoricalNdviGraphModel> getHistoricalNdvi(String jsonData){

        List<HistoricalNdviGraphModel> historicalNdviGraphModelList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length() ; i++) {

                JSONObject currentJSONObject = jsonArray.getJSONObject(i);
                String sentinelType = currentJSONObject.getString("type");

                // If the type of sentinel is SENTINEL 2
                if (sentinelType.equals("s2")){
                    // Get the date as UnixTimeStamp
                    String dateTime = currentJSONObject.getString("dt");
                    JSONObject dataJSONObject = currentJSONObject.getJSONObject("data");

                    // Get the max value of ndvi
                    double max = Double.parseDouble(dataJSONObject.getString("max"));
                    // Get the mean value of ndvi
                    double mean = Double.parseDouble(dataJSONObject.getString("mean"));
                    // Get the min value of ndvi
                    double min = Double.parseDouble(dataJSONObject.getString("min"));

                    // Initialize a new HistoricalNdviGraphModel object
                    historicalNdviGraphModelList.add(new HistoricalNdviGraphModel(dateTime,max,mean,min));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
       // return historicalNdviData;
        return historicalNdviGraphModelList;
    }


    public static WeatherModel getWeatherData(String jsonData){

        WeatherModel weatherModel = new WeatherModel();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject mainObject = jsonObject.getJSONObject("main");
            JSONObject weatherObject = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject windObject =   jsonObject.getJSONObject("wind");

            String description = weatherObject.getString("description");
            String icon = weatherObject.getString("icon");

            String temp = mainObject.getString("temp");
            String tempMin = mainObject.getString("temp_min");
            String tempMax = mainObject.getString("temp_max");
            String humidity = mainObject.getString("humidity");

            String windSpeed = windObject.getString("speed");

            weatherModel.setDescription(description);
            weatherModel.setIcon(icon);
            weatherModel.setTemp(temp);
            weatherModel.setTempMin(tempMin);
            weatherModel.setTempMax(tempMax);
            weatherModel.setHumidity(humidity);
            weatherModel.setWindSpeed(windSpeed);

        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherModel;
    }

}
