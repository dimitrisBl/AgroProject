package com.example.agroproject.model;

import android.util.Log;

import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class PolygonModel {

    private static final String TAG = "PolygonModelClass";

    private static List<PolygonOptions> polygonOptionsList = new ArrayList<>();
    /**
     *
     * @param polyOptions
     */
    public static void addPolygon(PolygonOptions polyOptions){
        Log.d(TAG, "addPolygon method performed");
        polygonOptionsList.add(polyOptions);
    }

    /**
     *
     * @return
     */
    public static List<PolygonOptions> getPolygonOptions() {
        return polygonOptionsList;
    }
}
