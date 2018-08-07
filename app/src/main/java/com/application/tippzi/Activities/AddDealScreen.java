package com.application.tippzi.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.BusinessModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.EditDealModel;
import com.application.tippzi.Models.RegisterDealModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.application.tippzi.Global.CF.EMOJI_FILTER;

public class AddDealScreen extends AbstractActivity implements View.OnClickListener {

    private EditText title, description, quantity;

    private TextView add_deal, duration, select_all, mon, tue, wed, thur, fri, sat, sun;

    private Boolean flag_mon = false, flag_tues = false, flag_wednes = false, flag_thurs = false, flag_fri = false, flag_satur = false, flag_sun = false;

    private SimpleDateFormat dateFormatter;
    private Calendar date;

    private ACProgressFlower dialog;

    private boolean select_days = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Registering...")
                .build();

        TextView bar_title = findViewById(R.id.tv_bar_title_add_deal);
        bar_title.setText(GD.businessModel.bars.get(0).business_name);

        title = findViewById(R.id.ed_deal_title_add_deal);
        title.setOnClickListener(this);
        description = findViewById(R.id.ed_deal_description_add_deal);
        description.setOnClickListener(this);
        duration = findViewById(R.id.ed_duration_add_deal);
        duration.setOnClickListener(this);
        quantity = findViewById(R.id.ed_quantity_add_deal);
        quantity.setOnClickListener(this);

        title.setFilters(new InputFilter[]{EMOJI_FILTER});
        description.setFilters(new InputFilter[]{EMOJI_FILTER});
        quantity.setFilters(new InputFilter[]{EMOJI_FILTER});

