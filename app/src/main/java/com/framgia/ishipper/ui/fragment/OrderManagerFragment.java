package com.framgia.ishipper.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.InvoiceData;
import com.framgia.ishipper.net.data.ListInvoiceData;
import com.framgia.ishipper.ui.activity.ListShipperRegActivity;
import com.framgia.ishipper.ui.activity.LoginActivity;
import com.framgia.ishipper.ui.adapter.OrderAdapter;
import com.framgia.ishipper.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderManagerFragment extends Fragment implements OrderListFragment.OnActionClickListener {
    private static final String TAG = "OrderManagerFragment";
    private OrderManagerPagerAdapter mOrderManagerPagerAdapter;
    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabLayout;

    private List<OrderListFragment> mListOrderFragment;
    private Unbinder mUnbinder;
    private List<String> mOrderTitleList;
    private ArrayList<Order> mOrderArrayList;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_manager, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initData();
        initEvent();
        return view;
    }

    private void initData() {
        mOrderArrayList = Order.SampleListOrder();
        mListOrderFragment = new ArrayList<>();

        mOrderManagerPagerAdapter =
                new OrderManagerPagerAdapter(getFragmentManager(), mListOrderFragment, mOrderTitleList);

        mViewPager.setAdapter(mOrderManagerPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(7);

        for (int type = 0; type < 7; type++) {
            //TODO: Fake list Order
            ArrayList<Invoice> invoices = new ArrayList<>();
            String titleTab;
            if (mOrderTitleList.size() > type) {
                titleTab = mOrderTitleList.get(type);
            } else {
                titleTab = getContext().getString(R.string.tab_title_unknow);
            }
            OrderListFragment orderListFragment =
                    OrderListFragment.newInstance(titleTab, invoices);
            orderListFragment.setOnActionClickListener(this);
            mListOrderFragment.add(orderListFragment);
            mOrderManagerPagerAdapter.notifyDataSetChanged();
        }
//        getInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
//                   Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
//                   mListOrderFragment.get(Invoice.STATUS_CODE_INIT).getOrderAdapter(),
//                   Invoice.STATUS_CODE_INIT);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: " + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);

                if (mListOrderFragment.get(position).getInvoiceList().size() == 0) {
                    getInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
                               Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
                               mListOrderFragment.get(position).getOrderAdapter(),
                               position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged: " + state);
            }
        });
    }

    private void getInvoice(String role, String authenticationToken, final OrderAdapter orderAdapter,
                            int type) {
        String status = Invoice.STATUS_ALL;
        if (type == Invoice.STATUS_CODE_INIT) status = Invoice.STATUS_INIT;
        if (type == Invoice.STATUS_CODE_WAITING) status = Invoice.STATUS_WAITING;
        if (type == Invoice.STATUS_CODE_SHIPPING) status = Invoice.STATUS_SHIPPING;
        if (type == Invoice.STATUS_CODE_SHIPPED) status = Invoice.STATUS_SHIPPED;
        if (type == Invoice.STATUS_CODE_FINISHED) status = Invoice.STATUS_FINISHED;
        if (type == Invoice.STATUS_CODE_CANCEL) status = Invoice.STATUS_CANCEL;
        if (type == Invoice.STATUS_CODE_ALL) status = Invoice.STATUS_ALL;
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.GetListInvoice.PARAM_STATUS, status);
        API.getInvoice(role,
                       authenticationToken,
                       params,
                       new API.APICallback<APIResponse<ListInvoiceData>>() {
                           @Override
                           public void onResponse(APIResponse<ListInvoiceData> response) {
                               Log.d(TAG, "onResponse: " + response.isSuccess());
                               orderAdapter.getInvoiceList().clear();
                               orderAdapter.getInvoiceList().addAll(response.getData().getInvoiceList());
                               orderAdapter.notifyDataSetChanged();
                           }

                           @Override
                           public void onFailure(int code, String message) {
                               Log.d(TAG, "onFailure: " + message);
                           }
                       }
        );
    }

    public ArrayList<Order> filterOrder(ArrayList<Order> orderArrayList, int type) {
        if (type == Order.ORDER_STATUS_ALL)
            return orderArrayList;
        ArrayList<Order> filteredOrderList = new ArrayList<>();
        for (Order order : orderArrayList) {
            if (order.getStatus() == type)
                filteredOrderList.add(order);
        }
        return filteredOrderList;
    }

    private void initEvent() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_order_manager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public List<String> getOrderTitleList() {
        return mOrderTitleList;
    }

    public void setOrderTitleList(List<String> orderTitleList) {
        mOrderTitleList = orderTitleList;
    }

    public void notifyChangeTab(int type) {
        if (mListOrderFragment.size() > type) {
            getInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
                       Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
                       mListOrderFragment.get(type).getOrderAdapter(),
                       type);
//            mListOrderFragment.get(type).notifyChangedData(orderList);
        }
    }

    @Override
    public void onClickAction(Invoice invoice) {
        final Dialog loadingDialog;
        switch (invoice.getStatusCode()) {
            case Order.ORDER_STATUS_WAIT:
                startActivity(new Intent(mContext, ListShipperRegActivity.class));
                break;
            case Order.ORDER_STATUS_TAKE:
                // TODO invoice id
                Log.d("hung", "onClickAction: take ");
                loadingDialog = CommonUtils.showLoadingDialog(getActivity());
                API.putUpdateInvoiceStatus(String.valueOf(invoice.getId()),
                        Invoice.STATUS_SHIPPING,
                        new API.APICallback<APIResponse<InvoiceData>>() {
                            @Override
                            public void onResponse(APIResponse<InvoiceData> response) {
//                                order.setStatus(Order.ORDER_STATUS_SHIPPING);
                                notifyChangeTab(Order.ORDER_STATUS_TAKE);
                                notifyChangeTab(Order.ORDER_STATUS_SHIPPING);
                                loadingDialog.dismiss();
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        });

                break;
            case Order.ORDER_STATUS_SHIPPING:
                Log.d("hung", "onClickAction: shipping ");
                loadingDialog = CommonUtils.showLoadingDialog(getActivity());
                API.putUpdateInvoiceStatus(String.valueOf(invoice.getId()),
                        Invoice.STATUS_SHIPPING,
                        new API.APICallback<APIResponse<InvoiceData>>() {
                            @Override
                            public void onResponse(APIResponse<InvoiceData> response) {
                                loadingDialog.dismiss();
//                                order.setStatus(Order.ORDER_STATUS_DELIVERED);
                                notifyChangeTab(Order.ORDER_STATUS_SHIPPING);
                                notifyChangeTab(Order.ORDER_STATUS_DELIVERED);
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                loadingDialog.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        });

                break;
            case Order.ORDER_STATUS_DELIVERED:
                Log.d("hung", "onClickAction: delivered ");
                loadingDialog = CommonUtils.showLoadingDialog(getActivity());
                API.putUpdateInvoiceStatus(String.valueOf(invoice.getId()),
                        Invoice.STATUS_SHIPPING,
                        new API.APICallback<APIResponse<InvoiceData>>() {
                            @Override
                            public void onResponse(APIResponse<InvoiceData> response) {
//                                order.setStatus(Order.ORDER_STATUS_SHIPPING);
                                loadingDialog.dismiss();
                                notifyChangeTab(Order.ORDER_STATUS_DELIVERED);
                                notifyChangeTab(Order.ORDER_STATUS_FINISHED);
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                loadingDialog.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
    }

    @Override
    public void onClickCancel(Invoice invoice) {
        for (Order ord : mOrderArrayList) {
            if (invoice.getId() == ord.getId()) {
                int currentStatus = invoice.getStatusCode();
                ord.setStatus(Order.ORDER_STATUS_CANCELLED);
                notifyChangeTab(currentStatus);
                notifyChangeTab(Order.ORDER_STATUS_CANCELLED);
            }
        }
    }

    public class OrderManagerPagerAdapter extends FragmentStatePagerAdapter {

        private List<OrderListFragment> mOrderListFragments;
        private List<String> mListTitle;

        public OrderManagerPagerAdapter(FragmentManager fm, List<OrderListFragment> fragmentList,
                                        List<String> orderTitleList) {
            super(fm);
            mOrderListFragments = fragmentList;
            mListTitle = orderTitleList;
        }

        @Override
        public Fragment getItem(int position) {

            return mOrderListFragments.get(position);
        }

        @Override
        public int getCount() {
            if (mOrderListFragments == null)
                return 0;
            else
                return mOrderListFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mListTitle.get(position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
