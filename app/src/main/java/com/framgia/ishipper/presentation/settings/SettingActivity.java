package com.framgia.ishipper.presentation.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.Const.Storage;
import com.framgia.ishipper.util.StorageUtils;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by HungNT on 9/16/16.
 */
public class SettingActivity extends BaseToolbarActivity implements SettingContact.View {
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
    private boolean mFavoriteLocationEnable;
    private SettingPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SettingPresenter(this, this);
    }

    @Override
    public void initViews() {
        User currentUser = Config.getInstance().getUserInfo(this);
        mInvoiceRadius = StorageUtils.getIntValue(this, Storage.KEY_SETTING_INVOICE_RADIUS,
                Const.SETTING_INVOICE_RADIUS_DEFAULT);
        cbReceiveNotification.setChecked(currentUser.getNotification() == Const.Notification.ON);
        seekbarInvoiceRadius.setProgress(mInvoiceRadius - 1);
        tvInvoiceRadius.setText(
                getString(R.string.fragment_setting_invoice_radius, mInvoiceRadius));

        // settingInvoiceRadiusSeekBar
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

        // Favorite Location
        //TODO fetch data from server
        setFavoriteCheckbox(mFavoriteLocationEnable);
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.nav_setting_item;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
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
                mPresenter.startBlacklistActivity();
                break;
            case R.id.ll_setting_notification:
                cbReceiveNotification.setChecked(!cbReceiveNotification.isChecked());
                break;
            case R.id.layout_favorite_location:
                mPresenter.pickFavoriteLocation(PLACE_AUTOCOMPLETE_REQUEST_CODE);
                break;
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
                mPresenter.onPickFavoritePlace(data);
                //TODO request save favorite location
            }
        }
    }

    @Override
    public void setFavoriteCheckbox(boolean isEnable) {
        mCbFavoriteLocation.setChecked(isEnable);
        mCbFavoriteLocation.setEnabled(isEnable);
        if (!isEnable) {
            mTvFavoriteContent.setText(R.string.setting_favorite_location_content);
        }
    }

    @Override
    public void setPlace(String name) {
        mTvFavoriteContent.setText(name);
        setFavoriteCheckbox(true);
    }
}
