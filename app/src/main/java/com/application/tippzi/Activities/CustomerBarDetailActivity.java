package com.application.tippzi.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Adapters.DetailBarAdapter;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.EngagementModel;
import com.application.tippzi.R;
import com.application.tippzi.swipeview.SwipeAdapterView;
import com.google.gson.Gson;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerBarDetailActivity extends AbstractActivity implements SwipeAdapterView.onSwipeListener {

    private RollPagerView mViewPager;
    private ImageView bardetail_back ;
    private TextView count_page ;
    private ArrayList<String> bitmapArrayList = new ArrayList<>();
    //Swipe view
    private ArrayList<DealModel> dataList;
    private DetailBarAdapter dummyAdapter;
    private SwipeAdapterView cardsContainer;
    private TextView mon_open_time, tue_open_time, wed_open_time, thu_open_time, fri_open_time, sat_open_time, sun_open_time, phonenumber, emailaddress, barTitle, business_address ;
    private LinearLayout lay_background ;
    private boolean flag_scroll = true;
    private int count_engagement = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bar_details);

        TextView bar_name = findViewById(R.id.tv_title_cliam);

        final ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
        lay_background = (LinearLayout) findViewById(R.id.lay_background) ;

        final GifImageView gif_image = (GifImageView) findViewById(R.id.iv_scroll_up);
        gif_image.setVisibility(View.VISIBLE);
        gif_image.setBackgroundResource(R.mipmap.ico_allow_gif);
        gif_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag_scroll == true) {
                    flag_scroll = false;
                    gif_image.setVisibility(View.VISIBLE);
                    gif_image.setBackgroundResource(R.mipmap.ico_allow_gif);
                    sv.scrollTo(0, sv.getBottom());
                } else {
                    flag_scroll = true;
//                    gif_image.setVisibility(View.GONE);
                    sv.scrollTo(sv.getBottom(), 0);
                }
            }
        });

        if (GD.barModels.get(GD.select_pos).business_name.length() > 24) {
            bar_name.setText(GD.barModels.get(GD.select_pos).business_name.substring(0, 24) + "...");
        } else {
            bar_name.setText(GD.barModels.get(GD.select_pos).business_name);
        }

        TextView open_time = findViewById(R.id.tv_open_time_claim);
        TextView walk_time = findViewById(R.id.tv_walk_time_claim);
        walk_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count_engagement = count_engagement + 1;
                Intent routeviewintent = new Intent(getApplicationContext(), GoogleRouteActivity.class);
                startActivity(routeviewintent);
                finish();
            }
        });

        TextView business_description = (TextView) findViewById(R.id.tv_business_description_customer);
        business_description.setText(GD.barModels.get(GD.select_pos).description);

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
        }

        if (opentime.equals(" - ")) {
            open_time.setText("Closed");
        } else {
            open_time.setText(opentime);
        }

        if (cal_walk_time(GD.barModels.get(GD.select_pos).distance) > 20) {
            walk_time.setText("20 +" + " mins walk");
        } else {
            walk_time.setText(String.valueOf(cal_walk_time(GD.barModels.get(GD.select_pos).distance)) + " mins walk");
        }

        GD.slide_index = 0 ;
        //swipe part
        ButterKnife.bind(this);

        //background image part
        mViewPager = findViewById(R.id.view_pager);
        count_page = findViewById(R.id.tv_count_page);
        bardetail_back = findViewById(R.id.iv_back_bar_details);
        cardsContainer = findViewById(R.id.container);

        getBarDeatilData();
        setAdapters();
        setListeners();

        bitmapArrayList = new ArrayList<>();
        if (!GD.barModels.get(GD.select_pos).galleryModel.background1.equals("")){

            bitmapArrayList.add(GD.downloadUrl + GD.barModels.get(GD.select_pos).galleryModel.background1);
        }
        if (!GD.barModels.get(GD.select_pos).galleryModel.background2.equals("")){
            bitmapArrayList.add(GD.downloadUrl + GD.barModels.get(GD.select_pos).galleryModel.background2);
        }
        if (!GD.barModels.get(GD.select_pos).galleryModel.background3.equals("")){
            bitmapArrayList.add(GD.downloadUrl + GD.barModels.get(GD.select_pos).galleryModel.background3);
        }
        if (!GD.barModels.get(GD.select_pos).galleryModel.background4.equals("")){
            bitmapArrayList.add(GD.downloadUrl + GD.barModels.get(GD.select_pos).galleryModel.background4);
        }
        if (!GD.barModels.get(GD.select_pos).galleryModel.background5.equals("")){
            bitmapArrayList.add(GD.downloadUrl + GD.barModels.get(GD.select_pos).galleryModel.background5);
        }
        if (!GD.barModels.get(GD.select_pos).galleryModel.background6.equals("")){
            bitmapArrayList.add(GD.downloadUrl + GD.barModels.get(GD.select_pos).galleryModel.background6);
        }
        if (bitmapArrayList.size() == 1) {
            mViewPager.setPlayDelay(0);
            mViewPager.pause();
        }
        if (bitmapArrayList.size() == 0) {
            lay_background.setVisibility(View.GONE);
        }
        mViewPager.setAdapter(new ImageLoopAdapter(mViewPager, bitmapArrayList));
        if (bitmapArrayList.size() == 1) {
            mViewPager.pause();
        }

        bardetail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomerMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.barModels.get(GD.select_pos).deals.size()) + " deals");

        mon_open_time = findViewById(R.id.tv_mon);
        tue_open_time = findViewById(R.id.tv_tue);
        wed_open_time = findViewById(R.id.tv_wed);
        thu_open_time = findViewById(R.id.tv_thu);
        fri_open_time = findViewById(R.id.tv_fri);
        sat_open_time = findViewById(R.id.tv_sat);
        sun_open_time = findViewById(R.id.tv_sun);

        phonenumber = findViewById(R.id.bus_telephone);
        emailaddress = findViewById(R.id.bus_email_address);
        barTitle = findViewById(R.id.business_bar_title);
        business_address = findViewById(R.id.business_bar_address);

        phonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count_engagement = count_engagement + 1;
                if (!phonenumber.getText().toString().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phonenumber.getText().toString()));
                    startActivity(intent);
                }
            }
        });

        emailaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count_engagement = count_engagement + 1;
                if (!emailaddress.getText().toString().equals("")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + emailaddress.getText().toString()));

                    try {
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException e) {
                        //TODO: Handle case where no email app is available
                    }
                }
            }
        });

        business_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count_engagement = count_engagement + 1;
                if (!business_address.getText().toString().equals("")) {
                    Intent intent = new Intent(getApplicationContext(), CustomerMapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                    finish();
                    callEngagementAPI();
                }
            }
        });

        String mon_opentime = GD.barModels.get(GD.select_pos).open_time.mon_start + " - " + GD.barModels.get(GD.select_pos).open_time.mon_end ;
        String tue_opentime = GD.barModels.get(GD.select_pos).open_time.tue_start + " - " + GD.barModels.get(GD.select_pos).open_time.tue_end ;
        String wed_opentime = GD.barModels.get(GD.select_pos).open_time.wed_start + " - " + GD.barModels.get(GD.select_pos).open_time.wed_end ;
        String thu_opentime = GD.barModels.get(GD.select_pos).open_time.thur_start + " - " + GD.barModels.get(GD.select_pos).open_time.thur_end;
        String fri_opentime = GD.barModels.get(GD.select_pos).open_time.fri_start + " - " + GD.barModels.get(GD.select_pos).open_time.fri_end ;
        String sat_opentime = GD.barModels.get(GD.select_pos).open_time.sat_start + " - " + GD.barModels.get(GD.select_pos).open_time.sat_end ;
        String sun_opentime = GD.barModels.get(GD.select_pos).open_time.sun_start + " - " + GD.barModels.get(GD.select_pos).open_time.sun_end ;

        if (mon_opentime.equals(" - "))
            mon_opentime = "Closed" ;
        if (tue_opentime.equals(" - "))
            tue_opentime = "Closed" ;
        if (wed_opentime.equals(" - "))
            wed_opentime = "Closed" ;
        if (thu_opentime.equals(" - "))
            thu_opentime = "Closed" ;
        if (fri_opentime.equals(" - "))
            fri_opentime = "Closed" ;
        if (sat_opentime.equals(" - "))
            sat_opentime = "Closed" ;
        if (sun_opentime.equals(" - "))
            sun_opentime = "Closed" ;


        mon_open_time.setText(mon_opentime);
        tue_open_time.setText(tue_opentime);
        wed_open_time.setText(wed_opentime);
        thu_open_time.setText(thu_opentime);
        fri_open_time.setText(fri_opentime);
        sat_open_time.setText(sat_opentime);
        sun_open_time.setText(sun_opentime);
        phonenumber.setText(GD.barModels.get(GD.select_pos).telephone);
        emailaddress.setText(GD.barModels.get(GD.select_pos).email);
        barTitle.setText(GD.barModels.get(GD.select_pos).business_name);
        business_address.setText(GD.barModels.get(GD.select_pos).address);
    }


    private int cal_walk_time(double distance) {
        String time = "";

        float walk_sec = ((float)distance * 1000) / 2.5f;

        int temp = Math.round(walk_sec);

        int mins = temp / 60;

        return mins;
    }

    private class ImageLoopAdapter extends LoopPagerAdapter {

        ArrayList<String> mBitmapArrayList = new ArrayList<>();

        public ImageLoopAdapter(RollPagerView viewPager, ArrayList<String> bitmapArrayList1) {
            super(viewPager);
            viewPager.setAnimationDurtion(200);
            mBitmapArrayList = bitmapArrayList1 ;
            if (mBitmapArrayList.size() == 1){
                viewPager.pause();
            }
        }

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);

            if (mBitmapArrayList.size() > 0) {
                Picasso.with(getApplicationContext()).load(mBitmapArrayList.get(position)).into(view);
            }

            return view;
        }

        @Override
        public int getRealCount() {
            return mBitmapArrayList.size();
        }
    }

    private Bitmap resizeBitmap(Bitmap origin_bitmap){
        Matrix matrix = new Matrix();
        float scale = 1024/origin_bitmap.getWidth();
        float xTranslation = 0.0f, yTranslation = (720 - origin_bitmap.getHeight() * scale)/2.0f;
        RectF drawableRect = new RectF(0, 0, origin_bitmap.getWidth()-100,
                origin_bitmap.getHeight()-100);

        RectF viewRect = new RectF(0, 0, origin_bitmap.getWidth(),
                origin_bitmap.getHeight());

        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
        matrix.setRotate(90, origin_bitmap.getWidth(), origin_bitmap.getHeight());
        matrix.postTranslate(xTranslation, yTranslation);
        matrix.preScale(scale, scale);
        return origin_bitmap ;
    }

    //swipe part

    @Override
    public void removeFirstObjectInAdapter() {
        dataList.remove(0);
        dummyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLeftCardExit(Object dataObject) {
        if (GD.slide_index == GD.barModels.get(GD.select_pos).deals.size()-1) {
            int first_index = 0 ;
            int last_index = GD.barModels.get(GD.select_pos).deals.size() ;
            ResetCardViewData(first_index, last_index, 2);
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.barModels.get(GD.select_pos).deals.size()) + " deals");
        } else {
            GD.slide_index++;
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.barModels.get(GD.select_pos).deals.size()) + " deals");
        }
    }

    @Override
    public void onRightCardExit(Object dataObject) {
        if (GD.slide_index == GD.barModels.get(GD.select_pos).deals.size()-1) {
            int first_index = 0 ;
            int last_index = GD.barModels.get(GD.select_pos).deals.size() ;
            ResetCardViewData(first_index, last_index, 2);
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.barModels.get(GD.select_pos).deals.size()) + " deals");
        } else {
            GD.slide_index++;
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.barModels.get(GD.select_pos).deals.size()) + " deals");
        }
    }

    @Override
    public void onAdapterAboutToEmpty(int itemsInAdapter) {
    }

    @Override
    public void onScroll(float scrollProgressPercent) {
    }


    @OnClick(R.id.iv_right)
    public void right() {
        if (GD.slide_index > GD.barModels.get(GD.select_pos).deals.size()-1) {
            int first_index = GD.slide_index - 1 ;
            int last_index = GD.barModels.get(GD.select_pos).deals.size() ;
            ResetCardViewData(first_index, last_index, 2);
        } else if (GD.slide_index == GD.barModels.get(GD.select_pos).deals.size() - 1) {
            cardsContainer.getTopCardListener().selectRight();
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.barModels.get(GD.select_pos).deals.size() + " deals"));
        } else {
            cardsContainer.getTopCardListener().selectRight();
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.barModels.get(GD.select_pos).deals.size()) + " deals");
        }
    }

    @OnClick(R.id.iv_left)
    public void left() {
        dataList = new ArrayList<>();
        if (GD.slide_index != 0) {
            int first_index = GD.slide_index - 1 ;
            int last_index = GD.barModels.get(GD.select_pos).deals.size() ;
            ResetCardViewData(first_index, last_index, 1);
        } else if ( GD.slide_index == 0){
            int first_index = 0 ;
            int last_index =  GD.barModels.get(GD.select_pos).deals.size();
            ResetCardViewData(first_index,last_index,2);
        }
    }

    private void ResetCardViewData(int first_index, int last_index, int flag) {
        for (int i = first_index; i < last_index; i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.barModels.get(GD.select_pos).deals.get(i).deal_id;
            dealModel.deal_title = GD.barModels.get(GD.select_pos).deals.get(i).deal_title;
            dealModel.deal_description = GD.barModels.get(GD.select_pos).deals.get(i).deal_description;
            dealModel.deal_days = GD.barModels.get(GD.select_pos).deals.get(i).deal_days;
            dealModel.deal_duration = GD.barModels.get(GD.select_pos).deals.get(i).deal_duration;
            dealModel.deal_qty = GD.barModels.get(GD.select_pos).deals.get(i).deal_qty;
            dealModel.claimed = GD.barModels.get(GD.select_pos).deals.get(i).claimed;
            dealModel.in_wallet = GD.barModels.get(GD.select_pos).deals.get(i).in_wallet;
            dealModel.impressions = GD.barModels.get(GD.select_pos).deals.get(i).impressions;
            dealModel.claim_check = GD.barModels.get(GD.select_pos).deals.get(i).claim_check;
            dealModel.index_number = i;
            dataList.add(dealModel);
        }
        if (flag == 1) {
            cardsContainer.undo();
            GD.slide_index--;
        } else if (flag == 2) {
            GD.slide_index = 0;
        }
        dummyAdapter = new DetailBarAdapter(this, dataList);
        cardsContainer.setAdapter(dummyAdapter);
        dummyAdapter.notifyDataSetChanged();
        count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.barModels.get(GD.select_pos).deals.size()) + " deals");
        dummyAdapter.setOnClickButtonListener(claimdealitem);
    }

    private void setAdapters() {
        dummyAdapter = new DetailBarAdapter(this, dataList);
        cardsContainer.setAdapter(dummyAdapter);
        dummyAdapter.setOnClickButtonListener(claimdealitem);
    }

    private void setListeners() {
        cardsContainer.setSwipeListener(this);
    }

    private void getBarDeatilData() {
        dataList = new ArrayList<>();
        for (int i = 0 ; i < GD.barModels.get(GD.select_pos).deals.size() ; i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.barModels.get(GD.select_pos).deals.get(i).deal_id ;
            dealModel.deal_title = GD.barModels.get(GD.select_pos).deals.get(i).deal_title ;
            dealModel.deal_description = GD.barModels.get(GD.select_pos).deals.get(i).deal_description ;
            dealModel.deal_days = GD.barModels.get(GD.select_pos).deals.get(i).deal_days ;
            dealModel.deal_duration = GD.barModels.get(GD.select_pos).deals.get(i).deal_duration ;
            dealModel.deal_qty = GD.barModels.get(GD.select_pos).deals.get(i).deal_qty ;
            dealModel.claimed = GD.barModels.get(GD.select_pos).deals.get(i).claimed ;
            dealModel.in_wallet = GD.barModels.get(GD.select_pos).deals.get(i).in_wallet ;
            dealModel.impressions = GD.barModels.get(GD.select_pos).deals.get(i).impressions ;
            dealModel.claim_check = GD.barModels.get(GD.select_pos).deals.get(i).claim_check ;
            dealModel.index_number = i ;
            dataList.add(dealModel);
        }
        GD.slide_index = 0 ;
    }

    private DetailBarAdapter.OnClickButtonSetListListener claimdealitem = new DetailBarAdapter.OnClickButtonSetListListener() {
        @Override
        public void onClick(int pos) {
            GD.slide_index = pos;
            GD.check_wallet = false;
            Intent intent = new Intent(getApplicationContext(), TicketScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
            finish();
            if (count_engagement == 0) {

            } else {
                callEngagementAPI();
            }
        }
    };

    private void callEngagementAPI() {
        String url = GD.BaseUrl + "add_engagement.php";
        EngagementModel engagementModel = new EngagementModel();
        engagementModel.user_id = GD.customerModel.user_id;
        engagementModel.bar_id = GD.barModels.get(GD.select_pos).bar_id;
        engagementModel.qty = count_engagement;

        Gson gson = new Gson();
        String json = gson.toJson(engagementModel);

        new EngagementWorker().execute(url, json);
    }

    public class EngagementWorker extends AsyncTask<String, String, String> {
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
                    JSONObject signupJson = new JSONObject(response);
                    String success = signupJson.getString("success");
                    if (success.equals("true")) {

                    } else {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), CustomerMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
        finish();
    }
}