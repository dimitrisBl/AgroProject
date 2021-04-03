package com.example.agroproject.model.file;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Placemark {

    private String name;
    private String description;
    private List<LatLng> coordinates;


    public Placemark(String fileName, String description, List<LatLng> coordinates) {
        this.name = fileName;
        this.description = description;
        this.coordinates = coordinates;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<LatLng> getLatLngList() {
        return coordinates;
    }

}