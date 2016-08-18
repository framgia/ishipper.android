package com.framgia.ishipper.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.activity.ShopCreateOrderActivity;
import com.framgia.ishipper.util.MapUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class ShopCreateOrderStep1Fragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private final int NONE = 1;
    private final int PICK_START_POINT = 2;
    private final int PICK_END_POINT = 3;

    @BindView(R.id.layoutMapContainer) FrameLayout mFrameMapContainer;
    @BindView(R.id.imgPickPosition) ImageView mImgPickPosition;
    @BindView(R.id.img_done_start) ImageView mImgDoneStart;
    @BindView(R.id.img_done_end) ImageView mImgDoneEnd;
    @BindView(R.id.edt_address_start) EditText mEdtAddressStart;
    @BindView(R.id.edt_address_end) EditText mEdtAddressEnd;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mMap;
    private Marker mMakerStart, mMakerEnd;
    private int mStatus = NONE;
    private LatLng mLatLngStart;
    private LatLng mLatLngFinish;
    private Polyline mPolylineRoute;

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
        moveCurrentLocation();
        return view;
    }

    private void moveCurrentLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @OnFocusChange({R.id.edt_address_end})
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d("hung", "onFocusChange: " + hasFocus);
    }

    @OnClick({R.id.btnPickStart, R.id.btnPickEnd, R.id.btnContinue, R.id.img_done_start,
                     R.id.img_done_end})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPickStart:
                if (mStatus == PICK_START_POINT) {
                    return;
                }
                mImgPickPosition.setImageResource(R.drawable.ic_map_picker_start);
                setPickStartLocation();

                break;
            case R.id.btnPickEnd:
                if (mStatus == PICK_END_POINT) {
                    return;
                }
                mImgPickPosition.setImageResource(R.drawable.ic_map_picker_end);
                setPickEndLocation();
                break;
            case R.id.img_done_start:
                addStartLocation();
                if (mMakerEnd == null) {
                    setPickEndLocation();
                }
                break;
            case R.id.img_done_end:
                addEndLocation();
                setDonePickLocation();
                break;
            case R.id.btnContinue:
                setAttributeInvoiceAtTab1();
                ((ShopCreateOrderActivity) getActivity()).addFragment(
                        new ShopCreateOrderStep2Fragment());
                break;
        }
    }

    private void setAttributeInvoiceAtTab1() {
        ShopCreateOrderActivity.sInvoice.setAddressStart(mEdtAddressStart.getText().toString());
        ShopCreateOrderActivity.sInvoice.setLatStart((float) mLatLngStart.latitude);
        ShopCreateOrderActivity.sInvoice.setLngStart((float) mLatLngStart.longitude);
        ShopCreateOrderActivity.sInvoice.setAddressFinish(mEdtAddressEnd.getText().toString());
        ShopCreateOrderActivity.sInvoice.setLatFinish((float) mLatLngFinish.latitude);
        ShopCreateOrderActivity.sInvoice.setLngFinish((float) mLatLngFinish.longitude);
    }

    private void addEndLocation() {
        mLatLngFinish = mMap.getCameraPosition().target;
        mMakerEnd = mMap.addMarker(
                new MarkerOptions().position(mLatLngFinish)
                        .icon(BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_map_picker_end))
        );
        mEdtAddressEnd.setText(MapUtils.getAddressFromLocation(getContext(), mLatLngFinish));
    }

    private void addStartLocation() {
        mLatLngStart = mMap.getCameraPosition().target;
        mMakerStart = mMap.addMarker(
                new MarkerOptions()
                        .position(mLatLngStart)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_picker_start)));
        mEdtAddressStart.setText(MapUtils.getAddressFromLocation(getContext(), mLatLngStart));
        if (mMakerEnd != null) {
            setDonePickLocation();
        }
    }

    private void setDonePickLocation() {
        mStatus = NONE;
        mImgPickPosition.setImageDrawable(null);
        mImgDoneStart.setVisibility(View.GONE);
        mImgDoneEnd.setVisibility(View.GONE);
        MapUtils.routing(mLatLngStart, mLatLngFinish, new RoutingListener() {
            @Override
            public void onRoutingFailure(RouteException e) {

            }

            @Override
            public void onRoutingStart() {

            }

            @Override
            public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                PolylineOptions polyOptions = new PolylineOptions();
                for (int j = 0; j < arrayList.size(); j++) {

                    polyOptions.color(ContextCompat.getColor(getContext().getApplicationContext()
                            , R.color.colorGreen));
                    polyOptions.width(8);
                    polyOptions.addAll(arrayList.get(j).getPoints());
                }
                if (mPolylineRoute != null) mPolylineRoute.remove();
                mPolylineRoute = mMap.addPolyline(polyOptions);
                MapUtils.updateZoomMap(mMap,
                                       mFrameMapContainer.getWidth(), mFrameMapContainer.getHeight(),
                                       mLatLngStart, mLatLngFinish);
            }

            @Override
            public void onRoutingCancelled() {

            }
        });
    }

    private void setPickStartLocation() {
        mStatus = PICK_START_POINT;
        startPickLocation(mMakerStart);
        mImgPickPosition.setImageResource(R.drawable.ic_map_picker_start);
        mImgDoneStart.setVisibility(View.VISIBLE);
        mImgDoneEnd.setVisibility(View.GONE);
        mEdtAddressStart.requestFocus();
    }

    private void setPickEndLocation() {
        mStatus = PICK_END_POINT;
        startPickLocation(mMakerEnd);
        mImgPickPosition.setImageResource(R.drawable.ic_map_picker_end);
        mImgDoneStart.setVisibility(View.GONE);
        mImgDoneEnd.setVisibility(View.VISIBLE);
        mEdtAddressEnd.requestFocus();
    }

    private void startPickLocation(Marker marker) {
        if (marker != null) {
            marker.remove();
            MapUtils.zoomToPosition(mMap, marker.getPosition());
            marker = null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkPermission()) return;

        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (checkPermission()) return;

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            MapUtils.zoomToPosition(mMap,
                                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        }
        addStartLocation();
        setPickEndLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                                               Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                                                   Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return true;
        }
        return false;
    }
}
