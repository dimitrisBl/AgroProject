package com.example.agroproject.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO CLASS DESCRIPTION
 */
public class MonitoringAreaManager {
    /** Class TAG */
    private final String TAG = "SelectedAreas";

    /** Name of the Shared preferences file */
    private final String PREFS_NAME ="PolygonOptionsSave";

    /** Name for the monitoringAreaList in shared preferences file */
    private final String MONITORING_AREA_LIST = "monitoringAreaList";

    private final String FARM_AREA_LIST = "farmAreaList";

    /** SharedPreferences object */
    private SharedPreferences polygonStatePrefs;

    /** List with MonitoringArea objects */
    private List<MonitoringArea> monitoringAreaList = new ArrayList<>();

    /** List with FarmArea objects */
    private List<FarmArea> farmAreaList = new ArrayList<>();

    /**
     * This method initialize the polygonStatePrefs object.
     * @param context takes the current context application.
     */
    public MonitoringAreaManager(Context context){
        polygonStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This method create a monitoring area and adds this area in the
     * list monitoringAreaList.
     * @param monitoringArea is a MonitoringArea object, this object
     * have info's about monitoring area.
     */
    public void createMonitoringArea(MonitoringArea monitoringArea){
        monitoringAreaList.add(monitoringArea);
    }

    /**
     * This method save the monitoringAreaList in shared preferences file.
     * monitoringAreaList haves the data of the monitoring areas.
     */
    public void saveMonitoringArea(){
        Log.d(TAG,"Area save executed");
        // Instantiate the gson object.
        Gson gson = new Gson();
        //Convert java object as a json string.
        String json = gson.toJson(monitoringAreaList);

        SharedPreferences.Editor editor = polygonStatePrefs.edit();
        // Put json string in shared preferences.
        editor.putString(MONITORING_AREA_LIST, json);
        editor.commit();
    }

    /**
     * This method receives monitoringAreaList from the shared preferences saved file.
     * @return the list monitoringAreaList it contains MonitoringArea objects from the save file.
     */
    public List<MonitoringArea> loadMonitoringArea(){
        Log.d(TAG,"Load area from saved file");
        String serializedObject = polygonStatePrefs.getString(MONITORING_AREA_LIST, null);
        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();

            Type type = new TypeToken<List<MonitoringArea>>(){}.getType();
            monitoringAreaList = gson.fromJson(serializedObject, type);
        }
        return monitoringAreaList;
    }



    /**
     *
     * @param farmArea
     */
    public void createFarmArea(FarmArea farmArea){
        Log.d(TAG,"Farm area create executed");
        farmAreaList.add(farmArea);
    }


    /**
     *
     */
    public void saveFarmArea(){
        Log.d(TAG,"Farm area save executed");
        Gson gson = new Gson();
        //Convert java object as a json string.
        String json = gson.toJson(farmAreaList);
        SharedPreferences.Editor editor = polygonStatePrefs.edit();
        // Put json string in shared preferences.
        editor.putString(FARM_AREA_LIST, json);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public List<FarmArea> loadFarmArea(){
        Log.d(TAG,"Load area from saved file");
        String serializedObject = polygonStatePrefs.getString(FARM_AREA_LIST, null);
        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();

            Type type = new TypeToken<List<FarmArea>>(){}.getType();
            farmAreaList = gson.fromJson(serializedObject, type);
        }
        return  farmAreaList;
    }
}
