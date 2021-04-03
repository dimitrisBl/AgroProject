package com.example.agroproject.model.file;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;

public class KmlFileWriter {

    private Context context;

    public KmlFileWriter(Context context) {
        this.context = context;
    }

    public void fileToWrite(Uri uri){
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            String data = "Write this data in the file";
            byte b[] = data.getBytes();//converting string into byte array
            outputStream.write(b);
            outputStream.close();
            Log.d("DONE","Success writing dataaa");
        } catch (FileNotFoundException e) {
            Log.d("Exception","FileNotFound Exception");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Exception","IOException");
            e.printStackTrace();
        }
    }

}
