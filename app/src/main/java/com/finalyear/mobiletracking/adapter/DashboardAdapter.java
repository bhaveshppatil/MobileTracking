package com.finalyear.mobiletracking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.mobiletracking.R;
import com.finalyear.mobiletracking.interfaces.DashboardItemClickListener;
import com.finalyear.mobiletracking.model.DashboardMenuModel;

import java.util.ArrayList;


public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewsHolder> {
    private final Context mContext;
    private final ArrayList<DashboardMenuModel> dashboardMenuModels;
    private final DashboardItemClickListener listener;

    public DashboardAdapter(Context mContext, ArrayList<DashboardMenuModel> dashboardMenuModels, DashboardItemClickListener listener) {
        this.mContext = mContext;
        this.dashboardMenuModels = dashboardMenuModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewsHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_item_dashboard, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewsHolder holder, int i) {
        holder.item = dashboardMenuModels.get(i);
        holder.iv_icon.setImageResource(holder.item.getIcon());
        holder.txt_name.setText(holder.item.getMenuName());
    }

    @Override
    public int getItemCount() {
        return dashboardMenuModels.size();
    }

    public class ViewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_name;
        ImageView iv_icon;
        DashboardMenuModel item;

        public ViewsHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(item.getPos());
            }
        }
    }
}
