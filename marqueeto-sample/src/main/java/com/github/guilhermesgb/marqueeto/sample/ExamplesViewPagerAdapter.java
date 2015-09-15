package com.github.guilhermesgb.marqueeto.sample;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.guilhermesgb.marqueeto.LabelledMarqueeEditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExamplesViewPagerAdapter extends PagerAdapter {

    @Bind(R.id.address_edit_text) LabelledMarqueeEditText addressField;
    @Bind(R.id.username_edit_text) LabelledMarqueeEditText usernameField;
    @Bind(R.id.work_edit_text) LabelledMarqueeEditText workField;

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_main_bak, container, false);
        ButterKnife.bind(this, view);
        addressField.setErrorEnabled(true);
        addressField.setError("ADDRESS WRONG!");
        usernameField.setErrorEnabled(false);
        workField.setErrorEnabled(true);
        workField.setError("Hey this is another error text!");
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
