package com.application.tippzi.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.WalletModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CoinActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ACProgressFlower dialog;
    private float mZoom = 15.5f;

    @Override
    public void onMapReady(GoogleMap mapboxMap) {
        mapbox = mapboxMap;
        mapbox.getUiSettings().setCompassEnabled(false);
        mapbox.getUiSettings().setMyLocationButtonEnabled(false);
//        mapbox.getUiSettings().setAttributionEnabled(false);
//        mapbox.getUiSettings().setLogoEnabled(false);
//        mapbox.getUiSettings().setDoubleTapGesturesEnabled(true);
        setMyLocation();
        String url = GD.coinApi + "get_near_coins";
        JSONObject param = new JSONObject();
        try {
            param.put("lat", Double.toString(mLatitude));
            param.put("lon", Double.toString(mLongitude));
            param.put("customer", /*1*/GD.customerModel.user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetNearCoins().execute(url, param.toString());
        mapbox.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int coinid = Integer.parseInt(marker.getSnippet());
                GD.selected_coin = coinid;
                Intent intent = new Intent(getApplicationContext(), GameSplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
                return false;
            }
        });
    }

    public class CoinItem {
        public int id;
        public String latitude;
        public String longitude;
        public String token;
    }

    public ArrayList<CoinItem> mCoinItems = new ArrayList<>();
    private SupportMapFragment map;
    private Bundle savebundle;
    private GoogleMap mapbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Mapbox.getInstance(this, "pk.eyJ1IjoicGV0ZXJwYW5tYXBib3giLCJhIjoiY2plcjJkN2QyMGFwZjJ3cGFuYnR6cDFoZSJ9.4U_mCCQ9LpUXKcQQb2NNuw");
        setContentView(R.layout.activity_coin);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Loading...")
                .build();

        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.onCreate(savedInstanceState);
        savebundle = savedInstanceState;
        map.getMapAsync(this);

        findViewById(R.id.iv_go_dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomerDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        findViewById(R.id.myLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMyLocation();
            }
        });

        findViewById(R.id.iv_cu_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WalletScreen.class);
                intent.putExtra("backIntent", "Coin");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
            }
        });

        GD.check_wallet = false;

        TextView count_wallet = findViewById(R.id.tv_count_wallet);


        int wallet_count = 0;
        for (int i = 0; i < GD.customerModel.walletModels.size(); i++) {

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
    }

    public double mLatitude;
    public double mLongitude;

    public void setMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mapbox.setMyLocationEnabled(true);
        if (mapbox.getMyLocation() != null) {
            CameraPosition select =
                    new CameraPosition.Builder().target(new LatLng(mapbox.getMyLocation().getLatitude(), mapbox.getMyLocation().getLongitude()))
                            .zoom(mZoom)
                            .bearing(0)
                            .tilt(0)
                            .build();
            mapbox.moveCamera(CameraUpdateFactory.newCameraPosition(select));
            mLatitude = mapbox.getMyLocation().getLatitude();
            mLongitude = mapbox.getMyLocation().getLongitude();
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            try{
                double latitude = location.getLatitude(); mLatitude = latitude;
                double longitude = location.getLongitude(); mLongitude = longitude;
            } catch(Exception ex){

            }

            CameraPosition select =
                    new CameraPosition.Builder().target(new LatLng(mLatitude, mLongitude))
                            .zoom(mZoom)
                            .bearing(0)
                            .tilt(0)
                            .build();
            mapbox.moveCamera(CameraUpdateFactory.newCameraPosition(select));
        }
//        map.invalidate();
    }

    public class GetNearCoins extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
            String response = CF.HttpPostRequest(url, data);
            publishProgress(response);
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            String response = progress[0];

            try {
                JSONArray arr = new JSONArray(response);
                for(int i = 0; i < arr.length(); i++){
                    JSONObject jo = arr.getJSONObject(i);
                    CoinItem item = new CoinItem();
                    item.id = jo.getInt("id");
                    item.latitude = jo.getString("latitude");
                    item.longitude = jo.getString("longitude");
                    item.token = jo.getString("token");
                    mCoinItems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            initCoinPositions();
        }
    }

    public void initCoinPositions(){
//        mapbox.clear();
        BitmapDrawable bitmapDrawable =(BitmapDrawable) ContextCompat.getDrawable(CoinActivity.this, R.mipmap.img_tippzi_coin);
        Bitmap originalBitmap = bitmapDrawable.getBitmap();

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 90, 90, false);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);

        for(int i = 0; i < mCoinItems.size(); i++) {
            MarkerOptions coin = new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(mCoinItems.get(i).latitude), Double.parseDouble(mCoinItems.get(i).longitude)))
                    .icon(icon)
                    .anchor(0.5f, 0.0f)
                    .infoWindowAnchor(0.5f, 0.0f)
//                    .title(Integer.toString(mCoinItems.get(i).id))
                    .snippet(Integer.toString(mCoinItems.get(i).id));
            Marker mv = mapbox.addMarker(coin);
//            Marker marker = mapbox.addMarker(mv);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }
}
