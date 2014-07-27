package com.battlehack.smallsolution;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends ListActivity {
    private Set<String> beaconsFound = new HashSet<String>();
    private Map<Integer, Vendor> vendorMap = new HashMap<Integer, Vendor>();
    private VendorAdapter adapter;
    private BeaconFinder bf = null;
    private ProgressBar scanningBar;
    public int currentNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Vendor vendorData[] = new Vendor[]{};
        adapter = new VendorAdapter(this, R.layout.list_vendor_row);

        ListView list = (ListView)findViewById(android.R.id.list);

        View header = (View)getLayoutInflater().inflate(R.layout.list_vendor_header, null);
        list.addHeaderView(header);
        scanningBar = (ProgressBar) header.findViewById(R.id.progressBar);
        list.setAdapter(adapter);
    }

    public void onStart() {
        super.onStart();
        startScanning();

        // Schedule the app to look for bluetooth tokens every minute
        //final BackgroundAsync pollBluetooth = new BackgroundAsync(new BackgroundNotifier(5), new BeaconRunnable(bc, this), 10, 60, TimeUnit.SECONDS);

        final Activity a = this;
        new Thread ( new Runnable () { public void run () {
            new BackgroundNotifier(5).scheduleAtFixedRate(new BeaconRunnable(bc, stopCallback, a), 10, 60, TimeUnit.SECONDS);
            //pollBluetooth.doInBackground();
        }}).start();
    }

    public void rescanClick() {
        startScanning();
    }

    public void startScanning() {
        beaconsFound.clear();
        vendorMap.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        byte[] toComp = {(byte)0xd5,(byte)0x70,(byte)0x92,(byte)0xac,   (byte)0xdf,(byte)0xaa,(byte)0x44,(byte)0x6c,    (byte)0x8e,(byte)0xf3,(byte)0xc8,(byte)0x1a,    (byte)0xa2,(byte)0x28,(byte)0x15,(byte)0xb5};
        if (bf != null) {
            bf.stopScan();
        }
        bf = new BeaconFinder(bc, stopCallback, toComp, this);
        bf.startSearching();
        scanningBar.setVisibility(View.VISIBLE);
        //setProgressBarIndeterminate(true);
        //setProgressBarIndeterminateVisibility(true);
        Toast toast = Toast.makeText(getApplicationContext(), "Starting scan...", Toast.LENGTH_SHORT);
        toast.show();
    }

    private BeaconFinder.ScanStopCallback stopCallback = new BeaconFinder.ScanStopCallback() {
        @Override
        public void onScanStop() {
            Log.d("Stop", "Called");
            scanningBar.setVisibility(View.GONE);
           //setProgressBarIndeterminateVisibility(false);
        }
    };

    private BeaconCallback bc = new BeaconCallback() {
        public void beaconFound(String major, String minor, byte[] beaconUuid) {
        String text = "Major: "+major + " minor: " + minor;
        Log.v("beacon callback", text);
        if (!beaconsFound.contains(text)) {
            beaconsFound.add(text);
            new HTTPHandlers().fetchVendorInfo(major, minor, new HTTPHandlers.VendorInfoCallback() {
                @Override
                public void infoFetchedSuccess(String major, String minor, Vendor v) {
                    Integer upTo = adapter.getCount();
                    vendorMap.put(upTo, v);
                    adapter.add(v);
                    adapter.notifyDataSetChanged();
                    notifyInRange(v.item, v.name, v.id);
                }

                public void infoFetchedFail(String major, String minor) {

                }
            });
        }
        }
    };

    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) return;
        Integer pos = position - 1;
        Intent paymentScreen = new Intent(getApplicationContext(), PaymentLoadingActivity.class);
        paymentScreen.putExtra("vendor", vendorMap.get(pos));
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
        resultIntent.putExtra("vendor_id", vendor_id);
        resultIntent.putExtra("vendor_name", vendor_name);
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

        // we can pass a var id which allows you to update the notification later on.
        //Clear old notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(currentNotification);

        Random r = new Random();
        currentNotification =  r.nextInt()%100;
        notificationManager.notify(currentNotification, mBuilder.build());
    }



}
