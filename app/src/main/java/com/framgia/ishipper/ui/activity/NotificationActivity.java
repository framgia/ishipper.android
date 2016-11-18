package com.framgia.ishipper.ui.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListNotificationData;
import com.framgia.ishipper.ui.adapter.NotificationAdapter;
import com.framgia.ishipper.util.Const;

import java.util.List;

import butterknife.BindView;

public class NotificationActivity extends BaseActivity {

    private static final String TAG = "NotificationActivity";
    @BindView(R.id.rvListNotification) RecyclerView rvListNotification;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    private NotificationAdapter mAdapter;
    private List<Notification> mNotificationList;
    private User mCurrentUser;
    private LinearLayoutManager mLayoutManager;
    private int mTotalItemCount, mLastVisibleItem;
    private int mVisibleThreshold = 1;
    private boolean mIsLoading;
    private int mPage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = Config.getInstance().getUserInfo(this);
        if (mCurrentUser == null) {
            return;
        }
        mLayoutManager = new LinearLayoutManager(getBaseContext(),
                LinearLayoutManager.VERTICAL, false);
        rvListNotification.setLayoutManager(mLayoutManager);
        API.getAllNotification(
                mCurrentUser.getAuthenticationToken(),
                mCurrentUser.getRole(),
                1,
                Const.Setting.PER_PAGE,
                new API.APICallback<APIResponse<ListNotificationData>>() {
                    @Override
                    public void onResponse(APIResponse<ListNotificationData> response) {
                        mNotificationList = response.getData().getNotifications();
                        mAdapter = new NotificationAdapter(getBaseContext(), mNotificationList);
                        rvListNotification.setAdapter(mAdapter);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        rvListNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                Log.d(TAG, "last: " + mLastVisibleItem);
                Log.d(TAG, "total: " + mTotalItemCount);
                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                    loadMore(++mPage);
                    mIsLoading = true;
                }
            }
        });
    }

    private void loadMore(int page) {
        mNotificationList.add(null);
        mAdapter.notifyItemInserted(mNotificationList.size() - 1);
        API.getAllNotification(
                mCurrentUser.getAuthenticationToken(),
                mCurrentUser.getRole(),
                page,
                Const.Setting.PER_PAGE,
                new API.APICallback<APIResponse<ListNotificationData>>() {
                    @Override
                    public void onResponse(APIResponse<ListNotificationData> response) {
                        mNotificationList.remove(mNotificationList.size()-1);
                        mAdapter.notifyItemRemoved(mNotificationList.size());
                        mNotificationList.addAll(response.getData().getNotifications());
                        mAdapter.notifyDataSetChanged();
                        mIsLoading = false;
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.title_activity_notification;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_notification;
    }

}
