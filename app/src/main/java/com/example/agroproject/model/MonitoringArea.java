package com.example.agroproject.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

/**
 * The MonitoringArea class will represent a monitoring area in the map
 * will include constructor as well as getter methods for important information about the MonitoringArea objects.
 */

public class MonitoringArea {

    private List<LatLng> latLngList;

    public MonitoringArea(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }



    //    /** The name */
//    private String name;
//
//    /** The description */
//    private String description;
//
//    /** The representation */
//    private PolygonOptions polygonOptions;
//
//    /**
//     * Instantiates a new monitoring area.
//     *
//     * @param name have the name of the area.
//     * @param description have the description of the area.
//     * @param polygonOptions have the polygonOptions of the area.
//     * The PolygonOptions is a representation of the monitoring area in the map.
//     */
//    public MonitoringArea(String name, String description, PolygonOptions polygonOptions) {
//        this.name = name;
//        this.description = description;
//        this.polygonOptions = polygonOptions;
//    }

//    /**
//     * @return the area name.
//     */
//    public String getName() {
//        return name;
//    }
//
//    /**
//     *
//     * @return the area description.
//     */
//    public String getDescription() {
//        return description;
//    }
//
//    /**
//     * PolygonOptions representation the monitoring area in the map.
//     * @return polygonOptions
//     */
//    public PolygonOptions getPolygonOptions() {
//        return polygonOptions;
//    }


    /**
     * TODO METHOD DESCRIPTION
     * @param points
     * @return
     */
    public  LatLng getPolygonCenterPoint(List<LatLng> points) {
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
}
