package com.example.weatherdisplay;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/*---------- Listener class to get coordinates ------------- */
public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {
        String longitude = "Longitude: " + loc.getLongitude();
        Log.v("TAG", longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v("TAG", latitude);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
