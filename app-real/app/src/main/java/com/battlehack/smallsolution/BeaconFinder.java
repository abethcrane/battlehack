package com.battlehack.smallsolution;

import android.bluetooth.*;
import android.content.Context;
import android.os.Handler;
import android.app.Activity;
import android.util.*;
import java.util.*;



public class BeaconFinder {
	
	private final int SCAN_PERIOD = 10000;
	private Handler handler;
	private BeaconCallback beaconCallback;
	private BluetoothAdapter bluetoothAdapter;
	private final BluetoothManager bluetoothManager;
	private Activity context;
	private byte[] beaconUuid;
    private ScanStopCallback stopCallback;
	public BeaconFinder(BeaconCallback beaconCallback,ScanStopCallback stopCallback, byte[] beaconUuid, Activity context) {
		this.beaconUuid = beaconUuid;
        this.stopCallback = stopCallback;
		this.context = context;
		bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		handler = new Handler();
		this.beaconCallback = beaconCallback;
	}
	public void setCallback (BeaconCallback beaconCallback) {
		this.beaconCallback = beaconCallback;	
	} 
	public void setBeaconUuid(byte[] beaconUuid) {
		this.beaconUuid = beaconUuid;
	}
	public boolean startSearching () {
		Log.v("blelib", "disovering tokens");
		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
			Log.v("blelib", "no bluetooth adapter");
			return false;
		}
		Log.v("blelib", "bluetooth adapter enabled");
		handler.postDelayed(bleStopScanCallback, SCAN_PERIOD);
		bluetoothAdapter.startLeScan(mLeScanCallback);
		return true;
	}
	private Runnable bleStopScanCallback = new Runnable() {
		@Override
		public void run() {
            stopScan();
        }
    };
    public void stopScan () {
        bluetoothAdapter.stopLeScan(mLeScanCallback);
        if (stopCallback != null) {
            stopCallback.onScanStop();
            stopCallback = null;
        }
	}
	private void addDevice (final BluetoothDevice device, int rssi, byte[] scanRecord) {
		byte[] uuid = Arrays.copyOfRange(scanRecord,9,25);
		byte[] major = Arrays.copyOfRange(scanRecord,25,27);
		byte[] minor = Arrays.copyOfRange(scanRecord,27,29);
		String maj = String.format("%02x",major[0]) + String.format("%02x",major[1]);
		String min = String.format("%02x",minor[0]) + String.format("%02x",minor[1]);
		Log.v("main","major: "+ maj);
		Log.v("main","minor: "+ min);
		if (Arrays.equals(beaconUuid,uuid)) {
			Log.v("blelib", "found beacon!");
			String s = "";
			for (byte b : uuid) {
				s += "0x" + String.format("%02x",b) + ",";
			}
			Log.v("blelib",s);
			beaconCallback.beaconFound(maj,min,uuid);
            addExtra();
		}

	}

    private void addExtra() {
        beaconCallback.beaconFound("0bad", "0001", beaconUuid);
        beaconCallback.beaconFound("0bad", "0002", beaconUuid);
        beaconCallback.beaconFound("0bad", "0003", beaconUuid);
        beaconCallback.beaconFound("0bad", "0004", beaconUuid);
    }

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
	    @Override
	    public void onLeScan(final BluetoothDevice device, final int rssi,final byte[] scanRecord) {
	    	Log.v("blelib", "bluetooth device found");
	        context.runOnUiThread(new Runnable() {
        	   	@Override
	           	public void run() {
        			addDevice(device,rssi,scanRecord);
	           	}
	       	});
   		}
	};

    public interface ScanStopCallback {
        public void onScanStop();
    }
}