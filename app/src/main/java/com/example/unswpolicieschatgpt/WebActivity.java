package com.example.unswpolicieschatgpt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.net.URL;
import java.util.Objects;

public class WebActivity extends AppCompatActivity {
    public static final String INTENT_MESSAGE = "intent_message";
    private static final String TAG = "WebActivity";

    String policy_url;

    Button backToSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        backToSearch = findViewById(R.id.backToSearchButton);
        backToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        /*
        Change colour of top action bar
         */
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.unsw_yellow)));

        // Change text colour of top action bar
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        /**
         * Get the intent that started the activity
         * Load PDF file according to policy title that users click on
         */
        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_MESSAGE)) {
            // Get the URL from the intent sent from SearchActivity
            policy_url = intent.getStringExtra(INTENT_MESSAGE);
            System.out.println("WebActivity: onCreate: url = " + policy_url);

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

        //docs.google.com added to start of URL. Based off of previous iteration of Open PDF.
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + policy_url);

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