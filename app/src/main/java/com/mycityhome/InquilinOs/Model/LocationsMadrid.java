package com.mycityhome.InquilinOs.Model;

import com.google.android.gms.maps.model.LatLng;

public class LocationsMadrid {
    String name;
    LatLng location;

    public LocationsMadrid(String name, LatLng location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

}
