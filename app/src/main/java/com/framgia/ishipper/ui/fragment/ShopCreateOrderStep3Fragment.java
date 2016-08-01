package com.framgia.ishipper.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.ishipper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopCreateOrderStep3Fragment extends Fragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.btn_detail_show_path) TextView mBtnDetailShowPath;
    @BindView(R.id.btn_detail_customer_call) TextView mBtnDetailCustomerCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_oder_detail, container, false);
        ButterKnife.bind(this, view);
        mToolbar.setVisibility(View.GONE);
        mBtnDetailShowPath.setVisibility(View.GONE);
        mBtnDetailCustomerCall.setVisibility(View.GONE);
        return view;
    }

}
