package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.Const.Storage;
import com.framgia.ishipper.util.StorageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HungNT on 9/16/16.
 */
public class SettingActivity extends ToolbarActivity {
    @BindView(R.id.cbReceiveNotification) CheckBox cbReceiveNotification;
    @BindView(R.id.seekbar_invoice_radius) SeekBar seekbarInvoiceRadius;
    @BindView(R.id.tvInvoiceRadius) TextView tvInvoiceRadius;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private int mInvoiceRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
        settingInvoiceRadiusSeekBar();
    }

    private void settingInvoiceRadiusSeekBar() {
        seekbarInvoiceRadius.setMax(Const.SETTING_MAX_INVOICE_RADIUS);
        seekbarInvoiceRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Set Progress from [1, Max_Progress + 1]
                mInvoiceRadius = progress + 1;

                tvInvoiceRadius
                    .setText(getString(R.string.fragment_setting_invoice_radius, mInvoiceRadius));
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
        StorageUtils.setValue(this, Storage.KEY_SETTING_NOTIFICATION,
            cbReceiveNotification.isChecked());
        StorageUtils.setValue(this, Storage.KEY_SETTING_INVOICE_RADIUS,
            mInvoiceRadius);
    }

    private void initView() {
        mInvoiceRadius = StorageUtils.getIntValue(this,
            Storage.KEY_SETTING_INVOICE_RADIUS, Const.SETTING_INVOICE_RADIUS_DEFAULT);
        cbReceiveNotification.setChecked(StorageUtils.getBooleanValue(this,
            Storage.KEY_SETTING_NOTIFICATION, true));
        seekbarInvoiceRadius.setProgress(mInvoiceRadius - 1);
        tvInvoiceRadius
            .setText(getString(R.string.fragment_setting_invoice_radius, mInvoiceRadius));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveSetting();
            onBackPressed();
        }
        return true;
    }
}
