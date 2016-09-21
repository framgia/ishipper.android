package com.framgia.ishipper.ui.adapter;

import android.content.Context;
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

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    private List<User> mUserList;
    private Context mContext;

    public SearchUserAdapter(Context context, List<User> invoiceList) {
        mContext = context;
        mUserList = invoiceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        User item = mUserList.get(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        if (mUserList == null)
            return 0;
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvUserName) TextView tvName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindData(User invoice) {
            tvName.setText(invoice.getName());
        }
    }
}
