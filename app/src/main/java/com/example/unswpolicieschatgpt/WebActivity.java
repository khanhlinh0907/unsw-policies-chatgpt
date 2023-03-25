package com.example.unswpolicieschatgpt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {
    public static final String INTENT_MESSAGE = "intent_message";
    private static final String TAG = "WebActivity";
    String policy_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        /**
         * Get the intent that started the activity
         * Load PDF file according to policy title that users click on
         */
        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_MESSAGE)) {
            // Get the URL from the intent sent from SearchActivity
            policy_url = intent.getStringExtra(INTENT_MESSAGE);
            System.out.println(policy_url);

        }

        /**
         * Setup ProgressDialog while loading the document
         */
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading PDF...");
        progressDialog.setCancelable(false);

        /**
         * Setup WebView with the given URL to the document PDF file
         */
        WebView webView = findViewById(R.id.web);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.loadUrl(String.valueOf(policy_url));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });

        if (18 < Build.VERSION.SDK_INT) {
            //18 = JellyBean MR2, KITKAT=19
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
    }
}