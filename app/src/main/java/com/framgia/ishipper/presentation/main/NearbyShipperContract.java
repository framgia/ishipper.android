package com.framgia.ishipper.presentation.main;

import android.content.Intent;

import com.framgia.ishipper.model.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HungNT on 11/28/16.
 */

public class NearbyShipperContract {
    interface View {

        void onSearchAreaComplete(CharSequence name, LatLng latLng);

        void onGetShipperNearbyComplete(List<User> users);

        void onAddressChange(String string);

        Marker addMark(LatLng latLng);
    }

    interface Presenter {
        void getPlace(Intent place);

        void getShipperInfo(User shipper);

        void startSearchPlace();

        void requestShipperNearby(LatLng latLng);

        void getAddressFromLatLng(LatLng latLng);

        void addShipper(User user, ArrayList<User> shipperList, HashMap<Integer, Marker> userMap);

        void removeShipper(User user, ArrayList<User> shipperList, HashMap<Integer, Marker> userMap);

        void updateCurrentLocation(User currentUser);
    }
}
