package com.application.tippzi.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.WalletModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.application.tippzi.Service.GPSTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TicketScreen extends AbstractActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    private BarModel barModel;
    private ACProgressFlower dialog;
    private TextView claim_deal, btn_yes, btn_no, add_wallet, deal_remain, expired_date, deal_title_description;
    private RelativeLayout redeemed_layout ;
    private ImageView iv_share_google, iv_share_facebook, iv_share_twitter ;
    private ImageView border_bar, close;
    private GPSTracker mGPS;
    /*share deal part*/
    private GoogleApiClient google_api_client;
    private GoogleApiAvailability google_api_availability;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private int request_code;
    private boolean is_signInBtn_clicked;
    private Dialog popupdailog ;
    private static final int SIGN_IN_CODE = 0;
    private static final String MyPREFERENCES = "Tippzi";
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";
    private SharedPreferences pref;
    private String accesstoken ;
    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;
    private String load_socialmedia_type ;
    public static final int WEBVIEW_REQUEST_CODE = 100;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private String messgae = "" ;

    private CallbackManager callbackManager;

    //facebook
    static final String APP_ID = "facebook app id";
    static final String[] PERMISSIONS = new String[] { "publish_stream" };
    static final String TOKEN = "access_token";
    static final String EXPIRES = "expires_in";
    static final String KEY = "facebook-credentials";
    private boolean FB_LOGIN = false;
    private ShareDialog shareDialog;
    private Uri appUri ;
    private Bitmap bitmap ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        load_socialmedia_type = "" ;

        mGPS = new GPSTracker(this);
        if (mGPS.canGetLocation()) {
            mGPS.getLocation();
        }

        initTwitterConfigs();
        FacebookSdk.sdkInitialize(getApplicationContext());
        buidNewGoogleApiClient();

        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } catch (Exception e){
            e.printStackTrace();
        }

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Registering...")
                .build();

        redeemed_layout = findViewById(R.id.lay_redeems);
        border_bar = findViewById(R.id.iv_ticket_bar);

        ImageView back = findViewById(R.id.iv_back_ticket);
        back.setOnClickListener(this);

        close = findViewById(R.id.iv_close_ticket);
        close.setOnClickListener(this);

        add_wallet = findViewById(R.id.btn_add_wallet);
        add_wallet.setOnClickListener(this);

        TextView share_deal = findViewById(R.id.btn_share_deal);
        share_deal.setOnClickListener(this);

        TextView open_time = findViewById(R.id.tv_open_time_this_day);

        claim_deal = findViewById(R.id.tv_ticket_code);

        claim_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GD.check_wallet == true) {
                    if (GD.customerModel.walletModels.get(GD.wallet_pos).claim_check == true) {

                    } else {
                        CallClaimWalletPopup();
                    }
                } else {
                    if (GD.barModels.get(GD.select_pos).deals.get(GD.slide_index).claim_check == true) {

                    } else {
                        CallClaimWalletPopup();
                    }
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String opentime = "";

        switch (day) {
            case Calendar.SUNDAY:
                opentime = GD.barModels.get(GD.select_pos).open_time.sun_start + " - " + GD.barModels.get(GD.select_pos).open_time.sun_end;
                break;
            case Calendar.MONDAY:
                opentime = GD.barModels.get(GD.select_pos).open_time.mon_start + " - " + GD.barModels.get(GD.select_pos).open_time.mon_end;
                break;
            case Calendar.TUESDAY:
                opentime = GD.barModels.get(GD.select_pos).open_time.tue_start + " - " + GD.barModels.get(GD.select_pos).open_time.tue_end;
                break;
            case Calendar.WEDNESDAY:
                opentime = GD.barModels.get(GD.select_pos).open_time.wed_start + " - " + GD.barModels.get(GD.select_pos).open_time.wed_end;
                break;
            case Calendar.THURSDAY:
                opentime = GD.barModels.get(GD.select_pos).open_time.thur_start + " - " + GD.barModels.get(GD.select_pos).open_time.thur_end;
                break;
            case Calendar.FRIDAY:
                opentime = GD.barModels.get(GD.select_pos).open_time.fri_start + " - " + GD.barModels.get(GD.select_pos).open_time.fri_end;
                break;
            case Calendar.SATURDAY:
                opentime = GD.barModels.get(GD.select_pos).open_time.sat_start + " - " + GD.barModels.get(GD.select_pos).open_time.sat_end;
                break;
        }

        if (opentime.equals(" - ")) {
            open_time.setText("Closed");
        } else {
            open_time.setText(opentime);
        }

        TextView walk_time = findViewById(R.id.tv_walk_time);

        TextView bar_title = findViewById(R.id.tv_business_name_ticket);
        expired_date = findViewById(R.id.tv_exp_date);
        TextView deal_title = findViewById(R.id.tv_deal_name_ticket);
        TextView deal_description = findViewById(R.id.tv_deal_description_ticket);
        deal_remain = findViewById(R.id.tv_remain_deals);
        TextView deal_duration = findViewById(R.id.tv_expire_date);

        if (GD.check_wallet == true) {
            bar_title.setText(GD.customerModel.walletModels.get(GD.wallet_pos).bar_name);
            expired_date.setText("Exp " + GD.customerModel.walletModels.get(GD.wallet_pos).deal_duration);
            deal_title.setText(GD.customerModel.walletModels.get(GD.wallet_pos).deal_title);

            if (cal_walk_time(GD.barModels.get(GD.select_pos).distance) > 20) {
                walk_time.setText("20 +" + " mins walk");
            } else {
                walk_time.setText(String.valueOf(cal_walk_time(GD.barModels.get(GD.select_pos).distance)) + " mins walk");
            }

            String opentime_wallet = "";

            switch (day) {
                case Calendar.SUNDAY:
                    opentime_wallet = GD.customerModel.walletModels.get(GD.wallet_pos).open_time.sun_start + " - " + GD.customerModel.walletModels.get(GD.wallet_pos).open_time.sun_end;
                    break;
                case Calendar.MONDAY:
                    opentime_wallet = GD.customerModel.walletModels.get(GD.wallet_pos).open_time.mon_start + " - " + GD.customerModel.walletModels.get(GD.wallet_pos).open_time.mon_end;
                    break;
                case Calendar.TUESDAY:
                    opentime_wallet = GD.customerModel.walletModels.get(GD.wallet_pos).open_time.tue_start + " - " + GD.customerModel.walletModels.get(GD.wallet_pos).open_time.tue_end;
                    break;
                case Calendar.WEDNESDAY:
                    opentime_wallet = GD.customerModel.walletModels.get(GD.wallet_pos).open_time.wed_start + " - " + GD.customerModel.walletModels.get(GD.wallet_pos).open_time.wed_end;
                    break;
                case Calendar.THURSDAY:
                    opentime_wallet = GD.customerModel.walletModels.get(GD.wallet_pos).open_time.thur_start + " - " + GD.customerModel.walletModels.get(GD.wallet_pos).open_time.thur_end;
                    break;
                case Calendar.FRIDAY:
                    opentime_wallet = GD.customerModel.walletModels.get(GD.wallet_pos).open_time.fri_start + " - " + GD.customerModel.walletModels.get(GD.wallet_pos).open_time.fri_end;
                    break;
                case Calendar.SATURDAY:
                    opentime_wallet = GD.customerModel.walletModels.get(GD.wallet_pos).open_time.sat_start + " - " + GD.customerModel.walletModels.get(GD.wallet_pos).open_time.sat_end;
                    break;
            }

            if (GD.customerModel.walletModels.get(GD.wallet_pos).claim_check == true) {
                claim_deal.setVisibility(View.GONE);
                close.setBackgroundResource(R.mipmap.ico_close_white);
                expired_date.setVisibility(View.GONE);
                redeemed_layout.setBackgroundResource(R.mipmap.ico_cliamed);
                border_bar.setBackgroundResource(R.mipmap.ico_ticket_bar_2);
                add_wallet.setBackgroundResource(R.drawable.rect_hint_button);
                add_wallet.setTextColor(getResources().getColor(R.color.HintWalet));
                add_wallet.setEnabled(false);
                claim_deal.setEnabled(false);
            } else {
                claim_deal.setText("Claim This Deal");
                claim_deal.setEnabled(true);
            }

            if (opentime.equals(" - ")) {
                open_time.setText("Closed");
            } else {
                open_time.setText(opentime_wallet);
            }

            deal_description.setText(GD.customerModel.walletModels.get(GD.wallet_pos).deal_description);
            deal_remain.setText("Hurry! only " + String.valueOf(GD.customerModel.walletModels.get(GD.wallet_pos).deal_qty - GD.customerModel.walletModels.get(GD.wallet_pos).claimed) + " left!");
            deal_duration.setText("Exp " + GD.customerModel.walletModels.get(GD.wallet_pos).deal_duration);

            add_wallet.setBackgroundResource(R.drawable.rect_hint_button);
            add_wallet.setTextColor(getResources().getColor(R.color.HintWalet));
            add_wallet.setEnabled(false);
        } else {

            barModel = GD.barModels.get(GD.select_pos);

            messgae  = barModel.deals.get(GD.slide_index).deal_title + "\n" + barModel.deals.get(GD.slide_index).deal_description ;

            bar_title.setText(barModel.business_name);
            expired_date.setText("Exp " + barModel.deals.get(GD.slide_index).deal_duration);
            deal_title.setText(barModel.deals.get(GD.slide_index).deal_title);

            if (cal_walk_time(barModel.distance) > 20) {
                walk_time.setText("20 +" + " mins walk");
            } else {
                walk_time.setText(String.valueOf(cal_walk_time(barModel.distance)) + " mins walk");
            }

            deal_description.setText(barModel.deals.get(GD.slide_index).deal_description);
            deal_remain.setText("Hurry! only " + String.valueOf(barModel.deals.get(GD.slide_index).deal_qty - barModel.deals.get(GD.slide_index).claimed) + " left!");
            deal_duration.setText("Exp " + barModel.deals.get(GD.slide_index).deal_duration);

            if (barModel.deals.get(GD.slide_index).claim_check == true) {
                claim_deal.setVisibility(View.GONE);
                close.setBackgroundResource(R.mipmap.ico_close_white);
                expired_date.setVisibility(View.GONE);
                redeemed_layout.setBackgroundResource(R.mipmap.ico_cliamed);
                border_bar.setBackgroundResource(R.mipmap.ico_ticket_bar_2);
                add_wallet.setBackgroundResource(R.drawable.rect_hint_button);
                add_wallet.setTextColor(getResources().getColor(R.color.HintWalet));
                add_wallet.setEnabled(false);
                claim_deal.setEnabled(false);
            } else {
                if (barModel.deals.get(GD.slide_index).wallet_check == true) {

                    add_wallet.setBackgroundResource(R.drawable.rect_hint_button);
                    add_wallet.setTextColor(getResources().getColor(R.color.HintWalet));
                    add_wallet.setEnabled(false);

                } else {

                    add_wallet.setBackgroundResource(R.drawable.rect_button_white);
                    add_wallet.setTextColor(getResources().getColor(R.color.HintBackground));
                    add_wallet.setEnabled(true);
                }
                claim_deal.setText("Claim This Deal");
                claim_deal.setEnabled(true);
            }
        }

        Drawable drawable = getDrawable(R.drawable.ico_app_logo);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        String path = Environment.getExternalStorageDirectory().toString();
        File file;
        file = new File(path, "bar"+".jpg");
        try{
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        appUri = Uri.parse(file.getAbsolutePath());
    }


    private void CallClaimWalletPopup(){
        popupdailog = new Dialog(this);
        popupdailog.requestWindowFeature(1);
        popupdailog.setCancelable(true);
        popupdailog.setContentView(R.layout.popup_claimwallet);
        popupdailog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btn_yes = popupdailog.findViewById(R.id.btn_yes);
        btn_no = popupdailog.findViewById(R.id.btn_no);
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.width = -1;
        wmlp.gravity = 17;
        popupdailog.show();

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupdailog.dismiss();
                callClaimWallet();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupdailog.dismiss();
            }
        });
    }
    private void callClaimWallet() {
        String url = GD.BaseUrl + "add_claim.php";
        String latitude = String.valueOf(mGPS.getLatitude());
        String longitude = String.valueOf(mGPS.getLongitude());

        String request = "";

        if (GD.check_wallet == true) {
            request = "{\"user_id\":\""+GD.customerModel.user_id+"\", \"deal_id\":\""+GD.customerModel.walletModels.get(GD.wallet_pos).deal_id+"\", \"bar_id\":\"" + GD.customerModel.walletModels.get(GD.wallet_pos).bar_id + "\", \"lat\":\"" + latitude + "\", \"lon\":\"" + longitude + "\" }";
        } else {
            request = "{\"user_id\":\""+GD.customerModel.user_id+"\", \"deal_id\":\""+barModel.deals.get(GD.slide_index).deal_id+"\", \"bar_id\":\"" + barModel.bar_id + "\", \"lat\":\"" + latitude + "\", \"lon\":\"" + longitude + "\" }";
        }
        new ClaimedWorker().execute(url, request);
    }

    public class ClaimedWorker extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
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
                            barModel.category = jsonObject.getString("category");

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

                        if (GD.category_option.equals("")) {
                            GD.barModels = GD.customerModel.bars;
                        } else if (GD.category_option.equals("Nightlife")) {
                            GD.barModels = adding_bars_by_category("Nightlife");
                        } else if (GD.category_option.equals("Health & Fitness")) {
                            GD.barModels = adding_bars_by_category("Health & Fitness");
                        } else if (GD.category_option.equals("Hair & Beauty")) {
                            GD.barModels = adding_bars_by_category("Hair & Beauty");
                        }

                        claim_deal.setVisibility(View.GONE);
                        close.setBackgroundResource(R.mipmap.ico_close_white);
                        expired_date.setVisibility(View.GONE);
                        redeemed_layout.setBackgroundResource(R.mipmap.ico_cliamed);
                        border_bar.setBackgroundResource(R.mipmap.ico_ticket_bar_2);

                        if (GD.check_wallet == true) {
                            deal_remain.setText("Hurry! only " + String.valueOf(GD.customerModel.walletModels.get(GD.wallet_pos).deal_qty - GD.customerModel.walletModels.get(GD.wallet_pos).claimed) + " left!");
                        } else {
                            deal_remain.setText("Hurry! only " + String.valueOf(GD.barModels.get(GD.select_pos).deals.get(GD.slide_index).deal_qty - GD.barModels.get(GD.select_pos).deals.get(GD.slide_index).claimed) + " left!");
                        }

                        add_wallet.setBackgroundResource(R.drawable.rect_hint_button);
                        add_wallet.setTextColor(getResources().getColor(R.color.HintWalet));
                        add_wallet.setEnabled(false);
                        claim_deal.setEnabled(false);
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
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

    private ArrayList<BarModel> adding_bars_by_category(String category_name) {
        ArrayList<BarModel> barModels = new ArrayList<BarModel>();
        for (int i = 0; i < GD.customerModel.bars.size(); i ++) {
            if (GD.customerModel.bars.get(i).category.equals(category_name)) {
                barModels.add(GD.customerModel.bars.get(i));
            }
        }

        return barModels;
    }

    private int cal_walk_time(double distance) {
        String time = "";

        float walk_sec = ((float)distance * 1000) / 2.5f;

        int temp = Math.round(walk_sec);

        int mins = temp / 60;

        return mins;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.iv_back_ticket:
                if (GD.check_wallet == true) {
                    intent = new Intent(getApplicationContext(), WalletScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                    finish();
                } else {
                    intent = new Intent(getApplicationContext(), CustomerBarDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                    finish();
                }
                break;

            case R.id.iv_close_ticket:
                if (GD.check_wallet == true) {
                    intent = new Intent(getApplicationContext(), WalletScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                    finish();
                } else {
                    intent = new Intent(getApplicationContext(), CustomerBarDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                    finish();
                }
                break;

            case R.id.btn_add_wallet:
                callAddInWallet();
                break;

            case R.id.btn_share_deal:
                ShareDealMethodCall();
                break;
            case R.id.btn_google_sign_in:
                ShareToGoogle();
                popupdailog.dismiss();
                break;
            case R.id.btn_facebook_sign_in:
                ShareToFacebook();
                popupdailog.dismiss();
                break;
            case R.id.btn_twitter_sign_in:
                ConnectionCheck();
                popupdailog.dismiss();
                break;
        }
    }
    //share deal method part
    private void ShareDealMethodCall(){
        popupdailog = new Dialog(this);
        popupdailog.requestWindowFeature(1);
        popupdailog.setCancelable(true);
        popupdailog.setContentView(R.layout.popup_share_deal);

        iv_share_google = popupdailog.findViewById(R.id.btn_google_sign_in);
        iv_share_facebook = popupdailog.findViewById(R.id.btn_facebook_sign_in);
        iv_share_twitter = popupdailog.findViewById(R.id.btn_twitter_sign_in);
        deal_title_description = popupdailog.findViewById(R.id.deal_title);

        iv_share_google.setOnClickListener(this);
        iv_share_facebook.setOnClickListener(this);

        iv_share_twitter.setOnClickListener(this);

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.width = -1;
        wmlp.gravity = 17;
        popupdailog.show();
    }

    private void ShareToGoogle() {
        File pictureFile;
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/icon");
            dir.mkdirs();
            pictureFile = new File(dir, "attachment.jpg");
            if (pictureFile.exists()) {
                pictureFile.delete();
            }
            pictureFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(pictureFile);

            InputStream in = getResources().openRawResource(R.drawable.ico_app_logo);

            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = in.read(buffer)) > 0) {
                fos.write(buffer, 0, size);
            }
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            //return;
        }

        //Uri pictureUri = Uri.fromFile(pictureFile);

        Intent shareIntent = new PlusShare.Builder(this)
                .setText(messgae)
                .setType("text/plain")
                //.setType("image/")
                .setContentUrl(Uri.parse("http://www.Tippzi.com"))
                //.setStream(pictureUri)
                .getIntent();

        startActivityForResult(shareIntent, 0);
    }

    private void ShareToFacebook() {
        messgae  = barModel.deals.get(GD.slide_index).deal_title + "\n" + barModel.deals.get(GD.slide_index).deal_description ;
        shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ico_app_logo);
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(logo).setCaption(messgae)
                    .build();

            ShareContent shareContent = new ShareMediaContent.Builder()
                    .addMedium(photo)
                    .build();

            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote(messgae)
                    //.setImageUrl(pictureUri)
                    //.setContentDescription("")
                    .setContentUrl(Uri.parse("http://www.tippzi.com/"))
                    .build();
            shareDialog.show(linkContent);  // Show facebook ShareDialog
        }
    }



    private void ConnectionCheck(){
        popupdailog.dismiss();
        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Registering...")
                .build();

        load_socialmedia_type = "twitter";
        pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String oauthaccesstoken = pref.getString(PREF_KEY_OAUTH_TOKEN,"");
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        final Configuration configuration = builder.build();
        final TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
        try {
            requestToken = twitter.getOAuthRequestToken(callbackUrl);
            final Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
            startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    //
    private void ShareTwitterAction(){
        popupdailog = new Dialog(this);
        popupdailog.requestWindowFeature(1);
        popupdailog.setCancelable(true);
        popupdailog.setContentView(R.layout.popup_post_twitter);
        EditText comment = popupdailog.findViewById(R.id.edit_comment_text);
        TextView btn_post = popupdailog.findViewById(R.id.btn_twitter_post);
        ImageView shareImage = popupdailog.findViewById(R.id.iv_shareimage);
        comment.setText(messgae);
        shareImage.setBackgroundResource(R.drawable.ico_app_logo);
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.width = -1;
        wmlp.gravity = 17;
        popupdailog.show();

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupdailog.dismiss();
                new updateTwitterStatus().execute(messgae);
            }
        });
    }

    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ACProgressFlower.Builder(TicketScreen.this)
                    .themeColor(getResources().getColor(R.color.Pink))
                    .text("Registering...")
                    .build();
        }

        protected Void doInBackground(String... args) {

            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                // Access Token
                pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                String access_token = pref.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = pref.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                StatusUpdate statusUpdate = new StatusUpdate(status);
                InputStream is = getResources().openRawResource(R.drawable.ico_app_logo);
                statusUpdate.setMedia("test.jpg", is);

                twitter4j.Status response = twitter.updateStatus(statusUpdate);

                Log.d("Status", response.getText());

            } catch (TwitterException e) {
                Log.d("Failed to post!", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
        }

    }
    //Activity Result
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        // Check which request we're responding to
        try {
            if (load_socialmedia_type.equals("twitter")) {
                if (responseCode == RESULT_OK) {
                    String verifier = intent.getExtras().getString(oAuthVerifier);
                    try {
                        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                        long userID = accessToken.getUserId();
                        final User user = twitter.showUser(userID);
                        String username = user.getName();
                        saveTwitterInfo(accessToken);
                    } catch (Exception e) {
                        Log.e("Twitter Login Failed", e.getMessage());
                    }
                }
            } else if (load_socialmedia_type.equals("facebook")) {
                super.onActivityResult(requestCode, responseCode, intent);
                callbackManager.onActivityResult(requestCode, responseCode, intent);
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
    //
    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();
        User user;
        try {
            user = twitter.showUser(userID);
            String username = user.getName();
			/* Storing oAuth tokens to shared preferences */
            SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            editor.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            editor.putLong("userid",userID);
            editor.putString(PREF_USER_NAME, username);
            editor.commit();

            ShareTwitterAction();

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }
    private Bitmap resizeBitmap(int resId){
        int newWidth = 200 ;
        int newHeight = 200 ;

        BitmapDrawable bitmapDrawable =(BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), resId);
        Bitmap originalBitmap = bitmapDrawable.getBitmap();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

        return resizedBitmap;
    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    private void buidNewGoogleApiClient(){
        google_api_client =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }

    //google part
    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        try {
            if (!result.hasResolution()) {
                google_api_availability.getErrorDialog(this, result.getErrorCode(),request_code).show();
                return;
            }

            if (!is_intent_inprogress) {

                connection_result = result;

                if (is_signInBtn_clicked) {

                    resolveSignInError();
                }
            }
        }catch (Exception e) {

        }
    }

    @Override
    public  void onConnected(Bundle bundle) {

    }
    @Override
    public void onConnectionSuspended(int i) {
        google_api_client.connect();
    }

    //
    private void callAddInWallet() {
        String url = GD.BaseUrl + "add_wallet.php";
        String latitude = String.valueOf(mGPS.getLatitude());
        String longitude = String.valueOf(mGPS.getLongitude());

        String request = "";

        if (GD.check_wallet == true) {
            request = "{\"user_id\":\""+GD.customerModel.user_id+"\", \"deal_id\":\""+GD.customerModel.walletModels.get(GD.wallet_pos).deal_id+"\", \"bar_id\":\"" + GD.customerModel.walletModels.get(GD.wallet_pos).bar_id + "\", \"lat\":\"" + latitude + "\", \"lon\":\"" + longitude + "\" }";
        } else {
            request = "{\"user_id\":\""+GD.customerModel.user_id+"\", \"deal_id\":\""+barModel.deals.get(GD.slide_index).deal_id+"\", \"bar_id\":\"" + barModel.bar_id + "\", \"lat\":\"" + latitude + "\", \"lon\":\"" + longitude + "\" }";
        }

        new AddWalletWorker().execute(url, request);
    }

    public class AddWalletWorker extends AsyncTask<String, String, String> {
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

                        if (GD.category_option.equals("")) {
                            GD.barModels = GD.customerModel.bars;
                        } else if (GD.category_option.equals("Nightlife")) {
                            GD.barModels = adding_bars_by_category("Nightlife");
                        } else if (GD.category_option.equals("Health & Fitness")) {
                            GD.barModels = adding_bars_by_category("Health & Fitness");
                        } else if (GD.category_option.equals("Hair & Beauty")) {
                            GD.barModels = adding_bars_by_category("Hair & Beauty");
                        }

                        Intent intent = new Intent(getApplicationContext(), WalletScreen.class);
                        startActivity(intent);
                        finish();

                    } else {
                        dialog.dismiss();
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

    @Override
    public void onBackPressed() {
        Intent intent;
        if (GD.check_wallet == true) {
            intent = new Intent(getApplicationContext(), WalletScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
            finish();
        } else {
            intent = new Intent(getApplicationContext(), CustomerBarDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
            finish();
        }
    }
}
