package com.framgia.ishipper.presentation.invoice.detail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.ui.activity.MainActivity;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.TextFormatUtils;
import com.framgia.ishipper.widget.dialog.ReviewDialog;
import com.framgia.ishipper.widget.dialog.UserInfoDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class InvoiceDetailActivity extends BaseToolbarActivity implements InvoiceDetailContact.View {

    public static final int REQUEST_INVOICE_ID = 1;

    @BindView(R.id.toolbar) Toolbar mToolbar;
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
    @BindView(R.id.tv_shipping_order_status) TextView mTvOrderStatus;

    private User mCurrentUser;
    private User mInvoiceUser;
    private Invoice mInvoice;
    private InvoiceDetailPresenter mPresenter;

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.title_activity_order_detail;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_oder_detail;
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
            mPresenter.startMainActivity();
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
                mPresenter.showRouteActivity(mInvoice);
                break;
            case R.id.btn_detail_shop_call:
                break;
            case R.id.btn_detail_receive_order:
                mPresenter.receiveInvoice(mInvoice.getStringId());
                break;
            case R.id.btn_detail_cancel_order:
                if (mInvoice.getStatusCode() == Invoice.STATUS_CODE_INIT) {
                    mPresenter.cancelInvoice(mInvoice.getId());
                } else {
                    mPresenter.report(mInvoice);
                }
                break;
            case R.id.btn_detail_cancel_register_order:
                // TODO: 25/08/2016 cancel register order
                break;
            case R.id.btn_report_user:
                mPresenter.report(mInvoice);
                break;
            case R.id.btn_finished_order:
                mPresenter.finishedInvoice(mInvoice.getStringId());
                break;
            case R.id.btn_take_order:
                mPresenter.takeInvoice(mInvoice.getId());
                break;
        }
    }

    @Override
    public void initViews() {
        mPresenter = new InvoiceDetailPresenter(this, this);
        mCurrentUser = Config.getInstance().getUserInfo(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        int invoiceId;
        if (CommonUtils.isOpenFromNoti(this)) {
            // Explicit Intent
            invoiceId = Integer.valueOf(bundle.getString(Const.FirebaseData.INVOICE_ID));
            String notiId = getIntent().getExtras().getString(Const.FirebaseData.NOTI_ID);
            mPresenter.readNotification(notiId);
        } else {
            // Implicit Intent
            invoiceId = getIntent().getIntExtra(InvoiceDetailPresenter.KEY_INVOICE_ID, -1);
        }

        mPresenter.getInvoiceDetail(invoiceId);
    }

    @Override
    public void onGetInvoiceDetailSuccess(Invoice invoice) {
        invalidateOptionsMenu();
        mInvoice = invoice;
        mInvoiceUser = mInvoice.getUser();
    }

    @Override
    public void setInvoiceStatus(Invoice invoice) {
        String textStatus;
        int status = invoice.getStatusCode();
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
        mTvOrderStatus.setText(textStatus);
        mTvOrderStatus.setTextColor(statusColor);
        mTvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(drawableStatus,
                null, null, null);
    }

    @Override
    public void showActionButton(int statusCode) {
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
    public void showInvoiceData(Invoice invoice) {
        mTvDetailDistance.setText(TextFormatUtils.formatDistance(invoice.getDistance()));
        mTvDetailStart.setText(invoice.getAddressStart());
        mTvDetailEnd.setText(invoice.getAddressFinish());
        mTvDetailOrderName.setText(invoice.getName());
        mTvDetailOrderPrice.setText(TextFormatUtils.formatPrice(invoice.getPrice()));
        mTvDetailShipPrice.setText(TextFormatUtils.formatPrice(invoice.getShippingPrice()));
        mTvDetailShipTime.setText(invoice.getDeliveryTime());
        mTvDetailNote.setText(invoice.getDescription());
    }

    @Override
    public void showUserData(User user) {
        if (!user.isShop()) {
            mCardviewDetailShopInfor.setVisibility(View.VISIBLE);
            mCardviewDetailShipperInfor.setVisibility(View.GONE);
            mTvDetailShopName.setText(user.getName());
            mTvDetailShopPhone.setText(user.getPhoneNumber());
        } else {
            mCardviewDetailShopInfor.setVisibility(View.GONE);
            if (mInvoice.getStatus().equals(Invoice.STATUS_INIT)) {
                mCardviewDetailShipperInfor.setVisibility(View.GONE);
            } else {
                mCardviewDetailShipperInfor.setVisibility(View.VISIBLE);
            }
            mTvDetailShipperName.setText(user.getName());
            mTvDetailShipperPhone.setText(user.getPhoneNumber());
        }
    }
}
