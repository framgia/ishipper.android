package com.framgia.ishipper.presentation.invoice.create_invoice;

import android.content.Context;
import android.content.Intent;

import com.framgia.ishipper.base.BaseFragment;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.CreateInVoiceData;
import com.framgia.ishipper.presentation.route.RouteActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by framgia on 18/11/2016.
 */

public class ShopCreateOrderStep3Presenter implements ShopCreateOrderStep3Contract.Presenter {
    private final ShopCreateOrderStep3Contract.View mView;
    private BaseFragment mFragment;
    private Context mContext;

    public ShopCreateOrderStep3Presenter(ShopCreateOrderStep3Contract.View view, BaseFragment fragment) {
        mView = view;
        mFragment = fragment;
        mContext = fragment.getContext();
    }

    @Override
    public void requestCreateInvoice(Invoice newInvoice) {
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.CreateInvoice.PARAM_NAME,
                newInvoice.getName());
        params.put(APIDefinition.CreateInvoice.PARAM_ADDRESS_START,
                newInvoice.getAddressStart());
        params.put(APIDefinition.CreateInvoice.PARAM_LATITUDE_START,
                String.valueOf(newInvoice.getLatStart()));
        params.put(APIDefinition.CreateInvoice.PARAM_LONGITUDE_START,
                String.valueOf(newInvoice.getLngStart()));
        params.put(APIDefinition.CreateInvoice.PARAM_ADDRESS_FINISH,
                newInvoice.getAddressFinish());
        params.put(APIDefinition.CreateInvoice.PARAM_LATITUDE_FINISH,
                String.valueOf(newInvoice.getLatFinish()));
        params.put(APIDefinition.CreateInvoice.PARAM_LONGITUDE_FINISH,
                String.valueOf(newInvoice.getLngFinish()));
        params.put(APIDefinition.CreateInvoice.PARAM_DELIVERY_TIME,
                newInvoice.getDeliveryTime());
        params.put(APIDefinition.CreateInvoice.PARAM_DISTANCE,
                String.valueOf(newInvoice.getDistance()));
        params.put(APIDefinition.CreateInvoice.PARAM_DESCRIPTION,
                newInvoice.getDescription());
        params.put(APIDefinition.CreateInvoice.PARAM_PRICE,
                String.valueOf(newInvoice.getPrice()));
        params.put(APIDefinition.CreateInvoice.PARAM_SHIPPING_PRICE,
                String.valueOf(newInvoice.getShippingPrice()));
        params.put(APIDefinition.CreateInvoice.PARAM_STATUS,
                newInvoice.getStatus());
        params.put(APIDefinition.CreateInvoice.PARAM_WEIGHT,
                String.valueOf(newInvoice.getWeight()));
        params.put(APIDefinition.CreateInvoice.PARAM_CUSTOMER_NAME,
                newInvoice.getCustomerName());
        params.put(APIDefinition.CreateInvoice.PARAM_CUSTOMER_NUMBER,
                newInvoice.getCustomerNumber());

        mFragment.showDialog();
        API.createInvoice(Config.getInstance().getUserInfo(mContext).getAuthenticationToken(),
                params,
                new API.APICallback<APIResponse<CreateInVoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<CreateInVoiceData> response) {
                        mFragment.dismissDialog();
                        // TODO: Go to invoice manager
                        mFragment.showUserMessage(response.getMessage());
                        mFragment.getActivity().finish();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mFragment.dismissDialog();
                        mFragment.showUserMessage(message);
                    }
                });
    }

    @Override
    public void startRouteActivity() {
        mFragment.startActivity(new Intent(mContext, RouteActivity.class));
    }
}
