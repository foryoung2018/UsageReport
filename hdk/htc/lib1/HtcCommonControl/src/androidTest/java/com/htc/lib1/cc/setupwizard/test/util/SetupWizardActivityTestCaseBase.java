package com.htc.lib1.cc.setupwizard.test.util;

import com.htc.test.HtcActivityTestCaseBase;

public class SetupWizardActivityTestCaseBase extends HtcActivityTestCaseBase {

    public SetupWizardActivityTestCaseBase(Class activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
    }
}
