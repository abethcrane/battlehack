package com.ecd;
public interface BeaconCallback {
	public void beaconFound (int major, int minor, byte[] beaconUuid);
}