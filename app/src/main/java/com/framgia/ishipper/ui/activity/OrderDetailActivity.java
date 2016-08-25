package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ShowInvoiceData;
import com.framgia.ishipper.util.TextFormatUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String KEY_INVOICE_ID = "invoice id";
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.tv_detail_distance) TextView tvDetailDistance;
    @BindView(R.id.tv_detail_start) TextView tvDetailStart;
    @BindView(R.id.tv_detail_end) TextView tvDetailEnd;
    @BindView(R.id.tv_detail_suggest) TextView tvDetailSuggest;
    @BindView(R.id.tv_detail_order_name) TextView tvDetailOrderName;
    @BindView(R.id.tv_detail_ship_price) TextView tvDetailShipPrice;
    @BindView(R.id.tv_detail_order_price) TextView tvDetailOrderPrice;
    @BindView(R.id.tv_detail_ship_time) TextView tvDetailShipTime;
    @BindView(R.id.tv_detail_note) TextView tvDetailNote;
    @BindView(R.id.tv_detail_shop_name) TextView tvDetailShopName;
    @BindView(R.id.tv_detail_shop_phone) TextView tvDetailShopPhone;
    @BindView(R.id.tv_detail_shipper_name) TextView tvDetailShipperName;
    @BindView(R.id.tv_detail_shipper_phone) TextView tvDetailShipperPhone;
    @BindView(R.id.cardview_detail_shop_infor) CardView mCardviewDetailShopInfor;
    @BindView(R.id.cardview_detail_shipper_infor) CardView mCardviewDetailShipperInfor;
    @BindView(R.id.tv_detail_customer_name) TextView mDetailCustomerName;
    @BindView(R.id.tv_detail_customer_phone) TextView mDetailCustomerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_detail);
        ButterKnife.bind(this);
        initData();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        API.getInvoiceDetail(
                 Config.getInstance().getUserInfo(this).getRole(),
                 String.valueOf(bundle.getInt(KEY_INVOICE_ID)),
                 Config.getInstance().getUserInfo(this).getAuthenticationToken(),
                 new API.APICallback<APIResponse<ShowInvoiceData>>() {
                     @Override
                     public void onResponse(APIResponse<ShowInvoiceData> response) {
                         Invoice invoice = response.getData().invoice;
                         User user = response.getData().user;
                         if (invoice != null) {
                             if (user != null && user.getRole().equals(User.ROLE_SHIPPER)) {
                                 mCardviewDetailShopInfor.setVisibility(View.VISIBLE);
                                 mCardviewDetailShipperInfor.setVisibility(View.GONE);
                                 tvDetailShipperName.setText(user.getName());
                                 tvDetailShipperPhone.setText(user.getPhoneNumber());
                             } else {
                                 mCardviewDetailShopInfor.setVisibility(View.GONE);
                                 tvDetailShopName.setText(user.getName());
                                 tvDetailShopPhone.setText(user.getPhoneNumber());
                                 if (invoice.getStatus().equals(Invoice.STATUS_INIT)) {
                                     mCardviewDetailShipperInfor.setVisibility(View.GONE);
                                 } else {
                                     mCardviewDetailShipperInfor.setVisibility(View.VISIBLE);
                                 }
                             }
                             tvDetailDistance.setText(TextFormatUtils.formatDistance(invoice.getDistance()));
                             tvDetailStart.setText(invoice.getAddressStart());
                             tvDetailEnd.setText(invoice.getAddressFinish());
                             tvDetailOrderName.setText(invoice.getName());
                             tvDetailOrderPrice.setText(TextFormatUtils.formatPrice(invoice.getPrice()));
                             tvDetailShipPrice.setText(TextFormatUtils.formatPrice(invoice.getShippingPrice()));
                             tvDetailShipTime.setText(invoice.getDeliveryTime());
                             tvDetailNote.setText(invoice.getDescription());

                         }
                     }

                     @Override
                     public void onFailure(int code, String message) {
                         Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                     }
                 });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    @OnClick({R.id.btn_detail_show_path, R.id.btn_detail_shop_call, R.id.btn_detail_receive_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_detail_show_path:
                startActivity(new Intent(OrderDetailActivity.this, RouteActivity.class));
                break;
            case R.id.btn_detail_shop_call:
                break;
            case R.id.btn_detail_receive_order:
                break;
        }
    }
}
