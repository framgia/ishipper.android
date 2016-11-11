package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.UpdateProfileData;
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
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 0x1234;
    @BindView(R.id.cbReceiveNotification) CheckBox cbReceiveNotification;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        mCurrentUser = Config.getInstance().getUserInfo(this);
        mInvoiceRadius = StorageUtils.getIntValue(this, Storage.KEY_SETTING_INVOICE_RADIUS,
                Const.SETTING_INVOICE_RADIUS_DEFAULT);
        cbReceiveNotification.setChecked(mCurrentUser.getNotification() == Const.Notification.ON);
        seekbarInvoiceRadius.setProgress(mInvoiceRadius - 1);
        tvInvoiceRadius.setText(
                getString(R.string.fragment_setting_invoice_radius, mInvoiceRadius));
        settingInvoiceRadiusSeekBar();

        // Favorite Location
        //TODO fetch data from server
        setFavoriteCheckbox(mFavoriteLocationEnable);
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
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void saveSetting() {
        if (mCurrentUser != null) {
            mCurrentUser.setNotification(cbReceiveNotification.isChecked()
                    ? Const.Notification.ON
                    : Const.Notification.OFF);
            Config.getInstance().setUserInfo(this, mCurrentUser);

            HashMap<String, String> params = new HashMap<>();
            params.put(APIDefinition.PutUpdateProfile.PARAM_NOTIFICATION,
                    String.valueOf(mCurrentUser.getNotification()));
            API.putUpdateProfile(params, new API.APICallback<APIResponse<UpdateProfileData>>() {
                @Override
                public void onResponse(APIResponse<UpdateProfileData> response) {
                    Toast.makeText(
                            SettingActivity.this,
                            response.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }

                @Override
                public void onFailure(int code, String message) {
                    Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
        StorageUtils.setValue(this, Storage.KEY_SETTING_NOTIFICATION,
                cbReceiveNotification.isChecked());
        StorageUtils.setValue(this, Storage.KEY_SETTING_INVOICE_RADIUS, mInvoiceRadius);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
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
                cbReceiveNotification.setChecked(!cbReceiveNotification.isChecked());
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
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //TODO request save favorite location
                Place place = PlaceAutocomplete.getPlace(this, data);
                mTvFavoriteContent.setText(place.getName());
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
