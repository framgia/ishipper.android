package com.framgia.ishipper.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Invoice;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouteActivity extends ToolbarActivity implements
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

    @BindView(R.id.img_start_address) ImageView mImgStartAddress;
    @BindView(R.id.img_finish_address) ImageView mImgFinishAddress;
    @BindView(R.id.shipping_from) TextView mTvStartAddress;
    @BindView(R.id.orderEndAddress) TextView mTvFinishAddress;
    @BindView(R.id.toolbar) Toolbar mToolbar;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.title_activity_route;
    }

    @Override
    int getLayoutId() {
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
}
