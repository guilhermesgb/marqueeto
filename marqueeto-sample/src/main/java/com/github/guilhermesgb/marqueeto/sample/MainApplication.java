package com.github.guilhermesgb.marqueeto.sample;

import com.activeandroid.app.Application;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;

import jonathanfinerty.once.Once;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Once.initialise(this);
        Iconify.with(new MaterialModule());
    }

}
