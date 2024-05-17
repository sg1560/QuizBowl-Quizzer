package com.example.androidexample.Chat;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.androidexample.R;
import com.example.androidexample.User.UserObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends ArrayAdapter<ChatObject> {
    private UserObject viewer;
    public ChatListAdapter(Context context, UserObject viewer, List<ChatObject> items) {
        super(context, 0, items);
        this.viewer = viewer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatObject userItem = getItem(position);


        // Check if an existing view is being reused, otherwise inflate the view
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            if (userItem.getSender() != null && userItem.getSender().getId() == viewer.getId()) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.test_chat_item_l, parent, false);
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.test_chat_item_r, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.test_chat_item_l, parent, false);
            }
        }


        // Lookup view for data population
        TextView userName = convertView.findViewById(R.id.itemUserName);
        TextView textBody = convertView.findViewById(R.id.itemText);
        TextView timeText = convertView.findViewById(R.id.itemTime);

        // Populate the data into the template view using the data object
        if (userItem.getSender().getId() != viewer.getId()) {
            if (userItem.getSender() != null && userName != null) {
                userName.setText(userItem.getSender().getUserName());
            }
        }
        textBody.setText(userItem.getTextBody());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        timeText.setText(currentTime);

        // Return the completed view to render on screen
        return convertView;
    }

}

