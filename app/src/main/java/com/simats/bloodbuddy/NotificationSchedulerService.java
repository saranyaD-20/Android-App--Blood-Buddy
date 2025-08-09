package com.simats.bloodbuddy;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationSchedulerService extends Service {

    private static final String TAG = "NotificationScheduler";
    private static final String PREFS_NAME = "BloodBuddyPrefs";
    private static final String KEY_BREAKFAST_TIME = "breakfast_time";
    private static final String KEY_LUNCH_TIME = "lunch_time";
    private static final String KEY_DINNER_TIME = "dinner_time";
    private static final String KEY_SNACK_TIME = "snack_time";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String CHANNEL_ID = "bloodbuddy_service";
    private static final String REMINDERS_CHANNEL_ID = "bloodbuddy_reminders";
    private static final int FOREGROUND_ID = 9999;

    private SharedPreferences preferences;
    private AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        createNotificationChannels();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // Start foreground service with the correct type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(FOREGROUND_ID, createForegroundNotification(), 
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
            } else {
                startForeground(FOREGROUND_ID, createForegroundNotification());
            }

            if (intent != null && intent.hasExtra("action")) {
                String action = intent.getStringExtra("action");
                Log.d(TAG, "Received action: " + action);
                
                switch (action) {
                    case "schedule":
                        scheduleAllNotifications();
                        break;
                    case "cancel":
                        cancelAllNotifications();
                        break;
                    case "update":
                        updateNotificationTime(
                            intent.getStringExtra("meal_type"),
                            intent.getIntExtra("hour", 8),
                            intent.getIntExtra("minute", 0)
                        );
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in service: " + e.getMessage());
        } finally {
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scheduleAllNotifications() {
        // Check if notifications are enabled
        boolean notificationsEnabled = preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
        if (!notificationsEnabled) {
            Log.d(TAG, "Notifications are disabled");
            return;
        }

        // Schedule breakfast notification
        scheduleNotification("breakfast", 
            preferences.getInt("breakfast_time_hour", 8),
            preferences.getInt("breakfast_time_minute", 0));
        
        // Schedule lunch notification
        scheduleNotification("lunch", 
            preferences.getInt("lunch_time_hour", 12),
            preferences.getInt("lunch_time_minute", 0));
        
        // Schedule dinner notification
        scheduleNotification("dinner", 
            preferences.getInt("dinner_time_hour", 18),
            preferences.getInt("dinner_time_minute", 0));
        
        // Schedule snack notification
        scheduleNotification("snack", 
            preferences.getInt("snack_time_hour", 15),
            preferences.getInt("snack_time_minute", 30));
        
        Log.d(TAG, "All notifications scheduled");
    }

    private void scheduleNotification(String mealType, int hour, int minute) {
        try {
            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("meal_type", mealType);
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 
                getMealTypeId(mealType), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            
            // If the time has already passed today, schedule for tomorrow
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAlarmClock(
                        new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent),
                        pendingIntent
                    );
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                    );
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                );
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                );
            }
            
            Log.d(TAG, "Scheduled " + mealType + " notification for " + hour + ":" + minute);
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling notification: " + e.getMessage());
        }
    }

    private void cancelAllNotifications() {
        // Cancel breakfast notification
        cancelNotification("breakfast");
        
        // Cancel lunch notification
        cancelNotification("lunch");
        
        // Cancel dinner notification
        cancelNotification("dinner");
        
        // Cancel snack notification
        cancelNotification("snack");
        
        Log.d(TAG, "All notifications cancelled");
    }

    private void cancelNotification(String mealType) {
        try {
            Intent intent = new Intent(this, ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 
                getMealTypeId(mealType), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "Cancelled " + mealType + " notification");
        } catch (Exception e) {
            Log.e(TAG, "Error cancelling notification: " + e.getMessage());
        }
    }

    private void updateNotificationTime(String mealType, int hour, int minute) {
        // Save the new time to preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(mealType + "_time_hour", hour);
        editor.putInt(mealType + "_time_minute", minute);
        editor.apply();
        
        // Cancel the existing notification
        cancelNotification(mealType);
        
        // Schedule a new notification with the updated time
        scheduleNotification(mealType, hour, minute);
        
        Log.d(TAG, "Updated " + mealType + " notification time to " + hour + ":" + minute);
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

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                // Create channel for service notifications
                NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "BloodBuddy Service",
                    NotificationManager.IMPORTANCE_LOW
                );
                serviceChannel.setDescription("Background service notifications");
                manager.createNotificationChannel(serviceChannel);

                // Create channel for meal reminders
                NotificationChannel remindersChannel = new NotificationChannel(
                    REMINDERS_CHANNEL_ID,
                    "Meal Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                );
                remindersChannel.setDescription("Meal time reminders");
                remindersChannel.enableVibration(true);
                manager.createNotificationChannel(remindersChannel);
            }
        }
    }

    private Notification createForegroundNotification() {
        Intent notificationIntent = new Intent(this, NotificationSettingsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BloodBuddy")
            .setContentText("Updating meal reminders...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build();
    }
} 