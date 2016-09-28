package com.framgia.ishipper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListInvoiceData;
import com.framgia.ishipper.ui.activity.OrderDetailActivity;
import com.framgia.ishipper.ui.adapter.OrderAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderListFragment extends Fragment implements OrderAdapter.OnClickCancelListener,
        OrderAdapter.OnClickActionListener, OrderAdapter.OnclickViewListener {
    private static final String TAG = "OrderListFragment";
    private static final String TAB_TITLE = "title";
    private static final String STATUS_CODE = "status";

    @BindView(R.id.list) RecyclerView mRecyclerView;
    @BindView(R.id.layout_loading) RelativeLayout mLayoutLoading;
    @BindView(R.id.layout_refresh) SwipeRefreshLayout mLayoutRefresh;
    @BindView(R.id.edtSearch) EditText mEdtSearch;
    @BindView(R.id.imgSearch) ImageView mImgSearch;

    private String mTitle;
    private int mStatusCode;
    private OnActionClickListener mOnActionClickListener;
    private LinearLayoutManager mLayoutManager;
    private List<Invoice> mInvoiceList;
    private OrderAdapter mOrderAdapter;
    private Unbinder mUnbinder;
    private Context mContext;

    public static OrderListFragment newInstance(String title, int status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(TAB_TITLE, title);
        args.putInt(STATUS_CODE, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTitle = getArguments().getString(TAB_TITLE);
            mStatusCode = getArguments().getInt(STATUS_CODE, Invoice.STATUS_CODE_ALL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_order_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mContext = view.getContext();
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initAdapter(mContext);
        mRecyclerView.setAdapter(mOrderAdapter);
        mLayoutRefresh.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE);
        setEvent();
        setData(mContext, null);
        return view;
    }

    private void setEvent() {
        mOrderAdapter.setClickCancelListener(this);
        mOrderAdapter.setClickActionListener(this);
        mLayoutRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
                        Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
                        mStatusCode, null);
            }
        });
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEdtSearch.getText().toString().equals("")) {
                    mImgSearch.setVisibility(View.VISIBLE);
                } else {
                    mImgSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mInvoiceList.clear();

                searchInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
                        Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
                        mStatusCode, mEdtSearch.getText().toString(), null);

            }
        });
    }


    public void setData(Context context, OnGetInvoiceListener listener) {
        initAdapter(context);
        getInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
                Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
                mStatusCode, listener);
    }

    public void notifyChangedData(Context context, OnGetInvoiceListener listener) {
        initAdapter(context);
        getInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
                Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
                mStatusCode, listener);
    }

    public void moveListToInvoice(int invoiceId) {
        if (invoiceId < 0) return;
        for (int i = 0; i < mInvoiceList.size(); i++) {
            if (invoiceId == mInvoiceList.get(i).getId()) {
                mLayoutManager.scrollToPosition(i);
                mOrderAdapter.setPositionHighlight(i);
                mOrderAdapter.notifyItemChanged(i);
            }
        }
    }

    private void initAdapter(Context context) {
        if (mInvoiceList == null) {
            mInvoiceList = new ArrayList<>();
        }
        if (mOrderAdapter == null) {
            mOrderAdapter = new OrderAdapter(context, mInvoiceList, this);
        }
    }

    private void getInvoice(String role, String authenticationToken, int statusCode,
                            final OnGetInvoiceListener callback) {
        showLoading();
        String status = Invoice.getStatusFromCode(statusCode);
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.GetListInvoice.PARAM_STATUS, status);
        API.getInvoice(role,
                authenticationToken,
                params,
                new API.APICallback<APIResponse<ListInvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<ListInvoiceData> response) {
                        mInvoiceList.clear();
                        mInvoiceList.addAll(response.getData().getInvoiceList());
                        mOrderAdapter.setPositionHighlight(-1);
                        mOrderAdapter.notifyDataSetChanged();
                        if (callback != null) callback.onGetInvoiceSuccess();
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d(TAG, "onFailure: " + message);
                        if (callback != null) callback.onGetInvoiceFail();
                        dismissLoading();
                    }
                }
        );
    }

    private void searchInvoice(String role, String authenticationToken, int statusCode, String nameSearch,
                               final OnGetInvoiceListener callback) {
        showLoading();
        String status = Invoice.getStatusFromCode(statusCode);
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.GetListInvoice.PARAM_STATUS, status);
        params.put(APIDefinition.GetListInvoice.PARAM_QUERY, nameSearch);
        API.getInvoice(role,
                authenticationToken,
                params,
                new API.APICallback<APIResponse<ListInvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<ListInvoiceData> response) {
                        mInvoiceList.clear();
                        mInvoiceList.addAll(response.getData().getInvoiceList());
                        mOrderAdapter.notifyDataSetChanged();
                        if (callback != null) callback.onGetInvoiceSuccess();
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        if (callback != null) callback.onGetInvoiceFail();
                        dismissLoading();
                    }
                }
        );
    }

    private void dismissLoading() {
        if (mLayoutLoading != null) mLayoutLoading.setVisibility(View.GONE);
        if (mRecyclerView != null) mRecyclerView.setVisibility(View.VISIBLE);
        if (mLayoutRefresh!= null && mLayoutRefresh.isRefreshing()) {
            mLayoutRefresh.setRefreshing(false);
        }
    }

    private void showLoading() {
        if (mLayoutLoading != null) mLayoutLoading.setVisibility(View.VISIBLE);
        if (mRecyclerView != null) mRecyclerView.setVisibility(View.GONE);
    }

    public List<Invoice> getInvoiceList() {
        return mInvoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        mInvoiceList = invoiceList;
    }

    public OrderAdapter getOrderAdapter() {
        return mOrderAdapter;
    }

    public String getTitle() {
        if (mTitle != null)
            return mTitle;
        else
            return "";
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setOnActionClickListener(OnActionClickListener onActionClickListener) {
        mOnActionClickListener = onActionClickListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onClickCancelListener(Invoice invoice) {
        if (mOnActionClickListener != null) {
            mOnActionClickListener.onClickCancel(invoice);
        }
    }

    @Override
    public void onClickActionListener(Invoice invoice) {
        if (mOnActionClickListener != null) {
            mOnActionClickListener.onClickAction(invoice);
        }
    }

    @Override
    public void onclickViewListener(Invoice invoice) {
        if (mOnActionClickListener != null) {
            mOnActionClickListener.onClickView(invoice);
        }
    }

    public interface OnActionClickListener {
        void onClickAction(Invoice invoice);

        void onClickCancel(Invoice invoice);

        void onClickView(Invoice invoice);
    }

    public interface OnGetInvoiceListener {
        void onGetInvoiceSuccess();

        void onGetInvoiceFail();
    }
}
