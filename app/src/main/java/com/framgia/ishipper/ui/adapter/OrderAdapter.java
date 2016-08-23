package com.framgia.ishipper.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.ui.activity.MainActivity;
import com.framgia.ishipper.ui.fragment.OrderListFragment.OnListFragmentInteractionListener;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Invoice> mInvoiceList;
    private OnListFragmentInteractionListener mListener;
    private OnClickActionListener mClickActionListener;
    private OnClickCancelListener mClickCancelListener;
    private Context mContext;

    public OrderAdapter(Context context, List<Invoice> invoiceList,
                        OnListFragmentInteractionListener listener) {
        mContext = context;
        mInvoiceList = invoiceList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_window,
                                                                     parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mInvoice = mInvoiceList.get(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mInvoice);
                }
            }
        });
        holder.mBtnCancelItemOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickCancelListener != null) {
                    mClickCancelListener.onClickCancelListener(holder.mInvoice);
                }
            }
        });
        holder.mBtnActionItemOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickActionListener != null) {
                    mClickActionListener.onClickActionListener(holder.mInvoice);
                }
            }
        });
        setStatus(holder);
        holder.mTvItemOrderFrom.setText(holder.mInvoice.getAddressStart());
        holder.mTvItemOrderTo.setText(holder.mInvoice.getAddressFinish());
        holder.mTvItemOrderShipTime.setText(holder.mInvoice.getDeliveryTime());
        holder.mTvItemOrderOrderPrice.setText(formatPrice(holder.mInvoice.getPrice()));
        holder.mTvNearbyShipPrice.setText(formatPrice(holder.mInvoice.getShippingPrice()));
    }

    private String formatPrice(float orderPrice) {
        StringBuilder builder = new StringBuilder();
        int length = String.valueOf(orderPrice).length();

        return String.valueOf(orderPrice);
    }

    private String getTimeFromOrder(Long time) {
        StringBuilder builder = new StringBuilder();
        Calendar currentCal = Calendar.getInstance();
        Calendar expiredCal = Calendar.getInstance();
        expiredCal.setTimeInMillis(time);
        long diffTime = expiredCal.getTimeInMillis() - currentCal.getTimeInMillis();
        return String.valueOf(TimeUnit.MILLISECONDS.toHours(diffTime) + 1) + "H";
    }

    private void setStatus(ViewHolder holder) {
        String textStatus;
        int status = holder.mInvoice.getStatusCode();
        String action = "";
        Drawable drawableStatus;
        int statusColor;
        boolean canCancel;
        switch (status) {
            case Order.ORDER_STATUS_WAIT:
                if (MainActivity.userType == MainActivity.SHIPPER) {
                    textStatus = mContext.getString(R.string.order_status_wait);
                    action = "";
                    canCancel = true;
                } else {
                    textStatus = mContext.getString(R.string.order_shop_status_wait);
                    action = mContext.getString(R.string.action_shop_wait);
                    canCancel = true;
                }
                drawableStatus = ResourcesCompat.getDrawable(mContext.getResources(),
                                                             R.drawable.ic_status_waiting,
                                                             null);
                statusColor = mContext.getResources().getColor(R.color.color_status_waiting);
                break;
            case Order.ORDER_STATUS_TAKE:
                if (MainActivity.userType == MainActivity.SHIPPER) {
                    textStatus = mContext.getString(R.string.order_status_take);
                    action = mContext.getString(R.string.action_shipper_take);
                    canCancel = true;
                } else {
                    textStatus = mContext.getString(R.string.order_shop_status_take);
                    action = "";
                    canCancel = true;
                }
                drawableStatus = ResourcesCompat.getDrawable(mContext.getResources(),
                                                             R.drawable.ic_status_pick,
                                                             null);
                statusColor = mContext.getResources().getColor(R.color.color_status_pick);
                break;
            case Order.ORDER_STATUS_SHIPPING:
                textStatus = mContext.getString(R.string.order_status_shipping);
                if (MainActivity.userType == MainActivity.SHIPPER) {
                    action = mContext.getString(R.string.action_shipper_shipping);
                    canCancel = true;
                } else {
                    action = "";
                    canCancel = false;
                }
                drawableStatus = ResourcesCompat.getDrawable(mContext.getResources(),
                                                             R.drawable.ic_status_delivering,
                                                             null);
                statusColor = mContext.getResources().getColor(R.color.color_status_shipping);
                break;
            case Order.ORDER_STATUS_DELIVERED:
                textStatus = mContext.getString(R.string.order_status_delivered);
                if (MainActivity.userType == MainActivity.SHIPPER) {
                    action = "";
                    canCancel = false;
                } else {
                    action = mContext.getString(R.string.action_shop_delivered);
                    canCancel = false;
                }
                drawableStatus = ResourcesCompat.getDrawable(mContext.getResources(),
                                                             R.drawable.ic_status_delivered,
                                                             null);
                statusColor = mContext.getResources().getColor(R.color.color_status_delivered);
                break;
            case Order.ORDER_STATUS_FINISHED:
                textStatus = mContext.getString(R.string.order_status_finished);
                if (MainActivity.userType == MainActivity.SHIPPER) {
                    canCancel = false;
                } else {
                    canCancel = false;
                }
                drawableStatus = ResourcesCompat.getDrawable(mContext.getResources(),
                                                             R.drawable.ic_status_finish,
                                                             null);
                statusColor = mContext.getResources().getColor(R.color.color_status_finish);
                break;
            case Order.ORDER_STATUS_CANCELLED:
                textStatus = mContext.getString(R.string.order_status_cancelled);
                if (MainActivity.userType == MainActivity.SHIPPER) {
                    canCancel = false;
                } else {
                    canCancel = false;
                }
                drawableStatus = ResourcesCompat.getDrawable(mContext.getResources(),
                                                             R.drawable.ic_cancel,
                                                             null);
                statusColor = mContext.getResources().getColor(R.color.color_status_cancelled);
                break;
            default:
                textStatus = "";
                action = mContext.getString(R.string.action);
                canCancel = true;
                drawableStatus = ResourcesCompat.getDrawable(mContext.getResources(),
                                                             R.drawable.ic_status_waiting,
                                                             null);
                statusColor = mContext.getResources().getColor(R.color.colorAccent);
                break;
        }

            holder.mShopOrderStatus.setVisibility(View.GONE);
            holder.mTvShippingOrderStatus.setVisibility(View.VISIBLE);
            holder.mTvShippingOrderStatus.setText(textStatus);
            holder.mTvShippingOrderStatus.setTextColor(statusColor);
            holder.mTvShippingOrderStatus.setCompoundDrawablesWithIntrinsicBounds(drawableStatus,
                                                                                  null, null, null);
        if (! canCancel) {
            holder.mBtnCancelItemOrder.setVisibility(View.GONE);
        } else {
            holder.mBtnCancelItemOrder.setVisibility(View.VISIBLE);
            holder.mBtnCancelItemOrder.setText(R.string.cancelled);
        }
        if (action.equals("")) {
            holder.mBtnActionItemOrder.setVisibility(View.GONE);
        } else {
            holder.mBtnActionItemOrder.setVisibility(View.VISIBLE);
            holder.mBtnActionItemOrder.setText(action);
        }
    }

    public List<Invoice> getInvoiceList() {
        return mInvoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        mInvoiceList = invoiceList;
    }

    public OnClickActionListener getClickActionListener() {
        return mClickActionListener;
    }

    public void setClickActionListener(OnClickActionListener clickActionListener) {
        mClickActionListener = clickActionListener;
    }

    public OnClickCancelListener getClickCancelListener() {
        return mClickCancelListener;
    }

    public void setClickCancelListener(OnClickCancelListener clickCancelListener) {
        mClickCancelListener = clickCancelListener;
    }

    @Override
    public int getItemCount() {
        if (mInvoiceList == null)
            return 0;
        return mInvoiceList.size();
    }

    static

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Invoice mInvoice;
        @BindView(R.id.tv_shipping_order_status)
        TextView mTvShippingOrderStatus;
        @BindView(R.id.tv_item_order_shop_name)
        TextView mTvNearbyShopName;
        @BindView(R.id.ll_order_status)
        LinearLayout mLlOrderStatus;
        @BindView(R.id.tv_item_order_ship_price)
        TextView mTvNearbyShipPrice;
        @BindView(R.id.tv_item_order_from)
        TextView mTvItemOrderFrom;
        @BindView(R.id.tv_item_order_to)
        TextView mTvItemOrderTo;
        @BindView(R.id.delivery_to_address_box)
        LinearLayout mDeliveryToAddressBox;
        @BindView(R.id.action_detail_order)
        LinearLayout mActionDetailOrder;
        @BindView(R.id.tv_item_order_distance)
        TextView mTvItemOrderDistance;
        @BindView(R.id.tv_item_order_ship_time)
        TextView mTvItemOrderShipTime;
        @BindView(R.id.tv_item_order_price)
        TextView mTvItemOrderOrderPrice;
        @BindView(R.id.window_order_detail)
        RelativeLayout mWindowOrderDetail;
        @BindView(R.id.ll_shop_order_status)
        LinearLayout mShopOrderStatus;
        @BindView(R.id.btn_cancel_item_order)
        TextView mBtnCancelItemOrder;
        @BindView(R.id.btn_action_item_order)
        TextView mBtnActionItemOrder;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    public interface OnClickActionListener {
        void onClickActionListener(Invoice invoice);
    }

    public interface OnClickCancelListener {
        void onClickCancelListener(Invoice invoice);
    }
}
