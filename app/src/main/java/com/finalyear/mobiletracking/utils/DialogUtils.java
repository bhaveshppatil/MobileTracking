package com.finalyear.mobiletracking.utils;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.activities.DashboardActivity;
import com.finalyear.mobiletracking.activities.LoginActivity;
import com.finalyear.mobiletracking.app.MobileTrackingApp;
import com.finalyear.mobiletracking.broadcast_receiver.TrackingBroadcastReceiver;

import java.util.Objects;

import static android.os.Build.VERSION.SDK_INT;

public class DialogUtils {



    public static void showWarningDialog(Context mContext, String msg) {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        dialog.setContentView(R.layout.dialog_warning);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.setCancelable(false);
        Button btn_ok;
        btn_ok = dialog.findViewById(R.id.btn_ok);

        TextView txt_msg = dialog.findViewById(R.id.txt_msg);

        txt_msg.setText(msg);

        if(msg.equalsIgnoreCase("Near by locations not found")){
            btn_ok.setText("Dismiss");
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        dialog.show();
    }
    public static void showSucessDialog(final Context mContext, String msg, final String comingFrom) {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        dialog.setContentView(R.layout.dialog_warning);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.setCancelable(false);
        Button btn_ok;
        btn_ok = dialog.findViewById(R.id.btn_ok);
       ImageView iv_warn = dialog.findViewById(R.id.iv_warn);
       iv_warn.setImageResource(R.drawable.ic_tick_mark);
        TextView txt_msg = dialog.findViewById(R.id.txt_msg);

        txt_msg.setText(msg);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comingFrom.equalsIgnoreCase(mContext.getString(R.string.login))){
                    DashboardActivity.start(mContext);
                }else{
                   // getLocationInBg();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(intent);
                }

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public static void showDesableBattreySaverDialog(final Context mContext) {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        dialog.setContentView(R.layout.dialog_warning);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.setCancelable(false);
        Button btn_ok;
        btn_ok = dialog.findViewById(R.id.btn_ok);
        ImageView iv_warn = dialog.findViewById(R.id.iv_warn);
        iv_warn.setImageResource(R.drawable.ic_battery);
        TextView txt_msg = dialog.findViewById(R.id.txt_msg);
        txt_msg.setText("Please set the battrey saver option as 'No restriction' for better performance");
        btn_ok.setText("OK");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent batterySaver = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                mContext.startActivity(batterySaver);
                dialog.dismiss();

            }
        });


        dialog.show();
    }

    private static void getLocationInBg(){
        AlarmManager alarmManager =( AlarmManager)MobileTrackingApp.context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(MobileTrackingApp.context, TrackingBroadcastReceiver.class);
        i.setAction("com.mobiletracking.intent.action.ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MobileTrackingApp.context, 0, i, 0);
        if (SDK_INT < Build.VERSION_CODES.KITKAT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        }


    }
}
