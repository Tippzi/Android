package com.application.tippzi.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Adapters.CustomerMapViewAdapter;
import com.application.tippzi.Fragments.CustomerMapViewFragment;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.ImpressionModel;
import com.application.tippzi.Models.LocationModel;
import com.application.tippzi.Models.WalletModel;
import com.application.tippzi.R;
import com.application.tippzi.Service.GPSTracker;
import com.application.tippzi.Service.MapViewPager;
import com.application.tippzi.swipeview.SwipeAdapterView;
import com.application.tippzi.until.Utils;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerMapViewActivity extends AbstractActivity implements MapViewPager.Callback {

    private ArrayList<LocationModel> locationModelArrayList ;
    private ImageView category, search, cu_wallet, switch_search ;
    private CustomAutoCompleteTextView search_google_map ;
    private TextView category_title;
    private ViewPager viewPager;
    private MapViewPager mvp = null;
    private MapView map;
    private Bundle savebundle ;
    private int select_pos ;
    private boolean search_statue = false;
    private GPSTracker mGPS ;
    private int switch_flag = 1;
    private FloatingActionButton FAB ;

    private double mLatitude = 0f;
    private double mLongitude = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicGV0ZXJwYW5tYXBib3giLCJhIjoiY2plcjJkN2QyMGFwZjJ3cGFuYnR6cDFoZSJ9.4U_mCCQ9LpUXKcQQb2NNuw");
        setContentView(R.layout.activity_customer_mapview);

        if(getIntent().getExtras().containsKey("latitude")){
            mLatitude = getIntent().getDoubleExtra("latitude", 0);
        }
        if(getIntent().getExtras().containsKey("longitude")){
            mLongitude = getIntent().getDoubleExtra("longitude", 0);
        }

        GD.barIdEngagement = new ArrayList<Integer>();

        mGPS = new GPSTracker(this);
        if (mGPS.canGetLocation()){
            mGPS.getLocation();
        }
        GD.mylocation = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());

        //mLocationHandler.postDelayed(mGetLocation, 10000);


        map = findViewById(R.id.map1);
        map.onCreate(savedInstanceState);
        savebundle = savedInstanceState ;

        GD.check_wallet = false;

        TextView count_wallet = findViewById(R.id.tv_count_wallet);



        int wallet_count = 0;
        for (int i = 0; i < GD.customerModel.walletModels.size(); i ++) {

            if (GD.customerModel.walletModels.get(i).claim_check == true) {

            } else {

                WalletModel walletListModel = new WalletModel();
                for (int j = 0; j < GD.customerModel.bars.size(); j++) {
                    if (GD.customerModel.bars.get(j).bar_id == GD.customerModel.walletModels.get(i).bar_id) {
                        walletListModel.category = GD.customerModel.bars.get(j).category;
                        break;
                    }
                }

                if (!walletListModel.category.equals(GD.category_option)) {
                    continue;
                }
                wallet_count++;
            }
        }

        if (wallet_count == 0) {
            count_wallet.setVisibility(View.GONE);
        } else {
            count_wallet.setText(String.valueOf(wallet_count));
        }

        viewPager = findViewById(R.id.viewPager1);
        category = findViewById(R.id.iv_go_category);
        category_title = findViewById(R.id.tv_category_title_map);
        cu_wallet = findViewById(R.id.iv_cu_wallet);
        search = findViewById(R.id.iv_search_google_map);
        switch_search = findViewById(R.id.iv_switch_search);
        search_google_map = findViewById(R.id.ed_search_google_map);
        FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);

        if (GD.category_option.equals("")) {
            GD.barModels = GD.customerModel.bars;
            category_title.setVisibility(View.GONE);
        } else if (GD.category_option.equals("Nightlife")) {
            GD.barModels = adding_bars_by_category("Nightlife");
            category_title.setVisibility(View.VISIBLE);
            category_title.setText("Nightlife");
        } else if (GD.category_option.equals("Health & Fitness")) {
            GD.barModels = adding_bars_by_category("Health & Fitness");
            category_title.setVisibility(View.VISIBLE);
            category_title.setText("Health & Fitness");
        } else if (GD.category_option.equals("Hair & Beauty")) {
            GD.barModels = adding_bars_by_category("Hair & Beauty");
            category_title.setVisibility(View.VISIBLE);
            category_title.setText("Hair & Beauty");
        }

        search_google_map.setHint("Search an area");
        switch_search.setBackgroundResource(R.mipmap.ico_location_search);

        viewPager.setPageMargin(Utils.dp(this, 10));
        Utils.setMargins(viewPager, 0, 0, 0,0);

        MapViewPagerDataLoad(GD.barModels,false) ;

        if ( GD.select_pos == 0 ) {
            select_pos = 0 ;
        } else {
            select_pos = GD.select_pos ;
        }

        switch_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!GD.category_option.equals("")) {
                    if (switch_flag == 0) {
                        switch_flag = 1;
                        switch_search.setBackgroundResource(R.mipmap.ico_location_search);
                        search_google_map.setHint("Search an area");
                    } else if (switch_flag == 1) {
                        switch_flag = 0;

                        if (GD.category_option.equals("Nightlife")) {
                            search_google_map.setHint("Search a bar");
                            switch_search.setBackgroundResource(R.mipmap.ico_nightlife_search);
                        } else if (GD.category_option.equals("Health & Fitness")) {
                            search_google_map.setHint("Search a venue");
                            switch_search.setBackgroundResource(R.mipmap.ico_health_search);
                        } else if (GD.category_option.equals("Hair & Beauty")) {
                            search_google_map.setHint("Search a beauty salon");
                            switch_search.setBackgroundResource(R.mipmap.ico_beauty_search);
                        }
                    }
                }
            }
        });

        cu_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WalletScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
                callImpressionAPI();
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GD.select_pos = 0;
                Intent backintent;
                if(GD.category_option.equals("")){
                    backintent = new Intent(getApplicationContext(), CustomerDashboardScreen.class);
                } else {
                    backintent = new Intent(getApplicationContext(), CategoryScreen.class);
                }
                backintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backintent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
                callImpressionAPI();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<BarModel> barList = new ArrayList<>();
                if (GD.category_option.equals("Nightlife")) {
                    barList = adding_bars_by_category("Nightlife");
                } else if (GD.category_option.equals("Health & Fitness")) {
                    barList = adding_bars_by_category("Health & Fitness");
                } else if (GD.category_option.equals("Hair & Beauty")) {
                    barList = adding_bars_by_category("Hair & Beauty");
                } else {
                    for (int i = 0; i < GD.customerModel.bars.size(); i ++) {
                        barList.add(GD.customerModel.bars.get(i));
                    }
                }
                if (search_google_map.getText().equals("")) {
                    MapViewPagerDataLoad(GD.barModels, false);
                } else {
                    if (search_statue == false) {
                        search_statue = true;
                        search.setImageDrawable(null);
                        search.setBackgroundResource(R.mipmap.ico_close_search);
                        String search_str = search_google_map.getText().toString();

                        GD.barModels = new ArrayList<BarModel>();

                        for (int i = 0 ; i < barList.size(); i++) {
                            if(GD.category_option.equals(""))
                            {
                                if ((barList.get(i).business_name.toLowerCase()).toString().contains(search_str.toLowerCase()) ||
                                        (barList.get(i).service_name.toLowerCase()).toString().contains(search_str.toLowerCase())){
                                    GD.barModels.add(barList.get(i));
                                }
                            } else {
                                if (switch_flag == 0) {
                                    if ((barList.get(i).business_name.toLowerCase()).toString().contains(search_str.toLowerCase())) {
                                        GD.barModels.add(barList.get(i));
                                    }
                                } else if (switch_flag == 1) {
                                    if ((barList.get(i).address.toLowerCase()).toString().contains(search_str.toLowerCase())) {
                                        GD.barModels.add(barList.get(i));
                                    }
                                }
                            }
                        }

                        if (GD.barModels.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Couldn't find bars matching your search please try again", Toast.LENGTH_LONG).show();
                            GD.barModels = barList;
                            MapViewPagerDataLoad(GD.barModels, true) ;
                        } else {
                            MapViewPagerDataLoad(GD.barModels, true) ;
                        }
                    } else {
                        search_statue = false;
                        search.setImageDrawable(null);
                        search.setBackgroundResource(R.mipmap.ico_search);
                        search_google_map.setText("");

                        MapViewPagerDataLoad(GD.barModels, true);
                    }
                }
            }
        });
        if(GD.category_option.equals("")){
            List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
            for(int i = 0; i < GD.barModels.size(); i++){
                String barname = GD.barModels.get(i).business_name;
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("type", Integer.toString(R.mipmap.icon_bar));
                hm.put("text", barname);
                aList.add(hm);
            }
            for(int i = 0; i < GD.gServiceTexts.size(); i++){
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("type", Integer.toString(R.mipmap.icon_service));
                hm.put("text", GD.gServiceTexts.get(i));
                aList.add(hm);
            }
            String[] from = {"type", "text"};
            int[] to = {R.id.flag, R.id.txt};
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.item_service_auto_custom, from, to);

            AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                    /** Each item in the adapter is a HashMap object.
                     *  So this statement creates the currently clicked hashmap object
                     * */
                    HashMap<String, String> hm = (HashMap<String, String>) arg0.getAdapter().getItem(position);

                    /** Getting a reference to the TextView of the layout file activity_main to set Currency */
                    search_google_map.setText(hm.get("text").toString());
                }
            };
            search_google_map.setOnItemClickListener(itemClickListener);
            search_google_map.setAdapter(adapter);
        }
    }

    // calling api for impressions
    private void callImpressionAPI() {
        try {
            String url = GD.BaseUrl + "add_impressions.php";

            ImpressionModel impressionModel = new ImpressionModel();
            Set<Integer> hs = new HashSet<>();
            hs.addAll(GD.barIdArrayList);
            GD.barIdArrayList.clear();
            GD.barIdArrayList.addAll(hs);
            impressionModel.user_id = GD.customerModel.user_id;
            impressionModel.bar_id_list = GD.barIdArrayList;

            GD.barIdEngagement.remove(0);

            impressionModel.engagement_list = GD.barIdEngagement;

            Gson gson = new Gson();
            String json = gson.toJson(impressionModel);

            new impressionWorker().execute(url, json);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public class impressionWorker extends AsyncTask<String, String, String> {
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
                    JSONObject loginJson = new JSONObject(response);
                    String success = loginJson.getString("success");
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

    private ArrayList<BarModel> adding_bars_by_category(String category_name) {
        ArrayList<BarModel> barModels = new ArrayList<BarModel>();
        for (int i = 0; i < GD.customerModel.bars.size(); i ++) {
            if (GD.customerModel.bars.get(i).category.equals(category_name)) {
                barModels.add(GD.customerModel.bars.get(i));
            }
        }

        return barModels;
    }

    private double deg2rad(double deg) {return (deg * Math.PI / 180.0);}
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public void MapViewPagerDataLoad(ArrayList<BarModel> barModelArrayList, boolean search){

        locationModelArrayList = new ArrayList<>();
        for (int i = 0; i < barModelArrayList.size() ; i++) {
            LocationModel locationModel = new LocationModel();
            double latitude;
            double longitude;
            try {
                latitude = Double.valueOf(barModelArrayList.get(i).lat);
                longitude = Double.valueOf(barModelArrayList.get(i).lon);
            } catch (Exception ex){
                latitude = 0;
                longitude = 0;
            }
            locationModel.location_info = new LatLng(latitude, longitude);
            if (barModelArrayList.get(i).deals.size() == 0) {
                locationModel.deal_title = "This bar did not create any deals.";
            } else {
                locationModel.deal_title = barModelArrayList.get(i).deals.get(0).deal_title;
            }
            if (barModelArrayList.get(i).deals.size() == 0) {
                locationModel.description = "";
            } else {
                locationModel.description = barModelArrayList.get(i).deals.get(0).deal_description;
            }

            if (barModelArrayList.get(i).deals.size() == 0) {
                locationModel.remain = "";
            } else {
                locationModel.remain = String.valueOf(barModelArrayList.get(i).deals.get(0).deal_qty - barModelArrayList.get(i).deals.get(0).claimed) + " left Exp " + barModelArrayList.get(i).deals.get(0).deal_duration;
            }
            locationModel.bar_title = barModelArrayList.get(i).business_name;
            locationModel.bar_music = barModelArrayList.get(i).music_type;
            locationModel.galleryModel = barModelArrayList.get(i).galleryModel;
            locationModel.bar_id = barModelArrayList.get(i).bar_id ;
            if(distance(latitude, longitude, mLatitude, mLongitude) > 4 && !search)
                continue;
            locationModelArrayList.add(locationModel);
        }
//        if(locationModelArrayList.size() == 0){
//            return;
//        }

        if(mvp != null) {
            mvp.refreshOnSearch(new CustomerMapViewAdapter(getSupportFragmentManager(), locationModelArrayList));
        } else {
            mvp = new MapViewPager.Builder(this)
                    .mapFragment(map)
                    .viewPager(viewPager)
                    .LoadBundle(savebundle)
                    .position(GD.select_pos)
                    .adapter(new CustomerMapViewAdapter(getSupportFragmentManager(), locationModelArrayList))
                    .callback(this)
                    .build();
        }

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvp.setMyLocation();
            }
        });
    }
    @Override
    public void onMapViewPagerReady() {
        mvp.getMap().setPadding(
                0,
                Utils.dp(this, 40),
                Utils.getNavigationBarWidth(this),
                viewPager.getHeight() + Utils.getNavigationBarHeight(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        map.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        map.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }
}
