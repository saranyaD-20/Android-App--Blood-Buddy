package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class selectday_nonveg_per extends AppCompatActivity {

    private Spinner mealSpinner;
    private MaterialButton submitButton;
    private String selectedMeal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_day); // Ensure the layout is correctly named

        // Initialize views
        mealSpinner = findViewById(R.id.mealSpinner);
        submitButton = findViewById(R.id.submitButton);

        // Set up the spinner with meal options using custom spinner item layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meal_options, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealSpinner.setAdapter(adapter);

        // Spinner item selected listener
        mealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update selected meal
                selectedMeal = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedMeal = "";
            }
        });

        // Set click listener for the Submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate based on selected meal
                if ("Breakfast".equals(selectedMeal)) {
                    Intent intent = new Intent(selectday_nonveg_per.this, nonveg_breakfast_per.class);
                    startActivity(intent);
                } else if ("Lunch".equals(selectedMeal)) {
                    Intent intent = new Intent(selectday_nonveg_per.this, nonveg_lunch_per.class);
                    startActivity(intent);
                } else if ("Dinner".equals(selectedMeal)) {
                    Intent intent = new Intent(selectday_nonveg_per.this, nonveg_dinner_per.class);
                    startActivity(intent);
                } else if ("Snack".equals(selectedMeal)) {
                    Intent intent = new Intent(selectday_nonveg_per.this,nonveg_snack_per.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(selectday_nonveg_per.this, "Please select a meal or snack to continue.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}