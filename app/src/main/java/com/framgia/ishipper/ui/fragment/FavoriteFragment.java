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
import com.framgia.ishipper.net.data.AddFavoriteListData;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.ListUserData;
import com.framgia.ishipper.ui.activity.SearchUserActivity;
import com.framgia.ishipper.ui.adapter.FavoriteListAdapter;
import com.framgia.ishipper.ui.view.ConfirmDialog;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private Unbinder mUnbinder;
    private Context mContext;
    private FavoriteListAdapter mFavoriteListAdapter;
    private List<User> mFavoriteList = new ArrayList<>();
    private User mCurrentUser;

    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
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
        return view;
    }

    private void invalidView(View view) {
        mContext = view.getContext();
        mCurrentUser = Config.getInstance().getUserInfo(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        setUpRecycleView(mRecyclerView);

    }

    private void setUpRecycleView(RecyclerView recyclerView) {
        mFavoriteListAdapter = new FavoriteListAdapter(mContext, mFavoriteList);
        recyclerView.setAdapter(mFavoriteListAdapter);
        if (mCurrentUser.getRole().equals(User.ROLE_SHOP)) {
            getFavoriteListShipper();
        } else {
            getFavoriteListShop();
        }
    }

    private void getFavoriteListShop() {
        API.getFavoriteListShop(mCurrentUser.getAuthenticationToken(),
                new API.APICallback<APIResponse<ListUserData>>() {
                    @Override
                    public void onResponse(APIResponse<ListUserData> response) {
                        Log.d(TAG, response.getMessage());
                        mFavoriteList.clear();
                        mFavoriteList.addAll(response.getData().getShippersList());
                        mFavoriteListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFavoriteListShipper() {
        API.getFavoriteListShipper(mCurrentUser.getAuthenticationToken(),
                new API.APICallback<APIResponse<ListUserData>>() {
                    @Override
                    public void onResponse(APIResponse<ListUserData> response) {
                        Log.d(TAG, response.getMessage());
                        mFavoriteList.clear();
                        mFavoriteList.addAll(response.getData().getShippersList());
                        mFavoriteListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_user_manage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_favorite_add) {
            startActivityForResult(
                    SearchUserActivity.startIntent(mContext, Const.RequestCode.REQUEST_SEARCH_FAVORITE),
                    Const.RequestCode.REQUEST_SEARCH_FAVORITE
            );
            return true;
        } else if (item.getItemId() == R.id.menu_delete_all) {
            new ConfirmDialog(mContext)
                    .setIcon(R.drawable.ic_delete_white_24dp)
                    .setMessage(getString(R.string.delete_all_dialog_message))
                    .setTitle(getString(R.string.delete_all_dialog_title))
                    .setButtonCallback(new ConfirmDialog.ConfirmDialogCallback() {
                        @Override
                        public void onPositiveButtonClick(final ConfirmDialog confirmDialog) {
                            API.deleteAllFavoriteList(
                                    mCurrentUser.getRole(),
                                    mCurrentUser.getAuthenticationToken(),
                                    new API.APICallback<APIResponse<EmptyData>>() {
                                        @Override
                                        public void onResponse(APIResponse<EmptyData> response) {
                                            mFavoriteList.clear();
                                            mFavoriteListAdapter.notifyDataSetChanged();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.RequestCode.REQUEST_SEARCH_FAVORITE) {
            if (resultCode == Activity.RESULT_OK) {
                final User user = data.getParcelableExtra(Const.KEY_USER);
                if (user != null) {
                    final Dialog loading = CommonUtils.showLoadingDialog(getContext());
                    API.addFavoriteUser(mCurrentUser.getUserType(),
                            mCurrentUser.getAuthenticationToken(),
                            user.getId(),
                            new API.APICallback<APIResponse<AddFavoriteListData>>() {
                                @Override
                                public void onResponse(APIResponse<AddFavoriteListData> response) {
                                    loading.dismiss();
                                    user.setFavoriteListId(
                                            String.valueOf(response.getData()
                                                    .getResponse().getFavoritelistId()));
                                    mFavoriteListAdapter.add(user);
//                                    mFavoriteList.add(0, user);
//                                    mFavoriteListAdapter.notifyItemInserted(0);
                                }

                                @Override
                                public void onFailure(int code, String message) {
                                    loading.dismiss();
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }
    }
}
