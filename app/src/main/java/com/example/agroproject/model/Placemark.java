package com.example.agroproject.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Placemark {

    private String name;
    private String description;
    private List<LatLng> coordinates;
    private String imageUrl;




    public Placemark(String name, String description, List<LatLng> coordinates, String imageUrl) {
        this.name = name;
        this.description = description;
        this.coordinates = coordinates;

        this.imageUrl = imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
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
