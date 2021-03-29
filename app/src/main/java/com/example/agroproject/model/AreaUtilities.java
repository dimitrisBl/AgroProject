package com.example.agroproject.model;


import android.util.Log;
import com.example.agroproject.model.area.FarmArea;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class AreaUtilities {

    private static FarmArea farmArea;

    /**
     * TODO METHOD DESCRIPTION
     * @param points is a List with LatLng objects of an area.
     * @return latLng object. This object have the center location of area.
     */
    public static LatLng getPolygonCenterPoint(List<LatLng> points) {
        double[] centroid = { 0.0, 0.0 };

        for (int i = 0; i < points.size(); i++) {
            centroid[0] += points.get(i).latitude;
            centroid[1] += points.get(i).longitude;
        }

        int totalPoints = points.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;
        Log.d("mesa edw re", "efafa");
        LatLng center = new LatLng(centroid[0], centroid[1]);

        return center;
    }


    /**
     *
     * @param latLng has the center location of current area
     * @param farmAreaList
     *
     */
    public static boolean detectInnerArea(LatLng latLng, List<FarmArea> farmAreaList){
        boolean innerArea = false;
            for (FarmArea area : farmAreaList) {
                //Don't check the same polygon
                if (!latLng.equals(getPolygonCenterPoint(area.getPolygonOptions().getPoints()))) {
                    innerArea = PolyUtil.containsLocation(latLng,
                            area.getPolygonOptions().getPoints(), false);
                    //The farmArea in which our current polygon exists
                    farmArea = area;
                    if(innerArea){
                        return  innerArea;
                    }
                }
            }
        return innerArea;
    }

    /**
     *
     * @return
     */
    public static FarmArea getFarmArea() {
        return farmArea;
    }
}
