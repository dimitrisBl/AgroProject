package com.example.agroproject.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.agroproject.model.MyLocation;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.LOCATION_SERVICE;

public class MyLocationService extends  Service implements LocationListener {

    /** Context */
    private Context context;

    /** Firebase database */
    private DatabaseReference dbReference;

    /** LocationManager  */
    private LocationManager locationManager;


    public MyLocationService(Context context) {
        this.context = context;

        // User-101 is a demo user
        dbReference = FirebaseDatabase.getInstance().getReference().child("user-101");

        // Initialize a locationManager object
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        readChanges();
    }


    @SuppressLint("MissingPermission")
    public void startLocationService(){
        if(locationManager!=null){
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                // Start a location request
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000, 1000,this);
            }else{
                Toast.makeText(context,
                        "No provider enable", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**\
     * apo edw tha stelnontai dedomena pisw sthn createAreaActivity
     */
    public void readChanges(){
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    MyLocation location = dataSnapshot.getValue(MyLocation.class);
                    try{
                        if(location!=null){
                            // Instantiate an intent
                            Intent intent = new Intent("LocationTrackingService");
                            // Include extra data
                            intent.putExtra("latitude", location.getLatitude());
                            intent.putExtra("longitude", location.getLongitude());

                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }catch (Exception e){

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        dbReference.setValue(location);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
