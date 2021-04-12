package com.example.agroproject.model.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.agroproject.model.Placemark;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KmlLocalStorageProvider {

    /** Class TAG */
    private final String TAG = "KmlLocalStorageProvider";

    /** Name of the Shared preferences file */
    private final String PREFS_NAME ="KmlLocalStorage";

    /** Placemark Map name in shared preferences */
    private final String PLACEMARK_MAP = "placemarkMap";

    /** Farm Map name in shared preferences */
    private final String FARM_MAP = "farmMap";

    /** SharedPreferences object */
    private SharedPreferences sharedPreferences;

    /** Map with Placemark objects */
    private Map<String, List<Placemark>> placemarkMap = new HashMap<>();

    /** Map with KmlFile objects */
    private Map<String, List<KmlFile>> farmMap = new HashMap<>();

    /**
     * This method initialize the KmlLocalStorageProvider object.
     * @param context takes the current context application.
     */
    public KmlLocalStorageProvider(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This method save the famMap in shared preferences file.
     * @param placemarkMap has objects of the Placemark class.
     */
    public void savePlacemarkMap(Map<String, List<Placemark>> placemarkMap){
        Log.d(TAG,"Area save executed");
        String converted = new Gson().toJson(placemarkMap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLACEMARK_MAP, converted);
        editor.apply();
    }


    /**
     * This method load the Map with KmlFile objects from shared preferences.
     * @return the Map kmlFileMap it contains KmlFile objects from the save file.
     */
    public Map<String, List<Placemark>> loadPlacemarkMap(){
        Log.d(TAG,"load area save executed");
        String defaultValue = new Gson().toJson(new HashMap<String, List<Placemark>>());
        String json = sharedPreferences.getString(PLACEMARK_MAP, defaultValue);
        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<Placemark>>>() {}.getType();
            placemarkMap = gson.fromJson(json, type);
        }
        return placemarkMap;
    }

    /**
     * TODO DESCRIPTION
     *
     * @param multimap
     */
    public void saveFarmMap(Map<String, List<KmlFile>> multimap){
        Log.d(TAG,"farm map save executed");
        String converted = new Gson().toJson(multimap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FARM_MAP, converted);
        editor.apply();
    }

    /**
     * TODO DESCRIPTION
     *
     * @return
     */
    public Map<String, List<KmlFile>> loadFarmMap(){
        Log.d(TAG,"farm map load executed");
        String defaultValue = new Gson().toJson(new HashMap<String, List<KmlFile>>());
        String json = sharedPreferences.getString(FARM_MAP, defaultValue);
        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<KmlFile>>>() {}.getType();
            farmMap = gson.fromJson(json, type);
        }
        return farmMap;
    }

}
