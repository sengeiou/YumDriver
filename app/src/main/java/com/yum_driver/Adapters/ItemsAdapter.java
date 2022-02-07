package com.yum_driver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.yum_driver.Pojo.OrderItemDetails;
import com.yum_driver.R;

import java.util.ArrayList;


public class ItemsAdapter extends RecyclerView.Adapter<com.yum_driver.Adapters.ItemsAdapter.ViewHolder> {
    private Context userInfo;
    private ArrayList<OrderItemDetails> itemDetailsArrayList;


    public ItemsAdapter(Context userInfo, ArrayList<OrderItemDetails> itemDetailsArrayList) {
        this.itemDetailsArrayList = itemDetailsArrayList;
        this.userInfo = userInfo;

        System.out.println("itemDetailsArrayList : " + itemDetailsArrayList.size());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_item_list, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtQty.setText("(" + itemDetailsArrayList.get(position).item_quantity + "x)");
        holder.txtItemName.setText(itemDetailsArrayList.get(position).item_name);

        if (itemDetailsArrayList.get(position).item_desc.equals("")) {
            holder.txtDescption.setVisibility(View.GONE);
        }
        else {
            holder.txtDescption.setText("" + itemDetailsArrayList.get(position).item_desc);
        }

    }

    @Override
    public int getItemCount() {
        return itemDetailsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtQty, txtItemName, txtDescption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtQty = itemView.findViewById(R.id.txtQty);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            txtDescption = itemView.findViewById(R.id.txtDescption);


        }
    }
}
