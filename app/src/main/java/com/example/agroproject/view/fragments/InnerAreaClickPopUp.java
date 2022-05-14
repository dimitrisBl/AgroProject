package com.example.agroproject.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.agroproject.R;
import com.example.agroproject.databinding.InnerAreaClickPopupBinding;
import com.example.agroproject.model.Placemark;


public class InnerAreaClickPopUp extends Fragment {

    /** Class TAG */
    private final String TAG = "InnerAreaClickFragment";

    private Placemark placemark;

    /** Current view */
    private View popupView;

    /** View binding */
    private InnerAreaClickPopupBinding binding;

    /** Event listener for delete button click of pop up */
    private FarmAreaClickPopUp.FarmAreaPopUpEventListener farmAreaPopUpEventListener;


    /**
     * Instantiate a new InnerAreaClickPopUp
     *
     * @param placemark takes the object of the area clicked by the user
     */
    public InnerAreaClickPopUp(Placemark placemark){
        this.placemark = placemark;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = InnerAreaClickPopupBinding.inflate(getLayoutInflater());
        popupView = binding.getRoot();
        // Set the farm name on the title of pop up
        binding.farmName.setText(placemark.getName());
        // Set description
        binding.areaDescription.setText(placemark.getDescription());
        // Set click listener for close image top right
        binding.btnCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close pop up
                //((ViewGroup)popupView.getParent()).removeView(popupView);
                // Close pop up and trigger onBackPressed function of MapActivityV2
                getActivity().onBackPressed();
            }
        });
        // Set the text change event listener of area description edit text
        binding.areaDescription.addTextChangedListener(areaDescriptionTextChangedEvent);
        // Set click listener for delete button
        binding.deleteBtn.setOnClickListener(buttonClickListener);
        // Set click listener for delete button
        //binding.saveBtn.setOnClickListener(buttonClickListener);
        return popupView;
    }



    /**
     * Event handler to handle the text changed event of area description
     */
    private android.text.TextWatcher areaDescriptionTextChangedEvent = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        String areaDescription;
        @Override
        public void onTextChanged(CharSequence input, int i, int i1, int i2) {
            areaDescription = String.valueOf(input);
        }
        @Override
        public void afterTextChanged(Editable editable) {
            // Disable the cursor of edit text
            binding.areaDescription.setCursorVisible(false);
            // Trigger the edit area description event listener
            //innerAreaPopUpEventListener.editAreaDescription(placemark, areaDescription);
            farmAreaPopUpEventListener.editAreaDescription(placemark,areaDescription);
        }
    };


    /**
     * Button click listener
     */
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            // Get current button click
            Button currentButton = (Button) view;
            // Get text from current button
            String currentButtonText = String
                    .valueOf(currentButton.getText()).toLowerCase();

            switch (currentButtonText) {
             case "delete":
                    // Show new pop up for the delete question
                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_baseline_delete_24)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this area?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Trigger the delete area event listener
                                    farmAreaPopUpEventListener.deleteAreaEvent(placemark);
                                    // Close pop up and trigger onBackPressed function of MapActivity
                                    getActivity().onBackPressed();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                        // Do nothing
                                }
                            })
                    .show();
                break;

//                case "save":
//                    // Show message
//                    Toast.makeText(getActivity(),
//                            "Save button pressed",Toast.LENGTH_LONG).show();
//
//                    String newAreaDescription = String.valueOf(binding.areaDescription.getText());
//                    // Trigger the edit area description event listener
//                    innerAreaPopUpEventListener.editAreaDescription(placemark, newAreaDescription);
//                    // Close pop up and trigger onBackPressed function of MapActivity
//                    getActivity().onBackPressed();
//                break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            farmAreaPopUpEventListener = (FarmAreaClickPopUp.FarmAreaPopUpEventListener) activity;
            //innerAreaPopUpEventListener = (InnerAreaPopUpEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

}
