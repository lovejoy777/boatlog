package com.akhgupta.easylocation;

import com.google.android.gms.location.LocationRequest;

public class EasyLocationRequestBuilder {
    private LocationRequest locationRequest;

    public EasyLocationRequestBuilder setLocationRequest(LocationRequest locationRequest) {
        this.locationRequest = locationRequest;
        return this;
    }

    public EasyLocationRequestBuilder setFallBackToLastLocationTime(long time) {
        return this;
    }

    public EasyLocationRequest build() {
        return new EasyLocationRequest(locationRequest);
    }
}
