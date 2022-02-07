package com.yum_driver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yum_driver.Activities.CurrentOrderDetails;
import com.yum_driver.Pojo.CommisionObject;
import com.yum_driver.R;

import java.util.ArrayList;

public class RevenueListAdapter extends RecyclerView.Adapter<com.yum_driver.Adapters.RevenueListAdapter.ViewHolder> {
    private Context userInfo;
    private ArrayList<CommisionObject> vehicalObjects;


    public RevenueListAdapter(Context userInfo, ArrayList<CommisionObject> vehicalObjects) {
        this.vehicalObjects = vehicalObjects;
        this.userInfo = userInfo;


    }

    @NonNull
    @Override
    public RevenueListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_revenue_list, parent, false);
        RevenueListAdapter.ViewHolder myViewHolder = new RevenueListAdapter.ViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueListAdapter.ViewHolder holder, int position) {

        holder.txtRestName.setText(vehicalObjects.get(position).rst_name);
        holder.txtDate.setText(vehicalObjects.get(position).dateadded);

        if (vehicalObjects.get(position).commission_status.equals("Completed")) {
            holder.txtStatus.setTextColor(Color.parseColor("#68B4B7"));
            holder.txtStatus.setText(vehicalObjects.get(position).commission_status);

            holder.txtAmount.setTextColor(Color.parseColor("#000000"));
            holder.txtAmount.setText(userInfo.getResources().getString(R.string.dollar)+vehicalObjects.get(position).commission_amt);


        } else  {

            holder.txtStatus.setTextColor(Color.parseColor("#FF5A5F"));
            holder.txtStatus.setText(vehicalObjects.get(position).commission_status);

            holder.txtAmount.setTextColor(Color.parseColor("#ADADAD"));
            holder.txtAmount.setText(userInfo.getResources().getString(R.string.dollar)+vehicalObjects.get(position).commission_amt);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(vehicalObjects.get(position).commission_status.equals("Completed")) {
                   if(vehicalObjects.get(position).order_id != null) {
                       Intent i = new Intent(userInfo, CurrentOrderDetails.class);
                       i.putExtra("order_id", vehicalObjects.get(position).order_id);
                       i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       userInfo.startActivity(i);
                   }
               }
            }
        });



    }

    @Override
    public int getItemCount() {
        return vehicalObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtRestName, txtDate, txtStatus, txtAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRestName = itemView.findViewById(R.id.txtRestName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAmount = itemView.findViewById(R.id.txtAmount);


        }
    }
}
