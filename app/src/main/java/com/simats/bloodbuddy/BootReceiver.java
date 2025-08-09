package com.simats.bloodbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "Device booted, restarting notifications");
            
            // Start the notification scheduler service
            Intent serviceIntent = new Intent(context, NotificationSchedulerService.class);
            serviceIntent.putExtra("action", "schedule");
            context.startService(serviceIntent);
        }
    }
} 