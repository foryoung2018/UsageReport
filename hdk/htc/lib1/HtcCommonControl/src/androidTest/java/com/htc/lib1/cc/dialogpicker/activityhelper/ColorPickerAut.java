
package com.htc.lib1.cc.dialogpicker.activityhelper;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.ColorPickerDialog;
import com.htc.lib1.cc.widget.HtcGridView;

public class ColorPickerAut extends ActivityBase {
    public static final int DIALOG = 0;
    public static final String DIALOG_TITLE = "title";
    public static final String DIALOG_POSITIVEBTN = "positiveButton";
    public static final String DIALOG_NEGATIVEBTN = "negativeButton";
    public static final String DIALOG_NUMCOLUMNS = "numColumns";
    public static final String DIALOG_ARRAYCOLOR = "arrayColor";
    public static final String DIALOG_TYPE = "type";

    Dialog mDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra(DIALOG_TITLE);
        String positiveButton = getIntent().getStringExtra(DIALOG_POSITIVEBTN);
        String negativeButton = getIntent().getStringExtra(DIALOG_NEGATIVEBTN);
        int numColumns = getIntent().getIntExtra(DIALOG_NUMCOLUMNS, 3);
        int[] arraycolor = getIntent().getIntArrayExtra(DIALOG_ARRAYCOLOR);
        int type = getIntent().getIntExtra(DIALOG_TYPE, DIALOG);
        switch (type) {
            case DIALOG:
                showDialog(title, positiveButton, numColumns, arraycolor, negativeButton);
                break;
            default:
                break;
        }
    }

    private void showDialog(final String title, final String positiveButton, final int numColumns, final int[] arraycolor, final String negativeButton) {
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this);
        if (null != title) {
            builder.setTitle(title);
        }
        builder.setNumColumns(numColumns);
        builder.setColorArray(arraycolor);
        if (null != positiveButton) {
            builder.setPositiveButton(positiveButton, null);
        }
        if (null != negativeButton) {
            builder.setNegativeButton(negativeButton, null);
        }
        mDialog = builder.show();
        hideScrollView(mDialog);
    }

    public void dismissDialog() {
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }

    private void hideScrollView(Dialog dialog) {
        if (null == dialog) return;

        HtcGridView gv = (HtcGridView) dialog.findViewById(android.R.id.list);
        if (null != gv) {
            gv.setVerticalScrollBarEnabled(false);
        }
    }

    public void changeFocusAndSelection() {
        final TextView alerttitle = (TextView) mDialog.findViewById(R.id.alertTitle);
        if (null != alerttitle) {
            alerttitle.setFocusable(true);
            alerttitle.requestFocus();
        }

        final HtcGridView gv = (HtcGridView) mDialog.findViewById(android.R.id.list);
        if (null != gv) {
            gv.setSelection(-1);
        }
    }
}
