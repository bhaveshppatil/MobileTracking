package com.finalyear.mobiletracking.activities;

import static com.finalyear.mobiletracking.utils.IConstants.KEY_MOBILE_TRAKING;
import static com.finalyear.mobiletracking.utils.IConstants.LOCATION_DETAILS;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.model.UserLocationModel;
import com.finalyear.mobiletracking.sharePref.SessionRepository;
import com.finalyear.mobiletracking.utils.CustomMultiColorProgressBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class LocationTrackerActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {


    Marker mCurrLocationMarker;
    private int childCount;
    private GoogleMap mMap;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private CustomMultiColorProgressBar progressBar;
    private ArrayList<UserLocationModel> aryListUserLocationModels;
    private SupportMapFragment mapFragment;
    private RelativeLayout rl_refresh, rl_last_update;
    private TextView txt_last_updated_location;
    private Button btn_get_all_locations;

    public static void start(Context context) {
        Intent starter = new Intent(context, LocationTrackerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracker);
        init();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    }

    private void init() {
        rl_refresh = findViewById(R.id.rl_refresh);
        rl_last_update = findViewById(R.id.rl_last_update);
        txt_last_updated_location = findViewById(R.id.txt_last_updated_location);
        btn_get_all_locations = findViewById(R.id.btn_get_all_locations);
        btn_get_all_locations.setOnClickListener(this);
        rl_refresh.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        getTrackedLocations();
    }

    private void getTrackedLocations() {
        progressBar = new CustomMultiColorProgressBar(LocationTrackerActivity.this, "Please Wait");
        progressBar.showProgressBar();
        aryListUserLocationModels = new ArrayList<>();
        childCount = 0;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(KEY_MOBILE_TRAKING).child(LOCATION_DETAILS).child(SessionRepository.getInstance().getMobileNo());


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

                    mapFragment.getMapAsync(LocationTrackerActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.hideProgressBar();
            }

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);


        if (aryListUserLocationModels != null && !aryListUserLocationModels.isEmpty()) {
            final ArrayList<LatLng> latLngArrayList = new ArrayList<>();

            sortByDate();

            for (int i = 0; i < aryListUserLocationModels.size(); i++) {
                latLngArrayList.add(new LatLng(aryListUserLocationModels.get(i).getLatitude(), aryListUserLocationModels.get(i).getLongitude()));
            }
//            for (int i = 0; i < latLngArrayList.size(); i++) {
//                String pinTitle =
//                        aryListUserLocationModels.get(i).getDate()+" "+aryListUserLocationModels.get(i).getTime();
//                MarkerOptions markerOptions = new MarkerOptions()
//                        .position(latLngArrayList.get(i));
//                 mCurrLocationMarker =  mMap.addMarker(markerOptions.icon(
//                        BitmapDescriptorFactory.fromBitmap(
//                                createCustomMapMarker(this,pinTitle))));
//
//
//            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.hideProgressBar();
                    for (int i = 0; i < latLngArrayList.size(); i++) {
                        String locTitle = aryListUserLocationModels.get(i).getUserName() + ", " + aryListUserLocationModels.get(i).getDate() + " " + aryListUserLocationModels.get(i).getTime();
                        mMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title(locTitle));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngArrayList.get(0)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14f));
                    rl_last_update.setVisibility(View.VISIBLE);
                    UserLocationModel userLocationModel = aryListUserLocationModels.get(0);
                    String lastUpdate = "<b>" + userLocationModel.getUserName() + "</b>" + "<br>" + userLocationModel.getLocation() + "<br>" + userLocationModel.getMobNumber() + "<br>" + userLocationModel.getDeviceName() + "<br>" + userLocationModel.getDate() + ", " + userLocationModel.getTime();
                    txt_last_updated_location.setText(Html.fromHtml(lastUpdate));
                }
            }, 2000);


        } else {
            progressBar.hideProgressBar();
            showWarningDialog(LocationTrackerActivity.this, getString(R.string.locations_not_found));
        }


    }

    private void sortByDate() {
        final SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
        Collections.sort(aryListUserLocationModels, new Comparator<UserLocationModel>() {
            @Override
            public int compare(UserLocationModel o1, UserLocationModel o2) {
                try {
                    return format.parse(o2.getDate() + " " + o2.getTime()).compareTo(format.parse(o1.getDate() + " " + o1.getTime()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);

                }
            }
        });
    }

    public Bitmap createCustomMapMarker(Context context, String tile) {

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Inflate the layout into a view and configure it the way you like
        LinearLayout view = new LinearLayout(context);
        mInflater.inflate(R.layout.pin_map_view, view, true);
        TextView tv = (TextView) view.findViewById(R.id.titleTextView);
        GradientDrawable drawable = (GradientDrawable) tv.getBackground();
        drawable.setColor(view.getResources().getColor(android.R.color.white));
        tv.setText(tile);


        //Provide it with a layout params. It should necessarily be wrapping the
        //content as we not really going to have a parent for it.
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        //Pre-measure the view so that height and width don't remain null.
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //Assign a size and position to the view and all of its descendants
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create the bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //Create a canvas with the specified bitmap to draw into
        Canvas c = new Canvas(bitmap);

        //Render this view (and all of its children) to the given Canvas
        view.draw(c);


        return bitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void showWarningDialog(Context mContext, String msg) {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        dialog.setContentView(R.layout.dialog_warning);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.setCancelable(false);
        Button btn_ok;
        btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setText("OK");

        TextView txt_msg = dialog.findViewById(R.id.txt_msg);

        txt_msg.setText(msg);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                finish();
            }
        });


        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rl_refresh) {
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            getTrackedLocations();
        } else if (view.getId() == R.id.btn_get_all_locations) {
            DetailsLocationsListActivity.start(this, "", aryListUserLocationModels);
        }
    }
}
