package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;


public class HTTPHandlers {
    private Activity act;
    public HTTPClientFinishedHandler getHTTPClientFinishedHandler(Activity act) {
        this.act = act;
        return new HTTPClientFinishedHandler();
    }

    public class HTTPClientFinishedHandler extends TextHttpResponseHandler {
        @Override
        public void onSuccess(String servResp) {
            Log.v("LOL2", servResp);
            String transID = "";
            try {
                JSONObject token = new JSONObject(servResp);
                transID = token.getString("transaction_id");
            } catch (Exception e) {
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://bh.epochfail.com:5000/vendors/find?ids=1337:1337", new HTTPVendorFindFinishedHandler(transID));
        }
    }

    public class HTTPVendorFindFinishedHandler extends TextHttpResponseHandler {

        private String transID;

        public HTTPVendorFindFinishedHandler(String transID) {
            this.transID = transID;
        }

        @Override
        public void onSuccess(String servResp) {
            Log.v("LOL3", servResp);
            String vendorID = "";
            String vendorName = "";
            try {
                JSONObject token = new JSONObject(servResp);
                JSONArray arr = token.getJSONArray("vendors");
                JSONObject vendOb = arr.getJSONObject(0);
                vendorID = vendOb.getString("id");
                vendorName = vendOb.getString("vendor");
            } catch (Exception e) {
            }
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams rp = new RequestParams();
            rp.add("transaction_id", transID);
            rp.add("vendor_id", vendorID);
            client.post("http://bh.epochfail.com:5000/vendors/redeem", rp, new HTTPVendorRedeemFinishedHandler(vendorName));
        }
    }

    public class HTTPVendorRedeemFinishedHandler extends TextHttpResponseHandler {

        private String vendorName;
        public HTTPVendorRedeemFinishedHandler(String vendorName) {
            this.vendorName = vendorName;
        }
        @Override
        public void onSuccess(String servResp) {
            Log.v("LOL4", servResp);
            String keyW = "";
            try {
                JSONObject token = new JSONObject(servResp);
                keyW = token.getString("keyword");
            } catch (Exception e) {
            }

            Intent successScreen = new Intent(act.getApplicationContext(), Purchased.class);
            successScreen.putExtra("name", vendorName);
            successScreen.putExtra("code", keyW);
            act.startActivity(successScreen);
        }
    }
}