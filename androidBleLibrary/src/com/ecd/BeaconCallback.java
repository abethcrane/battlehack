package com.ecd;
public interface BeaconCallback {
	public void beaconFound (String major, String minor, byte[] beaconUuid);
}