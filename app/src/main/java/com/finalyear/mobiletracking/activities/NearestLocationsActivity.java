package com.finalyear.mobiletracking.activities;

import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.utils.CustomMultiColorProgressBar;
import com.finalyear.mobiletracking.utils.DialogUtils;
import com.finalyear.mobiletracking.utils.NetworkUtils;
import android.location.LocationListener;
import android.widget.TextView;

public class NearestLocationsActivity  extends FragmentActivity {
    //OnMapReadyCallback,
    //        GoogleApiClient.ConnectionCallbacks,
    //        GoogleApiClient.OnConnectionFailedListener,
    public static void start(Context context, int comingFrom) {
        Intent starter = new Intent(context, NearestLocationsActivity.class);
        starter.putExtra("COMING_FROM", comingFrom);
        context.startActivity(starter);
    }


    private LocationManager locationManager;
    private CustomMultiColorProgressBar progressBar;

    private int comingFrom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_locations);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        comingFrom = getIntent().getIntExtra("COMING_FROM", -1);
        TextView textView = findViewById(R.id.txt_header);
        textView.setText(getHeader());
        progressBar = new CustomMultiColorProgressBar(NearestLocationsActivity.this, "Please Wait,\nGetting Nearest Locations");
        getLocation();
    }


    private String getHeader() {
        String which = "";
        switch (comingFrom) {
            case 4:
                which = "Near by Restaurants";
                break;
            case 5:
                which = "Near by Hospitals";
                break;
            case 6:
                which = "Near by Schools & Colleges";
                break;
        }
        return which;
    }

//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }

//    @Override
//    public void onConnected(Bundle bundle) {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        }
//    }

//    private String getUrl(double latitude, double longitude, String nearbyPlace) {
//
//        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        googlePlacesUrl.append("location=" + latitude + "," + longitude);
//        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
//        googlePlacesUrl.append("&type=" + nearbyPlace);
//        googlePlacesUrl.append("&sensor=true");
//        googlePlacesUrl.append("&key=" + "AIzaSyCoLoJvQPzXLZ5DRZfidpxHUB7_acv75OA");
//      //  googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
//        Log.d("getUrl", googlePlacesUrl.toString());
//        return (googlePlacesUrl.toString());
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }


    private String getMapData() {
        String which = "";
        switch (comingFrom) {
            case 4:
                which = "restaurants";
                break;
            case 5:
                which = "hospitals";
                break;
            case 6:
                which = "schools";
                break;
        }


        return which;
    }

    private void showProgress() {
        progressBar.showProgressBar();
    }

    private void dismissProgress() {
        progressBar.hideProgressBar();
    }

    void getLocation() {
        showProgress();
        try {
            if (NetworkUtils.isNetworkConnected(this)) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 5, locationListener);
            } else {
                dismissProgress();

            }

        } catch (SecurityException e) {
            e.printStackTrace();
            dismissProgress();
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                dismissProgress();
                if (location != null) {
                    if (locationManager != null) {
                        locationManager.removeUpdates(locationListener);
                        locationManager = null;
                    }
                    String url = "geo:" + String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()) + "?q=" + getMapData();
                    Uri gmmIntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                        finish();
                    } else {
                        //Please install a maps application
                        DialogUtils.showWarningDialog(NearestLocationsActivity.this, "Please install a google map application");
                    }
                } else {
                    DialogUtils.showWarningDialog(NearestLocationsActivity.this, "Not getting your current location");
                }
            } catch (Exception e) {
                e.printStackTrace();
                dismissProgress();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

    };


}
