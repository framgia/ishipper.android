package com.framgia.ishipper.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListUserData;
import com.framgia.ishipper.ui.adapter.SearchUserAdapter;
import com.framgia.ishipper.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchUserActivity extends BaseActivity
        implements API.APICallback<APIResponse<ListUserData>>,SearchUserAdapter.OnSearchItemCallback {
    private static final String EXTRA_REQUEST_CODE = "EXTRA_REQUEST_CODE";
    private static final String RESULT_USER = "RESULT_USER";
    @BindView(R.id.edtSearch) EditText mEdtSearch;
    @BindView(R.id.imgSearch) ImageView mImgSearch;
    @BindView(R.id.layoutSearch) RelativeLayout mLayoutSearch;
    @BindView(R.id.rvResult) RecyclerView mRvResult;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private SearchUserAdapter mAdapter;
    private List<User> mListUsers = new ArrayList<>();
    private int mRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestCode = getIntent()
                .getIntExtra(EXTRA_REQUEST_CODE, Const.RequestCode.REQUEST_SEARCH_BLACKLIST);

        mAdapter = new SearchUserAdapter(mListUsers, this);
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
        mListUsers.clear();
        mListUsers.addAll(response.getData().getShippersList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(int code, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.activity_search_title;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_user;
    }

    public static Intent startIntent(Context context, int requestCode) {
        Intent intent = new Intent(context, SearchUserActivity.class);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        return intent;
    }

    @Override
    public void onAddButtonClick(User data) {
        Intent intent = getIntent();
        intent.putExtra(Const.KEY_USER, data);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClick(User data) {
        // TODO show user info
    }
}
