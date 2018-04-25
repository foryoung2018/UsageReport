
package com.htc.lib1.cc.dialogpicker.test;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.htc.lib1.cc.dialogpicker.activityhelper.ColorPickerAut;
import com.htc.lib1.cc.test.R;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.Lock;
import com.htc.test.util.ScreenShotUtil;

public class ColorPickerTestCase extends HtcActivityTestCaseBase {

    private Resources res;
    private String va_ok;
    private String va_cancel;
    private boolean mIsPortrait;

    public ColorPickerTestCase() {
        super(ColorPickerAut.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        res = getInstrumentation().getTargetContext().getResources();
        va_ok = res.getString(R.string.va_ok);
        va_cancel = res.getString(R.string.va_cancel);
        mIsPortrait = res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void tearDown() throws Exception {
        final ColorPickerAut cpa = (ColorPickerAut) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                cpa.dismissDialog();
            }
        });
        getInstrumentation().waitForIdleSync();
        super.tearDown();
    }

    public void testDialog_onebtn() {
        int numColumns = mIsPortrait ? 3 : 5;
        int[] arrayColor = {
                0x00FF0000, 0xFFFFFFFF, 0xFF0000FF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF,
                0xFFF66F00, 0xFFFF00FF, 0x00FF0000,
                0x00FF0000, 0xFFFF00FF, 0xFF0000FF,
                0xFFFFFF00, 0xFF000300, 0xFFFF00FF
        };
        showDialog("ColorPickerDialog", numColumns, arrayColor, va_ok, null, ColorPickerAut.DIALOG);
    }

    public void testDialog_twobtn() {
        int[] twoButton = {
                0x00FF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000
        };
        showDialog("ColorPickerDialog", 4, twoButton, va_ok, va_cancel, ColorPickerAut.DIALOG);
    }

    public void testDialog_twobtn_specialarraycolor() {
        showDialog("ColorPickerDialog", 4, null, va_ok, va_cancel, ColorPickerAut.DIALOG);
    }

    public void testDialog_nobtn() {
        int numColumns = mIsPortrait ? 4 : 6;
        int[] noButton = {
                0x00FF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000
        };
        showDialog("ColorPickerDialog", numColumns, noButton, null, null, ColorPickerAut.DIALOG);
    }

    public void testDialog_nobtn_columns() {
        int numColumns = mIsPortrait ? 0 : 7;
        int[] noButton = {
                0x00FF0000, 0xFFFF00FF, 0xFF0000FF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF
        };
        showDialog("ColorPickerDialog", numColumns, noButton, null, null, ColorPickerAut.DIALOG);
    }

    public void testDialog_nobtn_specialcolumns() {
        int[] noButton = {
                0x00FF0000, 0xFFFF00FF, 0xFF0000FF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF
        };
        showDialog("ColorPickerDialog", -2, noButton, null, null, ColorPickerAut.DIALOG);
    }

    public void testDialog_specialarraycolor() {
        int[] noButton = {};
        showDialog("ColorPickerDialog", 0, noButton, null, null, ColorPickerAut.DIALOG);
    }

    public void testDialog_special() {
        showDialog("ColorPickerDialog", 7, null, null, null, ColorPickerAut.DIALOG);
    }

    private void showDialog(final String title, final int numColumns, final int[] arraycolor, final String positiveButton, final String negativeButton, final int type) {
        Intent intent = new Intent();
        intent.putExtra(ColorPickerAut.DIALOG_TITLE, title);
        intent.putExtra(ColorPickerAut.DIALOG_NUMCOLUMNS, numColumns);
        intent.putExtra(ColorPickerAut.DIALOG_ARRAYCOLOR, arraycolor);
        intent.putExtra(ColorPickerAut.DIALOG_POSITIVEBTN, positiveButton);
        intent.putExtra(ColorPickerAut.DIALOG_NEGATIVEBTN, negativeButton);
        intent.putExtra(ColorPickerAut.DIALOG_TYPE, type);
        setActivityIntent(intent);
        initActivity();
        final View view = mSolo.getView(FrameLayout.class, 0).getRootView();
        TextView vi = (TextView) view.findViewById(R.id.alertTitle);
        final Lock lock = new Lock();
        vi.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    lock.unlockAndNotify();
                }
            }
        });

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((ColorPickerAut) mActivity).changeFocusAndSelection();
            }
        });

        lock.waitUnlock(5000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, view, ScreenShotUtil.getScreenShotName(this));
    }
}
