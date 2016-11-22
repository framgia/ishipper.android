package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.model.UserSetting;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.UserSettingData;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.Const.Storage;
import com.framgia.ishipper.util.StorageUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by HungNT on 9/16/16.
 */
public class SettingActivity extends ToolbarActivity {
    private static final String TAG = SettingActivity.class.getName();

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 0x1234;
    @BindView(R.id.cbReceiveNotification) CheckBox mCbReceiveNotification;
    @BindView(R.id.seekbar_invoice_radius) SeekBar seekbarInvoiceRadius;
    @BindView(R.id.tvInvoiceRadius) TextView tvInvoiceRadius;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.layoutInvoiceRadius) LinearLayout layoutInvoiceRadius;
    @BindView(R.id.ll_setting_notification) LinearLayout mSettingNotification;
    @BindView(R.id.layout_blacklist) LinearLayout mLayoutBlacklist;
    @BindView(R.id.tvFavoriteContent) TextView mTvFavoriteContent;
    @BindView(R.id.cbFavoriteLocation) CheckBox mCbFavoriteLocation;
    private int mInvoiceRadius;
    private User mCurrentUser;
    private boolean mFavoriteLocationEnable;
    private Place mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        mCurrentUser = Config.getInstance().getUserInfo(this);
        mInvoiceRadius = StorageUtils.getIntValue(this, Storage.KEY_SETTING_INVOICE_RADIUS,
                Const.SETTING_INVOICE_RADIUS_DEFAULT);
        mCbReceiveNotification.setChecked(mCurrentUser.getNotification() == Const.Notification.ON);
        seekbarInvoiceRadius.setProgress(mInvoiceRadius - 1);
        tvInvoiceRadius.setText(
                getString(R.string.fragment_setting_invoice_radius, mInvoiceRadius));
        settingInvoiceRadiusSeekBar();

        // Favorite Location
        //TODO fetch data from server
        setFavoriteCheckbox(mFavoriteLocationEnable);
        getSettingFromServer();
    }

    private void getSettingFromServer() {
        if (mCurrentUser == null) return;
        API.getUserSetting(mCurrentUser.getAuthenticationToken(), new API.APICallback<APIResponse<UserSettingData>>() {
            @Override
            public void onResponse(
                    APIResponse<UserSettingData> response) {
                UserSetting setting = response.getData().userSetting;
                mCbReceiveNotification.setChecked(setting.isReceiveNotification());
                mCbFavoriteLocation.setChecked(setting.isFavoriteLocation());
                if (setting.isFavoriteLocation()) {
                    mTvFavoriteContent.setText(setting.getAddress());
                }
                try {
                    setRadiusDisplay(Integer.parseInt(setting.getRadiusDisplay()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                saveSettingOnLocale();

            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRadiusDisplay(int radiusDisplay) {
        mInvoiceRadius = radiusDisplay;
        seekbarInvoiceRadius.setProgress(radiusDisplay - 1);
        tvInvoiceRadius.setText(getString(R.string.fragment_setting_invoice_radius, radiusDisplay));
    }

    private void settingInvoiceRadiusSeekBar() {
        if (Config.getInstance().getUserInfo(getApplicationContext()).isShop()) {
            layoutInvoiceRadius.setVisibility(View.GONE);
            return;
        }
        seekbarInvoiceRadius.setMax(Const.SETTING_MAX_INVOICE_RADIUS);
        seekbarInvoiceRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Set Progress from [1, Max_Progress + 1]
                mInvoiceRadius = progress + 1;
                tvInvoiceRadius.setText(
                        getString(R.string.fragment_setting_invoice_radius, mInvoiceRadius));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: start");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: end");
            }
        });
    }

    public void saveSetting() {
        if (mCurrentUser != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put(APIDefinition.UserSetting.PARAM_USER_ID, mCurrentUser.getId());
            params.put(APIDefinition.UserSetting.PARAM_RECEIVE_NOTIFICATION,
                       String.valueOf(mCbReceiveNotification.isChecked()));
            params.put(APIDefinition.UserSetting.PARAM_FAVORITE_LOCATION,
                       String.valueOf(mCbFavoriteLocation.isChecked()));
            if (mPlace != null) {
                params.put(APIDefinition.UserSetting.PARAM_ADDRESS, mPlace.getAddress().toString());
                params.put(APIDefinition.UserSetting.PARAM_LATITUDE, String.valueOf(mPlace.getLatLng().latitude));
                params.put(APIDefinition.UserSetting.PARAM_LONGITUDE, String.valueOf(mPlace.getLatLng().longitude));
            }
            params.put(APIDefinition.UserSetting.PARAM_RADIUS, String.valueOf(mInvoiceRadius));
            API.updateUserSetting(mCurrentUser.getAuthenticationToken(),
                                  params,
                                  new API.APICallback<APIResponse<EmptyData>>() {
                                      @Override
                                      public void onResponse(
                                              APIResponse<EmptyData> response) {
                                          saveSettingOnLocale();
                                      }

                                      @Override
                                      public void onFailure(int code, String message) {
                                          Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT)
                                                  .show();
                                      }
                                  });
        }
    }

    private void saveSettingOnLocale() {
        StorageUtils.setValue(this, Storage.KEY_SETTING_NOTIFICATION,
                              mCbReceiveNotification.isChecked());
        StorageUtils.setValue(this, Storage.KEY_SETTING_INVOICE_RADIUS, mInvoiceRadius);
        StorageUtils.setValue(this, Storage.KEY_SETTING_LOCATION, mCbFavoriteLocation.isChecked());
        if (mPlace == null) {
            StorageUtils.remove(this, Storage.KEY_SETTING_ADDRESS);
            StorageUtils.remove(this, Storage.KEY_SETTING_LATITUDE);
            StorageUtils.remove(this, Storage.KEY_SETTING_LONGITUDE);
        } else {
            StorageUtils.setValue(this, Storage.KEY_SETTING_ADDRESS, mPlace.getAddress().toString());
            StorageUtils.setValue(this, Storage.KEY_SETTING_LATITUDE, String.valueOf(mPlace.getLatLng().latitude));
            StorageUtils.setValue(this, Storage.KEY_SETTING_LONGITUDE, String.valueOf(mPlace.getLatLng().longitude));
        }
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.nav_setting_item;
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                saveSetting();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: 26/10/2016 Save setting here
    }

    @OnClick({R.id.layout_blacklist, R.id.ll_setting_notification, R.id.layout_favorite_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_blacklist:
                Intent intent = new Intent(SettingActivity.this, BlackListActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_setting_notification:
                mCbReceiveNotification.setChecked(! mCbReceiveNotification.isChecked());
                break;
            case R.id.layout_favorite_location:
                pickFavoriteLocation();
                break;
        }
    }

    private void pickFavoriteLocation() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            Intent searchIntent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(searchIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, R.string.err_google_play_service_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @OnCheckedChanged(R.id.cbFavoriteLocation)
    public void settingFavoriteLocation(boolean isChecked) {
        if (!isChecked) {
            setFavoriteCheckbox(false);
            // TODO Request disable favorite location

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                //TODO request save favorite location
                mPlace = PlaceAutocomplete.getPlace(this, data);
                mTvFavoriteContent.setText(mPlace.getName());
                setFavoriteCheckbox(true);
            }
        }
    }

    private void setFavoriteCheckbox(boolean isEnable) {
        mCbFavoriteLocation.setChecked(isEnable);
        mCbFavoriteLocation.setEnabled(isEnable);
        if (!isEnable) {
            mTvFavoriteContent.setText(R.string.setting_favorite_location_content);
        }

    }

}
