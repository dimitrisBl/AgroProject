package com.example.agroproject.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
        // Set click listener for delete button
        binding.deleteBtn.setOnClickListener(buttonClickListener);
        // Set click listener for delete button
        binding.editBtn.setOnClickListener(buttonClickListener);
        return popupView;
    }


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
                                    // Trigger the insert file event listener
                                    farmAreaPopUpEventListener.deleteAreaEvent(placemark);
                                    // Close pop up and trigger onBackPressed function of MapActivityV2
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

                case "edit":
                    // Show message
                    Toast.makeText(getActivity(),
                            "Edit button pressed",Toast.LENGTH_LONG).show();

                break;
            }
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            farmAreaPopUpEventListener = (FarmAreaClickPopUp.FarmAreaPopUpEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

}
