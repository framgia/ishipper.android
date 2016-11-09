package com.framgia.ishipper.ui.activity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.AddBlacklistData;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.ListUserData;
import com.framgia.ishipper.ui.adapter.BlackListAdapter;
import com.framgia.ishipper.ui.view.ConfirmDialog;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vuduychuong1994 on 9/30/16.
 */

public class BlackListActivity extends ToolbarActivity {
    private static final String TAG = "BlackListActivity";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private BlackListAdapter mBlackListAdapter;
    private List<User> mBlackListUser;
    private User mCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        invalidView();
        getBlackList();
    }

    private void invalidView() {
        mCurrentUser = Config.getInstance().getUserInfo(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setUpRecycleView(mRecyclerView);
    }

    private void setUpRecycleView(RecyclerView recyclerView) {
        mBlackListUser = new ArrayList<>();
        mBlackListAdapter = new BlackListAdapter(BlackListActivity.this, mBlackListUser);
        recyclerView.setAdapter(mBlackListAdapter);
    }

    private void getBlackList() {
        API.getBlackList(mCurrentUser.getAuthenticationToken(), mCurrentUser.getRole(),
                         new API.APICallback<APIResponse<ListUserData>>() {
                             @Override
                             public void onResponse(APIResponse<ListUserData> response) {
                                 Log.d(TAG, response.getMessage());
                                 mBlackListAdapter.getUserList().clear();
                                 mBlackListAdapter.getUserList().addAll(
                                         response.getData().getShippersList());
                                 mBlackListAdapter.notifyDataSetChanged();
                             }

                             @Override
                             public void onFailure(int code, String message) {
                                 Toast.makeText(BlackListActivity.this, message,
                                                Toast.LENGTH_SHORT).show();
                             }
                         });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_favorite_add) {
            startActivityForResult(new Intent(BlackListActivity.this, SearchUserActivity.class),
                                   Const.RequestCode.REQUEST_SEARCH_BLACKLIST);
            return true;
        } else if (item.getItemId() == R.id.menu_delete_all) {
            if (mBlackListUser == null || mBlackListUser.size() == 0) {
                return false;
            }
            new ConfirmDialog(BlackListActivity.this).setIcon(
                    R.drawable.ic_delete_white_24dp).setMessage(
                    getString(R.string.delete_all_dialog_message)).setTitle(
                    getString(R.string.delete_all_dialog_title)).setButtonCallback(
                    new ConfirmDialog.ConfirmDialogCallback() {
                        @Override
                        public void onPositiveButtonClick(final ConfirmDialog confirmDialog) {
                            API.deleteAllBlackList(mCurrentUser.getRole(),
                                                   mCurrentUser.getAuthenticationToken(),
                                                   new API.APICallback<APIResponse<EmptyData>>() {
                                                       @Override
                                                       public void onResponse(
                                                               APIResponse<EmptyData> response) {
                                                           mBlackListAdapter.getUserList().clear();
                                                           mBlackListAdapter.notifyDataSetChanged();
                                                           confirmDialog.cancel();
                                                           Toast.makeText(BlackListActivity.this,
                                                                          R.string.toast_delete_all,
                                                                          Toast.LENGTH_SHORT).show();
                                                       }

                                                       @Override
                                                       public void onFailure(
                                                               int code, String message) {
                                                           Toast.makeText(BlackListActivity.this,
                                                                          message,
                                                                          Toast.LENGTH_SHORT).show();
                                                       }
                                                   });
                        }

                        @Override
                        public void onNegativeButtonClick(ConfirmDialog confirmDialog) {
                            confirmDialog.cancel();
                        }
                    }).show();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.RequestCode.REQUEST_SEARCH_BLACKLIST) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            final User user = data.getParcelableExtra(Const.KEY_USER);
            if (user == null) {
                return;
            }
            final Dialog dialog = CommonUtils.showLoadingDialog(BlackListActivity.this);
            dialog.show();
            API.addUserToBlackList(mCurrentUser.getRole(), mCurrentUser.getAuthenticationToken(),
                                   user.getId(),
                                   new API.APICallback<APIResponse<AddBlacklistData>>() {
                                       @Override
                                       public void onResponse(
                                               APIResponse<AddBlacklistData> response) {
                                           Toast.makeText(BlackListActivity.this,
                                                          response.getMessage(),
                                                          Toast.LENGTH_SHORT).show();
                                           user.setBlackListUserId(String.valueOf(
                                                   response.getData().getResponse().getBlacklistId()));
                                           mBlackListUser.add(0, user);
                                           mBlackListAdapter.setUserList(mBlackListUser);
                                           mBlackListAdapter.notifyDataSetChanged();
                                           if (dialog.isShowing()) {
                                               dialog.cancel();
                                           }
                                           Toast.makeText(BlackListActivity.this,
                                                          response.getMessage(),
                                                          Toast.LENGTH_SHORT).show();
                                       }

                                       @Override
                                       public void onFailure(int code, String message) {
                                           Toast.makeText(BlackListActivity.this, message,
                                                          Toast.LENGTH_SHORT).show();
                                           if (dialog.isShowing()) {
                                               dialog.cancel();
                                           }
                                       }
                                   });
        }
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.title_black_list_shipper;
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_blacklist_user;
    }
}
