package com.framgia.ishipper.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListInvoiceData;
import com.framgia.ishipper.ui.adapter.OrderAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListFragment extends Fragment implements OrderAdapter.OnClickCancelListener,
        OrderAdapter.OnClickActionListener {
    private static final String TAG = "OrderListFragment";
    private static final String LIST_INVOICE = "list invoice";
    private static final String TAB_TITLE = "title";
    private static final String STATUS_CODE = "status";
    private String mTitle;
    private int mStatusCode;
    private OnListFragmentInteractionListener mListener;
    private OnActionClickListener mOnActionClickListener;

    private List<Invoice> mInvoiceList;
    private OrderAdapter mOrderAdapter;

    public OrderListFragment() {
    }

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

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            initAdapter(context);
            mOrderAdapter.setClickCancelListener(this);
            mOrderAdapter.setClickActionListener(this);
            recyclerView.setAdapter(mOrderAdapter);
        }
        return view;
    }

    public void setData(Context context) {
        initAdapter(context);
        if (mInvoiceList.size() == 0) {
            getInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
                       Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
                       mStatusCode);
        }
    }

    public void notifyChangedData(Context cotext) {
        initAdapter(cotext);
        getInvoice(Config.getInstance().getUserInfo(getContext()).getRole(),
                   Config.getInstance().getUserInfo(getContext()).getAuthenticationToken(),
                   mStatusCode);
    }

    private void initAdapter(Context context) {
        if (mInvoiceList == null) {
            mInvoiceList = new ArrayList<>();
        }
        if (mOrderAdapter == null) {
            mOrderAdapter = new OrderAdapter(context, mInvoiceList, mListener);
        }
    }

    private void getInvoice(String role, String authenticationToken, int statusCode) {
        String status = Invoice.getStatusFromCode(statusCode);
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.GetListInvoice.PARAM_STATUS, status);
        API.getInvoice(role,
                       authenticationToken,
                       params,
                       new API.APICallback<APIResponse<ListInvoiceData>>() {
                           @Override
                           public void onResponse(APIResponse<ListInvoiceData> response) {
                               Log.d(TAG, "onResponse: " + response.isSuccess());
                               mInvoiceList.clear();
                               mInvoiceList.addAll(response.getData().getInvoiceList());
                               mOrderAdapter.notifyDataSetChanged();
                           }

                           @Override
                           public void onFailure(int code, String message) {
                               Log.d(TAG, "onFailure: " + message);
                           }
                       }
        );
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
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Invoice invoice);
    }

    public interface OnActionClickListener {
        void onClickAction(Invoice invoice);

        void onClickCancel(Invoice invoice);
    }
}
