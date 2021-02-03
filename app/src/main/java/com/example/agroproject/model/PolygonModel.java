package com.example.agroproject.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO CLASS DESCRIPTION
 */
public class PolygonModel {
    // Class TAG
    private final String TAG = "PolygonModel";

    // Name for the Shared preferences file
    private final String PREFS_NAME ="PolygonOptionsSave";

    // SharedPreferences
    private SharedPreferences polygonStatePrefs;

    // Name for the polygonOptionsList in shared preferences file
    private final String POLYGON_OPTIONS_LIST = "PolygonOptionsList";

    // List with PolygonOptions objects
    private List<PolygonOptions> polygonOptionsList = new ArrayList<>();

    /**
     * This method initialize the polygonStatePrefs object.
     * @param context takes the current context application.
     */
    public PolygonModel(Context context){
        polygonStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This method adds polygonOptions in the polygonOptionsList.
     * @param polygonOptions represent a polygon area.
     */
    public void addPolygonOptions(PolygonOptions polygonOptions){
        Log.d(TAG,"PolygonOptions were added in the polygonOptionsList.");
        polygonOptionsList.add(polygonOptions);
    }

    /**
     * This method save polygonOptionsList in shared preferences file.
     */
    public void savePolygonOptions(){
        Log.d(TAG,"PolygonOptions save executed");
        // Instantiate the gson object.
        Gson gson = new Gson();
        //Convert java object as a json string.
        String json = gson.toJson(polygonOptionsList);

        SharedPreferences.Editor editor = polygonStatePrefs.edit();
        // Put json string in shared preferences.
        editor.putString(POLYGON_OPTIONS_LIST, json);
        editor.commit();
    }

    /**
     * This method receives PolygonOptions from the shared preferences saved file.
     * @return the List it contains polygonOptions from the save file.
     */
    public List<PolygonOptions> getSavedPolygonOptions(){
        String serializedObject = polygonStatePrefs.getString(POLYGON_OPTIONS_LIST, null);
        if (serializedObject != null) {
            // Instantiate the gson object.
            Gson gson = new Gson();

            Type type = new TypeToken<List<PolygonOptions>>(){}.getType();
            polygonOptionsList = gson.fromJson(serializedObject, type);
        }
        return polygonOptionsList;
    }
}
