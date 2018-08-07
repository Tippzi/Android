package com.application.tippzi.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Global.CF;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarModel;
import com.application.tippzi.Models.CustomerModel;
import com.application.tippzi.Models.CustomerSignUpModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.Models.SocialMediaModel;
import com.application.tippzi.ProgressBar.ACProgressFlower;
import com.application.tippzi.R;
import com.application.tippzi.Service.GPSTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class SignUpScreen extends AbstractActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    //social
    private static final String MyPREFERENCES = "Tippzi";
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiAvailability google_api_availability;
    private static final int SIGN_IN_CODE = 0;
    private boolean is_intent_inprogress;
    private ConnectionResult connection_result;
    private int request_code;
    private String load_socialmedia_type = "" ;

    private SocialMediaModel userInformation = new SocialMediaModel();

    //Facebook sign in
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    private ACProgressFlower dialog;

    //Twitter sign in
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    public static final int WEBVIEW_REQUEST_CODE = 100;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    private GPSTracker mGPS ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mGPS = new GPSTracker(this);
        if (mGPS.canGetLocation()){
            mGPS.getLocation();
        }
        initTwitterConfigs();
        FacebookSdk.sdkInitialize(this);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Sign up...")
                .build();

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } catch (Exception e){
            e.printStackTrace();
        }
        ImageView back = findViewById(R.id.iv_back_sign_up);
        back.setOnClickListener(this);

        ImageView facebook = findViewById(R.id.btn_facebook_create_account_customer);
        facebook.setOnClickListener(this);

        ImageView twitter = findViewById(R.id.btn_twitter_create_account_customer);
        twitter.setOnClickListener(this);

        ImageView google = findViewById(R.id.btn_google_create_account_customer);
        google.setOnClickListener(this);

        TextView customer_sign_in = findViewById(R.id.btn_customer_sign_up);
        customer_sign_in.setOnClickListener(this);

        TextView business_sign_up = findViewById(R.id.btn_business_sign_up);
        business_sign_up.setOnClickListener(this);

        TextView term_condition = findViewById(R.id.tv_term_condition_sign_up);
        term_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terms_url = "http://tippzi.com/b-b-terms.php";
                Intent webIntent = new Intent(getApplicationContext(), TermsActivity.class);
                webIntent.putExtra(TermsActivity.EXTRA_URL, terms_url);
                startActivityForResult(webIntent, 101);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.iv_back_sign_up:
                intent = new Intent(getApplicationContext(), MainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
                break;

            case R.id.btn_facebook_create_account_customer:
                if(!mTermAccepted)
                    return;
                load_socialmedia_type = "facebook";
                ConnectFacebook();
                break;

            case R.id.btn_twitter_create_account_customer:
                if(!mTermAccepted)
                    return;
                load_socialmedia_type = "twitter";
                ConnectTwitter();
                break;

            case R.id.btn_google_create_account_customer:
                if(!mTermAccepted)
                    return;
                load_socialmedia_type = "google" ;
                ConnectGoogle();
                break;

            case R.id.btn_customer_sign_up:
                if(!mTermAccepted)
                    return;
                intent = new Intent(getApplicationContext(), CreateCustomerAccountScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
                break;

            case R.id.btn_business_sign_up:
                if(!mTermAccepted)
                    return;
                intent = new Intent(getApplicationContext(), BusinessAboutYouScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
                break;
        }
    }
    //social part
    private void ConnectGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope("https://www.googleapis.com/auth/user.birthday.read"))
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_CODE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        try {
            if (!result.hasResolution()) {
                google_api_availability.getErrorDialog(this, result.getErrorCode(),request_code).show();
                return;
            }
            if (!is_intent_inprogress) {
                connection_result = result;
            }
        }catch (Exception e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        // Check which request we're responding to
        if (load_socialmedia_type.equals("facebook")) {
            callbackManager.onActivityResult(requestCode, responseCode, intent);
            super.onActivityResult(requestCode, responseCode, intent);
        } else if (load_socialmedia_type.equals("twitter")) {
            if (responseCode == RESULT_OK) {
                String verifier = intent.getExtras().getString(oAuthVerifier);
                try {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    long userID = accessToken.getUserId();
                    final User user = twitter.showUser(userID);
                    String username = user.getName();
                    TwitterAuthToken twitterAuthToken = new TwitterAuthToken(accessToken.getToken(),accessToken.getTokenSecret());
                    GD.twitterSession = new TwitterSession(twitterAuthToken, userID, username);
                    saveTwitterInfo(accessToken);
                    userInformation = new SocialMediaModel();
                    userInformation.name = username;
                    userInformation.social_media = 2;
                    userInformation.email = Long.toString(userID);
                    callCustomerSignUpAPI();

                } catch (Exception e) {
                    Log.e("Twitter Login Failed", e.getMessage());
                }
            }



        }
        else if (load_socialmedia_type.equals("google")){
            if (requestCode == SIGN_IN_CODE) {
                request_code = requestCode;
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if (requestCode == SIGN_IN_CODE) {
                    if (responseCode != RESULT_OK) {
                        dialog.dismiss();
                    }
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
                    handleSignInResult(result);
                    is_intent_inprogress = false;
                }
            }
        }
        if(requestCode == 101){
            if(responseCode == RESULT_OK){
                mTermAccepted = true;
            } else {
                mTermAccepted = false;
            }
        }
    }
    boolean mTermAccepted = true;

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            userInformation = new SocialMediaModel();
            userInformation.email = acct.getEmail();
            userInformation.name = acct.getDisplayName();
            userInformation.social_media = 3;
            dialog.dismiss();

            callCustomerSignUpAPI();

            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();

        } else {
            dialog.dismiss();
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    private String accessToken;
    private String accessTokenKey;
    // twitter part
    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }
    private void ConnectTwitter(){
//        dialog = new ACProgressFlower.Builder(this)
//                .themeColor(getResources().getColor(R.color.Pink))
//                .text("Sign up...")
//                .build();
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();

        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        //builder.setOAuthAccessToken(accessToken);
        //builder.setOAuthAccessTokenSecret(accessTokenKey);

        final Configuration configuration = builder.build();
        final TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken(callbackUrl);
            final Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());

