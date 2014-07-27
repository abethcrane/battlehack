package com.battlehack.smallsolution;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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

    public void finalisedPayment(String nonce, String vendorId, PaymentFinishedCallback callback) {
        new HTTPInstantPayment(nonce, vendorId, callback).begin();
    }

    public void fetchCustomerID(String nonce, CustomerIDCallback callback) {
        new HTTPCustomerIDGet(nonce, callback).begin();
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
            getClient().get(String.format("http://bh.epochfail.com:5000/vendors/find?ids=%s:%s", major, minor), this);
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                JSONArray arr = response.getJSONArray("vendors");
                JSONObject vendOb = arr.getJSONObject(0);
                callback.infoFetchedSuccess(major, minor, Vendor.fromJSON(vendOb));
            } catch (Exception e) {
                callback.infoFetchedFail(major, minor);
            }
        }

        @Override
        public void onFailure(Throwable e, JSONArray errorResponse) {
            callback.infoFetchedFail(major, minor);
        }

    }

    private class HTTPCustomerIDGet extends JsonHttpResponseHandler {
        private CustomerIDCallback callback;
        private String nonce;

        public HTTPCustomerIDGet(String nonce, CustomerIDCallback callback) {
            this.nonce = nonce;
            this.callback = callback;
        }

        public void begin() {
            RequestParams rp = new RequestParams();
            rp.add("payment_method_nonce", nonce);
            getClient().post("http://bh.epochfail.com:5000/v3/client/create_customer", rp, this);
        }

        @Override
        public void onSuccess(JSONObject response) {
            String customer_id = "";
            try {
                customer_id = response.getString("customer_id");
            } catch (Exception e) {
                callback.customerIDFetchedFail();
            }
            callback.customerIDFetchedSuccess(customer_id);
        }

        @Override
        public void onFailure(Throwable e, JSONArray errorResponse) {
            callback.customerIDFetchedFail();
        }
    }

    private class HTTPPaymentTokenGet extends JsonHttpResponseHandler {
        private PaymentTokenCallback callback;

        public HTTPPaymentTokenGet(PaymentTokenCallback callback) {
            this.callback = callback;
        }

        public void begin() {
            getClient().get("http://bh.epochfail.com:5000/client/get_token/1337:1337", this);
        }

        @Override
        public void onSuccess(JSONObject response) {
            String actToken = "";
            try {
                actToken = response.getString("token");
            } catch (Exception e) {
                callback.tokenFetchedFail();
            }
            callback.tokenFetchedSuccess(actToken);
        }

        @Override
        public void onFailure(Throwable e, JSONArray errorResponse) {
            callback.tokenFetchedFail();
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
            RequestParams rp = new RequestParams();
            rp.add("payment_method_nonce", nonce);
            rp.add("vendor_id", vendorId);
            getClient().post("http://bh.epochfail.com:5000/client/instant", rp, this);
        }

        @Override
        public void onSuccess(JSONObject response) {
            String keyW = "";
            try {
                keyW = response.getString("keyword");
            } catch (Exception e) {
                callback.paymentFinishedFail();
            }
            callback.paymentFinishedSuccess(keyW);
        }

        @Override
        public void onFailure(Throwable e, JSONArray errorResponse) {
            callback.paymentFinishedFail();
        }
    }

    public interface VendorInfoCallback {
        public void infoFetchedSuccess(String major, String minor, Vendor v);
        public void infoFetchedFail(String major, String minor);
    }

    public interface PaymentTokenCallback {
        public void tokenFetchedSuccess(String token);
        public void tokenFetchedFail();
    }

    public interface CustomerIDCallback {
        public void customerIDFetchedSuccess(String token);
        public void customerIDFetchedFail();
    }

    public interface  PaymentFinishedCallback {
        public void paymentFinishedSuccess(String code);
        public void paymentFinishedFail();
    }

    private AsyncHttpClient getClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(5, 2000);
        return client;
    }
}