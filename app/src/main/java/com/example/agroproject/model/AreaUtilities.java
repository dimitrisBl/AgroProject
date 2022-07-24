package com.example.agroproject.model;

import android.util.Log;

import com.example.agroproject.model.file.KmlFile;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaUtilities {

    private static Placemark outsiderArea;

    /**
     * Detect the center LatLng object of an area.
     *
     * @param points is a List with LatLng objects of an area.
     * @return latLng object.This object is the center location of area.
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
     * @param latLng has the center location of new area
     * @param placemarkList has all saved placemarks
     * @return true if the point LatLng param its inside in some placemark.
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


    /**
     * Classifier of the placemarks.
     *
     *
     * @param kmlFileMap has all saved data of the application
     * @return Map with outer placemark as key and List with the inner placemarks as value.
     */
    public static Map<Placemark,List<Placemark>> placemarkClassification(Map<KmlFile,List<Placemark>> kmlFileMap){

        Map<Placemark,List<Placemark>> placemarkMap = new HashMap<>();
        // Get all placemarks from the kmlFileMap
        List<Placemark> placemarks  = new ArrayList<>();
        for (Map.Entry<KmlFile, List<Placemark>> entry : kmlFileMap.entrySet()) {
            for (Placemark placemark : entry.getValue()) {
                placemarks.add(placemark);
            }
        }

        for (Placemark placemark : placemarks){

            // Check if the current placemark located inside in other placemark
            boolean isInnerArea = isInnerArea(placemark,placemarks);

            if (isInnerArea){
                for (Map.Entry<Placemark, List<Placemark>> entry : placemarkMap.entrySet()){
                    if (entry.getKey().equals(outsiderArea)){
                        // Append value in the existing key
                        entry.getValue().add(placemark);
                    }
                }
            } else{
                // Create a new entry
                placemarkMap.put(placemark,new ArrayList<>());
            }
        }
        return placemarkMap;
    }


    public static boolean isInnerArea(Placemark currentPlacemark, List<Placemark> placemarkList){
        // Its try if the current placemark located inside in other
        boolean isInnerArea = true;
        for (LatLng latLng : currentPlacemark.getLatLngList()){
            if (detectInnerArea(latLng,placemarkList) == false){
                // ALl LatLng points of a placemark
                // must be inside in some other placemark to be inner area
                isInnerArea = false;
                break;
            }

//            boolean firstCondition = getAreaCenterPoint(currentPlacemark.getLatLngList()).latitude==getAreaCenterPoint(getOutsiderArea().getLatLngList()).latitude;
//            boolean secondCondition = getAreaCenterPoint(currentPlacemark.getLatLngList()).longitude==getAreaCenterPoint(getOutsiderArea().getLatLngList()).longitude;
//
//            if (firstCondition && secondCondition){
//                isInnerArea = false;
//                break;
//            }


            if (currentPlacemark.getLatLngList().size() == getOutsiderArea().getLatLngList().size()){

                for (int i =0;i < currentPlacemark.getLatLngList().size();i++){

                    boolean firstCondition = currentPlacemark.getLatLngList().get(i).latitude == getOutsiderArea().getLatLngList().get(i).latitude;
                    boolean secondCondition =  currentPlacemark.getLatLngList().get(i).longitude == getOutsiderArea().getLatLngList().get(i).longitude;

                    if (firstCondition && secondCondition){ isInnerArea = false; }
                }
            }

        }
        return isInnerArea;
    }
}
