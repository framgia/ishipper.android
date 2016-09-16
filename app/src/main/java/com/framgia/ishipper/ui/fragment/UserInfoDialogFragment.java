package com.framgia.ishipper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.adapter.UserInfoTabAdapter;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dinhduc on 14/09/2016.
 */
public class UserInfoDialogFragment extends DialogFragment {

    @BindView(R.id.img_info_dialog_avatar) CircularImageView mImgAvatar;
    @BindView(R.id.tv_info_dialog_name) TextView mTvName;
    @BindView(R.id.rtb_info_dialog_) AppCompatRatingBar mRatingBar;
    @BindView(R.id.tv_info_dialog_order_number) TextView mTvOrderNumber;
    @BindView(R.id.tv_info_dialog_success_order) TextView mTvSuccessOrder;
    @BindView(R.id.tbl_info_dialog) TabLayout mTabLayout;
    @BindView(R.id.vpg_info_dialog) ViewPager mViewPager;

    public static UserInfoDialogFragment newInstance() {
        return new UserInfoDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info_dialog, container, false);
        ButterKnife.bind(this, view);
        setUpViewPager();
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    private void setUpViewPager() {
        UserInfoTabAdapter adapter = new UserInfoTabAdapter(getChildFragmentManager());
        adapter.addFragment(new UserInfoFragment(), "Thông tin");
        adapter.addFragment(new UserReviewFragment(), "Đánh giá");
        mViewPager.setAdapter(adapter);
    }

    @OnClick(R.id.tv_info_dialog_close)
    public void onClick() {
        dismiss();
    }
}
