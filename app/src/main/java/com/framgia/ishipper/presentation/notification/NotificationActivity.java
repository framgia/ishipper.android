package com.framgia.ishipper.presentation.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListNotificationData;
import com.framgia.ishipper.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NotificationActivity extends BaseToolbarActivity
        implements NotificationContract.View, NotificationAdapter.OnItemClickListener {
    private static final String TAG = "NotificationActivity";

    @BindView(R.id.rvListNotification) RecyclerView rvListNotification;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_new_notification) TextView mTvNewNotification;

    private NotificationAdapter mAdapter;
    private List<Notification> mNotificationList = new ArrayList<>();
    private User mCurrentUser;
    private LinearLayoutManager mLayoutManager;
    private int mVisibleThreshold = 5;
    private boolean mIsLoading;
    private int mPage;
    private NotificationContract.Presenter mPresenter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPresenter != null) {
                addNewNotification(intent);
            }
        }
    };

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

    @Override
    public void initViews() {
        mPresenter = new NotificationPresenter(this, this);
        mCurrentUser = Config.getInstance().getUserInfo(this);
        if (mCurrentUser == null) return;
        mLayoutManager = new LinearLayoutManager(getBaseContext());
        rvListNotification.setLayoutManager(mLayoutManager);
        mAdapter = new NotificationAdapter(getBaseContext(), mNotificationList, this);
        rvListNotification.setAdapter(mAdapter);
        mPresenter.loadMore(mNotificationList, mAdapter, mCurrentUser, ++mPage);
        rvListNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager =
                        ((LinearLayoutManager) rvListNotification.getLayoutManager());
                if (layoutManager.findFirstVisibleItemPosition() == Const.HEAD_LIST) {
                    mTvNewNotification.setVisibility(View.GONE);
                }
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                Log.d(TAG, "totalCount: " + totalItemCount);
                Log.d(TAG, "lastVisible: " + lastVisibleItem);
                if (!mIsLoading && totalItemCount <= (lastVisibleItem + mVisibleThreshold)) {
                    //Load more data for recycle view
                    mIsLoading = true;
                    mPresenter.loadMore(mNotificationList, mAdapter, mCurrentUser, ++mPage);
                }
            }
        });
        registerReceiver(mReceiver, new IntentFilter(Const.ACTION_NEW_NOTIFICATION));
    }

    @Override
    public void updateListNoti(APIResponse<ListNotificationData> response) {
        mNotificationList.remove(mNotificationList.size() - 1);
        mAdapter.notifyItemRemoved(mNotificationList.size());
        if (response.getData().getNotifications().size() > 0) {
            mNotificationList.addAll(response.getData().getNotifications());
            mAdapter.notifyDataSetChanged();
        }
        mIsLoading = false;
    }

    @Override
    public void addNewNotification(Intent intent) {
        if (intent == null) return;
        LinearLayoutManager layoutManager =
                ((LinearLayoutManager) rvListNotification.getLayoutManager());
        if (layoutManager.findFirstVisibleItemPosition() == Const.HEAD_LIST ||
                mNotificationList.size() == Const.ZERO) {
            layoutManager.scrollToPosition(Const.HEAD_LIST);
        } else {
            mTvNewNotification.setVisibility(View.VISIBLE);
        }
        Notification notification = new Notification();
        notification.setContent(intent.getStringExtra(Const.KEY_BODY));
        notification.setTimePost(intent.getStringExtra(Const.KEY_TITLE));
        mNotificationList.add(Const.HEAD_LIST, notification);
        mAdapter.notifyItemInserted(Const.HEAD_LIST);
    }

    @OnClick(R.id.tv_new_notification)
    public void onClick(View view) {
        LinearLayoutManager layoutManager =
                ((LinearLayoutManager) rvListNotification.getLayoutManager());
        layoutManager.scrollToPosition(Const.HEAD_LIST);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onClick(Notification notification) {
        Intent intent = new Intent(notification.getAction());
        Bundle bundle = new Bundle();
        bundle.putString(Const.KEY_INVOICE_ID, notification.getInvoice().getStringId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
