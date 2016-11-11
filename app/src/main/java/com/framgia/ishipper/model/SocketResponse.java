package com.framgia.ishipper.model;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vuduychuong1994 on 11/9/16.
 */

public class SocketResponse {
    @SerializedName("type") private String mType;
    @SerializedName("identifier") private String mIdentifier;
    @SerializedName("message") private Message mMessage;

    public String getType() {
        return mType;
    }

    public String getAction() {
        return mMessage.mAction;
    }

    public int getUnreadNotification() {
        return mMessage.mData.mUnreadNotifiction;
    }

    private class Message {
        @SerializedName("action") private String mAction;
        @SerializedName("data") private Data mData;

    }

    private class Data {
        @SerializedName("unread_notification") private int mUnreadNotifiction;
    }
}
