package com.framgia.ishipper.server;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vuduychuong1994 on 8/4/16.
 */
public class Response {
    @SerializedName("status")
    private int mStatus;

    @SerializedName("message")
    private String mMessage;

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
