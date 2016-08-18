package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.FilterInvoiceData;
import com.framgia.ishipper.util.MapUtils;
import com.google.android.gms.maps.model.LatLng;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterOrderActivity extends ToolbarActivity {
    private static final String TAG = "FilterOrderActivity";
    private User mUser;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.appbar) AppBarLayout mAppbar;
    @BindView(R.id.seekbar_filter_order_price) RangeSeekBar mSeekbarFilterOrderPrice;
    @BindView(R.id.seekbar_filter_ship_price) RangeSeekBar mSeekbarFilterShipPrice;
    @BindView(R.id.seekbar_filter_distance) RangeSeekBar mSeekbarFilterDistance;
    @BindView(R.id.seekbar_filter_weight) RangeSeekBar mSeekbarFilterWeight;
    @BindView(R.id.edt_address_start) EditText mEdtAddressStart;
    @BindView(R.id.seekbar_filter_radius) RangeSeekBar mSeekbarFilterRadius;
    @BindView(R.id.btn_filter_invoice) Button mBtnFilterInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_order);
        ButterKnife.bind(this);
        getWidgetControl();
        mUser = Config.getInstance().getUserInfo(this);
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

    @OnClick(R.id.btn_filter_invoice)
    public void onClick() {
        LatLng currentLatLng = MapUtils.getLocationFromAddress(
                getBaseContext(),
                mEdtAddressStart.getText().toString());
        Map<String, String> params = new HashMap<>();
        if (currentLatLng != null) {
            params.put(APIDefinition.FilterInvoice.CURRENT_LAT_PARAM,
                    currentLatLng.latitude + "");
            params.put(APIDefinition.FilterInvoice.CURRENT_LONG_PARAM,
                    currentLatLng.longitude + "");
            params.put(APIDefinition.FilterInvoice.RADIUS_PARAM,
                    mSeekbarFilterRadius.getSelectedMaxValue() + "");
        }
        params.put(APIDefinition.FilterInvoice.MIN_DISTANCE_PARAM,
                mSeekbarFilterDistance.getSelectedMinValue() + "");
        params.put(APIDefinition.FilterInvoice.MAX_DISTANCE_PARAM,
                mSeekbarFilterDistance.getSelectedMaxValue() + "");
        params.put(APIDefinition.FilterInvoice.MIN_ORDER_PRICE_PARAM,
                mSeekbarFilterOrderPrice.getSelectedMinValue() + "");
        params.put(APIDefinition.FilterInvoice.MAX_ORDER_PRICE_PARAM,
                mSeekbarFilterOrderPrice.getSelectedMaxValue() + "");
        params.put(APIDefinition.FilterInvoice.MIN_SHIP_PRICE_PARAM,
                mSeekbarFilterShipPrice.getSelectedMinValue() + "");
        params.put(APIDefinition.FilterInvoice.MAX_SHIP_PRICE_PARAM,
                mSeekbarFilterShipPrice.getSelectedMaxValue() + "");
        params.put(APIDefinition.FilterInvoice.MIN_WEIGHT_PARAM,
                mSeekbarFilterWeight.getSelectedMinValue() + "");
        params.put(APIDefinition.FilterInvoice.MAX_WEIGHT_PARAM,
                mSeekbarFilterWeight.getSelectedMaxValue() + "");

        API.filterInvoice(
                mUser.getAuthenticationToken(),
                params,
                new API.APICallback<APIResponse<FilterInvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<FilterInvoiceData> response) {
                        // TODO: 18/08/2016
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        // TODO: 18/08/2016
                    }
                });
    }
}
