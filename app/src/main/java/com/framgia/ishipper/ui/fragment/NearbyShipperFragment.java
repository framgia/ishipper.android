package com.framgia.ishipper.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Shipper;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.server.ShipperNearbyResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dinhduc on 20/07/2016.
 */
public class NearbyShipperFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "NearbyShipperFragment";
    public static final int TILT_DEGREE = 30;
    public static final int ZOOM_LEVEL = 15;
    private Unbinder mUnbinder;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private ArrayList<Shipper> shippers = new ArrayList<>();
    private SupportMapFragment mMapFragment;

    public NearbyShipperFragment() {

    }

    public static NearbyShipperFragment newInstance() {
        NearbyShipperFragment fragment = new NearbyShipperFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_shipper, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_nearby_shipper);
        mMapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        markShipperNearby();
        configGoogleMap();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(), "Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        mUnbinder.unbind();
        super.onDestroy();
    }

    private void configGoogleMap() {
        LatLng currentPos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .tilt(TILT_DEGREE)
                .zoom(ZOOM_LEVEL)
                .target(currentPos)
                .build();
        mGoogleMap.setPadding(0, 150, 0, 0);
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void addMarkerToMap() {

        for (Shipper shipper : shippers) {
            LatLng latLng = shipper.getLatLng();
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_shipper))
            );
        }
    }

    private void markShipperNearby() {
        User user = new User();
        user.setAuthenticationToken("FQTeVhjFpWyGQiZ4W5Bw");
        user.setLatitude((float) mLocation.getLatitude());
        user.setLongitude((float) mLocation.getLongitude());
        int distance = 2;
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetShipperNearby.USER_LAT_PARAM, String.valueOf(user.getLatitude()));
        userParams.put(APIDefinition.GetShipperNearby.USER_LNG_PARAM, String.valueOf(user.getLongitude()));
        userParams.put(APIDefinition.GetShipperNearby.USER_DISTANCE_PARAM, String.valueOf(distance));
        API.getShipperNearby(user.getAuthenticationToken(), userParams,
                             new API.APICallback<APIResponse<ShipperNearbyResponse>>() {
                                 @Override
                                 public void onResponse(APIResponse<ShipperNearbyResponse> response) {
                                     Log.d(TAG, "onResponse: " + response.getCode());
                                     addListMarker(response.getData().getUsers());
                                 }

                                 @Override
                                 public void onFailure(int code, String message) {
                                     Log.d(TAG, "onFailure: " + message);
                                     Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                 }
                             });
    }

    private void addListMarker(List<User> users) {
        for (User user : users) {
            addMarkShipper(user);
        }
    }

    private void addMarkShipper(User user) {
        LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions()
                                     .position(latLng)
                                     .icon(BitmapDescriptorFactory
                                                   .fromResource(R.drawable.ic_marker_shipper)));
    }
}
