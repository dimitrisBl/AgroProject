package com.example.agroproject.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class KmlLocalStorageProvider {

    /** Class TAG */
    private final String TAG = "KmlLocalStorageProvider";

    /** Name of the Shared preferences file */
    private final String PREFS_NAME ="KmlLocalStorage";

    /** Name for the monitoringAreaList in shared preferences file */
    private final String KML_FILE_LIST = "kmlFileList";

    /** SharedPreferences object */
    private SharedPreferences kmlStatePrefs;

    /**
     * This method initialize the KmlLocalStorageProvider object.
     * @param context takes the current context application.
     */
    public KmlLocalStorageProvider(Context context){
        kmlStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This method save the kmlFileList in shared preferences file.
     *
     * kmlFileList has objects of the KmlFile class.
     */
    public void saveKmlFile(List<KmlFile> kmlFileList){
        Log.d(TAG,"Kml file save executed");
        // Instantiate the gson object.
        Gson gson = new Gson();
        //Convert java object as a json string.
        String json = gson.toJson(kmlFileList);

        SharedPreferences.Editor editor = kmlStatePrefs.edit();
        // Put json string in shared preferences.
        editor.putString(KML_FILE_LIST, json);
        editor.commit();
    }


    /**
     * This method receives kmlFileList from the shared preferences saved file.
     * @return the list kmlFileList it contains KmlFile objects from the save file.
     */
    public List<KmlFile> loadKmlFile(){
        Log.d(TAG,"Load kml file from saved file");
        List<KmlFile> kmlFileList = null;
        String serializedObject = kmlStatePrefs.getString(KML_FILE_LIST, null);
        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();
            Type type = new TypeToken<List<KmlFile>>(){}.getType();
            kmlFileList = gson.fromJson(serializedObject, type);
        }
        return kmlFileList;
    }

}
