package com.application.tippzi.Service;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.tippzi.Adapters.BarTitleViewAdapter;
import com.application.tippzi.Adapters.CustomerMapViewAdapter;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.R;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.exceptions.InvalidLatLngBoundsException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MapViewPager extends FrameLayout implements OnMapReadyCallback {

    public interface Callback {
        void onMapViewPagerReady();
    }

    public static abstract class AbsAdapter extends FragmentStatePagerAdapter {
        public AbsAdapter(FragmentManager fm) {
            super(fm);
        }
    }

    public static abstract class Adapter extends AbsAdapter {
        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public abstract CameraPosition getCameraPosition(int position);
    }

    public static abstract class MultiAdapter extends AbsAdapter {
        public MultiAdapter(FragmentManager fm) {
            super(fm);
        }

        public abstract List<CameraPosition> getCameraPositions(int page);

        public abstract String getMarkerTitle(int page, int position);

        public abstract int getBarId(int position);
    }


    private static final float DEFAULT_ALPHA = 1.0f;
    private float markersAlpha = DEFAULT_ALPHA;
    private int mapGravity = 1;
    private int mapWeight = 1, pagerWeight = 1;
    private int mapPaddingLeft, mapPaddingTop, mapPaddingRight, mapPaddingBottom;
    private int mapOffset;

    private MapboxMap map = null;
    private MapView mapFragment;
    private ViewPager viewPager;
    private AbsAdapter adapter;
    private Callback callback;

    protected CameraUpdate defaultPosition;
    private List<CameraUpdate> defaultPositions;
    protected List<Marker> markers;
    private List<List<Marker>> allMarkers;

    private int initialPosition;
    private boolean hidden = false;
    private Context mContext;
    private MarkerViewOptions mo;
    private Icon icon;
    private int newWidth;
    private int newHeight;
    private LatLng select_location;
    private Bitmap resizedBitmap;

    public void clearMap() {
        if (map != null) {
            for (int page = 0; page < allMarkers.size(); page++) {
                LinkedList<Marker> pageMarkers = (LinkedList<Marker>) allMarkers.get(page);
                for (int markIdx = 0; markIdx < pageMarkers.size(); markIdx++) {
                    Marker _marker = pageMarkers.get(markIdx);
                    map.removeMarker(_marker);
                }
            }
            map.clear();
        }
    }

    public MapViewPager(Context context) {
        super(context);
        // not calling initialize(context) to use it with Builder
    }

    public MapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public MapViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        mapOffset = dp(30);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MapViewPager, 0, 0);
            try {
                markersAlpha = a.getFloat(R.styleable.MapViewPager_markersAlpha, DEFAULT_ALPHA);
                pagerWeight = a.getInteger(R.styleable.MapViewPager_viewPagerWeight, 1);
                mapWeight = a.getInteger(R.styleable.MapViewPager_mapWeight, 1);
                mapGravity = a.getInteger(R.styleable.MapViewPager_mapGravity, 1);
                mapOffset = a.getDimensionPixelSize(R.styleable.MapViewPager_mapOffset, mapOffset);
                mapPaddingLeft = a.getDimensionPixelSize(R.styleable.MapViewPager_mapPaddingLeft, 0);
                mapPaddingTop = a.getDimensionPixelSize(R.styleable.MapViewPager_mapPaddingTop, 0);
                mapPaddingRight = a.getDimensionPixelSize(R.styleable.MapViewPager_mapPaddingRight, 0);
                mapPaddingBottom = a.getDimensionPixelSize(R.styleable.MapViewPager_mapPaddingBottom, 0);
            } finally {
                a.recycle();
            }
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (mapGravity) {
            case 0:
                inflater.inflate(R.layout.mapviewpager_left, this);
                break;
            case 1:
                inflater.inflate(R.layout.mapviewpager_top, this);
                break;
            case 2:
                inflater.inflate(R.layout.mapviewpager_right, this);
                break;
            case 3:
                inflater.inflate(R.layout.mapviewpager_bottom, this);
                break;
        }
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter) {

        start(activity, mapAdapter, 0, null);
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter,
                      @Nullable Callback callback) {
        start(activity, mapAdapter, 0, callback);
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter,
                      int initialPosition) {
        start(activity, mapAdapter, initialPosition, null);
    }

    public void start(@NonNull FragmentActivity activity,
                      @NonNull AbsAdapter mapAdapter,
                      int initialPosition,
                      @Nullable Callback callback) {
        this.initialPosition = initialPosition;
        this.callback = callback;
        adapter = mapAdapter;
        mapFragment = activity.findViewById(R.id.map1);
        viewPager = findViewById(R.id.pager);
        setWeights();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapbox) {
        map = mapbox;
        map.getUiSettings().setAttributionEnabled(false);
        map.getUiSettings().setLogoEnabled(false);
        map.getUiSettings().setDoubleTapGesturesEnabled(true);
//        map.getUiSettings().setCompassEnabled(true);
//        map.getUiSettings().setCompassFadeFacingNorth(true);
        map.setPadding(mapPaddingLeft, mapPaddingTop, mapPaddingRight, mapPaddingBottom);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(initialPosition);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                moveTo(position, true);
            }
        });
