package com.example.agroproject.model.area;

import com.google.android.gms.maps.model.PolygonOptions;

public class FarmArea {

    /** The name */
    private String name;

    /** The description */
    private String description;

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
    public FarmArea(String name, String description, PolygonOptions polygonOptions) {
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
