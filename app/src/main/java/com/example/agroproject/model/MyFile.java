package com.example.agroproject.model;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MyFile {

    private String dataFromFile;

    private static MyFile instance = null;

    /**
     *
     * @return
     */
    public static MyFile getInstance() {
        if (instance == null) { //if there is no instance available... create new one
            instance = new MyFile();
        }
        return instance;
    }

    /**
     *
     * @param dataFromFile
     */
    public void setDataFromFile(String dataFromFile) {
        this.dataFromFile = dataFromFile;
    }

    /**
     *
     * @return
     */
    public String getDataFromFile() {
        return dataFromFile;
    }
}
