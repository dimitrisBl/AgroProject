package com.example.agroproject.model;

import android.content.Context;

import android.net.Uri;
import android.util.Log;


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

    public KmlFileParser(Context context) {
        this.context = context;
    }


    public  String parseFile(Uri uri){
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


    private  List<String> trackString = new ArrayList<>();

    private  List<List<LatLng>> allTracks = new ArrayList<>();


    public  List<List<LatLng>> findCoordinatesSegment(String fileData){

        Document document = Jsoup.parse(fileData, "", Parser.xmlParser());
        for(Element currentElement : document.select("coordinates")){
            trackString.add(currentElement.toString()
                    .replace("<coordinates>", "").replace("</coordinates>", ""));
        }

        for(int i =0; i < trackString.size(); i++){
            List<LatLng> oneTrack = new ArrayList<>();
            List<String> oneTrackString = new ArrayList<>(Arrays.asList(trackString.get(i).split("\\s+")));
            for (int k = 1; k < oneTrackString.size(); k++) {
                LatLng latLng = new LatLng(Double.parseDouble(oneTrackString.get(k).split(",")[0]),
                        Double.parseDouble(oneTrackString.get(k).split(",")[1]));
                oneTrack.add(latLng);
            }
            allTracks.add(oneTrack);
        }
        // return
        return allTracks;
    }
}
