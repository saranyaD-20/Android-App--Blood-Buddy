package com.simats.bloodbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private MaterialButton feedbackButton;
    private MaterialButton supportButton;
    private Button deleteAccountBtn;
    private Button logOutSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Bottom navigation setup
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
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

        // Section navigation
        findViewById(R.id.personalInfoSection).setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, Profile.class)));

        findViewById(R.id.healthDataSection).setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, HealthCondition.class)));

        findViewById(R.id.notificationsSection).setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, NotificationsActivity.class)));

        findViewById(R.id.aboutUsSection).setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class)));

        // Feedback and support buttons
        feedbackButton = findViewById(R.id.feedbackButton);
        supportButton = findViewById(R.id.supportButton);

        feedbackButton.setOnClickListener(v -> showFeedbackDialog());
        supportButton.setOnClickListener(v -> showSupportDialog());

        // Log out button
        logOutSection = findViewById(R.id.logOutSection);
        logOutSection.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Delete account button setup
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);
        deleteAccountBtn.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int userId = getUserId();
                    if (userId != -1) {
                        deleteAccount(userId);
                    } else {
                        Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccount(int userId) {
        String url = "http://10.196.132.168/delete_account.php"; // Replace with your actual URL

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();

                    // Clear session
                    SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();

                    // Redirect to main screen
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };

        queue.add(request);
    }

    private int getUserId() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        return preferences.getInt("user_id", -1); // Default -1 if not found
    }

    private void showFeedbackDialog() {
        View feedbackView = getLayoutInflater().inflate(R.layout.dialog_feedback, null);
        TextInputEditText subjectEditText = feedbackView.findViewById(R.id.subjectEditText);
        TextInputEditText feedbackEditText = feedbackView.findViewById(R.id.feedbackEditText);
        RatingBar ratingBar = feedbackView.findViewById(R.id.ratingBar);
        RadioGroup feedbackTypeGroup = feedbackView.findViewById(R.id.feedbackTypeGroup);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Send Feedback")
                .setView(feedbackView)
                .setPositiveButton("Send", (dialog, which) -> {
                    String subject = subjectEditText.getText().toString().trim();
                    String feedback = feedbackEditText.getText().toString().trim();
                    float rating = ratingBar.getRating();

                    int selectedId = feedbackTypeGroup.getCheckedRadioButtonId();
                    String feedbackType = "General";
                    if (selectedId == R.id.radioBug) {
                        feedbackType = "Bug Report";
                    } else if (selectedId == R.id.radioFeature) {
                        feedbackType = "Feature Request";
                    }

                    if (!subject.isEmpty() && !feedback.isEmpty()) {
                        submitFeedback(feedbackType, subject, feedback, rating);
                    } else {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSupportDialog() {
        View supportView = getLayoutInflater().inflate(R.layout.dialog_support, null);
        MaterialButton emailButton = supportView.findViewById(R.id.btnEmailSupport);
        MaterialButton callButton = supportView.findViewById(R.id.btnCallSupport);
        MaterialButton chatButton = supportView.findViewById(R.id.btnLiveChat);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Support")
                .setView(supportView)
                .setPositiveButton("Close", null)
                .show();

        emailButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@bloodbuddy.com"));
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+15551234567"));
            startActivity(callIntent);
        });

        chatButton.setOnClickListener(v ->
                Toast.makeText(this, "Live chat feature coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void submitFeedback(String type, String subject, String feedback, float rating) {
        String message = String.format("Thank you for your %s!\nRating: %.1f stars", type, rating);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
