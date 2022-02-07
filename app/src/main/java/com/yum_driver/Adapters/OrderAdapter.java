package com.yum_driver.Adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.yum_driver.Interfaces.RecyclerViewClickListener;
import com.yum_driver.Pojo.OrderObject;
import com.yum_driver.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<com.yum_driver.Adapters.OrderAdapter.ViewHolder>{
    private Context context;
    private ArrayList<OrderObject> OrderObjectArrayList;
    private RecyclerViewClickListener recyclerViewClickListener;

    private long mLastClickTime = 0;
    private long WaitTime =1500;


    public OrderAdapter(Context context, ArrayList<OrderObject> OrderObjectArrayList, RecyclerViewClickListener recyclerViewClickListener) {
        this.context =context;
        this.OrderObjectArrayList = OrderObjectArrayList;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_item, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtordby.setText(""+OrderObjectArrayList.get(position).user_name);
        holder.txtordno.setText(""+OrderObjectArrayList.get(position).rst_name+"\n"+OrderObjectArrayList.get(position).rst_address);
        holder.txtdrop.setText(""+ OrderObjectArrayList.get(position).address1+" " );

//        String time[] = OrderObjectArrayList.get(position).time.split(" ");
//        holder.txtTime.setText(""+time[0]+":"+time[1]+" "+time[2]);
      holder.txtTime.setText(OrderObjectArrayList.get(position).order_date);
      holder.txtOrderNo.setText(OrderObjectArrayList.get(position).order_no);

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                recyclerViewClickListener.onRecyclerViewListClicked(position,true,"accept");
             //   recyclerViewClickListener.onRecyclerViewListClicked(position,true,"driver_accepted");
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                recyclerViewClickListener.onRecyclerViewListClicked(position,true,"reject");
            }
        });
        holder.llAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent().setClass(context, CurrentOrderDetails.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return OrderObjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtordno,txtordby,txtdrop,txtTime,txtOrderNo;
        Button btnAccept,btnReject;
        LinearLayout llAll;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime=itemView.findViewById(R.id.txt_time);
            txtordby=itemView.findViewById(R.id.txtx_orderby);
            txtordno=itemView.findViewById(R.id.txtx_orderno);
            txtdrop=itemView.findViewById(R.id.txtx_orderdrop);
            btnAccept=itemView.findViewById(R.id.btnAccept);
            btnReject=itemView.findViewById(R.id.btnReject);
            llAll=itemView.findViewById(R.id.llAll);
            txtOrderNo=itemView.findViewById(R.id.txtOrderNo);

        }
    }
}
