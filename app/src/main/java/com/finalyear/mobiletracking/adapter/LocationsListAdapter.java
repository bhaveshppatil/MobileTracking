package com.finalyear.mobiletracking.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.model.UserLocationModel;

import java.util.ArrayList;
import java.util.Locale;

public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.ViewsHolder> {
    private final Context mContext;
    private final ArrayList<UserLocationModel> locationModels;

    public LocationsListAdapter(Context mContext, ArrayList<UserLocationModel> locationModels) {
        this.mContext = mContext;
        this.locationModels = locationModels;
    }

    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewsHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_item_location_details, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewsHolder holder, int i) {
        holder.item = locationModels.get(i);
        holder.txt_address.setText(holder.item.getLocation());
        holder.txt_date_time.setText(String.format("%s %s", holder.item.getDate(), holder.item.getTime()));
        holder.btn_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDirections(holder.item.getLatitude(), holder.item.getLongitude());
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationModels.size();
    }

    private void getDirections(double lat, double longi) {

        String loc = lat + "," + longi;
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", lat, longi, "");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        Uri gmmIntentUri = Uri.parse("geo:" + loc);
        // Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "Google Map not installed in the device", Toast.LENGTH_SHORT).show();
        }
    }

    public class ViewsHolder extends RecyclerView.ViewHolder {
        UserLocationModel item;
        Button btn_directions;
        TextView txt_date_time,
                txt_address;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            btn_directions = itemView.findViewById(R.id.btn_directions);
            txt_date_time = itemView.findViewById(R.id.txt_date_time);
            txt_address = itemView.findViewById(R.id.txt_address);
        }
    }
}
