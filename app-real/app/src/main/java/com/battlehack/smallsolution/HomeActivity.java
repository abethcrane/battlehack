package com.battlehack.smallsolution;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends ListActivity {
    private Set<String> beaconsFound = new HashSet<String>();
    private Map<Integer, String> idMap = new HashMap<Integer, String>();
    private Map<Integer, String> nameMap = new HashMap<Integer, String>();
    private ArrayAdapter<String> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        setListAdapter(adapter);
    }

    public void onStart() {
        super.onStart();
        startScanning();
    }

    public void rescanClick() {
        startScanning();
    }

    public void startScanning() {
        beaconsFound.clear();
        idMap.clear();
        nameMap.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        byte[] toComp = {(byte)0xd5,(byte)0x70,(byte)0x92,(byte)0xac,   (byte)0xdf,(byte)0xaa,(byte)0x44,(byte)0x6c,    (byte)0x8e,(byte)0xf3,(byte)0xc8,(byte)0x1a,    (byte)0xa2,(byte)0x28,(byte)0x15,(byte)0xb5};
        BeaconFinder bf = new BeaconFinder(bc, toComp, this);
        bf.startSearching();
        Toast toast = Toast.makeText(getApplicationContext(), "Starting scan...", Toast.LENGTH_SHORT);
        toast.show();
    }


    private BeaconCallback bc = new BeaconCallback() {
        public void beaconFound(String major, String minor, byte[] beaconUuid) {
            String text = "Major: "+major + " minor: " + minor;
            Log.v("beacon callback", text);
            if (!beaconsFound.contains(text)) {
                beaconsFound.add(text);
                new HTTPHandlers().fetchVendorInfo(major, minor, new HTTPHandlers.VendorInfoCallback() {
                    @Override
                    public void infoFetchedSuccess(String major, String minor, String vendor_id, String vendor_name) {
                        Integer upTo = adapter.getCount();
                        idMap.put(upTo, vendor_id);
                        nameMap.put(upTo, vendor_name);
                        adapter.add(vendor_name);
                        adapter.notifyDataSetChanged();
                        String item_name = "The Big Issue";
                        notifyInRange(item_name, vendor_name, vendor_id);
                    }

                    public void infoFetchedFail(String major, String minor) {

                    }
                });
            }
        }
    };

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Integer pos = position;
        Intent paymentScreen = new Intent(getApplicationContext(), PaymentLoadingActivity.class);
        paymentScreen.putExtra("vendor_id", idMap.get(pos));
        paymentScreen.putExtra("vendor_name", nameMap.get(pos));
        startActivity(paymentScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
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
            case R.id.action_rescan:
                rescanClick();
                break;
            default:
                break;
        }

        return true;
    }

    protected void notifyInRange(String item_name, String vendor_name, String vendor_id) {

        Log.v("notify", "Got called!");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Buy "+item_name)
                        .setContentText(vendor_name+" is nearby")
                        .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, PaymentLoadingActivity.class);
        resultIntent.putExtra("name", vendor_name);
        resultIntent.putExtra("id", vendor_id);
    /* The stack builder object will contain an artificial back stack for the
     started Activity.
     This ensures that navigating backward from the Activity leads out of
     your application to the Home screen. */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(PaymentLoadingActivity.class); //TODO: Not sure this is correct?
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // we can pass a var instead of 0, which allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }



}
