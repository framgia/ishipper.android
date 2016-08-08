package com.framgia.ishipper.net;

import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HungNT on 8/5/16.
 */

interface APIError {
    /* Common */
    public int BAD_REQUEST = 400;
    public int DATA_NOT_FOUND = 404;

    /* Security */
    public int REQUEST_NOT_AUTHORIZED = 401;

    /* Local Error */
    public int LOCAL_ERROR = 1111;

}

public abstract class APIResponse<T> {
    private static final String TAG = "APIResponse";

    public boolean success;


    @SerializedName("code")
    private int mCode;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("data")
    private T mData;

    public static class PutUpdateProfileResponse extends APIResponse {

        public User user;

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }
}
