package com.example.agroproject.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agroproject.databinding.ActivityListViewBinding;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.example.agroproject.view.adapters.ListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {

    private ActivityListViewBinding binding;

    private ListView listView;

    private ListViewAdapter myAdapter;
    private KmlLocalStorageProvider kmlLocalStorageProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        kmlLocalStorageProvider = new KmlLocalStorageProvider(this);

        myAdapter = new ListViewAdapter(kmlLocalStorageProvider.loadFarmMap());

        listView = binding.listView;
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(ListViewActivity.this,
                        "clicked item name "+myAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });

        TextView filter = binding.filter;

        Button submitBtn = binding.submit;
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = filter.getText().toString();
                List<KmlFile> newListForAdapter = kmlLocalStorageProvider.loadFarmMap().get(text);
                Map<String, List<KmlFile>> tempMap = new HashMap<>();
                tempMap.put(text,kmlLocalStorageProvider.loadFarmMap().get(text));
                myAdapter = new ListViewAdapter(tempMap);
                listView.setAdapter(myAdapter);
            }
        });
    }
}