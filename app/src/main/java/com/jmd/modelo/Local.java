package com.jmd.modelo;

import android.location.Location;

public class Local {
    private double latitude;
    private double longitude;

    public Local() {}

    public Local (Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
