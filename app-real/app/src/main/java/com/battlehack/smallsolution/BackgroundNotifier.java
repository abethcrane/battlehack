package com.battlehack.smallsolution;

/**
 * Created by beth on 7/27/14.
 * Exception wrapping from srasul - http://code.nomad-labs.com/2011/12/09/mother-fk-the-scheduledexecutorservice/
 */
import android.util.Log;

import java.util.concurrent.ScheduledFuture;
        import java.util.concurrent.ScheduledThreadPoolExecutor;
        import java.util.concurrent.TimeUnit;

public class BackgroundNotifier extends ScheduledThreadPoolExecutor {

    public BackgroundNotifier(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(wrapRunnable(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(wrapRunnable(command), initialDelay, delay, unit);
    }

    private Runnable wrapRunnable(Runnable command) {
        return new LogOnExceptionRunnable(command);
    }

    private class LogOnExceptionRunnable implements Runnable {
        private Runnable theRunnable;

        public LogOnExceptionRunnable(Runnable theRunnable) {
            super();
            Log.v("logonexceptionrunnable", "create with " + theRunnable.toString());
            this.theRunnable = theRunnable;
        }

        @Override
        public void run() {
            Log.d("Background Notifier","Running Background Notifier");
            Log.d("Background Notifier", "Runnable =  " + theRunnable.toString());
            try {
                theRunnable.run();
                Log.d("Background Notifier", "after runnable.run()");
            } catch (Exception e) {
                String errorMessage = e.toString();
                String error = "error in executing: " + theRunnable + ". e: +"+ errorMessage + " it will no longer be run!";
                Log.d("Threadpool", error);
                // and re throw it so that the Executor also gets this error so that it can do what it would
                // usually do
                throw new RuntimeException(e);
            }
        }
    }

}