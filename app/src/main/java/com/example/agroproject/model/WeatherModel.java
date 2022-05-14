package com.example.agroproject.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class WeatherModel {

    private String humidity;
    private String temp;
    private String tempMin;
    private String tempMax;
    private String description;
    private String icon;
    private String windSpeed;


    //this case used to avoid null pointer exception for random locations
    public WeatherModel() {
        setHumidity("");
        setTemp("");
        setTempMax("");
        setTempMin("");
        setWindSpeed("");
        setDescription("");
        setIcon("");
    }


    public String getHumidity() {
        return humidity;
    }

    public String getTemp() {
        return temp;
    }

    public String getTempMin() {
        return tempMin;
    }

    public String getTempMax() {
        return tempMax;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public static String getImage(String icon){
        return String.format("http://api.openweathermap.org/img/w/%s.png",icon);
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setWindSpeed(String weatherSpeed) {
        this.windSpeed = weatherSpeed;
    }


    public static class WeatherImageLoader {

        public Activity activity;
        public RequestQueue mRequestQueue;
        public ImageLoader mImageLoader;

        public WeatherImageLoader(Activity activity) {
            this.activity = activity;
        }

        public ImageLoader getmImageLoader() {
            return mImageLoader;
        }

        public  void setImageLoader() {
            mRequestQueue = Volley.newRequestQueue(activity.getApplicationContext());
            this.mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }
                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });

        }
    }
}
