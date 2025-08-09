package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HealthCondition extends AppCompatActivity {

    private static final String TAG = "HealthCondition";
    private int userId = 1; // TODO: Replace with actual user ID from SharedPreferences or DB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_condition);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation == null) {
            Log.e(TAG, "BottomNavigationView not found in layout");
            Toast.makeText(this, "BottomNavigationView not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Prevent default selection from interfering
        bottomNavigation.setSelectedItemId(-1); // No default selection

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d(TAG, "BottomNavigationView item clicked: " + itemId);

            if (itemId == R.id.nav_home) {
                Log.d(TAG, "Already in Home");
                return true;
            } else if (itemId == R.id.nav_notifications) {
                Log.d(TAG, "Navigating to NotificationsActivity");
                Intent intent = new Intent(this, NotificationsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Log.d(TAG, "Navigating to Profile");
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_settings) {
                Log.d(TAG, "Navigating to SettingsActivity");
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });

        // Set up chatbot button
        FloatingActionButton fabChatbot = findViewById(R.id.fab_chatbot);
        if (fabChatbot != null) {
            fabChatbot.setOnClickListener(v -> {
                Log.d(TAG, "Chatbot button clicked");
                Intent intent = new Intent(HealthCondition.this, ChatbotActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e(TAG, "Chatbot button not found in layout");
        }

        // Set up health condition button clicks
        setupHealthConditionClick(R.id.btnAnemia, "Anemia", DietPreferencesActivity.class);
        setupHealthConditionClick(R.id.btnHypertension, "Hypertension", deit_hypertension.class);
        setupHealthConditionClick(R.id.btnDiabetes, "Diabetes", deit_diabetes.class);
        setupHealthConditionClick(R.id.btnMenstruation, "Menstruation", diet_men.class);
        setupHealthConditionClick(R.id.btnPregnancy, "Menstruation", deit_per.class);
    }

    private void setupHealthConditionClick(int buttonId, String condition, Class<?> targetActivity) {
        LinearLayout button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> {
                Log.d(TAG, condition + " clicked");
                startActivity(new Intent(HealthCondition.this, targetActivity));
            });
        } else {
            Log.e(TAG, "Button for " + condition + " not found in layout");
        }
    }
}