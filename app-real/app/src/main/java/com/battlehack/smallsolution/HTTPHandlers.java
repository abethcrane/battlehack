package com.battlehack.smallsolution;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;


public class HTTPHandlers {

    public void fetchVendorInfo(String major, String minor, VendorInfoCallback callback) {
        new HTTPVendorFind(major, minor, callback).begin();
    }

    public void fetchPaymentToken(PaymentTokenCallback callback) {
        new HTTPPaymentTokenGet(callback).begin();
    }

    public void finialisedPayment(String nonce, String vendorId, PaymentFinishedCallback callback) {
        new HTTPInstantPayment(nonce, vendorId, callback).begin();
    }

    private class HTTPVendorFind extends JsonHttpResponseHandler {

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
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            String vendorID = "";
            String vendorName = "";
            try {
                JSONArray arr = response.getJSONArray("vendors");
                JSONObject vendOb = arr.getJSONObject(0);
                vendorID = vendOb.getString("id");
                vendorName = vendOb.getString("vendor");
            } catch (Exception e) {
                callback.infoFetchedFail(major, minor);
            }
            callback.infoFetchedSuccess(major, minor, vendorID, vendorName);
        }
    }

    private class HTTPPaymentTokenGet extends JsonHttpResponseHandler {
        private PaymentTokenCallback callback;

        public HTTPPaymentTokenGet(PaymentTokenCallback callback) {
            this.callback = callback;
        }

        public void begin() {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://bh.epochfail.com:5000/client/get_token/1337:1337", this);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            String actToken = "";
            try {
                actToken = response.getString("token");
            } catch (Exception e) {
                callback.tokenFetchedFail();
            }
            callback.tokenFetchedSuccess(actToken);
        }
    }

    private class HTTPInstantPayment extends JsonHttpResponseHandler {
        private String nonce, vendorId;
        private PaymentFinishedCallback callback;

        public HTTPInstantPayment(String nonce, String vendorId, PaymentFinishedCallback callback) {
            this.nonce = nonce;
            this.vendorId = vendorId;
            this.callback = callback;
        }

        public void begin() {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams rp = new RequestParams();
            rp.add("payment_method_nonce", nonce);
            rp.add("vendor_id", vendorId);
            client.post("http://bh.epochfail.com:5000/client/instant", rp, this);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            String keyW = "";
            try {
                keyW = response.getString("keyword");
            } catch (Exception e) {
                callback.paymentFinishedFail();
            }
            callback.paymentFinishedSuccess(keyW);
        }



    }

    public interface VendorInfoCallback {
        public void infoFetchedSuccess(String major, String minor, String Id, String name);
        public void infoFetchedFail(String major, String minor);
    }

    public interface PaymentTokenCallback {
        public void tokenFetchedSuccess(String token);
        public void tokenFetchedFail();
    }

    public interface  PaymentFinishedCallback {
        public void paymentFinishedSuccess(String code);
        public void paymentFinishedFail();
    }
}