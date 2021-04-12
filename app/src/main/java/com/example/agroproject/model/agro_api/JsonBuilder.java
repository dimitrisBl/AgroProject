package com.example.agroproject.model.agro_api;

import com.example.agroproject.model.Placemark;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonBuilder {

    /**
     * TODO DESCRIPTION
     *
     * @param placemarkList
     * @return
     */
    public  static JSONObject build(List<Placemark> placemarkList){

        JSONObject jsonObject = new JSONObject();

        JSONObject geometryObject = new JSONObject();

        JSONObject geoJsonObject = new JSONObject();

        JSONObject propertiesObject = new JSONObject();

        JSONArray coords = new JSONArray();


        for(Placemark placemark : placemarkList) {

            JSONArray coordinatesJsonArray   = new JSONArray();

            for (LatLng latLng : placemark.getLatLngList()) {
                // BUILD JSON ARRAY
                JSONArray innerArray = null;
                try {
                    innerArray = new JSONArray(
                            "[\n" +
                                    latLng.longitude + ", " +
                                    latLng.latitude +
                                    "          ]"
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                coordinatesJsonArray.put(innerArray);
            }
            coords.put(coordinatesJsonArray);
        }

        try {
            jsonObject.put("name","");
            geoJsonObject.put("type","Feature");
            geoJsonObject.put("properties",propertiesObject);
            geoJsonObject.put("geometry",geometryObject);
            geometryObject.put("type","Polygon");
            geometryObject.put("coordinates",coords);
            jsonObject.put("geo_json",geoJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
