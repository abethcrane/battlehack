package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

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

    private class HTTPVendorFind extends TextHttpResponseHandler {

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

    private class HTTPPaymentTokenGet extends TextHttpResponseHandler {
        private PaymentTokenCallback callback;

        public HTTPPaymentTokenGet(PaymentTokenCallback callback) {
            this.callback = callback;
        }

        public void begin() {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://bh.epochfail.com:5000/client/get_token/1337:1337", this);
        }

        @Override
        public void onSuccess(String clientToken) {
            String actToken = "";
            try {
                JSONObject token = new JSONObject(clientToken);
                actToken = token.getString("token");
            } catch (Exception e) {
            }
            callback.tokenFetched(actToken);
        }
    }

    private class HTTPInstantPayment extends TextHttpResponseHandler {
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
        public void onSuccess(String servResp) {
            String keyW = "";
            try {
                JSONObject token = new JSONObject(servResp);
                keyW = token.getString("keyword");
            } catch (Exception e) {
            }
            callback.paymentFinished(keyW);
        }

    }

    public interface VendorInfoCallback {
        public void infoFetched(String major, String minor, String Id, String name);
    }

    public interface PaymentTokenCallback {
        public void tokenFetched(String token);
    }

    public interface  PaymentFinishedCallback {
        public void paymentFinished(String code);
    }
}