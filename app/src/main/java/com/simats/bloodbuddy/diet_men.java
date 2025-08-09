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

public class diet_men extends AppCompatActivity {

    private RadioGroup dietTypeGroup;
    private RadioButton veganRadioButton;
    private RadioButton vegetarianRadioButton;
    private RadioButton nonVegetarianRadioButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deit_hypertension);

        // Initialize views
        dietTypeGroup = findViewById(R.id.dietTypeGroup);
        vegetarianRadioButton = findViewById(R.id.vegetarianRadio);
        nonVegetarianRadioButton = findViewById(R.id.nonVegetarianRadio);
        continueButton = findViewById(R.id.continueButton);

        // Log to verify initialization
        Log.d("DietPreferencesActivity", "dietTypeGroup: " + (dietTypeGroup != null));
        Log.d("DietPreferencesActivity", "vegetarianRadioButton: " + (vegetarianRadioButton != null));
        Log.d("DietPreferencesActivity", "nonVegetarianRadioButton: " + (nonVegetarianRadioButton != null));
        Log.d("DietPreferencesActivity", "continueButton: " + (continueButton != null));

        // Set OnClickListener for Continue button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check selected diet type
                if (vegetarianRadioButton != null && vegetarianRadioButton.isChecked()) {
                    Log.d("DietPreferencesActivity", "Vegetarian selected, transitioning to SelectDayVegActivity");
                    Intent intent = new Intent(diet_men .this, selectday_men.class);
                    startActivity(intent);
                } else if (nonVegetarianRadioButton != null && nonVegetarianRadioButton.isChecked()) {
                    Log.d("DietPreferencesActivity", "Non-Vegetarian selected, transitioning to SelectDayNonVegActivity");
                    Intent intent = new Intent(diet_men .this, selectday_nonveg_men.class);
                    startActivity(intent);
                } else {
                    Log.d("DietPreferencesActivity", "No diet type selected");
                    Toast.makeText(diet_men .this, "Please select a diet type to continue.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}