package com.framgia.ishipper.ui.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.model.WindowOrder;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.GetUserData;
import com.framgia.ishipper.net.data.ListInvoiceData;
import com.framgia.ishipper.ui.activity.FilterOrderActivity;
import com.framgia.ishipper.ui.activity.OrderDetailActivity;
import com.framgia.ishipper.ui.activity.RouteActivity;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.MapUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
    private static final int TILT_DEGREE = 30;
    private static final int ZOOM_LEVEL = 15;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
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
    @BindView(R.id.tv_main_search_area) TextView mTvSearchArea;
    @BindView(R.id.rating_order_window) AppCompatRatingBar mRatingOrderWindow;
    @BindView(R.id.tv_shipping_order_status) TextView mTvShippingOrderStatus;
    @BindView(R.id.tv_item_order_shop_name) TextView mTvItemOrderShopName;
    @BindView(R.id.ll_shop_order_status) LinearLayout mLlShopOrderStatus;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private ArrayList<WindowOrder> orders = new ArrayList<>();
    private SupportMapFragment mMapFragment;
    private Unbinder mUnbinder;
    private Polyline mPolylineRoute;
    private Marker mMakerEndOrder;
    private Context mContext;
    private User mCurrentUser;

    public NearbyOrderFragment() {
        // Required empty public constructor
    }

    public static NearbyOrderFragment newInstance() {
        return new NearbyOrderFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearby_order, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mBtnNearbyShowPath.setVisibility(View.VISIBLE);
        mBtnNearbyReceiveOrder.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
        mWindowOrderDetail.setVisibility(View.GONE);
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
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
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
        mCurrentUser = Config.getInstance().getUserInfo(getContext());
        mCurrentUser.setLatitude(mLocation.getLatitude());
        mCurrentUser.setLongitude(mLocation.getLongitude());
        markInvoiceNearby(mCurrentUser.getLatitude(), mCurrentUser.getLongitude(), 5f);
    }

    private void markInvoiceNearby(final double latitude, final double longitude, float distance) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_LAT, String.valueOf(latitude));
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_LNG, String.valueOf(longitude));
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_DISTANCE, String.valueOf(distance));
        API.getInvoiceNearby(mCurrentUser.getAuthenticationToken(), userParams,
                new API.APICallback<APIResponse<ListInvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<ListInvoiceData> response) {
                        Log.d(TAG, "onResponse: " + response.getMessage());
                        addListMarker(response.getData().getInvoiceList());
                        Toast.makeText(getContext(),
                                response.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                        updateCamera(latitude, longitude);
                        configGoogleMap(response.getData().getInvoiceList());
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addListMarker(List<Invoice> invoiceList) {
        mGoogleMap.clear();
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
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.setPadding(0, 150, 0, 0);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mWindowOrderDetail.setVisibility(View.VISIBLE);
                String id = marker.getId();
                int pos = Integer.parseInt(id.replace("m", ""));
                final Invoice invoice = invoiceList.get(pos);

                /** get shop information */
                API.getUser(
                        mCurrentUser.getAuthenticationToken(),
                        String.valueOf(invoice.getUserId()),
                        new API.APICallback<APIResponse<GetUserData>>() {
                            @Override
                            public void onResponse(APIResponse<GetUserData> response) {
                                User user = response.getData().getUser();
                                mTvItemOrderShopName.setText(user.getName());
                                mRatingOrderWindow.setRating((float) user.getRate());

                                mTvNearbyDistance.setText(String.valueOf(invoice.getDistance()) + "km");
                                mTvNearbyFrom.setText(invoice.getAddressStart());
                                mTvNearbyTo.setText(invoice.getAddressFinish());
                                mTvNearbyShipTime.setText(invoice.getDeliveryTime());
                                mTvNearbyShipPrice.setText(String.valueOf(invoice.getShippingPrice()));
                                mTvNearbyOrderPrice.setText(String.valueOf(invoice.getPrice()));

                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });

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

                                MapUtils.updateZoomMap(mGoogleMap, Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT,
                                        startPoint, endPoint);
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

    private void updateCamera(double latitude, double longitude) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .tilt(TILT_DEGREE)
                .zoom(ZOOM_LEVEL)
                .target(new LatLng(latitude, longitude))
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @OnClick({R.id.btn_item_order_show_path, R.id.btn_item_order_register_order, R.id.rl_search_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_item_order_show_path:
                getActivity().startActivity(new Intent(getActivity(), RouteActivity.class));
                break;
            case R.id.btn_item_order_register_order:
                showReceiveDialog();
                break;
            case R.id.rl_search_view:
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .build();

                    Intent searchIntent = new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(getActivity());
                    startActivityForResult(searchIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
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
        startActivity(new Intent(getContext(), OrderDetailActivity.class));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                Log.d(TAG, "onActivityResult: " + place.getName());
                mTvSearchArea.setText(place.getName());
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                markInvoiceNearby(latitude, longitude, 5);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mContext, data);
                Toast.makeText(mContext, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, " Search Cancel");
            }
        }
    }
}
