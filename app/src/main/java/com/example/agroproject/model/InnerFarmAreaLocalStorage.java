package com.example.agroproject.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InnerFarmAreaLocalStorage {

    /** Class TAG */
    private final String TAG = "InnerFarmAreaLocalStorage";

    /** Name of the Shared preferences file */
    private final String PREFS_NAME = "InnerFarmAreaSave";

    /** Name for the monitoringAreaList in shared preferences file */
    private final String INNER_FARM_AREA_LIST = "innerFarmAreaList";

    /** SharedPreferences object */
    private SharedPreferences sharedPreferences;

    /** List with FarmArea objects */
    private List<InnerFarmArea> innerFarmAreaList = new ArrayList<>();

    /**
     * This method initialize the polygonStatePrefs object.
     * @param context takes the current context application.
     */
    public InnerFarmAreaLocalStorage(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    public void createInnerFarmArea(InnerFarmArea innerFarmArea){
        innerFarmAreaList.add(innerFarmArea);
    }

    /**
     * This method save the monitoringAreaList in shared preferences file.
     * monitoringAreaList haves the data of the monitoring areas.
     */
    @SuppressLint("LongLogTag")
    public void saveFarmArea(){
        Log.d(TAG,"Area save executed");
        // Instantiate the gson object.
        Gson gson = new Gson();
        //Convert java object as a json string.
        String json = gson.toJson(innerFarmAreaList);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Put json string in shared preferences.
        editor.putString(INNER_FARM_AREA_LIST, json);
        editor.commit();
    }


    /**
     * This method receives monitoringAreaList from the shared preferences saved file.
     * @return the list monitoringAreaList it contains MonitoringArea objects from the save file.
     */
    @SuppressLint("LongLogTag")
    public List<InnerFarmArea> loadInnerFarmArea(){
        Log.d(TAG,"Load area from saved file");
        String serializedObject = sharedPreferences.getString(INNER_FARM_AREA_LIST, null);
        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();

            Type type = new TypeToken<List<InnerFarmArea>>(){}.getType();
            innerFarmAreaList = gson.fromJson(serializedObject, type);
        }
        return innerFarmAreaList;
    }
}
