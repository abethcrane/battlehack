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

    public void fetchVendorInfo(String major, String minor, VendorInfoCallback callback) {
        new HTTPVendorFind(major, minor, callback).begin();
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
            new HTTPVendorFind("1337", "1337", new HTTPVendorRedeemFinishedHandler(transID)).begin();
        }
    }

    public class HTTPVendorFind extends TextHttpResponseHandler {

        private String major, minor;
        private VendorInfoCallback callback;

        public HTTPVendorFind(String major, String minor, VendorInfoCallback callback) {
            this.major = major;
            this.minor = minor;
            this.callback = callback;
        }

        public void begin() {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(String.format("http://bh.epochfail.com:5000/vendors/find?ids=%s:%s", major, minor), this);
        }

        @Override
        public void onSuccess(String servResp) {
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
            callback.infoFetched(major, minor, vendorID, vendorName);
        }
    }

    public class HTTPVendorRedeemFinishedHandler extends TextHttpResponseHandler implements VendorInfoCallback {

        private String transID;
        private String name;
        public HTTPVendorRedeemFinishedHandler(String transID) {
            this.transID = transID;
        }

        public void infoFetched(String major, String minor, String Id, String name) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams rp = new RequestParams();
            rp.add("transaction_id", transID);
            rp.add("vendor_id", Id);
            this.name = name;
            client.post("http://bh.epochfail.com:5000/vendors/redeem", rp, this);
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
            successScreen.putExtra("name", name);
            successScreen.putExtra("code", keyW);
            act.startActivity(successScreen);
        }
    }

    public interface VendorInfoCallback {
        public void infoFetched(String major, String minor, String Id, String name);
    }
}