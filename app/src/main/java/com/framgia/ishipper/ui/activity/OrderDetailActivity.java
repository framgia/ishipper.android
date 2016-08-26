package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.ReviewUser;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.InvoiceData;
import com.framgia.ishipper.net.data.ReportUserData;
import com.framgia.ishipper.net.data.ShowInvoiceData;
import com.framgia.ishipper.ui.view.ReportDialog;
import com.framgia.ishipper.util.TextFormatUtils;

import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String KEY_INVOICE_ID = "invoice id";
    public static final String KEY_STATUS_CODE = "status code";
    public static final int REQUEST_STATUS_CODE = 300;
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
    @BindView(R.id.btn_detail_receive_order) Button mBtnDetailReceiveOrder;
    @BindView(R.id.btn_detail_cancel_register_order) Button mBtnDetailCancelRegisterOrder;
    @BindView(R.id.btn_detail_cancel_order) Button mBtnDetailCancelOrder;
    @BindView(R.id.btn_report_user) Button mBtnReportUser;
    private User mCurrentUser;
    private Invoice mInvoice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_detail);
        ButterKnife.bind(this);
        initData();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCurrentUser = Config.getInstance().getUserInfo(this);

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
                        mInvoice = response.getData().invoice;
                        if (mInvoice != null) {
                            switch (mInvoice.getStatusCode()) {
                                case Invoice.STATUS_CODE_INIT:
                                    mBtnDetailReceiveOrder.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    mBtnDetailCancelOrder.setVisibility(View.VISIBLE);
                                    break;
                                case Invoice.STATUS_CODE_WAITING:
                                    mBtnReportUser.setVisibility(View.VISIBLE);
                                    break;
                                case Invoice.STATUS_CODE_CANCEL:
                                    mBtnReportUser.setVisibility(View.VISIBLE);
                                    break;

                            }
                            User user = response.getData().user;
                            if (user != null) {
                                if (user.getRole().equals(User.ROLE_SHIPPER)) {
                                    mCardviewDetailShopInfor.setVisibility(View.VISIBLE);
                                    mCardviewDetailShipperInfor.setVisibility(View.GONE);
                                    tvDetailShipperName.setText(user.getName());
                                    tvDetailShipperPhone.setText(user.getPhoneNumber());
                                } else {
                                    mCardviewDetailShopInfor.setVisibility(View.GONE);
                                    tvDetailShopName.setText(user.getName());
                                    tvDetailShopPhone.setText(user.getPhoneNumber());
                                    if (mInvoice.getStatus().equals(Invoice.STATUS_INIT)) {
                                        mCardviewDetailShipperInfor.setVisibility(View.GONE);
                                    } else {
                                        mCardviewDetailShipperInfor.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                            tvDetailDistance.setText(TextFormatUtils.formatDistance(mInvoice.getDistance()));
                            tvDetailStart.setText(mInvoice.getAddressStart());
                            tvDetailEnd.setText(mInvoice.getAddressFinish());
                            tvDetailOrderName.setText(mInvoice.getName());
                            tvDetailOrderPrice.setText(TextFormatUtils.formatPrice(mInvoice.getPrice()));
                            tvDetailShipPrice.setText(TextFormatUtils.formatPrice(mInvoice.getShippingPrice()));
                            tvDetailShipTime.setText(mInvoice.getDeliveryTime());
                            tvDetailNote.setText(mInvoice.getDescription());
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(KEY_STATUS_CODE, mInvoice.getStatusCode());
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick({
            R.id.btn_detail_show_path,
            R.id.btn_detail_shop_call,
            R.id.btn_detail_receive_order,
            R.id.btn_detail_cancel_order,
            R.id.btn_detail_cancel_register_order,
            R.id.btn_report_user
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_detail_show_path:
                startActivity(new Intent(OrderDetailActivity.this, RouteActivity.class));
                break;
            case R.id.btn_detail_shop_call:
                break;
            case R.id.btn_detail_receive_order:
                showReceiveDialog();
                break;
            case R.id.btn_detail_cancel_order:
                showCancelOrderDialog();
                break;
            case R.id.btn_detail_cancel_register_order:
                // TODO: 25/08/2016 cancel register order
                break;
            case R.id.btn_report_user:
                showReportDialog(mInvoice);
                break;
        }
    }

    private void showReportDialog(final Invoice invoice) {
        final ReportDialog reportDialog = new ReportDialog(OrderDetailActivity.this);
        reportDialog.setListener(new ReportDialog.OnReportListener() {
            @Override
            public void onReportListener(ReviewUser reviewUser) {
                Map<String, String> params = new HashMap<>();
                params.put(APIDefinition.ReportUser.PARAM_INVOICE_ID, String.valueOf(invoice.getId()));
                params.put(APIDefinition.ReportUser.PARAM_REVIEW_TYPE, ReviewUser.TYPE_REPORT);
                params.put(APIDefinition.ReportUser.PARAM_CONTENT, reviewUser.getContent());
                API.reportUser(User.ROLE_SHIPPER,
                               Config.getInstance().getUserInfo(OrderDetailActivity.this)
                                       .getAuthenticationToken(),
                               params,
                               new API.APICallback<APIResponse<ReportUserData>>() {
                                   @Override
                                   public void onResponse(APIResponse<ReportUserData> response) {
                                       Toast.makeText(OrderDetailActivity.this, response.getMessage(),
                                                      Toast.LENGTH_SHORT).show();
                                       initData();
                                   }

                                   @Override
                                   public void onFailure(int code, String message) {
                                       Toast.makeText(OrderDetailActivity.this, message,
                                                      Toast.LENGTH_SHORT).show();
                                   }
                               });
                reportDialog.cancel();
            }
        });
        reportDialog.show();
    }
    private void showCancelOrderDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_nearby_receive_order, null);
        ((TextView) view.findViewById(R.id.confirm_dialog_title)).setText(R.string.dialog_cancel_order_title);
        ((TextView) view.findViewById(R.id.confirm_dialog_message)).setText(R.string.dialog_cancel_order_message);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).show();
        view.findViewById(R.id.confirm_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                API.putUpdateInvoiceStatus(
                        mCurrentUser.getRole(),
                        mInvoice.getStringId(),
                        mCurrentUser.getAuthenticationToken(),
                        Invoice.STATUS_CANCEL,
                        new API.APICallback<APIResponse<InvoiceData>>() {
                            @Override
                            public void onResponse(APIResponse<InvoiceData> response) {
                                finish();
                                dialog.cancel();
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
        view.findViewById(R.id.confirm_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private void showReceiveDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_nearby_receive_order, null);
        dialog.setView(view);
        view.findViewById(R.id.confirm_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API.postShipperReceiveInvoice(
                        Config.getInstance().getUserInfo(getBaseContext()).getAuthenticationToken(),
                        mInvoice.getStringId(),
                        new API.APICallback<APIResponse<EmptyData>>() {
                            @Override
                            public void onResponse(APIResponse<EmptyData> response) {
                                dialog.dismiss();
                                Toast.makeText(getBaseContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
        view.findViewById(R.id.confirm_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
