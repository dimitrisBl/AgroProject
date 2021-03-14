package com.example.agroproject.model;

import android.net.Uri;

public class SingletonFilePath {


        private static SingletonFilePath instance = null;
        public Uri file;

       private   SingletonFilePath() {
           if (instance != null){
               throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
           }

       }

         public static SingletonFilePath getInstance() {
             if (instance == null) { //if there is no instance available... create new one
                 instance = new SingletonFilePath();
             }
             return instance;
         }

    public Uri getFile() {
        return file;
    }

    public void setFile(Uri file) {
        this.file = file;
    }
}