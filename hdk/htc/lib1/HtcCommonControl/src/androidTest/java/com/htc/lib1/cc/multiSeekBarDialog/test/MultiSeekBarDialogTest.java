
package com.htc.lib1.cc.multiSeekBarDialog.test;

import com.htc.lib1.cc.multiSeekBarDialog.activityhelper.HtcMultiSeekBarDialogDemo;
import com.htc.lib1.cc.test.R;
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

    public MultiSeekBarDialogTest() {
        super(HtcMultiSeekBarDialogDemo.class);
    }

    public final void testOnCreateBundle() {
        getInstrumentation().waitForIdleSync();
        assertNotNull(mActivity);
    }

    public final void testSnapShot() {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mActivity.findViewById(R.id.targetView), this);
    }

}
