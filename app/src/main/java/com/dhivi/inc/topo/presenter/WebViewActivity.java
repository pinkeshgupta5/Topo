package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 11/5/2017.
 */

public class WebViewActivity  extends Activity {

    @BindView(R.id.webView)
    WebView webView;
    String weburl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        if(getIntent()!=null)
        {
            weburl = getIntent().getStringExtra("weburl");
        }

        setWebViewProperties();
    }

    private void setWebViewProperties() {


        WebSettings webSettings = webView.getSettings();

        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setDatabasePath("/data/data/" + getPackageName()
                + "/databases/");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setWillNotCacheDrawing(true);
        webView.setBackgroundColor(0xFFFFFFFF);
        webView.setWebViewClient(new MainWebViewClient());

        if (weburl.indexOf(".pdf") != -1) {
            webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + weburl);
        } else {
            webView.loadUrl(weburl);
        }

    }

    private class MainWebViewClient extends WebViewClient {

        public void onPageFinished(WebView webView, String url) {
        }

        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }
    }
}
