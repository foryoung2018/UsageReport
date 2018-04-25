
package com.htc.lib1.cs.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/**
 * Simple {@link OnCancelListener} implementation which convert the dialog
 * cancel to {@link Activity#onBackPressed()}.
 * 
 * @author samael_wang@htc.com
 */
public class SimpleProgressDialogCancelListener implements OnCancelListener {
    @Override
    public void onCancel(DialogInterface dialogInf) {
        if (dialogInf instanceof Dialog) {
            Dialog dialog = (Dialog) dialogInf;
            dialog.getOwnerActivity().onBackPressed();
        }
    }

}
