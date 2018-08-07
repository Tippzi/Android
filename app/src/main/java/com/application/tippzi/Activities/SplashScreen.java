package com.application.tippzi.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.BusinessModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.WalletModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.application.tippzi.Service.AlaramReceiver;
import com.application.tippzi.Service.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

public class SplashScreen extends AbstractActivity {

    private static final String MyPREFERENCES = "Tippzi";
    private static final String USERID = "user_id";
    private static final String USERTYPE = "user_type";

    private ACProgressFlower dialog;
    private SharedPreferences pref;

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    private final int REQUEST_LOCATION = 1 ;
    LocationManager locationManager;
    private GPSTracker mGPS ;
    private GoogleApiClient googleApiClient ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Add code to print out the key hash
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.application.tippzi",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        //Log.e("LLL", Integer.toString(android.os.Build.VERSION.SDK_INT));

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Loading...")
                .build();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Boolean check_gps = checkLocation();

        if (check_gps == true) {

            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsToRequest = findUnAskedPermissions(permissions);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            REQUEST_LOCATION);
                } else {
                    mGPS = new GPSTracker(this);
                    if (mGPS.canGetLocation()){
                        mGPS.getLocation();
                    }
                }
            } else {
                mGPS = new GPSTracker(this);
                if (mGPS.canGetLocation()){
                    mGPS.getLocation();
                }
            }

            pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

            if (pref.getString(USERTYPE, "").equals("Business")) {
                if (pref.getString(USERID, "").equals("")) {
                    Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    callCheckRememberBusinessAPI();
                }
            } else if (pref.getString(USERTYPE, "").equals("Customer")) {
                if (pref.getString(USERID, "").equals("")) {
                    Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    callCheckRememberCustomerAPI();
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {

                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            enableLoc();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(SplashScreen.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        finish();
                    }
                    case -1: {
                        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                        startActivity(intent);
                        finish();
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

    }

    private void callCheckRememberCustomerAPI() {
        String url = GD.BaseUrl + "check_remember_customer.php";
        String lat = String.valueOf(mGPS.getLatitude());
        String lon = String.valueOf(mGPS.getLongitude());
        String request = "{\"user_id\":\""+pref.getString(USERID, "")+"\", \"lat\":\"" + lat +"\", \"lon\":\"" + lon+ "\"}";

        new CustomerRememberWorker().execute(url, request);
    }

    public class CustomerRememberWorker extends AsyncTask<String, String, String> {
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

                        GD.customerModel = new CustomerModel();
                        GD.customerModel.user_id = loginJson.getInt("user_id");
                        GD.customerModel.user_name = loginJson.getString("user_name");
                        GD.customerModel.email = loginJson.getString("email");
                        GD.customerModel.gender = loginJson.getString("gender");
                        GD.customerModel.birthday = loginJson.getString("birthday");
                        GD.customerModel.username = loginJson.getString("username");

                        JSONArray jsonArray = loginJson.getJSONArray("bars");
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BarModel barModel = new BarModel();
                            barModel.bar_id = jsonObject.getInt("bar_id");
                            barModel.business_name = jsonObject.getString("business_name");
                            barModel.service_name = jsonObject.getString("service_name");
                            barModel.category = jsonObject.getString("category");
                            barModel.post_code = jsonObject.getString("post_code");
                            barModel.address = jsonObject.getString("address");
                            barModel.lat = jsonObject.getString("lat");
                            if(barModel.lat.isEmpty())
                                barModel.lat = "0";
                            barModel.lon = jsonObject.getString("lon");
                            if(barModel.lon.isEmpty())
                                barModel.lon = "0";
                            barModel.distance = jsonObject.getDouble("distance");
                            barModel.telephone = jsonObject.getString("telephone");
                            barModel.website = jsonObject.getString("website");
                            barModel.email = jsonObject.getString("email");
                            barModel.description = jsonObject.getString("description");
                            barModel.music_type = jsonObject.getString("music_type");

                            JSONObject jsonObject1 = jsonObject.getJSONObject("open_time");
                            barModel.open_time.mon_start = jsonObject1.getString("mon_start");
                            barModel.open_time.mon_end = jsonObject1.getString("mon_end");
                            barModel.open_time.tue_start = jsonObject1.getString("tue_start");
                            barModel.open_time.tue_end = jsonObject1.getString("tue_end");
                            barModel.open_time.wed_start = jsonObject1.getString("wed_start");
                            barModel.open_time.wed_end = jsonObject1.getString("wed_end");
                            barModel.open_time.thur_start = jsonObject1.getString("thur_start");
                            barModel.open_time.thur_end = jsonObject1.getString("thur_end");
                            barModel.open_time.fri_start = jsonObject1.getString("fri_start");
                            barModel.open_time.fri_end = jsonObject1.getString("fri_end");
                            barModel.open_time.sat_start = jsonObject1.getString("sat_start");
                            barModel.open_time.sat_end = jsonObject1.getString("sat_end");
                            barModel.open_time.sun_start = jsonObject1.getString("sun_start");
                            barModel.open_time.sun_end = jsonObject1.getString("sun_end");

                            JSONObject jsonObject2 = jsonObject.getJSONObject("gallery");
                            barModel.galleryModel.background1 = jsonObject2.getString("background1");
                            barModel.galleryModel.background2 = jsonObject2.getString("background2");
                            barModel.galleryModel.background3 = jsonObject2.getString("background3");
                            barModel.galleryModel.background4 = jsonObject2.getString("background4");
                            barModel.galleryModel.background5 = jsonObject2.getString("background5");
                            barModel.galleryModel.background6 = jsonObject2.getString("background6");

                            JSONArray jsonArray1 = jsonObject.getJSONArray("deals");
                            for (int j = 0; j < jsonArray1.length(); j ++) {
                                JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                DealModel dealModel = new DealModel();
                                dealModel.deal_id = jsonObject3.getInt("deal_id");
                                dealModel.deal_title = jsonObject3.getString("title");
                                dealModel.deal_description = jsonObject3.getString("description");
                                dealModel.deal_duration =jsonObject3.getString("duration");
                                dealModel.deal_qty = jsonObject3.getInt("qty");
                                dealModel.impressions = jsonObject3.getInt("impressions");
                                dealModel.in_wallet = jsonObject3.getInt("in_wallet");
                                dealModel.claimed = jsonObject3.getInt("claimed");
                                dealModel.wallet_check = jsonObject3.getBoolean("wallet_check");
                                dealModel.claim_check = jsonObject3.getBoolean("claimed_check");
                                dealModel.impressions_check = jsonObject3.getBoolean("impressions_check");
                                dealModel.deal_days.monday = jsonObject3.getJSONObject("deal_days").getBoolean("monday");
                                dealModel.deal_days.tuesday = jsonObject3.getJSONObject("deal_days").getBoolean("tuesday");
                                dealModel.deal_days.wednesday = jsonObject3.getJSONObject("deal_days").getBoolean("wednesday");
                                dealModel.deal_days.thursday = jsonObject3.getJSONObject("deal_days").getBoolean("thursday");
                                dealModel.deal_days.friday = jsonObject3.getJSONObject("deal_days").getBoolean("friday");
                                dealModel.deal_days.saturday = jsonObject3.getJSONObject("deal_days").getBoolean("saturday");
                                dealModel.deal_days.sunday = jsonObject3.getJSONObject("deal_days").getBoolean("sunday");

                                barModel.deals.add(dealModel);
                            }

                            GD.customerModel.bars.add(barModel);
                        }

                        JSONArray jsonArray1 = loginJson.getJSONArray("wallets");

                        for (int i = 0; i < jsonArray1.length(); i ++ ) {
                            JSONObject jsonObject = jsonArray1.getJSONObject(i);

                            WalletModel walletModel = new WalletModel();
                            walletModel.bar_id = jsonObject.getInt("bar_id");
                            walletModel.bar_name = jsonObject.getString("business_name");
                            walletModel.bar_address = jsonObject.getString("address");
                            walletModel.lat = jsonObject.getString("lat");
                            walletModel.lon = jsonObject.getString("lon");
                            walletModel.distance = jsonObject.getDouble("distance");
                            walletModel.music_type = jsonObject.getString("music_type");

                            walletModel.deal_id = jsonObject.getInt("deal_id");
                            walletModel.deal_title = jsonObject.getString("title");
                            walletModel.deal_duration = jsonObject.getString("duration");
                            walletModel.deal_description = jsonObject.getString("description");
                            walletModel.deal_qty = jsonObject.getInt("qty");
                            walletModel.in_wallet = jsonObject.getInt("in_wallet");
                            walletModel.impressions = jsonObject.getInt("impressions");
                            walletModel.claimed = jsonObject.getInt("claimed");
                            walletModel.wallet_check = jsonObject.getBoolean("wallet_check");
                            walletModel.claim_check = jsonObject.getBoolean("claimed_check");
                            walletModel.impressions_check = jsonObject.getBoolean("impressions_check");

                            JSONObject jsonObject1 = jsonObject.getJSONObject("open_time");
                            walletModel.open_time.mon_start = jsonObject1.getString("mon_start");
                            walletModel.open_time.mon_end = jsonObject1.getString("mon_end");
                            walletModel.open_time.tue_start = jsonObject1.getString("tue_start");
                            walletModel.open_time.tue_end = jsonObject1.getString("tue_end");
                            walletModel.open_time.wed_start = jsonObject1.getString("wed_start");
                            walletModel.open_time.wed_end = jsonObject1.getString("wed_end");
                            walletModel.open_time.thur_start = jsonObject1.getString("thur_start");
                            walletModel.open_time.thur_end = jsonObject1.getString("thur_end");
                            walletModel.open_time.fri_start = jsonObject1.getString("fri_start");
                            walletModel.open_time.fri_end = jsonObject1.getString("fri_end");
                            walletModel.open_time.sat_start = jsonObject1.getString("sat_start");
                            walletModel.open_time.sat_end = jsonObject1.getString("sat_end");
                            walletModel.open_time.sun_start = jsonObject1.getString("sun_start");
                            walletModel.open_time.sun_end = jsonObject1.getString("sun_end");

                            GD.customerModel.walletModels.add(walletModel);
                        }

                        dialog.dismiss();

                        SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(USERID, loginJson.getString("user_id"));
                        editor.putString(USERTYPE, "Customer");
                        editor.commit();

//                        Intent intent = new Intent(getApplicationContext(), CategoryScreen.class);
                        Intent intent = new Intent(getApplicationContext(), CustomerDashboardScreen.class);
                        startActivity(intent);
                        finish();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), loginJson.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {

                }
            } else {
                dialog.dismiss();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private void callCheckRememberBusinessAPI() {
        String url = GD.BaseUrl + "check_remember_business.php";

        String request = "{\"user_id\":\""+pref.getString(USERID, "")+"\"}";

        new BusinessRememberWorker().execute(url, request);
    }

    public class BusinessRememberWorker extends AsyncTask<String, String, String> {
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

                        GD.businessModel = new BusinessModel();
                        GD.businessModel.user_id = loginJson.getInt("user_id");
                        GD.businessModel.username = loginJson.getString("username");
                        GD.businessModel.email = loginJson.getString("email");
                        GD.businessModel.telephone = loginJson.getString("telephone");
                        JSONArray jsonArray = loginJson.getJSONArray("bars");
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BarModel barModel = new BarModel();
                            barModel.bar_id = jsonObject.getInt("bar_id");
                            barModel.business_name = jsonObject.getString("business_name");
                            barModel.category = jsonObject.getString("category");
                            barModel.post_code = jsonObject.getString("post_code");
                            barModel.address = jsonObject.getString("address");
                            barModel.lat = jsonObject.getString("lat");
                            barModel.lon = jsonObject.getString("lon");
                            barModel.telephone = jsonObject.getString("telephone");
                            barModel.website = jsonObject.getString("website");
                            barModel.email = jsonObject.getString("email");
                            barModel.description = jsonObject.getString("description");
                            barModel.music_type = jsonObject.getString("music_type");

                            JSONObject jsonObject1 = jsonObject.getJSONObject("open_time");
                            barModel.open_time.mon_start = jsonObject1.getString("mon_start");
                            barModel.open_time.mon_end = jsonObject1.getString("mon_end");
                            barModel.open_time.tue_start = jsonObject1.getString("tue_start");
                            barModel.open_time.tue_end = jsonObject1.getString("tue_end");
                            barModel.open_time.wed_start = jsonObject1.getString("wed_start");
                            barModel.open_time.wed_end = jsonObject1.getString("wed_end");
                            barModel.open_time.thur_start = jsonObject1.getString("thur_start");
                            barModel.open_time.thur_end = jsonObject1.getString("thur_end");
                            barModel.open_time.fri_start = jsonObject1.getString("fri_start");
                            barModel.open_time.fri_end = jsonObject1.getString("fri_end");
                            barModel.open_time.sat_start = jsonObject1.getString("sat_start");
                            barModel.open_time.sat_end = jsonObject1.getString("sat_end");
                            barModel.open_time.sun_start = jsonObject1.getString("sun_start");
                            barModel.open_time.sun_end = jsonObject1.getString("sun_end");

                            JSONObject jsonObject2 = jsonObject.getJSONObject("gallery");
                            barModel.galleryModel.background1 = jsonObject2.getString("background1");
                            barModel.galleryModel.background2 = jsonObject2.getString("background2");
                            barModel.galleryModel.background3 = jsonObject2.getString("background3");
                            barModel.galleryModel.background4 = jsonObject2.getString("background4");
                            barModel.galleryModel.background5 = jsonObject2.getString("background5");
                            barModel.galleryModel.background6 = jsonObject2.getString("background6");

                            JSONArray jsonArray1 = jsonObject.getJSONArray("deals");
                            for (int j = 0; j < jsonArray1.length(); j ++) {
                                JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                DealModel dealModel = new DealModel();
                                dealModel.deal_id = jsonObject3.getInt("deal_id");
                                dealModel.deal_title = jsonObject3.getString("title");
                                dealModel.deal_description = jsonObject3.getString("description");
                                dealModel.deal_duration =jsonObject3.getString("duration");
                                dealModel.deal_qty = jsonObject3.getInt("qty");
                                dealModel.impressions = jsonObject3.getInt("impressions");
                                dealModel.in_wallet = jsonObject3.getInt("in_wallet");
                                dealModel.claimed = jsonObject3.getInt("claimed");
                                dealModel.engagement = jsonObject3.getInt("engagement");
                                dealModel.clicks = jsonObject3.getInt("clicks");
                                dealModel.deal_days.monday = jsonObject3.getJSONObject("deal_days").getBoolean("monday");
                                dealModel.deal_days.tuesday = jsonObject3.getJSONObject("deal_days").getBoolean("tuesday");
                                dealModel.deal_days.wednesday = jsonObject3.getJSONObject("deal_days").getBoolean("wednesday");
                                dealModel.deal_days.thursday = jsonObject3.getJSONObject("deal_days").getBoolean("thursday");
                                dealModel.deal_days.friday = jsonObject3.getJSONObject("deal_days").getBoolean("friday");
                                dealModel.deal_days.saturday = jsonObject3.getJSONObject("deal_days").getBoolean("saturday");
                                dealModel.deal_days.sunday = jsonObject3.getJSONObject("deal_days").getBoolean("sunday");

                                barModel.deals.add(dealModel);
                            }

                            GD.businessModel.bars.add(barModel);
                        }

                        dialog.dismiss();

                        SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(USERID, loginJson.getString("user_id"));
                        editor.putString(USERTYPE, "Business");
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                        finish();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), loginJson.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {

                }
            } else {

            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }
    protected void onPause()
    {
        super.onPause();
        System.gc();
    }

}
