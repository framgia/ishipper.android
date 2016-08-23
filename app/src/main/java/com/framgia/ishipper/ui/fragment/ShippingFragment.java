package com.framgia.ishipper.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.ui.activity.OrderDetailActivity;
import com.framgia.ishipper.ui.adapter.OrderShippingAdapter;
import com.framgia.ishipper.util.MapUtils;
import com.framgia.ishipper.util.PermissionUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShippingFragment extends Fragment implements OnMapReadyCallback, RoutingListener, OrderListFragment.OnListFragmentInteractionListener, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int POLY_LINE_WIDTH = 8;
    private static final int[] COLORS = new int[]{
            Color.RED, Color.BLUE,
            Color.BLACK, Color.GREEN,
            Color.CYAN};

    @BindView(R.id.layoutMap) View mLayoutMap;
    @BindView(R.id.rvOrders) RecyclerView mRvOrders;
    @BindView(R.id.tvOrderDesc) TextView mTvOrderDesc;
    @BindView(R.id.layoutOrderDetail) View mLayoutOrderDetail;
    @BindView(R.id.imgExpandMap) ImageView mImgExpandMap;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private List<Invoice> mInvoiceList = new ArrayList<>();

    private boolean isCheck = false;
    private int mPosition;
    private boolean mPermissionDenied = false;
    private boolean isExpand;
    private OrderShippingAdapter mAdapter;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shipping, container, false);
        ButterKnife.bind(this, v);

        initData();
        settingRecycleView();
        return v;
    }

    private void initData() {
        Invoice invoice = new Invoice();
        invoice.setLatStart(21.014332f);
        invoice.setLngStart(105.857217f);
        invoice.setLatFinish(21.014543f);
        invoice.setLngFinish(105.857445f);
        invoice.setPrice(500f);
        invoice.setShippingPrice(40f);
        invoice.setAddressStart("214 Linh Nam");
        mInvoiceList.add(invoice);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        for (Invoice item : mInvoiceList) {
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_shop))
                    .position(new LatLng(item.getLatStart(), item.getLngStart()))
            );
        }

        enableMyLocation();
        googleMap.setOnMarkerClickListener(this);
        updateZoomMap(googleMap);
    }

    private void updateZoomMap(GoogleMap googleMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Invoice item : mInvoiceList) {
            LatLng latLng = new LatLng(item.getLatFinish(), item.getLngStart());
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }


    @Override
    public void onRoutingFailure(RouteException e) {
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingCancelled() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        PolylineOptions polyOptions = new PolylineOptions();

        for (int i = 0; i < route.size(); i++) {
            int colorIndex = mPosition++ % COLORS.length;

            polyOptions.color(COLORS[colorIndex]);
            polyOptions.width(POLY_LINE_WIDTH);
            polyOptions.addAll(route.get(i).getPoints());
        }

        mMap.addPolyline(polyOptions);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (isCheck) return;
                    isCheck = true;

                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().title("My location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_position))
                            .position(myLocation));

                    for (Invoice item : mInvoiceList) {
                        MapUtils.routing(myLocation, new LatLng(item.getLatFinish(), item.getLngFinish()),
                                         ShippingFragment.this);
                    }

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
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
        } else {
            mPermissionDenied = true;
        }
    }

    @OnClick(R.id.btn_expand_map)
    public void onClick() {
        int height;
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_180);
        anim.setFillAfter(true);
        if (!isExpand) {
            height = 700;
            isExpand = true;
            mLayoutOrderDetail.setVisibility(View.GONE);
            mImgExpandMap.startAnimation(anim);
            mRvOrders.setVisibility(View.VISIBLE);
        } else {
            height = 200;
            isExpand = false;
            anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_180_reverse);
            mLayoutOrderDetail.setVisibility(View.VISIBLE);
            mImgExpandMap.startAnimation(anim);
            mRvOrders.setVisibility(View.GONE);
        }

        mRvOrders.getLayoutParams().height = height;
        mRvOrders.requestLayout();
    }

    private void settingRecycleView() {
        mAdapter = new OrderShippingAdapter(mInvoiceList, this);
        mRvOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRvOrders.setAdapter(mAdapter);
    }

    @Override
    public void onListFragmentInteraction(Invoice invoice) {
        startActivity(new Intent(getActivity(), OrderDetailActivity.class));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int makerId = Integer.valueOf(marker.getId().substring(1));
        if (makerId < mInvoiceList.size()) {
            Invoice invoice = mInvoiceList.get(makerId);
            mTvOrderDesc.setText(invoice.toString());
        }

        return false;
    }
}
