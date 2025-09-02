package com.baghdadhomes.fcm;

import com.google.gson.annotations.SerializedName;

public class RootModel {

    @SerializedName("message") //  "to" changed to token
    private NotificationInfo message;

    public RootModel(NotificationInfo message) {
        this.message = message;
    }

    public NotificationInfo getMessage() {
        return message;
    }

    public void setMessage(NotificationInfo message) {
        this.message = message;
    }
}
