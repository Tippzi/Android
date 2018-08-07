package com.application.tippzi.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Adapters.CategorySpinner;
import com.application.tippzi.Adapters.CategorySpinnerAdapter;
import com.application.tippzi.Adapters.HintAdapter;
import com.application.tippzi.Adapters.HintSpinner;
import com.application.tippzi.Adapters.LookupAddressAdapter;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.AddressModel;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.BusinessModel;
import com.application.tippzi.Models.BusinessSignUpModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.backendless.geo.GeoPoint;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.application.tippzi.Global.CF.EMOJI_FILTER;

public class BusinessAboutYouScreen extends AbstractActivity implements View.OnClickListener {

    // Views
    private EditText business_username, business_email, businss_phonenumber, business_password, business_name, post_code, address;
    private AutoCompleteTextView business_service;
    private TextView create_business_account, lookup;
    private TextView btn_confirm_address;
    private Spinner category;
    private ListView listView;

    // Define for search address
    private ArrayList<AddressModel> addressModelArrayList ;
    private LookupAddressAdapter resultViewAdapter ;
    private Dialog popupdailog ;

    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";
    private GeoPoint getlocation ;

    private CategorySpinner<String> customerSpinner;
    private String category_value = "" ;

    private ACProgressFlower dialog;

    private Boolean password_check = false;

    ArrayList<String> mAutoTexts;
    private ArrayAdapter<String> mAutoAdapter;

    // Saving account
    private BusinessSignUpModel businessSignUpModel = new BusinessSignUpModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business_account_about_you);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Loading...")
                .build();

        ImageView back = findViewById(R.id.iv_back_business_about_you);
        back.setOnClickListener(this);

        ImageView back_up = findViewById(R.id.btn_back_up_business_about_you);
        back_up.setOnClickListener(this);

        business_username = findViewById(R.id.ed_business_username);
        business_email = findViewById(R.id.ed_business_email);
        businss_phonenumber = findViewById(R.id.ed_business_telephone);
        business_password = findViewById(R.id.ed_business_password);
        business_name = findViewById(R.id.ed_business_name);
        category = findViewById(R.id.sp_category);
        post_code = findViewById(R.id.ed_postal_code);
        address = findViewById(R.id.ed_business_address);
        business_service = findViewById(R.id.ed_business_service);
