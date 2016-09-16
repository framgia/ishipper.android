package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.ui.fragment.MainContentFragment;
import com.framgia.ishipper.ui.fragment.ShipperManageFragment;
import com.framgia.ishipper.ui.fragment.ShipperOrderManagerFragment;
import com.framgia.ishipper.ui.fragment.ShopOrderManagerFragment;
import com.framgia.ishipper.ui.fragment.UserInfoDialogFragment;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.StorageUtils;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.main_navigation) NavigationView mNavigationView;
    @BindView(R.id.main_drawer_layout) DrawerLayout mDrawerLayout;

    public static final int SHIPPER = 0;
    public static final int SHOP = 1;
    public static int userType = SHIPPER;
    private User mCurrentUser;
    private int mSelectedId;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        selectItem(R.id.nav_nearby_order);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        mCurrentUser = Config.getInstance().getUserInfo(this);
        if (mCurrentUser.getRole().equals(User.ROLE_SHIPPER)) {
            userType = SHIPPER;
        } else {
            userType = SHOP;
        }
        if (userType == SHOP) {
            mNavigationView.inflateMenu(R.menu.menu_nav_drawer_shop);
        } else {
            mNavigationView.inflateMenu(R.menu.menu_nav_drawer_shipper);
        }
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectItem(item.getItemId());
                return true;
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        /** Set listener to image icon in drawer */
        View view = mNavigationView.getHeaderView(0);
        CircularImageView imageView = (CircularImageView) view.findViewById(R.id.nav_user_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), UserProfileActivity.class));
            }
        });

        UserInfoDialogFragment dialogFragment = UserInfoDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager().beginTransaction(), "dialog");

    }

    private void selectItem(int id) {
        mDrawerLayout.closeDrawer(mNavigationView);
        if (mSelectedId == id) return;
        Fragment fragment;
        String tag = null;
        switch (id) {
            case R.id.nav_nearby_order:
                mSelectedId = id;
                fragment = new MainContentFragment();
                mToolbar.setTitle(getString(R.string.nav_nearby_order_item));
                tag = MainContentFragment.class.getName();
                break;
            case R.id.nav_order_management:
                mSelectedId = id;
                if (userType == SHIPPER) {
                    fragment =
                            ShipperOrderManagerFragment.instantiate(MainActivity.this,
                                    ShipperOrderManagerFragment.class.getName(),
                                    null);
                    tag = ShipperOrderManagerFragment.class.getName();
                } else {
                    fragment =
                            ShipperOrderManagerFragment.instantiate(MainActivity.this,
                                    ShopOrderManagerFragment.class.getName(),
                                    null);
                    tag = ShipperOrderManagerFragment.class.getName();
                }
                break;
            case R.id.nav_shipper_management:
                fragment = ShipperManageFragment.newInstance();
                break;
            case R.id.nav_create_order:
                startActivity(new Intent(this, ShopCreateOrderActivity.class));
                return;
            case R.id.nav_sign_out:
                signOut();
            default:
                return;
        }
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment, tag)
                    .commit();
        }
    }

    private void signOut() {
        StorageUtils.clearAll(getApplicationContext());
        API.signOut(mCurrentUser.getAuthenticationToken(),
                mCurrentUser.getPhoneNumber(),
                new API.APICallback<APIResponse<EmptyData>>() {
                    @Override
                    public void onResponse(APIResponse<EmptyData> response) {
                        Toast.makeText(MainActivity.this, response.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_CHECK_SETTINGS) {
            getSupportFragmentManager()
                    .findFragmentByTag(MainContentFragment.class.getName())
                    .onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        // set double click back to exit
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.message_back_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, Const.TIME_DELAY_EXIT);
    }
}
