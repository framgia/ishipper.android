package com.framgia.ishipper.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.framgia.ishipper.util.CommonUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.framgia.ishipper.ui.activity.ShopCreateOrderActivity.sInvoice;

public class ShopCreateOrderStep3Fragment extends Fragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.btn_detail_show_path) TextView mBtnDetailShowPath;
    @BindView(R.id.btn_detail_customer_call) TextView mBtnDetailCustomerCall;
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
    @BindView(R.id.btn_detail_show_shipper) TextView mBtnDetailShowShipper;
    @BindView(R.id.tv_detail_customer_name) TextView mTvDetailCustomerName;
    @BindView(R.id.tv_detail_customer_phone) TextView mTvDetailCustomerPhone;
    @BindView(R.id.btn_detail_cancel_order) Button mBtnDetailCancelOrder;
    private User mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_oder_detail, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mToolbar.setVisibility(View.GONE);
        mCurrentUser = Config.getInstance().getUserInfo(getContext());
        mBtnCreateOrder.setText(R.string.create_order);
        mBtnCreateOrder.setVisibility(View.VISIBLE);
        mTvDetailDistance.setText(String.valueOf(sInvoice.getDistance()));
        mTvDetailStart.setText(sInvoice.getAddressStart());
        mTvDetailEnd.setText(sInvoice.getAddressFinish());
        mTvDetailOrderName.setText(sInvoice.getName());
        mTvDetailShipPrice.setText(String.valueOf(sInvoice.getShippingPrice()));
        mTvDetailOrderPrice.setText(String.valueOf(sInvoice.getPrice()));
        mTvDetailShipTime.setText(String.valueOf(sInvoice.getDeliveryTime()));
        mTvDetailNote.setText(sInvoice.getDescription());
        mTvDetailCustomerName.setText(sInvoice.getCustomerName());
        mTvDetailCustomerPhone.setText(sInvoice.getCustomerNumber());
    }

    @OnClick(R.id.btn_detail_receive_order)
    public void onClick(View view) {
        onCreateInvoice();
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

        final Dialog loadingDialog = CommonUtils.showLoadingDialog(getContext());
        API.createInvoice(mCurrentUser.getAuthenticationToken(), params,
                new API.APICallback<APIResponse<CreateInVoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<CreateInVoiceData> response) {
                        // TODO: Go to invoice manager
                        loadingDialog.dismiss();
                        Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        loadingDialog.dismiss();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
