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
    private Activity activity;

    public NetworkUtil(Activity activity) {
        this.activity = activity;
    }

    /**
     * Returns the type of connection that is established
     * @param context
     * @return
     */
    public int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    /**
     * Check's if there is an internet connection
     * @param context
     * @return
     */
    public String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Connected";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Connected";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    /**
     * Check's connection everytime it changes
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = getConnectivityStatusString(context);
        if(status != "Connected") {
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setTitle("Wifi permission")
                    .setMessage("The Wifi is required for this app, go to wifi settings to turn on Wifi.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent1 = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        activity.startActivity(intent1);
                    }))
                    .setNegativeButton("No", ((dialogInterface, i) -> {
                        Toast.makeText(activity,
                                "Wifi is required " +
                                        "Please enable Wifi.",Toast.LENGTH_LONG).show();
                        activity.finish();
                    }))
                    .setOnCancelListener(((dialogInterface) -> {
                        Toast.makeText(activity, "Wifi is required . " +
                                "Please enable Wifi.", Toast.LENGTH_LONG).show();
                        activity.finish();
                    }))
            .show();
        }
    }


}