package com.example.agroproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityMain extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.map_btn);
        button.setOnClickListener(onButtonClickListener);

    }



    private View.OnClickListener onButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button currentButton = (Button) view;

            if(currentButton.getText().equals("Open Map")){
                Intent intent = new Intent(ActivityMain.this, MapActivity.class);
                startActivity(intent);
            }
        }
    };
}