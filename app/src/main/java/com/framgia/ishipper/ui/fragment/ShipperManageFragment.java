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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass. Use the {@link ShipperManageFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class ShipperManageFragment extends Fragment {

    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.viewpager) ViewPager mViewpager;
    private Unbinder mUnbinder;

    public static ShipperManageFragment newInstance() {
        return new ShipperManageFragment();
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
        ShipperManagePagerAdapter pagerAdapter =
                new ShipperManagePagerAdapter(getFragmentManager());
        mViewpager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewpager);
    }

    public class ShipperManagePagerAdapter extends FragmentStatePagerAdapter {

        public ShipperManagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return BlacklistShipperFragment.newInstance();
                case 1:
                    return BlacklistShipperFragment.newInstance();
                default:
                    return BlacklistShipperFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.title_black_list_shipper);
            } else {
                return getResources().getString(R.string.title_favorite_list_shipper);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
