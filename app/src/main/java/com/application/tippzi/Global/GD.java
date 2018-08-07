package com.application.tippzi.Global;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.BusinessModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.EditBarModel;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;

public class GD {

    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";

//    public static String BaseUrl = "http://192.168.1.102/Tippzi/api/authorize/";
//    public static String downloadUrl = "http://192.168.1.102/Tippzi/upload/";
    public static String BaseUrl = "http://162.13.192.72/Tippzi/api/authorize/";
    public static String downloadUrl = "http://162.13.192.72/Tippzi/upload/";
//    public static String coinApi = "http://192.168.1.101/tippzi/public/api/coin/";
    public static String coinApi = "http://162.13.192.72/api/coin/";

    public static BusinessModel businessModel = new BusinessModel();
    public static CustomerModel customerModel = new CustomerModel();
    public static ArrayList<BarModel> barModels = new ArrayList<BarModel>();
    public static EditBarModel temp_bar = new EditBarModel();

    public static ArrayList<Integer> barIdArrayList = new ArrayList<>();
    public static ArrayList<Integer> barIdEngagement = new ArrayList<>();

    //use MapViewPager
    public static int select_pos = 0 ;
    public static String select_user_type ;
    public static int edit_deal_pos = 0;
    public static int add_edit_deal = 0;
    public static LatLng mylocation = null ;
    public static int slide_index = 0 ;
    public static int temp_index_count = 0 ;
    public static Boolean check_wallet = false;
    public static String category_option = "";
    public static int wallet_pos = 0;

    public static TwitterSession twitterSession;

    public static boolean isAlarm = false;

    public static ArrayList<String> gServiceTexts;

//    public static CountDownTimer timer = new CountDownTimer(5 * 1000 * 1, 1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//
//        }
//
//        @Override
//        public void onFinish() {
//
//            SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString(USERID, "");
//            editor.putString(USERTYPE, "Customer");
//            editor.commit();
//
//            GD.category_option = "";
//
//            finish();
//        }
//    }.start();
}