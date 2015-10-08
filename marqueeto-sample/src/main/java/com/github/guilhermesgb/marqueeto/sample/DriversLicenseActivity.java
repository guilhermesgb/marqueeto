package com.github.guilhermesgb.marqueeto.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.guilhermesgb.marqueeto.sample.event.DeleteLicenseEvent;
import com.github.guilhermesgb.marqueeto.sample.event.NewLicenseEvent;
import com.github.guilhermesgb.marqueeto.sample.event.UpdateLicenseEvent;
import com.joanzapata.iconify.IconDrawable;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import jonathanfinerty.once.Once;

public class DriversLicenseActivity extends AppCompatActivity {

    public static final String TAG_NO_SENSITIVE_DATA_WARNING = "noSensitiveDataWarning";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.collapsingToolbarLayout) CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.tabLayout) TabLayout mTabLayout;
    @Bind(R.id.viewPager) ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_license);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.header_toolbar_national_drivers_license));
        mViewPager.setAdapter(new LicensesViewPagerAdapter(this));
        mTabLayout.setupWithViewPager(mViewPager);
        if (!Once.beenDone(Once.THIS_APP_INSTALL, TAG_NO_SENSITIVE_DATA_WARNING)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title_notice))
                    .setMessage(getString(R.string.message_no_sensitive_data_warning))
                    .setPositiveButton(getString(R.string.dialog_understood),
                            new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Once.markDone(TAG_NO_SENSITIVE_DATA_WARNING);
                        }

                    }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_drivers_license_activity, menu);
        menu.getItem(0).setIcon(new IconDrawable(this, "md-note-add")
                .actionBarSize().colorRes(android.R.color.white));
        menu.getItem(1).setIcon(new IconDrawable(this, "md-settings")
                .actionBarSize().colorRes(android.R.color.white));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            mViewPager.setCurrentItem(0, true);
            return true;
        }
        else if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void onEvent(NewLicenseEvent event) {
        rebuildViewPager();
        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1, true);
    }

    public void onEvent(UpdateLicenseEvent event) {
        int currentPage = mViewPager.getCurrentItem();
        TabLayout.Tab currentTab = mTabLayout.getTabAt(currentPage);
        if (currentTab != null) {
            currentTab.setText(event.getNewTabName());
        }
    }

    public void onEvent(DeleteLicenseEvent event) {
        final int nextPage = mViewPager.getCurrentItem() - 1;
        rebuildViewPager();
        mViewPager.setCurrentItem(nextPage, true);
    }

    private void rebuildViewPager() {
        mViewPager.setAdapter(new LicensesViewPagerAdapter(this));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
