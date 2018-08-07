package com.application.tippzi.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Adapters.CategorySpinner;
import com.application.tippzi.Adapters.CategorySpinnerAdapter;
import com.application.tippzi.Adapters.LookupAddressAdapter;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.AddressModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.backendless.geo.GeoPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.application.tippzi.Global.CF.EMOJI_FILTER;

public class EditAccountProfileScreen extends AbstractActivity {

    private EditText business_name, business_post_code, business_address, business_email, business_phonenumber, business_website;

    private Spinner business_category;

    private CategorySpinner<String> customerSpinner;
    private String category_value = "" ;

    private ListView listView ;
    private LookupAddressAdapter resultViewAdapter ;
    private ArrayList<AddressModel> addressModelArrayList ;
    private Dialog popupdailog ;

    private ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_profile_account);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Loading...")
                .build();

        TextView bar_name = findViewById(R.id.tv_business_name_edit);
        bar_name.setText(GD.temp_bar.business_name);

        business_name = findViewById(R.id.ed_business_name_edit);
        business_category = findViewById(R.id.sp_category_edit);
        business_post_code = findViewById(R.id.ed_postal_code_edit);
        business_address = findViewById(R.id.ed_business_address_edit);
        business_email = findViewById(R.id.ed_business_email_edit);
        business_phonenumber = findViewById(R.id.ed_business_phonenumber_edit);
        business_website = findViewById(R.id.ed_business_website_edit);

        InitEditCategorySpinner(true);

        if (GD.temp_bar.category.equals("Nightlife")) {
            business_category.setSelection(0);
        } else if (GD.temp_bar.category.equals("Health & Fitness")) {
            business_category.setSelection(1);
        } else if (GD.temp_bar.category.equals("Hair & Beauty")) {
            business_category.setSelection(2);
        }

        business_name.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_post_code.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_address.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_email.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_phonenumber.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_website.setFilters(new InputFilter[]{EMOJI_FILTER});

        business_name.setText(GD.temp_bar.business_name);
        business_post_code.setText(GD.temp_bar.post_code);
        business_address.setText(GD.temp_bar.address);
        business_email.setText(GD.temp_bar.email);
        business_phonenumber.setText(GD.temp_bar.telephone);
        business_website.setText(GD.temp_bar.website);

        TextView btn_continue = findViewById(R.id.btn_continue_edit_account);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (!isEmailValid(business_email.getText().toString())) {
//                    business_email.setError(getString(R.string.lp_email_vaild));
//                } else {
//
//                }

                GD.temp_bar.bar_id = GD.businessModel.bars.get(0).bar_id;
                GD.temp_bar.business_name = business_name.getText().toString();
                GD.temp_bar.category = category_value;
                GD.temp_bar.post_code = business_post_code.getText().toString();
                GD.temp_bar.address = business_address.getText().toString();
                GD.temp_bar.telephone = business_phonenumber.getText().toString();
                GD.temp_bar.website = business_website.getText().toString();
                GD.temp_bar.email = business_email.getText().toString();

                try {
                    GeoPoint getlocation = getLocationFromAddress(business_address.getText().toString());

                    GD.temp_bar.lat = String.valueOf(getlocation.getLatitude());
                    GD.temp_bar.lon = String.valueOf(getlocation.getLongitude());

                    Intent intent = new Intent(getApplicationContext(), EditDescriptionScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                    finish();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Address is not correct", Toast.LENGTH_LONG).show();
                    GD.temp_bar.lat = "0";
                    GD.temp_bar.lon = "0";
                }
//                Intent intent = new Intent(getApplicationContext(), EditDescriptionScreen.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
//                finish();
            }
        });

        TextView lookup = findViewById(R.id.btn_look_up_edit);
        lookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (business_post_code.getText().toString().equals("")) {

                } else {
                    SearchAddress();
                }
            }
        });

        ImageView back = findViewById(R.id.iv_back_edit_account);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        ImageView back_up = findViewById(R.id.iv_back_up_edit_account);
        back_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });
    }

    private void InitEditCategorySpinner(boolean select_flag){
        List<String> genderString;
        genderString = new ArrayList<>();
        genderString.add("Nightlife");
        genderString.add("Health & Fitness");
        genderString.add("Hair &  Beauty");

        customerSpinner = new CategorySpinner<>(
                business_category,
                new CategorySpinnerAdapter<String>(getApplicationContext(), "Category", genderString, select_flag),
                new CategorySpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        category_value = business_category.getSelectedItem().toString();
                    }
                });
        customerSpinner.init();
    }

    private boolean isEmailValid(String email) {
        final String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }

    private void SearchAddress(){
        String search_string = business_post_code.getText().toString() ;
        search_string = search_string.replace(" ", "");
        String url = "https://api.getAddress.io/find/" + search_string + "?api-key=_GJ_sMycoUOy1_g7W2lavw10914";
        String request = "" ;
        new GetAddressWorker().execute(url, request);
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
                    dialog.dismiss();
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
            } else {
                Toast.makeText(getApplicationContext(), "You have entered an incorrect postcode", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void LoadAddress(ArrayList<AddressModel> results){
        popupdailog = new Dialog(this);
        popupdailog.requestWindowFeature(1);
        popupdailog.setCancelable(true);
        popupdailog.setContentView(R.layout.popup_lookup_address);
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
            business_address.setText(select_address);
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
            if (address.size() > 0) {
                Address location=address.get(0);
                location.getLatitude();
                location.getLongitude();
                p1 = new GeoPoint(location.getLatitude(),
                        location.getLongitude());
            } else {

            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return p1;
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
