package com.ecd.app;

import android.app.Activity;
import android.os.Bundle;
import com.ecd.*;
import android.webkit.*;
import android.widget.*;
import android.content.Context;
import java.util.*;

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

    public void onStart() {
    	super.onStart();
    	byte[] toComp = {(byte)0xd5,(byte)0x70,(byte)0x92,(byte)0xac,	(byte)0xdf,(byte)0xaa,(byte)0x44,(byte)0x6c,	(byte)0x8e,(byte)0xf3,(byte)0xc8,(byte)0x1a,	(byte)0xa2,(byte)0x28,(byte)0x15,(byte)0xb5};
    	BeaconFinder bf = new BeaconFinder(bc,toComp, this);
    	bf.startSearching();
    }


    private HashMap<String,Boolean> devices = new HashMap<String,Boolean>();
    private BeaconCallback bc = new BeaconCallback () {
    	public void beaconFound (String major, String minor, byte[] beaconUuid) {
    		if (devices.get(major+":"+minor) == null) {
    			devices.put(major+":"+minor,true );
	    		Context context = getApplicationContext();
				CharSequence text = "Found major: " + major + " minor: " + minor;
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
    	}
    };
}
