package com.framgia.ishipper.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by dinhduc on 19/07/2016.
 */
public class WindowOrder {
    private String shopName;
    private double distance;
    private double lat;
    private double lng;
    private LatLng endPoint;
    private String shipTime;
    private String goodPrice;
    private String shipPrice;
    private String startAddress;
    private String endAddress;

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getShipTime() {
        return shipTime;
    }

    public void setShipTime(String shipTime) {
        this.shipTime = shipTime;
    }

    public String getGoodPrice() {
        return goodPrice;
    }

    public void setGoodPrice(String goodPrice) {
        this.goodPrice = goodPrice;
    }

    public String getShipPrice() {
        return shipPrice;
    }

    public void setShipPrice(String shipPrice) {

        this.shipPrice = shipPrice;
    }

    public static ArrayList<WindowOrder> getSampleListData(Location location) {
        ArrayList<WindowOrder> orders = new ArrayList<>();
        double currentLat = location.getLatitude();
        double currentLong = location.getLongitude();
        WindowOrder order = new WindowOrder();
        order.setLat(currentLat + 0.002);
        order.setLng(currentLong + 0.002);
        order.setShopName("Rhodi Shop");
        order.setDistance(12);
        order.setEndPoint(new LatLng(21.021048, 105.844798));
        order.setStartAddress("Hoan Kiem");
        order.setEndAddress("Ha Dong");
        order.setShipTime("4 gio");
        order.setGoodPrice("500k");
        order.setShipPrice("25k");
        orders.add(order);

        WindowOrder order1 = new WindowOrder();
        order1.setLat(currentLat - 0.002);
        order1.setLng(currentLong - 0.002);
        order1.setShopName("3s Shop");
        order1.setDistance(12);
        order1.setStartAddress("Ha Noi");
        order1.setEndPoint(new LatLng(21.028979, 105.855399));
        order1.setEndAddress("Ho Chi Minh");
        order1.setShipTime("2 gio");
        order1.setGoodPrice("300k");
        order1.setShipPrice("30k");
        orders.add(order1);
        return orders;
    }

    public LatLng getEndPoint() {
        return endPoint;
    }

    public LatLng getStartPoint() {
        return new LatLng(lat, lng);
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }
}
