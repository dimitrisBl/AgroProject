package com.example.agroproject.model.area;


import com.google.android.gms.maps.model.PolygonOptions;

public class InnerArea extends Area {



    private String name;
    private String description;
    private PolygonOptions polygonOptions;

    public InnerArea(String name, String description, PolygonOptions polygonOptions) {
        this.name = name;
        this.polygonOptions=polygonOptions;
        this.description=description;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

}
