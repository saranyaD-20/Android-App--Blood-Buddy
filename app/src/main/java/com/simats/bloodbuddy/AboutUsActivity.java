package com.simats.bloodbuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

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
                return true;
            }
            return false;
        });

        // Find the Email and Website sections
        View emailSection = findViewById(R.id.emailSection);


        // Set an OnClickListener for the Email section
        emailSection.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@bloodbuddy.com"));
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        // Set an OnClickListener for the Website section

    }
}