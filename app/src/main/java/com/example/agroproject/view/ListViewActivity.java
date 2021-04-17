package com.example.agroproject.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.agroproject.view.adapters.ListViewAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ListViewActivity extends AppCompatActivity {

    /** Class TAG */
    private final String TAG = "ListViewActivity";

    /** Activity view binding */
    private ActivityListViewBinding binding;

    /** ListView */
    private ListView listView;

    /** Adapter for ListView */
    private ListViewAdapter listViewAdapter;

    /** Adapter for drop down menu */
    private ArrayAdapter<String> dropDownAdapter;

    /** KmlLocalStorageProvider */
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    /** kmlFile Map */
    private Map<KmlFile, List<Placemark>> kmlFileMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        //  Load the kmlFile Map from shared preferences storage
        kmlFileMap = kmlLocalStorageProvider.loadKmlFileMap();
        // Set data in the listViewAdapter from shared preferences
        listViewAdapter = new ListViewAdapter(new ArrayList<>(kmlFileMap.keySet()));
        // Initialize ui components
        initializeComponents();
    }

    /**
     * TODO DESCRIPTION
     *
     */
    private void initializeComponents(){
        //---- ListView ----- //
        listView = binding.listView;
        listView.setAdapter(listViewAdapter);
        // Item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /** TODO LOGIC */
                Toast.makeText(ListViewActivity.this,
                        "clicked item name "+listViewAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });
        // Item long click listener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Show dialog
                new AlertDialog.Builder(ListViewActivity.this)
                        .setTitle("Are you sure?")
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get the kml file which clicked
                                KmlFile kmlFile = listViewAdapter.getItem(position);
                                // Remove KmlFile from placemark Map
                                kmlFileMap.remove(kmlFile);
                                // Save the changes in shared preferences
                                kmlLocalStorageProvider.saveKmlFileMap(kmlFileMap);
                                // Refresh  ListView for the ui changes about the KmlFile removed
                                listViewAdapter = new ListViewAdapter(new ArrayList<>(kmlFileMap.keySet()));
                                listView.setAdapter(listViewAdapter);
                                // Refresh drop down menu for the ui changes about the KmlFile removed
                                fillDropDownData(kmlFileMap.keySet());
                                // Show message
                                Toast.makeText(ListViewActivity.this, "The file "
                                        +kmlFile.getName()+" was removed",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No",null)
                .show();
                return true;
            }
        });

        //---- DropDown menu ----- //
        // Add data in the dropDown menu
        fillDropDownData(kmlFileMap.keySet());
        // Set click listener
        binding.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get item which clicked
                String clickedItem = dropDownAdapter.getItem(position);
                // Get all kml files from kmlFile Map who have same farm name wth the clicked item
                List<KmlFile> kmlFiles = kmlFileMap.keySet().stream().
                        filter(element -> element.getFarmName().equals(clickedItem)).collect(Collectors.toList());
                // Refresh the ui
                listViewAdapter = new ListViewAdapter(kmlFiles);
                listView.setAdapter(listViewAdapter);
            }
        });
    }


    /**
     *
     * @param kmlFiles
     */
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
    }
}