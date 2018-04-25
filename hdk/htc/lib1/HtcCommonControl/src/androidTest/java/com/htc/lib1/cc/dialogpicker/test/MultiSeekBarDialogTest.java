
package com.htc.lib1.cc.dialogpicker.test;

import android.view.View;
import android.widget.FrameLayout;

import com.htc.lib1.cc.dialogpicker.activityhelper.HtcMultiSeekBarDialogDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class MultiSeekBarDialogTest extends HtcActivityTestCaseBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    @Override
    protected void tearDown() throws Exception {
        final HtcMultiSeekBarDialogDemo instance = (HtcMultiSeekBarDialogDemo) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                instance.dismissWindow();
            }
        });
        super.tearDown();
    }

    public MultiSeekBarDialogTest() {
        super(HtcMultiSeekBarDialogDemo.class);
    }

    public final void testOnCreateBundle() {
        getInstrumentation().waitForIdleSync();
        assertNotNull(mActivity);
    }

    public final void testSnapShot() {
        View v = mSolo.getView(FrameLayout.class, 0).getRootView();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, v, ScreenShotUtil.getScreenShotName(this));
    }

    public final void testImproveCoverage() {
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((HtcMultiSeekBarDialogDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
