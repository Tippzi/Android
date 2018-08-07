package com.application.tippzi.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.application.tippzi.Activities.BarDetailActivity;
import com.application.tippzi.Adapters.BusinessDetailBarAdapter;
import com.application.tippzi.Adapters.BusinessMapViewAdapter;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.R;
import com.application.tippzi.swipeview.SwipeAdapterView;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class BusinessMapViewFragment extends Fragment implements SwipeAdapterView.onSwipeListener {

    private int index;
    private TextView bar_title, remain_google_map, bar_music_google_map;
    private ImageView iv_profileimageview ;
    private SwipeAdapterView mapcardsContainer;
    private LatLng latLng ;
    private RelativeLayout layout ;
    private ArrayList<DealModel> dataList;
    private BusinessDetailBarAdapter businessDealViewAdapter;

    public BusinessMapViewFragment() { }

    public static BusinessMapViewFragment newInstance(int i) {
        BusinessMapViewFragment f = new BusinessMapViewFragment();
        Bundle args = new Bundle();
        args.putInt("INDEX", i);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_business_mapview, container, false);
        bar_title = view.findViewById(R.id.bar_title);
        bar_music_google_map = view.findViewById(R.id.tv_bar_music_google_map);
        remain_google_map = view.findViewById(R.id.tv_remain_google_map);
        iv_profileimageview = view.findViewById(R.id.iv_profile_image_slider);
        mapcardsContainer = view.findViewById(R.id.container_view);

        layout = view.findViewById(R.id.lay_select);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GD.select_user_type = "business";
                if (GD.businessModel.bars.get(0).deals.size() == 0) {
                    Toast.makeText(getActivity(), "Sorry, there is not deal you added already. Please input new deal for your bar.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getActivity(), BarDetailActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        latLng = BusinessMapViewAdapter.Seelect_Latlng[index];

        if (BusinessMapViewAdapter.PAGE_TITLES[index].length() > 18) {
            bar_title.setText(BusinessMapViewAdapter.PAGE_TITLES[index].substring(0, 18) + "...");
        } else {
            bar_title.setText(BusinessMapViewAdapter.PAGE_TITLES[index]);
        }

        bar_music_google_map.setText(BusinessMapViewAdapter.MUSIC[index]);
        remain_google_map.setText(BusinessMapViewAdapter.REMAIN[index]);

        if (!BusinessMapViewAdapter.GALLERY_MODELS[index].background1.equals("")){
            Picasso.with(getActivity()).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background1).into(iv_profileimageview);
        } else if (!BusinessMapViewAdapter.GALLERY_MODELS[index].background2.equals("")){
            Picasso.with(getActivity()).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background2).into(iv_profileimageview);
        } else if (!BusinessMapViewAdapter.GALLERY_MODELS[index].background3.equals("")){
            Picasso.with(getActivity()).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background3).into(iv_profileimageview);
        } else if (!BusinessMapViewAdapter.GALLERY_MODELS[index].background4.equals("")) {
            Picasso.with(getActivity()).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background4).into(iv_profileimageview);
        } else if (!BusinessMapViewAdapter.GALLERY_MODELS[index].background5.equals("")){
            Picasso.with(getActivity()).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background5).into(iv_profileimageview);
        } else if (!BusinessMapViewAdapter.GALLERY_MODELS[index].background6.equals("")){
            Picasso.with(getActivity()).load(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background6).into(iv_profileimageview);
        }

        ButterKnife.bind(getActivity());

        getBarDeatilData();
        setAdapters();
        setListeners();
    }

    private void getBarDeatilData() {
        dataList = new ArrayList<>();
        for (int i = 0 ; i < GD.businessModel.bars.get(0).deals.size() ; i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.businessModel.bars.get(0).deals.get(i).deal_id ;
            dealModel.deal_title = GD.businessModel.bars.get(0).deals.get(i).deal_title ;
            dealModel.deal_description = GD.businessModel.bars.get(0).deals.get(i).deal_description ;
            dealModel.deal_days = GD.businessModel.bars.get(0).deals.get(i).deal_days ;
            dealModel.deal_duration = GD.businessModel.bars.get(0).deals.get(i).deal_duration ;
            dealModel.deal_qty = GD.businessModel.bars.get(0).deals.get(i).deal_qty ;
            dealModel.claimed = GD.businessModel.bars.get(0).deals.get(i).claimed ;
            dealModel.in_wallet = GD.businessModel.bars.get(0).deals.get(i).in_wallet ;
            dealModel.impressions = GD.businessModel.bars.get(0).deals.get(i).impressions ;
            dealModel.impressions = GD.businessModel.bars.get(0).deals.get(i).impressions ;
            dataList.add(dealModel);
        }
    }

    private void setAdapters() {
        businessDealViewAdapter = new BusinessDetailBarAdapter(getActivity(), dataList);
        mapcardsContainer.setAdapter(businessDealViewAdapter);
    }

    private void setListeners() {
        mapcardsContainer.setSwipeListener(this);

    }

    @Override
    public void removeFirstObjectInAdapter() {
        dataList.remove(0);
        businessDealViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLeftCardExit(Object dataObject) {
        if (GD.slide_index == GD.businessModel.bars.get(0).deals.size()-1) {
            int first_index = 0 ;
            int last_index = GD.businessModel.bars.get(0).deals.size() ;
            ResetCardViewData(first_index, last_index, 2);
        } else {
            GD.slide_index++;
            remain_google_map.setText(String.valueOf(GD.businessModel.bars.get(0).deals.get(GD.slide_index).deal_qty - GD.businessModel.bars.get(0).deals.get(GD.slide_index).claimed) + " remaining Exp " + GD.businessModel.bars.get(0).deals.get(GD.slide_index).deal_duration);
        }
    }

    @Override
    public void onRightCardExit(Object dataObject) {
        if (GD.slide_index == GD.businessModel.bars.get(0).deals.size()-1) {
            int first_index = 0 ;
            int last_index = GD.businessModel.bars.get(0).deals.size() ;
            ResetCardViewData(first_index, last_index, 2);
        } else {
            GD.slide_index++;
            remain_google_map.setText(String.valueOf(GD.businessModel.bars.get(0).deals.get(GD.slide_index).deal_qty - GD.businessModel.bars.get(0).deals.get(GD.slide_index).claimed) + " remaining Exp " + GD.businessModel.bars.get(0).deals.get(GD.slide_index).deal_duration);
        }
    }

    private void ResetCardViewData(int first_index, int last_index, int flag) {
        for (int i = first_index; i < last_index; i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.businessModel.bars.get(0).deals.get(i).deal_id;
            dealModel.deal_title = GD.businessModel.bars.get(0).deals.get(i).deal_title;
            dealModel.deal_description = GD.businessModel.bars.get(0).deals.get(i).deal_description;
            dealModel.deal_days = GD.businessModel.bars.get(0).deals.get(i).deal_days;
            dealModel.deal_duration = GD.businessModel.bars.get(0).deals.get(i).deal_duration;
            dealModel.deal_qty = GD.businessModel.bars.get(0).deals.get(i).deal_qty;
            dealModel.claimed = GD.businessModel.bars.get(0).deals.get(i).claimed;
            dealModel.in_wallet = GD.businessModel.bars.get(0).deals.get(i).in_wallet;
            dealModel.impressions = GD.businessModel.bars.get(0).deals.get(i).impressions;
            dealModel.index_number = i;
            dataList.add(dealModel);
        }
        if (flag == 1) {
            mapcardsContainer.undo();
            GD.slide_index--;
        } else if (flag == 2) {
            GD.slide_index = 0;
        }
        remain_google_map.setText(String.valueOf(GD.businessModel.bars.get(0).deals.get(GD.slide_index).deal_qty - GD.businessModel.bars.get(0).deals.get(GD.slide_index).claimed) + " remaining Exp " + GD.businessModel.bars.get(0).deals.get(GD.slide_index).deal_duration);
        businessDealViewAdapter = new BusinessDetailBarAdapter(getActivity(), dataList);
        mapcardsContainer.setAdapter(businessDealViewAdapter);
        businessDealViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAdapterAboutToEmpty(int itemsInAdapter) {
    }

    @Override
    public void onScroll(float scrollProgressPercent) {
    }

}