        quantity.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});

        mon = findViewById(R.id.mon_add_deal);
        mon.setOnClickListener(this);
        tue = findViewById(R.id.tue_add_deal);
        tue.setOnClickListener(this);
        wed = findViewById(R.id.wed_add_deal);
        wed.setOnClickListener(this);
        thur = findViewById(R.id.thur_add_deal);
        thur.setOnClickListener(this);
        fri = findViewById(R.id.fri_add_deal);
        fri.setOnClickListener(this);
        sat = findViewById(R.id.sat_add_deal);
        sat.setOnClickListener(this);
        sun = findViewById(R.id.sun_add_deal);
        sun.setOnClickListener(this);

        add_deal = findViewById(R.id.btn_add_deal);
        add_deal.setOnClickListener(this);

        select_all = findViewById(R.id.tv_select_all);
        select_all.setOnClickListener(this);

        ImageView back = findViewById(R.id.iv_back_add_deal);
        back.setOnClickListener(this);

        TextView title = findViewById(R.id.tv_title_add_deal);

        if (GD.add_edit_deal == 2) {
            title.setText("Edit Deal");
            add_deal.setText("Edit Deal");
            refresh_screen();
        }
    }

    private void refresh_screen() {

        title.setBackgroundResource(R.drawable.rect_edittext_active);
        title.setHintTextColor(getResources().getColor(R.color.Dark));

        title.setText(GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_title);

        description.setEnabled(true);
        description.setBackgroundResource(R.drawable.rect_edittext_active);
        description.setHintTextColor(getResources().getColor(R.color.Dark));

        description.setText(GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_description);

        quantity.setEnabled(true);
        quantity.setBackgroundResource(R.drawable.rect_edittext_active);
        quantity.setHintTextColor(getResources().getColor(R.color.Dark));

        quantity.setText(String.valueOf(GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_qty));

        duration.setEnabled(true);
        duration.setText(GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_duration);
        duration.setBackgroundResource(R.drawable.rect_edittext_active);
        duration.setHintTextColor(getResources().getColor(R.color.Dark));

        mon.setEnabled(true);
        tue.setEnabled(true);
        wed.setEnabled(true);
        thur.setEnabled(true);
        fri.setEnabled(true);
        sat.setEnabled(true);
        sun.setEnabled(true);

        select_all.setEnabled(true);

        add_deal.setEnabled(true);

        if (GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_days.monday == true) {
            flag_mon = true;
            mon.setBackgroundResource(R.drawable.rect_edittext_active);
        } else {
            flag_mon = false;
            mon.setBackgroundResource(R.drawable.rect_edittext_inactive);
        }

        if (GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_days.tuesday == true) {
            flag_tues = true;
            tue.setBackgroundResource(R.drawable.rect_edittext_active);
        } else {
            flag_tues = false;
            tue.setBackgroundResource(R.drawable.rect_edittext_inactive);
        }

        if (GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_days.wednesday == true) {
            flag_wednes = true;
            wed.setBackgroundResource(R.drawable.rect_edittext_active);
        } else {
            flag_wednes = false;
            wed.setBackgroundResource(R.drawable.rect_edittext_inactive);
        }

        if (GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_days.thursday == true) {
            flag_thurs = true;
            thur.setBackgroundResource(R.drawable.rect_edittext_active);
        } else {
            flag_thurs = false;
            thur.setBackgroundResource(R.drawable.rect_edittext_inactive);
        }

        if (GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_days.friday == true) {
            flag_fri = true;
            fri.setBackgroundResource(R.drawable.rect_edittext_active);
        } else {
            flag_fri = false;
            fri.setBackgroundResource(R.drawable.rect_edittext_inactive);
        }

        if (GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_days.saturday == true) {
            flag_satur = true;
            sat.setBackgroundResource(R.drawable.rect_edittext_active);
        } else {
            flag_satur = false;
            sat.setBackgroundResource(R.drawable.rect_edittext_inactive);
        }

        if (GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_days.sunday == true) {
            flag_sun = true;
            sun.setBackgroundResource(R.drawable.rect_edittext_active);
        } else {
            flag_sun = false;
            sun.setBackgroundResource(R.drawable.rect_edittext_inactive);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_select_all:
                if (select_days == false) {
                    select_days = true;
                    select_all.setText("Deselect all days");
                    flag_mon = true;
                    mon.setBackgroundResource(R.drawable.rect_edittext_active);
                    flag_tues = true;
                    tue.setBackgroundResource(R.drawable.rect_edittext_active);
                    flag_wednes = true;
                    wed.setBackgroundResource(R.drawable.rect_edittext_active);
                    flag_thurs = true;
                    thur.setBackgroundResource(R.drawable.rect_edittext_active);
                    flag_fri = true;
                    fri.setBackgroundResource(R.drawable.rect_edittext_active);
                    flag_satur = true;
                    sat.setBackgroundResource(R.drawable.rect_edittext_active);
                    flag_sun = true;
                    sun.setBackgroundResource(R.drawable.rect_edittext_active);
                } else {
                    select_days = false;
                    select_all.setText("Select all days");
                    flag_mon = false;
                    mon.setBackgroundResource(R.drawable.rect_edittext_inactive);
                    flag_tues = false;
                    tue.setBackgroundResource(R.drawable.rect_edittext_inactive);
                    flag_wednes = false;
                    wed.setBackgroundResource(R.drawable.rect_edittext_inactive);
                    flag_thurs = false;
                    thur.setBackgroundResource(R.drawable.rect_edittext_inactive);
                    flag_fri = false;
                    fri.setBackgroundResource(R.drawable.rect_edittext_inactive);
                    flag_satur = false;
                    sat.setBackgroundResource(R.drawable.rect_edittext_inactive);
                    flag_sun = false;
                    sun.setBackgroundResource(R.drawable.rect_edittext_inactive);
                }
                break;
            case R.id.mon_add_deal:
                if (flag_mon == false) {
                    flag_mon = true;
                    mon.setBackgroundResource(R.drawable.rect_edittext_active);
                } else {
                    flag_mon = false;
                    mon.setBackgroundResource(R.drawable.rect_edittext_inactive);
                }
                break;

            case R.id.tue_add_deal:
                if (flag_tues == false) {
                    flag_tues = true;
                    tue.setBackgroundResource(R.drawable.rect_edittext_active);
                } else {
                    flag_tues = false;
                    tue.setBackgroundResource(R.drawable.rect_edittext_inactive);
                }
                break;

            case R.id.wed_add_deal:
                if (flag_wednes == false) {
                    flag_wednes = true;
                    wed.setBackgroundResource(R.drawable.rect_edittext_active);
                } else {
                    flag_wednes = false;
                    wed.setBackgroundResource(R.drawable.rect_edittext_inactive);
                }
                break;
            case R.id.thur_add_deal:
                if (flag_thurs == false) {
                    flag_thurs = true;
                    thur.setBackgroundResource(R.drawable.rect_edittext_active);
                } else {
                    flag_thurs = false;
                    thur.setBackgroundResource(R.drawable.rect_edittext_inactive);
                }
                break;

            case R.id.fri_add_deal:
                if (flag_fri == false) {
                    flag_fri = true;
                    fri.setBackgroundResource(R.drawable.rect_edittext_active);
                } else {
                    flag_fri = false;
                    fri.setBackgroundResource(R.drawable.rect_edittext_inactive);
                }
                break;

            case R.id.sat_add_deal:
                if (flag_satur == false) {
                    flag_satur = true;
                    sat.setBackgroundResource(R.drawable.rect_edittext_active);
                } else {
                    flag_satur = false;
                    sat.setBackgroundResource(R.drawable.rect_edittext_inactive);
                }
                break;
            case R.id.sun_add_deal:
                if (flag_sun == false) {
                    flag_sun = true;
                    sun.setBackgroundResource(R.drawable.rect_edittext_active);
                } else {
                    flag_sun = false;
                    sun.setBackgroundResource(R.drawable.rect_edittext_inactive);
                }
                break;

            case R.id.ed_deal_title_add_deal:

                title.setBackgroundResource(R.drawable.rect_edittext_active);
                title.setHintTextColor(getResources().getColor(R.color.Dark));

                description.setEnabled(true);
                description.setBackgroundResource(R.drawable.rect_edittext_active);
                description.setHintTextColor(getResources().getColor(R.color.Dark));

                duration.setEnabled(true);
                duration.setBackgroundResource(R.drawable.rect_edittext_active);
                duration.setHintTextColor(getResources().getColor(R.color.Dark));

                quantity.setEnabled(true);
                quantity.setBackgroundResource(R.drawable.rect_edittext_active);
                quantity.setHintTextColor(getResources().getColor(R.color.Dark));

                mon.setEnabled(true);
                tue.setEnabled(true);
                wed.setEnabled(true);
                thur.setEnabled(true);
                fri.setEnabled(true);
                sat.setEnabled(true);
                sun.setEnabled(true);

                select_all.setEnabled(true);

                add_deal.setEnabled(true);

                break;

            case R.id.ed_duration_add_deal:

                SelectDealDuration();

                break;

            case R.id.btn_add_deal:

                if (TextUtils.isEmpty(title.getText().toString())) {
                    title.setError(getString(R.string.lp_field_require));
                } else if (TextUtils.isEmpty(description.getText().toString())) {
                    description.setError(getString(R.string.lp_field_require));
                }else if (TextUtils.isEmpty(duration.getText().toString())) {
                    duration.setError(getString(R.string.lp_field_require));
                } else if (TextUtils.isEmpty(quantity.getText().toString())) {
                    quantity.setError(getString(R.string.lp_field_require));
                } else if (Integer.valueOf(quantity.getText().toString()) == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.lp_check_quantity) , Toast.LENGTH_LONG).show();
                } else {
                    if (GD.add_edit_deal == 1) {
                        callAddDealAPI();
                    } else if (GD.add_edit_deal == 2) {
                        callEditDealAPI();
                    }
                }
                break;

            case R.id.iv_back_add_deal:
                intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
                break;
        }
    }

    // Date Picker to select birthday
    private void SelectDealDuration(){
        dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String get_all_date = dateFormatter.format(newDate.getTime());

                duration.setText(get_all_date);
                duration.setBackgroundResource(R.drawable.rect_edittext_active);
                duration.setHintTextColor(getResources().getColor(R.color.Dark));
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void callAddDealAPI() {
        String url = GD.BaseUrl + "add_deal.php";

        RegisterDealModel registerDealModel = new RegisterDealModel();
        registerDealModel.user_id = GD.businessModel.user_id;
        registerDealModel.bar_id = GD.businessModel.bars.get(0).bar_id;
        registerDealModel.title = title.getText().toString();
        registerDealModel.description = description.getText().toString();
        registerDealModel.duration = duration.getText().toString();
        registerDealModel.qty = quantity.getText().toString();
        registerDealModel.deal_days.monday = flag_mon;
        registerDealModel.deal_days.tuesday = flag_tues;
        registerDealModel.deal_days.wednesday = flag_wednes;
        registerDealModel.deal_days.thursday = flag_thurs;
        registerDealModel.deal_days.friday = flag_fri;
        registerDealModel.deal_days.saturday = flag_satur;
        registerDealModel.deal_days.sunday = flag_sun;

        Gson gson = new Gson();
        String json = gson.toJson(registerDealModel);

        new AddDealWorker().execute(url, json);
    }

    public class AddDealWorker extends AsyncTask<String, String, String> {
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

                        GD.businessModel = new BusinessModel();
                        GD.businessModel.user_id = loginJson.getInt("user_id");
                        GD.businessModel.username = loginJson.getString("username");
                        GD.businessModel.email = loginJson.getString("email");
                        GD.businessModel.telephone = loginJson.getString("telephone");
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

                        dialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                        finish();

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

    private void callEditDealAPI() {
        String url = GD.BaseUrl + "edit_deal.php";

        EditDealModel editDealModel = new EditDealModel();
        editDealModel.user_id = GD.businessModel.user_id;
        editDealModel.bar_id = GD.businessModel.bars.get(0).bar_id;
        editDealModel.deal_id = GD.businessModel.bars.get(0).deals.get(GD.edit_deal_pos).deal_id;
        editDealModel.title = title.getText().toString();
        editDealModel.description = description.getText().toString();
        editDealModel.duration = duration.getText().toString();
        editDealModel.qty = quantity.getText().toString();
        editDealModel.deal_days.monday = flag_mon;
        editDealModel.deal_days.tuesday = flag_tues;
        editDealModel.deal_days.wednesday = flag_wednes;
        editDealModel.deal_days.thursday = flag_thurs;
        editDealModel.deal_days.friday = flag_fri;
        editDealModel.deal_days.saturday = flag_satur;
        editDealModel.deal_days.sunday = flag_sun;

        Gson gson = new Gson();
        String json = gson.toJson(editDealModel);

        new EditDealWorker().execute(url, json);
    }

    public class EditDealWorker extends AsyncTask<String, String, String> {
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

                        GD.businessModel = new BusinessModel();
                        GD.businessModel.user_id = loginJson.getInt("user_id");
                        GD.businessModel.username = loginJson.getString("username");
                        GD.businessModel.email = loginJson.getString("email");
                        GD.businessModel.telephone = loginJson.getString("telephone");
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
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                        finish();

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
        finish();
    }
}
