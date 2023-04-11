package com.finalyear.mobiletracking.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.activities.NearestLocationsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
  private Context context;
  private CustomMultiColorProgressBar progressBar;
    public GetNearbyPlacesData(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = new CustomMultiColorProgressBar(context,"Please Wait,\nGetting Nearest Locations");
        progressBar.showProgressBar();
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        progressBar.hideProgressBar();
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        if(nearbyPlacesList!=null&&!nearbyPlacesList.isEmpty()) {
            int count=0;
            if(nearbyPlacesList.size()>=10){
                count =10;
            }else{
                count=nearbyPlacesList.size();
            }

                for (int i = 0; i < count; i++) {
                    Log.d("onPostExecute", "Entered into showing locations");
                    MarkerOptions markerOptions = new MarkerOptions();
                    HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                    double lat = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lng")));
                    String placeName = googlePlace.get("place_name");
                    String vicinity = googlePlace.get("vicinity");
                    LatLng latLng = new LatLng(lat, lng);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);
                    mMap.addMarker(markerOptions);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createCustomMapMarker(placeName + " : " + vicinity)));
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                }

        }else{
            DialogUtils.showWarningDialog(context, "Near by locations not found");
        }
    }

    public Bitmap createCustomMapMarker( String tile) {

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Inflate the layout into a view and configure it the way you like
        LinearLayout view = new LinearLayout(context);
        mInflater.inflate(R.layout.pin_map_view, view, true);
        TextView tv = (TextView) view.findViewById(R.id.titleTextView);
        GradientDrawable drawable = (GradientDrawable) tv.getBackground();
        drawable.setColor(view.getResources().getColor(android.R.color.white));
        tv.setText(tile);


        //Provide it with a layout params. It should necessarily be wrapping the
        //content as we not really going to have a parent for it.
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        //Pre-measure the view so that height and width don't remain null.
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //Assign a size and position to the view and all of its descendants
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create the bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        //Create a canvas with the specified bitmap to draw into
        Canvas c = new Canvas(bitmap);

        //Render this view (and all of its children) to the given Canvas
        view.draw(c);


        return bitmap;
    }
}