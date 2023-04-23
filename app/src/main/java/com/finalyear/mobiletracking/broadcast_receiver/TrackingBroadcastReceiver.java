
package com.finalyear.mobiletracking.broadcast_receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.finalyear.mobiletracking.app.MobileTrackingApp;
import com.finalyear.mobiletracking.background_service.TrackingJobIntentService;

public class TrackingBroadcastReceiver extends BroadcastReceiver {

    public static final String CUSTOM_INTENT = "com.mobiletracking.intent.action.ALARM";


    @Override
    public void onReceive(Context context, Intent intent) {
        /* enqueue the job */
        TrackingJobIntentService.enqueueWork(context, intent);
    }


    public static void cancelAlarm() {
        AlarmManager alarm = (AlarmManager) MobileTrackingApp.context.getSystemService(Context.ALARM_SERVICE);

        /* cancel any pending alarm */
        alarm.cancel(getPendingIntent());
    }


    public static void setAlarm(boolean force) {
        cancelAlarm();
        AlarmManager alarm = (AlarmManager)  MobileTrackingApp.context.getSystemService(Context.ALARM_SERVICE);        // EVERY X MINUTES
        long delay = (1000 * 60 * 4);
        long when = System.currentTimeMillis();
        if (!force) {
            when += delay;
        }

        /* fire the broadcast */
        alarm.set(AlarmManager.RTC_WAKEUP, when, getPendingIntent());
    }

    private static PendingIntent getPendingIntent() {
        // Context ctx;   /* get the application context */
        Intent alarmIntent = new Intent(MobileTrackingApp.context, TrackingBroadcastReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);

        return PendingIntent.getBroadcast(MobileTrackingApp.context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}