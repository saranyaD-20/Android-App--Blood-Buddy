package com.simats.bloodbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the button by ID (Ensure the ID matches exactly)
        Button getStartedButton = findViewById(R.id.btn_get_started);

        // Set click listener
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RegisterActivitvy
                Intent intent = new Intent(MainActivity.this, registeractivity.class);
                startActivity(intent);
            }
        });
    }
}
