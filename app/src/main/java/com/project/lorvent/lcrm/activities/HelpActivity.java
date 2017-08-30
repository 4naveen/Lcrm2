package com.project.lorvent.lcrm.activities;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.fragments.admin.feedback.HelpFragment;
import com.project.lorvent.lcrm.utils.NetworkStatus;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        if (!NetworkStatus.isConnected(HelpActivity.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            finish();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Help Center");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Fragment fragment1=new HelpFragment();
        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
