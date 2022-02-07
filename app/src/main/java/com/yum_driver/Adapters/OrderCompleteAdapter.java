package com.yum_driver.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yum_driver.Pojo.OrderObject;
import com.yum_driver.R;
import com.yum_driver.utils.MyTextView;
import com.yum_driver.utils.SharedPreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class OrderCompleteAdapter extends RecyclerView.Adapter<OrderCompleteAdapter.ViewHolder>{
    private Context context;
    private ArrayList<OrderObject> OrderObjectArrayList;
    private SharedPreferenceManager preferenceManager;
    Dialog dialog;

    private long mLastClickTime = 0;
    private long WaitTime =1500;

    public OrderCompleteAdapter(Context context, ArrayList<OrderObject> OrderObjectArrayList) {
        dialog= new Dialog(context);
        this.context =context;
        this.OrderObjectArrayList = OrderObjectArrayList;
        preferenceManager = new SharedPreferenceManager(context);
    }

    @NonNull
    @Override
    public OrderCompleteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_close_order, parent, false);
        OrderCompleteAdapter.ViewHolder myViewHolder = new OrderCompleteAdapter.ViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderCompleteAdapter.ViewHolder holder, final int position) {


        holder.txtTime.setText("Time : "+OrderObjectArrayList.get(position).order_date);
        holder.txtCustomerName.setText(OrderObjectArrayList.get(position).user_name);
        holder.txtAddress.setText(OrderObjectArrayList.get(position).rst_address);
        holder.txtRestName.setText(OrderObjectArrayList.get(position).rst_name);




        float total = Float.parseFloat(OrderObjectArrayList.get(position).total_selling_price) + Float.parseFloat(OrderObjectArrayList.get(position).delivery_charges);

      //  holder.txtOrderAmount.setText(context.getResources().getString(R.string.paid)+context.getResources().getString(R.string.dollar)+" "+total);
//        holder.txtOrderAmount.setText(context.getResources().getString(R.string.paid)+context.getResources().getString(R.string.dollar)+" "+String.format("%.2f",total));
        holder.txtOrderAmount.setText(context.getResources().getString(R.string.paid)+OrderObjectArrayList.get(position).order_amount);

        // holder.txtOrderAmount.setText("Paid : "+context.getResources().getString(R.string.dollar)+" "+OrderObjectArrayList.get(position).total_order_amount);

        holder.txtOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(R.layout.layout_order_details);
                TextView txtItemPrice = dialog.findViewById(R.id.txtItemPrice);
                TextView txtPrice = dialog.findViewById(R.id.txtPrice);
                TextView txtDeliveryCharges = dialog.findViewById(R.id.txtDeliveryCharges);
                TextView txtTaxesAndCharges = dialog.findViewById(R.id.txtTaxesAndCharges);
                TextView txtPayableAmount = dialog.findViewById(R.id.txtPayableAmount);
                TextView txtVoucherDis = dialog.findViewById(R.id.txtVoucherDis);

                TextView txtOrderNumber = dialog.findViewById(R.id.txtOrderNumber);
                TextView txtOrderOn = dialog.findViewById(R.id.txtOrderOn);
                MyTextView txtPaymentMode = dialog.findViewById(R.id.txtPaymentMode);
                TextView txtPhoneNo = dialog.findViewById(R.id.txtPhoneNo);

                LinearLayout lyt_comment= dialog.findViewById(R.id.lyt_comment);
                TextView txtComment= dialog.findViewById(R.id.txtComment);


                if(OrderObjectArrayList.get(position).comment != null) {
                    if (!OrderObjectArrayList.get(position).comment.equalsIgnoreCase("")) {
                        txtComment.setText(OrderObjectArrayList.get(position).comment);
                    }
                    else{
                        lyt_comment.setVisibility(View.GONE);
                    }
                }

                txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                RecyclerView rvItems = dialog.findViewById(R.id.rvItems);
                rvItems.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
                rvItems.setHasFixedSize(true);

                txtDeliveryCharges.setText(context.getResources().getString(R.string.dollar)+" "+OrderObjectArrayList.get(position).delivery_charges);
                txtTaxesAndCharges.setText(context.getResources().getString(R.string.dollar) + " " + OrderObjectArrayList.get(position).taxes_and_charges);
                txtVoucherDis.setText("- "+context.getResources().getString(R.string.dollar)+"" + OrderObjectArrayList.get(position).offer_amount);

                txtPrice.setText(context.getResources().getString(R.string.dollar)+" "+OrderObjectArrayList.get(position).total_item_price);
                txtItemPrice.setText(context.getResources().getString(R.string.dollar)+" "+OrderObjectArrayList.get(position).total_selling_price);
                txtOrderNumber.setText(""+OrderObjectArrayList.get(position).order_no);
                txtOrderOn.setText(""+OrderObjectArrayList.get(position).order_date);
                txtPhoneNo.setText(""+OrderObjectArrayList.get(position).mobile_no);

             //   txtPaymentMode.setText(""+OrderObjectArrayList.get(position).txn_details.get(0).payment_type.toUpperCase());
                txtPaymentMode.setText(""+OrderObjectArrayList.get(position).txn_details.get(0).payment_type.toUpperCase());


                float total = Float.parseFloat(OrderObjectArrayList.get(position).total_selling_price) + Float.parseFloat(OrderObjectArrayList.get(position).delivery_charges);
//                txtPayableAmount.setText(context.getResources().getString(R.string.dollar)+" "+total);

//                txtPayableAmount.setText(context.getResources().getString(R.string.dollar)+" "+String.format("%.2f",total));
                txtPayableAmount.setText(context.getResources().getString(R.string.dollar)+" "+OrderObjectArrayList.get(position).order_amount);


                //txtPayableAmount.setText(context.getResources().getString(R.string.dollar)+" "+OrderObjectArrayList.get(position).order_amount);

                CloseItemListAdpter itemsAdapter = new CloseItemListAdpter(context,OrderObjectArrayList.get(position).item_details);
                rvItems.setAdapter(itemsAdapter);

                dialog.show();
            }
        });

    }


    @Override
    public int getItemCount()
    {
        return OrderObjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTime,txtCustomerName,txtAddress,txtOrderNo,txtRestName,txtOrderDetails,txtOrderAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderDetails=itemView.findViewById(R.id.txtOrderDetails);
            txtTime=itemView.findViewById(R.id.txtTime);
            txtCustomerName=itemView.findViewById(R.id.txtCustomerName);
            txtAddress=itemView.findViewById(R.id.txtAddress);
            txtOrderNo=itemView.findViewById(R.id.txtOrderNo);
            txtRestName=itemView.findViewById(R.id.txtRestName);
            txtOrderAmount=itemView.findViewById(R.id.txtOrderAmount);
        }
    }
}