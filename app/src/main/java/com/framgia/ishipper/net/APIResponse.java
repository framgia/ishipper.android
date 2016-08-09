package com.framgia.ishipper.net;

import com.framgia.ishipper.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HungNT on 8/5/16.
 */

public class APIResponse<T> {
    private static final String TAG = "APIResponse";
    private static final int CODE_SUCCESS = 1;

    public boolean success;

    public APIResponse(int code, String message, T data) {
        mCode = code;
        mMessage = message;
        mData = data;
    }

    @SerializedName("code")
    protected int mCode;

    @SerializedName("message")
    protected String mMessage;

    @SerializedName("data")
    protected T mData;

    public static class EmptyResponse {
    }

    public boolean isSuccess() {
        return mCode == CODE_SUCCESS;
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

    public class ChangePasswordResponse {
        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public class SignInResponse {
        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}
