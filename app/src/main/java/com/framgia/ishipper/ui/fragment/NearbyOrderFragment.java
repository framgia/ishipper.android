package com.framgia.ishipper.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.ListInvoiceData;
import com.framgia.ishipper.ui.LocationSettingCallback;
import com.framgia.ishipper.ui.activity.FilterOrderActivity;
import com.framgia.ishipper.ui.activity.OrderDetailActivity;
import com.framgia.ishipper.ui.activity.RouteActivity;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.MapUtils;
import com.framgia.ishipper.util.PermissionUtils;
import com.framgia.ishipper.util.StorageUtils;
import com.framgia.ishipper.util.TextFormatUtils;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private static final float RADIUS = 5;
    private static final int REQUEST_FILTER = 0x1234;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    @BindView(R.id.img_nearby_order_pos_marker) ImageView mImgPosMarker;
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
    @BindView(R.id.window_order_detail) RelativeLayout mRlOrderDetail;
    @BindView(R.id.ll_order_status) LinearLayout mOrderStatus;
    @BindView(R.id.tv_main_search_area) TextView mTvSearchArea;
    @BindView(R.id.rating_order_window) AppCompatRatingBar mRatingOrderWindow;
    @BindView(R.id.tv_shipping_order_status) TextView mTvShippingOrderStatus;
    @BindView(R.id.tv_item_order_shop_name) TextView mTvItemOrderShopName;
    @BindView(R.id.ll_shop_order_status) LinearLayout mLlShopOrderStatus;

    @OnClick({R.id.btn_item_order_show_path, R.id.btn_item_order_register_order,
        R.id.rl_search_view, R.id.window_order_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_item_order_show_path:
                getActivity().startActivity(new Intent(getActivity(), RouteActivity.class));
                break;
            case R.id.btn_item_order_register_order:
                String invoiceId = (String) view.getTag();
                showReceiveDialog(invoiceId);
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
            case R.id.window_order_detail:
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putInt(OrderDetailActivity.KEY_INVOICE_ID, mInvoice.getId());
                intent.putExtras(extras);
                startActivity(intent);
                break;
        }
    }

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private Unbinder mUnbinder;
    private Polyline mPolylineRoute;
    private Marker mMakerEndOrder;
    private Context mContext;
    private User mCurrentUser;
    private int mHeightMap;
    private int mWidthMap;
    private Invoice mInvoice;
    private FetchAddressTask mTask;
    private ArrayList<Invoice> mInvoices = new ArrayList<>();
    private boolean mAutoRefresh = true;
    private HashMap<String, Integer> mHashMap = new HashMap<>();

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
        mCurrentUser = Config.getInstance().getUserInfo(mContext);
        mRlOrderDetail.setVisibility(View.GONE);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment =
            (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_nearby_order);
        mMapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        if (PermissionUtils.checkLocationPermission(mContext)) return;
        mGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        if (PermissionUtils.checkLocationPermission(mContext)) return;
        CommonUtils.checkLocationRequestSetting(
            getActivity(),
            mGoogleApiClient,
            new LocationSettingCallback() {
                @Override
                public void onSuccess() {
                    initMap();
                }
            });
    }

    private void initMap() {
        if (PermissionUtils.checkLocationPermission(mContext)) return;
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            mCurrentUser.setLatitude(mLocation.getLatitude());
            mCurrentUser.setLongitude(mLocation.getLongitude());
            MapUtils.zoomToPosition(mGoogleMap,
                new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            markInvoiceNearby(mCurrentUser.getLatitude(), mCurrentUser.getLongitude(),
                StorageUtils.getIntValue(getContext(), Const.Storage.KEY_SETTING_INVOICE_RADIUS,
                    Const.SETTING_INVOICE_RADIUS_DEFAULT));
            configGoogleMap();
        } else {
            Toast.makeText(mContext, R.string.all_cant_get_location, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * mark all invoices near a location on map
     *
     * @param latitude  location's latitude
     * @param longitude location's longitude
     * @param radius    radius where to find invoice
     */
    private void markInvoiceNearby(final double latitude, final double longitude, float radius) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_LAT, String.valueOf(latitude));
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_LNG, String.valueOf(longitude));
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_DISTANCE, String.valueOf(radius));
        API.getInvoiceNearby(mCurrentUser.getAuthenticationToken(), userParams,
            new API.APICallback<APIResponse<ListInvoiceData>>() {
                @Override
                public void onResponse(APIResponse<ListInvoiceData> response) {
                    Log.d(TAG, "onResponse: " + response.getMessage());
                    Toast.makeText(mContext,
                        response.getMessage(),
                        Toast.LENGTH_SHORT)
                        .show();
                    mInvoices = (ArrayList<Invoice>) response.getData().getInvoiceList();
                    addListMarker(mInvoices);
                }

                @Override
                public void onFailure(int code, String message) {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     * add list invoice marker to Map
     *
     * @param invoiceList list invoice
     */
    private void addListMarker(List<Invoice> invoiceList) {
        mGoogleMap.clear();
        for (Invoice invoice : invoiceList) {
            addMarkInvoice(invoice);
        }
    }

    private void addMarkInvoice(Invoice invoice) {
        LatLng latLng = new LatLng(invoice.getLatStart(), invoice.getLngStart());
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_shop)));
        /** save position of invoice with marker id to hashmap*/
        mHashMap.put(marker.getId(), mInvoices.indexOf(invoice));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(mContext, "Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        mUnbinder.unbind();
        super.onDestroy();
    }

    private void configSizeMap() {
        mWidthMap = mMapFragment.getView().getWidth();
        if (mRlOrderDetail.getVisibility() == View.VISIBLE) {
            mHeightMap = mMapFragment.getView().getHeight() + 2 * mRlOrderDetail.getHeight();
        } else {
            mHeightMap = mMapFragment.getView().getHeight();
        }
    }

    /**
     * turn on/of auto fetch address
     *
     * @param status true/false
     */
    private void switchAutoRefresh(boolean status) {
        mAutoRefresh = status;
        mImgPosMarker.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    /**
     * Config google with setting like camera change listener, marker clicked listener...
     */
    private void configGoogleMap() {
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.setPadding(
            Const.MapPadding.LEFT_PADDING,
            Const.MapPadding.TOP_PADDING,
            Const.MapPadding.RIGHT_PADDING,
            Const.MapPadding.BOTTOM_PADDING);
        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (mAutoRefresh) {
                    if (mTask != null) {
                        mTask.cancel(true);
                    }
                    mTask = new FetchAddressTask();
                    mTask.execute(new LatLng(cameraPosition.target.latitude,
                        cameraPosition.target.longitude));
                }
            }
        });
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                switchAutoRefresh(false);
                mRlOrderDetail.setVisibility(View.VISIBLE);
                String id = marker.getId();
                int pos = mHashMap.get(id);
                mInvoice = mInvoices.get(pos);
                mBtnNearbyReceiveOrder.setTag(mInvoice.getStringId());
                User mUser = mInvoice.getUser();
                mTvItemOrderShopName.setText(mUser.getName());
                mRatingOrderWindow.setRating((float) mUser.getRate());
                mTvNearbyDistance.setText(
                    TextFormatUtils.formatDistance(mInvoice.getDistance()));
                mTvNearbyFrom.setText(mInvoice.getAddressStart());
                mTvNearbyTo.setText(mInvoice.getAddressFinish());
                mTvNearbyShipTime.setText(mInvoice.getDeliveryTime());
                mTvNearbyShipPrice.setText(
                    TextFormatUtils.formatPrice(mInvoice.getShippingPrice()));
                mTvNearbyOrderPrice.setText(
                    TextFormatUtils.formatPrice(mInvoice.getPrice()));
                if (mPolylineRoute != null) {
                    mPolylineRoute.remove();
                    mMakerEndOrder.remove();
                }
                mMakerEndOrder = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mInvoice.getLatFinish(), mInvoice.getLngFinish())));
                final LatLng startPoint =
                    new LatLng(mInvoice.getLatStart(), mInvoice.getLngStart());
                final LatLng endPoint =
                    new LatLng(mInvoice.getLatFinish(), mInvoice.getLngFinish());
                MapUtils.routing(startPoint, endPoint,
                    new RoutingListener() {
                        @Override
                        public void onRoutingFailure(RouteException e) {
                        }

                        @Override
                        public void onRoutingStart() {
                        }

                        @Override
                        public void onRoutingSuccess(ArrayList<Route> route,
                                                     int shortestRouteIndex) {
                            PolylineOptions polyOptions = new PolylineOptions();
                            for (int i = 0; i < route.size(); i++) {
                                polyOptions.color(ContextCompat.getColor(
                                    mContext.getApplicationContext(),
                                    R.color.colorGreen));
                                polyOptions.width(8);
                                polyOptions.addAll(route.get(i).getPoints());
                            }
                            if (mPolylineRoute != null && mPolylineRoute.isVisible()) {
                                mPolylineRoute.remove();
                            }
                            mPolylineRoute = mGoogleMap.addPolyline(polyOptions);
                            configSizeMap();
                            LatLng configLatLng = configLatLng(startPoint, endPoint);
                            MapUtils.updateZoomMap(mGoogleMap, mWidthMap, mHeightMap,
                                startPoint, endPoint, configLatLng);
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
                switchAutoRefresh(true);
                mRlOrderDetail.setVisibility(View.GONE);
            }
        });
    }

    /**
     * place the path in center of screen
     *
     * @param startPoint start point of path
     * @param endPoint   end point of pat
     * @return
     */
    private LatLng configLatLng(LatLng startPoint, LatLng endPoint) {
        double diffHeight = startPoint.latitude - endPoint.latitude;
        double diffWidth = startPoint.longitude - endPoint.longitude;
        double scale = 1;
        LatLng latLng;
        if (Math.abs(diffHeight) > Math.abs(diffWidth)) {
            if (diffHeight > 0) {
                latLng =
                    new LatLng(endPoint.latitude - scale * diffHeight - Math.abs(diffWidth) / 2,
                        (startPoint.longitude + endPoint.longitude) / 2);
            } else {
                latLng =
                    new LatLng(startPoint.latitude + scale * diffHeight - Math.abs(diffWidth) / 2,
                        (startPoint.longitude + endPoint.longitude) / 2);
            }
        } else {
            if (diffWidth > 0) {
                latLng = new LatLng(startPoint.latitude - scale * diffWidth,
                    (startPoint.longitude + endPoint.longitude) / 2);
            } else {
                latLng = new LatLng(endPoint.latitude + scale * diffWidth,
                    (startPoint.longitude + endPoint.longitude) / 2);
            }
        }
        return latLng;
    }

    private void showReceiveDialog(final String invoiceId) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        View view =
            LayoutInflater.from(mContext).inflate(R.layout.dialog_nearby_receive_order, null);
        dialog.setView(view);
        view.findViewById(R.id.confirm_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog loading = CommonUtils.showLoadingDialog(mContext);
                API.postShipperReceiveInvoice(
                    Config.getInstance().getUserInfo(mContext).getAuthenticationToken(),
                    invoiceId,
                    new API.APICallback<APIResponse<EmptyData>>() {
                        @Override
                        public void onResponse(APIResponse<EmptyData> response) {
                            loading.dismiss();
                            dialog.dismiss();
                            Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            loading.dismiss();
                            dialog.dismiss();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                );
            }
        });
        view.findViewById(R.id.confirm_dialog_cancel)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_content, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_filter) {
            Intent intent = new Intent(mContext, FilterOrderActivity.class);
            startActivityForResult(intent, REQUEST_FILTER);
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
                MapUtils.zoomToPosition(mGoogleMap, new LatLng(latitude, longitude));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mContext, data);
                Toast.makeText(mContext, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, " Search Cancel");
            }
        } else if (requestCode == Const.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                initMap();
            }
        } else if (requestCode == Const.REQUEST_SETTING) {
            if (resultCode == Activity.RESULT_OK) {
                initMap();
            }
        } else if (requestCode == REQUEST_FILTER
            && resultCode ==
            Activity.RESULT_OK) {
            // Update list invoice in map
            String jsonData = data.getStringExtra(FilterOrderActivity.INTENT_FILTER_DATA);
            List<Invoice> listInvoices = new Gson().fromJson(
                jsonData,
                new TypeToken<List<Invoice>>() {
                }.getType()
            );
            addListMarker(listInvoices);
        }
    }

    /**
     * Task fetch address from location in another thread
     */
    private class FetchAddressTask extends AsyncTask<LatLng, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTvSearchArea.setText("...");
        }

        @Override
        protected String doInBackground(LatLng... latLngs) {
            double latitude = latLngs[0].latitude;
            double longitude = latLngs[0].longitude;
            return MapUtils.getAddressFromLocation(
                mContext,
                new LatLng(latitude, longitude)
            );
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            if (mTvSearchArea != null) {
                mTvSearchArea.setText(address);
            }
        }
    }
}
