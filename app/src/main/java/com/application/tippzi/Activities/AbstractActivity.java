package com.application.tippzi.Activities;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.application.tippzi.Global.GD;

public class AbstractActivity extends AppCompatActivity {
    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";
    public CountDownTimer timer = new CountDownTimer(18000 * 1000 * 1, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(MyPREFERENCES, "CountDownTimer" + millisUntilFinished);
        }

        @Override
        public void onFinish() {

            SharedPreferences pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(USERID, "");
            editor.putString(USERTYPE, "Customer");
            editor.commit();

            GD.category_option = "";
            finish();
        }
    };

//    private static AbstractActivity _instance = null;
//    public static AbstractActivity getCurrentInstance(){
//        return _instance;
//    }

    @Override
    public void onStart(){
        super.onStart();
//        if(getCurrentInstance() != null){
//            getCurrentInstance().timer.cancel();
//        }
//        _instance = this;
        timer.start();
        AppStatusHelper.onStart();
    }
    @Override
    public void onStop(){
        super.onStop();
        AppStatusHelper.onStop();
        if(!AppStatusHelper.isAppInBackground()){
            timer.cancel();
        }
    }
}
