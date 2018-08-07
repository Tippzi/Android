package com.application.tippzi.Activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.tippzi.Adapters.BusinessDealAdapter;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.BusinessModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.EditBarModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.application.tippzi.Service.AlaramReceiver;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class BusinessDashboardScreen extends AbstractActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private TextView bar_title, account_statistics, claimed, impressions, in_wallet, comment_deal, percent_profile, comment_profile;
    private TextView business_name, category, address, telephone, email_address, description, music_type, opening_hours, gallery_image;
    private ProgressBar profile_progress;
    private ImageView ch_business_name, ch_category, ch_address, ch_phonenumber, ch_email, ch_description, ch_music_type, ch_open_hour, ch_gallery;
    static ListView business_deals ;
    public static BusinessDealAdapter businessDealAdapter ;
    private LinearLayout circle_background;

    private LinearLayout layout_gallery1, layout_gallery2, layout_gallery3, layout_gallery4, layout_gallery5, layout_gallery6;
    private ImageView image_gallery1, image_gallery2, image_gallery3, image_gallery4, image_gallery5, image_gallery6;

    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";

    private ACProgressFlower dialog;

    private SharedPreferences pref;

    private SwipeRefreshLayout refreshLayout;

    private Dialog popupdailog ;

    //resize bitmap
    private Bitmap resizebitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);

        if(!GD.isAlarm) {
//            Log.e("Tippzi", "onCreate");
//            Calendar cal = Calendar.getInstance();
//
//            cal.add(Calendar.MINUTE, 1); // you can use Calendar.Seconds etc according to your need
//
//            Intent intent = new Intent(this, AlaramReceiver.class);
//            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//            // Get the AlarmManager service
//            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//            am.cancel(sender);
//            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
//            GD.isAlarm = true;
        }

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Removing...")
                .build();

        pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        refreshLayout = findViewById(R.id.swiperefresh);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        circle_background = findViewById(R.id.lay_circle_background);

        bar_title = findViewById(R.id.tv_business_name);
        account_statistics = findViewById(R.id.tv_account_statistics);

        claimed = findViewById(R.id.tv_claimed);
        impressions = findViewById(R.id.tv_impressions);
        in_wallet = findViewById(R.id.tv_in_wallet);

        comment_deal = findViewById(R.id.tv_comment_add_deal);
        percent_profile = findViewById(R.id.tv_percent_profile);
        comment_profile = findViewById(R.id.tv_comment_profile);

        business_name = findViewById(R.id.tv_business_name_dashboard);
        category = findViewById(R.id.tv_business_category_dashboard);
        address = findViewById(R.id.tv_business_address);
        telephone = findViewById(R.id.tv_business_telephone);
        email_address = findViewById(R.id.tv_business_email);
        description = findViewById(R.id.tv_business_description);
        music_type = findViewById(R.id.tv_business_music_type);
        opening_hours = findViewById(R.id.tv_opening_hour);
        gallery_image = findViewById(R.id.tv_gallery_image);

        profile_progress = findViewById(R.id.prg_percent_profile);

        ch_business_name = findViewById(R.id.chc_business_name);
        ch_category = findViewById(R.id.chc_business_category);
        ch_address = findViewById(R.id.chc_business_address);
        ch_phonenumber = findViewById(R.id.chc_business_telephone);
        ch_email = findViewById(R.id.chc_business_email);
        ch_description = findViewById(R.id.chc_business_description);
        ch_music_type = findViewById(R.id.chc_business_music_type);
        ch_open_hour = findViewById(R.id.chc_business_open_hours);
        ch_gallery = findViewById(R.id.chc_business_image_galley);

        business_deals = findViewById(R.id.lv_business_deals);
        business_deals.setDivider(this.getResources().getDrawable(R.drawable.transperent_color));
        business_deals.setDividerHeight(30);

        layout_gallery1 = findViewById(R.id.lay_gallery1_dash);
        layout_gallery2 = findViewById(R.id.lay_gallery2_dash);
        layout_gallery3 = findViewById(R.id.lay_gallery3_dash);
        layout_gallery4 = findViewById(R.id.lay_gallery4_dash);
        layout_gallery5 = findViewById(R.id.lay_gallery5_dash);
        layout_gallery6 = findViewById(R.id.lay_gallery6_dash);

        image_gallery1 = findViewById(R.id.iv_gallery1_dash);
        image_gallery2 = findViewById(R.id.iv_gallery2_dash);
        image_gallery3 = findViewById(R.id.iv_gallery3_dash);
        image_gallery4 = findViewById(R.id.iv_gallery4_dash);
        image_gallery5 = findViewById(R.id.iv_gallery5_dash);
        image_gallery6 = findViewById(R.id.iv_gallery6_dash);

        // Go to map view
        ImageView map_view = findViewById(R.id.iv_switch_map);
        map_view.setOnClickListener(this);

        // Add Deal
        RelativeLayout add_deal = findViewById(R.id.lay_add_deal);
        add_deal.setOnClickListener(this);

        // Edit Profile
        TextView edit_profile = findViewById(R.id.btn_edit_profile);
        edit_profile.setOnClickListener(this);

        ImageView edit_profile_tip = findViewById(R.id.iv_edit_profile);
        edit_profile_tip.setOnClickListener(this);

        LinearLayout logout = findViewById(R.id.btn_logout_business);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(USERID, "");
                editor.putString(USERTYPE, "Business");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), SignInScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        businessDealAdapter = new BusinessDealAdapter(getApplicationContext());
        business_deals.setAdapter(businessDealAdapter);
        businessDealAdapter.setOnClickEditListener(editdeallistener);
        businessDealAdapter.setRemoveDealListener(removeDealListener);

        refresh_dashboard();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.iv_switch_map:
                GD.select_user_type = "business" ;
                intent = new Intent(getApplicationContext(), BusinessMapViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
                break;

            case R.id.lay_add_deal:
                GD.add_edit_deal = 1;
                intent = new Intent(getApplicationContext(), AddDealScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
                break;

            case R.id.btn_edit_profile:

                GD.temp_bar = new EditBarModel();
                GD.temp_bar.bar_id = GD.businessModel.bars.get(0).bar_id;
                GD.temp_bar.business_name = GD.businessModel.bars.get(0).business_name;
                GD.temp_bar.category = GD.businessModel.bars.get(0).category;
                GD.temp_bar.post_code = GD.businessModel.bars.get(0).post_code;
                GD.temp_bar.address = GD.businessModel.bars.get(0).address;
                GD.temp_bar.lat = GD.businessModel.bars.get(0).lat;
                GD.temp_bar.lon = GD.businessModel.bars.get(0).lon;
                GD.temp_bar.telephone = GD.businessModel.bars.get(0).telephone;
                GD.temp_bar.music_type = GD.businessModel.bars.get(0).music_type;
                GD.temp_bar.website = GD.businessModel.bars.get(0).website;
                GD.temp_bar.email = GD.businessModel.bars.get(0).email;
                GD.temp_bar.description = GD.businessModel.bars.get(0).description;
                GD.temp_bar.open_time = GD.businessModel.bars.get(0).open_time;
                GD.temp_bar.galleryModel.background1 = GD.businessModel.bars.get(0).galleryModel.background1;
                GD.temp_bar.galleryModel.background2 = GD.businessModel.bars.get(0).galleryModel.background2;
                GD.temp_bar.galleryModel.background3 = GD.businessModel.bars.get(0).galleryModel.background3;
                GD.temp_bar.galleryModel.background4 = GD.businessModel.bars.get(0).galleryModel.background4;
                GD.temp_bar.galleryModel.background5 = GD.businessModel.bars.get(0).galleryModel.background5;
                GD.temp_bar.galleryModel.background6 = GD.businessModel.bars.get(0).galleryModel.background6;

                intent = new Intent(getApplicationContext(), EditAccountProfileScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
                break;

            case R.id.iv_edit_profile:

                GD.temp_bar = new EditBarModel();
                GD.temp_bar.bar_id = GD.businessModel.bars.get(0).bar_id;
                GD.temp_bar.business_name = GD.businessModel.bars.get(0).business_name;
                GD.temp_bar.category = GD.businessModel.bars.get(0).category;
                GD.temp_bar.post_code = GD.businessModel.bars.get(0).post_code;
                GD.temp_bar.address = GD.businessModel.bars.get(0).address;
                GD.temp_bar.lat = GD.businessModel.bars.get(0).lat;
                GD.temp_bar.lon = GD.businessModel.bars.get(0).lon;
                GD.temp_bar.telephone = GD.businessModel.bars.get(0).telephone;
                GD.temp_bar.music_type = GD.businessModel.bars.get(0).music_type;
                GD.temp_bar.website = GD.businessModel.bars.get(0).website;
                GD.temp_bar.email = GD.businessModel.bars.get(0).email;
                GD.temp_bar.description = GD.businessModel.bars.get(0).description;
                GD.temp_bar.open_time = GD.businessModel.bars.get(0).open_time;
                GD.temp_bar.galleryModel = GD.businessModel.bars.get(0).galleryModel;

                intent = new Intent(getApplicationContext(), EditAccountProfileScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
                break;
        }
    }

    // Refresh Dashboard
    private void refresh_dashboard() {

        LoadBusinessDealData();

        if (GD.businessModel.bars.get(0).deals.size() == 0 || GD.businessModel.bars.get(0).deals.size() == 1) {
            circle_background.setScaleX(2);
            circle_background.setBackgroundResource(R.drawable.circle_shape_small);
        } else {
            circle_background.setScaleX(3);
            circle_background.setBackgroundResource(R.drawable.circle_shape);
        }

        bar_title.setText(GD.businessModel.bars.get(0).business_name);

        if (GD.businessModel.bars.get(0).deals.size() == 0) {
//            account_statistics.setText("Account Statistics : Overview");
            account_statistics.setText("Account Statistics");
        } else {
//            account_statistics.setText("Account Statistics : Deal " + String.valueOf(GD.businessModel.bars.get(0).deals.size()));
            account_statistics.setText("Account Statistics");
        }

        int count_impressions = 0, count_in_wallet = 0, count_claimed = 0, count_engagement = 0;

        for (int i = 0; i < GD.businessModel.bars.get(0).deals.size(); i ++) {
            count_in_wallet = count_in_wallet + GD.businessModel.bars.get(0).deals.get(i).in_wallet;
            count_claimed = count_claimed + GD.businessModel.bars.get(0).deals.get(i).claimed;
        }

        try {
            count_impressions = GD.businessModel.bars.get(0).deals.get(0).impressions;
            count_engagement = GD.businessModel.bars.get(0).deals.get(0).engagement;
            count_claimed = count_claimed + GD.businessModel.bars.get(0).deals.get(0).clicks;
        } catch (Exception e) {

        }

        if (count_impressions == 0) {
            impressions.setTextColor(getResources().getColor(R.color.in_Dark));
        } else {
            impressions.setTextColor(getResources().getColor(R.color.White));
        }

        if (count_in_wallet + count_engagement == 0) {
            in_wallet.setTextColor(getResources().getColor(R.color.in_Dark));
        } else {
            in_wallet.setTextColor(getResources().getColor(R.color.White));
        }

        if (count_claimed == 0) {
            claimed.setTextColor(getResources().getColor(R.color.in_Dark));
        } else {
            claimed.setTextColor(getResources().getColor(R.color.White));
        }

        impressions.setText(String.valueOf(count_impressions));
        in_wallet.setText(String.valueOf(count_in_wallet + count_engagement));
        claimed.setText(String.valueOf(count_claimed));

        if (GD.businessModel.bars.get(0).deals.size() == 0) {
            comment_deal.setVisibility(View.VISIBLE);
        } else {
            comment_deal.setVisibility(View.GONE);
        }

        // Check percent of profile

        float percent_value = 0;
        if (!GD.businessModel.bars.get(0).business_name.equals("")) {
            percent_value += 12.5;
        }

        if (!GD.businessModel.bars.get(0).address.equals("")) {
            percent_value += 12.5;
        }

        if (!GD.businessModel.bars.get(0).telephone.equals("")) {
            percent_value += 12.5;
        }

        if (!GD.businessModel.bars.get(0).email.equals("")) {
            percent_value += 12.5;
        }

        if (!GD.businessModel.bars.get(0).description.equals("")) {
            percent_value += 12.5;
        }

        if (GD.businessModel.bars.get(0).category.equals("Nightlife")) {
            if (!GD.businessModel.bars.get(0).music_type.equals("")) {
                percent_value += 12.5;
            }
        } else {
            percent_value += 12.5;
        }

        if (GD.businessModel.bars.get(0).open_time.mon_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.mon_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.tue_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.tue_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.wed_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.wed_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.thur_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.thur_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.fri_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.fri_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.sat_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.sat_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.sun_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.sun_end.equals("")) {

        } else {
            percent_value += 12.5;
        }

        // Gallery
        ArrayList<String> gallery_list = new ArrayList<String>();
        if (!GD.businessModel.bars.get(0).galleryModel.background1.equals("")) {
            gallery_list.add(GD.businessModel.bars.get(0).galleryModel.background1);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background2.equals("")) {
            gallery_list.add(GD.businessModel.bars.get(0).galleryModel.background2);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background3.equals("")) {
            gallery_list.add(GD.businessModel.bars.get(0).galleryModel.background3);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background4.equals("")) {
            gallery_list.add(GD.businessModel.bars.get(0).galleryModel.background4);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background5.equals("")) {
            gallery_list.add(GD.businessModel.bars.get(0).galleryModel.background5);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background6.equals("")) {
            gallery_list.add(GD.businessModel.bars.get(0).galleryModel.background6);
        }

        if (gallery_list.size() != 0) {
            percent_value += 12.5;
        }

        if (percent_value == 100) {
            percent_profile.setVisibility(View.GONE);
            comment_profile.setVisibility(View.GONE);
            profile_progress.setVisibility(View.GONE);
        } else {
            profile_progress.setProgress(Math.round(percent_value));
            percent_profile.setText("Your profile is " + String.valueOf(Math.round(percent_value)) + "% complete");
        }

        if (!GD.businessModel.bars.get(0).business_name.equals("")) {
            business_name.setText(GD.businessModel.bars.get(0).business_name);
            ch_business_name.setBackgroundResource(R.mipmap.ico_check);
        } else {
            ch_business_name.setBackgroundResource(R.mipmap.ico_minus);
        }

        if (!GD.businessModel.bars.get(0).category.equals("")) {
            category.setText(GD.businessModel.bars.get(0).category);
            ch_category.setBackgroundResource(R.mipmap.ico_check);
        } else {
            ch_category.setBackgroundResource(R.mipmap.ico_minus);
        }

        if (!GD.businessModel.bars.get(0).business_name.equals("")) {
            business_name.setText(GD.businessModel.bars.get(0).business_name);
            ch_business_name.setBackgroundResource(R.mipmap.ico_check);
        } else {
            ch_business_name.setBackgroundResource(R.mipmap.ico_minus);
        }

        if (!GD.businessModel.bars.get(0).address.equals("")) {
            address.setText(GD.businessModel.bars.get(0).address + " " +GD.businessModel.bars.get(0).post_code);
            ch_address.setBackgroundResource(R.mipmap.ico_check);
        } else {
            ch_address.setBackgroundResource(R.mipmap.ico_minus);
        }

        if (!GD.businessModel.bars.get(0).telephone.equals("")) {
            telephone.setText(GD.businessModel.bars.get(0).telephone);
            ch_phonenumber.setBackgroundResource(R.mipmap.ico_check);
        } else {
            ch_phonenumber.setBackgroundResource(R.mipmap.ico_minus);
        }

        if (!GD.businessModel.bars.get(0).email.equals("")) {
            email_address.setText(GD.businessModel.bars.get(0).email);
            ch_email.setBackgroundResource(R.mipmap.ico_check);
        } else {
            ch_email.setBackgroundResource(R.mipmap.ico_minus);
        }

        if (!GD.businessModel.bars.get(0).description.equals("")) {
            description.setText(GD.businessModel.bars.get(0).description);
            ch_description.setBackgroundResource(R.mipmap.ico_check);
        } else {
            ch_description.setBackgroundResource(R.mipmap.ico_minus);
        }

        if (GD.businessModel.bars.get(0).category.equals("Nightlife")) {
            music_type.setVisibility(View.VISIBLE);
            ch_music_type.setVisibility(View.VISIBLE);
            if (!GD.businessModel.bars.get(0).music_type.equals("")) {
                music_type.setText(GD.businessModel.bars.get(0).music_type);
                ch_music_type.setBackgroundResource(R.mipmap.ico_check);
            } else {
                ch_music_type.setBackgroundResource(R.mipmap.ico_minus);
            }
        } else {
            music_type.setVisibility(View.GONE);
            ch_music_type.setVisibility(View.GONE);
        }

        // Open hours

        if (GD.businessModel.bars.get(0).open_time.mon_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.mon_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.tue_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.tue_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.wed_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.wed_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.thur_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.thur_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.fri_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.fri_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.sat_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.sat_end.equals("") &&
                GD.businessModel.bars.get(0).open_time.sun_start.equals("") &&
                GD.businessModel.bars.get(0).open_time.sun_end.equals("")) {
            ch_open_hour.setBackgroundResource(R.mipmap.ico_minus);
        } else {
            ch_open_hour.setBackgroundResource(R.mipmap.ico_check);
            String monday = "", tuesday = "", wednesday = "", thurday = "", friday = "", saturday = "", sunday = "";
            if (GD.businessModel.bars.get(0).open_time.mon_start.equals("") && GD.businessModel.bars.get(0).open_time.mon_end.equals("")) {
                monday = "Closed";
            } else {
                monday = GD.businessModel.bars.get(0).open_time.mon_start + " - " + GD.businessModel.bars.get(0).open_time.mon_end;
            }

            if (GD.businessModel.bars.get(0).open_time.tue_start.equals("") && GD.businessModel.bars.get(0).open_time.tue_end.equals("")) {
                tuesday = "Closed";
            } else {
                tuesday = GD.businessModel.bars.get(0).open_time.tue_start + " - " + GD.businessModel.bars.get(0).open_time.tue_end;
            }

            if (GD.businessModel.bars.get(0).open_time.wed_start.equals("") && GD.businessModel.bars.get(0).open_time.wed_end.equals("")) {
                wednesday = "Closed";
            } else {
                wednesday = GD.businessModel.bars.get(0).open_time.wed_start + " - " + GD.businessModel.bars.get(0).open_time.wed_end;
            }

            if (GD.businessModel.bars.get(0).open_time.thur_start.equals("") && GD.businessModel.bars.get(0).open_time.thur_end.equals("")) {
                thurday = "Closed";
            } else {
                thurday = GD.businessModel.bars.get(0).open_time.thur_start + " - " + GD.businessModel.bars.get(0).open_time.thur_end;
            }

            if (GD.businessModel.bars.get(0).open_time.fri_start.equals("") && GD.businessModel.bars.get(0).open_time.fri_end.equals("")) {
                friday = "Closed";
            } else {
                friday = GD.businessModel.bars.get(0).open_time.fri_start + " - " + GD.businessModel.bars.get(0).open_time.fri_end;
            }

            if (GD.businessModel.bars.get(0).open_time.sat_start.equals("") && GD.businessModel.bars.get(0).open_time.sat_end.equals("")) {
                saturday = "Closed";
            } else {
                saturday = GD.businessModel.bars.get(0).open_time.sat_start + " - " + GD.businessModel.bars.get(0).open_time.sat_end;
            }

            if (GD.businessModel.bars.get(0).open_time.sun_start.equals("") && GD.businessModel.bars.get(0).open_time.sun_end.equals("")) {
                sunday = "Closed";
            } else {
                sunday = GD.businessModel.bars.get(0).open_time.sun_start + " - " + GD.businessModel.bars.get(0).open_time.sun_end;
            }

            opening_hours.setText("Monday        : " + monday + "\n"
                    + "Tuesday       : "  + tuesday + "\n"
                    + "Wednesday : "  + wednesday + "\n"
                    + "Thursday     : "  + thurday + "\n"
                    + "Friday           : "  + friday + "\n"
                    + "Saturday      : "  + saturday + "\n"
                    + "Sunday         : "  + sunday);
        }

        if (gallery_list.size() == 0) {

        } else {
            ch_gallery.setBackgroundResource(R.mipmap.ico_check);
            gallery_image.setText("Image gallery");
            if (gallery_list.size() == 1) {
                layout_gallery1.setVisibility(View.VISIBLE);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(0)).into(image_gallery1);

            } else if (gallery_list.size() == 2) {
                layout_gallery1.setVisibility(View.VISIBLE);
                layout_gallery2.setVisibility(View.VISIBLE);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(0)).into(image_gallery1);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(1)).into(image_gallery2);
            } else if (gallery_list.size() == 3) {
                layout_gallery1.setVisibility(View.VISIBLE);
                layout_gallery2.setVisibility(View.VISIBLE);
                layout_gallery3.setVisibility(View.VISIBLE);

                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(0)).into(image_gallery1);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(1)).into(image_gallery2);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(2)).into(image_gallery3);
            } else if (gallery_list.size() == 4) {
                layout_gallery1.setVisibility(View.VISIBLE);
                layout_gallery2.setVisibility(View.VISIBLE);
                layout_gallery3.setVisibility(View.VISIBLE);
                layout_gallery4.setVisibility(View.VISIBLE);

                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(0)).into(image_gallery1);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(1)).into(image_gallery2);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(2)).into(image_gallery3);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(3)).into(image_gallery4);
            } else if (gallery_list.size() == 5) {
                layout_gallery1.setVisibility(View.VISIBLE);
                layout_gallery2.setVisibility(View.VISIBLE);
                layout_gallery3.setVisibility(View.VISIBLE);
                layout_gallery4.setVisibility(View.VISIBLE);
                layout_gallery5.setVisibility(View.VISIBLE);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(0)).into(image_gallery1);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(1)).into(image_gallery2);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(2)).into(image_gallery3);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(3)).into(image_gallery4);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(4)).into(image_gallery5);
            } else if (gallery_list.size() == 6) {
                layout_gallery1.setVisibility(View.VISIBLE);
                layout_gallery2.setVisibility(View.VISIBLE);
                layout_gallery3.setVisibility(View.VISIBLE);
                layout_gallery4.setVisibility(View.VISIBLE);
                layout_gallery5.setVisibility(View.VISIBLE);
                layout_gallery6.setVisibility(View.VISIBLE);

                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(0)).into(image_gallery1);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(1)).into(image_gallery2);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(2)).into(image_gallery3);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(3)).into(image_gallery4);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(4)).into(image_gallery5);
                Picasso.with(this).load(GD.downloadUrl + gallery_list.get(5)).into(image_gallery6);
            }
        }
    }

    private void LoadBusinessDealData() {
        businessDealAdapter.clearFeedList();
        businessDealAdapter.notifyDataSetChanged();

        for (int i = 0 ; i < GD.businessModel.bars.get(0).deals.size(); i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.businessModel.bars.get(0).deals.get(i).deal_id ;
            dealModel.deal_title = GD.businessModel.bars.get(0).deals.get(i).deal_title ;
            dealModel.deal_description = GD.businessModel.bars.get(0).deals.get(i).deal_description ;
            dealModel.deal_duration = GD.businessModel.bars.get(0).deals.get(i).deal_duration;
            dealModel.deal_days = GD.businessModel.bars.get(0).deals.get(i).deal_days;
            dealModel.impressions = GD.businessModel.bars.get(0).deals.get(i).impressions ;
            dealModel.in_wallet = GD.businessModel.bars.get(0).deals.get(i).in_wallet + GD.businessModel.bars.get(0).deals.get(i).engagement;
            dealModel.claimed = GD.businessModel.bars.get(0).deals.get(i).claimed + GD.businessModel.bars.get(0).deals.get(i).clicks;
            businessDealAdapter.addFeedList(dealModel);
            businessDealAdapter.notifyDataSetChanged();
        }
        setListViewHeightBasedOnChildren();
    }

    public static void setListViewHeightBasedOnChildren() {
        int totoalheight = 0 ;
        int count = 0 ;
        count = businessDealAdapter.getCount();
        //it is the ListView Height
        for (int i = 0, len = businessDealAdapter.getCount(); i < len; i++) {

            View listItem = businessDealAdapter.getView(i, null, business_deals);
            int dd_height = listItem.getMeasuredHeight();
            listItem.measure(0, 0);
            int list_child_item_height = listItem.getMeasuredHeight() + business_deals.getDividerHeight();//item height
            totoalheight += list_child_item_height; //
        }
        business_deals.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,totoalheight));
        businessDealAdapter.notifyDataSetChanged();
        business_deals.setFocusable(false);
    }

    private BusinessDealAdapter.OnClickButtonEditDealListListener editdeallistener = new BusinessDealAdapter.OnClickButtonEditDealListListener() {
        @Override
        public void onClick(int pos) {
            GD.edit_deal_pos = pos;
            GD.add_edit_deal = 2;
            Intent intent = new Intent(getApplicationContext(), AddDealScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
            finish();
        }
    };

    private BusinessDealAdapter.OnClickButtonEditDealListListener removeDealListener= new BusinessDealAdapter.OnClickButtonEditDealListListener() {
        @Override
        public void onClick(int pos) {
            GD.edit_deal_pos = pos;
            RemoveDealPopup();
        }
    };

    private void RemoveDealPopup(){
        popupdailog = new Dialog(this);
        popupdailog.requestWindowFeature(1);
        popupdailog.setCancelable(true);
        popupdailog.setContentView(R.layout.popup_remove_deal);
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
                callRemoveDealAPI();
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
    public void onRefresh() {
        call_refresh_api();
    }

    private void call_refresh_api() {
        String url = GD.BaseUrl + "refresh_owner.php";

        String request = "{\"user_id\":\""+GD.businessModel.user_id+"\"}";

        new refreshWorker().execute(url, request);
    }

    public class refreshWorker extends AsyncTask<String, String, String> {
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
                    JSONObject refreshJson = new JSONObject(response);
                    String success = refreshJson.getString("success");
                    if (success.equals("true")) {

                        GD.businessModel = new BusinessModel();
                        GD.businessModel.user_id = refreshJson.getInt("user_id");
                        GD.businessModel.username = refreshJson.getString("username");
                        GD.businessModel.email = refreshJson.getString("email");
                        GD.businessModel.telephone = refreshJson.getString("telephone");
                        JSONArray jsonArray = refreshJson.getJSONArray("bars");
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BarModel barModel = new BarModel();
                            barModel.bar_id = jsonObject.getInt("bar_id");
                            barModel.business_name = jsonObject.getString("business_name");
                            barModel.post_code = jsonObject.getString("post_code");
                            barModel.address = jsonObject.getString("address");
                            barModel.lat = jsonObject.getString("lat");
                            barModel.lon = jsonObject.getString("lon");
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
                                dealModel.engagement = jsonObject3.getInt("engagement");
                                dealModel.clicks = jsonObject3.getInt("clicks");
                                dealModel.deal_days.monday = jsonObject3.getJSONObject("deal_days").getBoolean("monday");
                                dealModel.deal_days.tuesday = jsonObject3.getJSONObject("deal_days").getBoolean("tuesday");
                                dealModel.deal_days.wednesday = jsonObject3.getJSONObject("deal_days").getBoolean("wednesday");
                                dealModel.deal_days.thursday = jsonObject3.getJSONObject("deal_days").getBoolean("thursday");
                                dealModel.deal_days.friday = jsonObject3.getJSONObject("deal_days").getBoolean("friday");
                                dealModel.deal_days.saturday = jsonObject3.getJSONObject("deal_days").getBoolean("saturday");
                                dealModel.deal_days.sunday = jsonObject3.getJSONObject("deal_days").getBoolean("sunday");

                                barModel.deals.add(dealModel);
                            }

                            GD.businessModel.bars.add(barModel);
                        }
                        refreshLayout.setRefreshing(false);
                        refresh_dashboard();
                    } else {
                        refreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {

                }
            } else {
                refreshLayout.setRefreshing(false);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            refreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }

    private void callRemoveDealAPI() {
        String url = GD.BaseUrl + "delete_deal.php";

        String request = "{\"user_id\":\""+GD.businessModel.user_id+"\", \"deal_id\":\"" + GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_id + "\"}";

        new removeDealWorker().execute(url, request);
    }

    public class removeDealWorker extends AsyncTask<String, String, String> {
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
                    JSONObject refreshJson = new JSONObject(response);
                    String success = refreshJson.getString("success");
                    if (success.equals("true")) {

                        GD.businessModel = new BusinessModel();
                        GD.businessModel.user_id = refreshJson.getInt("user_id");
                        GD.businessModel.username = refreshJson.getString("username");
                        GD.businessModel.email = refreshJson.getString("email");
                        GD.businessModel.telephone = refreshJson.getString("telephone");
                        JSONArray jsonArray = refreshJson.getJSONArray("bars");
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BarModel barModel = new BarModel();
                            barModel.bar_id = jsonObject.getInt("bar_id");
                            barModel.business_name = jsonObject.getString("business_name");
                            barModel.post_code = jsonObject.getString("post_code");
                            barModel.address = jsonObject.getString("address");
                            barModel.lat = jsonObject.getString("lat");
                            barModel.lon = jsonObject.getString("lon");
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
                                dealModel.engagement = jsonObject3.getInt("engagement");
                                dealModel.deal_days.monday = jsonObject3.getJSONObject("deal_days").getBoolean("monday");
                                dealModel.deal_days.tuesday = jsonObject3.getJSONObject("deal_days").getBoolean("tuesday");
                                dealModel.deal_days.wednesday = jsonObject3.getJSONObject("deal_days").getBoolean("wednesday");
                                dealModel.deal_days.thursday = jsonObject3.getJSONObject("deal_days").getBoolean("thursday");
                                dealModel.deal_days.friday = jsonObject3.getJSONObject("deal_days").getBoolean("friday");
                                dealModel.deal_days.saturday = jsonObject3.getJSONObject("deal_days").getBoolean("saturday");
                                dealModel.deal_days.sunday = jsonObject3.getJSONObject("deal_days").getBoolean("sunday");

                                barModel.deals.add(dealModel);
                            }

                            GD.businessModel.bars.add(barModel);
                        }

                        refresh_dashboard();

                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                    dialog.dismiss();
                }
            } else {
                dialog.dismiss();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            refreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }
}
