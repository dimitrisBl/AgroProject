package com.example.agroproject.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

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

    private Map<String, FarmArea> farmAreaMap = new HashMap<>();
    /**
     * Instantiates a new monitoring area.
     *
     * @param name have the name of the area.
     * @param description have the description of the area.
     * @param polygonOptions have the polygonOptions of the area.
     * The PolygonOptions is a representation of the monitoring area in the map.
     */
    public MonitoringArea(String name, String description, PolygonOptions polygonOptions) {
        this.name = name;
        this.description = description;
        this.polygonOptions = polygonOptions;
    }

    /**
     *
     * @param farmAreaMap
     */
    public void setFarmAreaMap(Map<String, FarmArea> farmAreaMap) {
        this.farmAreaMap = farmAreaMap;
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
}
