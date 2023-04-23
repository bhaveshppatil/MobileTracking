package com.finalyear.mobiletracking.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.finalyear.mobiletracking.R;

public class ContactUsActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ContactUsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
    }
}
