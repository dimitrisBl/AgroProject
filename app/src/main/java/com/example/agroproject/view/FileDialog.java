package com.example.agroproject.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.agroproject.R;
import com.example.agroproject.model.SingletonFilePath;

public class FileDialog extends AppCompatActivity {
    /**
     *  Opens File Explorer to accept a file chosen by the user
     * @param savedInstanceState
     */

    Uri selectedfile;
    SingletonFilePath filepath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         selectedfile=null;
        setContentView(R.layout.activity_file_dialog);
        //Opening file explorer with ACTION_GET_CONTENT
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
        filepath = SingletonFilePath.getInstance();
    }

    /**
     *  After the user chooses a file, we store its location in Uri selectedfile
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123 && resultCode == RESULT_OK) {
            selectedfile = data.getData(); //The uri with the location of the file
            Toast.makeText(this,
                    "file"+selectedfile.getPath(),Toast.LENGTH_LONG).show();
        }
        filepath.setFile(selectedfile);
        finish();
    }
}