package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;

public class PaymentLoadingActivity extends Activity implements HTTPHandlers.PaymentTokenCallback, HTTPHandlers.CustomerIDCallback, HTTPHandlers.PaymentFinishedCallback {

    private Vendor v;
    private boolean active;
    private String custID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        v = (Vendor) getIntent().getSerializableExtra("vendor");
        active = true;
        setContentView(R.layout.payment_loading_activity);
        new HTTPHandlers().fetchPaymentToken(this);
        setText(String.format(getString(R.string.payment_loading), v.name));
        TextView tv = (TextView) findViewById(R.id.vendName);
        tv.setText("From " + v.name);
        tv = (TextView) findViewById(R.id.description);
        tv.setText(v.item + " - $" + v.price);
        ImageCacher.getImageAsync((ImageView) findViewById(R.id.prodDis), v.url);
        ProgressBar spinner;
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void tokenFetchedSuccess(String token) {
        if (!active) return;
        // if we have a token, call straight to evgeny
        if (getCustomerID() != null) {
            custID = getCustomerID();
            setText(String.format(getString(R.string.payment_processing), v.name));
            new HTTPHandlers().finalisedPayment(custID, v.id, this);
        } else {
            // else call braintree and then use the nonce to create a customer id
            Intent intent = new Intent(getApplicationContext(), BraintreePaymentActivity.class);
            Customization customization = new Customization.CustomizationBuilder()
                    .primaryDescription(v.item)
                    .amount("$" + v.price)
                    .submitButtonText("Buy")
                    .build();
            intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION, customization);
            intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, token);
            startActivityForResult(intent, 434);
        }
    }

    @Override
    public void tokenFetchedFail() {
        if (!active) return;
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.payment_error), Toast.LENGTH_SHORT);
        toast.show();
        Log.e("Payment", "Failed while fetching token");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 434 && resultCode == BraintreePaymentActivity.RESULT_OK) {
            String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);

            // Use the nonce to call /v3/client/create_customer
            new HTTPHandlers().fetchCustomerID(paymentMethodNonce, this);
            setText(String.format(getString(R.string.payment_processing), v.name));
            active = true;
        } else if (resultCode == BraintreePaymentActivity.RESULT_CANCELED) {
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.payment_error), Toast.LENGTH_SHORT);
            toast.show();
            Log.e("Payment", "Failed with " + requestCode + " - " + resultCode);
            finish();
        }
    }


    public String getCustomerID() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (!settings.getBoolean("paypal_save", false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("customer_id", null);
            editor.apply();
            return null;
        }
        Log.d("getcustomerID",settings.toString());
        Log.d("customer_id", settings.getString("customer_id", ""));
        return settings.getString("customer_id", null);
    }

    public void customerIDFetchedSuccess(String code) {
        if (!active) return;
        custID = code;
        Log.d("Fetched customer ID", custID);
        // Now pass the customer id through to finalised payment
        new HTTPHandlers().finalisedPayment(custID, v.id, this);
    }

    @Override
    public void customerIDFetchedFail() {
        if (!active) return;
        // TODO: Fix this. Keep retrying.
        Log.e("Customer", "Could not set ID");
        finish();
    }

    public void paymentFinishedSuccess(String code) {
        if (!active) return;

        // If users don't want us to save their paypal data then we clear it at the end of each transaction
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("paypal_save", false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("customer_id", custID);
            editor.apply();
        }

        Intent successScreen = new Intent(getApplicationContext(), FinishedFragmentActivity.class);
        successScreen.putExtra("vendor", v);
        successScreen.putExtra("code", code);
        startActivity(successScreen);
        finish();
    }

    @Override
    public void paymentFinishedFail() {
        if (!active) return;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivity(i);
                break;
            default:
                break;
        }
        return true;
    }

}
