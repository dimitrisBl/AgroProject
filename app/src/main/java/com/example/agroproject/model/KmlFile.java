package com.example.agroproject.model;


public class KmlFile {


    private String name;
    private String description;
    private String path;

    /**
     *
     * @param path
     */
    public KmlFile(String name, String description, String path) {
        this.name = name;
        this.description = description;
        this.path = path;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }


    /**
     *
     * @return
     */
    public String getPath() {
        return path;
    }
}
