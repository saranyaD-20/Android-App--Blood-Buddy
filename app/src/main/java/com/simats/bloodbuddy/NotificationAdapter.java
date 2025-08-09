package com.simats.bloodbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends ArrayAdapter<NotificationItem> {
    private final SimpleDateFormat dateFormat;

    public NotificationAdapter(Context context, List<NotificationItem> items) {
        super(context, R.layout.notification_item, items);
        dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.notification_item, parent, false);
        }

        NotificationItem item = getItem(position);
        if (item != null) {
            TextView titleView = convertView.findViewById(R.id.notification_title);
            TextView messageView = convertView.findViewById(R.id.notification_message);
            TextView timeView = convertView.findViewById(R.id.notification_time);

            titleView.setText(item.getTitle());
            messageView.setText(item.getMessage());
            timeView.setText(dateFormat.format(new java.util.Date(item.getTimestamp())));
        }

        return convertView;
    }
}