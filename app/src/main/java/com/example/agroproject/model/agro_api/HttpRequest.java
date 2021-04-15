package com.example.agroproject.model.agro_api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpRequest {

    /** Class TAG */
    private static final String TAG = "HttpRequest";

    /**
     * Performs a post request in endpoint polygons of Agro Api for create a new polygon.
     * The number of polygons to be created is determined by the size of the list jsonObjectList.
     *
     * @param jsonObjectList has the placemark data in json type for the new kml file to be added,
     * one kml file may contain multiple placemark data.
     */
    public static void postRequest(List<JSONObject> jsonObjectList){
        // Agro api endpoint for polygons.
        String url = StringBuildForRequest.polygonsRequestLink();
        // Create a POST request for each JSONObject
        for(JSONObject jsonObject : jsonObjectList){
            // Specify the type of data to be sent in agro api
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            // Create a body with the data of the request
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            // Instantiate a OkHttpClient object
            OkHttpClient client = new OkHttpClient();
            // Build the request
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            // Send the request
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call,  Response response) throws IOException {
                    String r = response.body().string();
                    Log.e(TAG, "onResponse(): " + r );

                }
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure() Request was: " + request);
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Performs a GET request in specific url
     * to receive data about the polygons from the agro api.
     *
     * @param context has the current context of application.
     * @param url has the url for GET http request.
     * @param requestType is auxiliary parameter to specifies
     * at which endpoint of agro api has been executed the get request
     */
    public static void getRequest(Context context, String url, String requestType){
        // Instantiate a OkHttpClient object
        OkHttpClient client = new OkHttpClient();
        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        // Send the request
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call,  Response response) throws IOException {
                // Get response data in string type
                String r = response.body().string();
                // Instantiate an intent
                Intent intent = new Intent("GetRequestData");
                // Include the device coordinates in intent
                intent.putExtra("Response data", r);
                //Include the device coordinates in intent
                intent.putExtra("Request type", requestType);
                // Send broadcast in MapActivity
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure() Request was: " + request);
                e.printStackTrace();
            }
        });
    }
}
