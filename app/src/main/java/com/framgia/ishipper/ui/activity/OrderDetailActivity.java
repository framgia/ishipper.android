package com.framgia.ishipper.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.framgia.ishipper.ui.fragment.UserInfoDialogFragment;
import com.framgia.ishipper.ui.view.CancelDialog;
import com.framgia.ishipper.ui.view.ReviewDialog;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.TextFormatUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailActivity extends ToolbarActivity {

    public static final String KEY_INVOICE_ID = "invoice_id";
    public static final int REQUEST_INVOICE_ID = 1;
    @BindView(R.id.toolbar) Toolbar mToolbar;
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
    @BindView(R.id.btn_detail_cancel_order) View mBtnDetailCancelOrder;
    @BindView(R.id.btn_report_user) View mBtnReportUser;
    @BindView(R.id.btn_finished_order) Button mBtnFinishedOrder;
    @BindView(R.id.btn_take_order) Button mBtnTakeOrder;
    @BindView(R.id.tv_shipping_order_status) TextView tvOrderStatus;

    private User mCurrentUser;
    private User mInvoiceUser;
    private Invoice mInvoice;
    private int mInvoiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_detail);
        ButterKnife.bind(this);
        mCurrentUser = Config.getInstance().getUserInfo(this);
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        if (CommonUtils.isOpenFromNoti(this)) {
            // Explicit Intent
            mInvoiceId = Integer.valueOf(bundle.getString(Const.FirebaseData.INVOICE_ID));
            String notiId = getIntent().getExtras().getString(Const.FirebaseData.NOTI_ID);
            API.updateNotification(mCurrentUser.getUserType(), notiId,
                                   mCurrentUser.getAuthenticationToken(), true,
                                   new API.APICallback<APIResponse<EmptyData>>() {
                                       @Override
                                       public void onResponse(
                                               APIResponse<EmptyData> response) {
                                           //TODO: read notification
                                       }

                                       @Override
                                       public void onFailure(int code, String message) {

                                       }
                                   });
        } else {
            // Implicit Intent
            mInvoiceId = getIntent().getIntExtra(KEY_INVOICE_ID, -1);
        }

        // get Invoice from invoice id
        API.getInvoiceDetail(
                mCurrentUser.getRole(),
                String.valueOf(mInvoiceId),
                mCurrentUser.getAuthenticationToken(),
                new API.APICallback<APIResponse<ShowInvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<ShowInvoiceData> response) {
                        mInvoiceUser = response.getData().mInvoice.getUser();
                        mInvoice = response.getData().mInvoice;
                        if (mInvoice == null) return;
                        invalidateOptionsMenu();
                        setStatus(mInvoice);
                        showAction(mInvoice.getStatusCode());
                        User user = mInvoice.getUser();
                        tvDetailDistance.setText(TextFormatUtils
                                .formatDistance(mInvoice.getDistance()));
                        tvDetailStart.setText(mInvoice.getAddressStart());
                        tvDetailEnd.setText(mInvoice.getAddressFinish());
                        tvDetailOrderName.setText(mInvoice.getName());
                        tvDetailOrderPrice.setText(TextFormatUtils
                                .formatPrice(mInvoice.getPrice()));
                        tvDetailShipPrice.setText(TextFormatUtils
                                .formatPrice(mInvoice.getShippingPrice()));
                        tvDetailShipTime.setText(mInvoice.getDeliveryTime());
                        tvDetailNote.setText(mInvoice.getDescription());
                        if (user == null) return;
                        if (mCurrentUser.getRole().equals(User.ROLE_SHIPPER)) {
                            mCardviewDetailShopInfor.setVisibility(View.VISIBLE);
                            mCardviewDetailShipperInfor.setVisibility(View.GONE);
                            tvDetailShopName.setText(user.getName());
                            tvDetailShopPhone.setText(user.getPhoneNumber());
                        } else {
                            mCardviewDetailShopInfor.setVisibility(View.GONE);
                            if (mInvoice.getStatus().equals(
                                    Invoice.STATUS_INIT)) {
                                mCardviewDetailShipperInfor.setVisibility(View.GONE);
                            } else {
                                mCardviewDetailShipperInfor.setVisibility(View.VISIBLE);
                            }
                            tvDetailShipperName.setText(user.getName());
                            tvDetailShipperPhone.setText(user.getPhoneNumber());
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setStatus(Invoice invoice) {
        String textStatus;
        int status = invoice.getStatusCode();
        String action = "";
        Drawable drawableStatus;
        int statusColor;
        switch (status) {
            case Invoice.STATUS_CODE_INIT:
                if (MainActivity.userType == MainActivity.SHIPPER) {
                    textStatus = getString(R.string.order_status_wait);
                } else {
                    textStatus = getString(R.string.order_shop_status_wait);
                }
                drawableStatus = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_status_waiting,
                        null
                );
                statusColor = getResources().getColor(R.color.color_status_waiting);
                break;
            case Invoice.STATUS_CODE_WAITING:
                if (MainActivity.userType == MainActivity.SHIPPER) {
                    textStatus = getString(R.string.order_status_take);
                } else {
                    textStatus = getString(R.string.order_shop_status_take);
                }
                drawableStatus = ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.ic_status_pick,
                        null
                );
                statusColor = getResources().getColor(R.color.color_status_pick);
                break;
            case Invoice.STATUS_CODE_SHIPPING:
                textStatus = getString(R.string.order_status_shipping);
                drawableStatus = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_status_delivering,
                        null
                );
                statusColor = getResources().getColor(R.color.color_status_shipping);
                break;
            case Invoice.STATUS_CODE_SHIPPED:
                textStatus = getString(R.string.order_status_delivered);
                drawableStatus = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_status_delivered,
                        null
                );
                statusColor = getResources().getColor(R.color.color_status_delivered);
                break;
            case Invoice.STATUS_CODE_FINISHED:
                textStatus = getString(R.string.order_status_finished);
                drawableStatus = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_status_finish,
                        null
                );
                statusColor = getResources().getColor(R.color.color_status_finish);
                break;
            case Invoice.STATUS_CODE_CANCEL:
                textStatus = getString(R.string.order_status_cancelled);
                drawableStatus = ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.ic_cancel,
                        null
                );
                statusColor = getResources().getColor(R.color.color_status_cancelled);
                break;
            default:
                textStatus = "";
                drawableStatus = ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.ic_status_waiting,
                        null
                );
                statusColor = getResources().getColor(R.color.colorAccent);
                break;
        }
        tvOrderStatus.setText(textStatus);
        tvOrderStatus.setTextColor(statusColor);
        tvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(drawableStatus,
                null, null, null);
    }

    private void showAction(int statusCode) {
        mBtnDetailCancelOrder.setVisibility(View.VISIBLE);
        if (mCurrentUser.getRole().equals(User.ROLE_SHIPPER)) {
            switch (statusCode) {
                case Invoice.STATUS_CODE_INIT:
                    if (!mInvoice.isReceived()) {
                        mBtnDetailReceiveOrder.setVisibility(View.VISIBLE);
                        mBtnDetailCancelOrder.setVisibility(View.GONE);
                    }
                    mBtnDetailCancelOrder.setVisibility(View.GONE);
                    break;
                case Invoice.STATUS_CODE_WAITING:
                    mBtnTakeOrder.setVisibility(View.VISIBLE);
                    break;
                case Invoice.STATUS_CODE_SHIPPING:
                    mBtnFinishedOrder.setVisibility(View.VISIBLE);
                    break;
                case Invoice.STATUS_CODE_SHIPPED:
                    mBtnDetailCancelOrder.setVisibility(View.GONE);
                    break;
                case Invoice.STATUS_CODE_FINISHED:
                    mBtnDetailCancelOrder.setVisibility(View.GONE);
                    break;
                case Invoice.STATUS_CODE_CANCEL:
//                    mBtnReportUser.setVisibility(View.VISIBLE);
                    mBtnDetailCancelOrder.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            switch (statusCode) {
                case Invoice.STATUS_CODE_SHIPPED:
                    mBtnFinishedOrder.setVisibility(View.VISIBLE);
                    break;
                case Invoice.STATUS_CODE_FINISHED:
                    mBtnDetailCancelOrder.setVisibility(View.GONE);
                    break;
                case Invoice.STATUS_CODE_CANCEL:
//                    mBtnReportUser.setVisibility(View.VISIBLE);
                    mBtnDetailCancelOrder.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.title_activity_order_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_invoice_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mInvoice == null) return super.onPrepareOptionsMenu(menu);
        if (mInvoice.getStatusCode() == Invoice.STATUS_CODE_SHIPPED ||
                mInvoice.getStatusCode() == Invoice.STATUS_CODE_FINISHED) {
            return super.onPrepareOptionsMenu(menu);
        }
        MenuItem menuItem = menu.findItem(R.id.menu_rating);
        if (menuItem == null) return super.onPrepareOptionsMenu(menu);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_rating:
                new ReviewDialog(this, mInvoice.getStringId()).show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (CommonUtils.isOpenFromNoti(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @OnClick({
            R.id.btn_detail_show_shipper,
            R.id.btn_detail_show_shop,
            R.id.btn_detail_show_path,
            R.id.btn_detail_shop_call,
            R.id.btn_detail_receive_order,
            R.id.btn_detail_cancel_order,
            R.id.btn_detail_cancel_register_order,
            R.id.btn_report_user,
            R.id.btn_finished_order,
            R.id.btn_take_order
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_detail_show_shop:
            case R.id.btn_detail_show_shipper:
                UserInfoDialogFragment dialogFragment = UserInfoDialogFragment.newInstance(mInvoiceUser);
                dialogFragment.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.btn_detail_show_path:
                startActivity(new Intent(OrderDetailActivity.this, RouteActivity.class));
                break;
            case R.id.btn_detail_shop_call:
                break;
            case R.id.btn_detail_receive_order:
                showReceiveDialog();
                break;
            case R.id.btn_detail_cancel_order:
                showReportDialog();
                break;
            case R.id.btn_detail_cancel_register_order:
                // TODO: 25/08/2016 cancel register order
                break;
            case R.id.btn_report_user:
                showReportDialog();
                break;
            case R.id.btn_finished_order:
                onFinishedOrder();
                break;
            case R.id.btn_take_order:
                onTakeOrder();
                break;
        }
    }

    private void onTakeOrder() {
        final Dialog loadingDialog = CommonUtils.showLoadingDialog(OrderDetailActivity.this);
        API.putUpdateInvoiceStatus(User.ROLE_SHIPPER, String.valueOf(mInvoice.getId()),
                mCurrentUser.getAuthenticationToken(),
                Invoice.STATUS_SHIPPING, new API.APICallback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceData> response) {
                        loadingDialog.dismiss();
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT)
                                .show();
                        loadingDialog.dismiss();
                    }
                });
    }

    private void onFinishedOrder() {
        final Dialog loadingDialog = CommonUtils.showLoadingDialog(OrderDetailActivity.this);
        String status;
        if (mCurrentUser.getRole().equals(User.ROLE_SHIPPER)) {
            status = Invoice.STATUS_SHIPPED;
        } else {
            status = Invoice.STATUS_FINISHED;
        }
        API.putUpdateInvoiceStatus(mCurrentUser.getRole(),
                String.valueOf(mInvoice.getId()),
                mCurrentUser.getAuthenticationToken(),
                status, new API.APICallback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceData> response) {
                        Toast.makeText(OrderDetailActivity.this, response.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(OrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
    }

    private void confirmShowReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
        builder.setMessage(R.string.message_report_dialog);
        builder.setPositiveButton(R.string.all_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showReportDialog();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton(R.string.all_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showReportDialog() {
        final CancelDialog cancelDialog = new CancelDialog(OrderDetailActivity.this);
        cancelDialog.setOnReportListener(new CancelDialog.OnReportListener() {
            @Override
            public void onReportListener(final ReviewUser reviewUser) {
                final Dialog loadingDialog = CommonUtils.showLoadingDialog(OrderDetailActivity.this);
                API.putUpdateInvoiceStatus(
                        mCurrentUser.getRole(),
                        mInvoice.getStringId(),
                        mCurrentUser.getAuthenticationToken(),
                        Invoice.STATUS_CANCEL,
                        new API.APICallback<APIResponse<InvoiceData>>() {
                            @Override
                            public void onResponse(APIResponse<InvoiceData> response) {
                                // Report User
                                User user = Config.getInstance().getUserInfo(OrderDetailActivity.this);
                                Map<String, String> params = new HashMap<>();
                                params.put(APIDefinition.ReportUser.PARAM_INVOICE_ID,
                                        String.valueOf(mInvoice.getId()));
                                params.put(APIDefinition.ReportUser.PARAM_REVIEW_TYPE, ReviewUser.TYPE_REPORT);
                                params.put(APIDefinition.ReportUser.PARAM_CONTENT, reviewUser.getContent());
                                API.reportUser(user.getRole(),
                                        user.getAuthenticationToken(),
                                        params,
                                        new API.APICallback<APIResponse<ReportUserData>>() {
                                            @Override
                                            public void onResponse(APIResponse<ReportUserData> response) {
                                                Toast.makeText(OrderDetailActivity.this, response.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                                loadingDialog.dismiss();
                                                Intent intent = new Intent();
                                                intent.putExtra(KEY_INVOICE_ID, mInvoice.getId());
                                                setResult(Activity.RESULT_OK, intent);
                                                onBackPressed();
                                            }

                                            @Override
                                            public void onFailure(int code, String message) {
                                                Toast.makeText(OrderDetailActivity.this, message,
                                                        Toast.LENGTH_SHORT).show();
                                                loadingDialog.dismiss();
                                            }
                                        });
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Toast.makeText(OrderDetailActivity.this, message,
                                        Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        });
            }
        });
        cancelDialog.show();
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
