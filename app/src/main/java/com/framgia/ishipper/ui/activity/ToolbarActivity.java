package com.framgia.ishipper.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by HungNT on 8/3/16.
 */
public abstract class ToolbarActivity extends AppCompatActivity {

    abstract Toolbar getToolbar();
    abstract int getActivityTitle();

    protected void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getActivityTitle());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setToolbar(getToolbar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
