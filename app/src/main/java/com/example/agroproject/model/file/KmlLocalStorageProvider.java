package com.example.agroproject.model.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.agroproject.model.Placemark;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KmlLocalStorageProvider {

    /** Class TAG */
    private final String TAG = "KmlLocalStorageProvider";

    /** Name of the Shared preferences file */
    private final String PREFS_NAME ="KmlLocalStorage";

    private final String KML_FILE_MAP = "kmlFileMap";

    /** Name for the monitoringAreaList in shared preferences file */
    private final String KML_FILE_LIST = "kmlFileList";

    private final String FARM_MAP = "farmMap";

    /** SharedPreferences object */
    private SharedPreferences sharedPreferences;

    /** Map with KmlFile objects */
    private Map<String, List<Placemark>> kmlFileMap = new HashMap<>();

    /** List with KmlFile objects */
    private List<KmlFile> kmlFileList = new ArrayList<>();


    private Map<String, List<String>> farmMap = new HashMap<>();

    /**
     * This method initialize the KmlLocalStorageProvider object.
     * @param context takes the current context application.
     */
    public KmlLocalStorageProvider(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This method save the famMap in shared preferences file.
     * @param famMap has objects of the KmlFile class.
     */
    public void saveLayers(Map<String, List<Placemark>> famMap){
        Log.d(TAG,"Area save executed");
        String converted = new Gson().toJson(famMap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KML_FILE_MAP, converted);
        editor.apply();
    }


    /**
     * This method load the Map with KmlFile objects from shared preferences.
     * @return the Map kmlFileMap it contains KmlFile objects from the save file.
     */
    public Map<String,List<Placemark>> loadLayers(){
        Log.d(TAG,"load area save executed");
        String defaultValue = new Gson().toJson(new HashMap<String, List<Placemark>>());
        String json = sharedPreferences.getString(KML_FILE_MAP, defaultValue);
        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<Placemark>>>() {}.getType();
            kmlFileMap = gson.fromJson(json, type);
        }
        return kmlFileMap;
    }

    /**
     * TODO DESCRIPTION
     *
     * @param kmlFileList
     */
    public void saveKmlFiles(List<KmlFile> kmlFileList){
        Log.d(TAG,"Kml file save executed");
        String converted = new Gson().toJson(kmlFileList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KML_FILE_LIST, converted);
        editor.apply();
    }

    /**
     * TODO DESCRIPTION
     *
     * @return
     */
    public  List<KmlFile> loadKmlFiles(){
        Log.d(TAG,"load kml file save executed");
        String defaultValue = new Gson().toJson(new ArrayList<KmlFile>());
        String json = sharedPreferences.getString(KML_FILE_LIST, defaultValue);
        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<KmlFile>>() {}.getType();
            kmlFileList = gson.fromJson(json, type);
        }
        return kmlFileList;
    }

    /**
     * TODO DESCRIPTION
     *
     * @param multimap
     */
    public void saveFarmMap(Map<String, List<String>> multimap){
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
    public Map<String, List<String>> loadFarmMap(){
        Log.d(TAG,"load kml file save executed");
        String defaultValue = new Gson().toJson(new HashMap<String, List<String>>());
        String json = sharedPreferences.getString(FARM_MAP, defaultValue);
        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
            farmMap = gson.fromJson(json, type);
        }
        return farmMap;
    }

}
