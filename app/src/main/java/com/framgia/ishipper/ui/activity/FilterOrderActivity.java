package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.framgia.ishipper.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterOrderActivity extends ToolbarActivity {
    private static final String TAG = "FilterOrderActivity";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.appbar) AppBarLayout mAppbar;
    @BindView(R.id.seekbar_filter_order_price) RangeSeekBar mSeekbarFilterOrderPrice;
    @BindView(R.id.seekbar_filter_ship_price) RangeSeekBar mSeekbarFilterShipPrice;
    @BindView(R.id.seekbar_filter_distance) RangeSeekBar mSeekbarFilterDistance;
    @BindView(R.id.seekbar_filter_weight) RangeSeekBar mSeekbarFilterWeight;
    @BindView(R.id.edt_address_start) EditText mEdtAddressStart;
    @BindView(R.id.seekbar_filter_radius) RangeSeekBar mSeekbarFilterRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_order);
        ButterKnife.bind(this);
        getWidgetControl();
    }

    private void getWidgetControl() {
        mSeekbarFilterOrderPrice.setNotifyWhileDragging(true);
        mSeekbarFilterDistance.setNotifyWhileDragging(true);
        mSeekbarFilterRadius.setNotifyWhileDragging(true);
        mSeekbarFilterShipPrice.setNotifyWhileDragging(true);
        mSeekbarFilterWeight.setNotifyWhileDragging(true);
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.all_filter_order;
    }

}
