package com.simats.bloodbuddy;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderReceiver";
    private static final String CHANNEL_ID = "bloodbuddy_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String mealType = intent.getStringExtra("meal_type");
            if (mealType == null) {
                Log.e(TAG, "No meal type provided");
                return;
            }

            // Check notification permission for Android 13 and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Notification permission not granted");
                    return;
                }
            }

            // Create the notification content
            String title = "BloodBuddy Meal Reminder";
            String message = getMealMessage(mealType);

            // Create notification channel for Android O and above
            createNotificationChannel(context);

            // Create an intent for when notification is tapped
            Intent tapIntent = new Intent(context, NotificationsActivity.class);
            tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                           | Intent.FLAG_ACTIVITY_CLEAR_TOP 
                           | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            tapIntent.putExtra("notification_title", title);
            tapIntent.putExtra("notification_message", message);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                getMealTypeId(mealType),
                tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 250, 500});

            // Show the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(getMealTypeId(mealType), builder.build());
            Log.d(TAG, "Notification shown for " + mealType);

            // Schedule next day's notification
            scheduleNextNotification(context, mealType);

        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Meal Reminders",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for meal times");
            channel.enableVibration(true);

            NotificationManager notificationManager = 
                context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private String getMealMessage(String mealType) {
        switch (mealType.toLowerCase()) {
            case "breakfast":
                return "Time for a healthy breakfast! Start your day right.";
            case "lunch":
                return "It's lunch time! Take a break and enjoy your meal.";
            case "dinner":
                return "Dinner time! Remember to eat a balanced meal.";
            case "snack":
                return "Time for a healthy snack! Keep your energy up.";
            default:
                return "It's time for your " + mealType + "!";
        }
    }

    private int getMealTypeId(String mealType) {
        switch (mealType.toLowerCase()) {
            case "breakfast": return 1001;
            case "lunch": return 1002;
            case "dinner": return 1003;
            case "snack": return 1004;
            default: return 1000;
        }
    }

    private void scheduleNextNotification(Context context, String mealType) {
        Intent serviceIntent = new Intent(context, NotificationSchedulerService.class);
        serviceIntent.putExtra("action", "schedule");
        serviceIntent.putExtra("meal_type", mealType);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
