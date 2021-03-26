package com.example.agroproject.model;

import com.google.android.gms.maps.model.PolygonOptions;

public class InnerFarmArea extends FarmArea {

    private FarmArea farmArea;

    public InnerFarmArea(String name, String description, PolygonOptions polygonOptions, FarmArea area) {
        super(name, description, polygonOptions);
        this.farmArea = area;
    }

    public FarmArea getFarmArea() {
        return farmArea;
    }
}
