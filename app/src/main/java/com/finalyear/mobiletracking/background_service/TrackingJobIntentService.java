package com.finalyear.mobiletracking.background_service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.finalyear.mobiletracking.broadcast_receiver.TrackingBroadcastReceiver;
import com.finalyear.mobiletracking.constants.Logger;
import com.finalyear.mobiletracking.model.RegistrationModel;
import com.finalyear.mobiletracking.model.UserLocationModel;
import com.finalyear.mobiletracking.sharePref.SessionRepository;
import com.finalyear.mobiletracking.sqlite_db.SqLiteDBHelper;
import com.finalyear.mobiletracking.utils.CommonUtils;
import com.finalyear.mobiletracking.utils.NetworkUtils;
import com.finalyear.mobiletracking.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.finalyear.mobiletracking.utils.IConstants.KEY_MOBILE_TRAKING;
import static com.finalyear.mobiletracking.utils.IConstants.LOCATION_DETAILS;
import static com.finalyear.mobiletracking.utils.IConstants.REGISTRATION_DETAILS;

public class TrackingJobIntentService extends JobIntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;

    private static double ACCURANCY_LOCATION_FILTER_METERS = 200.0;
    private static double ACCURANCY_LOCATION_FROM_PERVIOUS_LOCATION = 200.0;
    private static int ACCURANCY_LOCATION_TIME_MINS = 5;
    private static int LOCATION_DETECTION_TIME_MINS = 5;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    /* Give the Job a Unique Id */
    private static final int JOB_ID = 1000;
    private static Context mContext;

    public static void enqueueWork(Context ctx, Intent intent) {
        mContext = ctx;
        enqueueWork(ctx, TrackingJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /* your code here */        /* reset the alarm */
        String text;
        //  try {
        Log.i("test", Calendar.getInstance().getTime().toString());

        startLocationService();

        TrackingBroadcastReceiver.setAlarm(false);
        stopSelf();
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.addLog("TrackingJobIntentService:  onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.addLog("TrackingJobIntentService:  onConnectionFailed");

    }

    private void createGoogleApi() {
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void startLocationService() {
        createGoogleApi();
        googleApiClient.connect();
    }

    /*Creating Location Request Object*/
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2 * 1000 * 60);// 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(4000 * 60); // 4 mins, in milliseconds
    }

    // Start location Updates
    private void startLocationUpdates() {

        if (checkPermission()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        } else {
            Log.e("LocationManager", "checkPermission denied");
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        SessionRepository.getInstance().storeCurrentLoc(getAddressFromLatLong(location.getLatitude(),location.getLongitude()));

        // Make a new directory/folder
        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/", ("TRACKING_APP_LOGS"));

        if (!directory.exists()) {
            directory.mkdir();
        }
        // make a new text file in that created new directory/folder
        File file = new File(directory.getPath(), "Tracking_Logs.txt");

        if (!file.exists() && directory.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            String text = Calendar.getInstance().getTime().toString()+ "  :"+getAddressFromLatLong(location.getLatitude(),location.getLongitude())+ "  :"+String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude());
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(("\r\n").getBytes());
            stream.write((text).getBytes());
            stream.close();
            UserLocationModel model = new UserLocationModel(
                    SessionRepository.getInstance().getName(),
                    SessionRepository.getInstance().getEmailId(),
                    SessionRepository.getInstance().getMobileNo(),
                    SessionRepository.getInstance().getIMEI_NO(),
                    CommonUtils.getDeviceName(), CommonUtils.getAndroidOs(),location.getLatitude(),location.getLongitude(),getAddressFromLatLong(location.getLatitude(),location.getLongitude()),CommonUtils.getCurrentDate(),CommonUtils.getCurrentTime());

            if(NetworkUtils.isNetworkConnected(getApplicationContext())){
                SqLiteDBHelper dbHelper =new SqLiteDBHelper(mContext);
                ArrayList<UserLocationModel> userLocationModelArrayList= dbHelper.getAllLocRecords();
                if(userLocationModelArrayList!=null&&!userLocationModelArrayList.isEmpty()){
                    userLocationModelArrayList.add(model);
                    FirebaseDatabase  mDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference  mDatabaseReference = mDatabase.getReference().child(KEY_MOBILE_TRAKING).child(LOCATION_DETAILS).child(SessionRepository.getInstance().getMobileNo()).child(CommonUtils.getCurrentDateKey());
                    int count = 0;
                    for (int i = 0; i < userLocationModelArrayList.size(); i++) {
                        count++;
                        mDatabaseReference.setValue(userLocationModelArrayList.get(i));
                        Logger.addLog(String.valueOf(userLocationModelArrayList.get(i).getLatitude())+","+String.valueOf(userLocationModelArrayList.get(i).getLatitude()));

                    }
                 if(count==userLocationModelArrayList.size()){
                        dbHelper.deleteAllRecords();
                    }

                }else{
                    FirebaseDatabase  mDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference  mDatabaseReference = mDatabase.getReference().child(KEY_MOBILE_TRAKING).child(LOCATION_DETAILS).child(SessionRepository.getInstance().getMobileNo()).child(CommonUtils.getCurrentDateKey());
                    mDatabaseReference.setValue(model);
                }



            }else{
                SqLiteDBHelper dbHelper =new SqLiteDBHelper(mContext);
                dbHelper.insertLocDetails(model);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("File creation ", "file");
        }
    }

    private String getAddressFromLatLong(double lat,double longt){
        StringBuilder stringBuilder = new StringBuilder();
        try {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, longt, 2); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if(addresses!=null&&!addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(returnedAddress.getAddressLine(i)).append(" ");
                }
                if(addresses.get(0).getLocality()!=null&&!addresses.get(0).getLocality().isEmpty()){
                    stringBuilder.append(addresses.get(0).getLocality()).append(",");
                }else if(addresses.size()>1){
                    if(addresses.get(1).getLocality()!=null&&!addresses.get(1).getLocality().isEmpty()){
                        stringBuilder.append(addresses.get(1).getLocality()).append(",");
                    }
                }

                if(addresses.get(0).getFeatureName()!=null&&!addresses.get(0).getFeatureName().isEmpty()){

                    stringBuilder.append(addresses.get(0).getFeatureName()).append(",");
                }
                if(addresses.get(0).getAdminArea()!=null&&!addresses.get(0).getAdminArea().isEmpty()){
                    stringBuilder.append(addresses.get(0).getAdminArea()).append(",");
                }else if(addresses.size()>1){
                    if(addresses.get(1).getAdminArea()!=null&&!addresses.get(1).getAdminArea().isEmpty()){
                        stringBuilder.append(addresses.get(1).getAdminArea()).append(",");
                    }
                }
                if(addresses.get(0).getPostalCode()!=null&&!addresses.get(0).getPostalCode().isEmpty()){

                    stringBuilder.append(addresses.get(0).getPostalCode()).append(",");
                }
                if(addresses.get(0).getCountryName()!=null&&!addresses.get(0).getCountryName().isEmpty()){

                    stringBuilder.append(addresses.get(0).getCountryName());
                }


                // stringBuilder.append(addresses.get(0).getAddressLine(0)) ;
                return stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "LOCATION NOT FOUND";
        }
         return stringBuilder.toString();
    }



}
