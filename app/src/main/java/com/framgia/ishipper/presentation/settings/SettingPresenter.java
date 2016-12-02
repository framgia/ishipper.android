package com.framgia.ishipper.presentation.settings;

import android.content.Context;
import android.content.Intent;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.presentation.block.BlackListActivity;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.StorageUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.HashMap;

/**
 * Created by HungNT on 11/22/16.
 */

class SettingPresenter implements SettingContact.Presenter {

    private final SettingContact.View mView;
    private BaseActivity mActivity;
    private User mCurrentUser;
    private Context mContext;


    public SettingPresenter(SettingContact.View view, BaseActivity activity) {
        mView = view;
        mActivity = activity;
        mCurrentUser = Config.getInstance().getUserInfo(mActivity);
        mContext = activity.getBaseContext();
    }


    @Override
    public void saveSetting(final boolean receiveNotification, final boolean favoriteLocation, final int invoiceRadius, final Place mPlace) {
        if (mCurrentUser != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put(APIDefinition.UserSetting.PARAM_USER_ID, mCurrentUser.getId());
            params.put(APIDefinition.UserSetting.PARAM_RECEIVE_NOTIFICATION,
                    String.valueOf(receiveNotification));
            params.put(APIDefinition.UserSetting.PARAM_FAVORITE_LOCATION,
                    String.valueOf(favoriteLocation));
            if (mPlace != null) {
                params.put(APIDefinition.UserSetting.PARAM_ADDRESS, mPlace.getAddress().toString());
                params.put(APIDefinition.UserSetting.PARAM_LATITUDE, String.valueOf(mPlace.getLatLng().latitude));
                params.put(APIDefinition.UserSetting.PARAM_LONGITUDE, String.valueOf(mPlace.getLatLng().longitude));
            }
            params.put(APIDefinition.UserSetting.PARAM_RADIUS, String.valueOf(invoiceRadius));
            API.updateUserSetting(mCurrentUser.getAuthenticationToken(),
                    params,
                    new API.APICallback<APIResponse<EmptyData>>() {
                        @Override
                        public void onResponse(APIResponse<EmptyData> response) {
                            saveSettingLocal(receiveNotification, favoriteLocation, invoiceRadius, mPlace);
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            mActivity.showUserMessage(message);
                        }
                    });
        }


    }

    @Override
    public void saveSettingLocal(boolean receiveNotification, boolean favoriteLocation, int invoiceRadius, Place mPlace) {
        StorageUtils.setValue(mContext, Const.Storage.KEY_SETTING_NOTIFICATION,
                receiveNotification);
        StorageUtils.setValue(mContext, Const.Storage.KEY_SETTING_INVOICE_RADIUS, invoiceRadius);
        StorageUtils.setValue(mContext, Const.Storage.KEY_SETTING_LOCATION, favoriteLocation);
        if (mPlace == null) {
            StorageUtils.remove(mContext, Const.Storage.KEY_SETTING_ADDRESS);
            StorageUtils.remove(mContext, Const.Storage.KEY_SETTING_LATITUDE);
            StorageUtils.remove(mContext, Const.Storage.KEY_SETTING_LONGITUDE);
        } else {
            StorageUtils.setValue(mContext, Const.Storage.KEY_SETTING_ADDRESS, mPlace.getAddress().toString());
            StorageUtils.setValue(mContext, Const.Storage.KEY_SETTING_LATITUDE, String.valueOf(mPlace.getLatLng().latitude));
            StorageUtils.setValue(mContext, Const.Storage.KEY_SETTING_LONGITUDE, String.valueOf(mPlace.getLatLng().longitude));
        }
    }

    @Override
    public void startBlacklistActivity() {
        Intent intent = new Intent(mActivity, BlackListActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public void pickFavoriteLocation(int requestCode) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            Intent searchIntent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(mActivity);
            mActivity.startActivityForResult(searchIntent, requestCode);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            mActivity.showUserMessage(R.string.err_google_play_service_not_available);
        }
    }

    @Override
    public void getSetting() {
        //TODO get setting from server
    }

    @Override
    public void onPickFavoritePlace(Intent data) {
        Place place = PlaceAutocomplete.getPlace(mActivity, data);
        mView.setPlace(place.getName().toString());
    }
}
