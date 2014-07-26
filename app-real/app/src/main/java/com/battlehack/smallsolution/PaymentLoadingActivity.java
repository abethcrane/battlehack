package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;

public class PaymentLoadingActivity extends Activity implements HTTPHandlers.PaymentTokenCallback, HTTPHandlers.PaymentFinishedCallback {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_loading_activity);
        new HTTPHandlers().fetchPaymentToken(this);
    }

    @Override
    public void tokenFetched(String token) {
        Intent intent = new Intent(getApplicationContext(), BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, token);
        startActivityForResult(intent, 434);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 434 && resultCode == BraintreePaymentActivity.RESULT_OK) {
            String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
            new HTTPHandlers().finialisedPayment(paymentMethodNonce, getIntent().getStringExtra("vendor_id"), this);
        } else {
            Log.e("Fail", "Failed with " + requestCode + " - " + resultCode);
            finish();
        }
    }

    public void paymentFinished(String code) {
        Intent successScreen = new Intent(getApplicationContext(), Purchased.class);
        successScreen.putExtra("name", getIntent().getStringExtra("vendor_name"));
        successScreen.putExtra("code", code);
        startActivity(successScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.other, menu);
        return true;
    }

}
