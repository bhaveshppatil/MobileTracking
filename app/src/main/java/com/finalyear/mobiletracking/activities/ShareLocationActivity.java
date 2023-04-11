package com.finalyear.mobiletracking.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.utils.CustomMultiColorProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShareLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    com.github.clans.fab.FloatingActionButton fab_addr, fab_screen_shot;
    String address;
    private GoogleMap mMap;
    private CustomMultiColorProgressBar progressBar;

    public static void start(Context context) {
        Intent starter = new Intent(context, ShareLocationActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        try {
            progressBar = new CustomMultiColorProgressBar(this, "Please Wait\nGetting your Location");
            progressBar.showProgressBar();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {

            fab_addr = findViewById(R.id.fab_addr);
            fab_screen_shot = findViewById(R.id.fab_screen_shot);
            fab_addr.setOnClickListener(this);
            fab_screen_shot.setOnClickListener(this);
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            //Initialize Google Play Services
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        //hideProgressDialog();
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

//
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
            StringBuilder stringBuilder = new StringBuilder();
            try {

                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 2);
                if (null != listAddresses && listAddresses.size() > 0) {

                    if (listAddresses.get(0).getLocality() != null && !listAddresses.get(0).getLocality().isEmpty()) {
                        stringBuilder.append(listAddresses.get(0).getLocality()).append(",");
                    } else if (listAddresses.size() > 1) {
                        if (listAddresses.get(1).getLocality() != null && !listAddresses.get(1).getLocality().isEmpty()) {
                            stringBuilder.append(listAddresses.get(1).getLocality()).append(",");
                        }
                    }

                    if (listAddresses.get(0).getFeatureName() != null && !listAddresses.get(0).getFeatureName().isEmpty()) {

                        stringBuilder.append(listAddresses.get(0).getFeatureName()).append(",");
                    }

                    if (listAddresses.get(0).getAdminArea() != null && !listAddresses.get(0).getAdminArea().isEmpty()) {
                        stringBuilder.append(listAddresses.get(0).getAdminArea()).append(",");
                    } else if (listAddresses.size() > 1) {
                        if (listAddresses.get(1).getAdminArea() != null && !listAddresses.get(1).getAdminArea().isEmpty()) {
                            stringBuilder.append(listAddresses.get(1).getAdminArea()).append(",");
                        }
                    }

                    if (listAddresses.get(0).getPostalCode() != null && !listAddresses.get(0).getPostalCode().isEmpty()) {

                        stringBuilder.append(listAddresses.get(0).getPostalCode()).append(",");
                    }
                    if (listAddresses.get(0).getCountryName() != null && !listAddresses.get(0).getCountryName().isEmpty()) {

                        stringBuilder.append(listAddresses.get(0).getCountryName());
                    }

                    markerOptions.title(stringBuilder.toString()

                    );
                    address = stringBuilder.toString();
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createCustomMapMarker(stringBuilder.toString())));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                }
            } catch (IOException e) {
                e.printStackTrace();
                hideProgressDialog();
            }
        }

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
        hideProgressDialog();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        hideProgressDialog();
    }

    private void hideProgressDialog() {

        if (progressBar.isShowing()) {
            progressBar.hideProgressBar();
        }
    }

    public Bitmap createCustomMapMarker(String tile) {

        LayoutInflater mInflater = (LayoutInflater) ShareLocationActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Inflate the layout into a view and configure it the way you like
        LinearLayout view = new LinearLayout(ShareLocationActivity.this);
        mInflater.inflate(R.layout.pin_map_view, view, true);
        TextView tv = (TextView) view.findViewById(R.id.titleTextView);
        GradientDrawable drawable = (GradientDrawable) tv.getBackground();
        drawable.setColor(view.getResources().getColor(android.R.color.white));
        tv.setText(tile);


        //Provide it with a layout params. It should necessarily be wrapping the
        //content as we not really going to have a parent for it.
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        //Pre-measure the view so that height and width don't remain null.
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //Assign a size and position to the view and all of its descendants
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create the bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        //Create a canvas with the specified bitmap to draw into
        Canvas c = new Canvas(bitmap);

        //Render this view (and all of its children) to the given Canvas
        view.draw(c);


        return bitmap;
    }


    private void shareScreenshot() {
        Double latitude = mLastLocation.getLatitude();
        Double longitude = mLastLocation.getLongitude();

        String uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String ShareSub = "Here is my location";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void shareAddress(String address) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("text/plain");// Plain format text
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, address);
        startActivity(Intent.createChooser(sharingIntent, "Share Address Using"));
    }


//    public void captureScreen()
//    {
//        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
//        {
//
//            @Override
//            public void onSnapshotReady(Bitmap snapshot)
//            {
//                // TODO Auto-generated method stub
//             Bitmap   bitmap = snapshot;
//
//                OutputStream fout = null;
//
//                String filePath = String.valueOf(System.currentTimeMillis())+".png";
//                try
//                {
//                  //  filePath=file.getPath();
//                    fout = openFileOutput(filePath,
//                            Context.MODE_PRIVATE);
//
//                    // Write the string to the file
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
//                    fout.flush();
//                    fout.close();
//                }
//                catch (FileNotFoundException e)
//                {
//                    // TODO Auto-generated catch block
//                    Log.d("ImageCapture", "FileNotFoundException");
//                    Log.d("ImageCapture", e.getMessage());
//                    filePath = "";
//                }
//                catch (IOException e)
//                {
//                    // TODO Auto-generated catch block
//                    Log.d("ImageCapture", "IOException");
//                    Log.d("ImageCapture", e.getMessage());
//                    filePath = "";
//                }
//
//                shareScreenshot(filePath);
//            }
//        };
//
//        mMap.snapshot(callback);
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_screen_shot:
                shareScreenshot();
                break;
            case R.id.fab_addr:
                shareAddress(address);
                break;
        }
    }
}