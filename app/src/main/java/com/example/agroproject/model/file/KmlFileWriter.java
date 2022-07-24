package com.example.agroproject.model.file;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import com.example.agroproject.model.Placemark;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * TODO CLASS DESCRIPTION
 */
public class KmlFileWriter {
    /** Context */
    private Context context;

    private final String kmlstart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
            "<Document>\n";

    private final String styleTag =
                "\t<Style id=\"placemarkStyle\">\n" +
                "      <LineStyle>\n" +
                "\t   <width>2</width>\n" +
                "        <color>ff0000ff</color>\n" +
                "      </LineStyle>\n" +
                "      <PolyStyle>\n" +
                "        <color>7f00ff00</color>\n" +
                "\t\t<opacity>0</opacity>\n" +
                "      </PolyStyle>\n" +
                "\t</Style>\n";


    private final String kmlEnd = "</Document>\n"+"</kml>";



    /**
     * Constructor
     * <p>Instantiates a new KmlFileWriter object</p>
     *
     * @param context the current context of application
     */
    public KmlFileWriter(Context context) {
        this.context = context;
    }


    /** Create the kml filedata using StringBuilder. It sends the data to the createFile() method where
     * the kml file is exported
     * @param kmlFile The kml file that is going to get exported
     * @param placemarks  The placemarks contained in the kml file
     * @param placemark    The placemark that the fround overlay is going to be displayed on
     */
    public void fileToWrite(KmlFile kmlFile, List<Placemark> placemarks,Placemark placemark){
            StringBuilder builder = new StringBuilder(kmlstart);
            builder.append(String.format(addFileName(kmlFile.getName())));
            builder.append(String.format(styleTag));
            builder.append(String.format(addPlacemarks(placemarks)));
            if (placemark.getImageUrl()!=null){
                builder.append(String.format(addGroundOverlay(placemark)));
            }
            builder.append(String.format(kmlEnd));
            exportKmlFile(builder.toString(), kmlFile.getName());
    }


    /** Export file with the specified data and name
     *
     * @param fileData Data of the file
     * @param filename Name of the file
     */
    private void exportKmlFile(String fileData, String filename){
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try {
            // catches IOException below

            /* We have to use the openFileOutput()-method
             * the ActivityContext provides, to
             * protect your file from others and
             * This is done for security-reasons.
             * We chose MODE_WORLD_READABLE, because
             *  we have nothing to hide in our file */
            File file = new File(path+"/"+filename);
            FileOutputStream fis = new FileOutputStream (file);
            OutputStreamWriter osw = new OutputStreamWriter(fis);
            // Write the string to the file
            osw.write(fileData);
            /* ensure that everything is
             * really written out and close */
            osw.flush();
            osw.close();


            if(file.exists()){
                Log.d("File state","The file "+filename+" was created in path "+path);
            }else{
                Log.d("File state","File was not created.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String addFileName(String fileName){
        return "\t<name>"+fileName+"</name>\n";
    }

    /**
     * Create the Overlay Tag
     *
     * @param placemark of Placemark that the overlay is going to be displayed on
     * @return Overlay Tag
     */
    private String addGroundOverlay(Placemark placemark){
        double north = 0;
        double south = 99.9999999999999999999;
        double east = 0;
        double west = 99.99999999999999999999;

        for (LatLng latLng : placemark.getLatLngList()) {
            if (north < latLng.latitude){ north = latLng.latitude; }
            if (south > latLng.latitude ){ south = latLng.latitude; }

            if (east< latLng.longitude) {east = latLng.longitude; }
            if (west>latLng.longitude) {west = latLng.longitude; }
        }

        String OverlayTag=
                "\t<GroundOverlay>\n" +
                          "    \t<name>NDVI Overlay</name>\n" +
                          "    \t<description>\n" +
                                "\t\t\tNormalized Difference Vegetation Index\n" +
                          "     \t</description>";

        String iconTag =
                " \n\t\t<Icon>\n" +
                "    \t\t<href>"+placemark.getImageUrl()+"</href>"+"\n" +
                " \t\t</Icon>";

        String latLonBoxtag =
                "   \t\t<LatLonBox>\n" +
                "        \t<north>"+north+"</north>\n" +
                "        \t<south>"+south+"</south>\n" +
                "        \t<east>"+east+"</east>\n" +
                "        \t<west>"+west+"</west>\n" +
                "        \t<rotation>-0.1556640799496235</rotation>\n" +
                "   \t\t</LatLonBox>";


        return OverlayTag + iconTag+"\n"+latLonBoxtag+"\n"+"\t</GroundOverlay>\n";
    }


    /**
     * Create the Placemark tag
     *
     * @param placemarks placemarks of the specified file
     * @return Placemark Tag
     */
    private String addPlacemarks(List<Placemark> placemarks){
        String placemarkTag="";
        for(Placemark placemark : placemarks) {
           placemarkTag +=   "\t<Placemark>\n" +
                    "\t\t<name>"+placemark.getName()+"</name>\n" +
                    "\t\t<description>"+placemark.getDescription()+"</description>\n" +
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
                    "\t<styleUrl>#placemarkStyle</styleUrl>"+
                    "\n\t</Placemark>\n";
        }
        return placemarkTag;
    }

    /**
     * Create the Coordinates tag
     *
     * @param coords coordinates for a placemark
     * @return coordinates tag
     */
    private String getStringCoords(List<LatLng> coords){
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
