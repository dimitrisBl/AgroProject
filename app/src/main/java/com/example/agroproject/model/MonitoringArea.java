package com.example.agroproject.model;


import com.google.android.gms.maps.model.PolygonOptions;

/**
 * The MonitoringArea class will represent a monitoring area in the map
 * will include constructor as well as getter methods for important information about the MonitoringArea objects.
 */

public class MonitoringArea {
    /** The name. */
    private String name;

    /** The description. */
    private String description;

    /** The representation . */
    private PolygonOptions polygonOptions;

    /**
     * Instantiates a new monitoring area.
     * TODO na allaskw to thn param polygonOptions kai na thn kanw polygon, me boleuei perissotero na krataw ena polygon para ta options tou
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
