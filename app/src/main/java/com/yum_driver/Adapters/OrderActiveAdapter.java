package com.yum_driver.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yum_driver.Activities.CompleteDeliveryActivity;
import com.yum_driver.Activities.MishapActivity;
import com.yum_driver.Interfaces.RecyclerViewClickListenerNew;
import com.yum_driver.Pojo.OrderObject;
import com.yum_driver.R;
import com.yum_driver.utils.MyTextView;
import com.yum_driver.utils.SharedPreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class OrderActiveAdapter extends RecyclerView.Adapter<OrderActiveAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OrderObject> OrderObjectArrayList;
    private RecyclerViewClickListenerNew recyclerViewClickListener;
    private SharedPreferenceManager preferenceManager;
    Dialog dialog;

    private long mLastClickTime = 0;
    private long WaitTime =1500;

    public OrderActiveAdapter(Context context, ArrayList<OrderObject> OrderObjectArrayList, RecyclerViewClickListenerNew recyclerViewClickListener) {
        dialog = new Dialog(context);
        this.context = context;
        this.OrderObjectArrayList = OrderObjectArrayList;
        this.recyclerViewClickListener = recyclerViewClickListener;
        preferenceManager = new SharedPreferenceManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_active_order, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.txtTime.setText(OrderObjectArrayList.get(position).order_date);
        //String[] dateTime = OrderObjectArrayList.get(position).order_date.split(" ");
        // holder.txtTime.setText(context.getResources().getString(R.string.time)+ dateTime[1]);
        //  holder.txtTime.setText("Time : " + OrderObjectArrayList.get(position).time);
        holder.txtCustomerName.setText(OrderObjectArrayList.get(position).user_name);
        holder.txtAddress.setText(OrderObjectArrayList.get(position).address1);
        holder.txtRestName.setText(OrderObjectArrayList.get(position).rst_name + " : ");
        holder.txtOrderNo.setText(context.getResources().getString(R.string.order_id) + OrderObjectArrayList.get(position).order_no);


        if (OrderObjectArrayList.get(position).order_status.equalsIgnoreCase("order_accepted") &&
                OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("accepted") ||
                OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("preparing") ||
                OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("pickup") ||
                OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("packing")) {

            holder.txtCustomerName.setText(OrderObjectArrayList.get(position).rst_name);
            holder.txtAddress.setText(OrderObjectArrayList.get(position).rst_address);


        } else if (OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("driver_reached") ||
                OrderObjectArrayList.get(position).order_status.equalsIgnoreCase("order_delivered") ||
                OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("driver_start")) {
            holder.txtCustomerName.setText(OrderObjectArrayList.get(position).user_name);
            holder.txtAddress.setText(OrderObjectArrayList.get(position).address1);
        }


        if (!OrderObjectArrayList.get(position).driver_last_status.equalsIgnoreCase("driver_start")) {
            if (OrderObjectArrayList.get(position).driver_is_reached.equalsIgnoreCase("No")) {
                holder.btnStatus.setVisibility(View.VISIBLE);
                holder.btnStatus.setText("" + context.getResources().getString(R.string.reached));
            } else {
                holder.btnStatus.setVisibility(View.VISIBLE);
                holder.btnStatus.setText("" + context.getResources().getString(R.string.start_journey));
            }
        } else {
            holder.btnStatus.setVisibility(View.VISIBLE);
            holder.btnStatus.setText("" + context.getResources().getString(R.string.delivered));
            holder.btnStatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.btnStatus.setBackground(context.getResources().getDrawable(R.drawable.border_orange));
            holder.txtDelivered.setVisibility(View.GONE);
            holder.txtWait.setVisibility(View.GONE);
        }

        if (OrderObjectArrayList.get(position).restaurant_last_status.equalsIgnoreCase("accepted")) {
            holder.txtDelivered.setVisibility(View.GONE);
            holder.txtWait.setVisibility(View.VISIBLE);
            holder.txtWait.setText(context.getResources().getString(R.string.order_in_process));

        } else if (OrderObjectArrayList.get(position).restaurant_last_status.equalsIgnoreCase("preparing")) {
            holder.txtDelivered.setVisibility(View.GONE);

            holder.txtWait.setVisibility(View.VISIBLE);
            holder.txtWait.setText(context.getResources().getString(R.string.order_preparing));

            holder.btnStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_button));

        } else if (OrderObjectArrayList.get(position).restaurant_last_status.equalsIgnoreCase("packing")) {

            holder.txtDelivered.setVisibility(View.GONE);

            holder.txtWait.setVisibility(View.VISIBLE);
            holder.txtWait.setText(context.getResources().getString(R.string.order_packing));

            holder.btnStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_button));

        } else if (OrderObjectArrayList.get(position).restaurant_last_status.equalsIgnoreCase("pickup")) {
            holder.txtDelivered.setVisibility(View.GONE);

            holder.txtWait.setText( ""+context.getResources().getString(R.string.your_waiting));
            holder.btnStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_button));

        } else if (OrderObjectArrayList.get(position).driver_last_status.equalsIgnoreCase("driver_reached")) {
            holder.btnStatus.setVisibility(View.VISIBLE);
            holder.btnStatus.setText("" + context.getResources().getString(R.string.start_journey));
            holder.btnStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_button));
            holder.txtDelivered.setVisibility(View.GONE);
            holder.txtWait.setVisibility(View.GONE);

        } else if (OrderObjectArrayList.get(position).driver_last_status.equalsIgnoreCase("driver_start")) {
            holder.btnStatus.setVisibility(View.VISIBLE);
            holder.btnStatus.setText("" + context.getResources().getString(R.string.delivered));
            holder.btnStatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.btnStatus.setBackground(context.getResources().getDrawable(R.drawable.border_orange));
            holder.txtDelivered.setVisibility(View.GONE);
            holder.txtWait.setVisibility(View.GONE);

        } else if (OrderObjectArrayList.get(position).driver_last_status.equalsIgnoreCase("delivered")) {
            holder.btnStatus.setVisibility(View.GONE);
            holder.txtDelivered.setVisibility(View.VISIBLE);
            holder.txtWait.setVisibility(View.GONE);

        }


        holder.imgTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mapsURL = "";
                preferenceManager.connectDB();
                String driver_lat = preferenceManager.getString("currentLat");
                String driver_lng = preferenceManager.getString("currentLng");
                preferenceManager.closeDB();


                if (OrderObjectArrayList.get(position).order_status.equalsIgnoreCase("order_accepted") &&
                        OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("accepted") ||
                        OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("preparing") ||
                        OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("pickup") ||
                        OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("packing")) {
                    mapsURL = "https://www.google.com/maps/dir/?api=1&dir_action=navigate&travelmode=driving&origin=" + driver_lat + "," + driver_lng +
                            "&destination=" + OrderObjectArrayList.get(position).rst_lat + "," + OrderObjectArrayList.get(position).rst_lng;

                    Intent intentMaps = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsURL));
                    context.startActivity(intentMaps);
                } else if (OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("driver_reached") ||
                        OrderObjectArrayList.get(position).order_status.equalsIgnoreCase("order_delivered") ||
                        OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("driver_start")) {
                    mapsURL = "https://www.google.com/maps/dir/?api=1&dir_action=navigate&travelmode=driving&origin=" + driver_lat + "," + driver_lng +
                            "&destination=" + OrderObjectArrayList.get(position).address_lat + "," + OrderObjectArrayList.get(position).address_lng;

                    Intent intentMaps = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsURL));
                    context.startActivity(intentMaps);
                } else {

                }
            }
        });

        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String finalStatus = "";

                if (OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("accepted")) {
                    finalStatus = "reached";
                } else if (OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("packing")) {
                    if (!OrderObjectArrayList.get(position).driver_is_reached.equalsIgnoreCase("Yes")) {
                        finalStatus = "reached";
                    } else {
                        finalStatus = "start_journey";
                    }
                } else if (OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("preparing")) {
                    if (!OrderObjectArrayList.get(position).driver_is_reached.equalsIgnoreCase("Yes")) {
                        finalStatus = "reached";
                    } else {
                        finalStatus = "start_journey";
                    }
                } else if (OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("pickup")) {
                    if (!OrderObjectArrayList.get(position).driver_is_reached.equalsIgnoreCase("Yes")) {
                        finalStatus = "reached";
                    } else {
                        finalStatus = "start_journey";
                    }
                } else if (OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("driver_reached")) {
                    finalStatus = "start_journey";
                } else if (OrderObjectArrayList.get(position).order_recent_status.equalsIgnoreCase("driver_start")) {
                    finalStatus = "delivered";
                }
                if (finalStatus.equalsIgnoreCase("start_journey")) {
                    if (OrderObjectArrayList.get(position).restaurant_last_status.equalsIgnoreCase("pickup")) {
                        recyclerViewClickListener.onRecyclerViewListClicked(position, finalStatus);
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.not_ready_pickup), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (finalStatus.equalsIgnoreCase("reached")) {
                        if (!OrderObjectArrayList.get(position).driver_is_reached.equalsIgnoreCase("Yes")) {
                            recyclerViewClickListener.onRecyclerViewListClicked(position, finalStatus);
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.change_stutus), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        recyclerViewClickListener.onRecyclerViewListClicked(position, finalStatus);
                    }

                }
            }
        });

        holder.rlMishap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MishapActivity.class);
                i.putExtra("order_id", OrderObjectArrayList.get(position).order_id);
                context.startActivity(i);
            }
        });

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "" + OrderObjectArrayList.get(position).mobile_no;
                if (!phone.equalsIgnoreCase("")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.contact_unavaiable), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.imgRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = "" + OrderObjectArrayList.get(position).rst_contact;
                if (!contact.equalsIgnoreCase("")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contact, null));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.contact_unavaiable), Toast.LENGTH_SHORT).show();
                }
            }
        });
     /*   holder.btnOrderItemPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(R.layout.layout_order_items_list);


                Button btnOrderPickup = dialog.findViewById(R.id.btnOrderPickup);
                LinearLayout rlNoItem = dialog.findViewById(R.id.rlNoItem);
                RecyclerView rvItems = dialog.findViewById(R.id.rvItems);
                rvItems.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                rvItems.setHasFixedSize(true);

                btnOrderPickup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });



                if (OrderObjectArrayList.get(position).item_details.size() < 1) {
                    rvItems.setVisibility(View.GONE);
                    rlNoItem.setVisibility(View.VISIBLE);
                } else {

                    rvItems.setVisibility(View.VISIBLE);
                    rlNoItem.setVisibility(View.GONE);
                    ItemsAdapter itemsAdapter = new ItemsAdapter(context, OrderObjectArrayList.get(position).item_details);
                    rvItems.setAdapter(itemsAdapter);
                }

                dialog.show();
            }
        });*/


        holder.btnStripCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, CompleteDeliveryActivity.class));
            }
        });

        holder.txtOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                showBottomSheet(position);
            }
        });

        holder.btnStatus.setTextColor(context.getResources().getColor(R.color.white));

    }

    private void showBottomSheet(int position) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.layout_order_details);
        TextView txtItemPrice = dialog.findViewById(R.id.txtItemPrice);
        TextView txtPrice = dialog.findViewById(R.id.txtPrice);
        TextView txtDeliveryCharges = dialog.findViewById(R.id.txtDeliveryCharges);
        TextView txtTaxesAndCharges = dialog.findViewById(R.id.txtTaxesAndCharges);
        TextView txtPayableAmount = dialog.findViewById(R.id.txtPayableAmount);

        TextView txtOrderNumber = dialog.findViewById(R.id.txtOrderNumber);
        TextView txtOrderOn = dialog.findViewById(R.id.txtOrderOn);
        MyTextView txtPaymentMode = dialog.findViewById(R.id.txtPaymentMode);
        TextView txtPhoneNo = dialog.findViewById(R.id.txtPhoneNo);
        TextView txtVoucherDis = dialog.findViewById(R.id.txtVoucherDis);

        LinearLayout lyt_comment= dialog.findViewById(R.id.lyt_comment);
        TextView txtComment= dialog.findViewById(R.id.txtComment);

        txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        RecyclerView rvItems = dialog.findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvItems.setHasFixedSize(true);


        if(OrderObjectArrayList.get(position).comment != null) {
            if (!OrderObjectArrayList.get(position).comment.equalsIgnoreCase("")) {
                txtComment.setText(OrderObjectArrayList.get(position).comment);
            }
            else{
                lyt_comment.setVisibility(View.GONE);
            }
        }


        txtDeliveryCharges.setText(context.getResources().getString(R.string.dollar) + " " + OrderObjectArrayList.get(position).delivery_charges);
        txtTaxesAndCharges.setText(context.getResources().getString(R.string.dollar) + " " + OrderObjectArrayList.get(position).taxes_and_charges);
        txtVoucherDis.setText("- "+context.getResources().getString(R.string.dollar)+"" + OrderObjectArrayList.get(position).offer_amount);

        txtPrice.setText(context.getResources().getString(R.string.dollar) + " " + OrderObjectArrayList.get(position).total_item_price);
        txtItemPrice.setText(context.getResources().getString(R.string.dollar) + " " + OrderObjectArrayList.get(position).total_selling_price);
        txtOrderNumber.setText("" + OrderObjectArrayList.get(position).order_no);
        txtOrderOn.setText("" + OrderObjectArrayList.get(position).order_date);
        txtPhoneNo.setText("" + OrderObjectArrayList.get(position).mobile_no);
        //   txtPaymentMode.setText(""+OrderObjectArrayList.get(position).txn_details.get(0).payment_type.toUpperCase());
        txtPaymentMode.setText("" + OrderObjectArrayList.get(position).txn_details.get(0).payment_type.toUpperCase());


       // float total = Float.parseFloat(OrderObjectArrayList.get(position).total_selling_price) + Float.parseFloat(OrderObjectArrayList.get(position).delivery_charges);
        float total = Float.parseFloat(OrderObjectArrayList.get(position).order_amount) ;
                //+ Float.parseFloat(OrderObjectArrayList.get(position).delivery_charges);
