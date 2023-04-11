package com.finalyear.mobiletracking.activities;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.adapter.DashboardAdapter;
import com.finalyear.mobiletracking.broadcast_receiver.TrackingBroadcastReceiver;
import com.finalyear.mobiletracking.interfaces.DashboardItemClickListener;
import com.finalyear.mobiletracking.model.DashboardMenuModel;
import com.finalyear.mobiletracking.sharePref.SessionRepository;
import com.finalyear.mobiletracking.utils.DialogUtils;
import com.finalyear.mobiletracking.utils.IConstants;
import com.finalyear.mobiletracking.utils.NetworkUtils;
import com.finalyear.mobiletracking.utils.Utils;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity implements DashboardItemClickListener, View.OnClickListener {
    private TextView txt_user_name,
            txt_location;
    private ImageView iv_admin, iv_contact_us, iv_logout;

    public static void start(Context context) {
        Intent starter = new Intent(context, DashboardActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init();

    }

    private void init() {
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_location = findViewById(R.id.txt_location);
        iv_admin = findViewById(R.id.iv_admin);
        iv_contact_us = findViewById(R.id.iv_contact_us);
        iv_logout = findViewById(R.id.iv_logout);
        iv_admin.setOnClickListener(this);
        iv_contact_us.setOnClickListener(this);
        iv_logout.setOnClickListener(this);
        if (IConstants.IS_ADMIN_LOGIN) {
            iv_admin.setVisibility(View.VISIBLE);
        } else {
            iv_admin.setVisibility(View.GONE);
        }
        txt_user_name.setText(SessionRepository.getInstance().getName());
        setTrackingLocDashboardListItems();
        if (!SessionRepository.getInstance().getCurrentLocation().equalsIgnoreCase("na")) {
            txt_location.setText(SessionRepository.getInstance().getCurrentLocation());
        }
        requestLocation();


    }

    private void setTrackingLocDashboardListItems() {
        ArrayList<DashboardMenuModel> dashboardMenuModels = new ArrayList<>();
        dashboardMenuModels.add(new DashboardMenuModel("Current Location", R.drawable.location, 1));
        dashboardMenuModels.add(new DashboardMenuModel("Share Location", R.drawable.send, 2));
        dashboardMenuModels.add(new DashboardMenuModel("Track Location", R.drawable.track, 3));
        RecyclerView rvDashboard = findViewById(R.id.rv_dashboard_tracking);
        rvDashboard.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        DashboardAdapter adapter = new DashboardAdapter(this, dashboardMenuModels, this);
        rvDashboard.setLayoutManager(gridLayoutManager);
        rvDashboard.setAdapter(adapter);
    }

    private void requestLocation() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        String rationale = "Please provide permissions";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                CheckGpsStatus();
                // Toast.makeText(MainActivity.this, "Location granted.", Toast.LENGTH_SHORT).show();
                if (Utils.getDeviceId(DashboardActivity.this).equalsIgnoreCase(SessionRepository.getInstance().getIMEI_NO())) {

                    getLocationInBg();
                }

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                //   Toast.makeText(MainActivity.this, "Location denied.", Toast.LENGTH_SHORT).show();
                //  showPermissDialog();
            }
        });
    }


    private void getLocationInBg() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, TrackingBroadcastReceiver.class);
        i.setAction("com.mobiletracking.intent.action.ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        if (SDK_INT < Build.VERSION_CODES.KITKAT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        }
    }


    @Override
    public void onItemClick(int pos) {
        if (NetworkUtils.isNetworkConnected(this)) {
            if (CheckGpsStatus()) {
                if (pos == 1) {//current location
                    CurrentLocationMapsActivity.start(this);
                } else if (pos == 2) {//share
                    ShareLocationActivity.start(this);
                } else if (pos == 3) {//track location
                    LocationTrackerActivity.start(this);
                }
            } else {
                showGPSDisabledAlertToUser();
            }
        } else {
            DialogUtils.showWarningDialog(this, getString(R.string.error__internet_unavailable));
        }

    }


    private boolean CheckGpsStatus() {
        LocationManager locationManager = (LocationManager) DashboardActivity.this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please click on 'Allow' button to activate" +
                        " location setting and select mode 'High Accuracy'")
                .setCancelable(false)
                .setPositiveButton("Allow",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_admin:
                AdminActivity.start(this);
                break;
            case R.id.iv_contact_us:
                ContactUsActivity.start(this);
                break;
            case R.id.iv_logout:
                handleLogout();
                break;
        }
    }

    private void handleLogout() {
        SessionRepository.getInstance().storeLogoutSession(false);
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
