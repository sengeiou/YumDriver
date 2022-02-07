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
import com.yum_driver.utils.MyTextView;
import com.yum_driver.utils.MyTextViewBold;

import java.util.ArrayList;


public class CloseItemListAdpter extends RecyclerView.Adapter<CloseItemListAdpter.ViewHolder> {
    private Context userInfo;
    private ArrayList<OrderItemDetails> itemDetailsArrayList;
    private String rst_id;
    private int h;
    private int w;
    private int ITEM_ADD_CODE =1;
    public CloseItemListAdpter(Context userInfo, ArrayList<OrderItemDetails> itemDetailsArrayList) {
        this.itemDetailsArrayList =itemDetailsArrayList;
        this.userInfo =userInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_items, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtFoodName.setText("("+itemDetailsArrayList.get(position).item_quantity+"x)"+itemDetailsArrayList.get(position).item_name);
        holder.txtDesc.setText(""+itemDetailsArrayList.get(position).strAddons);
        holder.txtPrice.setText(userInfo.getResources().getString(R.string.dollar)+""+itemDetailsArrayList.get(position).selling_price+"");
        holder.txtTotal.setText(userInfo.getResources().getString(R.string.dollar)+""+itemDetailsArrayList.get(position).item_plus_addon_price+"");
        holder.txtAddonsPrize.setText(userInfo.getResources().getString(R.string.addons)+" "+userInfo.getResources().getString(R.string.dollar)+""+itemDetailsArrayList.get(position).addons_price+"");

        if(itemDetailsArrayList.get(position).strAddons.equals(""))
        {
            holder.txtPrice.setVisibility(View.GONE);
            holder.txtDesc.setVisibility(View.GONE);
            holder.txtAddonsPrize.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return itemDetailsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MyTextViewBold txtFoodName,txtTotal;
        TextView txtPrice;
        MyTextView txtDesc,txtAddonsPrize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtAddonsPrize = itemView.findViewById(R.id.txtAddonsPrize);
            txtTotal = itemView.findViewById(R.id.txtTotal);


        }
    }
}