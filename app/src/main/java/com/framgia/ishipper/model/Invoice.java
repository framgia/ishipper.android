package com.framgia.ishipper.model;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vuduychuong1994 on 8/9/16.
 */
public class Invoice {

    @SerializedName("id") private int mId;
    @SerializedName("name") private String mName;
    @SerializedName("address_start") private String mAddressStart;
    @SerializedName("latitude_start") private Float mLatStart;
    @SerializedName("longitude_start") private Float mLngStart;
    @SerializedName("address_finish") private String mAddressFinish;
    @SerializedName("latitude_finish") private Float mLatFinish;
    @SerializedName("longitude_finish") private Float mLngFinish;
    @SerializedName("delivery_time") private String mDeliveryTime;
    @SerializedName("distance") private Float mDistance;
    @SerializedName("description") private String mDescription;
    @SerializedName("price") private Float mPrice;
    @SerializedName("shipping_price") private Float mShippingPrice;
    @SerializedName("status") private String mStatus;
    @SerializedName("weight") private Float mWeight;
    @SerializedName("user_id") private int mUserId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddressStart() {
        return mAddressStart;
    }

    public void setAddressStart(String addressStart) {
        mAddressStart = addressStart;
    }

    public Float getLatStart() {
        return mLatStart;
    }

    public void setLatStart(Float latStart) {
        mLatStart = latStart;
    }

    public Float getLngStart() {
        return mLngStart;
    }

    public void setLngStart(Float lngStart) {
        mLngStart = lngStart;
    }

    public String getAddressFinish() {
        return mAddressFinish;
    }

    public void setAddressFinish(String addressFinish) {
        mAddressFinish = addressFinish;
    }

    public Float getLatFinish() {
        return mLatFinish;
    }

    public void setLatFinish(Float latFinish) {
        mLatFinish = latFinish;
    }

    public Float getLngFinish() {
        return mLngFinish;
    }

    public void setLngFinish(Float lngFinish) {
        mLngFinish = lngFinish;
    }

    public String getDeliveryTime() {
        return mDeliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        mDeliveryTime = deliveryTime;
    }

    public Float getDistance() {
        return mDistance;
    }

    public void setDistance(Float distance) {
        mDistance = distance;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Float getPrice() {
        return mPrice;
    }

    public void setPrice(Float price) {
        mPrice = price;
    }

    public Float getShippingPrice() {
        return mShippingPrice;
    }

    public void setShippingPrice(Float shippingPrice) {
        mShippingPrice = shippingPrice;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public Float getWeight() {
        return mWeight;
    }

    public void setWeight(Float weight) {
        mWeight = weight;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }
}
