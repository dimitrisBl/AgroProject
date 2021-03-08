package com.example.agroproject.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The MonitoringArea class will represent a monitoring area in the map
 * will include constructor as well as getter methods for important information about the MonitoringArea objects.
 */

public class MonitoringArea {
    /** The name */
    private String name;

    /** The description */
    private String description;

    /** */
    private String farmName;

    /** The representation */
    private PolygonOptions polygonOptions;

    /**
     * Instantiates a new monitoring area.
     *
     * @param name have the name of the area.
     * @param description have the description of the area.
     * @param polygonOptions have the polygonOptions of the area.
     * The PolygonOptions is a representation of the monitoring area in the map.
     */
    public MonitoringArea(String name, String description, PolygonOptions polygonOptions, String farmName) {
        this.name = name;
        this.description = description;
        this.polygonOptions = polygonOptions;
        this.farmName=farmName;
    }

    /**
     *
     * @param name
     * @param description
     * @param polygonOptions
     */
    public MonitoringArea(String name, String description, PolygonOptions polygonOptions){
        this.name = name;
        this.description = description;
        this.polygonOptions = polygonOptions;
    }


    /**
     * @return the area name.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the area description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * PolygonOptions representation the monitoring area in the map.
     * @return polygonOptions
     */
    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

    /**
     *
     * @return the name of the farm to which the area belongs.
     */
    public String getFarmName() {
        return farmName;
    }
}
