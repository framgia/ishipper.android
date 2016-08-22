package com.framgia.ishipper.ui.activity;
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
import com.framgia.ishipper.model.Shipper;
import com.framgia.ishipper.ui.adapter.ShipperRegAdapter;
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

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ShipperRegAdapter mShipperRegAdapter;
    private List<Shipper> mShipperList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_shipper_reg);
        ButterKnife.bind(this);
        initData();
        initEvent();
    }

    private void initData() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mShipperList = new ArrayList<>();
        mShipperRegAdapter = new ShipperRegAdapter(this, mShipperList);
        mShipperRegAdapter.setListener(this);
        mShipperRegAdapter.setAcceptShipperListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mShipperRegAdapter);
        fakeListShipper();
    }

    private void initEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void fakeListShipper() {

        mShipperList.addAll(Shipper.getSampleListShipper(this));
        mShipperRegAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClickShipperRegListener(Shipper shipper) {
        //TODO : Go to detail Shipper
        Log.d("clicked", shipper.getClass().getName());
    }

    @Override
    public void onClickAcceptShipperListener(Shipper shipper) {
        //TODO: set accept shipper
        Toast.makeText(this, "Đã chọn shipper thành công", Toast.LENGTH_SHORT).show();
        finish();

    }
}
