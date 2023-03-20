package com.example.unswpolicieschatgpt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.unswpolicieschatgpt.database.PDFTextExtractor;
import com.example.unswpolicieschatgpt.database.Policy;
import com.example.unswpolicieschatgpt.database.PolicyDao;
import com.example.unswpolicieschatgpt.database.PolicyDatabase;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebActivity extends AppCompatActivity {
    public static final String INTENT_MESSAGE = "intent_message";
    private static final String TAG = "WebActivity";
    String policyTitle;
    URL policy_url;

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
            policyTitle = intent.getStringExtra(INTENT_MESSAGE);
            Log.d(TAG, "Intent Message = " + policyTitle);
        }

        /**
         * Setup database
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                PolicyDatabase policyDatabase = Room.databaseBuilder(WebActivity.this,
                        PolicyDatabase.class, "Policy_Database").allowMainThreadQueries().build();
                PolicyDao policyDao = policyDatabase.mainDao();
                policy_url = policyDao.getPolicyURLByTitle(policyTitle);
            }
        }).start();


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