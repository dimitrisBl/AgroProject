package com.example.agroproject.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO CLASS DESCRIPTION
 */
public class FarmAreaLocalStorage {
    /** Class TAG */
    private final String TAG = "FarmAreaLocalStorage";

    /** Name of the Shared preferences file */
    private final String PREFS_NAME = "FarmAreaSave";

    /** Name for the monitoringAreaList in shared preferences file */
    private final String FARM_AREA_LIST = "farmAreaList";

    /** SharedPreferences object */
    private SharedPreferences sharedPreferences;

    /** List with FarmArea objects */
    private List<FarmArea> farmAreaList = new ArrayList<>();

    /**
     * This method initialize the polygonStatePrefs object.
     * @param context takes the current context application.
     */
    public FarmAreaLocalStorage(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This method create a farm monitoring area and adds this area in the
     * list farmAreaList.
     *
     * @param farmArea is a FarmArea object, this object
     * have info's about monitoring area.
     */
    public void createFarmAre(FarmArea farmArea){
        farmAreaList.add(farmArea);
    }

    /**
     * This method save the monitoringAreaList in shared preferences file.
     * monitoringAreaList haves the data of the monitoring areas.
     */
    public void saveFarmArea(){
        Log.d(TAG,"Area save executed");
        // Instantiate the gson object.
        Gson gson = new Gson();
        //Convert java object as a json string.
        String json = gson.toJson(farmAreaList);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Put json string in shared preferences.
        editor.putString(FARM_AREA_LIST, json);
        editor.commit();
    }

    /**
     * This method receives monitoringAreaList from the shared preferences saved file.
     * @return the list monitoringAreaList it contains MonitoringArea objects from the save file.
     */
    public List<FarmArea> loadFarmArea(){
        Log.d(TAG,"Load area from saved file");
        String serializedObject = sharedPreferences.getString(FARM_AREA_LIST, null);
        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();

            Type type = new TypeToken<List<FarmArea>>(){}.getType();
            farmAreaList = gson.fromJson(serializedObject, type);
        }
        return farmAreaList;
    }

}
