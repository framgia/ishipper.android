package com.framgia.ishipper.model;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.slmyldz.random.Randoms;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * Created by dinhduc on 20/07/2016.
 */
public class Shipper extends User {
    private LatLng latLng;
    private String name;
    private String phoneNumber;
    private int successOrder;
    private int totalOrder;
    private String mLicensePlate;
    private String mDistance;
    private String mRating;
    private int mCountRating;

    public static ArrayList<Shipper> getSampleListData(Location location) {
        ArrayList<Shipper> shippers = new ArrayList<>();
        double currentLat = location.getLatitude();
        double currentLong = location.getLongitude();
        Shipper shipper = new Shipper();
        shipper.setLatLng(new LatLng(currentLat + 0.002, currentLong - 0.002));
        shippers.add(shipper);
        Shipper shipper1 = new Shipper();
        shipper1.setLatLng(new LatLng(currentLat - 0.002, currentLong + 0.002));
        shippers.add(shipper1);
        return shippers;
    }

    public static ArrayList<Shipper> getSampleListShipper(Context context) {
        ArrayList<Shipper> shipperList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Shipper shipper = new Shipper();
            shipper.setName(Randoms.name(context));
            shipper.setLicensePlate(String.format(Locale.getDefault(),
                    "%s%c%s - %d",
                    Randoms.Integer(1, 99),
                    (char) (new Random().nextInt(26) + 'A'),
                    new Random().nextInt(9) + 1,
                    Randoms.Integer(10000, 99999)));
            int total = new Random().nextInt(100) + 1;
            int success = new Random().nextInt(total);
            shipper.setTotalOrder(total);
            shipper.setSuccessOrder(success);
            shipper.setDistance(String.format(Locale.getDefault(), "%d Km", Randoms.Integer(1, 9)));
            shipper.setRating(String.format(Locale.getDefault(),
                    "%1$.2f",
                    Randoms.Float(1.0f, 5.0f)));
            shipper.setCountRating(Randoms.Integer(100, 500));
            shipperList.add(shipper);
        }
        return shipperList;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getSuccessOrder() {
        return successOrder;
    }

    public void setSuccessOrder(int successOrder) {
        this.successOrder = successOrder;
    }

    public int getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        this.totalOrder = totalOrder;
    }


    public String getLicensePlate() {
        return mLicensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        mLicensePlate = licensePlate;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        mRating = rating;
    }

    public int getCountRating() {
        return mCountRating;
    }

    public void setCountRating(int countRating) {
        mCountRating = countRating;
    }
}
