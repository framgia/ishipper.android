package com.framgia.ishipper.ui.fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vuduychuong1994 on 7/20/16.
 */
public class ShopOrderManagerFragment extends OrderManagerFragment {

    private List<String> mListOrderTitle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        init();
        super.setOrderTitleList(mListOrderTitle);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        mListOrderTitle = new ArrayList<>();
        mListOrderTitle.add(getString(R.string.tab_title_shop_order_wait));
        mListOrderTitle.add(getString(R.string.tab_title_shop_take_order));
        mListOrderTitle.add(getString(R.string.tab_title_shipping));
        mListOrderTitle.add(getString(R.string.tab_title_delivered));
        mListOrderTitle.add(getString(R.string.tab_title_finished));
        mListOrderTitle.add(getString(R.string.tab_title_cancelled));
        mListOrderTitle.add(getString(R.string.tab_title_all));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }


}
