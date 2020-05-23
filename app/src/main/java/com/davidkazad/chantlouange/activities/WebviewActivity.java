package com.davidkazad.chantlouange.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.davidkazad.chantlouange.R;

public class WebviewActivity extends BaseActivity {

    private static final String LOG_TAG = WebviewActivity.class.getName();
    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationDrawer(savedInstanceState, null);

        String webViewUrl =  "https://14ka135.wixsite.com/website/music";

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress_bar);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        //webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setPluginState(PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setSupportZoom(true);

        webView.loadUrl(webViewUrl);

        startWebView();

        mySwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mySwipeRefreshLayout.setRefreshing(true);

        mySwipeRefreshLayout.setOnRefreshListener(
                () -> {
                    Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                    mySwipeRefreshLayout.setRefreshing(true);
                    // This method performs the actual data-refresh operation.
                    // The method calls setRefreshing(false) when it's finished.
                    webView.loadUrl(webViewUrl);
                }
        );

    }

    private void startWebView() {

        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mySwipeRefreshLayout.setRefreshing(false);
                webView.loadData("Connection lost", MimeTypeMap.getFileExtensionFromUrl(""),"UTF-8");
            }
            public void onProgressChanged(WebView view, int progress)
            {

            }
            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                progressBar.setVisibility(View.VISIBLE);

            }
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                mySwipeRefreshLayout.setRefreshing(false);
            }

        });
        // we will define openFileChooser for select file from camera
        webView.setWebChromeClient(new WebChromeClient() {

        });
    }

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}