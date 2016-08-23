package com.framgia.ishipper.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.ui.activity.ListShipperRegActivity;
import com.framgia.ishipper.ui.fragment.NearbyOrderFragment;
import com.framgia.ishipper.ui.fragment.NearbyShipperFragment;
import com.framgia.ishipper.ui.fragment.OrderListFragment;
import com.framgia.ishipper.ui.fragment.ShippingFragment;

import java.util.ArrayList;
import static com.framgia.ishipper.ui.activity.MainActivity.*;

/**
 * Created by dinhduc on 20/07/2016.
 */
public class MainTabAdapter extends FragmentPagerAdapter
        implements OrderListFragment.OnActionClickListener {
    private String[] mTitle;
    private ArrayList<Invoice> mInvoiceList;
    private Context mContext;
    private OrderListFragment mOrderListFragment;

    public MainTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        if (userType == SHIPPER) {
            mTitle = new String[]{
                    context.getString(R.string.all_nearby_order),
                    context.getString(R.string.all_shipping_order)
            };
        } else {
            mTitle = new String[]{
                    context.getString(R.string.all_nearby_order),
                    context.getString(R.string.all_waiting_order)
            };

            // TODO: set list invoice status = wait
            mInvoiceList = new ArrayList<>();
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (userType == SHIPPER) {
                return new NearbyOrderFragment();
            } else {
                return new NearbyShipperFragment();
            }
        } else {
            if (userType == SHIPPER) {
                return new ShippingFragment();
            } else {
                mOrderListFragment =
                        new OrderListFragment().newInstance(
                                                            mContext.getString(R.string.tab_title_shop_order_wait),
                                                            Invoice.STATUS_CODE_INIT);
                mOrderListFragment.setOnActionClickListener(this);
                return mOrderListFragment;
            }
        }
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }

    @Override
    public void onClickAction(Invoice invoice) {
        mContext.startActivity(new Intent(mContext, ListShipperRegActivity.class));
    }

    @Override
    public void onClickCancel(Invoice invoice) {
        Toast.makeText(mContext, "Đã huỷ đơn", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < mInvoiceList.size(); i++) {
            if(invoice.getId() == mInvoiceList.get(i).getId()) {
                mInvoiceList.remove(i);
                mOrderListFragment.getOrderAdapter().notifyDataSetChanged();
            }
        }
    }
}
