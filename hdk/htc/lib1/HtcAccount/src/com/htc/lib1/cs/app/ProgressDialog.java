
package com.htc.lib1.cs.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnCancelListener;

/**
 * Simple dialog to spin when a slow task is working.
 */
public class ProgressDialog {

    /**
     * Construct a progress dialog with given message.
     * 
     * @param activity Activity to show progress dialog.
     * @param message Message to display.
     * @return {@link Dialog}
     */
    public static Dialog newInstance(Activity activity, String message) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog
                .setOnCancelListener(new SimpleProgressDialogCancelListener());
        progressDialog.setOwnerActivity(activity);
        return progressDialog;
    }

}
