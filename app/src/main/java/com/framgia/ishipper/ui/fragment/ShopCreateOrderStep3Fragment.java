package com.framgia.ishipper.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.CreateInVoiceData;
import com.framgia.ishipper.presentation.route.RouteActivity;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.TextFormatUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.framgia.ishipper.ui.activity.ShopCreateOrderActivity.sInvoice;

public class ShopCreateOrderStep3Fragment extends Fragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.btn_detail_show_path) View mBtnDetailShowPath;
    @BindView(R.id.btn_detail_customer_call) View mBtnDetailCustomerCall;
    @BindView(R.id.btn_detail_receive_order) Button mBtnCreateOrder;
    @BindView(R.id.tv_detail_distance) TextView mTvDetailDistance;
    @BindView(R.id.tv_detail_start) TextView mTvDetailStart;
    @BindView(R.id.tv_detail_end) TextView mTvDetailEnd;
    @BindView(R.id.tv_detail_suggest) TextView mTvDetailSuggest;
    @BindView(R.id.tv_detail_order_name) TextView mTvDetailOrderName;
    @BindView(R.id.tv_detail_ship_price) TextView mTvDetailShipPrice;
    @BindView(R.id.tv_detail_order_price) TextView mTvDetailOrderPrice;
    @BindView(R.id.tv_detail_ship_time) TextView mTvDetailShipTime;
    @BindView(R.id.tv_detail_note) TextView mTvDetailNote;
    @BindView(R.id.tv_detail_shop_name) TextView mTvDetailShopName;
    @BindView(R.id.tv_detail_shop_phone) TextView mTvDetailShopPhone;
    @BindView(R.id.tv_detail_shipper_name) TextView mTvDetailShipperName;
    @BindView(R.id.tv_detail_shipper_phone) TextView mTvDetailShipperPhone;
    @BindView(R.id.tv_detail_customer_name) TextView mTvDetailCustomerName;
    @BindView(R.id.tv_detail_customer_phone) TextView mTvDetailCustomerPhone;
    @BindView(R.id.btn_detail_cancel_order) LinearLayout mBtnDetailCancelOrder;
    @BindView(R.id.layoutInvoiceStatus) CardView mLayoutInvoiceStatus;
    private User mCurrentUser;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_oder_detail, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mLayoutInvoiceStatus.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        mCurrentUser = Config.getInstance().getUserInfo(mContext);
        mBtnCreateOrder.setText(R.string.create_order);
        mBtnCreateOrder.setVisibility(View.VISIBLE);
        mTvDetailDistance.setText(TextFormatUtils.formatDistance(sInvoice.getDistance()));
        mTvDetailStart.setText(sInvoice.getAddressStart());
        mTvDetailEnd.setText(sInvoice.getAddressFinish());
        mTvDetailOrderName.setText(sInvoice.getName());
        mTvDetailShipPrice.setText(TextFormatUtils.formatPrice(sInvoice.getShippingPrice()));
        mTvDetailOrderPrice.setText(TextFormatUtils.formatPrice(sInvoice.getPrice()));
        mTvDetailShipTime.setText(String.valueOf(sInvoice.getDeliveryTime()));
        mTvDetailNote.setText(sInvoice.getDescription());
        mTvDetailCustomerName.setText(sInvoice.getCustomerName());
        mTvDetailCustomerPhone.setText(sInvoice.getCustomerNumber());
    }

    private void onCreateInvoice() {
        sInvoice.setStatus(Invoice.STATUS_INIT);

        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.CreateInvoice.PARAM_NAME,
                sInvoice.getName());
        params.put(APIDefinition.CreateInvoice.PARAM_ADDRESS_START,
                sInvoice.getAddressStart());
        params.put(APIDefinition.CreateInvoice.PARAM_LATITUDE_START,
                String.valueOf(sInvoice.getLatStart()));
        params.put(APIDefinition.CreateInvoice.PARAM_LONGITUDE_START,
                String.valueOf(sInvoice.getLngStart()));
        params.put(APIDefinition.CreateInvoice.PARAM_ADDRESS_FINISH,
                sInvoice.getAddressFinish());
        params.put(APIDefinition.CreateInvoice.PARAM_LATITUDE_FINISH,
                String.valueOf(sInvoice.getLatFinish()));
        params.put(APIDefinition.CreateInvoice.PARAM_LONGITUDE_FINISH,
                String.valueOf(sInvoice.getLngFinish()));
        params.put(APIDefinition.CreateInvoice.PARAM_DELIVERY_TIME,
                sInvoice.getDeliveryTime());
        params.put(APIDefinition.CreateInvoice.PARAM_DISTANCE,
                String.valueOf(sInvoice.getDistance()));
        params.put(APIDefinition.CreateInvoice.PARAM_DESCRIPTION,
                sInvoice.getDescription());
        params.put(APIDefinition.CreateInvoice.PARAM_PRICE,
                String.valueOf(sInvoice.getPrice()));
        params.put(APIDefinition.CreateInvoice.PARAM_SHIPPING_PRICE,
                String.valueOf(sInvoice.getShippingPrice()));
        params.put(APIDefinition.CreateInvoice.PARAM_STATUS,
                sInvoice.getStatus());
        params.put(APIDefinition.CreateInvoice.PARAM_WEIGHT,
                String.valueOf(sInvoice.getWeight()));
        params.put(APIDefinition.CreateInvoice.PARAM_CUSTOMER_NAME,
                sInvoice.getCustomerName());
        params.put(APIDefinition.CreateInvoice.PARAM_CUSTOMER_NUMBER,
                sInvoice.getCustomerNumber());

        final Dialog loadingDialog = CommonUtils.showLoadingDialog(mContext);
        API.createInvoice(mCurrentUser.getAuthenticationToken(), params,
                new API.APICallback<APIResponse<CreateInVoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<CreateInVoiceData> response) {
                        // TODO: Go to invoice manager
                        loadingDialog.dismiss();
                        Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        loadingDialog.dismiss();
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick({
            R.id.btn_detail_show_path,
            R.id.btn_detail_shop_call,
            R.id.btn_detail_show_shop,
            R.id.btn_detail_receive_order
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_detail_show_path:
                startActivity(new Intent(mContext, RouteActivity.class));
                break;
            case R.id.btn_detail_shop_call:
                break;
            case R.id.btn_detail_show_shop:
                break;
            case R.id.btn_detail_receive_order:
                onCreateInvoice();
                break;

        }
    }
}
