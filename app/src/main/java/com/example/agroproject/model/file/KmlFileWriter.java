package com.example.agroproject.model.file;

import android.content.Context;
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
import java.util.Collection;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.os.ParcelFileDescriptor.MODE_WORLD_READABLE;

public class KmlFileWriter {

    private Context context;

    public KmlFileWriter(Context context) {
        this.context = context;
    }

    public void fileToWrite(){
      File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        String fileName = "test5.kml";

        try {
            // catches IOException below
            final String kmlstart = "<?xml version=1.0 encoding=UTF-8?> /**" +
                    "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
                    "<Document>\n" +
                    "\t<name>test5.kml</name>\n" +
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
                    "\t</Style>\n" +
                    "\t<Placemark>\n" +
                    "\t\t<name>test6</name>\n" +
                    "\t\t<description>Auth einai h perigrafh ths perioxhss</description>\n" +
                    "\t\t<styleUrl>#m_ylw-pushpin</styleUrl>\n" +
                    "\t\t<Polygon>\n" +
                    "\t\t\t<tessellate>1</tessellate>\n" +
                    "\t\t\t<outerBoundaryIs>\n" +
                    "\t\t\t\t<LinearRing>\n" +
                    "\t\t\t\t\t<coordinates>\n" +
                    "\t\t\t\t\t\t23.02221105852468,40.7385327578248,0 22.96335707756997,43.54000659775764,0 20.65922536944068,41.27936229051883,0 23.02221105852468,40.7385327578248,0 \n" +
                    "\t\t\t\t\t</coordinates>\n" +
                    "\t\t\t\t</LinearRing>\n" +
                    "\t\t\t</outerBoundaryIs>\n" +
                    "\t\t</Polygon>\n" +
                    "\t</Placemark>\n" +
                    "</Document>\n" +
                    "</kml>";
            /* We have to use the openFileOutput()-method
             * the ActivityContext provides, to
             * protect your file from others and
             * This is done for security-reasons.
             * We chose MODE_WORLD_READABLE, because
             *  we have nothing to hide in our file */
            File file = new File(path+"/"+fileName);
            FileOutputStream fis = new FileOutputStream (file);
            OutputStreamWriter osw = new OutputStreamWriter(fis);
            // Write the string to the file
            osw.write(kmlstart);
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
}
