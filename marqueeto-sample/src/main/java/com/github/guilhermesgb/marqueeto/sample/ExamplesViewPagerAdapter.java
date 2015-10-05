package com.github.guilhermesgb.marqueeto.sample;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.guilhermesgb.marqueeto.LabelledMarqueeEditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExamplesViewPagerAdapter extends PagerAdapter {

    @Bind(R.id.website_edit_text) LabelledMarqueeEditText websiteField;
    @Bind(R.id.address_edit_text) LabelledMarqueeEditText addressField;
    @Bind(R.id.username_edit_text) LabelledMarqueeEditText usernameField;
    @Bind(R.id.work_edit_text) LabelledMarqueeEditText workField;
    @Bind(R.id.home_edit_text) LabelledMarqueeEditText homeField;
    @Bind(R.id.identity_number_edit_text) LabelledMarqueeEditText identityNumberField;

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_main_bak, container, false);
        ButterKnife.bind(this, view);
        websiteField.setCustomStyle(R.style.LabelledMarqueeEditTextCustomStyle2);
        websiteField.setText(websiteField.getText() + " :)");
        websiteField.setTextColor(R.color.material_deep_teal_500);
        websiteField.setHint(websiteField.getHint() + " :)");
        websiteField.setError(websiteField.getError() + " :)");
        //websiteField.setInputType(InputType.TYPE_CLASS_PHONE);
        websiteField.setIcon("md-access-alarms");
        //websiteField.setMode(LabelledMarqueeEditText.MODE_EDIT);
        //addressField.setErrorEnabled(true);
        addressField.setError("ADDRESS WRONG!");
        usernameField.setErrorEnabled(false);
        //workField.setErrorEnabled(true);
        workField.setError("Hey this is another error text!");
        container.addView(view);
        homeField.setCustomStyle(R.style.LabelledMarqueeEditTextCustomStyle2);
        identityNumberField.setText("12345678901234567890");
        //websiteField.setMode(LabelledMarqueeEditText.MODE_MARQUEE);
        //websiteField.setMode(LabelledMarqueeEditText.MODE_EDIT);
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
