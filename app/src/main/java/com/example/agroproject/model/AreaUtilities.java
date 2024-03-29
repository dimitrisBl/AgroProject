package com.example.agroproject.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class AreaUtilities {

    private static Placemark outsiderArea;

    /**
     * TODO METHOD DESCRIPTION
     * @param points is a List with LatLng objects of an area.
     * @return latLng object. This object have the center location of area.
     */
    public static LatLng getAreaCenterPoint(List<LatLng> points) {
        double[] centroid = { 0.0, 0.0 };

        for (int i = 0; i < points.size(); i++) {
            centroid[0] += points.get(i).latitude;
            centroid[1] += points.get(i).longitude;
        }
        int totalPoints = points.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;
        LatLng center = new LatLng(centroid[0], centroid[1]);
        return center;
    }


    /**
     *
     * @param latLng has the center location of new  area
     * @param placemarkList
     *
     */
    public static boolean detectInnerArea(LatLng latLng, List<Placemark> placemarkList){
        for (Placemark placemark : placemarkList) {
            boolean isInnerArea = PolyUtil.containsLocation
                    (latLng, placemark.getLatLngList(), false);
            if(isInnerArea){
                // The farm area in which our current polygon exists
                outsiderArea = placemark;
                return true;
            }
        }
        return false;
    }


    public static Placemark getOutsiderArea() {
        return outsiderArea;
    }
}
