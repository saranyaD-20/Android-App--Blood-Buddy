package com.simats.bloodbuddy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI
        TextView welcomeText = findViewById(R.id.welcomeText);

        // Retrieve userId from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("BloodBuddyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            welcomeText.setText("Error: User not logged in");
            return;
        }

        // Retrieve saved preferences (stored after DietaryPreferencesActivity)
        String healthCondition = prefs.getString("health_condition", "Not set");
        String dietType = prefs.getString("diet_type", "Not set");
        String cuisinePrefs = prefs.getString("cuisine_preferences", "None");
        String allergies = prefs.getString("allergies", "None");

        // Display personalized message
        String welcomeMessage = String.format(
                "Welcome, User %d!\n\n" +
                        "Health Condition: %s\n" +
                        "Diet Type: %s\n" +
                        "Cuisine Preferences: %s\n" +
                        "Allergies: %s\n\n" +
                        "Explore personalized meal plans based on your preferences!",
                userId, healthCondition, dietType, cuisinePrefs, allergies
        );
        welcomeText.setText(welcomeMessage);

        /*
        // Alternative: Fetch preferences from backend
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("YOUR_BACKEND_URL_HERE?user_id=" + userId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network error: " + e.getMessage());
                runOnUiThread(() -> welcomeText.setText("Error fetching preferences"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d(TAG, "Response: " + res);
                try {
                    JSONObject json = new JSONObject(res);
                    String healthCondition = json.getString("health_condition");
                    String dietType = json.getString("diet_type");
                    String cuisinePrefs = json.getString("cuisine_preferences");
                    String allergies = json.getString("allergies");
                    String message = String.format(
                            "Welcome, User %d!\n\n" +
                            "Health Condition: %s\n" +
                            "Diet Type: %s\n" +
                            "Cuisine Preferences: %s\n" +
                            "Allergies: %s",
                            userId, healthCondition, dietType, cuisinePrefs, allergies
                    );
                    runOnUiThread(() -> welcomeText.setText(message));
                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error", e);
                    runOnUiThread(() -> welcomeText.setText("Error parsing preferences"));
                }
            }
        });
        */
    }
}