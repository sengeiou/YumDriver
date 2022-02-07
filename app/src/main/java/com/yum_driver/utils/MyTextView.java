package com.yum_driver.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        //if (!isInEditMode()) 
        {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "RalewayRegular.ttf");
            setTypeface(tf);
        }
    }

    public void setTypeface(Typeface tf, int style) {
        Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "RalewayRegular.ttf");
        Typeface boldTypeface = Typeface.createFromAsset(getContext().getAssets(), "RalewayRegular.ttf");


        /*Typeface normalTypeface = Typeface.DEFAULT;
        Typeface boldTypeface = Typeface.DEFAULT;*/

        if (style == Typeface.BOLD) {
            super.setTypeface(boldTypeface/*, -1*/);
        } else {
            super.setTypeface(normalTypeface/*, -1*/);
        }
    }
}