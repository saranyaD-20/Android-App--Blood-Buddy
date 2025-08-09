package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private EditText etName, etAge, etHeight, etWeight, etHemoglobin, etIron,
            etSystolic, etDiastolic, etPlatelets, etWBC, etVitaminB12,
            etVitaminD, etExercise, etSleep, etWater;
    private RadioGroup genderGroup;
    private Button btnSave;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize bottom navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(this, HealthCondition.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    Intent intent = new Intent(this, NotificationsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
        }

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etHemoglobin = findViewById(R.id.etHemoglobin);
        etIron = findViewById(R.id.etIron);
        etSystolic = findViewById(R.id.etSystolic);
        etDiastolic = findViewById(R.id.etDiastolic);
        etPlatelets = findViewById(R.id.etPlatelets);
        etWBC = findViewById(R.id.etWBC);
        etVitaminB12 = findViewById(R.id.etVitaminB12);
        etVitaminD = findViewById(R.id.etVitaminD);
        etExercise = findViewById(R.id.etExercise);
        etSleep = findViewById(R.id.etSleep);
        etWater = findViewById(R.id.etWater);

        genderGroup = findViewById(R.id.radioGroupGender);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Load existing profile data
        loadProfile();

        // Set save button listener
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        progressBar.setVisibility(View.VISIBLE);

        // Get input values
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String hemoglobin = etHemoglobin.getText().toString().trim();
        String iron = etIron.getText().toString().trim();
        String systolic = etSystolic.getText().toString().trim();
        String diastolic = etDiastolic.getText().toString().trim();
        String platelets = etPlatelets.getText().toString().trim();
        String wbc = etWBC.getText().toString().trim();
        String vitaminB12 = etVitaminB12.getText().toString().trim();
        String vitaminD = etVitaminD.getText().toString().trim();
        String exercise = etExercise.getText().toString().trim();
        String sleep = etSleep.getText().toString().trim();
        String water = etWater.getText().toString().trim();

        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedGenderId);
            gender = selectedRadioButton.getText().toString();
        }

        // Validate required inputs
        if (name.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty() || gender.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set default values for unknown measurements
        if (hemoglobin.isEmpty()) hemoglobin = "0";
        if (iron.isEmpty()) iron = "0";
        if (systolic.isEmpty()) systolic = "0";
        if (diastolic.isEmpty()) diastolic = "0";
        if (platelets.isEmpty()) platelets = "0";
        if (wbc.isEmpty()) wbc = "0";
        if (vitaminB12.isEmpty()) vitaminB12 = "0";
        if (vitaminD.isEmpty()) vitaminD = "0";
        if (exercise.isEmpty()) exercise = "0";
        if (sleep.isEmpty()) sleep = "0";
        if (water.isEmpty()) water = "0";

        // Create JSON request body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", 1); // Replace with dynamic user ID from login
            jsonBody.put("name", name);
            jsonBody.put("age", Integer.parseInt(age));
            jsonBody.put("gender", gender);
            jsonBody.put("height", Float.parseFloat(height));
            jsonBody.put("weight", Float.parseFloat(weight));
            jsonBody.put("hemoglobin_level", Float.parseFloat(hemoglobin));
            jsonBody.put("iron_level", Float.parseFloat(iron));
            jsonBody.put("systolic", Integer.parseInt(systolic));
            jsonBody.put("diastolic", Integer.parseInt(diastolic));
            jsonBody.put("platelets", Float.parseFloat(platelets));
            jsonBody.put("wbc", Float.parseFloat(wbc));
            jsonBody.put("vitamin_b12", Float.parseFloat(vitaminB12));
            jsonBody.put("vitamin_d", Float.parseFloat(vitaminD));
            jsonBody.put("exercise_frequency", Integer.parseInt(exercise));
            jsonBody.put("sleep_duration", Float.parseFloat(sleep));
            jsonBody.put("water_intake", Float.parseFloat(water));
        } catch (NumberFormatException | JSONException e) {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error creating request: " + e.getMessage());
            Toast.makeText(this, "Invalid input data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Define URL
        String url = IPv4Connection.getBaseUrl()+"profile.php";

        // Create Volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        Log.d(TAG, "Save response: " + response.toString()); // Log the full response
                        Toast.makeText(Profile.this, message, Toast.LENGTH_SHORT).show();

                        if ("success".equals(status)) {
                            JSONObject updatedData = response.optJSONObject("updated_data");
                            if (updatedData != null) {
                                updateUIWithData(updatedData);
                            } else {
                                Toast.makeText(Profile.this, "No updated data received", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(Profile.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(Profile.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        // Add request to queue
        requestQueue.add(jsonObjectRequest);
    }

    private void updateUIWithData(JSONObject data) {
        try {
            // Check and update fields only if they exist
            if (data.has("name")) {
                etName.setText(data.getString("name"));
            }
            if (data.has("age")) {
                etAge.setText(String.valueOf(data.getInt("age")));
            }
            if (data.has("height")) {
                etHeight.setText(String.valueOf(data.getDouble("height")));
            }
            if (data.has("weight")) {
                etWeight.setText(String.valueOf(data.getDouble("weight")));
            }
            if (data.has("hemoglobin_level")) {
                etHemoglobin.setText(String.valueOf(data.getDouble("hemoglobin_level")));
            }
            if (data.has("iron_level")) {
                etIron.setText(String.valueOf(data.getDouble("iron_level")));
            }
            if (data.has("systolic")) {
                etSystolic.setText(String.valueOf(data.getInt("systolic")));
            }
            if (data.has("diastolic")) {
                etDiastolic.setText(String.valueOf(data.getInt("diastolic")));
            }
            if (data.has("platelets")) {
                etPlatelets.setText(String.valueOf(data.getDouble("platelets")));
            }
            if (data.has("wbc")) {
                etWBC.setText(String.valueOf(data.getDouble("wbc")));
            }
            if (data.has("vitamin_b12")) {
                etVitaminB12.setText(String.valueOf(data.getDouble("vitamin_b12")));
            }
            if (data.has("vitamin_d")) {
                etVitaminD.setText(String.valueOf(data.getDouble("vitamin_d")));
            }
            if (data.has("exercise_frequency")) {
                etExercise.setText(String.valueOf(data.getInt("exercise_frequency")));
            }
            if (data.has("sleep_duration")) {
                etSleep.setText(String.valueOf(data.getDouble("sleep_duration")));
            }
            if (data.has("water_intake")) {
                etWater.setText(String.valueOf(data.getDouble("water_intake")));
            }

            // Set gender radio button
            if (data.has("gender")) {
                String gender = data.getString("gender");
                for (int i = 0; i < genderGroup.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) genderGroup.getChildAt(i);
                    if (rb.getText().toString().equals(gender)) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error updating UI with data: " + e.getMessage());
            Toast.makeText(this, "Error updating UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);
        String url = IPv4Connection.getBaseUrl()+"profile.php";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", 1); // Replace with dynamic user ID from login
        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error creating load request: " + e.getMessage());
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        String status = response.getString("status");
                        Log.d(TAG, "Load response: " + response.toString()); // Log the full response
                        if ("success".equals(status)) {
                            JSONObject updatedData = response.optJSONObject("updated_data");
                            if (updatedData != null) {
                                updateUIWithData(updatedData);
                            } else {
                                Toast.makeText(Profile.this, "No profile data available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Profile.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing load response: " + e.getMessage());
                        Toast.makeText(Profile.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error loading profile: " + error.getMessage());
                    Toast.makeText(Profile.this, "Error loading profile: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        requestQueue.add(jsonObjectRequest);
    }
}