//        txtPayableAmount.setText(context.getResources().getString(R.string.dollar) + " " + total);
//        txtPayableAmount.setText(context.getResources().getString(R.string.dollar)+" "+String.format("%.2f",total));
        txtPayableAmount.setText(context.getResources().getString(R.string.dollar)+" "+OrderObjectArrayList.get(position).order_amount);

       // txtPayableAmount.setText(context.getResources().getString(R.string.dollar) + " " +OrderObjectArrayList.get(position).order_amount);

        CloseItemListAdpter itemsAdapter = new CloseItemListAdpter(context, OrderObjectArrayList.get(position).item_details);
        rvItems.setAdapter(itemsAdapter);

        dialog.show();
    }


    @Override
    public int getItemCount() {
        return OrderObjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTrack, imgCall, imgRestaurant;
        Button btnOrderConfirm, btnOrderItemPickUp, btnStatus, btnStartDelivery, btnCompleteDelivery, btnStripCompleted, btnStripStart, btnConfirmDelivery;
        RelativeLayout rlMishap;

        TextView txtTime, txtWait, txtOrderDetails, txtCustomerName, txtAddress, txtOrderNo, txtRestName, txtDelivered;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWait = itemView.findViewById(R.id.txtWait);
            imgTrack = itemView.findViewById(R.id.imgTrack);
            imgCall = itemView.findViewById(R.id.imgCall);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            btnStatus = itemView.findViewById(R.id.btnStatus);
            btnOrderItemPickUp = itemView.findViewById(R.id.btnOrderItemPickUp);
            // btnStartDelivery = itemView.findViewById(R.id.btnStartDelivery);
            rlMishap = itemView.findViewById(R.id.rlMishap);

            //  btnCompleteDelivery = itemView.findViewById(R.id.btnCompleteDelivery);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtOrderNo = itemView.findViewById(R.id.txtOrderNo);
            txtRestName = itemView.findViewById(R.id.txtRestName);
            btnStripStart = itemView.findViewById(R.id.btnStripStart);
            btnStripCompleted = itemView.findViewById(R.id.btnStripCompleted);
            btnConfirmDelivery = itemView.findViewById(R.id.btnConfirmDelivery);
            txtDelivered = itemView.findViewById(R.id.txtDelivered);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);

        }
    }
}