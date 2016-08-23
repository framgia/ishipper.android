package com.framgia.ishipper.ui.fragment;

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
import com.framgia.ishipper.ui.activity.ShopCreateOrderActivity;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopCreateOrderStep3Fragment extends Fragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.btn_detail_show_path) TextView mBtnDetailShowPath;
    @BindView(R.id.btn_detail_customer_call) TextView mBtnDetailCustomerCall;
    @BindView(R.id.btn_detail_receive_order) Button mBtnCreateOrder;
    private User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_oder_detail, container, false);
        ButterKnife.bind(this, view);
        mToolbar.setVisibility(View.GONE);
        mBtnDetailShowPath.setVisibility(View.GONE);
        mBtnDetailCustomerCall.setVisibility(View.GONE);
        mBtnCreateOrder.setText(R.string.create_order);
        mUser = Config.getInstance().getUserInfo(getContext());
        return view;
    }

    @OnClick(R.id.btn_detail_receive_order)
    public void onClick(View view) {
        onCreateInvoice();
    }

    private void onCreateInvoice() {
        ShopCreateOrderActivity.sInvoice.setDistance(3.0234f);
        ShopCreateOrderActivity.sInvoice.setStatus(Invoice.STATUS_INIT);

        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.CreateInvoice.PARAM_NAME, ShopCreateOrderActivity.sInvoice.getName());
        params.put(APIDefinition.CreateInvoice.PARAM_ADDRESS_START,
                   ShopCreateOrderActivity.sInvoice.getAddressStart());
        params.put(APIDefinition.CreateInvoice.PARAM_LATITUDE_START,
                   ShopCreateOrderActivity.sInvoice.getLatStart().toString());
        params.put(APIDefinition.CreateInvoice.PARAM_LONGITUDE_START,
                   ShopCreateOrderActivity.sInvoice.getLngStart().toString());
        params.put(APIDefinition.CreateInvoice.PARAM_ADDRESS_FINISH,
                   ShopCreateOrderActivity.sInvoice.getAddressFinish());
        params.put(APIDefinition.CreateInvoice.PARAM_LATITUDE_FINISH,
                   ShopCreateOrderActivity.sInvoice.getLatFinish().toString());
        params.put(APIDefinition.CreateInvoice.PARAM_LONGITUDE_FINISH,
                   ShopCreateOrderActivity.sInvoice.getLngFinish().toString());
        params.put(APIDefinition.CreateInvoice.PARAM_DELIVERY_TIME,
                   ShopCreateOrderActivity.sInvoice.getDeliveryTime());
        params.put(APIDefinition.CreateInvoice.PARAM_DISTANCE,
                   ShopCreateOrderActivity.sInvoice.getDistance().toString());
        params.put(APIDefinition.CreateInvoice.PARAM_DESCRIPTION,
                   ShopCreateOrderActivity.sInvoice.getDescription());
        params.put(APIDefinition.CreateInvoice.PARAM_PRICE,
                   ShopCreateOrderActivity.sInvoice.getPrice().toString());
        params.put(APIDefinition.CreateInvoice.PARAM_SHIPPING_PRICE,
                   ShopCreateOrderActivity.sInvoice.getShippingPrice().toString());
        params.put(APIDefinition.CreateInvoice.PARAM_STATUS, ShopCreateOrderActivity.sInvoice.getStatus());
        params.put(APIDefinition.CreateInvoice.PARAM_WEIGHT,
                   ShopCreateOrderActivity.sInvoice.getWeight().toString());
        params.put(APIDefinition.CreateInvoice.PARAM_CUSTOMER_NAME,
                   ShopCreateOrderActivity.sInvoice.getCustomerName());
        params.put(APIDefinition.CreateInvoice.PARAM_CUSTOMER_NUMBER,
                   ShopCreateOrderActivity.sInvoice.getCustomerNumber());

        API.createInvoice(mUser.getAuthenticationToken(), params,
                          new API.APICallback<APIResponse<CreateInVoiceData>>() {
                              @Override
                              public void onResponse(APIResponse<CreateInVoiceData> response) {
                                  // TODO: Go to invoice manager
                                  Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT)
                                          .show();
                                  getActivity().finish();
                              }

                              @Override
                              public void onFailure(int code, String message) {
                                  Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                              }
                          });
    }
}
