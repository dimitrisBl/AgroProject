package com.example.agroproject.model.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.agroproject.model.Placemark;
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

    /** Name of the placemarkMap Map in shared preferences stored */
    private final String KML_FILE_LIST = "kmlFiles";

    /** Name of the farmMap Map in shared preferences stored */
    private final String PLACEMARK_LIST = "placemarks";

    /** SharedPreferences object */
    private SharedPreferences sharedPreferences;

    /** Map with Placemark objects */
    private Map<KmlFile, List<Placemark>> kmlFileMap = new HashMap<>();

    /** List with KmlFile objects */
    private List<KmlFile> kmlFileList = new ArrayList<>();

    /** List with List of placemark objects */
    private List<List<Placemark>> placemarkList = new ArrayList<>();

    /**
     * This method initialize the KmlLocalStorageProvider object.
     * @param context takes the current context of the application.
     */
    public KmlLocalStorageProvider(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This method save the kmlFileMap in files of shared preferences.
     * @param kmlFileMap has the data to be saved.
     */
    public void saveKmlFileMap(Map<KmlFile, List<Placemark>> kmlFileMap){
        Log.d(TAG,"Shared preferences save executed");
        // Initialize a kmlFileList
        kmlFileList = new ArrayList<>();
        // Initialize a placemarkList
        placemarkList = new ArrayList<>();
        // Pass the keys from kmlFileMap in kmlFileList
        kmlFileList.addAll(kmlFileMap.keySet());
        // Pass the values from KmlFileMap in placemarkList
        placemarkList.addAll(kmlFileMap.values());
        // Convert data from KmlFileList in String type
        String kmlFilesConverted = new Gson().toJson(kmlFileList);
        // Convert data from placemarkList in String type
        String placemarksConverted = new Gson().toJson(placemarkList);
        // Create editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Put kmlFilesConverted data on shared preferences stored
        editor.putString(KML_FILE_LIST, kmlFilesConverted);
        // Put placemarksConverted data on shared preferences stored
        editor.putString(PLACEMARK_LIST, placemarksConverted);
        editor.apply();
    }


    /**
     * This method load the Map with KmlFile objects from shared preferences.
     * @return the Map kmlFileMap it has key with  KmlFile objects and
     * List of Placemark objects for the value.
     */
    public Map<KmlFile, List<Placemark>> loadKmlFileMap(){
        Log.d(TAG,"Shared preferences load executed");
        // Load kmlFileList
        String kmlDefaultValue = new Gson().toJson(new ArrayList<KmlFile>());
        String kmlFileListJson = sharedPreferences.getString(KML_FILE_LIST, kmlDefaultValue);
        // Pass data from kmlFileListJson on kmlFileList
        if(kmlFileListJson != null){
            Gson gson = new Gson();
            Type type = new TypeToken< List<KmlFile>>() {}.getType();
            kmlFileList = gson.fromJson(kmlFileListJson, type);
        }

        // Load placemarkList
        String placemarkDefaultValue = new Gson().toJson(new ArrayList<Placemark>());
        String placemarkListJson = sharedPreferences.getString(PLACEMARK_LIST, placemarkDefaultValue);
        // Pass data from placemarkListJson on placemarkList
        if(placemarkListJson!=null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<List<Placemark>>>() {}.getType();
            placemarkList = gson.fromJson(placemarkListJson, type);
        }

        // Synthesize all loading data in the kmlFileMap
        for(int i = 0;i < kmlFileList.size(); i++){
            kmlFileMap.put(kmlFileList.get(i), placemarkList.get(i));
        }
        return kmlFileMap;
    }
}
