package com.example.agroproject.model.area;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

public class InnerArea {

    private String name;
    private String description;
    private PolygonOptions polygonOptions;
    private FarmArea farmArea;

    public InnerArea(String name, String description, PolygonOptions polygonOptions, FarmArea farmArea) {
        this.name = name;
        this.description = description;
        this.polygonOptions = polygonOptions;
        this.farmArea = farmArea;
    }


    public FarmArea getFarmArea() {
        return farmArea;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

}
