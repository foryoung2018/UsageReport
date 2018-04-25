
package com.htc.lib1.cs.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Simple dialog skeleton that logs major callbacks.
 */
public class SelfLogDialog extends DialogFragment {
    protected HtcLogger mLogger = new CommLoggerFactory(this).create();

    @Override
    public void onAttach(Activity activity) {
        mLogger.verbose("onAttach: activity = ", activity);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mLogger.verbose();
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLogger.verbose("onCreate: savedInstanceState = ", savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogger.verbose("onCreateDialog: arguments = ", getArguments());
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mLogger.verbose();
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mLogger.verbose();
        super.onDismiss(dialog);
    }

    @Override
    public void onStart() {
        mLogger.verbose();
        super.onStart();
    }

    @Override
    public void onStop() {
        mLogger.verbose();
        super.onStop();
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
    public void onDestroy() {
        mLogger.verbose();
        super.onDestroy();
    }

}
