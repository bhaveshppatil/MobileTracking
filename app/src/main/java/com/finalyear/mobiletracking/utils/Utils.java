package com.finalyear.mobiletracking.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

public class Utils {

    public static String getDeviceIMEINumber(Context mContext) {


        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);


      if((ContextCompat.checkSelfPermission(mContext,
              android.Manifest.permission.READ_PHONE_STATE)
              == PackageManager.PERMISSION_GRANTED)) {
          if (telephonyManager.getDeviceId() == null) {
              if( Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID)!=null){
                  return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
              }else {
                  return "";
              }

          } else {
              return telephonyManager.getDeviceId();
          }
      }else{
          return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
      }

    }
}
