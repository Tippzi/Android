package com.application.tippzi.Activities;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.application.tippzi.R;

public class WebViewActivity extends Activity {
    private WebView webView;

    public static String EXTRA_URL = "extra_url";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        setTitle("Login");

        final String url = this.getIntent().getStringExtra(EXTRA_URL);
        if (null == url) {
            Log.e("Twitter", "URL cannot be null");
            finish();
        }

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);
    }


    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.contains(getResources().getString(R.string.twitter_callback))) {
                Uri uri = Uri.parse(url);

				/* Sending results back */
                String verifier = uri.getQueryParameter(getString(R.string.twitter_oauth_verifier));
                Intent resultIntent = new Intent();
                resultIntent.putExtra(getString(R.string.twitter_oauth_verifier), verifier);
                setResult(RESULT_OK, resultIntent);

				/* closing webview */
                finish();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedSslError (WebView view, final SslErrorHandler handler, SslError error)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
            builder.setMessage("SSL Error!");
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
