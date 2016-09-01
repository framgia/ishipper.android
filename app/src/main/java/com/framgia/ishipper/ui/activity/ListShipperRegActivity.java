package com.framgia.ishipper.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.ListShipperData;
import com.framgia.ishipper.ui.adapter.ShipperRegAdapter;
import com.framgia.ishipper.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vuduychuong1994 on 7/20/16.
 */
public class ListShipperRegActivity extends AppCompatActivity implements
        ShipperRegAdapter.OnItemClickShipperRegListener,
        ShipperRegAdapter.OnClickAcceptShipperListener {

    public static final String KEY_INVOICE_ID = "KEY_INVOICE_ID";
    public static final int REQUEST_CODE_RESULT = 888;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ShipperRegAdapter mShipperRegAdapter;
    private String mInvoiceId;
    private List<User> mShipperList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_shipper_reg);
        ButterKnife.bind(this);
        initData();
        initEvent();
    }

    private void initData() {
        mInvoiceId = String.valueOf(getIntent().getIntExtra(KEY_INVOICE_ID, 0));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
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

        API.getListShipperReceived(Config.getInstance().getUserInfo(getApplicationContext()).getAuthenticationToken(),
                mInvoiceId, new API.APICallback<APIResponse<ListShipperData>>() {
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
                        Toast.makeText(ListShipperRegActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClickShipperRegListener(User shipper) {
        //TODO : Go to detail Shipper
        Log.d("clicked", shipper.getClass().getName());
    }

    @Override
    public void onClickAcceptShipperListener(User shipper) {
        final Dialog dialog = CommonUtils.showLoadingDialog(this);
        API.putShopReceiveShipper(Config.getInstance().getUserInfo(getApplicationContext()).getAuthenticationToken(),
                shipper.getUserInvoiceId(), new API.APICallback<APIResponse<EmptyData>>() {
                    @Override
                    public void onResponse(APIResponse<EmptyData> response) {
                        dialog.dismiss();
                        Toast.makeText(ListShipperRegActivity.this, R.string.accept_shipper_success, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        dialog.dismiss();
                        Toast.makeText(ListShipperRegActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });


    }
}
