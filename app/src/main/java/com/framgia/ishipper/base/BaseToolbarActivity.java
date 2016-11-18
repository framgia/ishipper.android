package com.framgia.ishipper.base;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by HungNT on 8/3/16.
 */
public abstract class BaseToolbarActivity extends BaseActivity {
    private static final String TAG = "BaseToolbarActivity";

    public abstract Toolbar getToolbar();

    public abstract int getActivityTitle();

    public abstract int getLayoutId();

    protected void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getActivityTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
