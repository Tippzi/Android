package com.application.tippzi.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.application.tippzi.R;

public class TermsActivity extends AbstractActivity {
    private WebView webView;
    public static String EXTRA_URL = "extra_url";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        findViewById(R.id.tv_Accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        findViewById(R.id.tv_Decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        final String url = this.getIntent().getStringExtra(EXTRA_URL);
        if (null == url) {
            finish();
        }

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new TermsActivity.MyWebViewClient());
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(TermsActivity.this);
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