//        populate_origin((MultiAdapter) adapter);
        populate();
        if (callback != null) callback.onMapViewPagerReady();
        map.setOnCameraIdleListener(onMoveEnd);
        //moveTo(viewPager.getCurrentItem(), false);
        if(adapter.getCount() == 0){
            setMyLocation();
        } else
        moveTo(viewPager.getCurrentItem(), true);
    }

    public void refreshOnSearch(CustomerMapViewAdapter pAdapter) {
        clearMap();
        adapter = pAdapter;
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(initialPosition);
        moveTo(viewPager.getCurrentItem(), true);
        populate();
    }

    public void populate() {
//        if (adapter instanceof MultiAdapter) populateMulti((MultiAdapter) adapter);
//        else populateSingle((Adapter) adapter);
        populateMulti((MultiAdapter) adapter);

    }

    private void populateMulti(final MultiAdapter adapter) {
        allMarkers = new LinkedList<>();
        for (int page = 0; page < adapter.getCount(); page++) {
            LinkedList<Marker> pageMarkers = new LinkedList<>();
            if (adapter.getCameraPositions(page) != null) {
//                String category = adapter.getBarId(page);
                for (int i = 0; i < adapter.getCameraPositions(page).size(); i++) {
                    final CameraPosition cp = adapter.getCameraPositions(page).get(i);
                    if (cp != null) {
                        double lat = cp.target.getLatitude(); double lon = cp.target.getLongitude();
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        map.setInfoWindowAdapter(new BarTitleViewAdapter(inflater));
                        mo = createMarkerOptions(cp, adapter.getMarkerTitle(page, i), "unselect");
                        MarkerView mv = map.addMarker(mo);
//                        map.addMarker(mo).setAnchor(0.5f, 0.0f);
//                        map.addMarker(mo).setInfoWindowAnchor(0.5f, 0.0f);
//                        pageMarkers.add(map.addMarker(mo));
                        mv.setInfoWindowAnchor(0.5f, 0.1f);
//                        mv.setAnchor(0.5f, 0.5f);
                        pageMarkers.add(mv);
                    } else pageMarkers.add(null);
                }
            }
            allMarkers.add(pageMarkers);
        }

//        for (int i = 0; i < GD.barModels.size(); i++){
//            BarModel bar = GD.barModels.get(i);
//            LinkedList<Marker> pageMarkers = new LinkedList<>();
//            double latitude;
//            double longitude;
//            try {
//                latitude = Double.valueOf(bar.lat);
//                longitude = Double.valueOf(bar.lon);
//            } catch (Exception ex){
//                latitude = 0;
//                longitude = 0;
//            }
//            LatLng location_info = new LatLng(latitude, longitude);
//            CameraPosition cp
//                    =  new CameraPosition.Builder().target(location_info).zoom(14.0f)
//                    .bearing(0)
//                    .tilt(0)
//                    .build();
//            LayoutInflater inflater = LayoutInflater.from(mContext);
//            map.setInfoWindowAdapter(new BarTitleViewAdapter(inflater));
//            mo = createMarkerOptions(cp, bar.business_name, "unselect");
//            map.addMarker(mo).setAnchor(0.5f, 0.0f);
//            map.addMarker(mo).setInfoWindowAnchor(0.5f, 0.0f);
//            pageMarkers.add(map.addMarker(mo));
//        }

        map.getMarkerViewManager().setOnMarkerViewClickListener(createMarkerClickListenerMulti(adapter));
        initDefaultPositions(adapter);
    }

    private MapboxMap.OnMarkerViewClickListener createMarkerClickListenerMulti(final MultiAdapter adapter1) {
        return new MapboxMap.OnMarkerViewClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker, @NonNull View view, @NonNull MapboxMap.MarkerViewAdapter adapter) {
                //map.clear();
                CameraPosition select =
                        new CameraPosition.Builder().target(new LatLng(marker.getPosition().getLatitude(), marker.getPosition().getLongitude()))
                                .zoom(13.5f)
                                .bearing(0)
                                .tilt(0)
                                .build();
                //populateMulti_1(adapter1, select);
                for (int page = 0; page < adapter1.getCount(); page++) {
                    if (adapter1.getCameraPositions(page) != null) {
                        for (int i = 0; i < adapter1.getCameraPositions(page).size(); i++) {
                            CameraPosition cp = adapter1.getCameraPositions(page).get(i);
                            if (cp != null && cp.target != null
                                    && cp.target.getLatitude() == marker.getPosition().getLatitude()
                                    && cp.target.getLongitude() == marker.getPosition().getLongitude()) {

                                GD.barIdEngagement.add(adapter1.getBarId(page));

                                GD.select_pos = page;
                                if (marker.isInfoWindowShown()) { // this doesn't seem to work !!
                                    Log.d("clck2", "2");
                                    viewPager.setCurrentItem(page);
                                    CameraPosition results
                                            = new CameraPosition.Builder().target(new LatLng(marker.getPosition().getLatitude(), marker.getPosition().getLongitude()))
                                            .zoom(13.5f)
                                            .bearing(0)
                                            .tilt(0)
                                            .build();
                                    //mo = createMarkerOptions(results, marker.getTitle(), "select");
                                    //map.selectMarker( map.addMarker(mo));

                                    return true;
                                } else {
                                    viewPager.setCurrentItem(page);
                                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                                    CameraPosition results
                                            = new CameraPosition.Builder().target(new LatLng(marker.getPosition().getLatitude(), marker.getPosition().getLongitude()))
                                            .zoom(13.5f)
                                            .bearing(0)
                                            .tilt(0)
                                            .build();
                                    //mo = createMarkerOptions(results, marker.getTitle(), "select");
                                    //map.selectMarker( map.addMarker(mo));

                                    return true;
                                }
                            } else {
//                                CameraPosition results
//                                        = new CameraPosition.Builder().target(new LatLng(marker.getPosition().getLatitude(), marker.getPosition().getLongitude()))
//                                        .zoom(14.0f)
//                                        .bearing(0)
//                                        .tilt(0)
//                                        .build();
//                                mo = createMarkerOptions(results, adapter1.getPageTitle(i).toString(), "unselect");
//                                map.addMarker(mo);
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    public void setMyLocation() {
        map.setMyLocationEnabled(true);
        if (map.getMyLocation() != null) {
            CameraPosition select =
                    new CameraPosition.Builder().target(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
                            .zoom(13.5f)
                            .bearing(0)
                            .tilt(0)
                            .build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(select));
        } else {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            CameraPosition select =
                    new CameraPosition.Builder().target(new LatLng(latitude, longitude))
                            .zoom(13.5f)
                            .bearing(0)
                            .tilt(0)
                            .build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(select));
        }
    }

    public void setMyLocation_dep(){
        map.setMyLocationEnabled(true);
        Location location = map.getMyLocation();
        if(location == null)
            return;
        CameraPosition select =
                new CameraPosition.Builder().target(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
                        .zoom(13.5f)
                        .bearing(0)
                        .tilt(0)
                        .build();
        map.setCameraPosition(select);
        //map.moveCamera(CameraUpdateFactory.newCameraPosition(select));
    }

    private void populateMulti_1(final MultiAdapter adapter, CameraPosition select_cp) {
        map.clear();
        allMarkers = new LinkedList<>();
        for (int page = 0; page < adapter.getCount(); page++) {
            LinkedList<Marker> pageMarkers = new LinkedList<>();
            if (adapter.getCameraPositions(page) != null) {
                for (int i = 0; i < adapter.getCameraPositions(page).size(); i++) {
                    final CameraPosition cp = adapter.getCameraPositions(page).get(i);
                    if (cp != null) {
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        map.setInfoWindowAdapter(new BarTitleViewAdapter(inflater));
                        if (cp.target.getLatitude() == select_cp.target.getLatitude() && cp.target.getLongitude() == select_cp.target.getLongitude()){
                            //mo = createMarkerOptions(cp, adapter.getMarkerTitle(page, i),"select");
                            //map.addMarker(mo).setAnchor(0.5f, 0.0f);
                            //map.addMarker(mo).setInfoWindowAnchor(0.5f, 0.0f);
                        } else {
                            mo = createMarkerOptions(cp, adapter.getMarkerTitle(page, i),"unselect");
                            map.addMarker(mo).setAnchor(1.0f, 1.0f);
                            map.addMarker(mo).setInfoWindowAnchor(1.0f, 0.0f);
                        }
                        pageMarkers.add(map.addMarker(mo));
                    }
                    else pageMarkers.add(null);
                }
            }
            allMarkers.add(pageMarkers);
        }
        initDefaultPositions(adapter);
    }

    private void moveTo(int page, boolean animate) {
        /*if (adapter instanceof MultiAdapter) moveToMulti((MultiAdapter) adapter,page, animate);
        else moveToSingle((Adapter) adapter, page, animate);*/
        moveToMulti((MultiAdapter)adapter, page, animate);
    }

    private void moveToSingle(Adapter adapter, int index, boolean animate) {
        CameraPosition cp = adapter.getCameraPosition(index);
        CameraUpdate cu;
        if (cp != null && cp.target != null
                && cp.target.getLatitude() != 0.0
                && cp.target.getLongitude() != 0.0) {
            cu = CameraUpdateFactory.newCameraPosition(cp);
            if (hidden) showMarkers();
            if (markers.get(index) != null) markers.get(index).showInfoWindow(map,mapFragment);
        }
        else {
            cu = defaultPosition;
            hideInfoWindowSingle();
        }
        if (animate) map.animateCamera(cu);
        else map.moveCamera(cu);
    }

    private Marker updateMarks(final MultiAdapter adapter, CameraPosition select_cp, String title){
        Marker res = null;
        for(int page = 0; page < allMarkers.size(); page++){
            LinkedList<Marker> pageMarkers = (LinkedList<Marker>)allMarkers.get(page);
            for(int markIdx = 0; markIdx < pageMarkers.size(); markIdx++){
                Marker _marker = pageMarkers.get(markIdx);
                String markerTitle = _marker.getTitle();
                if (select_cp.target.getLatitude() == _marker.getPosition().getLatitude()
                        && select_cp.target.getLongitude() == _marker.getPosition().getLongitude()
                        && markerTitle.equals(title)){
                    //_marker.setIcon(iconSelected());
                    res = _marker;
                } else {
                    _marker.setIcon(resizeBitmap(R.mipmap.ico_unselected_bar, "unselect"));
                }
                map.updateMarker(_marker);
            }
        }
        if(res != null){
            res.setIcon(iconSelected());
            map.updateMarker(res);
        }
        initDefaultPositions(adapter);
        return res;
    }

    private boolean isSwipingBar = false;
    private int nSwipingId = -1;
    MapboxMap.OnCameraIdleListener onMoveEnd = new MapboxMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            Log.e("onCameraIdle", map.getCameraPosition().toString());
            if(isSwipingBar){
                MultiAdapter _adapter = (MultiAdapter)adapter;
                String title = _adapter.getMarkerTitle(nSwipingId, 0);
                CameraPosition select =
                    new CameraPosition.Builder().target(
                            new LatLng(_adapter.getCameraPositions(nSwipingId).get(0).target.getLatitude(),
                                    _adapter.getCameraPositions(nSwipingId).get(0).target.getLongitude()))
                            .zoom(14.0f)
                            .bearing(0)
                            .tilt(0)
                            .build();

                Marker _selectedMarker = updateMarks(_adapter, select, title);
                if(_selectedMarker != null){
                    map.selectMarker(_selectedMarker);
                    map.updateMarker(_selectedMarker);
                }
                SharedPreferences pref = getContext().getSharedPreferences("Tippzi", 0);
                if (pref.getString("user_type", "").equals("Business")) {

                } else if(pref.getString("user_type", "").equals("Customer")){
//                    populate();
                    select_location = new LatLng(_adapter.getCameraPositions(nSwipingId).get(0).target.getLatitude(),_adapter.getCameraPositions(nSwipingId).get(0).target.getLongitude());
                    for (int i = 0 ; i < GD.customerModel.bars.size() ; i++) {
                        double lat = 0;
                        double lon = 0;
                        try {
                            lat = Double.valueOf(GD.customerModel.bars.get(i).lat);
                            lon = Double.valueOf(GD.customerModel.bars.get(i).lon);
                        }catch(Exception ex){
                            lat = 0;
                            lon = 0;
                        }
                        double calc_distance = distance(select_location.getLatitude(), select_location.getLongitude(), lat, lon) ;
                        if (calc_distance <=  1) {
                            if (GD.customerModel.bars.get(i).deals.get(0).impressions_check == false) {
                                GD.barIdArrayList.add(GD.customerModel.bars.get(i).bar_id);
                            }
                        }
                    }

                    GD.barIdEngagement.add(GD.customerModel.bars.get(nSwipingId).bar_id);
                }
            }
        }
    };

    private void moveToMulti(MultiAdapter adapter, int page, boolean animate) {
        try {
            isSwipingBar = true;
            nSwipingId = page;
            CameraUpdate cu = defaultPositions.get(page);
            GD.temp_index_count = 0 ;
            if (cu == null) cu = defaultPosition;
            if (animate) {
                map.animateCamera(cu);
            } else {
                map.moveCamera(cu);
            }
            GD.select_pos = page;
        } catch (RuntimeException e){
            e.printStackTrace();
        }
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

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void initDefaultPosition() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) if (marker != null) builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        defaultPosition = CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
    }

    private void initDefaultPositions(final MultiAdapter adapter) {
        // each page
        try {
            defaultPositions = new LinkedList<>();
            for (int i = 0; i < adapter.getCount() ; i++) {
                defaultPositions.add(getDefaultPagePosition(adapter, i));
            }
            // global
            LinkedList<Marker> all = new LinkedList<>();
            for (List<Marker> list : allMarkers) if (list != null) all.addAll(list);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : all) if (marker != null) builder.include(marker.getPosition());
            LatLngBounds bounds = builder.build();
            defaultPosition = CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
        }catch (InvalidLatLngBoundsException e) {
            e.printStackTrace();
        }
    }

    private CameraUpdate getDefaultPagePosition(final MultiAdapter adapter, int page) {
        if (allMarkers.get(page).size() == 0)
            return null;
        if (allMarkers.get(page).size() == 1)
            return CameraUpdateFactory.newCameraPosition(adapter.getCameraPositions(page).get(0));
        // more than 1 marker on this page
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : allMarkers.get(page)) if (marker != null) builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        return CameraUpdateFactory.newLatLngBounds(bounds, mapOffset);
    }

    private MarkerViewOptions createMarkerOptions(CameraPosition cp, String title, String select_flag) {
        if (cp == null) return null;
        if (select_flag.equals("select")) {
            icon = iconSelected();
        } else if (select_flag.equals("unselect")){
            icon = resizeBitmap(R.mipmap.ico_unselected_bar, select_flag);
        }
        return new MarkerViewOptions()
                .position(new LatLng(cp.target.getLatitude(), cp.target.getLongitude()))
                .icon(icon)
                .anchor(0.5f,0.0f)
                .infoWindowAnchor(0.5f,0.0f)
                .title(title);
    }

    private MarkerOptions createMarkerOptions_op(CameraPosition cp, String title, String select_flag) {
        if (cp == null) return null;
        if (select_flag.equals("select")) {
            icon = iconSelected();
        } else if (select_flag.equals("unselect")){
            icon = resizeBitmap(R.mipmap.ico_unselected_bar, select_flag);
        }
        return new MarkerOptions()
                .position(new LatLng(cp.target.getLatitude(), cp.target.getLongitude()))
                .icon(icon)
                .title(title);
    }

    private  Icon iconSelected(){
        if (GD.category_option.equals("")) {
            return resizeBitmap(R.mipmap.ico_selected_location, "select");
        } else if (GD.category_option.equals("Nightlife")) {
            return resizeBitmap(R.mipmap.ico_blue_location, "select");
        } else if (GD.category_option.equals("Health & Fitness")) {
            return resizeBitmap(R.mipmap.ico_yellow_icon, "select");
        } else if (GD.category_option.equals("Hair & Beauty")) {
            return resizeBitmap(R.mipmap.ico_red_location, "select");
        }
        return null;
    }

    private Icon resizeBitmap(int resId, String select_flag){
        if (select_flag.equals("select")) {
            newWidth = 200;
            newHeight = 200;
        } else if (select_flag.equals("unselect")){
            newWidth = 90;
            newHeight = 90;
        }
        BitmapDrawable bitmapDrawable =(BitmapDrawable) ContextCompat.getDrawable(getContext(), resId);
        Bitmap originalBitmap = bitmapDrawable.getBitmap();

        resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

        IconFactory iconFactory = IconFactory.getInstance(getContext());
        Icon icon = iconFactory.fromBitmap(resizedBitmap);

        return icon;
    }
    private void showMarkers() {
//        for (Marker marker : markers) if (marker != null) marker(1.0f);
    }

    private void showMarkersForPage(int page) {
        // make all translucent
        for (List<Marker> list : allMarkers) {
            for (Marker marker : list) {
                MarkerView markerView ;
                //if (marker != null) marker.getOp;
            }
        }
        // make this page ones opaque
        for (Marker marker : allMarkers.get(page)) {
//            if (marker != null) marker.setAlpha(1.0f);
        }
    }

    private void hideInfoWindowMulti() {
        for (List<Marker> list : allMarkers) {
            if (list != null) {
                for (Marker marker : list) {
                    if (marker != null) {
                        marker.hideInfoWindow();
                    }

                }
            }
        }
    }

    private void hideInfoWindowSingle() {
        hidden = true;
        for (Marker marker : markers) {
            if (marker != null) {
                marker.hideInfoWindow();
//                marker.setAlpha(markersAlpha);
            }
        }
    }

    private void setWeights() {
        // viewPager
        LinearLayout.LayoutParams pagerParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, mapWeight);
        viewPager.setLayoutParams(pagerParams);
        // mapFragment
        View mapView = mapFragment.getRootView();
        if (mapView != null) {
            LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, pagerWeight);
            mapView.setLayoutParams(mapParams);
        }
    }

    private int dp(int dp) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    // general getters

    public MapboxMap getMap() {
        return map;
    }

    public MapView getMapFragment() {
        return mapFragment;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public CameraUpdate getDefaultPosition() {
        return defaultPosition;
    }

    // single getters

    public Marker getMarker(int position) {
        return markers.get(position);
    }

    public List<Marker> getMarkers() {
        return markers;
    }


    // multi getters

    public Marker getMarker(int page, int position) {
        if (allMarkers.get(page) != null) return allMarkers.get(page).get(position);
        return null;
    }

    public List<Marker> getMarkers(int page) {
        return allMarkers.get(page);
    }

    public List<List<Marker>> getAllMarkers() {
        return allMarkers;
    }

    public CameraUpdate getDefaultPosition(int page) {
        return defaultPositions.get(page);
    }

    public List<CameraUpdate> getDefaultPositions() {
        return defaultPositions;
    }



    // Builder

    private MapViewPager(Builder builder, Context context) {
        super(context);
        // check that requited fields are provided
        if (context == null) throw new IllegalArgumentException("context can't be null");
        if (builder.mapFragment == null) throw new IllegalArgumentException("mapFragment can't be null");
        if (builder.viewPager == null) throw new IllegalArgumentException("viewPager can't be null");
        if (builder.adapter == null) throw new IllegalArgumentException("adapter can't be null");
        mapFragment = builder.mapFragment;
        viewPager = builder.viewPager;
        adapter = builder.adapter;
        callback = builder.callback;
        initialPosition = builder.position;
        markersAlpha = builder.markersAlpha;
        mapPaddingLeft = builder.mapPaddingLeft;
        mapPaddingTop = builder.mapPaddingTop;
        mapPaddingRight = builder.mapPaddingRight;
        mapPaddingBottom = builder.mapPaddingBottom;

        mContext = builder.context ;
        mapOffset = builder.mapOffset != 0 ? builder.mapOffset : dp(32);
        mapFragment.onCreate(builder.bundle);
        mapFragment.getMapAsync(this);
    }

    public static class Builder {

        private Context context;
        private MapView mapFragment;
        private ViewPager viewPager;
        private AbsAdapter adapter;
        private Callback callback;
        private int position;
        private float markersAlpha = DEFAULT_ALPHA;
        private int mapOffset;
        private Bundle bundle ;
        private int mapPaddingLeft, mapPaddingTop, mapPaddingRight, mapPaddingBottom;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder mapFragment(@NonNull MapView mapFragment) {
            this.mapFragment = mapFragment;
            return this;
        }

        public Builder LoadBundle(Bundle bundle) {
            this.bundle = bundle ;
            return this;
        }

        public Builder viewPager(@NonNull ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        public Builder adapter(@NonNull AbsAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder callback(@Nullable Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder position(int position) {
            this.position = position;
            return this;
        }

        public Builder markersAlpha(float alpha) {
            this.markersAlpha = alpha;
            return this;
        }

        public Builder mapOffset(int mapOffset) {
            this.mapOffset = mapOffset;
            return this;
        }

        public Builder mapPadding(int mapPaddingLeft,
                                  int mapPaddingTop,
                                  int mapPaddingRight,
                                  int mapPaddingBottom) {
            this.mapPaddingLeft = mapPaddingLeft;
            this.mapPaddingTop = mapPaddingTop;
            this.mapPaddingRight = mapPaddingRight;
            this.mapPaddingBottom = mapPaddingBottom;
            return this;
        }

        public MapViewPager build() {
            return new MapViewPager(this, context);
        }

    }

}
