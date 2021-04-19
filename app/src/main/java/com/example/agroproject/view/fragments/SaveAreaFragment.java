package com.example.agroproject.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.agroproject.R;
import com.example.agroproject.databinding.SaveAreaPopupBinding;
import com.example.agroproject.model.AreaUtilities;
import com.example.agroproject.model.Placemark;
import com.google.android.gms.maps.SupportMapFragment;


public class SaveAreaFragment extends Fragment {

    /** Class TAG */
    private final String TAG = "SaveAreaFragment";

    /** View binding */
    private SaveAreaPopupBinding binding;

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
        binding = SaveAreaPopupBinding.inflate(getLayoutInflater());
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
                String areaName = binding.areaName.getText().toString();
                // Get the description
                String areaDescription = binding.areaDescription.getText().toString();
                if(!areaName.isEmpty() && !areaDescription.isEmpty()){
                    // Get the object of outsider area
                    Placemark outsiderArea = AreaUtilities.getOutsiderArea();
                    // Trigger the create area event listener
                    createAreaEventListener.createAreaEvent(areaName, areaDescription, outsiderArea);
                    // Close pop up and trigger onBackPressed function of MapActivityV2
                    getActivity().onBackPressed();
                }else{
                    // Show message
                    Toast.makeText(getActivity(),
                            "Fill all the fields of form please", Toast.LENGTH_LONG).show();
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
