package com.akhgupta.easylocation;

import com.google.android.gms.location.LocationRequest;

public class EasyLocationRequest {
    private LocationRequest locationRequest;

    public EasyLocationRequest(LocationRequest locationRequest) {
        this.locationRequest = locationRequest;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }
}
