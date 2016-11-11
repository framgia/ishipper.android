package com.framgia.ishipper.ui.fragment;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.GetUserData;
import com.framgia.ishipper.net.data.ShipperNearbyData;
import com.framgia.ishipper.ui.listener.LocationSettingCallback;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.MapUtils;
import com.framgia.ishipper.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by dinhduc on 20/07/2016.
 */
public class NearbyShipperFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "NearbyShipperFragment";
    private static final float RADIUS = 5;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Unbinder mUnbinder;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private ArrayList<User> shipperList = new ArrayList<>();
    private SupportMapFragment mMapFragment;
    private User mCurrentUser;
    private Context mContext;
    private FetchAddressTask task;
    private Dialog mDialog;

    @BindView(R.id.tv_main_search_area) TextView mTvSearchArea;

    public static NearbyShipperFragment newInstance() {
        NearbyShipperFragment fragment = new NearbyShipperFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_shipper, null);
        mUnbinder = ButterKnife.bind(this, view);
        mCurrentUser = Config.getInstance().getUserInfo(mContext);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_nearby_shipper);
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
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        if (PermissionUtils.checkLocationPermission(mContext)) {
            return;
        }
        mGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        initMap();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        // check location permission
        if (!PermissionUtils.checkLocationPermission(mContext)) {
            // check location setting
            CommonUtils.checkLocationRequestSetting(
                    getActivity(),
                    mGoogleApiClient,
                    new LocationSettingCallback() {
                        @Override
                        public void onSuccess() {
                            mMapFragment.getMapAsync(NearbyShipperFragment.this);
                        }
                    });
        } else {
            // request location permission
            PermissionUtils.requestPermission(
                    (AppCompatActivity) getActivity(),
                    Const.RequestCode.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    false
            );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Place place = PlaceAutocomplete.getPlace(mContext, data);
                    Log.d(TAG, "onActivityResult: " + place.getName());
                    mTvSearchArea.setText(place.getName());
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                    MapUtils.zoomToPosition(mGoogleMap, new LatLng(latitude, longitude));
                    break;
                case PlaceAutocomplete.RESULT_ERROR:
                    Status status = PlaceAutocomplete.getStatus(mContext, data);
                    Toast.makeText(mContext, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(TAG, " Search Cancel");
                    break;
            }
        } else if (requestCode == Const.REQUEST_CHECK_SETTINGS) {
            //            location setting is set up
            if (resultCode == Activity.RESULT_OK) {
                mMapFragment.getMapAsync(NearbyShipperFragment.this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
        )) {
            CommonUtils.checkLocationRequestSetting(
                    getActivity(),
                    mGoogleApiClient,
                    new LocationSettingCallback() {
                        @Override
                        public void onSuccess() {
                            mMapFragment.getMapAsync(NearbyShipperFragment.this);
                        }
                    });
        }
    }

    @OnClick(R.id.rl_search_view)
    public void onClick() {
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
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        onLocationChange(location);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mDialog.dismiss();
    }

    // initialize map
    private void initMap() {
        // Disable tilt and rotate map
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);

        if (PermissionUtils.checkLocationPermission(mContext)) return;
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            mDialog = CommonUtils.showLoadingDialog(mContext);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, new LocationRequest(), NearbyShipperFragment.this);
        } else {
            onLocationChange(mLocation);
            android.util.Log.d(TAG, "initMap: " + mLocation);
        }
    }

    private void onLocationChange(Location location) {
        mCurrentUser.setLatitude(location.getLatitude());
        mCurrentUser.setLongitude(location.getLongitude());
        MapUtils.zoomToPosition(mGoogleMap,
                new LatLng(location.getLatitude(), location.getLongitude()));
        markShipperNearby(mCurrentUser.getLatitude(), mCurrentUser.getLongitude(), RADIUS);
        configGoogleMap();
    }

    /**
     * mark all shippers near a location on map
     *
     * @param latitude  location's latitude
     * @param longitude location's longitude
     * @param radius    radius to find shipper
     */
    private void markShipperNearby(final double latitude, final double longitude, float radius) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_LAT, String.valueOf(latitude));
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_LNG, String.valueOf(longitude));
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_DISTANCE, String.valueOf(radius));
        API.getShipperNearby(mCurrentUser.getAuthenticationToken(), userParams,
                new API.APICallback<APIResponse<ShipperNearbyData>>() {
                    @Override
                    public void onResponse(APIResponse<ShipperNearbyData> response) {
                        Log.d(TAG, "onResponse: " + response.getMessage());
                        shipperList = (ArrayList<User>) response.getData().getUsers();
                        addListMarker(shipperList);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d(TAG, "onFailure: " + message);
                    }
                });
    }

    /**
     * add all marker on map
     *
     * @param users list users
     */
    private void addListMarker(List<User> users) {
        mGoogleMap.clear();
        for (User user : users) {
            addMarkShipper(user);
        }
    }

    private void addMarkShipper(User user) {
        LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_shipper)));
    }


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
                if (task != null) {
                    task.cancel(true);
                }
                task = new FetchAddressTask();
                task.execute(
                        new LatLng(cameraPosition.target.latitude,
                                cameraPosition.target.longitude)
                );
            }
        });
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String id = marker.getId();
                int pos = Integer.parseInt(id.replace("m", ""));
                Log.d(TAG, "onMarkerClick: " + pos);
                if (pos >= shipperList.size()) {
                    return false;
                }
                User shipper = shipperList.get(pos);

                /** get shop information */
                API.getUser(
                        mCurrentUser.getAuthenticationToken(),
                        String.valueOf(shipper.getId()),
                        new API.APICallback<APIResponse<GetUserData>>() {
                            @Override
                            public void onResponse(APIResponse<GetUserData> response) {
                                User shipper = response.getData().getUser();
                                Toast.makeText(mContext, shipper.getName(), Toast.LENGTH_SHORT).show();
                                // TODO: 30/08/2016
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            }
                        });

                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // TODO: 30/08/2016  
            }
        });
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
            markShipperNearby(latitude, longitude, RADIUS);
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
