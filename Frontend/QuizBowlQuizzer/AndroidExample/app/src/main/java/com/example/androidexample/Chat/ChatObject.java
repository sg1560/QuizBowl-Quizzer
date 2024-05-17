package com.example.androidexample.Chat;

import com.example.androidexample.User.UserObject;

public class ChatObject {
    private UserObject sender;
    private String textBody;

    public ChatObject(UserObject sender, String textBody) {
        this.sender = sender;
        this.textBody = textBody;
    }

    public UserObject getSender() {
        return sender;
    }

    public String getTextBody() {
        return textBody;
    }
}
