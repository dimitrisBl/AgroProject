package com.example.agroproject.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.core.util.Pair;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.agroproject.R;
import com.example.agroproject.databinding.AreaClickPopupBinding;
import com.example.agroproject.model.DatePicker;
import com.example.agroproject.model.Placemark;
import com.example.agroproject.model.agro_api.HttpRequest;
import com.example.agroproject.model.agro_api.JsonParser;
import com.example.agroproject.model.agro_api.StringBuildForRequest;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class AreaClickFragment extends Fragment {

    /** Class TAG */
    private final String TAG = "AreaClickFragment";

    /** Permission code for write file */
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 10;

    /** View binding */
    private AreaClickPopupBinding binding;

    /** Current view */
    private View popupView;

    private Placemark placemark;

    /** Event listener for button click of pop up */
    private AreaPopUpEventListener popUpClickEventListener;

    /** The polygon ID pressed by the user */
    private String polygonId;

    /** BitmapDescriptor takes the ndvi image after request in the sentinel url fo agro api */
    private BitmapDescriptor bitmapDescriptor;

    /**
     * Instantiate a new AreaClickFragment
     *
     * @param placemark takes the object of the area clicked by the user
     * @param agroApiSentinelUrl
     */
    public AreaClickFragment(Placemark placemark,String agroApiSentinelUrl){
        this.placemark = placemark;
        this.polygonId = agroApiSentinelUrl;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        binding = AreaClickPopupBinding.inflate(getLayoutInflater());
        popupView = binding.getRoot();
        // We are registering an observer (responseReceiver) with action name GetRequestData to receive Intents after http Get request in Agro api.
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(responseReceiver, new IntentFilter("GetRequestData"));
        // Set click listener for close image top right
        binding.btnCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Unregister since the activity is about to be closed.
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
                // Close pop up
                //((ViewGroup)popupView.getParent()).removeView(popupView);
                // Close pop up and trigger onBackPressed function of MapActivityV2
                getActivity().onBackPressed();
            }
        });
        // Set the farm name on the title of pop up
        binding.farmName.setText(placemark.getName());
        // Set description
        binding.areaDescription.setText(placemark.getDescription());
        // Set click listener for delete button
        binding.deleteBtn.setOnClickListener(buttonClickListener);
        // Set click listener for ndvi button
        binding.ndviBtn.setOnClickListener(buttonClickListener);
        // Set click listener for export button
        binding.exportBtn.setOnClickListener(buttonClickListener);
        return popupView;
    }


    /**
     *  Our handler for received Intents. This will be called whenever an Intent
     *  with an action named "GetRequestData".
     *  TODO MORE DESCRIPTION
     */
    private BroadcastReceiver responseReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get response data and request type that are included in the Intent
            String responseData = intent.getStringExtra("Response data");
            String requestType = intent.getStringExtra("Request type");

            if(requestType.equals("Get sentinel data")){
                // Get image url
                String imageUrl = JsonParser.getImage(responseData);
                // Get image from Agro api
                new getImageAsync().execute(imageUrl);
            }
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
                                    // Trigger the insert file event listener
                                    popUpClickEventListener.deleteAreaEvent(placemark);
                                    // Unregister since the pop up is about to be closed.
                                    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
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
                case "export":
                    // Check permissions for write external storage
                    checkSelfPermissionForFiles();
                break;

                case "ndvi":
                    // Instantiate a new DatePicker object
                    DatePicker datePicker = new DatePicker();
                    datePicker.Init();
                    MaterialDatePicker materialDatePicker = datePicker.getMaterialDatePicker();
                    // Show calendar
                    materialDatePicker.show(getActivity().getSupportFragmentManager(), "DATE_PICKER");
                    // Set click listener for save button of calendar
                   materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                       @Override
                       public void onPositiveButtonClick(Pair<Long, Long>  selection) {
                              // Edit time before add the time in sentinel url of agro api
                              // This processing is done only for  time zone of the agro api
                              int dateFromLength = selection.first.toString().length();
                              int dateToLength = selection.second.toString().length();
                              String dateFrom = selection.first.toString().substring(0, dateFromLength-3);
                              String dateTo = selection.second.toString().substring(0, dateToLength-3);
                              // Create a url for sentinel Get request of agro api for specific polygon and date
                              String sentinelRequestLink = StringBuildForRequest.sentinelRequestLink(polygonId,dateFrom,dateTo);
                              // Get request on sentinel url of Agro api
                              HttpRequest.getRequest( getContext().getApplicationContext(), sentinelRequestLink,  "Get sentinel data");
                       }
                   });
                break;
            }
        }
    };

    /**
     *
     * Check if write external storage permission granted.
     * <p>
     * If the permission has been granted calls the trigger the exportFile method of popUpClickEventListener interface and closes the pop up.
     * If the permission has not been granted displays a request for the missing permissions and asks the permission.
     * </p>
     */
    private void checkSelfPermissionForFiles(){
        // Permission is not granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults).
            // Requests the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_CODE);

          // Permission is  granted
        }else{
            // Trigger the export file event listener
            popUpClickEventListener.exportFileEvent(placemark);
            // Unregister since the pop up is about to be closed.
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
            // Close pop up and trigger onBackPressed function of MapActivityV2
            getActivity().onBackPressed();
        }
    }

    /**
     * Interface for handle event listener about the insert file
     */
    public interface AreaPopUpEventListener {
        void deleteAreaEvent(Placemark placemark);
        void loadNdviEvent(Placemark placemark, BitmapDescriptor bitmapDescriptor);
        void exportFileEvent(Placemark placemark);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if(requestCode == WRITE_EXTERNAL_STORAGE_CODE){
            // Permission granted
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                // Trigger the export file event listener
                popUpClickEventListener.exportFileEvent(placemark);
                // Unregister since the pop up is about to be closed.
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
                // Close pop up and trigger onBackPressed function of MapActivityV2
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            popUpClickEventListener = (AreaPopUpEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }


    /**
     * TODO CLASS DESCRIPTION
     */
    private class getImageAsync extends AsyncTask<String, Void, BitmapDescriptor> {
        @Override
        protected BitmapDescriptor doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                input.close();
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(myBitmap);
                return  bitmapDescriptor;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d(TAG,"MalformedURLException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"io exception");
            }
            return null;
        }
        @Override
        protected void onPostExecute(BitmapDescriptor descriptor) {
            super.onPostExecute(descriptor);
            bitmapDescriptor = descriptor;
            // Trigger the event listener for load nvdi in the ui
            popUpClickEventListener.loadNdviEvent(placemark, bitmapDescriptor);
            // Unregister since the pop up is about to be closed.
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(responseReceiver);
            // Close pop up and trigger onBackPressed function of MapActivityV2
            getActivity().onBackPressed();
        }
    }
}
