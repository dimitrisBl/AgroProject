package com.example.agroproject.model.file;

public class KmlFile {

    private String name;
    private String path;
    private String data;
    private String farmName;

    public KmlFile(String name, String path, String data, String farmName) {
        this.name = name;
        this.path = path;
        this.data = data;
        this.farmName = farmName;
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
