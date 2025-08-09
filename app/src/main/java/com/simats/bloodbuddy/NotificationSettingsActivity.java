package com.simats.bloodbuddy;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class NotificationSettingsActivity extends AppCompatActivity {
    private static final String TAG = "NotificationSettings";
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private TextView breakfastTimeText, lunchTimeText, dinnerTimeText, snackTimeText;
    private Button saveButton, clearAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        breakfastTimeText = findViewById(R.id.breakfast_time_text);
        lunchTimeText = findViewById(R.id.lunch_time_text);
        dinnerTimeText = findViewById(R.id.dinner_time_text);
        snackTimeText = findViewById(R.id.snack_time_text);
        saveButton = findViewById(R.id.save_button);
        clearAllButton = findViewById(R.id.clear_all_button);

        // Load saved times
        loadSavedTimes();

        // Set up time picker dialogs
        setupTimePickerDialog(breakfastTimeText, "breakfast");
        setupTimePickerDialog(lunchTimeText, "lunch");
        setupTimePickerDialog(dinnerTimeText, "dinner");
        setupTimePickerDialog(snackTimeText, "snack");

        // Save button click listener
        saveButton.setOnClickListener(v -> saveNotificationSettings());

        // Clear all button click listener
        clearAllButton.setOnClickListener(v -> showClearAllConfirmationDialog());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button in the action bar
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Ensure proper navigation
        finish();
    }

    private void loadSavedTimes() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        breakfastTimeText.setText(prefs.getString("breakfast_time", "Set Time"));
        lunchTimeText.setText(prefs.getString("lunch_time", "Set Time"));
        dinnerTimeText.setText(prefs.getString("dinner_time", "Set Time"));
        snackTimeText.setText(prefs.getString("snack_time", "Set Time"));
    }

    private void setupTimePickerDialog(TextView timeText, String mealType) {
        timeText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, selectedMinute) -> {
                        String time = String.format("%02d:%02d", hourOfDay, selectedMinute);
                        timeText.setText(time);
                    }, hour, minute, true);

            timePickerDialog.show();
        });

        // Long press to delete individual reminder
        timeText.setOnLongClickListener(v -> {
            showDeleteConfirmationDialog(timeText, mealType);
            return true;
        });
    }

    private void showDeleteConfirmationDialog(TextView timeText, String mealType) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Reminder")
                .setMessage("Are you sure you want to delete this reminder?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteReminder(timeText, mealType);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReminder(TextView timeText, String mealType) {
        // Cancel the alarm
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("meal_type", mealType);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, getMealTypeRequestCode(mealType),
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        // Reset the text
        timeText.setText("Set Time");

        // Remove from SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove(mealType + "_time");
        editor.apply();

        // Remove corresponding notification
        removeReminderNotification(mealType);

        Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show();
    }

    private void showClearAllConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Reminders")
                .setMessage("Are you sure you want to delete all reminders?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    clearAllReminders();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearAllReminders() {
        // Cancel all alarms
        String[] mealTypes = {"breakfast", "lunch", "dinner", "snack"};
        for (String mealType : mealTypes) {
            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("meal_type", mealType);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, getMealTypeRequestCode(mealType),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }

        // Reset all text views
        breakfastTimeText.setText("Set Time");
        lunchTimeText.setText("Set Time");
        dinnerTimeText.setText("Set Time");
        snackTimeText.setText("Set Time");

        // Clear SharedPreferences for times
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove("breakfast_time");
        editor.remove("lunch_time");
        editor.remove("dinner_time");
        editor.remove("snack_time");
        editor.apply();

        // Remove all reminder notifications
        removeAllReminderNotifications();

        Toast.makeText(this, "All reminders cleared", Toast.LENGTH_SHORT).show();
    }

    private void removeReminderNotification(String mealType) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String notificationsJson = prefs.getString(KEY_NOTIFICATIONS, "[]");
            JSONArray jsonArray = new JSONArray(notificationsJson);
            JSONArray newArray = new JSONArray();

            String targetTitle = mealType.substring(0, 1).toUpperCase() + mealType.substring(1) + " Reminder";

            // Filter out the specific reminder notification
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (!obj.getString("title").equals(targetTitle)) {
                    newArray.put(obj);
                }
            }

            // Save updated array
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_NOTIFICATIONS, newArray.toString());
            editor.apply();

            Log.d(TAG, "Removed notification for: " + targetTitle);
        } catch (Exception e) {
            Log.e(TAG, "Error removing reminder notification: " + e.getMessage());
            Toast.makeText(this, "Error clearing notification", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeAllReminderNotifications() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String notificationsJson = prefs.getString(KEY_NOTIFICATIONS, "[]");
            JSONArray jsonArray = new JSONArray(notificationsJson);
            JSONArray newArray = new JSONArray();

            // Filter out all reminder notifications
            String[] reminderTitles = {"Breakfast Reminder", "Lunch Reminder", "Dinner Reminder", "Snack Reminder"};
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                boolean isReminder = false;
                for (String title : reminderTitles) {
                    if (obj.getString("title").equals(title)) {
                        isReminder = true;
                        break;
                    }
                }
                if (!isReminder) {
                    newArray.put(obj);
                }
            }

            // Save updated array
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_NOTIFICATIONS, newArray.toString());
            editor.apply();

            Log.d(TAG, "Removed all reminder notifications");
        } catch (Exception e) {
            Log.e(TAG, "Error removing all reminder notifications: " + e.getMessage());
            Toast.makeText(this, "Error clearing notifications", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNotificationSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        boolean hasChanges = false;

        // Save times to SharedPreferences
        String breakfastTime = breakfastTimeText.getText().toString();
        String lunchTime = lunchTimeText.getText().toString();
        String dinnerTime = dinnerTimeText.getText().toString();
        String snackTime = snackTimeText.getText().toString();

        if (!breakfastTime.equals("Set Time")) {
            editor.putString("breakfast_time", breakfastTime);
            scheduleNotification("breakfast", breakfastTime);
            updateOrAddNotification("Breakfast Reminder", "Reminder set for " + breakfastTime);
            hasChanges = true;
        }
        if (!lunchTime.equals("Set Time")) {
            editor.putString("lunch_time", lunchTime);
            scheduleNotification("lunch", lunchTime);
            updateOrAddNotification("Lunch Reminder", "Reminder set for " + lunchTime);
            hasChanges = true;
        }
        if (!dinnerTime.equals("Set Time")) {
            editor.putString("dinner_time", dinnerTime);
            scheduleNotification("dinner", dinnerTime);
            updateOrAddNotification("Dinner Reminder", "Reminder set for " + dinnerTime);
            hasChanges = true;
        }
        if (!snackTime.equals("Set Time")) {
            editor.putString("snack_time", snackTime);
            scheduleNotification("snack", snackTime);
            updateOrAddNotification("Snack Reminder", "Reminder set for " + snackTime);
            hasChanges = true;
        }

        editor.apply();

        if (hasChanges) {
            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOrAddNotification(String title, String message) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String notificationsJson = prefs.getString(KEY_NOTIFICATIONS, "[]");
            JSONArray jsonArray = new JSONArray(notificationsJson);
            boolean updated = false;

            // Look for existing notification with the same title and update it
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getString("title").equals(title)) {
                    obj.put("message", message);
                    obj.put("timestamp", System.currentTimeMillis());
                    updated = true;
                    break;
                }
            }

            // If no existing notification, add a new one
            if (!updated) {
                JSONObject newNotification = new JSONObject();
                newNotification.put("title", title);
                newNotification.put("message", message);
                newNotification.put("timestamp", System.currentTimeMillis());
                jsonArray.put(newNotification);
            }

            // Save updated array
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_NOTIFICATIONS, jsonArray.toString());
            editor.apply();

            Log.d(TAG, "Updated/Added notification: " + title);
        } catch (Exception e) {
            Log.e(TAG, "Error updating/adding notification: " + e.getMessage());
            Toast.makeText(this, "Error saving notification", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleNotification(String mealType, String time) {
        try {
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // If time has already passed today, schedule for tomorrow
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("meal_type", mealType);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    getMealTypeRequestCode(mealType),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );

            Log.d(TAG, "Scheduled " + mealType + " notification at " + time);
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling notification: " + e.getMessage());
            Toast.makeText(this, "Error scheduling notification", Toast.LENGTH_SHORT).show();
        }
    }

    private int getMealTypeRequestCode(String mealType) {
        switch (mealType.toLowerCase()) {
            case "breakfast":
                return 1;
            case "lunch":
                return 2;
            case "dinner":
                return 3;
            case "snack":
                return 4;
            default:
                return 0;
        }
    }
}