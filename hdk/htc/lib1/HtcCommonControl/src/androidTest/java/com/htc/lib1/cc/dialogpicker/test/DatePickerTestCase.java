
package com.htc.lib1.cc.dialogpicker.test;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.htc.lib1.cc.dialogpicker.activityhelper.DatePickerDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class DatePickerTestCase extends HtcActivityTestCaseBase {
    private final int SHOW_NO_DIALOG = 0;
    private final int SHOW_DIALOG = 1;
    private final int SHOW_DIALOG_CREDIT_CARD = 2;

    public DatePickerTestCase() {
        super(DatePickerDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {
        final DatePickerDemo instance = (DatePickerDemo) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                instance.dismissWindow();
            }
        });
        super.tearDown();
    }

    private void showDialog(int id, int year, int month, int day) {
        Intent intent = new Intent();
        intent.putExtra("dialogId", id);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        setActivityIntent(intent);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
    }

    public final void testOnCreateBundle() {
        showDialog(SHOW_NO_DIALOG, 2014, 8, 20);
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v,
                ScreenShotUtil.getScreenShotName(this));
    }

    public final void testDialogCreditCard() {
        showDialog(SHOW_DIALOG_CREDIT_CARD, 2014, 8, 20);
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v,
                ScreenShotUtil.getScreenShotName(this));
    }

    public void testDialog() {
        showDialog(SHOW_DIALOG, 2014, 8, 20);
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v,
                ScreenShotUtil.getScreenShotName(this));
    }

    public final void testImproveCoverage() {
        initActivity();
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((DatePickerDemo) mActivity).improveHtcDatePickerCoverage(2014, 8, 8);
                    ((DatePickerDemo) mActivity).improveHtcDatePickerDialogCoverage(2014, 8, 8);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
