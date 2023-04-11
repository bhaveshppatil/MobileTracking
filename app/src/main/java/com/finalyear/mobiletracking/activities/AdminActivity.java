package com.finalyear.mobiletracking.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.utils.DialogUtils;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_find;
    private EditText edt_mob_no;

    public static void start(Context context) {
        Intent starter = new Intent(context, AdminActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        init();


    }

    private void init() {
        edt_mob_no = findViewById(R.id.edt_mob_no);
        btn_find = findViewById(R.id.btn_find);
        btn_find.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_find:
                validate();
                break;
        }
    }

    private void validate() {
        if (edt_mob_no.getText().toString().trim().isEmpty()) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_mobile_no));
        } else if (edt_mob_no.getText().toString().trim().length() != 10) {
            DialogUtils.showWarningDialog(this, getString(R.string.warn_valid_mob_no));
        } else {
            DetailsLocationsListActivity.start(this, edt_mob_no.getText().toString().trim(), null);
        }
    }
}
