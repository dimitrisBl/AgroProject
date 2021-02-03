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

    private final String PREFS_NAME ="PolygonOptionsSave";

    private final String POLYGON_OPTIONS_LIST = "PolygonOptionsList";

    private SharedPreferences polygonStatePrefs;

    private List<PolygonOptions> polygonOptionsList = new ArrayList<>();

    /**
     *
     * @param context
     */
    public PolygonModel(Context context){
        polygonStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     *
     * @param polygonOptions
     */
    public void addPolygonOptions(PolygonOptions polygonOptions){
        Log.d(TAG,"PolygonOptions were added in the polygonOptionsList.");
        polygonOptionsList.add(polygonOptions);
    }

    /**
     * TODO METHOD DESCRIPTION
     *
     */
    public void savePolygonOptions(){
        Log.d(TAG,"Saving PolygonOptions in shared preferences.");
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
     * TODO METHOD DESCRIPTION
     * @return
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


    public void clearSharedPrefsData(){
        SharedPreferences.Editor editor = polygonStatePrefs.edit();
        editor.clear();
        editor.commit();
    }
}
