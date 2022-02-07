package com.yum_driver.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.yum_driver.Activities.BaseActivity;
import com.yum_driver.Pojo.NotificationObject;
import com.yum_driver.R;
import com.yum_driver.utils.MyTextViewBold;

import java.util.ArrayList;


public class NotificationAdapter extends RecyclerView.Adapter<com.yum_driver.Adapters.NotificationAdapter.ViewHolder> {
    private BaseActivity userInfo;
    private ArrayList<NotificationObject> notificationObjectArrayList;
    private int h;
    private int w;
    public NotificationAdapter(BaseActivity userInfo, ArrayList<NotificationObject> notificationObjectArrayList) {
        this.userInfo =userInfo;
        this.notificationObjectArrayList =notificationObjectArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notifications, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Glide.with(userInfo)
                .load(R.drawable.ic_email)
                .into(holder.imgRestaurant);

        holder.txtTitle.setText(""+notificationObjectArrayList.get(position).noti_title);
        holder.txtDescription.setText(""+notificationObjectArrayList.get(position).noti_message+"");



        if(!notificationObjectArrayList.get(position).dateadded.equals("")) {
            String[] datetime = notificationObjectArrayList.get(position).dateadded.split(" ");

            holder.txtDate.setText("" +notificationObjectArrayList.get(position).dateadded);
        }


        h = userInfo.metrices.heightPixels;
        w = userInfo.metrices.widthPixels;

    }

    @Override
    public int getItemCount() {
        return notificationObjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRestaurant;

        MyTextViewBold txtTitle;
        TextView txtDate;
        TextView txtDescription;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}
