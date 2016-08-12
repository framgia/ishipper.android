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
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.ui.adapter.OrderAdapter;
import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment implements OrderAdapter.OnClickCancelListener,
        OrderAdapter.OnClickActionListener {

    private static final String LIST_ORDER = "list order";
    private static final String TAB_TITLE = "title";
    private String mTitle;
    private OnListFragmentInteractionListener mListener;
    private OnActionClickListener mOnActionClickListener;
    private OrderManagerFragment mManagerFragment;

    private List<Order> mOrderList;
    private OrderAdapter mOrderAdapter;

    public OrderListFragment() {
    }


    public static OrderListFragment newInstance(String title, ArrayList<Order> orderList) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(TAB_TITLE, title);
        args.putParcelableArrayList(LIST_ORDER, orderList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mOrderList = getArguments().getParcelableArrayList(LIST_ORDER);
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
            mOrderAdapter = new OrderAdapter(context, mOrderList, mListener);
            mOrderAdapter.setClickCancelListener(this);
            mOrderAdapter.setClickActionListener(this);
            recyclerView.setAdapter(mOrderAdapter);
        }
        return view;
    }

    public List<Order> getOrderList() {
        return mOrderList;
    }

    public void setOrderList(List<Order> orderList) {
        mOrderList = orderList;
    }

    public void notifyChangedData (List<Order> orderList) {
        mOrderList.clear();
        mOrderList.addAll(orderList);
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
    public void onClickCancelListener(Order order) {
        if (mOnActionClickListener != null) {
            mOnActionClickListener.onClickCancel(order);
        }
    }

    @Override
    public void onClickActionListener(Order order) {
        if (mOnActionClickListener != null) {
            mOnActionClickListener.onClickAction(order);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Order order);
    }

    public interface OnActionClickListener {
        void onClickAction(Order order);
        void onClickCancel(Order order);
    }
}
