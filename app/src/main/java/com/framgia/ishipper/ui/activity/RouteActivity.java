package com.framgia.ishipper.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.util.MapUtils;
import com.framgia.ishipper.util.PermissionUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Order mOrder;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private boolean isCheck;
    private LatLng mLatLng;
    private LatLng mMyLatLng;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        initData();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        mOrder = new Order();
        mLatLng = new LatLng(20.988086, 105.8751013);
        mOrder.setLatLng(20.988086, 105.8751013);
        mOrder.setGoodPrice(1200);
        mOrder.setShipPrice(60);
        mOrder.setAddress("Hang Bai");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.addMarker(new MarkerOptions().position(mLatLng));
        enableMyLocation();

    }

    private void enableMyLocation() {
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

                    mMyLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().title("My location")
                            .position(mMyLatLng));

                    updateZoomMap(mMap);

                    MapUtils.routing(mMyLatLng, mLatLng, new RoutingListener() {
                        @Override
                        public void onRoutingFailure(RouteException e) {

                        }

                        @Override
                        public void onRoutingStart() {

                        }

                        @Override
                        public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
                            PolylineOptions polyOptions = new PolylineOptions();

                            for (int i = 0; i < route.size(); i++) {

                                polyOptions.color(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                                polyOptions.width(8);
                                polyOptions.addAll(route.get(i).getPoints());
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

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        }
    }

    private void updateZoomMap(GoogleMap googleMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(mLatLng);
        builder.include(mMyLatLng);

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

}
