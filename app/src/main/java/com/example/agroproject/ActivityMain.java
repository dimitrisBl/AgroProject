package com.example.agroproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ActivityMain extends AppCompatActivity {

    private static final int MAP_ACTIVITY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.yourMap_item:

                Intent intent = new Intent(ActivityMain.this, MapActivity.class);
                ActivityMain.this.startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        This method  called after the thread return from the intent service
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(requestCode == 1){

            if(resultCode == RESULT_CANCELED){

                Toast.makeText(ActivityMain.this, "Accept this permission for use map and other services",Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}