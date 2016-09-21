package com.framgia.ishipper.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.adapter.GeneralFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass. Use the {@link UserManageFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class UserManageFragment extends Fragment {

    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.viewpager) ViewPager mViewpager;
    private Unbinder mUnbinder;

    public static UserManageFragment newInstance() {
        return new UserManageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shipper_manage, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        GeneralFragmentPagerAdapter pagerAdapter =
                new GeneralFragmentPagerAdapter(getFragmentManager());
        pagerAdapter.addFragment(
                BlacklistFragment.newInstance(),
                getString(R.string.title_black_list_shipper));
        pagerAdapter.addFragment(
                BlacklistFragment.newInstance(),
                getString(R.string.title_favorite_list_shipper));
        mViewpager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
