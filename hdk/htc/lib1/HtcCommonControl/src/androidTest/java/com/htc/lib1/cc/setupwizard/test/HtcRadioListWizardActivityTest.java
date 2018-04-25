package com.htc.lib1.cc.setupwizard.test;

import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.setupwizard.test.util.SetupWizardActivityTestCaseBase;
import com.htc.lib1.cc.setupwizard.test.util.SetupWizardTestUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcRadioListWizardActivityTest extends SetupWizardActivityTestCaseBase {

    public HtcRadioListWizardActivityTest() throws ClassNotFoundException {
        super(Class.forName("com.htc.lib1.cc.setupwizard.activityhelper.DemoRadioListActivity"));
    }

    private void assertWizardActivity() {
        SetupWizardTestUtil.hideScrollView(mSolo);
        HtcListView mlistView = (HtcListView) mSolo.getView("list");
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mlistView.getRootView(),
                this);
    }

    public void tesRadioListWizardShow() {
        assertWizardActivity();
    }

    public final void testSubTitleTextSize() {
        SetupWizardTestUtil.testSubTitleTextSize(mActivity, mSolo, this, "tip");
    }

    public final void testDescriptTextSize() {
        SetupWizardTestUtil
                .testDescriptTextSize(mActivity, mSolo, this, "desc");
    }

    public final void testRadioListItemClick() {
        final HtcListView mListView = (HtcListView) mSolo.getView("list");
        mListView.setFocusable(true);

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListView.performItemClick(null, 1, 0);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mListView, this);
    }
}
