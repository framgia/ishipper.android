package com.framgia.ishipper.model;
import com.google.gson.GsonBuilder;

/**
 * Created by vuduychuong1994 on 8/3/16.
 */
public class User {
    public static final int ROLE_SHOP = 0;
    public static final int ROLE_SHIPPER = 1;

    private String mPhoneNumber;
    private String mPassword;
    private int mRole;
    private String mName;

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getRole() {
        return mRole;
    }

    public void setRole(int role) {
        mRole = role;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
