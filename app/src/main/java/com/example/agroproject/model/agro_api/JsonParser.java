package com.example.agroproject.model.agro_api;

import android.util.Log;

import com.google.gson.JsonArray;

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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return id;
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

    /**
     * TODO DESCRIPTION
     * @param name
     * @param jsonArray
     * @return
     */
    public static String getDateToAdded(String name, JSONArray jsonArray){
        String dateToAdded =null;
        for(int i=0;i<jsonArray.length();i++){
            try {
                if (name.equals(jsonArray.getJSONObject(i).getString("name"))){
                    dateToAdded = jsonArray.getJSONObject(i).getString("created_at");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dateToAdded;
    }


    public static List<JSONObject> getHistoricalNdvi(String jsonData){

        List<JSONObject> historicalNdviData = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length() ; i++) {

                JSONObject currentJSONObject = jsonArray.getJSONObject(i);
                String sentinelType = currentJSONObject.getString("type");

                // If the type of sentinel is LANDSAT 8
                if (sentinelType.equals("l8")){
                    // Get the date as UnixTimeStamp
                    String dateTime = currentJSONObject.getString("dt");
                    JSONObject dataJSONObject = currentJSONObject.getJSONObject("data");

                    // Get the max value of ndvi
                    String max = dataJSONObject.getString("max");
                    // Get the mean value of ndvi
                    String mean = dataJSONObject.getString("mean");
                    // Get the min value of ndvi
                    String min = dataJSONObject.getString("min");


                    // Initialize a new JSONObject and put only the
                    // important data for historical ndvi on it
                    JSONObject myJSONObject = new JSONObject();
                    myJSONObject.put("dt",dateTime);
                    myJSONObject.put("max",max);
                    myJSONObject.put("mean",mean);
                    myJSONObject.put("min",min);


                    historicalNdviData.add(myJSONObject);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return historicalNdviData;
    }


}
