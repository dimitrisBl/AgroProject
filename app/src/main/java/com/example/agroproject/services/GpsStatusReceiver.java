package com.example.agroproject.services;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class GpsStatusReceiver extends BroadcastReceiver {

    // Register receiver in MainActivity
    // GpsStatusReceiver gpsStatusReceiver = new GpsStatusReceiver();
    // registerReceiver(gpsStatusReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        private Context context;

        @Override
        public void onReceive(Context context, Intent intent) {
            this.context = context;
            LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
            boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            onGpsStatusChanged(gpsStatus);
        }

        private void onGpsStatusChanged(boolean gpsStatus){

            Toast.makeText(context,
                    "GPS STATUS - " + gpsStatus, Toast.LENGTH_LONG).show();
        }
}
