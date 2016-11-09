package com.framgia.ishipper.ui.fragment;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.activity.ShopCreateOrderActivity;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.MapUtils;
import com.framgia.ishipper.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class ShopCreateOrderStep1Fragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ShopCreateOrder1";
    private final int NONE = 1;
    private final int PICK_START_POINT = 2;
    private final int PICK_END_POINT = 3;

    @BindView(R.id.layoutMapContainer) FrameLayout mFrameMapContainer;
    @BindView(R.id.imgPickPosition) ImageView mImgPickPosition;
    @BindView(R.id.edt_address_start) PlacesAutocompleteTextView mEdtAddressStart;
    @BindView(R.id.edt_address_end) PlacesAutocompleteTextView mEdtAddressEnd;
    @BindView(R.id.btnPickStart) ImageView mBtnPickStart;
    @BindView(R.id.btnPickEnd) ImageView mBtnPickEnd;
    @BindView(R.id.tvDistance) TextView mTvDistance;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mMap;
    private Marker mMakerStart, mMakerEnd;
    private int mStatus = NONE;
    private LatLng mLatLngStart;
    private LatLng mLatLngFinish;
    private Polyline mPolylineRoute;
    private Context mContext;
    private FetchAddressTask task;
    private float mDistance;
    private AsyncTask mGetDistanceTask;
    private boolean mDisableCameraChange;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_shop_create_order_step1, container, false);
        ButterKnife.bind(this, view);
        initView();

        return view;
    }

    @OnFocusChange({R.id.edt_address_start, R.id.edt_address_end})
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.edt_address_start:
                if (hasFocus) {
                    setPickStartLocation();
                    if (mLatLngFinish == null) return;

                    mBtnPickEnd.setImageResource(R.drawable.ic_map_picker_end);
                    mMakerEnd = mMap.addMarker(
                            new MarkerOptions()
                                    .position(mLatLngFinish)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_picker_end)));
                }
                break;
            case R.id.edt_address_end:
                if (hasFocus) {
                    setPickEndLocation();
                    mBtnPickStart.setImageResource(R.drawable.ic_map_picker_start);
                    mMakerStart = mMap.addMarker(
                            new MarkerOptions()
                                    .position(mLatLngStart)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_picker_start)));
                }
                break;
        }
    }

    private void connectToGoogleApi() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @OnClick({R.id.btnPickStart, R.id.btnPickEnd, R.id.btnContinue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPickStart:
                // if user is picking start point
                if (mStatus == PICK_START_POINT) {
                    addStartLocation();
                    // if user haven't pick end point
                    if (mMakerEnd == null) {
                        setPickEndLocation();
                    } else {
                        setDonePickLocation();
                    }
                    // if user is picking end point
                } else if (mStatus == PICK_END_POINT) {
                    addEndLocation();
                    setDonePickLocation();
                    // if user isn't picking start point or end point
                } else {
                    mImgPickPosition.setImageResource(R.drawable.ic_map_picker_start);
                    // if user haven't pick end point then reset
                    if (mMakerEnd != null) {
                        mMakerEnd = null;
                        mEdtAddressEnd.setText("");
                        mTvDistance.setText("");
                        mMap.clear();
                    }
                    setPickStartLocation();
                }

                break;
            case R.id.btnPickEnd:
                // if user is picking end point
                if (mStatus == PICK_END_POINT) {
                    addEndLocation();
                    setDonePickLocation();
                    // if user is picking start point
                } else if (mStatus == PICK_START_POINT) {
                    addStartLocation();
                    if (mMakerEnd == null) {
                        setPickEndLocation();
                    } else {
                        setDonePickLocation();
                    }
                    // if user isn't picking start point or end point
                } else {
                    mImgPickPosition.setImageResource(R.drawable.ic_map_picker_end);
                    setPickEndLocation();
                }
                break;
            case R.id.btnContinue:
                setDonePickLocation();
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
        ShopCreateOrderActivity.sInvoice.setDistance(mDistance);
    }

    private void addEndLocation() {
        mBtnPickEnd.setImageResource(R.drawable.ic_map_picker_end);
        mLatLngFinish = mMap.getCameraPosition().target;
        mMakerEnd = mMap.addMarker(
                new MarkerOptions().position(mLatLngFinish)
                        .icon(BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_map_picker_end))
        );
    }

    private void addStartLocation() {
        mBtnPickStart.setImageResource(R.drawable.ic_map_picker_start);
        mLatLngStart = mMap.getCameraPosition().target;
        mMakerStart = mMap.addMarker(
                new MarkerOptions()
                        .position(mLatLngStart)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_picker_start)));
    }

    private void setDonePickLocation() {
        mStatus = NONE;
        mImgPickPosition.setImageDrawable(null);
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
                Route route;
                int distance = 0;
                for (int j = 0; j < arrayList.size(); j++) {
                    route = arrayList.get(j);
                    polyOptions.color(ContextCompat.getColor(getContext().getApplicationContext(),
                            R.color.colorGreen));
                    polyOptions.width(8);
                    polyOptions.addAll(arrayList.get(j).getPoints());
                    distance += route.getDistanceValue();
                }

                // Show distance
                mDistance = CommonUtils.convertMetreToKm(distance);
                mTvDistance.setText(getString(R.string.text_distance, mDistance));

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

    private void getDistance(LatLng start, LatLng end) {
        if (start == null || end == null) return;
        if (mGetDistanceTask != null && !mGetDistanceTask.isCancelled()) {
            mGetDistanceTask.cancel(true);
        }


        mGetDistanceTask = MapUtils.routing(start, end, new RoutingListener() {
            @Override
            public void onRoutingFailure(RouteException e) {

            }

            @Override
            public void onRoutingStart() {
                mTvDistance.setText(R.string.all_symbol_loading);
            }

            @Override
            public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                int distance = arrayList.get(0).getDistanceValue();

                // Show distance
                mDistance = CommonUtils.convertMetreToKm(distance);
                mTvDistance.setText(getString(R.string.text_distance, mDistance));
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
        mBtnPickStart.setImageResource(R.drawable.ic_done);
    }

    private void setPickEndLocation() {
        mDisableCameraChange = true;
        mStatus = PICK_END_POINT;
        startPickLocation(mMakerEnd);
        mImgPickPosition.setImageResource(R.drawable.ic_map_picker_end);
        mBtnPickEnd.setImageResource(R.drawable.ic_done);
    }

    private void startPickLocation(Marker marker) {
        mDisableCameraChange = true;
        if (marker != null) {
            marker.remove();
            MapUtils.zoomToPosition(mMap, marker.getPosition());
            marker = null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (PermissionUtils.checkLocationPermission(mContext)) return;

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (mDisableCameraChange) {
                    mDisableCameraChange = false;
                    return;
                }
                if (mStatus == NONE) return;
                LatLng position = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                if (mStatus == PICK_START_POINT) {
                    mLatLngStart = position;
                } else if (mStatus == PICK_END_POINT) {
                    mLatLngFinish = position;
                }

                if (task != null) {
                    task.cancel(true);
                }
                task = new FetchAddressTask();
                task.execute(position);
                getDistance(mLatLngStart, mLatLngFinish);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (PermissionUtils.checkLocationPermission(mContext)) return;
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            MapUtils.zoomToPosition(mMap, new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            com.framgia.ishipper.common.Log.w(TAG, mLocation.getLatitude() + "");
            mLatLngStart = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            setPickStartLocation();
        } else {
            Toast.makeText(
                    getContext(),
                    getString(R.string.all_cant_get_location),
                    Toast.LENGTH_SHORT).show();
        }

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

    /**
     * Task fetch address from location in another thread
     */
    private class FetchAddressTask extends AsyncTask<LatLng, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String loading = "...";
            if (mStatus == PICK_START_POINT) {
                mEdtAddressStart.setText(loading);
            } else if (mStatus == PICK_END_POINT) {
                mEdtAddressEnd.setText(loading);
            }
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
            if (mStatus == NONE) return;
            PlacesAutocompleteTextView tvShowing;
            tvShowing = mStatus == PICK_START_POINT ? mEdtAddressStart : mEdtAddressEnd;
            // Disable dropdown showing up
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tvShowing.setText(address, false);
            } else {
                mEdtAddressStart.setFocusable(false);
                mEdtAddressStart.setFocusableInTouchMode(false);
                mEdtAddressStart.setText(address);
                mEdtAddressStart.setFocusable(true);
                mEdtAddressStart.setFocusableInTouchMode(true);
            }
        }
    }

    private void initView() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.layoutMapContainer, mapFragment).commit();
        connectToGoogleApi();

        mEdtAddressStart.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mEdtAddressStart.clearFocus();
                mEdtAddressStart.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(PlaceDetails placeDetails) {
                        mLatLngStart = new LatLng(placeDetails.geometry.location.lat,
                                placeDetails.geometry.location.lng);
                        mDisableCameraChange = true;
                        MapUtils.zoomToPosition(mMap, mLatLngStart);
                        getDistance(mLatLngStart, mLatLngFinish);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        // Dont update
                    }
                });
            }
        });

        mEdtAddressEnd.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mEdtAddressEnd.clearFocus();
                mEdtAddressEnd.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(PlaceDetails placeDetails) {
                        mLatLngFinish = new LatLng(placeDetails.geometry.location.lat,
                                placeDetails.geometry.location.lng);
                        mDisableCameraChange = true;
                        MapUtils.zoomToPosition(mMap, mLatLngFinish);
                        getDistance(mLatLngStart, mLatLngFinish);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        // Dont update
                    }
                });
            }
        });
    }
}
