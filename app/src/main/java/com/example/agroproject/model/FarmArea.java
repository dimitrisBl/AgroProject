package com.example.agroproject.model;

import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

/**
 * TODO CLASS DESCRIPTION
 */
public class FarmArea {

    private String name;

    private String description;


    private PolygonOptions polygonOptions;

    private List<MonitoringArea> monitoringAreas;

    /**
     *
     *
     * @param name
     * @param description
     */
    public FarmArea(String name, String description, PolygonOptions polygonOptions, List<MonitoringArea> monitoringAreaList) {
        this.name = name;
        this.description = description;
        this.polygonOptions = polygonOptions;

        if(!monitoringAreaList.isEmpty()){
            monitoringAreas = monitoringAreaList;
        }
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return
     */
    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

}
