package com.framgia.ishipper.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.ui.fragment.OrderListFragment.OnListFragmentInteractionListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderShippingAdapter extends RecyclerView.Adapter<OrderShippingAdapter.ViewHolder> {

    private List<Order> mOrderList;
    private OnListFragmentInteractionListener mListener;

    public OrderShippingAdapter(List<Order> orderList, OnListFragmentInteractionListener listener) {
        mOrderList = orderList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_window, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = mOrderList.get(position);
        holder.mOrderEndAddress.setText(order.getAddress());
        holder.mOrderPrePay.setText(order.getGoodPrice() + " ");
        holder.mOrderShippingCost.setText(order.getShipPrice() + " ");
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_item_order_to) TextView mOrderEndAddress;
        @BindView(R.id.tv_item_order_price) TextView mOrderPrePay;
        @BindView(R.id.tv_item_order_ship_price) TextView mOrderShippingCost;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            ButterKnife.findById(view, R.id.btn_nearby_done_order).setVisibility(View.VISIBLE);
            ButterKnife.findById(view, R.id.action_call_order).setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListFragmentInteraction(mOrderList.get(getAdapterPosition()));
                }
            });
        }

        @OnClick(R.id.btn_nearby_done_order)
        public void doneOrder() {
            int pos = getAdapterPosition();
            mOrderList.remove(pos);
            notifyItemRemoved(pos);
        }
    }


}
