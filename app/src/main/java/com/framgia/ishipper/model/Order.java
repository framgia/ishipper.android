package com.framgia.ishipper.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.slmyldz.random.Randoms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by HungNT on 7/18/16.
 */
public class Order implements Parcelable {
    public static final int ORDER_STATUS_WAIT = 0;
    public static final int ORDER_STATUS_TAKE = 1;
    public static final int ORDER_STATUS_SHIPPING = 2;
    public static final int ORDER_STATUS_DELIVERED = 3;
    public static final int ORDER_STATUS_FINISHED = 4;
    public static final int ORDER_STATUS_CANCELLED = 5;
    public static final int ORDER_STATUS_ALL = 6;

    public static final String[] arrAddress = new String[]{"Ap #805-3135 Diam Avenue",
            "Ap #688-6218 Donec St.", "P.O. Box 680, 6623 Mauris Av.", "561-1837 Massa. St.",
            "499-3973 A, Av.", "507-9499 Enim, Ave"};

    private int mId;
    private double mLat;
    private double mLng;
    private String mAddress;
    private String mStartingAddress;
    private String mEndAddress;
    private String mNote;
    private int mGoodPrice;
    private int mShipPrice;


    private Long mTime;
    private int mOrderPrice;
    private int mStatus;

    public Order() {
    }

    protected Order(Parcel in) {
        mId = in.readInt();
        mLat = in.readDouble();
        mLng = in.readDouble();
        mStartingAddress = in.readString();
        mEndAddress = in.readString();
        mNote = in.readString();
        mTime = in.readLong();
        mStatus = in.readInt();
    }


    public static ArrayList<Order> SampleListOrder() {
        ArrayList<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Order order = new Order();
            order.setId(i);
            order.setStatus(new Random().nextInt(Order.ORDER_STATUS_CANCELLED + 1));
            order.setOrderPrice((new Random().nextInt(399) + 100) * 1000);
            order.setShipPrice((new Random().nextInt(30) + 20) * 1000);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, new Random().nextInt(4) + 1);
            order.setTime(calendar.getTimeInMillis());
            order.setStartingAddress(arrAddress[Randoms.Integer(0, arrAddress.length - 1)]);
            order.setEndAddress(arrAddress[Randoms.Integer(0, arrAddress.length - 1)]);
            orderList.add(order);
        }
        Collections.sort(orderList, new Comparator<Order>() {
            @Override
            public int compare(Order order, Order t1) {
                return (int) (order.getTime() - t1.getTime());
            }
        });

        return orderList;
    }

    public static ArrayList<Order> filterOrder(ArrayList<Order> orderArrayList, int type) {
        if (type == Order.ORDER_STATUS_ALL)
            return orderArrayList;
        ArrayList<Order> filteredOrderList = new ArrayList<>();
        for (Order order : orderArrayList) {
            if (order.getStatus() == type)
                filteredOrderList.add(order);
        }
        return filteredOrderList;
    }


    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        this.mLat = lat;
    }

    public double getLng() {
        return mLng;
    }

    public void setLng(double lng) {
        this.mLng = lng;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getStartingAddress() {
        return mStartingAddress;
    }

    public void setStartingAddress(String startingAddress) {
        this.mStartingAddress = startingAddress;
    }

    public String getEndAddress() {
        return mEndAddress;
    }

    public void setEndAddress(String endAddress) {
        mEndAddress = endAddress;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
    }

    public void setLatLng(double lat, double lng) {
        this.mLat = lat;
        this.mLng = lng;
    }
    public Long getTime() {
        return mTime;
    }

    public void setTime(Long time) {
        mTime = time;
    }

    public int getOrderPrice() {
        return mOrderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        mOrderPrice = orderPrice;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeDouble(mLat);
        parcel.writeDouble(mLng);
        parcel.writeString(mStartingAddress);
        parcel.writeString(mEndAddress);
        parcel.writeString(mNote);
        parcel.writeLong(mTime);
        parcel.writeInt(mOrderPrice);
        parcel.writeInt(mShipPrice);
        parcel.writeInt(mStatus);
    }


    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public int getShipPrice() {
        return mShipPrice;
    }

    public void setShipPrice(int shipPrice) {
        this.mShipPrice = shipPrice;
    }

    public int getGoodPrice() {
        return mGoodPrice;
    }

    public void setGoodPrice(int goodPrice) {
        this.mGoodPrice = goodPrice;
    }

    public String toString() {
        return getAddress() + ", Ship " + getShipPrice() + ", Thu " + getGoodPrice();
    }
}
