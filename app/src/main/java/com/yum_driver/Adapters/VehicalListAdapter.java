package com.yum_driver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yum_driver.Activities.VehicalInfoActivity;
import com.yum_driver.Interfaces.VehicleObjectClick;
import com.yum_driver.Pojo.VehicalListObject;
import com.yum_driver.R;
import com.yum_driver.utils.SharedPreferenceManager;

import java.util.ArrayList;

public class VehicalListAdapter extends RecyclerView.Adapter<com.yum_driver.Adapters.VehicalListAdapter.ViewHolder> {
    private Context userInfo;
    private ArrayList<VehicalListObject> vehicalObjects;
    SharedPreferenceManager sharedPreferenceManager;
    private VehicleObjectClick vehicleObjectClick;


    public VehicalListAdapter(Context userInfo, ArrayList<VehicalListObject> vehicalObjects,VehicleObjectClick vehicleObjectClick) {
        this.vehicalObjects = vehicalObjects;
        this.userInfo = userInfo;
        this.vehicleObjectClick = vehicleObjectClick;
        sharedPreferenceManager = new SharedPreferenceManager(userInfo);

    }

    @NonNull
    @Override
    public VehicalListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_vehical_list, parent, false);
        VehicalListAdapter.ViewHolder myViewHolder = new VehicalListAdapter.ViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        sharedPreferenceManager.connectDB();
//        String vehicaleNo = sharedPreferenceManager.getString("defaultVehicalNo");
//        sharedPreferenceManager.closeDB();

        holder.txtBikeNo.setText(vehicalObjects.get(position).vehicle_regno);
        holder.txtOtherDetails.setText(vehicalObjects.get(position).vehicle_type + ","
                + vehicalObjects.get(position).vehicle_color + "," + vehicalObjects.get(position).vehicle_model
                + userInfo.getResources().getString(R.string.chesis_no) + vehicalObjects.get(position).chesis_no
        );

        if(vehicalObjects.get(position).is_default.equalsIgnoreCase("Yes")) {
            holder.imgSelect.setVisibility(View.VISIBLE);
            holder.imgCancel.setVisibility(View.GONE);
        } else {
            holder.imgSelect.setVisibility(View.GONE);
            holder.imgCancel.setVisibility(View.GONE);
        }

//        if(vehicalObjects.get(position).vehicle_status.equalsIgnoreCase("delete")) {
//            holder.imgSelect.setVisibility(View.GONE);
//            holder.imgCancel.setVisibility(View.VISIBLE);
//        } else {
//            holder.imgSelect.setVisibility(View.GONE);
//            holder.imgCancel.setVisibility(View.GONE);
//        }


        holder.txtBikeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(userInfo, VehicalInfoActivity.class);
                i.putExtra("vehicleObject", vehicalObjects.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                userInfo.startActivity(i);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicleObjectClick.onRecyclerViewListClicked(position, vehicalObjects.get(position),"select" );
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vehicleObjectClick.onRecyclerViewListClicked(position, vehicalObjects.get(position),"delete" );
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return vehicalObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtBikeNo, txtOtherDetails;
        ImageView imgSelect, imgCancel;
        LinearLayout llAll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtBikeNo = itemView.findViewById(R.id.txtBikeNo);
            txtOtherDetails = itemView.findViewById(R.id.txtOtherDetails);
            imgSelect = itemView.findViewById(R.id.imgSelect);
            imgCancel = itemView.findViewById(R.id.imgCancel);
            llAll = itemView.findViewById(R.id.llAll);


        }
    }
}