package com.framgia.ishipper.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.Config;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by HungNT on 7/18/16.
 */
public class MapUtils {

    public static void routing(LatLng start, LatLng end, RoutingListener listener) {
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(listener)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    public static void updateZoomMap(GoogleMap googleMap, LatLng... points) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng item : points) {
            builder.include(item);
        }
        LatLngBounds bounds = builder.build();

        int width = Config.SCREEN_WIDTH;
        int height = Config.SCREEN_HEIGHT;
        int padding = (int) (width * 0.12);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }

    public static void zoomToPosition(GoogleMap map, LatLng latLng) {
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        map.moveCamera(center);
        map.animateCamera(center);
    }

    public static String getAddressName(Context context, LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
