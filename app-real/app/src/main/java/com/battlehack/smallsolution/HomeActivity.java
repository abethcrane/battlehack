package com.battlehack.smallsolution;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends ListActivity {
    private Set<String> beaconsFound = new HashSet<String>();
    private ArrayAdapter<String> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String[] values = new String[]{"Android", "iPhone", "WindowsMobile", "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    public void onStart() {
        super.onStart();
        startScanning();
    }

    public void rescanClick(View v) {
        startScanning();
    }

    public void startScanning() {
        beaconsFound = new HashSet<String>();
        byte[] toComp = {(byte)0xd5,(byte)0x70,(byte)0x92,(byte)0xac,   (byte)0xdf,(byte)0xaa,(byte)0x44,(byte)0x6c,    (byte)0x8e,(byte)0xf3,(byte)0xc8,(byte)0x1a,    (byte)0xa2,(byte)0x28,(byte)0x15,(byte)0xb5};
        BeaconFinder bf = new BeaconFinder(bc, toComp, this);
        bf.startSearching();
        Toast toast = Toast.makeText(getApplicationContext(), "Starting scan...", Toast.LENGTH_SHORT);
        toast.show();
    }


    private BeaconCallback bc = new BeaconCallback() {
        public void beaconFound(String major, String minor, byte[] beaconUuid) {
            String text = "Major: "+major + " minor: " + minor;
            if (!beaconsFound.contains(text)) {
                beaconsFound.add(text);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://bh.epochfail.com:5000/client/get_token/" + item, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(String clientToken) {
                Intent intent = new Intent(getApplicationContext(), BraintreePaymentActivity.class);
                Log.v("HEEE", clientToken);

                String actToken = "";
                try {
                    JSONObject token = new JSONObject(clientToken);
                    actToken = token.getString("token");
                } catch (Exception e) {

                }
                Log.v("Mytoken", actToken);
                intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, actToken);
                // REQUEST_CODE is arbitrary and is only used within this activity.
                Log.v("HA?", "HHHH");
                startActivityForResult(intent, 434);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 434 && resultCode == BraintreePaymentActivity.RESULT_OK) {
            String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
            Log.v("LOL", paymentMethodNonce);
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams rp = new RequestParams();
            rp.add("payment_method_nonce", paymentMethodNonce);
            client.post("http://bh.epochfail.com:5000/client/finish", rp, new HTTPHandlers().getHTTPClientFinishedHandler(this));
        } else {
            Log.v("Fail", "Failed with " + requestCode + " - " + resultCode);
        }
    }



}
