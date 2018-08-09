package com.application.tippzi.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Adapters.WalletListAdapter;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.WalletModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.application.tippzi.Service.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class WalletScreen extends AbstractActivity {

    private ListView wallet_list ;
    private TextView my_wallet ;
    private ImageView close_wallet ;

    private GPSTracker mGPS;
    private WalletListAdapter walletListAdapter ;

    private ACProgressFlower dialog;
    private Dialog popupdailog ;

    private TextView mCoinCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        mGPS = new GPSTracker(this);
        if (mGPS.canGetLocation()) {
            mGPS.getLocation();
        }

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Removing...")
                .build();

        wallet_list = findViewById(R.id.lv_wallet_list);
        my_wallet = findViewById(R.id.tv_my_wallet);
        close_wallet = findViewById(R.id.iv_close_wallet);

        close_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomerMapViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        walletListAdapter = new WalletListAdapter(this);
        wallet_list.setAdapter(walletListAdapter);
        walletListAdapter.setOnClickButtonListener(walletselectitem);
        walletListAdapter.setOnRemoveDealListener(removeSelectItem);
        wallet_list.setDivider(this.getResources().getDrawable(R.drawable.transperent_color));
        wallet_list.setDividerHeight(30);

        mCoinCount = findViewById(R.id.tv_coin_count);

        LoadWalletList() ;

        String url = GD.coinApi + "get_coin_count";
        JSONObject param = new JSONObject();
        try {
            param.put("customer", /*1*/GD.customerModel.user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetCoinCount().execute(url, param.toString());
    }

    public class GetCoinCount extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
            String response = CF.HttpPostRequest(url, data);
            publishProgress(response);
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            try {
                JSONObject jo = new JSONObject(response);
                coinct = jo.getString("coinct");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            mCoinCount.setText(coinct + " tokens");
        }
    }

    public String coinct = "";

    private void LoadWalletList() {
        walletListAdapter.clearFeedList();
        walletListAdapter.notifyDataSetChanged();
        int wallet_count = 0;
        for (int i = 0 ; i < GD.customerModel.walletModels.size(); i++) {
            if (GD.customerModel.walletModels.get(i).claim_check == true) {

            } else {

                WalletModel walletListModel = new WalletModel();
                for (int j = 0; j < GD.customerModel.bars.size(); j++) {
                    if (GD.customerModel.bars.get(j).bar_id == GD.customerModel.walletModels.get(i).bar_id) {
                        walletListModel.category = GD.customerModel.bars.get(j).category;
                    }
                }

                if (!walletListModel.category.equals(GD.category_option)) {
                    continue;
                }

                walletListModel.deal_id = GD.customerModel.walletModels.get(i).deal_id;
                walletListModel.deal_title = GD.customerModel.walletModels.get(i).deal_title;
                walletListModel.deal_description = GD.customerModel.walletModels.get(i).deal_description;
                walletListModel.deal_duration = GD.customerModel.walletModels.get(i).deal_duration;
                walletListModel.deal_qty = GD.customerModel.walletModels.get(i).deal_qty;
                walletListModel.impressions = GD.customerModel.walletModels.get(i).impressions;
                walletListModel.in_wallet = GD.customerModel.walletModels.get(i).in_wallet;
                walletListModel.claimed = GD.customerModel.walletModels.get(i).claimed;
                walletListModel.bar_name = GD.customerModel.walletModels.get(i).bar_name;
                walletListModel.music_type = GD.customerModel.walletModels.get(i).music_type;
                walletListAdapter.addFeedList(walletListModel);
                walletListAdapter.notifyDataSetChanged();

                wallet_count++;
            }
        }

        my_wallet.setText( "My deals (" +String.valueOf(wallet_count) + ")");
    }

    private WalletListAdapter.OnEditDealListListener walletselectitem = new WalletListAdapter.OnEditDealListListener() {
        @Override
        public void onClick(int pos) {
            GD.wallet_pos = pos;
            GD.check_wallet = true;
            Intent intent = new Intent(getApplicationContext(), TicketScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
            finish();
        }
    } ;

    private WalletListAdapter.OnEditDealListListener removeSelectItem = new WalletListAdapter.OnEditDealListListener() {
        @Override
        public void onClick(int pos) {
            GD.wallet_pos = pos;
            RemoveWalletPopup();
        }
    };

    private void RemoveWalletPopup(){
        popupdailog = new Dialog(this);
        popupdailog.requestWindowFeature(1);
        popupdailog.setCancelable(true);
        popupdailog.setContentView(R.layout.popup_remove_wallet);
        popupdailog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView btn_yes = popupdailog.findViewById(R.id.btn_yes);
        TextView btn_no = popupdailog.findViewById(R.id.btn_no);
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.width = -1;
        wmlp.gravity = 17;
        popupdailog.show();

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupdailog.dismiss();
                callRemoveWalletAPI();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupdailog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), CategoryScreen.class);
        startActivity(intent);
        finish();
    }

    private void callRemoveWalletAPI() {
        String url = GD.BaseUrl + "delete_wallet.php";

        String latitude = String.valueOf(mGPS.getLatitude());
        String longitude = String.valueOf(mGPS.getLongitude());

        String request = "";

        request = "{\"user_id\":\""+GD.customerModel.user_id+"\", \"deal_id\":\""+GD.customerModel.walletModels.get(GD.wallet_pos).deal_id+"\", \"bar_id\":\"" + GD.customerModel.walletModels.get(GD.wallet_pos).bar_id + "\", \"lat\":\"" + latitude + "\", \"lon\":\"" + longitude + "\" }";

        new RemoveWalletWorker().execute(url, request);
    }

    public class RemoveWalletWorker extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
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

                        dialog.dismiss();

                        LoadWalletList();
                        my_wallet.setText( "My deals (" +String.valueOf(GD.customerModel.walletModels.size()) + ")");

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), loginJson.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {

                }
            } else {

            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
