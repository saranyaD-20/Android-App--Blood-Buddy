package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class diabetes_food_option extends AppCompatActivity {

    private static final String TAG = "DiabetesFoodOption";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diabetes_food_option);

        // Find the BottomNavigationView
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation == null) {
            Log.e(TAG, "BottomNavigationView not found in layout");
            Toast.makeText(this, "BottomNavigationView not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Prevent default selection from interfering
        bottomNavigation.setSelectedItemId(-1); // No default selection

        // Set up the navigation item selected listener
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d(TAG, "BottomNavigationView item clicked: " + itemId);
            if (itemId == R.id.nav_profile) {
                Log.d(TAG, "Navigating to Profile");
                Intent intent = new Intent(diabetes_food_option.this, Profile.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_home) {
                Log.d(TAG, "Navigating to HealthCondition from BottomNavigationView");
                Intent intent = new Intent(diabetes_food_option.this, HealthCondition.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));
                Log.d(TAG, "Notifications clicked");
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_settings) {
                Log.d(TAG, "Settings clicked");
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Breakfast LinearLayout navigation
        LinearLayout btnBreakfast = findViewById(R.id.btn_breakfast);
        if (btnBreakfast == null) {
            Log.e(TAG, "Breakfast button not found in layout");
            Toast.makeText(this, "Breakfast button not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        btnBreakfast.setOnClickListener(v -> {
            Log.d(TAG, "Breakfast button clicked");
            try {
                Toast.makeText(this, "Navigating to Breakfast Plan", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(diabetes_food_option.this, breakfast_plan_diabetes.class);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    Log.d(TAG, "Successfully started breakfast_plan_diabetes activity");
                } else {
                    Log.e(TAG, "Breakfast plan activity not found");
                    Toast.makeText(this, "Breakfast Plan activity not found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to Breakfast Plan: " + e.getMessage(), e);
                Toast.makeText(this, "Error navigating to Breakfast Plan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        // Ensure the Breakfast button consumes the touch event to prevent propagation
        btnBreakfast.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "Breakfast button touched (ACTION_DOWN)");
            }
            v.performClick();
            return true; // Consume the touch event to prevent propagation
        });

        // Lunch LinearLayout navigation
        LinearLayout btnLunchArrow = findViewById(R.id.btn_lunch_arrow);
        if (btnLunchArrow == null) {
            Log.e(TAG, "Lunch button not found in layout");
            Toast.makeText(this, "Lunch button not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        btnLunchArrow.setOnClickListener(v -> {
            Log.d(TAG, "Lunch button clicked");
            try {
                Toast.makeText(this, "Navigating to Lunch Plan", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(diabetes_food_option.this, lunch_plan_diabetes.class);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Lunch plan activity not found");
                    Toast.makeText(this, "Lunch Plan activity not found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to Lunch Plan: " + e.getMessage(), e);
                Toast.makeText(this, "Error navigating to Lunch Plan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        // Dinner LinearLayout navigation
        LinearLayout btnDinner = findViewById(R.id.btn_dinner);
        if (btnDinner == null) {
            Log.e(TAG, "Dinner button not found in layout");
            Toast.makeText(this, "Dinner button not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        btnDinner.setOnClickListener(v -> {
            Log.d(TAG, "Dinner button clicked");
            try {
                Toast.makeText(this, "Navigating to Dinner Plan", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(diabetes_food_option.this, dinner_plan_diabetes.class);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Dinner plan activity not found");
                    Toast.makeText(this, "Dinner Plan activity not found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to Dinner Plan: " + e.getMessage(), e);
                Toast.makeText(this, "Error navigating to Dinner Plan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        // Snack LinearLayout navigation
        LinearLayout btnSnack = findViewById(R.id.snack);
        if (btnSnack == null) {
            Log.e(TAG, "Snack button not found in layout");
            Toast.makeText(this, "Snack button not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        btnSnack.setOnClickListener(v -> {
            Log.d(TAG, "Snack button clicked");
            try {
                Toast.makeText(this, "Navigating to Snack Plan", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(diabetes_food_option.this, snack_plan_diabetes.class);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Snack plan activity not found");
                    Toast.makeText(this, "Snack Plan activity not found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to Snack Plan: " + e.getMessage(), e);
                Toast.makeText(this, "Error navigating to Snack Plan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }
}