package com.application.tippzi.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Adapters.CategoryListAdapter;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.CategoryLIstModel;
import com.application.tippzi.R;
import com.application.tippzi.Service.AlaramReceiver;
import com.application.tippzi.Service.TrackerService;

import java.util.ArrayList;
import java.util.Calendar;

public class CategoryScreen extends AbstractActivity {

    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";
    private SharedPreferences pref;

    private ListView category_list;
    private CategoryListAdapter categoryListAdapter;

    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        category_list = (ListView) findViewById(R.id.lv_category);
        categoryListAdapter = new CategoryListAdapter(this);
        category_list.setAdapter(categoryListAdapter);
        categoryListAdapter.setOnClickButtonListener(categoryselectitem);
        category_list.setDivider(this.getResources().getDrawable(R.drawable.transperent_color));
        category_list.setDividerHeight(30);

        LoadCategoryList();

        TextView sign_out = (TextView) findViewById(R.id.tv_sign_out);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomerDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });
    }

    private void LoadCategoryList(){
        categoryListAdapter.clearFeedList();
        categoryListAdapter.notifyDataSetChanged();
        for (int i = 0 ; i < 3; i++) {
            CategoryLIstModel categoryLIstModel = new CategoryLIstModel();
            if (i == 0) {
                categoryLIstModel.category = "Nightlife";
            } else if (i == 1) {
                categoryLIstModel.category = "Health & Fitness";
            } else if (i == 2) {
                categoryLIstModel.category = "Hair & Beauty";
            }
            categoryListAdapter.addFeedList(categoryLIstModel);
        }

        categoryListAdapter.notifyDataSetChanged();
    }

    private CategoryListAdapter.OnEditCategoryListListener categoryselectitem = new CategoryListAdapter.OnEditCategoryListListener() {
        @Override
        public void onClick(int pos) {
            GD.select_user_type = "customer" ;
            if (pos == 0) {
                GD.category_option = "Nightlife";
                GD.barModels = adding_bars_by_category("Nightlife");
            } else if (pos == 1) {
                GD.category_option = "Health & Fitness";
                GD.barModels = adding_bars_by_category("Health & Fitness");
            } else if (pos == 2) {
                GD.category_option = "Hair & Beauty";
                GD.barModels = adding_bars_by_category("Hair & Beauty");
            }

            RealtimeRefreshBusinessInfo();

            if (GD.barModels.size() == 0) {
                Toast.makeText(getApplicationContext(), "There are not bars in this category.", Toast.LENGTH_LONG).show();
            } else {
                Intent backintent = new Intent(getApplicationContext(), CustomerMapViewActivity.class);
                backintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backintent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
            }
        }
    } ;

    private void RealtimeRefreshBusinessInfo(){
        pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        if (pref.getString(USERTYPE, "").equals("Customer")) {
            mServiceIntent = new Intent(getApplicationContext(), TrackerService.class);
            startService(mServiceIntent);
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
