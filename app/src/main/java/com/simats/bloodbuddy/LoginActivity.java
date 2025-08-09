package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL = IPv4Connection.getBaseUrl() + "login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.forgotPassword);
        TextView tvSignUp = findViewById(R.id.tvSignUp);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email and Password are required", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        Log.d("URL" , LOGIN_URL );

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, registeractivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            String userName = jsonObject.optString("name", "User");
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HealthCondition.class); // Replace with your actual dashboard activity
                                intent.putExtra("userName", userName);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            });
                        }

                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("LoginActivity", "JSON Parsing Error: " + e.getMessage());
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                    Log.e("LoginActivity", "Server Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login failed. Please check your network.", Toast.LENGTH_SHORT).show();
                });
                Log.e("LoginActivity", "Network Error: " + e.getMessage());
            }
        });
    }
}



