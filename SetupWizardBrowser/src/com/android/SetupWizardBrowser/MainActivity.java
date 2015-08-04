package com.android.SetupWizardBrowser;

import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;






import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends Activity {
	private EditText url;
	private WebView webview;
	private SharedPreferences sp = null;
    private SharedPreferences.Editor editor = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		url=(EditText) this.findViewById(R.id.url1);
		webview=(WebView) this.findViewById(R.id.webView1);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportMultipleWindows(true);
		webSettings.setUseWideViewPort(true); 
	    webSettings.setLoadWithOverviewMode(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.setWebChromeClient(new mWebChromeClient());
		webview.setWebViewClient(new mWebViewClient());
        webview.addJavascriptInterface(this, "demo");
        
        sp = getSharedPreferences("setupwizard", MODE_PRIVATE);
        editor = sp.edit();
        String urlstr=url.getText().toString();
		System.out.println(urlstr);
		webview.loadUrl(urlstr);
		
	}
	
	
	
	//为后退键添加事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==event.KEYCODE_BACK){
			webview.goBack();
			return true;
		}
		return false;
	}
	
	@JavascriptInterface
    public void clickOnAndroid(){
          Log.d("LFK","jsFunction");
          finish();
    }
	
	public class mWebChromeClient extends WebChromeClient{

		@Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //Log.d(LOG_TAG, message);
            result.confirm();
            return true;
        }
	}
	
	public class mWebViewClient extends WebViewClient{

		/*@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			pb_webview.setVisibility(View.VISIBLE);
			view.loadUrl(url);
			return true;
		}	*/
		@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            URL local_url;
            URLConnection connection;
            try {
                local_url = new URL(url);
                connection = local_url.openConnection();
                connection.setConnectTimeout(15000);
                connection.connect();
            } catch (Exception e) {
            }

            final HttpGet httpGet = new HttpGet(url);
            Thread theard = new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        HttpResponse response;
                        String htmlContent;
                        HttpClient httpClient = new DefaultHttpClient();
                        response = httpClient.execute(httpGet);
                        if (response.getStatusLine().getStatusCode() == 200) {
                            Log.d("lfk", "success =="
                                    + response.getStatusLine().getStatusCode());
                            editor.putBoolean("isFinished", true);
                            editor.commit();
                        } else {
                            Log.d("lfk", "failed=="
                                    + response.getStatusLine().getStatusCode());
                            editor.putBoolean("isFinished", false);
                            editor.commit();
                        }
                    } catch (Exception e) {
                    }
                    ;
                }
            });
            theard.start();
            // return true;
            return super.shouldOverrideUrlLoading(view, url);
        }
	}
	
	
	
}