//        business_service.setAdapter();

        InitCategorySpinner(false);

        business_username.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_email.setFilters(new InputFilter[]{EMOJI_FILTER});
        businss_phonenumber.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_password.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_name.setFilters(new InputFilter[]{EMOJI_FILTER});
        post_code.setFilters(new InputFilter[]{EMOJI_FILTER});
        address.setFilters(new InputFilter[]{EMOJI_FILTER});

        business_username.setOnClickListener(this);

        btn_confirm_address = findViewById(R.id.btn_enter_address);
        btn_confirm_address.setOnClickListener(this);

        create_business_account = findViewById(R.id.btn_create_business_account);
        create_business_account.setOnClickListener(this);

        lookup = findViewById(R.id.btn_look_up);
        lookup.setOnClickListener(this);

        final ImageView eye_password = findViewById(R.id.iv_eye_password_about_you);
        eye_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_check == false) {
                    password_check = true;
                    business_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    eye_password.setBackgroundResource(R.mipmap.ico_visibility_eye);
                } else {
                    password_check = false;
                    business_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eye_password.setBackgroundResource(R.mipmap.ico_unvisibility_eye);
                }
            }
        });

        String url = GD.BaseUrl + "get_service_name.php";
        new GetServiceNames().execute(url);
    }

    // Spinner to select Category
    private void InitCategorySpinner(boolean select_flag){
        List<String> genderString;
        genderString = new ArrayList<>();
        genderString.add("Nightlife");
        genderString.add("Health & Fitness");
        genderString.add("Hair & Beauty");

        customerSpinner = new CategorySpinner<>(
                category,
                new CategorySpinnerAdapter<String>(getApplicationContext(), "Category", genderString, select_flag),
                new CategorySpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        category_value = category.getSelectedItem().toString();
                    }
                });
        customerSpinner.init();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_back_business_about_you:
                intent = new Intent(getApplicationContext(), SignUpScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
                break;

            case R.id.btn_back_up_business_about_you:
                intent = new Intent(getApplicationContext(), SignUpScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
                break;

            case R.id.btn_enter_address:

                address.setVisibility(View.VISIBLE);
                address.setEnabled(true);

                address.setBackgroundResource(R.drawable.rect_edittext_active);
                address.setHintTextColor(getResources().getColor(R.color.Dark));

                break;

            case R.id.btn_create_business_account:

                if (TextUtils.isEmpty(business_username.getText().toString())) {
                    business_username.setError(getString(R.string.lp_field_require));
                } else if (TextUtils.isEmpty(business_email.getText().toString())) {
                    business_email.setError(getString(R.string.lp_field_require));
                } else if (!isEmailValid(business_email.getText().toString())) {
                    business_email.setError(getString(R.string.lp_email_vaild));
                } else if (TextUtils.isEmpty(businss_phonenumber.getText().toString())) {
                    businss_phonenumber.setError(getString(R.string.lp_field_require));
                } else if (TextUtils.isEmpty(business_name.getText().toString())) {
                    business_name.setError(getString(R.string.lp_field_require));
                } else if (category_value.equals("")) {
                    Toast.makeText(getApplicationContext(), "Select your category", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(post_code.getText().toString())) {
                    post_code.setError(getString(R.string.lp_field_require));
                } else if (TextUtils.isEmpty(address.getText().toString())) {
//                    Toast.makeText(getApplicationContext(), "Please get exactly address from your post code.", Toast.LENGTH_LONG).show();
                } else {

                    businessSignUpModel.username = business_username.getText().toString();
                    businessSignUpModel.email = business_email.getText().toString();
                    businessSignUpModel.telephone = businss_phonenumber.getText().toString();
                    businessSignUpModel.password = business_password.getText().toString();
                    businessSignUpModel.business_name = business_name.getText().toString();
                    businessSignUpModel.category = category_value;
                    businessSignUpModel.post_code = post_code.getText().toString();
                    businessSignUpModel.address = address.getText().toString();
                    businessSignUpModel.service_name = business_service.getText().toString();
                    CallBusinessSignUpAPI();
//                    try {
//                        getlocation = getLocationFromAddress(address.getText().toString());
//                        businessSignUpModel.lat = String.valueOf(getlocation.getLatitude());
//                        businessSignUpModel.lon = String.valueOf(getlocation.getLongitude());
//                        CallBusinessSignUpAPI();
//                    }catch (Exception e) {
//                        Toast.makeText(getApplicationContext(), "Address is not correct", Toast.LENGTH_LONG).show();
//                        GD.temp_bar.lat = "0";
//                        GD.temp_bar.lon = "0";
//                    }
                }
                break;

            case R.id.ed_business_username:

                business_username.setBackgroundResource(R.drawable.rect_edittext_active);
                business_username.setHintTextColor(getResources().getColor(R.color.Dark));
                business_username.setEnabled(true);

                business_email.setBackgroundResource(R.drawable.rect_edittext_active);
                business_email.setHintTextColor(getResources().getColor(R.color.Dark));
                business_email.setEnabled(true);

                businss_phonenumber.setBackgroundResource(R.drawable.rect_edittext_active);
                businss_phonenumber.setHintTextColor(getResources().getColor(R.color.Dark));
                businss_phonenumber.setEnabled(true);

                business_password.setBackgroundResource(R.drawable.rect_edittext_active);
                business_password.setHintTextColor(getResources().getColor(R.color.Dark));
                business_password.setEnabled(true);

                business_name.setBackgroundResource(R.drawable.rect_edittext_active);
                business_name.setHintTextColor(getResources().getColor(R.color.Dark));
                business_name.setEnabled(true);

                business_service.setBackgroundResource(R.drawable.rect_edittext_active);
                business_service.setHintTextColor(getResources().getColor(R.color.Dark));
                business_service.setEnabled(true);

                category.setBackgroundResource(R.drawable.rect_edittext_active);
                category.setEnabled(true);
                InitCategorySpinner(true);

                post_code.setBackgroundResource(R.drawable.rect_edittext_active);
                post_code.setHintTextColor(getResources().getColor(R.color.Dark));
                post_code.setEnabled(true);

                lookup.setEnabled(true);

                btn_confirm_address.setEnabled(true);

                create_business_account.setBackgroundResource(R.drawable.rect_button_active);
                create_business_account.setTextColor(getResources().getColor(R.color.White));
                create_business_account.setEnabled(true);

                break;

            case R.id.btn_look_up:

                if (post_code.getText().toString().equals("")) {

                } else {
                    SearchAddress();
                }

                break;
        }
    }

    private void CallBusinessSignUpAPI() {

        String url = GD.BaseUrl + "business_sign_up.php";

        Gson gson = new Gson();
        String json = gson.toJson(businessSignUpModel);

        new BusinessSignUpWorker().execute(url, json);
    }

    public class BusinessSignUpWorker extends AsyncTask<String, String, String> {
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
                        GD.businessModel = new BusinessModel();
                        GD.businessModel.user_id = signupJson.getInt("user_id");
                        GD.businessModel.username = signupJson.getString("username");
                        GD.businessModel.email = signupJson.getString("email");
                        GD.businessModel.telephone = signupJson.getString("telephone");
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
                            barModel.open_time.tue_end = jsonObject1.getString("tue_end");
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

                            GD.businessModel.bars.add(barModel);
                        }
                        dialog.dismiss();

                        SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(USERID, signupJson.getString("user_id"));
                        editor.putString(USERTYPE, "Business");
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
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
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private boolean isEmailValid(String email) {
        final String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }
    // Alert of less than 8 charactors for password
    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    // Search Address

    private void SearchAddress(){
        String search_string = post_code.getText().toString() ;
        search_string = search_string.replace(" ", "");
        String url = "https://api.getAddress.io/find/" + search_string + "?api-key=_GJ_sMycoUOy1_g7W2lavw10914";
        String request = "" ;
        new GetAddressWorker().execute(url, request);
    }

    public class GetServiceNames extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
//            String data = param[1];
            String response = CF.HttpGetRequest(url);
            publishProgress(response);
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            mAutoTexts = new ArrayList<>();
            try {
                JSONArray arr = new JSONArray(response);
                for(int i = 0; i < arr.length(); i++){
                    String str = arr.get(i).toString();
                    mAutoTexts.add(str);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);

            mAutoAdapter = new ArrayAdapter<String>(BusinessAboutYouScreen.this, R.layout.item_service_auto, mAutoTexts);
            business_service.setAdapter(mAutoAdapter);
        }
    }

    public class GetAddressWorker extends AsyncTask<String, String, String> {
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
            String response = CF.HttpGetRequest(url);
            publishProgress(response);
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            addressModelArrayList = new ArrayList<>();
            if (!response.isEmpty()) {
                try {
                    JSONObject responseJson = new JSONObject(response);

                    JSONArray jsonArray = responseJson.getJSONArray("addresses");
                    for (int i = 0 ; i < jsonArray.length() ; i++){
                        AddressModel addressModel = new AddressModel();
                        String temp = "";
                        temp = jsonArray.get(i).toString().replace(", ", " ");
                        temp = temp.replace("     ", ", ");
                        addressModel.address = temp;
                        addressModelArrayList.add(addressModel);
                    }

                    LoadAddress(addressModelArrayList);

                } catch (Exception e) {

                }

                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (!responseJson.getString("Message").equals("")) {
                        Toast.makeText(getApplicationContext(), "You have entered an incorrect postcode", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {

                }

                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "You have entered an incorrect postcode", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private void LoadAddress(ArrayList<AddressModel> results){
        popupdailog = new Dialog(this);
        popupdailog.requestWindowFeature(1);
        popupdailog.setCancelable(true);
        popupdailog.setContentView(R.layout.popup_lookup_address);
        popupdailog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        listView = popupdailog.findViewById(R.id.list_address);
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.width = -1;
        wmlp.gravity = 17;
        popupdailog.show();

        resultViewAdapter = new LookupAddressAdapter(this);
        listView.setAdapter(resultViewAdapter);
        resultViewAdapter.setOnClickButtonListener(setaddressitem);

        resultViewAdapter.clearFeedList();
        for (int i = 0 ; i < results.size(); i++) {
            AddressModel addressModel = new AddressModel();
            addressModel.address = results.get(i).address ;
            resultViewAdapter.addFeedList(addressModel);
        }
        resultViewAdapter.notifyDataSetChanged();

    }

    private LookupAddressAdapter.OnClickButtonSetListListener setaddressitem = new LookupAddressAdapter.OnClickButtonSetListListener() {
        @Override
        public void onClick(int pos, String select_address) {

            address.setVisibility(View.VISIBLE);
            address.setEnabled(false);
            address.setBackgroundResource(R.drawable.rect_edittext_active);
            address.setHintTextColor(getResources().getColor(R.color.Dark));
            address.setText(select_address);
            popupdailog.dismiss();
        }
    };

    // Convert long, lat from address

    public GeoPoint getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,3);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new GeoPoint(location.getLatitude(),
                    location.getLongitude());
        } catch (IOException e){
            e.printStackTrace();
        }
        return p1;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SignUpScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
        finish();
    }
}
