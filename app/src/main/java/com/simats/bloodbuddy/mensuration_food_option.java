package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class mensuration_food_option extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensuration_food_option);

        // Find the BottomNavigationView
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set up the navigation item selected listener
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, Profile.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HealthCondition.class));
                return true;
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));

                return true; // Handle notifications if needed
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });

        // Set up the Breakfast arrow button
        ImageView btnBreakfastArrow = findViewById(R.id.btn_breakfast_arrow);
        btnBreakfastArrow.setOnClickListener(v -> {
            Toast.makeText(mensuration_food_option.this, "Navigating to Breakfast Plan", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mensuration_food_option.this, breakfast_plan_menstruation.class));
        });

        // Set up the Lunch arrow button
        ImageView btnLunchArrow = findViewById(R.id.btn_lunch_arrow);
        btnLunchArrow.setOnClickListener(v -> {
            Toast.makeText(mensuration_food_option.this, "Navigating to Lunch Plan", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mensuration_food_option.this, lunch_plan_mensuration.class));
        });

        // Set up the Dinner arrow button
        ImageView btnDinnerArrow = findViewById(R.id.btn_dinner_arrow);
        btnDinnerArrow.setOnClickListener(v -> {
            Toast.makeText(mensuration_food_option.this, "Navigating to Dinner Plan", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mensuration_food_option.this, dinner_plan_mensuration.class));
        });

        // Set up the Snack arrow button
        ImageView btnSnackArrow = findViewById(R.id.btn_snack_arrow);
        btnSnackArrow.setOnClickListener(v -> {
            Toast.makeText(mensuration_food_option.this, "Navigating to Snack Plan", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mensuration_food_option.this, veg_snack_men.class));
        });
    }
}