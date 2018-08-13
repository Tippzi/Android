package com.application.tippzi.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import com.application.tippzi.Adapters.BusinessMapViewAdapter;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.LocationModel;
import com.application.tippzi.R;
import com.application.tippzi.Service.MapViewPager;
import com.application.tippzi.until.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BusinessMapViewActivity extends AbstractActivity implements MapViewPager.Callback {

    private ArrayList<LocationModel> locationModelArrayList ;
    private ViewPager viewPager;
    private ImageView iv_back ;
    private MapViewPager mvp;
    private int select_pos ;
    private GoogleMap map;
    SupportMapFragment mapFragment;
    private Bundle savebundle ;
    private FloatingActionButton FAB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_mapview);

//        Mapbox.getInstance(this, "pk.eyJ1IjoicGV0ZXJwYW5tYXBib3giLCJhIjoiY2plcjJkN2QyMGFwZjJ3cGFuYnR6cDFoZSJ9.4U_mCCQ9LpUXKcQQb2NNuw");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        savebundle = savedInstanceState ;

        viewPager = findViewById(R.id.viewPager);
        iv_back = findViewById(R.id.iv_bs_back);
        FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        viewPager.setPageMargin(Utils.dp(this, 10));
        Utils.setMargins(viewPager, 0, 0, 0, 0);

        locationModelArrayList = new ArrayList<>();
        for (int i = 0; i < GD.businessModel.bars.size() ; i++){
            LocationModel locationModel = new LocationModel();
            double latitude = 0;
            double longitude = 0;
            try {
                latitude = Double.valueOf(GD.businessModel.bars.get(i).lat);
                longitude = Double.valueOf(GD.businessModel.bars.get(i).lon);
            } catch (Exception ex){

            }
            locationModel.location_info = new LatLng(latitude, longitude);

            if (GD.businessModel.bars.get(i).deals.size() == 0) {
                locationModel.deal_title = "You didn't create any deal still.";
            } else {
                locationModel.deal_title = GD.businessModel.bars.get(i).deals.get(0).deal_title;
            }

            if (GD.businessModel.bars.get(i).deals.size() == 0) {
                locationModel.description = "";
            } else {
                locationModel.description = GD.businessModel.bars.get(i).deals.get(0).deal_description;
            }

            if (GD.businessModel.bars.get(i).deals.size() == 0) {
                locationModel.remain = "";
            } else {
                locationModel.remain = String.valueOf(GD.businessModel.bars.get(i).deals.get(0).deal_qty - GD.businessModel.bars.get(i).deals.get(0).claimed) + " remaining Exp " + GD.businessModel.bars.get(i).deals.get(0).deal_duration;
            }

            if (GD.businessModel.bars.get(i).business_name.length() > 20) {
                locationModel.bar_title = GD.businessModel.bars.get(i).business_name.substring(0, 20) + "...";
            } else {
                locationModel.bar_title = GD.businessModel.bars.get(i).business_name;
            }

            if (GD.businessModel.bars.get(i).music_type.length() > 25) {
                locationModel.bar_music = GD.businessModel.bars.get(i).music_type.substring(0, 25);
            } else {
                locationModel.bar_music = GD.businessModel.bars.get(i).music_type;
            }
            locationModel.galleryModel = GD.businessModel.bars.get(i).galleryModel;

            locationModelArrayList.add(locationModel);
        }

        if ( GD.select_pos == 0 ) {
            select_pos = 0 ;
        } else {
            select_pos = GD.select_pos ;
        }
        mvp = new MapViewPager.Builder(this)
                .mapFragment(mapFragment)
                .viewPager(viewPager)
                .LoadBundle(savebundle)
                .position(select_pos)
                .adapter(new BusinessMapViewAdapter(getSupportFragmentManager(), locationModelArrayList))
                .callback(this)
                .build();
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvp.setMyLocation();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
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
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
