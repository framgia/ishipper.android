package com.framgia.ishipper.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.FbInvoice;
import com.framgia.ishipper.ui.adapter.FacebookInvoicesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FacebookInvoiceFragment extends Fragment {
    private static final String BUNDLE_SHOW_PIN = "BUNDLE_SHOW_PIN";
    @BindView(R.id.rvInvoices) RecyclerView mRvInvoices;
    @BindView(R.id.btnPinnedInvoice) RelativeLayout mBtnPinnedInvoice;
    @BindView(R.id.btnBackToFacebook) RelativeLayout mBtnBackToFacebook;
    private FacebookInvoicesAdapter mAdapter;
    private List<FbInvoice> mListInvoices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_facebook_invoice, container, false);
        ButterKnife.bind(this, view);

        // INVISIBLE show pin button
        if (!getArguments().getBoolean(BUNDLE_SHOW_PIN, true)) {
            mBtnPinnedInvoice.setVisibility(View.GONE);
            mBtnBackToFacebook.setVisibility(View.VISIBLE);
        }

        mListInvoices = new ArrayList<>();
        mAdapter = new FacebookInvoicesAdapter(getContext(), mListInvoices);
        mRvInvoices.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mRvInvoices.setAdapter(mAdapter);
        return view;
    }

    public static FacebookInvoiceFragment newInstance(boolean showPinButton) {
        Bundle args = new Bundle();
        FacebookInvoiceFragment fragment = new FacebookInvoiceFragment();
        args.putBoolean(BUNDLE_SHOW_PIN, showPinButton);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.btnPinnedInvoice)
    public void onClick() {
        // TODO Pin invoice
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left,
                        R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_container, FacebookInvoiceFragment.newInstance(false))
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.btnBackToFacebook)
    public void backToFacebook() {
        getActivity().getSupportFragmentManager().popBackStack();
    }


}
