package com.framgia.ishipper.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.data.ListRouteData;
import com.framgia.ishipper.ui.adapter.PathGuideAdapter;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.MapUtils;
import com.framgia.ishipper.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteActivity extends BaseToolbarActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "RouteActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Invoice mInvoice;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private boolean isCheck;
    private LatLng mStartLatLng;
    private LatLng mFinishLatLng;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<ListRouteData.Step> mSteps;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    @BindView(R.id.rv_detail_guide_path) RecyclerView mRvGuidePath;
    @BindView(R.id.img_start_address) ImageView mImgStartAddress;
    @BindView(R.id.img_finish_address) ImageView mImgFinishAddress;
    @BindView(R.id.shipping_from) TextView mTvStartAddress;
    @BindView(R.id.orderEndAddress) TextView mTvFinishAddress;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.rc_guide_path_empty) TextView mTvEmpty;
    @BindView(R.id.layout_bottom_sheet) View mBottomSheetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mInvoice = getIntent().getParcelableExtra(Const.KeyIntent.KEY_INVOICE);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        getListSteps(mInvoice.getAddressStart(), mInvoice.getAddressFinish());
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (PermissionUtils.checkLocationPermission(this)) {
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        switch (mInvoice.getStatusCode()) {
            case Invoice.STATUS_CODE_INIT:
                setUpUI(
                        new LatLng(mInvoice.getLatStart(), mInvoice.getLngStart()),
                        new LatLng(mInvoice.getLatFinish(), mInvoice.getLngFinish()),
                        mInvoice.getAddressStart(),
                        mInvoice.getAddressFinish(),
                        R.drawable.ic_shop,
                        R.drawable.ic_destination
                );
                break;
            case Invoice.STATUS_CODE_WAITING:
                setUpUI(
                        new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                        new LatLng(mInvoice.getLatStart(), mInvoice.getLngStart()),
                        getString(R.string.all_current_position),
                        mInvoice.getAddressStart(),
                        R.drawable.ic_current_position,
                        R.drawable.ic_shop
                );
                break;
            default:
                setUpUI(
                        new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                        new LatLng(mInvoice.getLatFinish(), mInvoice.getLngFinish()),
                        getString(R.string.all_current_position),
                        mInvoice.getAddressFinish(),
                        R.drawable.ic_current_position,
                        R.drawable.ic_destination
                );
                break;
        }
        showPath();

    }

    private void setUpUI(
            LatLng startLatLng,
            LatLng finishLatLng,
            String startAddress,
            String finishAddress,
            int startIcon,
            int finishIcon) {
        mTvStartAddress.setText(startAddress);
        mTvFinishAddress.setText(finishAddress);
        mStartLatLng = startLatLng;
        mFinishLatLng = finishLatLng;
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(startIcon))
                .position(mStartLatLng));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(finishIcon))
                .position(mFinishLatLng));
    }

    /**
     * Show path of 2 position
     */
    private void showPath() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (isCheck) return;
                    isCheck = true;
                    updateZoomMap(mMap);
                    MapUtils.routing(mStartLatLng, mFinishLatLng, new RoutingListener() {
                        @Override
                        public void onRoutingFailure(RouteException e) {

                        }

                        @Override
                        public void onRoutingStart() {

                        }

                        @Override
                        public void onRoutingSuccess(ArrayList<Route> routes, int shortestRouteIndex) {
                            PolylineOptions polyOptions = new PolylineOptions();
                            for (int i = 0; i < routes.size(); i++) {
                                Route route = routes.get(i);
                                polyOptions.color(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                                polyOptions.width(8);
                                polyOptions.addAll(route.getPoints());
                                Log.d(TAG, route.getPoints().size() + "");
                            }

                            mMap.addPolyline(polyOptions);
                        }

                        @Override
                        public void onRoutingCancelled() {

                        }
                    });

                    mMap.setMyLocationEnabled(false);
                }
            });
        }
    }

    /**
     * Get list steps from start to the end address
     */
    private void getListSteps(String startAddress, String finishAddress) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetListRoutes.PARAM_ORIGIN, startAddress);
        userParams.put(APIDefinition.GetListRoutes.PARAM_DESTINATION, finishAddress);
        userParams.put(APIDefinition.GetListRoutes.PARAM_KEY, getString(R.string.google_maps_key));
        userParams.put(APIDefinition.PARAM_LANGUAGE, Const.Language.VIETNAMESE);
        API.getListRoutes(userParams, new Callback<ListRouteData>() {
            @Override
            public void onResponse(Call<ListRouteData> call, Response<ListRouteData> response) {
                mSteps = response.body().getRoutes().get(0).getLegs().get(0).getSteps();
                if (mSteps != null) {
                    PathGuideAdapter pathGuideAdapter = new PathGuideAdapter(RouteActivity.this, mSteps);
                    mRvGuidePath.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    mRvGuidePath.setAdapter(pathGuideAdapter);
                    mRvGuidePath.setVisibility(View.VISIBLE);
                } else {
                    mTvEmpty.setText(R.string.not_have_guide_for_route);
                    mTvEmpty.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ListRouteData> call, Throwable t) {
                mTvEmpty.setVisibility(View.VISIBLE);
                mTvEmpty.setText(R.string.cant_get_guide_for_route);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(
                permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showPath();
        }
    }

    private void updateZoomMap(GoogleMap googleMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mStartLatLng);
        builder.include(mFinishLatLng);
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.moveCamera(cu);
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.title_activity_route;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_route;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
