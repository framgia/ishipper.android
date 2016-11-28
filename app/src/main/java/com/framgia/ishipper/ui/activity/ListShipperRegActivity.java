package com.framgia.ishipper.ui.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.ListShipperData;
import com.framgia.ishipper.ui.adapter.ShipperRegAdapter;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by vuduychuong1994 on 7/20/16.
 */
public class ListShipperRegActivity extends ToolbarActivity implements
        ShipperRegAdapter.OnItemClickShipperRegListener,
        ShipperRegAdapter.OnClickAcceptShipperListener {
    private static final String TAG = "ListShipperRegActivity";
    public static final String KEY_INVOICE_ID = "KEY_INVOICE_ID";
    public static final int REQUEST_CODE_RESULT = 888;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private ShipperRegAdapter mShipperRegAdapter;
    private int mInvoiceId;
    private List<User> mShipperList;
    private User mCurrentUser;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addShipper(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initEvent();
        registerReceiver(mReceiver, new IntentFilter(Const.ACTION_NEW_NOTIFICATION));
        Log.d(TAG, FirebaseInstanceId.getInstance().getToken());

    }

    private void initData() {
        mCurrentUser = Config.getInstance().getUserInfo(this);
        if (CommonUtils.isOpenFromNoti(this)) {
            // Explicit Intent
            mInvoiceId = Integer.valueOf(getIntent().getExtras()
                    .getString(Const.FirebaseData.INVOICE_ID));
            String notiId = getIntent().getExtras().getString(Const.FirebaseData.NOTIFICATION_ID);
            API.updateNotification(mCurrentUser.getUserType(), notiId,
                                   mCurrentUser.getAuthenticationToken(), true,
                                   new API.APICallback<APIResponse<EmptyData>>() {
                                       @Override
                                       public void onResponse(
                                               APIResponse<EmptyData> response) {
                                           //TODO: read notificationItem
                                       }

                                       @Override
                                       public void onFailure(int code, String message) {

                                       }
                                   });
        } else {
            // Implicit Intent
            mInvoiceId = getIntent().getIntExtra(KEY_INVOICE_ID, -1);
        }

        mShipperList = new ArrayList<>();
        mShipperRegAdapter = new ShipperRegAdapter(this, mShipperList);
        mShipperRegAdapter.setListener(this);
        mShipperRegAdapter.setAcceptShipperListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mShipperRegAdapter);
        getListShipper();
    }

    private void initEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void getListShipper() {
        API.getListShipperReceived(Config.getInstance().getUserInfo(this).getAuthenticationToken(),
                String.valueOf(mInvoiceId),
                new API.APICallback<APIResponse<ListShipperData>>() {
                    @Override
                    public void onResponse(APIResponse<ListShipperData> response) {
                        // Update the list
                        mShipperList.clear();
                        for (User item : response.getData().getShippersList()) {
                            mShipperList.add(item);
                        }
                        mShipperRegAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(ListShipperRegActivity.this, message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addShipper(Intent intent) {
        if (intent == null) return;
        String userStr = intent.getStringExtra(Const.KEY_USER);
        User user = new Gson().fromJson(userStr, User.class);
        mShipperList.add(user);
        mShipperRegAdapter.notifyDataSetChanged();
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
        super.onBackPressed();
        if (isTaskRoot()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) unregisterReceiver(mReceiver);
    }

    @Override
    public void onItemClickShipperRegListener(User shipper) {
        //TODO : Go to detail Shipper
        Log.d("clicked", shipper.getClass().getName());
    }

    @Override
    public void onClickAcceptShipperListener(User shipper) {
        final Dialog dialog = CommonUtils.showLoadingDialog(this);
        API.putShopReceiveShipper(Config.getInstance().getUserInfo(getApplicationContext())
                .getAuthenticationToken(),
                shipper.getUserInvoiceId(), new API.APICallback<APIResponse<EmptyData>>() {
                    @Override
                    public void onResponse(APIResponse<EmptyData> response) {
                        dialog.dismiss();
                        Toast.makeText(ListShipperRegActivity.this,
                                R.string.accept_shipper_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra(KEY_INVOICE_ID, mInvoiceId);
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        dialog.dismiss();
                        Toast.makeText(ListShipperRegActivity.this, message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.title_activity_list_shipper_reg;
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_list_shipper_reg;
    }
}
