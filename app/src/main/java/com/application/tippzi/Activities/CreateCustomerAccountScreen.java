package com.application.tippzi.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Adapters.HintAdapter;
import com.application.tippzi.Adapters.HintSpinner;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.CustomerSignUpModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.application.tippzi.Service.GPSTracker;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.application.tippzi.Global.CF.EMOJI_FILTER;

public class CreateCustomerAccountScreen extends AbstractActivity {

    private EditText user_name, email, username, password, confirm_password;
    private Spinner gender;
    private HintSpinner<String> customerSpinner;

    private String gener_value = "" ;

    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";

    private Boolean password_check = false;
    private Boolean password_confirm_check = false;

    private ACProgressFlower dialog;
    private GPSTracker mGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_customer);

        mGPS = new GPSTracker(this) ;
        if (mGPS.canGetLocation()) {
            mGPS.getLocation() ;
        }
        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Registering...")
                .build();

        RelativeLayout background = findViewById(R.id.activity_customer_sign_up_main);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ImageView back = findViewById(R.id.iv_back_create_account_customer);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        ImageView back_up = findViewById(R.id.btn_back_up_customer_sign_up);
        back_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        user_name = findViewById(R.id.ed_customer_name_create);
        email = findViewById(R.id.ed_customer_email_create);
        gender = findViewById(R.id.sp_gender);
        username = findViewById(R.id.ed_customer_username_create);
        password = findViewById(R.id.ed_customer_password_create);
        confirm_password = findViewById(R.id.ed_customer_cinfirm_password_create);

        user_name.setFilters(new InputFilter[]{EMOJI_FILTER});
        email.setFilters(new InputFilter[]{EMOJI_FILTER});
        username.setFilters(new InputFilter[]{EMOJI_FILTER});
        password.setFilters(new InputFilter[]{EMOJI_FILTER});
        confirm_password.setFilters(new InputFilter[]{EMOJI_FILTER});

        findViewById(R.id.tv_term_condition_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terms_url = "http://tippzi.com/b-c-terms.php";
                Intent webIntent = new Intent(getApplicationContext(), TermsActivity.class);
                webIntent.putExtra(TermsActivity.EXTRA_URL, terms_url);
                startActivityForResult(webIntent, 101);
            }
        });

        InitGenderSpinner(false);

        final TextView create_account = findViewById(R.id.btn_create_business_account);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTermAccepted)
                    return;
                if (TextUtils.isEmpty(user_name.getText().toString())) {
                    user_name.setError(getString(R.string.lp_field_require));
                } else if (TextUtils.isEmpty(email.getText().toString())) {
                    email.setError(getString(R.string.lp_field_require));
                } else if (!isEmailValid(email.getText().toString())) {
                    email.setError(getString(R.string.lp_email_vaild));
                } else if (gener_value.equals("")) {
                    Toast.makeText(getApplicationContext(), "You must select your gender.", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(username.getText().toString())) {
                    username.setError(getString(R.string.lp_field_require));
                } else if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError(getString(R.string.lp_field_require));
                } else if (!isPasswordValid(password.getText().toString())) {
                    password.setError(getString(R.string.lp_check_password));
                } else if (TextUtils.isEmpty(confirm_password.getText().toString())) {
                    confirm_password.setError(getString(R.string.lp_field_require));
                } else if (!password.getText().toString().equals(confirm_password.getText().toString())) {
                    confirm_password.setError(getString(R.string.lp_wrong_password));
                } else {
                    callCustomerSignUpAPI();
                }
            }
        });

        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setEnabled(true);
                gender.setEnabled(true);
                username.setEnabled(true);
                password.setEnabled(true);
                confirm_password.setEnabled(true);

                user_name.setBackgroundResource(R.drawable.rect_edittext_active);
                email.setBackgroundResource(R.drawable.rect_edittext_active);
                gender.setBackgroundResource(R.drawable.rect_edittext_active);
                username.setBackgroundResource(R.drawable.rect_edittext_active);
                password.setBackgroundResource(R.drawable.rect_edittext_active);
                confirm_password.setBackgroundResource(R.drawable.rect_edittext_active);

                create_account.setEnabled(true);
                create_account.setBackgroundResource(R.drawable.rect_sign_up_button);
                create_account.setTextColor(getResources().getColor(R.color.White));
                InitGenderSpinner(true);
            }
        });

        final ImageView eye_password = findViewById(R.id.iv_eye_password_customer_sign_up);

        eye_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_check == false) {
                    password_check = true;
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    eye_password.setBackgroundResource(R.mipmap.ico_visibility_eye);
                } else {
                    password_check = false;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eye_password.setBackgroundResource(R.mipmap.ico_unvisibility_eye);
                }
            }
        });

        final ImageView eye_password_confirm = findViewById(R.id.iv_eye_password_confirm_customer_sign_up);
        eye_password_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_confirm_check == false) {
                    password_confirm_check = true;
                    confirm_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    eye_password_confirm.setBackgroundResource(R.mipmap.ico_visibility_eye);
                } else {
                    password_confirm_check = false;
                    confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eye_password_confirm.setBackgroundResource(R.mipmap.ico_unvisibility_eye);
                }
            }
        });
    }

    // Spinner to select Gender
    private void InitGenderSpinner(boolean select_flag){
        List<String> genderString;
        genderString = new ArrayList<>();
        genderString.add("Male");
        genderString.add("Female");

        customerSpinner = new HintSpinner<>(
                gender,
                new HintAdapter<>(getApplicationContext(), "Gender", genderString, select_flag),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        gener_value = gender.getSelectedItem().toString();
                    }
                });
        customerSpinner.init();
    }

    // Alert of less than 8 charactors for password
    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    private boolean isEmailValid(String email) {
        final String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }

    private void callCustomerSignUpAPI() {
        String url = GD.BaseUrl + "customer_sign_up.php";
        CustomerSignUpModel tempModel = new CustomerSignUpModel();
        tempModel.user_name = user_name.getText().toString();
        tempModel.email = email.getText().toString();
        tempModel.gender = gender.getSelectedItem().toString();
        tempModel.username = username.getText().toString();
        tempModel.password = password.getText().toString();
        tempModel.social_account = 0;
        tempModel.lat = String.valueOf(mGPS.getLatitude());
        tempModel.lon = String.valueOf(mGPS.getLongitude());

        Gson gson = new Gson();
        String json = gson.toJson(tempModel);

        new CustomerSignUpWorker().execute(url, json);
    }

    public class CustomerSignUpWorker extends AsyncTask<String, String, String> {
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
                    JSONObject signupJson = new JSONObject(response);
                    String success = signupJson.getString("success");
                    if (success.equals("true")) {

                        GD.customerModel = new CustomerModel();
                        GD.customerModel.user_id = signupJson.getInt("user_id");
                        GD.customerModel.user_name = signupJson.getString("user_name");
                        GD.customerModel.email = signupJson.getString("email");
                        GD.customerModel.gender = signupJson.getString("gender");
                        GD.customerModel.birthday = signupJson.getString("birthday");
                        GD.customerModel.username = signupJson.getString("username");

                        JSONArray jsonArray = signupJson.getJSONArray("bars");
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
                        dialog.dismiss();

                        SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(USERID, signupJson.getString("user_id"));
                        editor.putString(USERTYPE, "Customer");
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), Tutorial1Screen.class);
                        startActivity(intent);
                        finish();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), signupJson.getString("message"), Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(getApplicationContext(), SignUpScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if(requestCode == 101){
            if(responseCode == RESULT_OK){
                mTermAccepted = true;
            } else {
                mTermAccepted = false;
            }
        }
    }
    boolean mTermAccepted = true;
}
