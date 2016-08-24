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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.Shipper;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ShipperNearbyData;
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
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "NearbyShipperFragment";
    public static final int ZOOM_LEVEL = 15;
    private static final float RADIUS = 5;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Unbinder mUnbinder;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private ArrayList<Shipper> shippers = new ArrayList<>();
    private SupportMapFragment mMapFragment;
    private User mCurrentUser;
    private Context mContext;
    @BindView(R.id.tv_main_search_area) TextView mTvSearchArea;

    public NearbyShipperFragment() {

    }

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_shipper, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_nearby_shipper);
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
        mCurrentUser.setLatitude((float) mLocation.getLatitude());
        mCurrentUser.setLongitude((float) mLocation.getLongitude());
        markShipperNearby(mCurrentUser.getLatitude(), mCurrentUser.getLongitude(), RADIUS);
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

    private void configGoogleMap(List<User> shippers) {
        LatLng currentPos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(ZOOM_LEVEL)
                .target(currentPos)
                .build();
        mGoogleMap.setPadding(0, 150, 0, 0);
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void markShipperNearby(final double latitude, final double longitude, float radius) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_LAT, String.valueOf(latitude));
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_LNG, String.valueOf(longitude));
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_DISTANCE, String.valueOf(radius));
        API.getShipperNearby(mCurrentUser.getAuthenticationToken(), userParams,
                new API.APICallback<APIResponse<ShipperNearbyData>>() {
                    @Override
                    public void onResponse(APIResponse<ShipperNearbyData> response) {
                        Log.d(TAG, "onResponse: " + response.getCode());
                        Toast.makeText(getContext(),
                                response.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                        addListMarker(response.getData().getUsers());
                        MapUtils.zoomToPosition(mGoogleMap, new LatLng(latitude, longitude));
                        configGoogleMap(response.getData().getUsers());
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d(TAG, "onFailure: " + message);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addListMarker(List<User> users) {
        for (User user : users) {
            addMarkShipper(user);
        }
    }

    private void addMarkShipper(User user) {
        LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_marker_shipper)));
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
                markShipperNearby(latitude, longitude, RADIUS);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mContext, data);
                Toast.makeText(mContext, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, " Search Cancel");
            }
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
}
