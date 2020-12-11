package com.example.agroproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
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

        // set title alignment for each item is center
        int positionOfMenuItem0 = 0; //or any other postion
        MenuItem item = menu.getItem(positionOfMenuItem0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);


        int positionOfMenuItem1 = 1; //or any other postion
        MenuItem item1 = menu.getItem(positionOfMenuItem1);
        SpannableString s1 = new SpannableString(item1.getTitle());
        s1.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s1.length(), 0);
        item1.setTitle(s1);

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
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MAP_ACTIVITY_REQUEST_CODE){
            // If location permission is not granted
            if(resultCode == RESULT_OK){
                Toast.makeText(ActivityMain.this, "Accept this permission for use map and other services",Toast.LENGTH_LONG).show();
            }
        }
    }
}