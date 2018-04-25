package com.htc.lib1.cc.setupwizard.test;

import com.htc.lib1.cc.setupwizard.test.util.SetupWizardActivityTestCaseBase;
import com.htc.lib1.cc.setupwizard.test.util.SetupWizardTestUtil;

public class HtcPortraitActivityTest  extends SetupWizardActivityTestCaseBase{

    public HtcPortraitActivityTest() throws ClassNotFoundException {
        super(Class.forName("com.htc.lib1.cc.setupwizard.activityhelper.DemoRadioListActivity"));
    }

    public final void testPortraitmageMarginMeasure() {
        SetupWizardTestUtil.testImageMarginMeasure(mActivity, mSolo, this,
                "image");
    }


}
