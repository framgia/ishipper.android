package com.framgia.ishipper.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HungNT on 10/21/16.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        mContext = context;
        mNotificationList = notificationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent,
                                                                 false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mNotificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final View view;
        public Notification notification;
        User currentUser;
        @BindView(R.id.imgAvatar) ImageView mImgAvatar;
        @BindView(R.id.tvContent) TextView mTvContent;
        @BindView(R.id.tvTimePost) TextView mTvTimePost;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view = itemView;
            currentUser = Config.getInstance().getUserInfo(mContext);
        }

        private void bindData(Notification item) {
            mTvContent.setText(item.getContent());
            mTvTimePost.setText(item.getContent());
            notification = item;
        }

        @Override
        public void onClick(View mView) {
            if (mView.getId() == view.getId()) {
                API.updateNotification(currentUser.getUserType(),
                                       String.valueOf(notification.getId()),
                                       currentUser.getAuthenticationToken(), true,
                                       new API.APICallback<APIResponse<EmptyData>>() {
                                           @Override
                                           public void onResponse(
                                                   APIResponse<EmptyData> response) {
                                               //TODO: read notification
                                           }

                                           @Override
                                           public void onFailure(int code, String message) {

                                           }
                                       });
            }
        }
    }
}
