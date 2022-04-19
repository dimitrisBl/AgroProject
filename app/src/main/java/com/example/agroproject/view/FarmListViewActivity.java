package com.example.agroproject.view;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityListViewBinding;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.view.adapters.FarmListViewAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class FarmListViewActivity extends AppCompatActivity {

    /** Activity view binding */
    private ActivityListViewBinding binding;

    /** ListView */
    private ListView listView;

    /** Adapter for ListView */
    private FarmListViewAdapter farmListViewAdapter;

    /** Adapter for drop down menu */
    private ArrayAdapter<String> dropDownAdapter;

    /** KmlLocalStorageProvider */
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    /** kmlFile Map */
    private Map<KmlFile, List<Placemark>> kmlFileMap = new HashMap<>();

    private int MAP_ACTIVITY_INTENT_CODE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        //  Load the kmlFile Map from shared preferences storage
        kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
        // Initialize ui components
        initializeComponents();
    }


    /**
     * Initialize UI components
     */
    private void initializeComponents(){
        // Set data to the listViewAdapter from shared preferences
        farmListViewAdapter = new FarmListViewAdapter(new ArrayList<>(kmlFileMap.keySet()));
        //----- ListView ----- //
        listView = binding.listView;
        listView.setAdapter(farmListViewAdapter);
        // ListView item click listener
        listView.setOnItemClickListener(listViewItemClickListener);
        // ListView item long click listener
        listView.setOnItemLongClickListener(listViewItemLongClickEvent);

        //---- AutoCompleteTextView ----- //
        // Add data in the AutoCompleteTextView dropDown
        fillDropDownData(kmlFileMap.keySet());
        // AutoCompleteTextview item click listener
        binding.autoCompleteTextView.setOnItemClickListener(autoCompleteTextViewItemClickEvent);
        // AutoCompleteTextView text change event listener
        //binding.autoCompleteTextView.addTextChangedListener(autoCompleteTextViewTextChangedEvent);
    }

    /**
     * Event handler for click on kml file of list view
     */
    private AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
            // The name of the clicked kmlFile
            String kmlFileName = farmListViewAdapter.getItem(index).getName();
            // Start an intent on the MapActivity
            Intent mapActivityIntent = new Intent(FarmListViewActivity.this, MapActivity.class);
            mapActivityIntent.setAction("Find the kmlFile that clicked");
            mapActivityIntent.putExtra("kmlFileName", kmlFileName);
            //startActivity(mapActivityIntent);
            startActivityForResult(mapActivityIntent, MAP_ACTIVITY_INTENT_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MAP_ACTIVITY_INTENT_CODE){
            // Refresh data
            kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
            kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
            initializeComponents();
        }
    }

    /**
     * Event handler for Long click on kml file of list view
     */
    private AdapterView.OnItemLongClickListener listViewItemLongClickEvent = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
            // Show dialog
            new AlertDialog.Builder(FarmListViewActivity.this)
                    .setTitle("Are you sure?")
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setMessage("Do you want to delete this item?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Get the kml file which clicked
                            KmlFile kmlFile = farmListViewAdapter.getItem(index);
                            // Remove KmlFile from placemark Map
                            kmlFileMap.remove(kmlFile);
                            // Save the changes in shared preferences
                            kmlLocalStorageProvider.saveKmlFileMap(kmlFileMap);
                            // Refresh  ListView for the ui changes about the KmlFile removed
                            farmListViewAdapter = new FarmListViewAdapter(new ArrayList<>(kmlFileMap.keySet()));
                            listView.setAdapter(farmListViewAdapter);
                            // Refresh drop down menu for the ui changes about the KmlFile removed
                            fillDropDownData(kmlFileMap.keySet());
                            // Show message
                            Toast.makeText(FarmListViewActivity.this, "The file "
                                    +kmlFile.getName()+" was removed",Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
            return true;
        }
    };

    /**
     * Event handler for item click event of AutoCompleteTextView
     */
    private AdapterView.OnItemClickListener autoCompleteTextViewItemClickEvent = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
            // Get item which clicked
            String clickedItem = dropDownAdapter.getItem(index);
            // Get all kml files from kmlFile Map who have same farm name wth the clicked item
            List<KmlFile> kmlFiles = kmlFileMap.keySet().stream().
                    filter(element -> element.getFarmName().equals(clickedItem)).collect(Collectors.toList());
            // Refresh the ui
            farmListViewAdapter = new FarmListViewAdapter(kmlFiles);
            listView.setAdapter(farmListViewAdapter);
        }
    };

//
//    /**
//     * Event handler for text changed event of AutoCompleteTextView
//     */
//    private android.text.TextWatcher autoCompleteTextViewTextChangedEvent = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public void onTextChanged(CharSequence input, int i, int i1, int i2) {
//            List<KmlFile> kmlFiles = kmlFileMap.keySet().stream().
//                    filter(element -> element.getFarmName().toLowerCase().equals(String.valueOf(input).toLowerCase())).collect(Collectors.toList());
//
//            if (kmlFiles.size() > 0){
//                // Refresh the ui
//                farmListViewAdapter = new FarmListViewAdapter(kmlFiles);
//                listView.setAdapter(farmListViewAdapter);
//            }else if(kmlFiles.size() == 0){
//                // Set data in the listViewAdapter from shared preferences
//                farmListViewAdapter = new FarmListViewAdapter(new ArrayList<>(kmlFileMap.keySet()));
//                listView.setAdapter(farmListViewAdapter);
//            }
//        }
//        @Override
//        public void afterTextChanged(Editable editable) { }
//    };



    /**
     * TODO COMMENTS
     * @param kmlFiles
     */

    @SuppressLint("NewApi")
    private void fillDropDownData(Set<KmlFile> kmlFiles){
        // This List have a data for the
        // drop down filter at the top of activity
        List<String> dropDownData = new ArrayList<>();
        // Add each different farm name in the dropDownData List
        for(KmlFile kmlFile : kmlFiles){
            if(!dropDownData.contains(kmlFile.getFarmName())){
                dropDownData.add(kmlFile.getFarmName());
            }
        }

        // Pass data from List in the dropDownAdapter
        dropDownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dropDownData);
        // Set in the ui
        binding.autoCompleteTextView.setAdapter(dropDownAdapter);
        binding.autoCompleteTextView.setText("");
    }
}