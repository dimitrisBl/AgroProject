package com.example.agroproject.services;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;
/**
 * Checks network availability and displays the require message to user
 * */

public class NetworkUtil extends BroadcastReceiver{
    /** WIFI CONNECTION */
    public static int TYPE_WIFI = 1;
    /** Mobile CONNECTION */
    public static int TYPE_MOBILE = 2;
    /** not connected **/
    public static int TYPE_NOT_CONNECTED = 0;

    /** Activity */
    private Activity activity;

    /**
     * Instantiates a new NetworkUtil.
     *
     * @param activity has the current activity of application.
     */
    public NetworkUtil(Activity activity) {
        this.activity = activity;
    }

    /**
     * Checks CONNECTIVITY SERVICE using Connectivity Manager
     * and returns the established connection if available.
     *
     * @param context
     * @return Returns the type of connection that is established
     */
    public int getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    /**
     * Check's connection everytime it changes using BroadCast Receiver.
     *
     * @param context has the current context of application.
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // Get the internet connection status
        int connection = getConnectivityStatus(context);

        if(connection == NetworkUtil.TYPE_NOT_CONNECTED){
            // Show message
             AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setTitle("Wifi permission")
                    .setMessage("The Wifi is required for this app, go to wifi settings to turn on Wifi.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        //Intent intent1 = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                        Intent intent1 = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        activity.startActivity(intent1);
                    }))
                    .setNegativeButton("No", ((dialogInterface, i) -> {
                        Toast.makeText(activity,
                                "Wifi is required." +
                                        "Please enable wifi.",Toast.LENGTH_LONG).show();
                        activity.finish();
                    }))
                    .setOnCancelListener(((dialogInterface) -> {
                        Toast.makeText(activity, "Wifi is required." +
                                "Please enable wifi.", Toast.LENGTH_LONG).show();
                        activity.finish();
                    }))
            .show();
        }
    }
}