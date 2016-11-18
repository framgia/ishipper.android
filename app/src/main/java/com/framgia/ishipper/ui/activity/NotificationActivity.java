package com.framgia.ishipper.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListNotificationData;
import com.framgia.ishipper.ui.adapter.NotificationAdapter;
import com.framgia.ishipper.util.Const;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class NotificationActivity extends ToolbarActivity {

    private static final String TAG = "NotificationActivity";
    @BindView(R.id.rvListNotification) RecyclerView rvListNotification;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_new_notification) TextView mTvNewNotification;
    @BindView(R.id.appbar) AppBarLayout mAppbar;
    private NotificationAdapter mAdapter;
    private List<Notification> mNotificationList;
    private User mCurrentUser;
    private LinearLayoutManager mLayoutManager;
    private int mTotalItemCount, mLastVisibleItem;
    private int mVisibleThreshold = 1;
    private boolean mIsLoading;
    private int mPage;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addNewNotification(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setEvent();
        registerReceiver(mReceiver, new IntentFilter(Const.ACTION_NEW_NOTIFICATION));
    }

    private void setEvent() {
        rvListNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager =
                        ((LinearLayoutManager) rvListNotification.getLayoutManager());
                if (layoutManager.findFirstVisibleItemPosition() == Const.ZERO &&
                    mTvNewNotification.getVisibility() == View.VISIBLE) {
                    mTvNewNotification.setVisibility(View.GONE);
                }
                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                Log.d(TAG, "last: " + mLastVisibleItem);
                Log.d(TAG, "total: " + mTotalItemCount);
                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                    loadListNotification(++mPage);
                    mIsLoading = true;
                }
            }
        });
    }

    private void initView() {
        mCurrentUser = Config.getInstance().getUserInfo(this);
        if (mCurrentUser == null) return;
        mLayoutManager =
                new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        rvListNotification.setLayoutManager(mLayoutManager);
        mNotificationList = new ArrayList<>();
        mAdapter = new NotificationAdapter(getBaseContext(),
                                           mNotificationList);
        rvListNotification.setAdapter(mAdapter);
        loadListNotification(++mPage);
    }

    private void loadListNotification(int page) {
        mNotificationList.add(null);
        mAdapter.notifyItemInserted(mNotificationList.size() - 1);
        API.getAllNotification(mCurrentUser.getAuthenticationToken(), mCurrentUser.getRole(),
                               page, Const.Setting.PER_PAGE,
                               new API.APICallback<APIResponse<ListNotificationData>>() {
                                   @Override
                                   public void onResponse(
                                           APIResponse<ListNotificationData> response) {
                                       mNotificationList.remove(mNotificationList.size() - 1);
                                       mAdapter.notifyItemRemoved(mNotificationList.size());
                                       mNotificationList.addAll(
                                               response.getData().getNotifications());
                                       mAdapter.notifyDataSetChanged();
                                       mIsLoading = false;
                                   }

                                   @Override
                                   public void onFailure(int code, String message) {
                                       Toast.makeText(getBaseContext(), message,
                                                      Toast.LENGTH_SHORT).show();
                                   }
                               }

        );
    }

    private void addNewNotification(Intent intent) {
        if (intent == null) return;
        Notification notification = new Notification();
        notification.setContent(intent.getStringExtra(Const.KEY_BODY));
        notification.setTimePost(intent.getStringExtra(Const.KEY_TITLE));
        mNotificationList.add(Const.ZERO, notification);
        mAdapter.notifyItemInserted(Const.ZERO);
        LinearLayoutManager layoutManager =
                ((LinearLayoutManager) rvListNotification.getLayoutManager());
        if (layoutManager.findFirstVisibleItemPosition() == Const.ZERO) {
            layoutManager.scrollToPosition(Const.ZERO);
        } else {
            mTvNewNotification.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.title_activity_notification;
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_notification;
    }

    @OnClick(R.id.tv_new_notification)
    public void onClick(View view) {
        LinearLayoutManager layoutManager =
                ((LinearLayoutManager) rvListNotification.getLayoutManager());
        layoutManager.scrollToPosition(Const.ZERO);
        view.setVisibility(View.GONE);
    }
}
