package com.htc.test;

public abstract class HtcActivityInstrumentationTestCase extends
        HtcActivityTestCase {

    public HtcActivityInstrumentationTestCase(Class activityClass) {
        super(activityClass);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        setActivityIntent(null);
        initActivity();
    }

}
