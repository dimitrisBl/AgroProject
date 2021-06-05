package com.example.agroproject.model.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.os.ParcelFileDescriptor.MODE_CREATE;
import static android.os.ParcelFileDescriptor.MODE_WORLD_READABLE;

import com.example.agroproject.model.Placemark;
import com.google.android.gms.maps.model.LatLng;

public class KmlFileWriter {

    private Context context;

    public KmlFileWriter(Context context) {
        this.context = context;
    }

    public void fileToWrite(KmlFile kmlFile, List<Placemark> placemarks){
      File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        try {
            // catches IOException below
            final String kmlstart = "<?xml version=1.0 encoding=UTF-8?> /**" +
                    "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
                    "<Document>\n" +
                    "\t<name>"+kmlFile.getName()+"</name>\n" +
                    "\t<StyleMap id=\"m_ylw-pushpin\">\n" +
                    "\t\t<Pair>\n" +
                    "\t\t\t<key>normal</key>\n" +
                    "\t\t\t<styleUrl>#s_ylw-pushpin</styleUrl>\n" +
                    "\t\t</Pair>\n" +
                    "\t\t<Pair>\n" +
                    "\t\t\t<key>highlight</key>\n" +
                    "\t\t\t<styleUrl>#s_ylw-pushpin_hl</styleUrl>\n" +
                    "\t\t</Pair>\n" +
                    "\t</StyleMap>\n" +
                    "\t<Style id=\"s_ylw-pushpin_hl\">\n" +
                    "\t\t<IconStyle>\n" +
                    "\t\t\t<scale>1.3</scale>\n" +
                    "\t\t\t<Icon>\n" +
                    "\t\t\t\t<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\n" +
                    "\t\t\t</Icon>\n" +
                    "\t\t\t<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\n" +
                    "\t\t</IconStyle>\n" +
                    "\t</Style>\n" +
                    "\t<Style id=\"s_ylw-pushpin\">\n" +
                    "\t\t<IconStyle>\n" +
                    "\t\t\t<scale>1.1</scale>\n" +
                    "\t\t\t<Icon>\n" +
                    "\t\t\t\t<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\n" +
                    "\t\t\t</Icon>\n" +
                    "\t\t\t<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\n" +
                    "\t\t</IconStyle>\n" +
                    "\t</Style>\n";

            final String end = "</Document>\n" + "</kml>";
            /* We have to use the openFileOutput()-method
             * the ActivityContext provides, to
             * protect your file from others and
             * This is done for security-reasons.
             * We chose MODE_WORLD_READABLE, because
             *  we have nothing to hide in our file */
            File file = new File(path+"/"+kmlFile.getName());
            FileOutputStream fis = new FileOutputStream (file);
            OutputStreamWriter osw = new OutputStreamWriter(fis);
            // Write the string to the file
            osw.write(kmlstart+addPlacemarks(placemarks)+end);
            /* ensure that everything is
            * really written out and close */
            osw.flush();
            osw.close();
            //Reading the file back...
            /* We have to use the openFileInput()-method
             * the ActivityContext provides.
             * Again for security reasons with
             * openFileInput(...) */
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fIn);
            /* Prepare a char-Array that will
             * hold the chars we read back in. */
            char[] inputBuffer = new char[kmlstart.length()];
            // Fill the Buffer with data from the file
            isr.read(inputBuffer);
            // Transform the chars to a String
            String readString = new String(inputBuffer);
            // Check if we read back the same chars that we had written out
            boolean isTheSame = kmlstart.equals(readString);
            Log.d("  File Reading stuff  ","  success = " + isTheSame);
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("  File Reading stuff  ", e.toString());
        }
    }
    public String addPlacemarks(List<Placemark> placemarks){
        String placemarkTag="";
        for(Placemark placemark : placemarks) {
           placemarkTag +=   "\t<Placemark>\n" +
                    "\t\t<name>"+placemark.getName()+"</name>\n" +
                    "\t\t<description>"+placemark.getDescription()+"</description>\n" +
                    "\t\t<styleUrl>#m_ylw-pushpin</styleUrl>\n" +
                    "\t\t<Polygon>\n" +
                    "\t\t\t<tessellate>1</tessellate>\n" +
                    "\t\t\t<outerBoundaryIs>\n" +
                    "\t\t\t\t<LinearRing>\n" +
                    "\t\t\t\t\t<coordinates>\n" +
                    "\t\t\t\t\t\t"+getStringCoords(placemark.getLatLngList())+"\n" +
                    "\t\t\t\t\t</coordinates>\n" +
                    "\t\t\t\t</LinearRing>\n" +
                    "\t\t\t</outerBoundaryIs>\n" +
                    "\t\t</Polygon>\n" +
                    "\t</Placemark>\n";
        }
        return placemarkTag;
    }

    public String getStringCoords(List<LatLng> coords){
        List<String> stringCoords = new ArrayList<>();

        for(LatLng latLng : coords){
            String obj = latLng.longitude+","+latLng.latitude+",0\t";
            stringCoords.add(obj);
        }
        String placemarkCoordinates="";
        for(int i=0;i<stringCoords.size();i++){
            placemarkCoordinates +=stringCoords.get(i);
        }
       return  placemarkCoordinates;
    }
}
