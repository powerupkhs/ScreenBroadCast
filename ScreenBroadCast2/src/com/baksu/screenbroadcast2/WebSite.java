package com.baksu.screenbroadcast2;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebViewClient;

public class WebSite extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
			
		WebView webview = (WebView) findViewById(R.id.webView1);
		webview.setWebViewClient(new WebClient());
		
		WebSettings webSettings = webview.getSettings();
		webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true); // 줌 서포터 표시
        webSettings.setBuiltInZoomControls(true); // 멀티터치 줌 지원
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(PluginState.ON);
		webview.loadUrl("http://211.189.127.226:3000");
	}
}

class WebClient extends WebViewClient
{
	public boolean shouldOverrideUrlLoading(WebView view, String url)
	{
		view.loadUrl(url);
		return true;
	}
}