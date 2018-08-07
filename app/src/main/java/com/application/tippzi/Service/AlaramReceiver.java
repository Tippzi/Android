package com.application.tippzi.Service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.application.tippzi.Activities.SignInScreen;
import com.application.tippzi.Activities.SplashScreen;
import com.application.tippzi.Global.GD;
import com.application.tippzi.R;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AlaramReceiver extends BroadcastReceiver {
    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String userType = pref.getString(USERTYPE, "Customer");
        if(userType.equals("Customer")) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(USERID, "");
            editor.putString(USERTYPE, "Customer");
            editor.commit();
        } else if(userType.equals("Business")){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(USERID, "");
            editor.putString(USERTYPE, "Business");
            editor.commit();
        }
        GD.isAlarm = false;
        GD.category_option = "";
        Log.e(MyPREFERENCES, "Receiver");
    }
}
