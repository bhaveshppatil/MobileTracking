package com.finalyear.mobiletracking.activities;

import static com.finalyear.mobiletracking.utils.IConstants.KEY_MOBILE_TRAKING;
import static com.finalyear.mobiletracking.utils.IConstants.LOCATION_DETAILS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.adapter.LocationsListAdapter;
import com.finalyear.mobiletracking.model.UserLocationModel;
import com.finalyear.mobiletracking.utils.CustomMultiColorProgressBar;
import com.finalyear.mobiletracking.utils.DialogUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailsLocationsListActivity extends AppCompatActivity {
    private RecyclerView rv_location_list;
    private TextView txt_android_os;
    private TextView txt_device_name;
    private TextView txt_mobile_no;
    private TextView txt_user_name;
    private String strMobileNo;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private CustomMultiColorProgressBar progressBar;
    private ArrayList<UserLocationModel> aryListUserLocationModels;
    private int childCount;
    private LinearLayout ll_user_details;

    public static void start(Context context, String mob_no, ArrayList<UserLocationModel> aryListUserLocationModels) {
        Intent starter = new Intent(context, DetailsLocationsListActivity.class);
        starter.putExtra("KEY_MOBILE_NO", mob_no);
        starter.putParcelableArrayListExtra("KEY_LOC_LIST", aryListUserLocationModels);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_locations_list);
        init();

    }

    private void init() {
        strMobileNo = getIntent().getStringExtra("KEY_MOBILE_NO");
        aryListUserLocationModels = getIntent().getParcelableArrayListExtra("KEY_LOC_LIST");
        rv_location_list = findViewById(R.id.rv_location_list);
        txt_android_os = findViewById(R.id.txt_android_os);
        txt_device_name = findViewById(R.id.txt_device_name);
        txt_mobile_no = findViewById(R.id.txt_mobile_no);
        txt_user_name = findViewById(R.id.txt_user_name);
        ll_user_details = findViewById(R.id.ll_user_details);
        if (aryListUserLocationModels == null) {
            callToGetUserLocations();
        } else {
            setLocationDetails();
        }

    }

    private void callToGetUserLocations() {

        progressBar = new CustomMultiColorProgressBar(DetailsLocationsListActivity.this, "Please Wait");
        progressBar.showProgressBar();
        aryListUserLocationModels = new ArrayList<>();
        childCount = 0;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(KEY_MOBILE_TRAKING).child(LOCATION_DETAILS).child(strMobileNo);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    childCount++;
                    UserLocationModel post = postSnapshot.getValue(UserLocationModel.class);

                    aryListUserLocationModels.add(post);

                }
                if (childCount == snapshot.getChildrenCount()) {
                    setLocationDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.hideProgressBar();
            }

        });

    }

    private void setLocationDetails() {
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.hideProgressBar();
        }
        if (aryListUserLocationModels.isEmpty()) {
            DialogUtils.showWarningDialog(this, "Location details not found");
            ll_user_details.setVisibility(View.GONE);
        } else {
            ll_user_details.setVisibility(View.VISIBLE);
            txt_user_name.setText(aryListUserLocationModels.get(0).getUserName());
            txt_mobile_no.setText(aryListUserLocationModels.get(0).getMobNumber());
            txt_device_name.setText(aryListUserLocationModels.get(0).getDeviceName());
            txt_android_os.setText(aryListUserLocationModels.get(0).getAndroidOs());
            LocationsListAdapter locationsListAdapter = new LocationsListAdapter(this, aryListUserLocationModels);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            rv_location_list.setLayoutManager(manager);
            rv_location_list.setAdapter(locationsListAdapter);

        }
    }

}
