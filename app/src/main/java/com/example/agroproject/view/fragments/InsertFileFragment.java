package com.example.agroproject.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.agroproject.databinding.InsertFilePopupBinding;
import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.agro_api.JsonBuilder;
import com.example.agroproject.model.file.KmlFile;
import com.example.agroproject.model.file.KmlFileParser;
import com.example.agroproject.model.file.KmlLocalStorageProvider;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;


public class InsertFileFragment extends Fragment implements Executor {

    /** Class TAG */
    private final String TAG = "InsertFileFragment";

    /** Intent code for file selection */
    private final int FILE_SELECTION_CODE = 4;

    /** This code indicates that the
     * user has selected a file from file explorer */
    private final int RESULT_OK = -1;

    /** View binding */
    private InsertFilePopupBinding binding;

    /** Current view */
    private View popupView;

    /** Insert file event listener */
    private InsertFileEventListener insertFileEventListener;

    /** This set takes KmlFile objects from shared preferences storage*/
    private Set<KmlFile> kmlFiles;

    /** This List have a data for the
    * drop down filter at the top of activity */
    private List<String> dropDownData = new ArrayList<>();

    /** Array Adapter for drop down filter */
    private ArrayAdapter<String> dropDownAdapter;

    /** Uri */
    private Uri uri;

    /** This variable takes the file name after the user selects the file */
    private String fileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"on create view of InsertFileFragment executed");
        binding = InsertFilePopupBinding.inflate(getLayoutInflater());
        popupView = binding.getRoot();
        // Instantiate a KmlLocalStorageProvider object
        KmlLocalStorageProvider kmlLocalStorageProvider = new KmlLocalStorageProvider(getContext().getApplicationContext());
        // Load only the keys of kmlFileMap from shared preferences stored
        kmlFiles = kmlLocalStorageProvider.loadKmlFileMap().keySet();
        // Fill drop down with the farm names
        fillDropDownData(kmlFiles);
        // Set farm names in the drop down filter on ui
        binding.autoCompleteTextView.setAdapter(dropDownAdapter);
        // Set click listener for close image top right
        binding.btnCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close pop up and trigger onBackPressed function of MapActivity
                getActivity().onBackPressed();
            }
        });
        // Set click listener for choose file button click
        binding.chooseFileBtn.setOnClickListener(buttonClickListener);
        // Set click listener for save button click
        binding.saveFileBtn.setOnClickListener(buttonClickListener);
        return popupView;
    }

    /**
     *
     * @param kmlFiles
     */
    private void fillDropDownData(Set<KmlFile> kmlFiles){
        // Add each different farm name in the dropDownData List
        for(KmlFile kmlFile : kmlFiles){
            if(!dropDownData.contains(kmlFile.getFarmName())){
                dropDownData.add(kmlFile.getFarmName());
            }
        }
        // Pass data from List in the dropDownAdapter
        dropDownAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, dropDownData);
    }

    /**
     * Button click listener for choose file and save buttons
     */
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Get current button click
            Button currentButton = (Button) view;
            // Get text from current button
            String currentButtonText = String
                    .valueOf(currentButton.getText()).toLowerCase();
            switch (currentButtonText){
                case "choose file":
                     // Open file explorer
                     Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                     intent.setType("*/*");
                     //intent.setType("text/xml");/* TODO AUTO EINAI TO SWSTO GIA NA EPILEGEI MONO KML ARXEIA */
                     startActivityForResult(intent, FILE_SELECTION_CODE);
                break;

                case "save":
                    // Get the text from farmName TextView in String type
                    String farmNameText = binding.autoCompleteTextView.getText().toString().trim();
                    // Check if the  fields of pop up is empty
                    if (!farmNameText.isEmpty() && fileName != null) {
                        // Instantiate a KmlFileParse object
                        KmlFileParser kmlFileParser = new KmlFileParser(getContext().getApplicationContext());
                        // Open file and get the data in String type
                        String dataFromFile = kmlFileParser.getFileData(uri);
                        // Parse data from the file and create a List with Placemark objects
                        List<Placemark> placemarks = kmlFileParser.parseDataFromFile(dataFromFile);
                        // Create a JSONObject for Agro Api request,
                        // List jsonObjectList has the data in JSON type of the current file
                        List<JSONObject> jsonObjectList = JsonBuilder.build(placemarks);
                        // Post data in Agro Api TODO EINAI SXOLIO TO POST GIA NA MHN TREXEI SUNEXEIA
                        //HttpRequest.postRequest(jsonObjectList);
                        // Create a new KmlFile object
                        KmlFile kmlFile = new KmlFile(fileName, uri.getPath(), dataFromFile, farmNameText);
                        // Check if the kmlFile is exists
                        if(fileExistsCheck(kmlFile) == false){
                            Toast.makeText(getActivity(), "The file "+fileName+" was successfully added",Toast.LENGTH_LONG).show();
                            // Get the center area of first placemark which contained this file
                            LatLng center = AreaUtilities.getAreaCenterPoint(placemarks.get(0).getLatLngList());
                            // Trigger the insert file event listener
                            insertFileEventListener.inertFileEvent(center, kmlFile, placemarks);
                            // Close pop up and trigger onBackPressed function of MapActivityV2
                            getActivity().onBackPressed();
                        }
                    }else{
                        // Show message
                        Toast.makeText(getActivity(),
                                "Fill all the fields of form please", Toast.LENGTH_LONG).show();
                    }
                break;
            }
        }
    };

    /**
     * @param kmlFile has the new kml file witch user wants to add
     * @return true if file exists false if file is not exists
     */
    private boolean fileExistsCheck(KmlFile kmlFile) {
        for(KmlFile file : kmlFiles){
            if(file.getData().equals(kmlFile.getData())){
                // Show message
                Toast.makeText(getActivity(), "The file " + file.getName()
                        + " is already exists in the " + file.getFarmName(), Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_SELECTION_CODE){
            if(resultCode == RESULT_OK){
                // Create a Uri path
                uri = Uri.parse(data.getDataString());
                // Get the name of the file
                fileName = uri.getPath();
                int cut = fileName.lastIndexOf('/');
                if(cut != -1){
                    fileName = fileName.substring(cut + 1);
                }
                // Set file name in TextView
                binding.fileName.setText(fileName);
            }
        }
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }


    /**
     * Interface for handle event listener about the insert file
     */
    public interface InsertFileEventListener {
        void inertFileEvent(LatLng bounds, KmlFile kmlFile, List<Placemark> placemarks);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            insertFileEventListener = (InsertFileEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
}
