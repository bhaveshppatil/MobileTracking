package com.finalyear.mobiletracking.activities;


import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.sharePref.SessionRepository;

import static com.finalyear.mobiletracking.utils.IConstants.SPLASH_DISPLAY_LENGTH;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showSplashScreen();
    }

    private void showSplashScreen() {
        /* New Handler to start the Login-Activity
         * and close this Splash-Screen after 2 seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Login-Activity or DashboardActivity. */
                checkAlreadyLogin();
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkAlreadyLogin() {
        if(!SessionRepository.getInstance().getLogoutSession()){
            LoginActivity.start(SplashActivity.this);
        }else {
            DashboardActivity.start(this);
        }
    }
}
