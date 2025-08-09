package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class SecurityPrivacyActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_privacy);

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

        // Find the EditText fields and Save button
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Set an OnClickListener for the Save Changes button
        btnSaveChanges.setOnClickListener(v -> {
            // Validate the input fields
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(currentPassword)) {
                etCurrentPassword.setError("Current password is required");
                return;
            }

            if (TextUtils.isEmpty(newPassword)) {
                etNewPassword.setError("New password is required");
                return;
            }

            if (newPassword.length() < 6) {
                etNewPassword.setError("New password must be at least 6 characters");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            // If validation passes, show a success message
            // In a real app, you would also update the password in your backend or local storage
            Snackbar.make(v, "Saved Successfully", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.pink))
                    .setTextColor(getResources().getColor(android.R.color.white))
                    .show();

            // Clear the fields after saving
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");
        });
    }
}