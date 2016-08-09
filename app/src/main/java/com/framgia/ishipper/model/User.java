package com.framgia.ishipper.model;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vuduychuong1994 on 8/3/16.
 */
public class User {
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_SHOP = "shop";
    public static final String ROLE_SHIPPER = "shipper";

    @SerializedName("name") private String mName;

    @SerializedName("email") private String mEmail;

    @SerializedName("address") private String mAddress;

    @SerializedName("latitude") private Float mLatitude;

    @SerializedName("longitude") private Float mLongitude;

    @SerializedName("phone_number") private String mPhoneNumber;

    @SerializedName("plate_number") private String mPlateNumber;

    @SerializedName("status") private String mStatus;

    @SerializedName("role") private String mRole;

    @SerializedName("rate") private Float mRate;

    @SerializedName("pin") private String mPin;

    @SerializedName("authentication_token") private String mAuthenticationToken;

    @SerializedName("signed_in") private boolean mIsSignedIn;

    @SerializedName("password") private String mPassword;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public Float getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Float latitude) {
        mLatitude = latitude;
    }

    public Float getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Float longitude) {
        mLongitude = longitude;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getPlateNumber() {
        return mPlateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        mPlateNumber = plateNumber;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }

    public Float getRate() {
        return mRate;
    }

    public void setRate(Float rate) {
        mRate = rate;
    }

    public String getPin() {
        return mPin;
    }

    public void setPin(String pin) {
        mPin = pin;
    }

    public String getAuthenticationToken() {
        return mAuthenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        mAuthenticationToken = authenticationToken;
    }

    public boolean isSignedIn() {
        return mIsSignedIn;
    }

    public void setSignedIn(boolean signedIn) {
        mIsSignedIn = signedIn;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
