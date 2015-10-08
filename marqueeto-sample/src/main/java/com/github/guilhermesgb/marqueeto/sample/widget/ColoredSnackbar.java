package com.github.guilhermesgb.marqueeto.sample.widget;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.github.guilhermesgb.marqueeto.sample.R;

public class ColoredSnackbar {

    public static Snackbar alert(Snackbar snackbar) {
        View snackbarView = snackbar.getView();
        if (snackbarView != null) {
            snackbarView.setBackgroundResource(R.color.primary_dark);
        }
        return snackbar;
    }

}
