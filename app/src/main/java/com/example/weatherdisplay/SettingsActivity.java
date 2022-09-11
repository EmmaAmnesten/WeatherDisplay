package com.example.weatherdisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().getDecorView().setBackgroundColor(GlobalVariables.dayBlue);

        Switch mySwitch = findViewById(R.id.switch1);
        mySwitch.setChecked(GlobalVariables.useFullHeight);

        Button saveSettingsButton = findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(view -> {
            switchListener();
        });

    }

    private void switchListener() {
        Switch mySwitch = findViewById(R.id.switch1);
        GlobalVariables.useFullHeight = mySwitch.isChecked();
        System.out.println(mySwitch.isChecked());
    }
}