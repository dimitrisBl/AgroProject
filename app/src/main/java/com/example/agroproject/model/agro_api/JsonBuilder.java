package com.example.agroproject.model.agro_api;

import com.example.agroproject.model.Placemark;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonBuilder {
    /** Class TAG */
    private static final String TAG = "JsonBuilder";

    /**
     * Create a JSONObject for each Placemark to contained in placemarkList
     *
     * @param placemarkList has the Placemark objects
     * @return a List with JSONObjects
     */
     public static List<JSONObject> build(List<Placemark> placemarkList){

        List<JSONObject> jsonObjectList = new ArrayList<>();

        for(Placemark placemark : placemarkList){
            // Initialize the Json Objects - Json Arrays for each placemark
            JSONObject jsonObject = new JSONObject();
            JSONObject geometryObject = new JSONObject();
            JSONObject geoJsonObject = new JSONObject();
            JSONArray coordinatesJsonArray = new JSONArray();
            JSONObject propertiesObject = new JSONObject();
            // Build a jsonArray for each coordinate of the Placemark
            for(LatLng latLng : placemark.getLatLngList()){
                JSONArray innerArray  = null;
                try {
                    innerArray = new JSONArray(
                            "[\n" +
                                    latLng.longitude+", "+
                                    latLng.latitude+
                                    "          ]"
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Put each inner jsonArray on coordinatesJsonArray
                coordinatesJsonArray.put(innerArray);
            }

            try {
                // Build a jsonObject
                jsonObject.put("name", placemark.getName());
                geoJsonObject.put("type","Feature");
                geoJsonObject.put("properties", propertiesObject);
                geoJsonObject.put("geometry",geometryObject);
                geometryObject.put("type","Polygon");
                geometryObject.put("coordinates",new JSONArray().put(coordinatesJsonArray));
                jsonObject.put("geo_json",geoJsonObject);

                // add jsonObject to List
                jsonObjectList.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObjectList;
    }
}
