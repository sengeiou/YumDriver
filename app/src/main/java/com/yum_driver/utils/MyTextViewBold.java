package com.yum_driver.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MyTextViewBold extends TextView {

    public MyTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        //if (!isInEditMode()) 
        {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "RalewaySemiBold.ttf");
            setTypeface(tf);
        }
    }

    public void setTypeface(Typeface tf, int style) {
        Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "RalewaySemiBold.ttf");
        Typeface boldTypeface = Typeface.createFromAsset(getContext().getAssets(), "RalewaySemiBold.ttf");


        /*Typeface normalTypeface = Typeface.DEFAULT;
        Typeface boldTypeface = Typeface.DEFAULT;*/

        if (style == Typeface.BOLD) {
            super.setTypeface(boldTypeface/*, -1*/);
        } else {
            super.setTypeface(normalTypeface/*, -1*/);
        }
    }
}