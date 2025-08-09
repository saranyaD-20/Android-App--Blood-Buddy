package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonResetPassword;
    private TextView textViewBackToLogin;

    // Replace with your backend URL
    private static final String RESET_PASSWORD_URL = IPv4Connection.getBaseUrl()+"reset_password.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin);

        // Reset Password button click
        buttonResetPassword.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ForgotPassword.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else {
                sendResetRequest(email);
            }
        });

        // Back to login
        textViewBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void sendResetRequest(String email) {
        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    RESET_PASSWORD_URL,
                    requestBody,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Toast.makeText(ForgotPassword.this, message, Toast.LENGTH_LONG).show();

                            if (status.equals("success")) {
                                // Optionally go back to login
                                Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ForgotPassword.this, "Response error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(ForgotPassword.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            );

            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ForgotPassword.this, "Failed to create request", Toast.LENGTH_SHORT).show();
        }
    }
}
