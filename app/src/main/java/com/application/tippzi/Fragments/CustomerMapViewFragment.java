package com.application.tippzi.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.tippzi.Activities.CustomerBarDetailActivity;
import com.application.tippzi.Activities.SignInScreen;
import com.application.tippzi.Adapters.BusinessDetailBarAdapter;
import com.application.tippzi.Adapters.CustomerMapViewAdapter;
import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.ImpressionModel;
import com.application.tippzi.Models.SignInModel;
import com.application.tippzi.Models.WalletModel;
import com.application.tippzi.R;
import com.application.tippzi.Service.RoundedImageView;
import com.application.tippzi.swipeview.SwipeAdapterView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;

public class CustomerMapViewFragment extends Fragment implements SwipeAdapterView.onSwipeListener {

    private int index;
    private TextView bar_title, remain_google_map, bar_music_google_map ;
    private ImageView  ticket_google_map;
    private RoundedImageView profile_image_slider;
    private LatLng latLng ;
    private LinearLayout lay_bottom_select ;
    private BusinessDetailBarAdapter businessDealViewAdapter;
    private SwipeAdapterView mapcardsContainer;
    private ArrayList<DealModel> dataList;
    private ArrayList<Integer> idhistory;

    public CustomerMapViewFragment() { }

    public static CustomerMapViewFragment newInstance(int i) {
        CustomerMapViewFragment f = new CustomerMapViewFragment();
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

        bar_title = view.findViewById(R.id.bar_title);
        bar_music_google_map = view.findViewById(R.id.tv_bar_music_google_map);
        remain_google_map = view.findViewById(R.id.tv_remain_google_map);
        profile_image_slider = view.findViewById(R.id.iv_profile_image_slider);
        mapcardsContainer = view.findViewById(R.id.container_view);
        lay_bottom_select = view.findViewById(R.id.lay_bottom_select) ;

        lay_bottom_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GD.customerModel.bars.get(GD.select_pos).deals.size() == 0 ) {
                    Toast.makeText(getActivity(), "This bar has not deal now.", Toast.LENGTH_LONG).show();
                } else {
                    GD.select_user_type = "customer";
                    Intent intent = new Intent(getActivity(), CustomerBarDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                    getActivity().finish();
                }
                callImpressionAPI();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        Log.d("index", "" + index);
        latLng = CustomerMapViewAdapter.Seelect_Latlng[index];

        if (CustomerMapViewAdapter.PAGE_TITLES[index].length() > 18) {
            bar_title.setText(CustomerMapViewAdapter.PAGE_TITLES[index].substring(0, 18) + "...");
        } else {
            bar_title.setText(CustomerMapViewAdapter.PAGE_TITLES[index]);
        }

        bar_music_google_map.setText(CustomerMapViewAdapter.MUSIC[index]);
        remain_google_map.setText(CustomerMapViewAdapter.REMAIN[index]);
        if (!CustomerMapViewAdapter.GALLERY_MODELS[index].background1.equals("")){
            Picasso.with(getContext()).load(GD.downloadUrl + GD.barModels.get(index).galleryModel.background1).fit().skipMemoryCache().into(profile_image_slider);
        } else if (!CustomerMapViewAdapter.GALLERY_MODELS[index].background2.equals("")){
            Picasso.with(getContext()).load(GD.downloadUrl + GD.barModels.get(index).galleryModel.background2).fit().skipMemoryCache().into(profile_image_slider);
        } else if (!CustomerMapViewAdapter.GALLERY_MODELS[index].background3.equals("")){
            Picasso.with(getContext()).load(GD.downloadUrl + GD.barModels.get(index).galleryModel.background3).fit().skipMemoryCache().into(profile_image_slider);
        } else if (!CustomerMapViewAdapter.GALLERY_MODELS[index].background4.equals("")) {
            Picasso.with(getContext()).load(GD.downloadUrl + GD.barModels.get(index).galleryModel.background4).fit().skipMemoryCache().into(profile_image_slider);
        } else if (!CustomerMapViewAdapter.GALLERY_MODELS[index].background5.equals("")) {
            Picasso.with(getContext()).load(GD.downloadUrl + GD.barModels.get(index).galleryModel.background5).fit().skipMemoryCache().into(profile_image_slider);
        } else if (!CustomerMapViewAdapter.GALLERY_MODELS[index].background6.equals("")) {
            Picasso.with(getContext()).load(GD.downloadUrl + GD.barModels.get(index).galleryModel.background6).fit().skipMemoryCache().into(profile_image_slider);
        } else {
        }

        GD.slide_index = 0 ;
        //swipe part
        ButterKnife.bind(getActivity());

        RefreshBarDetailData();

    }

