package com.battlehack.smallsolution;

import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by beth on 7/27/14.
 */
public class BackgroundAsync extends AsyncTask<String, Void, Void> {

    private BackgroundNotifier bn;
    private BeaconRunnable bc;
    private int delay;
    private int period;
    private TimeUnit t;

    public BackgroundAsync(BackgroundNotifier bn, BeaconRunnable bc, int delay, int period, TimeUnit t) {
        this.bn = bn;
        this.bc = bc;
        this.delay =delay;
        this.period = period;
        this.t = t;
    }

    @Override
    protected Void doInBackground(String... strings) {
        bn.scheduleAtFixedRate(bc, delay, period, t);
        return null;
    }
}
