package com.application.tippzi.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CustomerDashboardScreen extends AbstractActivity {
    private ACProgressFlower dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        ImageView find_venue = (ImageView) findViewById(R.id.iv_find_venue);
        find_venue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GD.category_option = "";
                ArrayList<BarModel> barModels = new ArrayList<BarModel>();
                for (int i = 0; i < GD.customerModel.bars.size(); i ++) {
                    barModels.add(GD.customerModel.bars.get(i));
                }
                GD.barModels = barModels;
                GD.select_user_type = "customer" ;
                if (GD.customerModel.bars.size() == 0) {
                    Toast.makeText(getApplicationContext(), "There are not bars.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), CustomerMapViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                    finish();
                }
            }
        });

        ImageView find_deal = (ImageView) findViewById(R.id.iv_find_deal);
        find_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CategoryScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
            }
        });

        ImageView tippzi_go = (ImageView) findViewById(R.id.iv_tippzi_go);
        tippzi_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Loading...")
                .build();
        String url = GD.BaseUrl + "get_service_name.php";
        new GetServiceNames().execute(url);
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
            GD.gServiceTexts = new ArrayList<>();
            try {
                JSONArray arr = new JSONArray(response);
                for(int i = 0; i < arr.length(); i++){
                    String str = arr.get(i).toString();
                    GD.gServiceTexts.add(str);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
