package com.framgia.ishipper.server;
import com.framgia.ishipper.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vuduychuong1994 on 8/4/16.
 */
public class SignUpResponse {

    @SerializedName("user") private User mUser;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
