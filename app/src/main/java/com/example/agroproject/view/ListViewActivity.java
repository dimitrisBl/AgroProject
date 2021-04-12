package com.example.agroproject.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.agroproject.databinding.ActivityListViewBinding;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.view.adapters.ListViewAdapter;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Instantiate a KmlLocalStorageProvider object
        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);
        // Set data in the listViewAdapter from shared preferences
        listViewAdapter = new ListViewAdapter(new ArrayList<>
                (kmlLocalStorageProvider.loadPlacemarkMap().keySet()));
        // Convert Set<String> to String array
        String[] dropDownData = kmlLocalStorageProvider.
                loadFarmMap().keySet().toArray(new String[0]);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(ListViewActivity.this,
                        "clicked item name "+listViewAdapter.getItem(position), Toast.LENGTH_SHORT).show();
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
                for(Map.Entry<String, List<KmlFile>> entry : kmlLocalStorageProvider.loadFarmMap().entrySet()){
                    if(entry.getKey().equals(clickedItem)){
                        List<String> kmlFileNames = new ArrayList<>();
                        for(KmlFile kmlFile : entry.getValue()){
                            kmlFileNames.add(kmlFile.getName());
                        }
                        listViewAdapter = new ListViewAdapter(kmlFileNames);
                        listView.setAdapter(listViewAdapter);
                    }
                }
            }
        });
    }
}