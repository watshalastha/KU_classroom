package com.example.watshala.kuclassroom.MenuActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.watshala.kuclassroom.R;

public class About_Us_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Developers!");
    }

}
