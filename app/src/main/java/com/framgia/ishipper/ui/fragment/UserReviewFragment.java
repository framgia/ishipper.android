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
import com.framgia.ishipper.model.ReviewUser;
import com.framgia.ishipper.ui.adapter.ReviewUserAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserReviewFragment extends Fragment {
    Context mContext;

    @BindView(R.id.rcv_user_review) RecyclerView mListReview;
    ArrayList<ReviewUser> mReviewUsers = new ArrayList<>();
    ReviewUserAdapter mAdapter;

    public UserReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_review, container, false);
        ButterKnife.bind(this, view);
        initView();
        getWidgetControl();
        return view;
    }

    private void initView() {
        ReviewUser reviewUser = new ReviewUser();
        reviewUser.setRatingPoint("3.5");
        reviewUser.setContent("Good Good");
        mReviewUsers.add(reviewUser);
        reviewUser = new ReviewUser();
        reviewUser.setRatingPoint("3.5");
        reviewUser.setContent("Good Good");
        mReviewUsers.add(reviewUser);
        reviewUser = new ReviewUser();
        reviewUser.setRatingPoint("3.5");
        reviewUser.setContent("Good Good");
        mReviewUsers.add(reviewUser);


        mAdapter = new ReviewUserAdapter(mContext, R.layout.item_user_review, mReviewUsers);
        mListReview.setLayoutManager(new LinearLayoutManager(mContext));
        mListReview.setAdapter(mAdapter);
    }

    private void getWidgetControl() {

    }

}
