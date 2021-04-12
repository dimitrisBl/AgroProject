package com.example.agroproject.model.agro_api;

import android.util.Log;

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

    // key apo -> 6285cde775f088c749b4f1201829658a
    // key dimitri -> 126813a55ec5e945022783add18142d2

    /** Class TAG */
    private static final String TAG = "HttpRequest";

    /** API endpoint for polygons. This url can be used for GET and POST requests. */
    private static final String url = "http://api.agromonitoring.com/agro/1.0/polygons?appid=6285cde775f088c749b4f1201829658a";

    /**
     * This method performs a post request in Agro Api for create a new polygon.
     * @param jsonObjectList has the data to be sent in Agro api with the post request.
     */
    public static void postRequest(List<JSONObject> jsonObjectList){
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
     *
     * TODO DESCRIPTION
     *
     */
    public static void getRequest(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

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
