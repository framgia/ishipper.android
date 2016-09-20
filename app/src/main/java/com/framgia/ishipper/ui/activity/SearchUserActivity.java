package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.framgia.ishipper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchUserActivity extends AppCompatActivity {
    @BindView(R.id.edtSearch) EditText mEdtSearch;
    @BindView(R.id.imgSearch) ImageView mImgSearch;
    @BindView(R.id.layoutSearch) RelativeLayout mLayoutSearch;
    @BindView(R.id.rvResult) RecyclerView mRvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        ButterKnife.bind(this);
    }
}
