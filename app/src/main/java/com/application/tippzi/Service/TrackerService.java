package com.application.tippzi.Service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.application.tippzi.Activities.CustomerMapViewActivity;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.WalletModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by E on 11/10/2017.
 */

public class TrackerService extends IntentService {
    private SharedPreferences pref;
    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";
    private Timer mTimer = new Timer();
    GPSTracker mGPS ;
    CustomerMapViewActivity cuactivity ;

    double longitude ;
    double latitude ;

    public TrackerService(){
        super("TrackerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mGPS = new GPSTracker(this);

        getLocationChange();
        mTimer.schedule(mgetLocation, 10*1000, 10*1000);
    }

    TimerTask mgetLocation = new TimerTask() {
        @Override
        public void run() {
            getLocationChange();
        }
    };

    public void getLocationChange() {
        if (mGPS.canGetLocation()) {
            mGPS.getLocation() ;
        } else {
        }
        longitude = mGPS.getLongitude() ;
        latitude = mGPS.getLatitude() ;

        callCheckRememberCustomerAPI();
    }


    private void callCheckRememberCustomerAPI() {
        pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        String url = GD.BaseUrl + "check_remember_customer.php";
        String lat = String.valueOf(mGPS.getLatitude());
        String lon = String.valueOf(mGPS.getLongitude());
        String request = "{\"user_id\":\""+pref.getString(USERID, "")+"\", \"lat\":\"" + lat +"\", \"lon\":\"" + lon+ "\"}";

        new CustomerRememberWorker().execute(url, request);
    }

    public class CustomerRememberWorker extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
            String response = CF.HttpPostRequest(url, data);
            publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            if (!response.isEmpty()) {
                try {
                    JSONObject loginJson = new JSONObject(response);
                    String success = loginJson.getString("success");
                    if (success.equals("true")) {

                        GD.customerModel = new CustomerModel();
                        GD.customerModel.user_id = loginJson.getInt("user_id");
                        GD.customerModel.user_name = loginJson.getString("user_name");
                        GD.customerModel.email = loginJson.getString("email");
                        GD.customerModel.gender = loginJson.getString("gender");
                        GD.customerModel.birthday = loginJson.getString("birthday");
                        GD.customerModel.username = loginJson.getString("username");

                        JSONArray jsonArray = loginJson.getJSONArray("bars");
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BarModel barModel = new BarModel();
                            barModel.bar_id = jsonObject.getInt("bar_id");
                            barModel.business_name = jsonObject.getString("business_name");
                            barModel.category = jsonObject.getString("category");
                            barModel.post_code = jsonObject.getString("post_code");
                            barModel.address = jsonObject.getString("address");
                            barModel.lat = jsonObject.getString("lat");
                            barModel.lon = jsonObject.getString("lon");
                            barModel.distance = jsonObject.getDouble("distance");
                            barModel.telephone = jsonObject.getString("telephone");
                            barModel.website = jsonObject.getString("website");
                            barModel.email = jsonObject.getString("email");
                            barModel.description = jsonObject.getString("description");
                            barModel.music_type = jsonObject.getString("music_type");

                            JSONObject jsonObject1 = jsonObject.getJSONObject("open_time");
                            barModel.open_time.mon_start = jsonObject1.getString("mon_start");
                            barModel.open_time.mon_end = jsonObject1.getString("mon_end");
                            barModel.open_time.tue_start = jsonObject1.getString("tue_start");
                            barModel.open_time.tue_end = jsonObject1.getString("tue_end");
                            barModel.open_time.wed_start = jsonObject1.getString("wed_start");
                            barModel.open_time.wed_end = jsonObject1.getString("wed_end");
                            barModel.open_time.thur_start = jsonObject1.getString("thur_start");
                            barModel.open_time.thur_end = jsonObject1.getString("thur_end");
                            barModel.open_time.fri_start = jsonObject1.getString("fri_start");
                            barModel.open_time.fri_end = jsonObject1.getString("fri_end");
                            barModel.open_time.sat_start = jsonObject1.getString("sat_start");
                            barModel.open_time.sat_end = jsonObject1.getString("sat_end");
                            barModel.open_time.sun_start = jsonObject1.getString("sun_start");
                            barModel.open_time.sun_end = jsonObject1.getString("sun_end");

                            JSONObject jsonObject2 = jsonObject.getJSONObject("gallery");
                            barModel.galleryModel.background1 = jsonObject2.getString("background1");
                            barModel.galleryModel.background2 = jsonObject2.getString("background2");
                            barModel.galleryModel.background3 = jsonObject2.getString("background3");
                            barModel.galleryModel.background4 = jsonObject2.getString("background4");
                            barModel.galleryModel.background5 = jsonObject2.getString("background5");
                            barModel.galleryModel.background6 = jsonObject2.getString("background6");

                            JSONArray jsonArray1 = jsonObject.getJSONArray("deals");
                            for (int j = 0; j < jsonArray1.length(); j ++) {
                                JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                DealModel dealModel = new DealModel();
                                dealModel.deal_id = jsonObject3.getInt("deal_id");
                                dealModel.deal_title = jsonObject3.getString("title");
                                dealModel.deal_description = jsonObject3.getString("description");
                                dealModel.deal_duration =jsonObject3.getString("duration");
                                dealModel.deal_qty = jsonObject3.getInt("qty");
                                dealModel.impressions = jsonObject3.getInt("impressions");
                                dealModel.in_wallet = jsonObject3.getInt("in_wallet");
                                dealModel.claimed = jsonObject3.getInt("claimed");
                                dealModel.wallet_check = jsonObject3.getBoolean("wallet_check");
                                dealModel.claim_check = jsonObject3.getBoolean("claimed_check");
                                dealModel.impressions_check = jsonObject3.getBoolean("impressions_check");
                                dealModel.deal_days.monday = jsonObject3.getJSONObject("deal_days").getBoolean("monday");
                                dealModel.deal_days.tuesday = jsonObject3.getJSONObject("deal_days").getBoolean("tuesday");
                                dealModel.deal_days.wednesday = jsonObject3.getJSONObject("deal_days").getBoolean("wednesday");
                                dealModel.deal_days.thursday = jsonObject3.getJSONObject("deal_days").getBoolean("thursday");
                                dealModel.deal_days.friday = jsonObject3.getJSONObject("deal_days").getBoolean("friday");
                                dealModel.deal_days.saturday = jsonObject3.getJSONObject("deal_days").getBoolean("saturday");
                                dealModel.deal_days.sunday = jsonObject3.getJSONObject("deal_days").getBoolean("sunday");

                                barModel.deals.add(dealModel);
                            }

                            GD.customerModel.bars.add(barModel);
                        }

                        JSONArray jsonArray1 = loginJson.getJSONArray("wallets");

                        for (int i = 0; i < jsonArray1.length(); i ++ ) {
                            JSONObject jsonObject = jsonArray1.getJSONObject(i);

                            WalletModel walletModel = new WalletModel();
                            walletModel.bar_id = jsonObject.getInt("bar_id");
                            walletModel.bar_name = jsonObject.getString("business_name");
                            walletModel.bar_address = jsonObject.getString("address");
                            walletModel.lat = jsonObject.getString("lat");
                            walletModel.lon = jsonObject.getString("lon");
                            walletModel.distance = jsonObject.getDouble("distance");
                            walletModel.music_type = jsonObject.getString("music_type");

                            walletModel.deal_id = jsonObject.getInt("deal_id");
                            walletModel.deal_title = jsonObject.getString("title");
                            walletModel.deal_duration = jsonObject.getString("duration");
                            walletModel.deal_description = jsonObject.getString("description");
                            walletModel.deal_qty = jsonObject.getInt("qty");
                            walletModel.in_wallet = jsonObject.getInt("in_wallet");
                            walletModel.impressions = jsonObject.getInt("impressions");
                            walletModel.claimed = jsonObject.getInt("claimed");
                            walletModel.wallet_check = jsonObject.getBoolean("wallet_check");
                            walletModel.claim_check = jsonObject.getBoolean("claimed_check");
                            walletModel.impressions_check = jsonObject.getBoolean("impressions_check");

                            JSONObject jsonObject1 = jsonObject.getJSONObject("open_time");
                            walletModel.open_time.mon_start = jsonObject1.getString("mon_start");
                            walletModel.open_time.mon_end = jsonObject1.getString("mon_end");
                            walletModel.open_time.tue_start = jsonObject1.getString("tue_start");
                            walletModel.open_time.tue_end = jsonObject1.getString("tue_end");
                            walletModel.open_time.wed_start = jsonObject1.getString("wed_start");
                            walletModel.open_time.wed_end = jsonObject1.getString("wed_end");
                            walletModel.open_time.thur_start = jsonObject1.getString("thur_start");
                            walletModel.open_time.thur_end = jsonObject1.getString("thur_end");
                            walletModel.open_time.fri_start = jsonObject1.getString("fri_start");
                            walletModel.open_time.fri_end = jsonObject1.getString("fri_end");
                            walletModel.open_time.sat_start = jsonObject1.getString("sat_start");
                            walletModel.open_time.sat_end = jsonObject1.getString("sat_end");
                            walletModel.open_time.sun_start = jsonObject1.getString("sun_start");
                            walletModel.open_time.sun_end = jsonObject1.getString("sun_end");

                            GD.customerModel.walletModels.add(walletModel);
                        }

                        SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(USERID, loginJson.getString("user_id"));
                        editor.putString(USERTYPE, "Customer");
                        editor.commit();
                        GD.barModels = adding_bars_by_category(GD.category_option);
                    } else {
                        //Toast.makeText(getApplicationContext(), loginJson.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {

                }
            } else {

            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private ArrayList<BarModel> adding_bars_by_category(String category_name) {
        ArrayList<BarModel> barModels = new ArrayList<BarModel>();
        for (int i = 0; i < GD.customerModel.bars.size(); i ++) {
            if (GD.customerModel.bars.get(i).category.equals(category_name)) {
                barModels.add(GD.customerModel.bars.get(i));
            }
        }
        return barModels;
    }
}
