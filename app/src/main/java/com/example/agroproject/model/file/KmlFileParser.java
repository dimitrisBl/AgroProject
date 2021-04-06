package com.example.agroproject.model.file;

import android.content.Context;

import android.net.Uri;
import android.util.Log;


import com.example.agroproject.model.Placemark;
import com.google.android.gms.maps.model.LatLng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KmlFileParser {

    private Context context;

    private List<Placemark> placemarkList = new ArrayList<>();

    public KmlFileParser(Context context) {
        this.context = context;
    }

    /**
     * TODO DESCRIPTION
     *
     * @param uri
     * @return
     */
    public  String getFileData(Uri uri){
        String data="";
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine())!=null){
                data = data + line + "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("TAG","File not found exception");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG","IOException exception");
        }
        return data;
    }


    /**
     * TODO DESCRIPTION
     *
     * @param fileData
     * @return
     */
    public List<Placemark> parseDataFromFile(String fileData){
        // Create a document from fileData
        Document document = Jsoup.parse(fileData, "", Parser.xmlParser());
        // Iterate a documents
        for(Element currentElement : document.select("Placemark")){
            // Initialize a new coordinates List for each Placemark
            List<String> coordinates = new ArrayList<>();
            // Get the name
            String areaName = String.valueOf(currentElement.select("name"))
                     .replace("<name>","").replace("</name>","").trim();
           // Get the description
            String areaDescription = String.valueOf(currentElement.select("description"))
                    .replace("<description>","").replace("</description>","").trim();
            // Get the coordinates
            coordinates.add(currentElement.select("coordinates").toString()
                    .replace("<coordinates>","").replace("</coordinates>",""));
            // Convert List type from string to LatLng
            List<LatLng> convertedLatLngList = convertToLatLng(coordinates);
            // Create a new Placemark object and put it in placemarkList
            placemarkList.add(new Placemark(areaName, areaDescription, convertedLatLngList));
        }
        // return the List
        return placemarkList;
    }


    /**
     * TODO DESCRIPTION
     *
     * @param coordinates
     * @return
     */
    private List<LatLng> convertToLatLng(List<String> coordinates){
        List<LatLng> convertedList = new ArrayList<>();
        for(int i =0; i< coordinates.size(); i++){
            List<String> oneTrackString = new ArrayList<>(Arrays.asList(coordinates.get(i).split("\\s+")));
            for(int k = 1; k < oneTrackString.size(); k++){
                // Convert current object
                LatLng latLng = new LatLng(Double.parseDouble(oneTrackString.get(k).split(",")[0]),
                         Double.parseDouble(oneTrackString.get(k).split(",")[1]));
                // Add to list
                convertedList.add(latLng);
            }
        }
        return convertedList;
    }
}
