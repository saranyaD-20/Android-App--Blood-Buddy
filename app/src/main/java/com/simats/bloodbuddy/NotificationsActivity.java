package com.simats.bloodbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private ListView notificationListView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize views
        notificationListView = findViewById(R.id.notification_list_view);
        emptyView = findViewById(R.id.empty_view);
        ImageButton settingsButton = findViewById(R.id.settings_button);

        // Initialize list and adapter
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationList);
        notificationListView.setAdapter(adapter);
        notificationListView.setEmptyView(emptyView);

        // Set up settings button
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationSettingsActivity.class);
            startActivity(intent);
        });

        // Set up bottom navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_notifications);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(this, Profile.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, HealthCondition.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }

        // Load existing notifications
        loadNotifications();

        // Check if opened from notification
        if (getIntent().hasExtra("notification_title")) {
            handleNotificationIntent(getIntent());
        }
    }

    private void loadNotifications() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String notificationsJson = prefs.getString(KEY_NOTIFICATIONS, "[]");
        Log.d(TAG, "Loading notifications from JSON: " + notificationsJson);

        try {
            JSONArray jsonArray = new JSONArray(notificationsJson);
            notificationList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                NotificationItem item = new NotificationItem(
                        obj.getString("title"),
                        obj.getString("message"),
                        obj.optLong("timestamp", System.currentTimeMillis())
                );
                notificationList.add(item);
                Log.d(TAG, "Loaded notification: " + item.getTitle());
            }

            // Sort notifications by timestamp (newest first)
            notificationList.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                if (notificationList.isEmpty()) {
                    emptyView.setVisibility(TextView.VISIBLE);
                    notificationListView.setVisibility(ListView.GONE);
                } else {
                    emptyView.setVisibility(TextView.GONE);
                    notificationListView.setVisibility(ListView.VISIBLE);
                }
            });

            Log.d(TAG, "Loaded " + notificationList.size() + " notifications");
        } catch (JSONException e) {
            Log.e(TAG, "Error loading notifications: " + e.getMessage());
            Toast.makeText(this, "Error loading notifications", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNotification(String title, String message) {
        Log.d(TAG, "Adding new notification - Title: " + title + ", Message: " + message);

        NotificationItem newNotification = new NotificationItem(
                title,
                message,
                System.currentTimeMillis()
        );

        notificationList.add(0, newNotification);
        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            if (notificationList.isEmpty()) {
                emptyView.setVisibility(TextView.VISIBLE);
                notificationListView.setVisibility(ListView.GONE);
            } else {
                emptyView.setVisibility(TextView.GONE);
                notificationListView.setVisibility(ListView.VISIBLE);
            }
        });

        saveNotifications();
        Toast.makeText(this, "Notification added", Toast.LENGTH_SHORT).show();
    }

    private void saveNotifications() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (NotificationItem item : notificationList) {
                JSONObject obj = new JSONObject();
                obj.put("title", item.getTitle());
                obj.put("message", item.getMessage());
                obj.put("timestamp", item.getTimestamp());
                jsonArray.put(obj);
            }

            String jsonString = jsonArray.toString();
            Log.d(TAG, "Saving notifications to JSON: " + jsonString);

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_NOTIFICATIONS, jsonString);
            editor.apply();

            Log.d(TAG, "Successfully saved " + notificationList.size() + " notifications");
        } catch (Exception e) {
            Log.e(TAG, "Error saving notifications: " + e.getMessage());
            Toast.makeText(this, "Error saving notifications", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null && intent.hasExtra("notification_title")) {
            try {
                String title = intent.getStringExtra("notification_title");
                String message = intent.getStringExtra("notification_message");
                if (title != null && message != null) {
                    addNotification(title, message);
                    Log.d(TAG, "handleNotificationIntent: Added notification - " + title);
                } else {
                    Log.w(TAG, "handleNotificationIntent: Null title or message");
                }
            } catch (Exception e) {
                Log.e(TAG, "handleNotificationIntent: Error - " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}