    public void RefreshBarDetailData() {
        getBarDeatilData();
        setAdapters();
        setListeners();
    }

    private void getBarDeatilData() {
        dataList = new ArrayList<>();
        for (int i = 0 ; i < GD.barModels.get(index).deals.size() ; i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.barModels.get(index).deals.get(i).deal_id ;
            dealModel.deal_title = GD.barModels.get(index).deals.get(i).deal_title ;
            dealModel.deal_description = GD.barModels.get(index).deals.get(i).deal_description ;
            dealModel.deal_days = GD.barModels.get(index).deals.get(i).deal_days ;
            dealModel.deal_duration = GD.barModels.get(index).deals.get(i).deal_duration ;
            dealModel.deal_qty = GD.barModels.get(index).deals.get(i).deal_qty ;
            dealModel.claimed = GD.barModels.get(index).deals.get(i).claimed ;
            dealModel.in_wallet = GD.barModels.get(index).deals.get(i).in_wallet ;
            dealModel.impressions = GD.barModels.get(index).deals.get(i).impressions ;
            dealModel.category = GD.barModels.get(index).category;
            dataList.add(dealModel);
        }
    }

    private void setAdapters() {
        businessDealViewAdapter = new BusinessDetailBarAdapter(getActivity(), dataList);
        mapcardsContainer.setAdapter(businessDealViewAdapter);

        mapcardsContainer.setOnItemClickListener(new SwipeAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(Object dataObject) {
                GD.select_user_type = "customer";

                Intent intent = new Intent(getActivity(), CustomerBarDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                getActivity().finish();


                callImpressionAPI();
            }
        });

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
        if (GD.temp_index_count == 0) {
            GD.temp_index_count = 1 ;
            RefreshBarDetailData();
        } else {
            if (GD.slide_index == GD.barModels.get(index).deals.size()-1) {
                int first_index = 0 ;
                int last_index = GD.barModels.get(index).deals.size() ;
                ResetCardViewData(first_index, last_index, 2);
            } else {
                GD.slide_index++;
                remain_google_map.setText(String.valueOf(GD.barModels.get(index).deals.get(GD.slide_index).deal_qty - GD.barModels.get(index).deals.get(GD.slide_index).claimed) + " remaining Exp " + GD.barModels.get(index).deals.get(GD.slide_index).deal_duration);
            }
        }
    }

    @Override
    public void onRightCardExit(Object dataObject) {
        if (GD.temp_index_count == 0) {
            GD.temp_index_count = 1 ;
            RefreshBarDetailData();
        } else {
            if (GD.slide_index == GD.barModels.get(index).deals.size() - 1) {
                int first_index = 0;
                int last_index = GD.barModels.get(index).deals.size();
                ResetCardViewData(first_index, last_index, 2);
            } else {
                GD.slide_index++;
                remain_google_map.setText(String.valueOf(GD.barModels.get(index).deals.get(GD.slide_index).deal_qty - GD.barModels.get(index).deals.get(GD.slide_index).claimed) + " remaining Exp " + GD.barModels.get(index).deals.get(GD.slide_index).deal_duration);
            }
        }
    }

    private void ResetCardViewData(int first_index, int last_index, int flag) {
        dataList = new ArrayList<>();
        for (int i = first_index; i < last_index; i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.barModels.get(index).deals.get(i).deal_id;
            dealModel.deal_title = GD.barModels.get(index).deals.get(i).deal_title;
            dealModel.deal_description = GD.barModels.get(index).deals.get(i).deal_description;
            dealModel.deal_days = GD.barModels.get(index).deals.get(i).deal_days;
            dealModel.deal_duration = GD.barModels.get(index).deals.get(i).deal_duration;
            dealModel.deal_qty = GD.barModels.get(index).deals.get(i).deal_qty;
            dealModel.claimed = GD.barModels.get(index).deals.get(i).claimed;
            dealModel.in_wallet = GD.barModels.get(index).deals.get(i).in_wallet;
            dealModel.impressions = GD.barModels.get(index).deals.get(i).impressions;
            dealModel.index_number = i;
            dealModel.category = GD.barModels.get(index).category;
            dataList.add(dealModel);
        }
        if (flag == 1) {
            mapcardsContainer.undo();
            GD.slide_index--;
        } else if (flag == 2) {
            GD.slide_index = 0;
        }
        remain_google_map.setText(String.valueOf(GD.barModels.get(index).deals.get(GD.slide_index).deal_qty - GD.barModels.get(index).deals.get(GD.slide_index).claimed) + " remaining Exp " + GD.barModels.get(index).deals.get(GD.slide_index).deal_duration);
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

    private void callImpressionAPI() {

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
}
