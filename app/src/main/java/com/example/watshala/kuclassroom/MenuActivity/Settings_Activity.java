package com.example.watshala.kuclassroom.MenuActivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.watshala.kuclassroom.R;

public class Settings_Activity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
