package com.akhgupta.easylocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public abstract class EasyLocationAppCompatActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        onLocationReceived(location);
                    }
                }
            }
        };
    }

    protected void requestLocationUpdates(EasyLocationRequest request) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            long interval = 2000;
            long fastestInterval = 1000;

            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(interval)
                    .setFastestInterval(fastestInterval);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    onLocationReceived(location);
                }
            });
        }
    }

    protected void requestSingleLocationFix(EasyLocationRequest request) {
        requestLocationUpdates(request);
    }

    public abstract void onLocationReceived(Location location);

    public void onLocationPermissionGranted() {}
    public void onLocationPermissionDenied() {}
    public void onLocationProviderEnabled() {}
    public void onLocationProviderDisabled() {}

    public void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}
