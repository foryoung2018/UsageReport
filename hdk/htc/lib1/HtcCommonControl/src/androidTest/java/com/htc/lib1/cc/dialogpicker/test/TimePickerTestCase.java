
package com.htc.lib1.cc.dialogpicker.test;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.htc.lib1.cc.dialogpicker.activityhelper.TimePickerDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class TimePickerTestCase extends HtcActivityTestCaseBase {
    private final int WIDTH_PORTRAIT_FHD = 1080;
    private final int WIDTH_PORTRAIT_HD = 720;
    private final int WIDTH_PORTRAIT_QHD = 540;
    private final int WIDTH_PORTRAIT_WVGA = 480;
    private final int WIDTH_PORTRAIT_HVGA = 320;

    private final int SHOW_NO_DIALOG = 0;
    private final int SHOW_DIALOG = 1;

    public TimePickerTestCase() {
        super(TimePickerDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        final TimePickerDemo instance = (TimePickerDemo) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                instance.dismissWindow();
            }
        });
        super.tearDown();
    }

    private void showDialog(int id, int hour, int minute, int second) {
        Intent intent = new Intent();
        intent.putExtra("dialogId", id);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        intent.putExtra("second", second);
        setActivityIntent(intent);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
    }

    public final void testOnCreateBundle() {
        showDialog(SHOW_NO_DIALOG, 10, 8, 20);
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v,
                ScreenShotUtil.getScreenShotName(this));
    }

    public final void testDialog() {
        showDialog(SHOW_DIALOG, 10, 8, 20);
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
                    ((TimePickerDemo) mActivity).improveHtcTimePickerDialogCoverage(10, 10, 10);
                    ((TimePickerDemo) mActivity).improveHtcTimePickerCoverage(10, 10, 10);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
