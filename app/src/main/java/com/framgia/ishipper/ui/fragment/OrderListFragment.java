package com.framgia.ishipper.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.ui.adapter.OrderAdapter;
import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment implements OrderAdapter.OnClickCancelListener,
        OrderAdapter.OnClickActionListener {

    private static final String LIST_INVOICE = "list invoice";
    private static final String TAB_TITLE = "title";
    private String mTitle;
    private OnListFragmentInteractionListener mListener;
    private OnActionClickListener mOnActionClickListener;
    private OrderManagerFragment mManagerFragment;

//    private List<Order> mOrderList;
    private List<Invoice> mInvoiceList;
    private OrderAdapter mOrderAdapter;

    public OrderListFragment() {
    }


    public static OrderListFragment newInstance(String title, ArrayList<Invoice> invoiceList) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(TAB_TITLE, title);
        args.putParcelableArrayList(LIST_INVOICE, invoiceList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mInvoiceList = getArguments().getParcelableArrayList(LIST_INVOICE);
            mTitle = getArguments().getString(TAB_TITLE);
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
            mOrderAdapter = new OrderAdapter(context, mInvoiceList, mListener);
            mOrderAdapter.setClickCancelListener(this);
            mOrderAdapter.setClickActionListener(this);
            recyclerView.setAdapter(mOrderAdapter);
        }
        return view;
    }

    public List<Invoice> getInvoiceList() {
        return mInvoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        mInvoiceList = invoiceList;
    }

    public void notifyChangedData (List<Invoice> invoiceList) {
        mInvoiceList.clear();
        mInvoiceList.addAll(invoiceList);
        mOrderAdapter.notifyDataSetChanged();
    }

    public OrderAdapter getOrderAdapter() {
        return mOrderAdapter;
    }

    public String getTitle() {
        if (mTitle != null)
            return mTitle;
        else return "";
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
