package com.framgia.ishipper.ui.adapter;
import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.framgia.ishipper.R;
import com.framgia.ishipper.model.User;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vuduychuong1994 on 9/15/16.
 */
public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUserList;


    public BlackListAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_black_list_shipper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.user = mUserList.get(position);
        holder.mTextName.setText(holder.user.getName());
        holder.mRtbRatingUser.setRating((float) holder.user.getRate());

    }

    @Override
    public int getItemCount() {
        if (mUserList != null) return mUserList.size();

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rtb_rating_user) AppCompatRatingBar mRtbRatingUser;
        @BindView(R.id.text_count_rating) TextView mTextCountRating;
        @BindView(R.id.text_name) TextView mTextName;
        @BindView(R.id.text_distance) TextView mTextDistance;
        @BindView(R.id.text_count_order) TextView mTextCountOrder;
        @BindView(R.id.layout_container) CardView mLayoutContainer;

        User user;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
