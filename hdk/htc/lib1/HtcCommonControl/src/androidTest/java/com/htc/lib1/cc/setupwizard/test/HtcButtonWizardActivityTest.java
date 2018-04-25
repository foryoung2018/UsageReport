package com.htc.lib1.cc.setupwizard.test;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

import com.htc.lib1.cc.setupwizard.test.util.SetupWizardActivityTestCaseBase;
import com.htc.lib1.cc.setupwizard.test.util.SetupWizardTestUtil;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.test.util.ScreenShotUtil;

public class HtcButtonWizardActivityTest extends SetupWizardActivityTestCaseBase {

    public HtcButtonWizardActivityTest() throws ClassNotFoundException {
        super(Class.forName("com.htc.lib1.cc.setupwizard.activityhelper.DemoButtonActivity"));
    }

    private void assertWizardActivity() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SetupWizardTestUtil.hideScrollView(mSolo);
            }
        });
        View view =mActivity.findViewById(android.R.id.content);
        ScreenShotUtil
                .AssertViewEqualBefore(mSolo, view, this);
    }

    public void testButtonWizardShow() {
        assertWizardActivity();
    }

    public final void testDescriptTextSize() {
        SetupWizardTestUtil
                .testDescriptTextSize(mActivity, mSolo, this, "desc");
    }

    public final void testDescriptMarginMeasure() {
        SetupWizardTestUtil.testDescriptMarginMeasure(mActivity, mSolo, this,
                "desc");
    }

    public final void testMainButtonMarginMeasure() {
        String packageName = getInstrumentation().getTargetContext()
                .getPackageName();
        int mMargin_l_ID = mActivity.getResources().getIdentifier(
                "margin_l", "dimen", packageName);
        int mMargin_l = mActivity.getResources().getDimensionPixelSize(
                mMargin_l_ID);
        HtcRimButton mButton = (HtcRimButton) mSolo.getView("button");
        MarginLayoutParams mlp = (MarginLayoutParams) mButton
                .getLayoutParams();
        assertEquals(mMargin_l, mlp.bottomMargin);
    }

    public final void testButtonClick() {
        final HtcRimButton mButton = (HtcRimButton) mSolo.getView("button");
        try {
            runTestOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mButton.setText("Button is clicked");
                    mButton.performClick();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mButton, this);
    }
}
