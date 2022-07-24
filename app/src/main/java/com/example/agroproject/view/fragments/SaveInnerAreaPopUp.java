package com.example.agroproject.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.agroproject.databinding.SaveInnerAreaPopupBinding;
import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.Placemark;


public class SaveInnerAreaPopUp extends Fragment {

    /** Class TAG */
    private final String TAG = "SaveAreaFragment";

    /** View binding */
    private SaveInnerAreaPopupBinding binding;

    /** Current view */
    private View popupView;

    /** Create area event listener */
    private CreateAreaEventListener createAreaEventListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"on create view of SaveAreaFragment executed");
        binding = SaveInnerAreaPopupBinding.inflate(getLayoutInflater());
        popupView = binding.getRoot();
        // Set click listener for close image top right
        binding.btnCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close pop up and trigger onBackPressed function of MapActivityV2
                getActivity().onBackPressed();
            }
        });
        // Save button click listener
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the area name text
                String areaName = binding.areaName.getText().toString().trim();
                // Get the description
                String areaDescription = binding.areaDescription.getText().toString().trim();
                // Check input
                if(!areaName.isEmpty() ){ //&& !areaDescription.isEmpty()
                    // Get the object of outsider area
                    Placemark outsiderArea = AreaUtilities.getOutsiderArea();
                    // Trigger the create area event listener
                    createAreaEventListener.createAreaEvent(areaName, areaDescription, outsiderArea);
                    // Close pop up and trigger onBackPressed function of MapActivityV2
                    getActivity().onBackPressed();
                }else{
                    Toast.makeText(getActivity(),
                            "Please type a name of area", Toast.LENGTH_LONG).show();
                }
            }
        });
        return popupView;
    }

    /**
     * Interface for handle event listener about the area create
     */
    public interface CreateAreaEventListener {
        void createAreaEvent(String areaName, String areaDescription, Placemark outsiderArea);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            createAreaEventListener = (CreateAreaEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
}
