package com.yum_driver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import com.yum_driver.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonMethod {
    private static ProgressDialog pDialog1;
    private static ProgressDialog pDialog3;
    private static ProgressDialog pDialog4;
    private static ProgressDialog pDialog2;

    private static long mLastClickTime = 0;
    private static long WaitTime = 1500;

    public static void showAlert(String message, Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        try {
            netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String getDate(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = format.format(new Date(Long.parseLong(timestamp)));

        return date;
    }

    public static boolean isPasswordValidMethod(String password) {


        boolean isValid = false;

        // ^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$
        // ^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$

        String expression = "^(?=.{8,}$)(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*\\W).*$";
        CharSequence inputStr = password;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            System.out.println("if");
            isValid = true;
        } else {
            System.out.println("else");
        }
        return isValid;
    }

    public static void showProgressDialog(Context context) {
        pDialog1 = ProgressDialog.show(context, null, null, true);
        pDialog1.setContentView(R.layout.progress_dialog);
        pDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void hideProgressDialog(Context context) {
        if (pDialog1 != null) {
            pDialog1.dismiss();
        }
    }

    public static void showProgressDialog1(Context context) {
        pDialog3 = ProgressDialog.show(context, null, null, true);
        pDialog3.setContentView(R.layout.progress_dialog);
        pDialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void hideProgressDialog1(Context context) {
        if (pDialog3 != null) {
            pDialog3.dismiss();
        }
    }

    public static void showProgressDialog3(Context context) {
        pDialog4 = ProgressDialog.show(context, null, null, true);
        pDialog4.setContentView(R.layout.progress_dialog);
        pDialog4.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void hideProgressDialog3(Context context) {
        if (pDialog4 != null) {
            pDialog4.dismiss();
        }
    }

    public static void showProgressDialog2(Context context) {
        pDialog2 = ProgressDialog.show(context, null, null, true);
        pDialog2.setContentView(R.layout.progress_dialog);
        pDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void hideProgressDialog2(Context context) {
        if (pDialog2 != null) {
            pDialog2.dismiss();
        }
    }

    public static void avoidClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime) {
            return;
        }

        mLastClickTime = SystemClock.elapsedRealtime();
    }


}