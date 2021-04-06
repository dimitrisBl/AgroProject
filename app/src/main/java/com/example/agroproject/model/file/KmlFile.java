package com.example.agroproject.model.file;

import android.net.Uri;

public class KmlFile {

    private String name;
    private String path;
    private String data;


    public KmlFile(String name, String path, String data) {
        this.name = name;
        this.path = path;
        this.data = data;
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
