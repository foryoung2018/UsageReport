
package com.htc.lib1.cc.appfragment.test;

import com.htc.lib1.cc.appfragment.activityhelper.HtcListFragmentDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class HtcListFragmentTest extends HtcActivityTestCaseBase {

    public HtcListFragmentTest() {
        super(HtcListFragmentDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testSnapShot() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView("fragment"), this);
    }

}
