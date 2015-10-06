package com.github.guilhermesgb.marqueeto.sample;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class ExamplesViewPagerAdapter extends PagerAdapter {

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.example_drivers_license, container, false);
        ButterKnife.bind(this, view);
        container.addView(view);
        return view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page #" + position;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
