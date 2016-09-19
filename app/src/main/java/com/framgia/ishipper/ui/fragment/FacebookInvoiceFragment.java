package com.framgia.ishipper.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.FbInvoice;
import com.framgia.ishipper.ui.adapter.FacebookInvoicesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FacebookInvoiceFragment extends Fragment {
    @BindView(R.id.rvInvoices) RecyclerView mRvInvoices;
    private FacebookInvoicesAdapter mAdapter;
    private List<FbInvoice> mListInvoices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook_invoice, container, false);
        ButterKnife.bind(this, view);
        mListInvoices = new ArrayList<>();
        mAdapter = new FacebookInvoicesAdapter(getContext(), mListInvoices);
        mRvInvoices.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mRvInvoices.setAdapter(mAdapter);
        return view;
    }

    @OnClick(R.id.btnPinnedInvoice)
    public void onClick() {
        //TODO Pin invoice
    }
}
