package com.finalyear.mobiletracking.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

public class Utils {

    public static String getDeviceIMEINumber(Context mContext) {


        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);


        if ((ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED)) {
            if (telephonyManager.getDeviceId() == null) {
                if (Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID) != null) {
                    return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                } else {
                    return "";
                }

            } else {
                return telephonyManager.getDeviceId();
            }
        } else {
            return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

    }

    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }
}
