package com.example.agroproject.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agroproject.R;
import com.example.agroproject.databinding.ActivityListViewBinding;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.view.adapters.ListViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class ListViewActivity extends AppCompatActivity {

    /** Class TAG */
    private final String TAG = "ListViewActivity";

    /** Binding */
    private ActivityListViewBinding binding;

    /** ListView */
    private ListView listView;

    /** Adapter for ListView */
    private ListViewAdapter listViewAdapter;

    /** Adapter for drop down menu */
    private ArrayAdapter<String> dropDownAdapter;

    /** KmlLocalStorageProvider */
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    /** List with Placemark objects, auxiliary List for the ui only */
    private List<KmlFile> kmlFileList = new ArrayList<>();

    /** farmMap */
    private Map<String, List<KmlFile>> farmMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        // Load the farmMap from shared preferences
        farmMap = kmlLocalStorageProvider.loadFarmMap();
        // Fill data in the kmlFileList
        fillTheKmlFileList();
        // Set data in the listViewAdapter from shared preferences
        listViewAdapter = new ListViewAdapter(new ArrayList<>(kmlFileList));
        // Convert Set<String> to String array
        String[] dropDownData = kmlLocalStorageProvider
                .loadFarmMap().keySet().toArray(new String[0]);
        // Set data in the dropDownAdapter
        dropDownAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dropDownData);
        // Initialize ui components
        initializeComponents();
    }


    /**
     * Add the stored data from the
     * shared preferences in the kmlFileList.
     */
    private void fillTheKmlFileList(){
        for(Map.Entry<String, List<KmlFile>> entry : farmMap.entrySet()){
            for(KmlFile kmlFile: entry.getValue()){
                kmlFileList.add(kmlFile);
            }
        }
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
                Toast.makeText(ListViewActivity.this,
                        "clicked item name "+listViewAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });
        // Item long click listener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Show dialog
                removeFileAlertDialog(position);
                return true;
            }
        });


        //---- DropDown menu ----- //
        try{
            binding.autoCompleteTextView.setText(dropDownAdapter.getItem(0));
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        binding.autoCompleteTextView.setAdapter(dropDownAdapter);
        binding.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Current clicked item
                String clickedItem = dropDownAdapter.getItem(position);
                for(Map.Entry<String, List<KmlFile>> entry : farmMap.entrySet()){
                    if(entry.getKey().equals(clickedItem)){
                        listViewAdapter = new ListViewAdapter(entry.getValue());
                        listView.setAdapter(listViewAdapter);
                    }
                }
            }
        });
    }

    /**
     * Remove kmlFiles and Placemarks from the stored Maps
     *
     * @param position have position of the clicked item
     */
    private void removeFileAlertDialog(int position){
        new AlertDialog.Builder(ListViewActivity.this)
                .setTitle("Are you sure?")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setMessage("Do you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the name of the clicked item
                        String clickedItemName = listViewAdapter.getItem(position).getName();
                        // Load the placemarkMap from shared preferences
                        Map<String, List<Placemark>> placemarkMap = kmlLocalStorageProvider.loadLayers();
                        // Remove a file from farmMap
                        for(List<KmlFile> kmlFiles : farmMap.values()){
                            for(KmlFile kmlFile : kmlFiles){
                                if(kmlFile.getName().equals(clickedItemName)){
                                    // Remove
                                    kmlFiles.remove(kmlFile);
                                    // Save changes
                                    kmlLocalStorageProvider.saveFarmMap(farmMap);
                                    // Remove Placemark that belongs to this file from placemarkMap
                                    placemarkMap.remove(kmlFile.getName());
                                    // Save changes
                                    kmlLocalStorageProvider.saveLayers(placemarkMap);
                                    // Remove the kmlFile from kmlFileList
                                    kmlFileList.remove(kmlFile);
                                    // Refresh the ui
                                    listViewAdapter = new ListViewAdapter
                                            (new ArrayList<>(kmlFileList));
                                    listView.setAdapter(listViewAdapter);
                                    // Show message
                                    Toast.makeText(ListViewActivity.this, "The file "
                                            +clickedItemName+" was removed",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("No",null)
        .show();
    }
}