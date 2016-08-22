package com.framgia.ishipper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.adapter.MainTabAdapter;
import com.framgia.ishipper.ui.view.CustomViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dinhduc on 18/07/2016.
 */
public class MainContentFragment extends Fragment {
    private static final String TAG = "MainContentFragment";
    private Unbinder mUnbinder;

    @BindView(R.id.tablayout_main)
    TabLayout mTabLayout;
    @BindView(R.id.main_fragment_container)
    CustomViewPager mViewPager;

    public MainContentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mViewPager.setPagingEnabled(false);
        MainTabAdapter adapter = new MainTabAdapter(getChildFragmentManager(), getContext());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}
