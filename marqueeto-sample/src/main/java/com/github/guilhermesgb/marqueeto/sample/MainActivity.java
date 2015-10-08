package com.github.guilhermesgb.marqueeto.sample;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.guilhermesgb.marqueeto.sample.event.DeleteLicenseEvent;
import com.github.guilhermesgb.marqueeto.sample.event.NewLicenseEvent;
import com.github.guilhermesgb.marqueeto.sample.event.UpdateLicenseEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.collapsingToolbarLayout) CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.tabLayout) TabLayout mTabLayout;
    @Bind(R.id.viewPager) ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.header_toolbar_national_drivers_license));
        mViewPager.setAdapter(new LicensesViewPagerAdapter());
        mTabLayout.setupWithViewPager(mViewPager);
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
        rebuildViewPager();
        mViewPager.setCurrentItem(currentPage, false);
    }

    public void onEvent(DeleteLicenseEvent event) {
        final int nextPage = mViewPager.getCurrentItem() - 1;
        rebuildViewPager();
        mViewPager.setCurrentItem(nextPage, true);
    }

    private void rebuildViewPager() {
        mViewPager.setAdapter(new LicensesViewPagerAdapter());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
