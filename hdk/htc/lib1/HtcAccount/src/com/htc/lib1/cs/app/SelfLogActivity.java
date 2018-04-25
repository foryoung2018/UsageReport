
package com.htc.lib1.cs.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * An base activity that logs major callbacks.
 * 
 * @author samael_wang@htc.com
 */
public abstract class SelfLogActivity extends Activity {
    protected HtcLogger mLogger = new CommLoggerFactory(this).create();
    private boolean mIsStarted;
    private boolean mIsResumed;
    private boolean mIsDestroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.verboseS("onCreate: intent = ", getIntent(), ", savedInstanceState = ",
                savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        mLogger.verbose();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        mLogger.verbose();
        super.onStart();

        mIsStarted = true;
    }

    @Override
    protected void onRestart() {
        mLogger.verbose();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        mLogger.verbose();
        super.onResume();

        mIsResumed = true;
    }

    @Override
    protected void onPostResume() {
        mLogger.verbose();
        super.onPostResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLogger.verboseS(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLogger.verboseS(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        mLogger.verbose();
        super.onPause();

        mIsResumed = false;
    }

    @Override
    protected void onStop() {
        mLogger.verbose("onStop: isFinishing = ", isFinishing());
        super.onStop();

        mIsResumed = false;
        mIsStarted = false;
    }

    @Override
    protected void onDestroy() {
        mLogger.verbose();
        super.onDestroy();

        mIsDestroyed = true;
    }

    @Override
    public void finish() {
        mLogger.verbose();
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLogger.verboseS("onActivityResult: requestCode = ", requestCode, ", resultCode = ",
                resultCode, ", data = ", data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLogger.verbose("onConfigurationChanged: ", newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        mLogger.verbose();
        super.onBackPressed();
    }

    /**
     * Check if the activity is destroyed. It's an alternative to
     * {@link Activity#isDestroyed()} for earlier platforms.
     * 
     * @return {@code true} if the final {@link #onDestroy()} call has been made
     *         on the Activity, so this instance is now dead.
     */
    public boolean isDestroyedCompact() {
        return mIsDestroyed;
    }

    /**
     * Check if the activity state is between {@link #onStart()} and
     * {@link #onStop()} .
     * 
     * @return {@code true} if it is.
     */
    public boolean isStartedCompact() {
        return mIsStarted;
    }

    /**
     * Check if the activity state is between {@link #onResume()} and
     * {@link #onPause()}. It has similar function as the hidden
     * {@code isResumed()} method.
     * 
     * @return {@code true} if it is.
     */
    public boolean isResumedCompact() {
        return mIsResumed;
    }
}
