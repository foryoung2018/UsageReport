package com.htc.lib1.cc.setupwizard.test;

import android.os.Bundle;
import android.test.InstrumentationTestSuite;

import com.htc.test.HtcTestRunner;

import junit.framework.TestSuite;

public class SetupWizardTestRunner extends HtcTestRunner {

    public TestSuite getAllTests() {

        // 添加全部场景都需要测试的
        TestSuite ts = new InstrumentationTestSuite(this);
        ts.addTestSuite(HtcButtonWizardActivityTest.class);
        ts.addTestSuite(HtcRadioListWizardActivityTest.class);
        ts.addTestSuite(HtcWizardActivityTest.class);
        ts.addTestSuite(HtcSpecialWizardActivityTest.class);

        Bundle arg = getArguments();
        if (null != arg) {

            String orientation = arg.getString("orientation");
            if ("portrait".equals(orientation)) {
                ts.addTestSuite(HtcPortraitActivityTest.class);
            }

        }

        return ts;
    }

}
