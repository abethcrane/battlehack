package com.battlehack.smallsolution;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


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

    public void rescanClick(View v) {
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
            if (!beaconsFound.contains(text)) {
                beaconsFound.add(text);
                new HTTPHandlers().fetchVendorInfo(major, minor, new HTTPHandlers.VendorInfoCallback() {
                    @Override
                    public void infoFetchedSuccess(String major, String minor, String Id, String name) {
                        Integer upTo = adapter.getCount();
                        idMap.put(upTo, Id);
                        nameMap.put(upTo, name);
                        adapter.add(name);
                        adapter.notifyDataSetChanged();
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





}
