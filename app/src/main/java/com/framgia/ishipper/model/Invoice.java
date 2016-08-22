package com.framgia.ishipper.model;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vuduychuong1994 on 8/9/16.
 */
public class Invoice implements Parcelable {
    public static final String STATUS_INIT = "init";
    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_SHIPPING = "shipping";
    public static final String STATUS_SHIPPED = "shipped";
    public static final String STATUS_FINISHED = "finished";
    public static final String STATUS_CANCEL = "cancel";
    public static final String STATUS_ALL = "all";
    public static final int STATUS_CODE_INIT = 0;
    public static final int STATUS_CODE_WAITING = 1;
    public static final int STATUS_CODE_SHIPPING = 2;
    public static final int STATUS_CODE_SHIPPED = 3;
    public static final int STATUS_CODE_FINISHED = 4;
    public static final int STATUS_CODE_CANCEL = 5;
    public static final int STATUS_CODE_ALL = 6;

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
    @SerializedName("customer_name") private String mCustomerName;
    @SerializedName("customer_number") private String mCustomerNumber;


    public Invoice() {

    }

    public int getStatusCode() {
        if (mStatus.equals(Invoice.STATUS_INIT)) return STATUS_CODE_INIT;
        if (mStatus.equals(Invoice.STATUS_WAITING)) return STATUS_CODE_WAITING;
        if (mStatus.equals(Invoice.STATUS_SHIPPING)) return STATUS_CODE_SHIPPING;
        if (mStatus.equals(Invoice.STATUS_SHIPPED)) return STATUS_CODE_SHIPPED;
        if (mStatus.equals(Invoice.STATUS_FINISHED)) return STATUS_CODE_FINISHED;
        if (mStatus.equals(Invoice.STATUS_CANCEL)) return STATUS_CODE_CANCEL;
        if (mStatus.equals(Invoice.STATUS_ALL)) return STATUS_CODE_ALL;
        return -1;
    }

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

    public String getCustomerName() {
        return mCustomerName;
    }

    public void setCustomerName(String customerName) {
        mCustomerName = customerName;
    }

    public String getCustomerNumber() {
        return mCustomerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        mCustomerNumber = customerNumber;
    }

    public String getInvoiceDesc() {
        return String.format("Ship +%s, Thu %s\n%s", mShippingPrice, mPrice, mDescription);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeString(mAddressStart);
        parcel.writeFloat(mLatStart);
        parcel.writeFloat(mLngStart);
        parcel.writeString(mAddressFinish);
        parcel.writeFloat(mLatFinish);
        parcel.writeFloat(mLngFinish);
        parcel.writeString(mDeliveryTime);
        parcel.writeFloat(mDistance);
        parcel.writeString(mDescription);
        parcel.writeFloat(mPrice);
        parcel.writeFloat(mShippingPrice);
        parcel.writeString(mStatus);
        parcel.writeFloat(mWeight);
        parcel.writeInt(mUserId);
        parcel.writeString(mCustomerName);
        parcel.writeString(mCustomerNumber);
    }

    protected Invoice(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mAddressStart = in.readString();
        mAddressFinish = in.readString();
        mDeliveryTime = in.readString();
        mDescription = in.readString();
        mStatus = in.readString();
        mUserId = in.readInt();
        mCustomerName = in.readString();
        mCustomerNumber = in.readString();
    }

    public static final Creator<Invoice> CREATOR = new Creator<Invoice>() {
        @Override
        public Invoice createFromParcel(Parcel in) {
            return new Invoice(in);
        }

        @Override
        public Invoice[] newArray(int size) {
            return new Invoice[size];
        }
    };
}
