package com.framgia.ishipper.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListUserData;
import com.framgia.ishipper.ui.adapter.BlackListAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private Unbinder mUnbinder;
    private Context mContext;
    private BlackListAdapter mFavoriteListAdapter;
    private List<User> mShipperList;
    private User mUser;

    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
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
        if (mFavoriteListAdapter == null)
            mFavoriteListAdapter = new BlackListAdapter(mContext, mShipperList);
        mRecyclerView.setAdapter(mFavoriteListAdapter);
        getBlackListShipper();
    }

    private void getBlackListShipper() {
        API.getFavoriteListShipper(mUser.getAuthenticationToken(),
                                new API.APICallback<APIResponse<ListUserData>>() {
                                    @Override
                                    public void onResponse(APIResponse<ListUserData> response) {
                                        Log.d(TAG, response.getMessage());
                                        mShipperList.clear();
                                        mShipperList.addAll(response.getData().getShippersList());
                                        mFavoriteListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(int code, String message) {
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
