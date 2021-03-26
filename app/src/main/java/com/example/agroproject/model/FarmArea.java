package com.example.agroproject.model;

import com.google.android.gms.maps.model.PolygonOptions;

/**
 * OSO O USER DIMhOURGEI POLyGONA ta peranw ola sthn idia lista pou briskete topika mesa sthn create area
 * KAI THA TA EMFANIZW EPANW STON XARTI, STHN ON_STOP ~ ON_PAUSE THA KANW TO SAVE
 *
 */
public class FarmArea {

    private String name;

    private String description;

    private PolygonOptions polygonOptions;

    public FarmArea(String name, String description, PolygonOptions polygonOptions) {
        this.name = name;
        this.description = description;
        this.polygonOptions = polygonOptions;
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
