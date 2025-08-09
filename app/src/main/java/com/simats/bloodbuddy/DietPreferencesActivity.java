package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DietPreferencesActivity extends AppCompatActivity {

    private RadioGroup dietTypeGroup;
    private RadioButton veganRadioButton;
    private RadioButton vegetarianRadioButton;
    private RadioButton nonVegetarianRadioButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietary_preferences);

        // Initialize views
        dietTypeGroup = findViewById(R.id.dietTypeGroup);
        veganRadioButton = findViewById(R.id.veganRadio);
        vegetarianRadioButton = findViewById(R.id.vegetarianRadio);
        nonVegetarianRadioButton = findViewById(R.id.nonVegetarianRadio);
        continueButton = findViewById(R.id.continueButton);

        // Log to verify initialization
        Log.d("DietPreferencesActivity", "dietTypeGroup: " + (dietTypeGroup != null));
        Log.d("DietPreferencesActivity", "veganRadioButton: " + (veganRadioButton != null));
        Log.d("DietPreferencesActivity", "vegetarianRadioButton: " + (vegetarianRadioButton != null));
        Log.d("DietPreferencesActivity", "nonVegetarianRadioButton: " + (nonVegetarianRadioButton != null));
        Log.d("DietPreferencesActivity", "continueButton: " + (continueButton != null));

        // Set OnClickListener for Continue button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check selected diet type
                if (veganRadioButton != null && veganRadioButton.isChecked()) {
                    Log.d("DietPreferencesActivity", "Vegan selected, transitioning to SelectDayActivity");
                    Intent intent = new Intent(DietPreferencesActivity.this, SelectDayActivity.class);
                    startActivity(intent);
                } else if (vegetarianRadioButton != null && vegetarianRadioButton.isChecked()) {
                    Log.d("DietPreferencesActivity", "Vegetarian selected, transitioning to SelectDayVegActivity");
                    Intent intent = new Intent(DietPreferencesActivity.this, selectday_veg.class);
                    startActivity(intent);
                } else if (nonVegetarianRadioButton != null && nonVegetarianRadioButton.isChecked()) {
                    Log.d("DietPreferencesActivity", "Non-Vegetarian selected, transitioning to SelectDayNonVegActivity");
                    Intent intent = new Intent(DietPreferencesActivity.this, selectday_nonveg_hyper.class);
                    startActivity(intent);
                } else {
                    Log.d("DietPreferencesActivity", "No diet type selected");
                    Toast.makeText(DietPreferencesActivity.this, "Please select a diet type to continue.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}