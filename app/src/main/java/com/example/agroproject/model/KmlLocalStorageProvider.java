package com.example.agroproject.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class KmlLocalStorageProvider {

    /** Class TAG */
    private final String TAG = "KmlLocalStorageProvider";

    /** Name of the Shared preferences file */
    private final String PREFS_NAME ="KmlLocalStorage";

    /** Name for the monitoringAreaList in shared preferences file */
    private final String KML_FILE_LIST = "kmlFileList";

    /** SharedPreferences object */
    private SharedPreferences sharedPreferences;

    /** Map with KmlFile objects */
    private Map<String, KmlFile> kmlFileMap = new HashMap<>();

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
    public void saveFile(Map<String, KmlFile> famMap){
        Log.d(TAG,"Area save executed");
        String converted = new Gson().toJson(famMap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KML_FILE_LIST, converted);
        editor.apply();
    }


    /**
     * This method load the Map with KmlFile objects from shared preferences.
     * @return the Map kmlFileMap it contains KmlFile objects from the save file.
     */
    public Map<String, KmlFile> loadFile(){
        Log.d(TAG,"load area save executed");
        String defaultValue = new Gson().toJson(new HashMap<String, MonitoringArea>());
        String json = sharedPreferences.getString(KML_FILE_LIST, defaultValue);
        if(json != null){
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, KmlFile>>() {}.getType();
            kmlFileMap = gson.fromJson(json, type);
        }
        return kmlFileMap;
    }
}
