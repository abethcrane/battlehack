package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;

public class PaymentLoadingActivity extends Activity implements HTTPHandlers.PaymentTokenCallback, HTTPHandlers.PaymentFinishedCallback {

    private String vendorName;
    private String vendorId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vendorName = getIntent().getStringExtra("vendor_name");
        vendorId = getIntent().getStringExtra("vendor_id");
        setContentView(R.layout.payment_loading_activity);
        new HTTPHandlers().fetchPaymentToken(this);
        setText(String.format(getString(R.string.payment_loading), vendorName));
    }

    @Override
    public void tokenFetchedSuccess(String token) {
        Intent intent = new Intent(getApplicationContext(), BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, token);
        startActivityForResult(intent, 434);
    }

    @Override
    public void tokenFetchedFail() {
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.payment_error), Toast.LENGTH_SHORT);
        toast.show();
        Log.e("Payment", "Failed while fetching token");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 434 && resultCode == BraintreePaymentActivity.RESULT_OK) {
            String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
            new HTTPHandlers().finalisedPayment(paymentMethodNonce, vendorId, this);
            setText(String.format(getString(R.string.payment_processing), vendorName));
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.payment_error), Toast.LENGTH_SHORT);
            toast.show();
            Log.e("Payment", "Failed with " + requestCode + " - " + resultCode);
            finish();
        }
    }

    public void paymentFinishedSuccess(String code) {
        Intent successScreen = new Intent(getApplicationContext(), Purchased.class);
        successScreen.putExtra("name", vendorName);
        successScreen.putExtra("code", code);
        startActivity(successScreen);
        finish();
    }

    @Override
    public void paymentFinishedFail() {
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.payment_error), Toast.LENGTH_SHORT);
        toast.show();
        Log.e("Payment", "Failed while finalising");
        finish();
    }

    private void setText(String text) {
        TextView tv = (TextView) findViewById(R.id.loading_text);
        tv.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.other, menu);
        return true;
    }

}
