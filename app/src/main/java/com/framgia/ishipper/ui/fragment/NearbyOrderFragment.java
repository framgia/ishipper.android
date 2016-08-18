package com.framgia.ishipper.ui.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.model.WindowOrder;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.InvoiceNearbyData;
import com.framgia.ishipper.ui.activity.FilterOrderActivity;
import com.framgia.ishipper.ui.activity.LoginActivity;
import com.framgia.ishipper.ui.activity.OrderDetailActivity;
import com.framgia.ishipper.ui.activity.RouteActivity;
import com.framgia.ishipper.util.MapUtils;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyOrderFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "NearbyOrderFragment";
    public static final int TILT_DEGREE = 30;
    public static final int ZOOM_LEVEL = 15;
    @BindView(R.id.iv_detail_promotion_label) ImageView mIvPromotionLabel;
    @BindView(R.id.tv_item_order_ship_price) TextView mTvNearbyShipPrice;
    @BindView(R.id.tv_item_order_from) TextView mTvNearbyFrom;
    @BindView(R.id.tv_item_order_to) TextView mTvNearbyTo;
    @BindView(R.id.delivery_to_address_box) LinearLayout mDeliveryToAddressBox;
    @BindView(R.id.action_detail_order) LinearLayout mActionDetailOrder;
    @BindView(R.id.tv_item_order_distance) TextView mTvNearbyDistance;
    @BindView(R.id.tv_item_order_ship_time) TextView mTvNearbyShipTime;
    @BindView(R.id.tv_item_order_price) TextView mTvNearbyOrderPrice;
    @BindView(R.id.btn_item_order_show_path) TextView mBtnNearbyShowPath;
    @BindView(R.id.btn_item_order_register_order) TextView mBtnNearbyReceiveOrder;
    @BindView(R.id.action_cancel_accept_order) TextView mActionCancelAcceptOrder;
    @BindView(R.id.action_receive_order) TextView mActionReceiveOrder;
    @BindView(R.id.window_order_detail) RelativeLayout mWindowOrderDetail;
    @BindView(R.id.ll_order_status) LinearLayout mOrderStatus;
    @BindView(R.id.rating_order_window) RatingBar mRatingOrderWindow;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private ArrayList<WindowOrder> orders = new ArrayList<>();
    private SupportMapFragment mMapFragment;
    private Unbinder mUnbinder;
    private Polyline mPolylineRoute;
    private Marker mMakerEndOrder;
    private User mCurrentUser;

    public NearbyOrderFragment() {
        // Required empty public constructor
    }

    public static NearbyOrderFragment newInstance() {
        NearbyOrderFragment fragment = new NearbyOrderFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearby_order, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mBtnNearbyShowPath.setVisibility(View.VISIBLE);
        mBtnNearbyReceiveOrder.setVisibility(View.VISIBLE);
        mRatingOrderWindow.setRating(4);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_nearby_order);
        mMapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mWindowOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OrderDetailActivity.class));
            }
        });
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
        markInvoiceNearby();
    }

    private void markInvoiceNearby() {
        mCurrentUser = Config.getInstance().getUserInfo(getContext());
        mCurrentUser.setLatitude((float) mLocation.getLatitude());
        mCurrentUser.setLongitude((float) mLocation.getLongitude());
        int distance = 5;
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetInvoiceNearby.USER_LAT_PARAM, String.valueOf(mCurrentUser.getLatitude()));
        userParams.put(APIDefinition.GetInvoiceNearby.USER_LNG_PARAM, String.valueOf(mCurrentUser.getLongitude()));
        userParams.put(APIDefinition.GetInvoiceNearby.USER_DISTANCE_PARAM, String.valueOf(distance));
        API.getInvoiceNearby(mCurrentUser.getAuthenticationToken(), userParams,
                new API.APICallback<APIResponse<InvoiceNearbyData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceNearbyData> response) {
                        Log.d(TAG, "onResponse: " + response.getMessage());
                        addListMarker(response.getData().getInvoiceList());
                        Toast.makeText(getContext(),
                                response.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                        configGoogleMap(response.getData().getInvoiceList());
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addListMarker(List<Invoice> invoiceList) {
        for (Invoice invoice : invoiceList) {
            addMarkInvoice(invoice);
        }
    }

    private void addMarkInvoice(Invoice invoice) {
        LatLng latLng = new LatLng(invoice.getLatStart(), invoice.getLngStart());
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_shop)));
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

    private void configGoogleMap(final List<Invoice> invoiceList) {
        LatLng currentPos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .tilt(TILT_DEGREE)
                .zoom(ZOOM_LEVEL)
                .target(currentPos)
                .build();
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.setPadding(0, 150, 0, 0);
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mWindowOrderDetail.setVisibility(View.VISIBLE);
                String id = marker.getId();
                int pos = Integer.parseInt(id.replace("m", ""));
                final Invoice invoice = invoiceList.get(pos);
                mTvNearbyDistance.setText(String.valueOf(invoice.getDistance()) + "km");
                mTvNearbyFrom.setText(invoice.getAddressStart());
                mTvNearbyTo.setText(invoice.getAddressFinish());
                mTvNearbyShipTime.setText(invoice.getDeliveryTime());
                mTvNearbyShipPrice.setText(String.valueOf(invoice.getShippingPrice()));
                mTvNearbyOrderPrice.setText(String.valueOf(invoice.getPrice()));
                if (mPolylineRoute != null) {
                    mPolylineRoute.remove();
                    mMakerEndOrder.remove();
                }

                mMakerEndOrder = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(invoice.getLatFinish(), invoice.getLngFinish())));
                final LatLng startPoint = new LatLng(invoice.getLatStart(), invoice.getLngStart());
                final LatLng endPoint = new LatLng(invoice.getLatFinish(), invoice.getLngFinish());
                MapUtils.routing(startPoint, endPoint,
                        new RoutingListener() {
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

                                    polyOptions.color(ContextCompat.getColor(getContext().getApplicationContext()
                                            , R.color.colorGreen));
                                    polyOptions.width(8);
                                    polyOptions.addAll(route.get(i).getPoints());
                                }

                                mPolylineRoute = mGoogleMap.addPolyline(polyOptions);

                                MapUtils.updateZoomMap(mGoogleMap, startPoint, endPoint);
                            }

                            @Override
                            public void onRoutingCancelled() {

                            }
                        });
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mWindowOrderDetail.setVisibility(View.GONE);
                if (mPolylineRoute != null) {
                    mPolylineRoute.remove();
                    mMakerEndOrder.remove();
                }
            }
        });
    }


    @OnClick({R.id.btn_item_order_show_path, R.id.btn_item_order_register_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_item_order_show_path:
                getActivity().startActivity(new Intent(getActivity(), RouteActivity.class));
                break;
            case R.id.btn_item_order_register_order:
                showReceiveDialog();
                break;
        }
    }

    private void showReceiveDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(R.layout.dialog_nearby_receive_order).create();
        dialog.show();
    }

    @OnClick(R.id.window_order_detail)
    public void onClick() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_nearby_order, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_filter) {
            startActivity(new Intent(getContext(), FilterOrderActivity.class));
        }
        return true;
    }
}
