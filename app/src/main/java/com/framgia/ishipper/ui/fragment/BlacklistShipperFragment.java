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
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListShipperData;
import com.framgia.ishipper.ui.adapter.BlackListAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BlacklistShipperFragment extends Fragment {
    private static final String TAG = "BlacklistShipperFragment";

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private Unbinder mUnbinder;
    private Context mContext;
    private BlackListAdapter mBlackListAdapter;
    private List<User> mShipperList;
    private User mUser;

    public BlacklistShipperFragment() {
    }

    public static BlacklistShipperFragment newInstance() {
        return new BlacklistShipperFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blacklist_shipper, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        invalidView(view);
        return view;
    }

    private void invalidView(View view) {
        mContext = view.getContext();
        mUser = Config.getInstance().getUserInfo(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        setAdapter(mRecyclerView);

    }

    private void setAdapter(RecyclerView recyclerView) {
        if (mShipperList == null)
            mShipperList = new ArrayList<>();
        if (mBlackListAdapter == null)
            mBlackListAdapter = new BlackListAdapter(mContext, mShipperList);
        mRecyclerView.setAdapter(mBlackListAdapter);
        getBlackListShipper();
    }

    private void getBlackListShipper() {
        API.getBlackListShipper(mUser.getAuthenticationToken(),
                                new API.APICallback<APIResponse<ListShipperData>>() {
                                    @Override
                                    public void onResponse(APIResponse<ListShipperData> response) {
                                        Log.d(TAG, response.getMessage());
                                    }

                                    @Override
                                    public void onFailure(int code, String message) {

                                    }
                                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
