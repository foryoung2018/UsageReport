package com.htc.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.test.PerformanceTestCase;
import android.util.Log;

/**
 * Base class for all launch performance Instrumentation classes.
 */
public class PerformanceBase extends Instrumentation implements
        PerformanceTestCase {
    public static final String TAG = "PerformanceBase";
    protected Bundle mResults;
    protected Intent mIntent;

    /**
     * Constructor.
     */
    public PerformanceBase() {
        mResults = new Bundle();
        mIntent = new Intent(Intent.ACTION_MAIN);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        setAutomaticPerformanceSnapshots();
    }

    /**
     * Launches intent {@link #mIntent}, and waits for idle before returning.
     */
    protected void LaunchApp() {
        startActivitySync(mIntent);
        waitForIdleSync();
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        Log.v(TAG,
                "==============================================================");
        Log.v(TAG, "Test reults = " + results);
        super.finish(resultCode, results);
        Log.v(TAG,
                "=========================*****=====================================");
        Log.v(TAG, "Test reults = " + results);
    }

    @Override
    public boolean isPerformanceOnly() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int startPerformance(Intermediates intermediates) {
        // TODO Auto-generated method stub
        intermediates.setInternalIterations(100);
        return 100;
    }
}
