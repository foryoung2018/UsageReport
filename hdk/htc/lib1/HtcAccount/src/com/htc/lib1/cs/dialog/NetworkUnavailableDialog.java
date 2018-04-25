
package com.htc.lib1.cs.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.htc.lib1.cs.app.SelfLogDialog;
import com.htc.lib1.cs.auth.R;

public class NetworkUnavailableDialog extends SelfLogDialog {
    public static final int BUTTON_LAUNCH_SETTINGS = Dialog.BUTTON_POSITIVE;
    public static final int BUTTON_CANCEL = Dialog.BUTTON_NEGATIVE;

    /**
     * Listener to listens on network unavailable dialog button click events.
     * 
     * @author samael_wang@htc.com
     */
    public interface OnClickListener {

        /**
         * Invoked when the dialog button is clicked.
         * 
         * @param dialog {@link NetworkUnavailableDialog} instance.
         * @param which Either {@link #BUTTON_LAUNCH_SETTINGS} or
         *        {@link #BUTTON_CANCEL}.
         */
        public void onClick(NetworkUnavailableDialog dialog, int which);
    }

    private OnClickListener mClickListener;

    /**
     * Create a new instance with configurable behavior setting.
     * 
     * @return Dialog instance.
     */
    public static NetworkUnavailableDialog newInstance() {
        NetworkUnavailableDialog dialog = new NetworkUnavailableDialog();
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_network_unavailable)
                .setMessage(R.string.dialog_msg_network_unavailable)
                .setPositiveButton(R.string.nn_settings,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final Intent i = new Intent(
                                        android.provider.Settings.ACTION_SETTINGS);
                                dialog.dismiss();
                                getActivity().startActivity(i);

                                // Notify listener.
                                synchronized (NetworkUnavailableDialog.this) {
                                    if (mClickListener != null)
                                        mClickListener.onClick(NetworkUnavailableDialog.this,
                                                whichButton);
                                }
                            }
                        })
                .setNegativeButton(R.string.va_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                                // Notify listener.
                                synchronized (NetworkUnavailableDialog.this) {
                                    if (mClickListener != null)
                                        mClickListener.onClick(NetworkUnavailableDialog.this,
                                                whichButton);
                                }
                            }
                        })
                .create();

        return dialog;
    }

    public synchronized void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // After detach, the listener could be invalid.
        synchronized (this) {
            mClickListener = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
