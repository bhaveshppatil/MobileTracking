package com.finalyear.mobiletracking.broadcast_receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.finalyear.mobiletracking.background_service.TrackingJobIntentService;
import com.finalyear.mobiletracking.sharePref.SessionRepository;
import com.finalyear.mobiletracking.utils.Utils;

import static android.os.Build.VERSION.SDK_INT;

public class BootCompleteBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
        //TrackingJobIntentService.enqueueWork(context, intent);
        getLocationInBg(context);
       Toast.makeText(context, "Called", Toast.LENGTH_LONG).show();
//        Log.i("Calledddd","Test");
//        }
    }

    private void getLocationInBg(Context context) {
        String devIMEINo = SessionRepository.getInstance().getIMEI_NO();
        String currentIMEI = Utils.getDeviceIMEINumber(context);

        if (devIMEINo != null && currentIMEI != null) {

            if (devIMEINo.equalsIgnoreCase(currentIMEI)) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(context, TrackingBroadcastReceiver.class);
                i.setAction("com.mobiletracking.intent.action.ALARM");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
                if (SDK_INT < Build.VERSION_CODES.KITKAT)
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M)
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                else if (SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                }
            }
        }
    }
}
