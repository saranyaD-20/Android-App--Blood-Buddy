package com.simats.bloodbuddy;

import android.content.Context;
import android.content.SharedPreferences;

public class IPv4Connection {
    private static final String PREF_NAME = "MyPrefs";
    private static final String CONNECTION_ID_KEY = "connectionId";
    private static String baseUrl = "http://14.139.187.229:8081/bloodbuddy/";

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        IPv4Connection.baseUrl = baseUrl;
    }

    private static String connectionId;

    public static String getConnectionId(Context context) {
        if (connectionId == null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            connectionId = prefs.getString(CONNECTION_ID_KEY, null);
        }
        return connectionId;
    }

    public static void setConnectionId(Context context, String connectionId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CONNECTION_ID_KEY, connectionId);
        editor.apply();

        IPv4Connection.connectionId = connectionId;
    }
}