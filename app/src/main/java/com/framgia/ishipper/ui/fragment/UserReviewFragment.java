package com.framgia.ishipper.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.ReviewUser;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListReviewData;
import com.framgia.ishipper.ui.adapter.ReviewUserAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserReviewFragment extends Fragment {
    private static final String KEY_USER = "User";
    Context mContext;

    @BindView(R.id.rcv_user_review) RecyclerView mListReview;
    private ArrayList<ReviewUser> mReviewUsers = new ArrayList<>();
    private ReviewUserAdapter mAdapter;
    private User mUserInvoice;
    private User mCurrentUser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static UserReviewFragment newInstance(User user) {
        UserReviewFragment dialogFragment = new UserReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_USER, user);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInvoice = getArguments().getParcelable(KEY_USER);
        mCurrentUser = Config.getInstance().getUserInfo(mContext);
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

    private void getData() {
        if (mCurrentUser != null) {
            API.getListReviews(
                    mCurrentUser.getAuthenticationToken(),
                    mUserInvoice.getId(),
                    new API.APICallback<APIResponse<ListReviewData>>() {
                        @Override
                        public void onResponse(APIResponse<ListReviewData> response) {
                            mReviewUsers = (ArrayList<ReviewUser>) response.getData().getReviewUsers();
                            initView();
                            getWidgetControl();
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void initView() {
        ReviewUser reviewUser = new ReviewUser();
        reviewUser.setRatingPoint("3.5");
        reviewUser.setContent("Good Good");
        reviewUser.setDate("20/09/2016");
        mReviewUsers.add(reviewUser);
        reviewUser = new ReviewUser();
        reviewUser.setRatingPoint("3.5");
        reviewUser.setContent("Good Good");
        reviewUser.setDate("20/09/2016");
        mReviewUsers.add(reviewUser);
        reviewUser = new ReviewUser();
        reviewUser.setRatingPoint("3.5");
        reviewUser.setContent("Good Good");
        reviewUser.setDate("20/09/2016");
        mReviewUsers.add(reviewUser);


        mAdapter = new ReviewUserAdapter(mContext, R.layout.item_user_review, mReviewUsers);
        mListReview.setLayoutManager(new LinearLayoutManager(mContext));
        mListReview.setAdapter(mAdapter);
    }

    private void getWidgetControl() {

    }

}
