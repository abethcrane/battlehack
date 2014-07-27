package com.battlehack.smallsolution;

import android.app.Activity;
import android.util.Log;

public class BeaconRunnable implements Runnable {
    private BeaconCallback bc;
    private BeaconFinder.ScanStopCallback stopCallback;
    private Activity context;

    BeaconRunnable(BeaconCallback bc, BeaconFinder.ScanStopCallback stopCallback, Activity context) {
        this.bc = bc;
        this.stopCallback = stopCallback;
        this.context = context;
    }

    @Override
    public void run() {
        Log.d("BeaconRunnable", "Running beacon search");
        byte[] toComp = {(byte) 0xd5, (byte) 0x70, (byte) 0x92, (byte) 0xac, (byte) 0xdf, (byte) 0xaa, (byte) 0x44, (byte) 0x6c, (byte) 0x8e, (byte) 0xf3, (byte) 0xc8, (byte) 0x1a, (byte) 0xa2, (byte) 0x28, (byte) 0x15, (byte) 0xb5};
        BeaconFinder bf = new BeaconFinder(bc, stopCallback, toComp, context);
        bf.startSearching();
    }

}