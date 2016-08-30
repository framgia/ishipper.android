package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.FilterInvoiceData;
import com.framgia.ishipper.util.MapUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterOrderActivity extends ToolbarActivity {
    private static final String TAG = "FilterOrderActivity";
    public static final String INTENT_FILTER_DATA = "INTENT_FILTER_DATA";
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

    @OnClick({
            R.id.tv_filter_order_price,
            R.id.tv_filter_ship_price,
            R.id.tv_filter_distance,
            R.id.tv_filter_weight,
            R.id.tv_filter_radius,
            R.id.btn_filter_invoice
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_filter_order_price:
                showView((TextView) view, mSeekbarFilterOrderPrice);
                break;
            case R.id.tv_filter_ship_price:
                showView((TextView) view, mSeekbarFilterShipPrice);
                break;
            case R.id.tv_filter_distance:
                showView((TextView) view, mSeekbarFilterDistance);
                break;
            case R.id.tv_filter_weight:
                showView((TextView) view, mSeekbarFilterWeight);
                break;
            case R.id.tv_filter_radius:
                showView((TextView) view, mSeekbarFilterRadius);
                break;
            case R.id.btn_filter_invoice:
                LatLng currentLatLng = MapUtils.getLocationFromAddress(
                        getBaseContext(),
                        mEdtAddressStart.getText().toString());
                Map<String, String> params = new HashMap<>();
                if (currentLatLng != null) {
                    params.put(APIDefinition.FilterInvoice.PARAM_CURRENT_LAT,
                            currentLatLng.latitude + "");
                    params.put(APIDefinition.FilterInvoice.PARAM_CURRENT_LONG,
                            currentLatLng.longitude + "");
                    params.put(APIDefinition.FilterInvoice.PARAM_RADIUS,
                            mSeekbarFilterRadius.getSelectedMaxValue() + "");
                }
                if (isVisible(mSeekbarFilterDistance)) {
                    params.put(APIDefinition.FilterInvoice.PARAM_MIN_DISTANCE,
                            mSeekbarFilterDistance.getSelectedMinValue() + "");
                    params.put(APIDefinition.FilterInvoice.PARAM_MAX_DISTANCE,
                            mSeekbarFilterDistance.getSelectedMaxValue() + "");
                }
                if (isVisible(mSeekbarFilterOrderPrice)) {
                    params.put(APIDefinition.FilterInvoice.PARAM_MIN_ORDER_PRICE,
                            mSeekbarFilterOrderPrice.getSelectedMinValue() + "");
                    params.put(APIDefinition.FilterInvoice.PARAM_MAX_ORDER_PRICE,
                            mSeekbarFilterOrderPrice.getSelectedMaxValue() + "");
                }
                if (isVisible(mSeekbarFilterShipPrice)) {
                    params.put(APIDefinition.FilterInvoice.PARAM_MIN_SHIP_PRICE,
                            mSeekbarFilterShipPrice.getSelectedMinValue() + "");
                    params.put(APIDefinition.FilterInvoice.PARAM_MAX_SHIP_PRICE,
                            mSeekbarFilterShipPrice.getSelectedMaxValue() + "");
                }
                if (isVisible(mSeekbarFilterWeight)) {
                    params.put(APIDefinition.FilterInvoice.PARAM_MIN_WEIGHT,
                            mSeekbarFilterWeight.getSelectedMinValue() + "");
                    params.put(APIDefinition.FilterInvoice.PARAM_MAX_WEIGHT,
                            mSeekbarFilterWeight.getSelectedMaxValue() + "");
                }

                API.filterInvoice(
                        mUser.getAuthenticationToken(),
                        params,
                        new API.APICallback<APIResponse<FilterInvoiceData>>() {
                            @Override
                            public void onResponse(APIResponse<FilterInvoiceData> response) {
                                Intent intent = getIntent();
                                String data = new Gson().toJson(response.getData().getInvoiceList(),
                                        new TypeToken<List<Invoice>>() {}.getType());

                                intent.putExtra(INTENT_FILTER_DATA, data);
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Toast.makeText(FilterOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
    }

    private void showView(TextView textView, RangeSeekBar seekBar) {
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                seekBar.getVisibility() == View.VISIBLE
                        ? R.drawable.ic_arrow_drop_down_24dp
                        : R.drawable.ic_arrow_drop_up_24dp, 0);
        seekBar.setVisibility(seekBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }
}
