package com.finalyear.mobiletracking.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.broadcast_receiver.TrackingBroadcastReceiver;
import com.finalyear.mobiletracking.sharePref.SessionRepository;
import com.finalyear.mobiletracking.utils.CustomMultiColorProgressBar;
import com.finalyear.mobiletracking.utils.DialogUtils;
import com.finalyear.mobiletracking.utils.NetworkUtils;
import com.finalyear.mobiletracking.model.RegistrationModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;
import static com.finalyear.mobiletracking.utils.IConstants.KEY_MOBILE_TRAKING;
import static com.finalyear.mobiletracking.utils.IConstants.REGISTRATION_DETAILS;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView cv_btn_log_in;
    private ShowHidePasswordEditText edt_password;
    private EditText edt_mobile_no;
    private CustomMultiColorProgressBar progressBar;
    private AppCompatCheckBox checkbox;;
    public static void start(Context context) {
      Intent starter = new Intent(context, LoginActivity.class);
//      starter.putExtra();
      context.startActivity(starter);
  }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }
    private void init() {
        Button txt_signup = findViewById(R.id.txt_signup);
        cv_btn_log_in = findViewById(R.id.cv_btn_log_in);
        edt_password = (ShowHidePasswordEditText)findViewById(R.id.edt_password);
        edt_mobile_no = findViewById(R.id.edt_mobile_no);
        checkbox = (AppCompatCheckBox) findViewById(R.id.checkbox);
        txt_signup.setOnClickListener(this);
        cv_btn_log_in.setOnClickListener(this);
        checkPermissions();
//        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (!isChecked) {
//                    // show password
//                    edt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                } else {
//                    // hide password
//                    edt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                }
//            }
//        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_btn_log_in:
                validateLogIn();
                break;
            case R.id.txt_signup:
                RegisterActivity.start(this);

                break;

        }
    }
    private void validateLogIn() {



        if (edt_mobile_no.getText().toString().isEmpty()) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_mobile_no));
            edt_mobile_no.requestFocus();
            return;
        } else if (edt_mobile_no.getText().toString().length() < 10) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_valid_mob_no));
            edt_mobile_no.requestFocus();
            return;
        }

        if (edt_password.getText().toString().isEmpty()) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_passw));
            edt_password.requestFocus();
            return;
        }
        if (edt_password.getText().toString().length() < 6) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_valid_paasw));
            edt_password.requestFocus();
            return;
        }


        if (NetworkUtils.isNetworkConnected(this)) {
            progressBar = new CustomMultiColorProgressBar(LoginActivity.this,"Please Wait");
            progressBar.showProgressBar();
            checkUserExist();

        } else {
            DialogUtils.showWarningDialog(this, getString(R.string.error__internet_unavailable));
        }
    }
    private void checkUserExist(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference userNameRef = rootRef.child(KEY_MOBILE_TRAKING).child(REGISTRATION_DETAILS).child(edt_mobile_no.getText().toString().trim());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.hideProgressBar();
                if(dataSnapshot.exists()) {
                    //create new user
                    try {
                        RegistrationModel model=  dataSnapshot.getValue(RegistrationModel.class);
                        if (model!=null) {
                            if(model.getPassword().equalsIgnoreCase(edt_password.getText().toString())){
                                SessionRepository.getInstance().storeName(model.getUserName());
                                SessionRepository.getInstance().storeMobileNo(model.getMobNumber());
                                SessionRepository.getInstance().storeEmailId(model.getEmailId());
                                SessionRepository.getInstance().storeIMEI_NO(model.getImeiNumber());
                                SessionRepository.getInstance().storeDeviceName(model.getDeviceName());
                                SessionRepository.getInstance().storeRegistrastionDate(model.getRegistrationDate());
                                SessionRepository.getInstance().storeLogoutSession(true);
                                DialogUtils.showSucessDialog(LoginActivity.this,getString(R.string.login_sucess),getString(R.string.login));
                            }else{
                                DialogUtils.showWarningDialog(LoginActivity.this, getString(R.string.invalidpassw));

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    DialogUtils.showWarningDialog(LoginActivity.this, getString(R.string.user_not_exist));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RegisterActivity", databaseError.getMessage()); //Don't ignore errors!
                progressBar.hideProgressBar();
                DialogUtils.showWarningDialog(LoginActivity.this, getString(R.string.something_went_wrong));
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);

    }

    private void checkPermissions(){
        final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        String rationale = "Please provide permissions";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                  CheckGpsStatus();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
            }
        });
    }

    public void CheckGpsStatus(){
        LocationManager  locationManager = (LocationManager)LoginActivity.this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
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

}
