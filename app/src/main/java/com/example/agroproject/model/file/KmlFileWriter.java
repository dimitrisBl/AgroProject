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

    private final String kmlstartBeforeName = "<?xml version=1.0 encoding=UTF-8?> /**" +
            "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
            "<Document>\n" +
            "\t<name>";
    private final String kmlstartAfterName = "</name>\n" +
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

    private final String end = "</Document>\n" + "</kml>";

    private final String overlayTagBeforeIcon = "<GroundOverlay>\n" +
            "      <name>Large-scale overlay on terrain</name>\n" +
            "    <description>Overlay shows Mount Etna erupting\n" +
            "    on July 13th, 2001.</description>\n" +
            "      <Icon>\n" +
            "        <href>";
    private final String overlayTagAftericon = "</href>\n" +
            "      </Icon>\n" +"<gx:LatLonQuad>\n"+
                        "<coordinates>";
    private final String overlayTagAfterCoords=
            "        </coordinates>\n" +
            "       </gx:LatLonQuad>\n" +
            "    </GroundOverlay>";

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

            StringBuilder builder = new StringBuilder(kmlstartBeforeName);
            builder.append(String.format(kmlFile.getName()));
            builder.append(String.format(kmlstartAfterName));
            builder.append(String.format(addPlacemarks(placemarks)));
            builder.append(String.format(getOverlay(placemark)));
            builder.append(String.format(end));
            createFile(builder.toString(), kmlFile.getName());

    }

    /** Export file with the specified data and name
     *
     * @param fileData Data of the file
     * @param filename Name of the file
     */
    private void createFile(String fileData, String filename){
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

    /** Create the Overlay Tag
     *
     * @param placemark Placemark that the overlay is going to be displayed on
     * @return Overlay Tag
     */
    private String getOverlay(Placemark placemark){
      String OverlayTag=overlayTagBeforeIcon;
      String coords = getStringCoords(placemark.getLatLngList());
      OverlayTag += placemark.getImageUrl() + overlayTagAftericon + coords + overlayTagAfterCoords;
      return OverlayTag;
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

    /**
     * Create the Coordinates tag
     *
     * @param coords coordinates for a placemark
     * @return coordinates tag
     */
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
