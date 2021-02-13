package com.example.agroproject.model;

/**
 * This class receives realtime location changes of the device from firebase db
 * TODO CLASS DESCRIPTION
 */
public class MyLocation {

    /** Current latitude of the device */
    private double latitude;

    /** Current longitude of the device */
    private double longitude;

    /**
     *
     *
     * @param latitude
     * @param longitude
     */
    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyLocation() {}


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
