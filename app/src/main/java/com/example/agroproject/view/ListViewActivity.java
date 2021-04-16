package com.example.agroproject.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
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
        // Add the stored data from shared preferences in the kmlFileList
        for(Map.Entry<String, List<KmlFile>> entry : farmMap.entrySet()){
            kmlFileList.addAll(entry.getValue());
        }
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
//            binding.autoCompleteTextView.setText(dropDownAdapter.getItem(0));
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        binding.autoCompleteTextView.setAdapter(dropDownAdapter);
        binding.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get item which clicked
                String clickedItem = dropDownAdapter.getItem(position);
                // Get values from farmMap for current item
                List<KmlFile> kmlFile = farmMap.get(clickedItem);
                // Refresh the ui
                listViewAdapter = new ListViewAdapter(kmlFile);
                listView.setAdapter(listViewAdapter);
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
                        // Get the kml file which clicked
                        KmlFile kmlFile = listViewAdapter.getItem(position);
                        // Remove the kmlFile from kmlFileList
                        kmlFileList.remove(kmlFile);
                        // Iterate the farmMap
                        for(Map.Entry<String, List<KmlFile>> entry : farmMap.entrySet()){
                            // Remove kml file
                            entry.getValue().remove(kmlFile);
                            // Save changes
                            kmlLocalStorageProvider.saveFarmMap(farmMap);
                            // If this entry don't have values
                            if(entry.getValue().isEmpty()){
                                // Remove record from farmMap
                                farmMap.remove(entry.getKey());
                                // Save changes
                                kmlLocalStorageProvider.saveFarmMap(farmMap);
                                // Refresh the drop down menu
                                String[] dropDownData = farmMap.keySet().toArray(new String[0]);
                                // Set the changes in the dropDownAdapter
                                dropDownAdapter = new ArrayAdapter<>(ListViewActivity.this,
                                        android.R.layout.simple_list_item_1, dropDownData);
                                binding.autoCompleteTextView.setAdapter(dropDownAdapter);
                                // break the for loop
                                break;
                            }
                        }
                        // Load the placemarkMap from shared preferences
                        Map<String, List<Placemark>> placemarkMap = kmlLocalStorageProvider.loadPlacemarkMap();
                        // Remove Placemark that belongs to this file from placemarkMap
                        placemarkMap.remove(kmlFile.getName());
                        // Save changes
                        kmlLocalStorageProvider.savePlacemarkMap(placemarkMap);
                        // Refresh the ListView
                        listViewAdapter = new ListViewAdapter(new ArrayList<>(kmlFileList));
                        listView.setAdapter(listViewAdapter);
                        // Show message
                        Toast.makeText(ListViewActivity.this, "The file "
                                +kmlFile.getName()+" was removed",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No",null)
        .show();
    }
}