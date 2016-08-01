package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Order;
import com.framgia.ishipper.ui.fragment.MainContentFragment;
import com.framgia.ishipper.ui.fragment.OrderListFragment;
import com.framgia.ishipper.ui.fragment.ShipperOrderManagerFragment;
import com.framgia.ishipper.ui.fragment.ShopOrderManagerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements OrderListFragment.OnListFragmentInteractionListener {
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.main_navigation) NavigationView mNavigationView;
    @BindView(R.id.main_drawer_layout) DrawerLayout mDrawerLayout;
    public static final int SHIPPER = 0;
    public static final int SHOP = 1;
    public static int userType = SHIPPER;

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

    }

    private void selectItem(int id) {
        mDrawerLayout.closeDrawer(mNavigationView);
        Fragment fragment;
        switch (id) {
            case R.id.nav_nearby_order:
                fragment = new MainContentFragment();
                mToolbar.setTitle(getString(R.string.nav_nearby_order_item));
                break;
            case R.id.nav_order_management:
                //TODO: show order fragment
                if (userType == SHIPPER) {
                    fragment =
                            ShipperOrderManagerFragment.instantiate(MainActivity.this,
                                    ShipperOrderManagerFragment.class.getName(),
                                    null);
                } else {
                    fragment =
                            ShipperOrderManagerFragment.instantiate(MainActivity.this,
                                    ShopOrderManagerFragment.class.getName(),
                                    null);
                }
                break;
            case R.id.nav_create_order:
//                fragment = new CreateOrderFragment();
                fragment = null;
                startActivity(new Intent(this, ShopCreateOrderActivity.class));
                break;
            case R.id.nav_sign_out:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            default:
                return;
        }
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
            mNavigationView.setCheckedItem(id);
        }
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
    public void onListFragmentInteraction(Order order) {
        //TODO: GO to Order details
        Log.d("onClick item fragment", order.getStartingAddress() + "null");
        startActivity(new Intent(this, OrderDetailActivity.class));
    }
}
