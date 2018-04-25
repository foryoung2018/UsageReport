
package com.htc.lib1.cc.railingLayout.test;

import com.htc.lib1.cc.railingLayout.activityhelper.RailingLayoutDemoActivity;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

import com.htc.lib1.cc.test.R;

public class RailingLayoutTest extends HtcActivityTestCaseBase {

    public RailingLayoutTest() {
        super(RailingLayoutDemoActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void test_light() {
        test(R.id.light_rl);
    }

    public final void test_dark1() {
        test(R.id.dark_rl1);
    }

    public final void test_dark2() {
        test(R.id.dark_rl2);
    }

    private void test(int id) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(id), this);
    }

}
