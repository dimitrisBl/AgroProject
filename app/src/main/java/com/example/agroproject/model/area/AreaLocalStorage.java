package com.example.agroproject.model.area;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.agroproject.model.area.FarmArea;

import com.example.agroproject.model.area.InnerArea;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AreaLocalStorage {


    /** Class TAG */
    private final String TAG = "SelectedAreas";

    /** Name of the Shared preferences file */
    private final String PREFS_NAME ="AreaSave";

    /** Name for the monitoringAreaList in shared preferences file */
    private final String FARM_AREA_LIST = "AreaList";

    private final String INNER_AREA_LIST = "HoleList";

    /** SharedPreferences object */
    private SharedPreferences sharedPreferences;

    /** List with MonitoringArea objects */
    private List<FarmArea> farmAreaList = new ArrayList<>();

    /** List with Hole objects */
    private List<InnerArea> innerAreaList = new ArrayList<>();

    /**
     * This method initialize the polygonStatePrefs object.
     * @param context takes the current context application.
     */
    public AreaLocalStorage(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This method create a monitoring area and adds this area in the
     * list monitoringAreaList.
     * @param area is a MonitoringArea object, this object
     * have info's about monitoring area.
     */
    public void createFarmArea(FarmArea area){
        farmAreaList.add(area);
    }

    /**
     * This method save the farmAreaList in shared preferences file.
     * farmAreaList haves the data of the farm monitoring areas.
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
    public List<FarmArea> loadFarmArea() {
        Log.d(TAG, "Load area from saved file");
        String serializedObject = sharedPreferences.getString(FARM_AREA_LIST, null);
        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();

            Type type = new TypeToken<List<FarmArea>>() {}.getType();
            farmAreaList = gson.fromJson(serializedObject, type);
        }
        return farmAreaList;
    }




    public void createInnerArea(InnerArea area){
        innerAreaList.add(area);
    }

    /**
     * This method save the innerAreaList in shared preferences file.
     * innerAreaList haves the data of the farm monitoring areas.
     */
    public void saveInnerArea(){
        Log.d(TAG,"Hole save executed");
        // Instantiate the gson object.
        Gson gson = new Gson();
        //Convert java object as a json string.
        String json = gson.toJson(innerAreaList);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Put json string in shared preferences.
        editor.putString(INNER_AREA_LIST, json);
        editor.commit();
    }

    /**
     * This method receives monitoringAreaList from the shared preferences saved file.
     * @return the list monitoringAreaList it contains MonitoringArea objects from the save file.
     */
    public List<InnerArea> loadInnerArea() {
        Log.d(TAG, "Load hole from saved file");
        String serializedObject = sharedPreferences.getString(INNER_AREA_LIST, null);
        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();

            Type type = new TypeToken<List<InnerArea>>() {}.getType();
            innerAreaList = gson.fromJson(serializedObject, type);
        }
        return innerAreaList;
    }
}
