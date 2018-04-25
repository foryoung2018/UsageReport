
package com.htc.lib1.cc.alertdialog.activityhelper;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.htc.aut.ActivityBase;

public class AlertDialogAut extends ActivityBase {
    private static final int DIALOG = 0;
    private static final int MESSAGE_DIALOG = 1;
    private static final int LIST_DIALOG = 2;
    private static final int PROGRESS_DIALOG = 3;
    private static final int HORIZATOR_DIALOG = 4;

    Dialog mDialog = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");
        String positiveButton = getIntent().getStringExtra("positiveButton");
        String neutralButton = getIntent().getStringExtra("neutralButton");
        String negativeButton = getIntent().getStringExtra("negativeButton");
        int singleItems = getIntent().getIntExtra("singleItems", 0);
        int multiItems = getIntent().getIntExtra("multiItems", 0);
        int items = getIntent().getIntExtra("items", 0);
        int type = getIntent().getIntExtra("type", DIALOG);
        switch (type) {
        case DIALOG:
            showDialog(title, message, positiveButton, neutralButton, negativeButton);
            break;
        case MESSAGE_DIALOG:
            showMessageDialog(title, singleItems, multiItems, positiveButton, neutralButton);
            break;
        case PROGRESS_DIALOG:
            showProgressDialog(title, message);
            break;
        case HORIZATOR_DIALOG:
            showHorizontalProgressDialog(title, message, positiveButton, negativeButton);
            break;
        case LIST_DIALOG:
            showListDialog(title, items);
            break;
        default:
            break;
        }
    }

    private void showMessageDialog(final String title, final int singleItems, final int multiItems, final String positiveButton, final String neutralButton) {
        Builder builder = new Builder(this);
        builder.setTitle(title);
        if (0 != singleItems) {
            builder.setSingleChoiceItems(singleItems, 0, null);
        }
        if (0 != multiItems) {
            builder.setMultiChoiceItems(
                    multiItems,
                    new boolean[] {
                            false, true, false, true, false,
                            false, false
                    }, null);
        }
        if (null != positiveButton) {
            builder.setPositiveButton(positiveButton, null);
        }
        if (null != neutralButton) {
            builder.setNeutralButton(neutralButton, null);
        }
        mDialog = builder.show();
        hideScrollView(mDialog);
    }

    private void showDialog(final String title, final String message, final String positiveButton, final String neutralButton, final String negativeButton) {
        Builder builder = new Builder(this);
        if (null != title) {
            builder.setTitle(title);
        }
        if (null != message) {
            builder.setMessage(message);
        }
        if (null != positiveButton) {
            builder.setPositiveButton(positiveButton, null);
        }
        if (null != neutralButton) {
            builder.setNeutralButton(neutralButton, null);
        }
        if (null != negativeButton) {
            builder.setNegativeButton(negativeButton, null);
        }
        mDialog = builder.show();
        hideScrollView(mDialog);
    }

    private void showHorizontalProgressDialog(final String title, final String message, final String positiveButton, final String negativeButton) {
        final ProgressDialog horizontalProgress = new ProgressDialog(this);
        mDialog = horizontalProgress;
        if (null != title) {
            horizontalProgress.setTitle(title);
        }
        if (null != message) {
            horizontalProgress.setMessage(message);

        }
        horizontalProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        horizontalProgress.setMax(100);
        horizontalProgress.setProgress(30);
        horizontalProgress.setCancelable(false);
        horizontalProgress.setButton(DialogInterface.BUTTON_POSITIVE,
                positiveButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                    }
                });
        horizontalProgress.setButton(DialogInterface.BUTTON_NEGATIVE,
                negativeButton,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                        horizontalProgress.setProgress(0);
                    }
                });
        horizontalProgress.incrementProgressBy(23);
        horizontalProgress.show();
        hideScrollView(horizontalProgress);
    }

    private void showProgressDialog(final String title, final String message) {
        final MyProgressDialog mSpinnerDialog = new MyProgressDialog(this);
        mDialog = mSpinnerDialog;

        if (null != title) {
            mSpinnerDialog.setTitle(title);
        }
        if (null != message) {
            mSpinnerDialog
                    .setMessage(message);
        }
        mSpinnerDialog.setIndeterminate(true);
        mSpinnerDialog.setCancelable(true);
        mSpinnerDialog.setProgressBarVisibility(View.INVISIBLE);
        mSpinnerDialog.show();
    }

    private void showListDialog(final String title, final int items) {
        Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setItems(items, null);
        mDialog = builder.show();
        hideScrollView(mDialog);
    }

    public void dismissDialog() {
        if (null != mDialog)
            mDialog.dismiss();
    }

    private class MyProgressDialog extends ProgressDialog {
        private int visibility = View.VISIBLE;
        public MyProgressDialog(Context context) {
            super(context);
        }

        @Override
        public void setView(View view) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
            if (progressBar != null) {
                progressBar.setVisibility(visibility);
            }
            super.setView(view);
        }

        public void setProgressBarVisibility(int v) {
            visibility = v;
        }
    }

    private void hideScrollView(Dialog dialog)
    {
        if (null == dialog)
            return;

        int id = getResources().getIdentifier("scrollView", "id", "android");
        ScrollView sv = (ScrollView) dialog.findViewById(id);
        if (null != sv) {
            sv.setVerticalScrollBarEnabled(false);
        }

        int listId = getResources().getIdentifier("select_dialog_listview", "id", "android");
        View v = (View) dialog.findViewById(listId);
        if (null != v) {
            v.setVerticalScrollBarEnabled(false);
        }
    }
}
