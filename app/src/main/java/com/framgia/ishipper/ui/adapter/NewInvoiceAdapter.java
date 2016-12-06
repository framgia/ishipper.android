package com.framgia.ishipper.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Invoice;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HungNT on 10/21/16.
 */

public class NewInvoiceAdapter extends RecyclerView.Adapter<NewInvoiceAdapter.ViewHolder> {

    private List<Invoice> mInvoiceList;
    private OnItemClickListener mCallback;

    public NewInvoiceAdapter(List<Invoice> invoiceList, OnItemClickListener callback) {
        mInvoiceList = invoiceList;
        mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new NewInvoiceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mInvoiceList.get(position));
    }

    @Override
    public int getItemCount() {
        return mInvoiceList == null ? 0 : mInvoiceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_item_order_to) TextView mOrderEndAddress;
        @BindView(R.id.tv_item_order_from) TextView mOrderStartAddress;
        @BindView(R.id.tv_item_order_price) TextView mOrderPrePay;
        @BindView(R.id.tv_item_order_ship_price) TextView mOrderShippingCost;
        @BindView(R.id.tv_item_order_ship_time) TextView mOrderShippingTime;
        @BindView(R.id.btn_item_order_register_order) View mBtnReceiveInvoice;
        @BindView(R.id.tv_item_order_distance) TextView mTvDistance;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mBtnReceiveInvoice.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onInvoiceItemClick(mInvoiceList.get(getAdapterPosition()));
                }
            });
        }

        @OnClick(R.id.btn_item_order_register_order)
        void receiveInvoice() {
            mCallback.onInvoiceReceiveItemClick(mInvoiceList.get(getAdapterPosition()));
        }

        void bindData(Invoice invoice) {
            mOrderEndAddress.setText(invoice.getAddressFinish());
            mOrderStartAddress.setText(invoice.getAddressStart());
            mOrderPrePay.setText(String.valueOf(invoice.getPrice()));
            mOrderShippingCost.setText(String.valueOf(invoice.getShippingPrice()));
            mOrderShippingTime.setText(invoice.getDeliveryTime());
            mTvDistance.setText(String.valueOf(invoice.getDistance()));
        }
    }

    public interface OnItemClickListener {
        void onInvoiceReceiveItemClick(Invoice invoice);
        void onInvoiceItemClick(Invoice invoice);
    }
}
