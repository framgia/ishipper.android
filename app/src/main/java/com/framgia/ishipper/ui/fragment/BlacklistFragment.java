package com.framgia.ishipper.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.AddBlacklistData;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.ListUserData;
import com.framgia.ishipper.ui.activity.SearchUserActivity;
import com.framgia.ishipper.ui.adapter.BlackListAdapter;
import com.framgia.ishipper.ui.view.ConfirmDialog;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BlacklistFragment extends Fragment {
    private static final String TAG = "BlacklistFragment";
    private static final int REQUEST_ADD_USER = 1111;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private Unbinder mUnbinder;
    private Context mContext;
    private BlackListAdapter mBlackListAdapter;
    private List<User> mBlackListUser;
    private User mCurrentUser;

    public BlacklistFragment() {
    }

    public static BlacklistFragment newInstance() {
        return new BlacklistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blacklist_shipper, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        invalidView(view);
        getBlackList();
        return view;
    }

    private void invalidView(View view) {
        mContext = view.getContext();
        mCurrentUser = Config.getInstance().getUserInfo(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        setUpRecycleView(mRecyclerView);
    }

    private void setUpRecycleView(RecyclerView recyclerView) {
        mBlackListUser = new ArrayList<>();
        mBlackListAdapter = new BlackListAdapter(mContext, mBlackListUser);
        recyclerView.setAdapter(mBlackListAdapter);
    }

    private void getBlackList() {
        API.getBlackList(
                mCurrentUser.getAuthenticationToken(),
                mCurrentUser.getRole(),
                new API.APICallback<APIResponse<ListUserData>>() {
                    @Override
                    public void onResponse(APIResponse<ListUserData> response) {
                        Log.d(TAG, response.getMessage());
                        mBlackListUser.clear();
                        mBlackListUser.addAll(response.getData().getShippersList());
                        mBlackListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_favorite_add) {
            startActivityForResult(
                    new Intent(mContext, SearchUserActivity.class),
                    Const.RequestCode.REQUEST_SEARCH_BLACKLIST);
            return true;
        } else if (item.getItemId() == R.id.menu_delete_all) {
            new ConfirmDialog(mContext)
                    .setIcon(R.drawable.ic_delete_white_24dp)
                    .setMessage(getString(R.string.delete_all_dialog_message))
                    .setTitle(getString(R.string.delete_all_dialog_title))
                    .setButtonCallback(new ConfirmDialog.ConfirmDialogCallback() {
                        @Override
                        public void onPositiveButtonClick(final ConfirmDialog confirmDialog) {
                            API.deleteAllBlackList(
                                    mCurrentUser.getRole(),
                                    mCurrentUser.getAuthenticationToken(),
                                    new API.APICallback<APIResponse<EmptyData>>() {
                                        @Override
                                        public void onResponse(APIResponse<EmptyData> response) {
                                            mBlackListUser.clear();
                                            mBlackListAdapter.notifyDataSetChanged();
                                            confirmDialog.cancel();
                                            Toast.makeText(
                                                    mContext,
                                                    R.string.toast_delete_all,
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }

                                        @Override
                                        public void onFailure(int code, String message) {
                                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onNegativeButtonClick(ConfirmDialog confirmDialog) {
                            confirmDialog.cancel();
                        }
                    })
                    .show();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.RequestCode.REQUEST_SEARCH_BLACKLIST) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(mContext, R.string.add_action_fail, Toast.LENGTH_SHORT).show();
                return;
            }
            final User user = data.getParcelableExtra(Const.KEY_USER);
            if (user == null) {
                return;
            }
            final Dialog dialog = CommonUtils.showLoadingDialog(mContext);
            dialog.show();
            API.addUserToBlackList(mCurrentUser.getRole(),
                    mCurrentUser.getAuthenticationToken(),
                    user.getId(),
                    new API.APICallback<APIResponse<AddBlacklistData>>() {
                        @Override
                        public void onResponse(
                                APIResponse<AddBlacklistData> response) {
                            Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT).show();
                            user.setBlackListUserId(
                                    String.valueOf(response.getData().getResponse().getBlacklistId()));
                            mBlackListUser.add(0, user);
                            mBlackListAdapter.notifyItemInserted(0);
                            if (dialog.isShowing()) {
                                dialog.cancel();
                            }
                            Toast.makeText(
                                    mContext,
                                    response.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing()) {
                                dialog.cancel();
                            }
                        }
                    });
        }
    }
}
