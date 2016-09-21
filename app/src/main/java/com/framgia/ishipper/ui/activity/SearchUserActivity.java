package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListUserData;
import com.framgia.ishipper.ui.adapter.SearchUserAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchUserActivity extends AppCompatActivity
        implements API.APICallback<APIResponse<ListUserData>> {
    @BindView(R.id.edtSearch) EditText mEdtSearch;
    @BindView(R.id.imgSearch) ImageView mImgSearch;
    @BindView(R.id.layoutSearch) RelativeLayout mLayoutSearch;
    @BindView(R.id.rvResult) RecyclerView mRvResult;

    private SearchUserAdapter mAdapter;
    private List<User> mListUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        ButterKnife.bind(this);

        mAdapter = new SearchUserAdapter(this, mListUsers);
        mRvResult.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRvResult.setAdapter(mAdapter);

        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEdtSearch.getText().toString().equals("")) {
                    mImgSearch.setVisibility(View.VISIBLE);
                } else {
                    mImgSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mListUsers.clear();
                mAdapter.notifyDataSetChanged();

                if (editable.length() == 0) return;

                API.getSearchUser(
                        Config.getInstance().getUserInfo(getBaseContext()).getAuthenticationToken(),
                        editable.toString(), SearchUserActivity.this);
            }
        });

    }

    @Override
    public void onResponse(APIResponse<ListUserData> response) {
        mListUsers.addAll(response.getData().getShippersList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(int code, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
