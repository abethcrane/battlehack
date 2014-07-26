package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

public class PaymentLoadingActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_loading_activity);
        AsyncHttpClient client = new AsyncHttpClient();
        Intent i = getIntent();
        client.get("http://bh.epochfail.com:5000/client/get_token/" + i.getStringExtra("vendor_id"), new TextHttpResponseHandler() {
            @Override
            public void onSuccess(String clientToken) {

                Log.v("HEEE", clientToken);

                String actToken = "";
                try {
                    JSONObject token = new JSONObject(clientToken);
                    actToken = token.getString("token");
                } catch (Exception e) {

                }
                Log.v("Mytoken", actToken);
                Intent intent = new Intent(getApplicationContext(), BraintreePaymentActivity.class);
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
            finish();
        }
    }
}
