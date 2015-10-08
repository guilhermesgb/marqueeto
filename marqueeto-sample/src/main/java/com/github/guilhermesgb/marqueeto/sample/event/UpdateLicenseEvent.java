package com.github.guilhermesgb.marqueeto.sample.event;

public class UpdateLicenseEvent {

    private String newTabName;

    public UpdateLicenseEvent(String newTabName) {
        this.newTabName = newTabName;
    }

    public String getNewTabName() {
        return newTabName;
    }

    public void setNewTabName(String newTabName) {
        this.newTabName = newTabName;
    }

}