//            dialog.dismiss();

            startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
        }catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = twitter.showUser(userID);
            String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
            SharedPreferences pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            editor.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            editor.putLong("userid",userID);
            editor.putString(PREF_USER_NAME, username);
            editor.commit();
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }


    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.survivingwithandroidp.PROFILE";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Prf", "Receive info");


        }
    }


    //facebook
    private void ConnectFacebook(){
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("email", "user_birthday", "user_friends");
        loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, permissionNeeds);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                // Application code
                                try {
                                    //Get data from facebook
                                    userInformation = new SocialMediaModel();
                                    userInformation.name = object.getString("name");
                                    userInformation.email = object.getString("email");
                                    userInformation.gender = object.getString("gender");
                                    userInformation.social_media = 1;

                                    callCustomerSignUpAPI();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    private void callCustomerSignUpAPI() {
        String url = GD.BaseUrl + "customer_sign_up.php";

        CustomerSignUpModel tempModel = new CustomerSignUpModel();
        tempModel.user_name = userInformation.name;
        tempModel.email = userInformation.email;
        tempModel.social_account = userInformation.social_media;
        tempModel.lat = String.valueOf(mGPS.getLatitude());
        tempModel.lon = String.valueOf(mGPS.getLongitude());
        Gson gson = new Gson();
        String json = gson.toJson(tempModel);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.Pink))
                .text("Sign up...")
                .build();

        new CustomerSignUpWorker().execute(url, json);
    }

    public class CustomerSignUpWorker extends AsyncTask<String, String, String> {
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
                    JSONObject signupJson = new JSONObject(response);
                    String success = signupJson.getString("success");
                    if (success.equals("true")) {

                        GD.customerModel = new CustomerModel();
                        GD.customerModel.user_id = signupJson.getInt("user_id");
                        GD.customerModel.user_name = signupJson.getString("user_name");
                        GD.customerModel.email = signupJson.getString("email");
                        GD.customerModel.gender = signupJson.getString("gender");
                        GD.customerModel.birthday = signupJson.getString("birthday");
                        GD.customerModel.username = signupJson.getString("username");

                        JSONArray jsonArray = signupJson.getJSONArray("bars");
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            BarModel barModel = new BarModel();
                            barModel.bar_id = jsonObject.getInt("bar_id");
                            barModel.business_name = jsonObject.getString("business_name");
                            barModel.post_code = jsonObject.getString("post_code");
                            barModel.address = jsonObject.getString("address");
                            barModel.lat = jsonObject.getString("lat");
                            barModel.lon = jsonObject.getString("lon");
                            barModel.telephone = jsonObject.getString("telephone");
                            barModel.website = jsonObject.getString("website");
                            barModel.email = jsonObject.getString("email");
                            barModel.description = jsonObject.getString("description");
                            barModel.music_type = jsonObject.getString("music_type");
                            barModel.category = jsonObject.getString("category");

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
                                //dealModel.engagement = jsonObject3.getInt("engagement");
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
                        dialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), Tutorial1Screen.class);
                        startActivity(intent);
                        finish();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), signupJson.getString("message"), Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
        finish();
    }

}
