package com.example.agroproject.model.file;

import java.util.Date;

public class KmlFile {

    private String name;
    private String path;
    private String data;
    private String farmName;
    private Long dateToAdded;

    public KmlFile(String name, String path, String data, String farmName, long dateToAdded) {
        this.name = name;
        this.path = path;
        this.data = data;
        this.farmName = farmName;
        this.dateToAdded = dateToAdded;
    }

    public Long getDateToAdded(){
        return dateToAdded;

    }


    public String getFarmName() {
        return farmName;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getData() {
        return data;
    }
}
