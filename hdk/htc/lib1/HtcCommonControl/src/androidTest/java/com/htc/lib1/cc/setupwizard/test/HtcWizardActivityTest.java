package com.htc.lib1.cc.setupwizard.test;

import android.widget.ProgressBar;

import com.htc.lib1.cc.setupwizard.test.util.SetupWizardActivityTestCaseBase;
import com.htc.lib1.cc.setupwizard.test.util.SetupWizardTestUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcWizardActivityTest extends SetupWizardActivityTestCaseBase {

    public HtcWizardActivityTest() throws ClassNotFoundException {
        super(
                Class.forName("com.htc.lib1.cc.setupwizard.activityhelper.DemoActivity1"));
    }

    private void assertWizardActivity() {
        ProgressBar pb = (ProgressBar) mSolo.getView("progress_bar");
        ScreenShotUtil.AssertViewEqualBefore(mSolo, pb.getRootView(), this);
    }

    public void testWizardShow() {
        assertWizardActivity();
    }

    public final void testSubTitleTextSize() {
        SetupWizardTestUtil.testSubTitleTextSize(mActivity, mSolo, this, "tip");
    }
}
