package com.ecd;

import android.bluetooth.*;
import android.content.Context;
import android.os.Handler;


public class BeaconFinder {
	private Handler handler;
	private BeaconCallback beaconCallback;
	private BluetoothAdapter bluetoothAdapter;
	private final BluetoothManager bluetoothManager;

	public BeaconFinder(BeaconCallback beaconCallback, Context context) {
		this.context = context;
		bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		handler = new Handler();
		this.beaconCallback = beaconCallback;
	}
	public void setCallback (BeaconCallback beaconCallback) {
		this.beaconCallback = beaconCallback;	
	} 
	public boolean startSearching (byte[] beaconUuid) {

		return false;
	}
}