package com.application.tippzi.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.application.tippzi.Fragments.CustomerMapViewFragment;
import com.application.tippzi.Models.GalleryModel;
import com.application.tippzi.Models.LocationModel;
import com.application.tippzi.Service.MapViewPager;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.List;

public class CustomerMapViewAdapter extends MapViewPager.MultiAdapter {

    public static final int MAX_COUNT = 512;
    public static final String[] DEAL_TITLES = new String[MAX_COUNT] ;
    public static final String[] DESCRPTION = new String[MAX_COUNT] ;
    public static final String[] MUSIC = new String[MAX_COUNT] ;
    public static final String[] REMAIN = new String[MAX_COUNT] ;
    public static final String[] PAGE_TITLES = new String[MAX_COUNT] ;
    public static final GalleryModel[] GALLERY_MODELS = new GalleryModel[MAX_COUNT];
    public static final LatLng[] Seelect_Latlng = new LatLng[MAX_COUNT];
    public static final int[] bar_ids = new int[MAX_COUNT];
    private ArrayList<LocationModel> resultlocation  ;

    public CustomerMapViewAdapter(FragmentManager fm, ArrayList<LocationModel> locationModelArrayList) {
        super(fm);
        resultlocation = new ArrayList<>();
        resultlocation = locationModelArrayList;
        // camera positions
        try {
            for (int i = 0 ; i < resultlocation.size() ; i++) {
                PAGE_TITLES[i] = resultlocation.get(i).bar_title;
                DEAL_TITLES[i] = resultlocation.get(i).deal_title;
                DESCRPTION[i] = resultlocation.get(i).description;
                MUSIC[i] = resultlocation.get(i).bar_music;
                REMAIN[i] = resultlocation.get(i).remain;
                Seelect_Latlng[i] = resultlocation.get(i).location_info;
                GALLERY_MODELS[i] = resultlocation.get(i).galleryModel;
                bar_ids[i] = resultlocation.get(i).bar_id;
            }
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return resultlocation.size();
    }
    @Override
    public Fragment getItem(int position) {
        return CustomerMapViewFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return resultlocation.get(position).bar_title;
    }

    @Override
    public int getBarId(int position) {
        return resultlocation.get(position).bar_id;
    }

    @Override
    public String getMarkerTitle(int page, int position) {
        return resultlocation.get(page).bar_title;
    }

    @Override
    public CameraPosition getCameraPositions(int page) {
        List<CameraPosition> result = new ArrayList<>();
        CameraPosition results
                =  new CameraPosition.Builder().target(resultlocation.get(page).location_info).zoom(14.0f)
                .bearing(0)
                .tilt(0)
                .build();
        result.add(results);
        return results;
    }
}
