
package com.htc.lib1.cs.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Simple fragment skeleton that logs major callbacks.
 */
public class SelfLogFragment extends Fragment {
    protected HtcLogger mLogger = new CommLoggerFactory(this).create();

    @Override
    public void onAttach(Activity activity) {
        mLogger.verbose("onAttach: activity = ", activity);
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLogger.verbose("onCreate: savedInstanceState = ", savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        mLogger.verbose();
        super.onStart();
    }

    @Override
    public void onResume() {
        mLogger.verbose();
        super.onResume();
    }

    @Override
    public void onPause() {
        mLogger.verbose();
        super.onPause();
    }

    @Override
    public void onStop() {
        mLogger.verbose();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mLogger.verbose();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mLogger.verbose();
        super.onDetach();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLogger.verbose("onConfigurationChanged: ", newConfig);
        super.onConfigurationChanged(newConfig);
    }
}
