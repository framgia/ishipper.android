package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.fragment.ShopCreateOrderStep1Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopCreateOrderActivity extends AppCompatActivity {
    private static final String TAG = "ShopCreateOrderActivity";
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_create_order);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addFragment(new ShopCreateOrderStep1Fragment());

    }

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
        Log.d(TAG, "addFragment: " + fragment.getClass().getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
