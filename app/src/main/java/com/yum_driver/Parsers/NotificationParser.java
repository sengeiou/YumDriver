package com.yum_driver.Parsers;



import com.yum_driver.Pojo.NotificationObject;

import java.io.Serializable;
import java.util.ArrayList;

public class NotificationParser implements Serializable {

    public String responsemessage, responsecode;
    public ArrayList<NotificationObject> data;
}