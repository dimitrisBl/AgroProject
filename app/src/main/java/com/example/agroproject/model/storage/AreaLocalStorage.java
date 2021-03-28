package com.example.agroproject.model.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.agroproject.model.area.Area;
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
    private final String AREA_LIST = "AreaList";

    /** SharedPreferences object */
    private SharedPreferences sharedPreferences;

    /** List with MonitoringArea objects */
    private List<Area> areaList = new ArrayList<>();


    /**
     * This method initialize the polygonStatePrefs object.
     * @param context takes the current context application.
     */
    public AreaLocalStorage(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
//
//    /**
//     * This method create a monitoring area and adds this area in the
//     * list monitoringAreaList.
//     * @param area is a MonitoringArea object, this object
//     * have info's about monitoring area.
//     */
//    public void createMonitoringArea(Area area){
//        areaList.add(area);
//    }
//
//
//    public List<Area> getAreaList() {
//        return areaList;
//    }

    /**
     * This method save the areaList in shared preferences file.
     * monitoringAreaList haves the data of the monitoring areas.
     */
    public void saveArea(List<Area> areaList){
        Log.d(TAG,"Area save executed");
        // Instantiate the gson object.
        Gson gson = new Gson();
        //Convert java object as a json string.
        String json = gson.toJson(areaList);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Put json string in shared preferences.
        editor.putString(AREA_LIST, json);
        editor.commit();
    }


    /**
     * This method receives monitoringAreaList from the shared preferences saved file.
     * @return the list monitoringAreaList it contains MonitoringArea objects from the save file.
     */
    public List<Area> loadArea(){
        Log.d(TAG,"Load area from saved file");
        String serializedObject = sharedPreferences.getString(AREA_LIST, null);

        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();

            Type type = new TypeToken<List<Area>>(){}.getType();
            areaList = gson.fromJson(serializedObject, type);
        }
        return areaList;
    }



}
