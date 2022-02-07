package com.yum_driver.Pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderListObject implements Serializable {
    public String order_id,order_no,user_id,address_id,order_amount,total_item_price,total_selling_price,delivery_charges,total_order_amount,comment,order_date,time,
            order_status,addr_user_name,address1,address2,city,state,zip,mobile_no,rst_image,rst_name,rst_id,rst_address,rst_lat,rst_lng,rst_contact_no,favouriteOrdersflag,review_count,user_name,strRestDriverTime,strUserDriverTime,order_recent_status,
            address_lat,address_lng;
    public ArrayList<OrderItemDetails> item_details;
    public ArrayList<TransactionDetails> txn_details;
}