package com.finalyear.mobiletracking.locations;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.finalyear.mobiletracking.app.MobileTrackingApp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*---------- Listener class to get coordinates ------------- */
public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v("MyLocationListener", latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(MobileTrackingApp.context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
