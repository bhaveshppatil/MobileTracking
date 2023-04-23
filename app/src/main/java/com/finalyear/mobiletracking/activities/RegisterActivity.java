package com.finalyear.mobiletracking.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.utils.CommonUtils;
import com.finalyear.mobiletracking.utils.CustomMultiColorProgressBar;
import com.finalyear.mobiletracking.utils.DialogUtils;
import com.finalyear.mobiletracking.utils.NetworkUtils;
import com.finalyear.mobiletracking.utils.Utils;
import com.finalyear.mobiletracking.model.RegistrationModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.finalyear.mobiletracking.utils.IConstants.KEY_MOBILE_TRAKING;
import static com.finalyear.mobiletracking.utils.IConstants.REGISTRATION_DETAILS;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {



    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterActivity.class);
        context.startActivity(starter);
    }

    private EditText edt_password, edt_mob_no, edt_email, edt_name;
    private Button txt_login;
    private CardView cv_btn_sign_up;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private CustomMultiColorProgressBar progressBar;
    ArrayList<RegistrationModel> registrationModelArrayList;
    private boolean isUserCreated=false;
    int childCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        edt_password = findViewById(R.id.edt_password);
        edt_mob_no = findViewById(R.id.edt_mob_no);
        edt_email = findViewById(R.id.edt_email);
        edt_name = findViewById(R.id.edt_name);
        txt_login = findViewById(R.id.txt_login);
        cv_btn_sign_up = findViewById(R.id.cv_btn_sign_up);
        txt_login.setOnClickListener(this);
        cv_btn_sign_up.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_login:
                callLoginActivity();
                break;

            case R.id.cv_btn_sign_up:
                validateSignUp();
                break;


        }

    }

    private void callLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void validateSignUp() {
        if (edt_name.getText().toString().isEmpty()) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_mr_name));
            edt_name.requestFocus();
            return;
        }

        if (edt_email.getText().toString().isEmpty()) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_email));
            edt_email.requestFocus();
            return;
        }
        if (!CommonUtils.validateEmail(edt_email.getText().toString())) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_valid_email));
            edt_email.requestFocus();
            return;
        }


        if (edt_mob_no.getText().toString().isEmpty()) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_mobile_no));
            edt_mob_no.requestFocus();
            return;
        } else if (edt_mob_no.getText().toString().length() < 10) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_valid_mob_no));
            edt_mob_no.requestFocus();
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
            progressBar = new CustomMultiColorProgressBar(RegisterActivity.this, "Please Wait");
            progressBar.showProgressBar();

            checkDeviceIsAlreadyRegistered();
          //  checkUserExist();

        } else {
            DialogUtils.showWarningDialog(this, getString(R.string.error__internet_unavailable));
        }
    }

    private void checkDeviceIsAlreadyRegistered() {
         registrationModelArrayList = new ArrayList<>();
         childCount =0;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(KEY_MOBILE_TRAKING).child(REGISTRATION_DETAILS);


        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    childCount++;
                    RegistrationModel post = postSnapshot.getValue(RegistrationModel.class);

                   registrationModelArrayList.add(post);

                }
                if(childCount==snapshot.getChildrenCount()) {
                    checkUserExist();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               progressBar.hideProgressBar();
            }

        });

    }

    private void checkUserExist() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference userNameRef = rootRef.child(KEY_MOBILE_TRAKING).child(REGISTRATION_DETAILS).child(edt_mob_no.getText().toString().trim());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.hideProgressBar();
                if (!dataSnapshot.exists()) {
                    boolean isFound =false;
                    if(registrationModelArrayList!=null){
                        for (int i = 0; i < registrationModelArrayList.size(); i++) {
                          if(registrationModelArrayList.get(i).getImeiNumber().equalsIgnoreCase(Utils.getDeviceIMEINumber(RegisterActivity.this))){
                              isFound =true;
                             break;
                          }
                        }

                        if(isFound){
                            DialogUtils.showWarningDialog(RegisterActivity.this, getString(R.string.user_exist_with_device));
                        }else{
                            createNewUser();
                        }
                    }else{
                        createNewUser();
                    }
                    //create new user

                } else {
                    if(!isUserCreated) {
                        DialogUtils.showWarningDialog(RegisterActivity.this, getString(R.string.user_exist));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RegisterActivity", databaseError.getMessage()); //Don't ignore errors!
                progressBar.hideProgressBar();
                DialogUtils.showWarningDialog(RegisterActivity.this, getString(R.string.something_went_wrong));
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);

    }

    private void createNewUser() {
        mDatabaseReference = mDatabase.getReference().child(KEY_MOBILE_TRAKING).child(REGISTRATION_DETAILS).child(edt_mob_no.getText().toString().trim());
        RegistrationModel model = new RegistrationModel(
                edt_name.getText().toString(),
                edt_email.getText().toString(),
                edt_password.getText().toString(),
                edt_mob_no.getText().toString(),
                Utils.getDeviceIMEINumber(this), CommonUtils.getDeviceName(), CommonUtils.getAndroidOs(),CommonUtils.getCurrentDate());
        mDatabaseReference.setValue(model);
        DialogUtils.showSucessDialog(RegisterActivity.this, getString(R.string.registration_success), getString(R.string.signup));
          isUserCreated= true;

    }

}
