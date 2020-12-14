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

import com.example.agroproject.databinding.ActivityMainBinding;
import com.example.agroproject.databinding.ActivityMapsBinding;

public class MainActivity extends AppCompatActivity {

    private static final int MAP_ACTIVITY_INTENT_CODE = 1;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Initiating Menu XML file (activity_main_top_menu.xml)
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_top_menu, menu);

        // set title alignment for each item is center
        int positionOfMenuItem0 = 0; //or any other postion
        MenuItem item = menu.getItem(positionOfMenuItem0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
        item.setTitle(s);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.yourMap_item:

                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                MainActivity.this.startActivityForResult(intent, MAP_ACTIVITY_INTENT_CODE);

            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method  called after the thread return from the intent service.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MAP_ACTIVITY_INTENT_CODE){
            // If location permission is not granted
            if(resultCode == RESULT_OK){
                Toast.makeText(MainActivity.this, "Accept this permission for use map and other services",Toast.LENGTH_LONG).show();
            }
        }
    }
}