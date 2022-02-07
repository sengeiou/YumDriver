package com.yum_driver.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.yum_driver.utils.SharedPreferenceManager;

import java.util.Locale;


public class BaseActivity extends AppCompatActivity {

    public SharedPreferenceManager prefManager;
    public DisplayMetrics metrices;
    public Typeface normalTypeface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        prefManager = new SharedPreferenceManager(this);
        metrices = getResources().getDisplayMetrics();
      //  normalTypeface = Typeface.createFromAsset(getAssets(), "RalewayRegular.ttf");
        prefManager.connectDB();
        String language = prefManager.getString("language");
        prefManager.closeDB();

        if(language.equalsIgnoreCase(""))
        {
            setLanguage("en", com.yum_driver.Activities.BaseActivity.this);
            prefManager.connectDB();
            prefManager.setString("language","en");
            prefManager.closeDB();
        }
        else {
            setLanguage(language, com.yum_driver.Activities.BaseActivity.this);
        }
        System.out.println(language);

    }
    public Context setLanguage(String lang, Context c)
    {
        int API = Build.VERSION.SDK_INT;
        if(API >= 17){
            return setLanguageLegacy(lang, c);
        }else{
            return setLanguage17(lang, c);
        }
    }
    @TargetApi(17)
    public Context setLanguage17(String lang, Context c){
        Configuration overrideConfiguration = c.getResources().getConfiguration();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        overrideConfiguration.setLocale(locale);
        //the configuration can be used for other stuff as well
        Context context  = createConfigurationContext(overrideConfiguration);//"local variable is redundant" if the below line is uncommented, it is needed
        //Resources resources = context.getResources();//If you want to pass the resources instead of a Context, uncomment this line and put it somewhere useful
        return context;
    }

    public Context setLanguageLegacy(String lang, Context c){
        Resources res = c.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();//Utility line
        Configuration conf = res.getConfiguration();

        conf.locale = new Locale(lang);//setLocale requires API 17+ - just like createConfigurationContext
        Locale.setDefault(conf.locale);
        res.updateConfiguration(conf, dm);

        //Using this method you don't need to modify the Context itself. Setting it at the start of the app is enough. As you
        //target both API's though, you want to return the context as you have no clue what is called. Now you can use the Context
        //supplied for both things
        return c;
    }
}
