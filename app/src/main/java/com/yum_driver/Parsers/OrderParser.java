package com.yum_driver.Parsers;

import com.yum_driver.Pojo.OrderObject;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderParser implements Serializable {

    public String responsemessage, responsecode;
    public ArrayList<OrderObject> order_list;
}
