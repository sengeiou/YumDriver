package com.yum_driver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yum_driver.Pojo.DriverReview;
import com.yum_driver.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<com.yum_driver.Adapters.ReviewAdapter.MyViewHolder> {
    Context context;
    public List<DriverReview> reviewList;

    public ReviewAdapter(Context context, List<DriverReview> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_review,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);

        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.ratingbar.setRating(Float.valueOf(reviewList.get(position).ratings));
        holder.txtDate.setText(reviewList.get(position).addedDate);
        holder.txtDetails.setText(reviewList.get(position).comments);

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtDetails;
        RatingBar ratingbar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingbar = itemView.findViewById(R.id.rtingbar);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDetails = itemView.findViewById(R.id.txtDetails);


        }
    }

}
