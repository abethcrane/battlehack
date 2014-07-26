package com.ecd.app;

import android.app.Activity;
import android.os.Bundle;
import com.ecd.*;
import android.webkit.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        WebView myWebView = (WebView) findViewById(R.id.webview);
		myWebView.loadUrl("file:///android_asset/index.html");
    }
}
