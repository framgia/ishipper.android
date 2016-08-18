package com.framgia.ishipper.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.activity.ShopCreateOrderActivity;
import com.framgia.ishipper.util.MapUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class ShopCreateOrderStep1Fragment extends Fragment implements OnMapReadyCallback {
    private final int NONE = 1;
    private final int PICK_START_POINT = 2;
    private final int PICK_END_POINT = 3;

    @BindView(R.id.imgPickPosition) ImageView mImgPickPosition;
    @BindView(R.id.edt_address_start) EditText mEdtAddressStart;
    @BindView(R.id.edt_address_end) EditText mEdtAddressEnd;
    private GoogleMap mMap;
    private Marker mMakerStart, mMakerEnd;
    private int status = NONE;
    private LatLng mLatLngStart;
    private LatLng mLatLngFinish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_shop_create_order_step1, container, false);
        ButterKnife.bind(this, view);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.layoutMapContainer, mapFragment).commit();

        return view;
    }

    @OnFocusChange({R.id.edt_address_end})
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d("hung", "onFocusChange: " + hasFocus);
    }

    @OnClick({R.id.btnPickStart, R.id.btnPickEnd, R.id.btnContinue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPickStart:

                if (status == PICK_START_POINT || status == NONE) {
                    return;
                }

                // Change image marker color
                mImgPickPosition.setImageResource(R.drawable.ic_map_picker_start);

                if (mMakerEnd != null) {
                    mMakerEnd.remove();
                }

                mLatLngStart = mMap.getCameraPosition().target;
                mEdtAddressEnd.setText(MapUtils.getAddressFromLocation(getContext(), mLatLngStart));

                mMakerEnd = mMap.addMarker(
                        new MarkerOptions().position(mLatLngStart)
                                .icon(BitmapDescriptorFactory.fromResource(
                                        R.drawable.ic_map_picker_end))
                );

                // Zoom to previous location, if don't have previous -> go to current location
                startPickLocation(mMakerStart);

                status = PICK_START_POINT;
                break;
            case R.id.btnPickEnd:

                if (status == PICK_END_POINT) {
                    return;
                }

                mImgPickPosition.setImageResource(R.drawable.ic_map_picker_end);

                if (mMakerStart != null) {
                    mMakerStart.remove();
                }
                mLatLngFinish = mMap.getCameraPosition().target;
                mMakerStart = mMap.addMarker(
                        new MarkerOptions().position(mLatLngFinish)
                                .icon(BitmapDescriptorFactory.fromResource(
                                        R.drawable.ic_map_picker_start))
                );

                startPickLocation(mMakerEnd);

                status = PICK_END_POINT;
                break;
            case R.id.btnContinue:
                //TODO
                ShopCreateOrderActivity.sInvoice.setAddressStart(mEdtAddressStart.getText().toString());
                ShopCreateOrderActivity.sInvoice.setLatStart((float) mLatLngStart.latitude);
                ShopCreateOrderActivity.sInvoice.setLngStart((float) mLatLngStart.longitude);
                ShopCreateOrderActivity.sInvoice.setAddressFinish(mEdtAddressEnd.getText().toString());
                ShopCreateOrderActivity.sInvoice.setLatFinish((float) mLatLngFinish.latitude);
                ShopCreateOrderActivity.sInvoice.setLngFinish((float) mLatLngFinish.longitude);
                ((ShopCreateOrderActivity) getActivity()).addFragment(
                        new ShopCreateOrderStep2Fragment());
                break;
        }
    }

    private void saveLocation(Marker marker) {
    }

    private void startPickLocation(Marker marker) {
        if (marker != null) {
            marker.remove();
            MapUtils.zoomToPosition(mMap, marker.getPosition());
            marker = null;
        }
    }

    private void donePickLocation() {
        mImgPickPosition.setVisibility(View.GONE);
    }

    private void enableMyLocation() {
        // Permission to access the location is missing.

        //        mMap.setMyLocationEnabled(true);
        //        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
        //            @Override
        //            public void onMyLocationChange(Location location) {
        //                MapUtils.updateZoomMap(mMap, new LatLng(location.getLatitude(), location.getLongitude()));
        //                mMap.setMyLocationEnabled(false);
        //            }
        //        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        if (ActivityCompat.checkSelfPermission(getContext(),
                                               Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                                                   Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }
}
