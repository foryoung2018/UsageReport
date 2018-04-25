
package com.htc.lib1.cc.layout.test;

import android.view.View;

import com.htc.lib1.cc.layout.activityhelper.HtcVcardActivity;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.test.R;

public class VcardTest extends HtcActivityTestCaseBase {

    public VcardTest() {
        super(HtcVcardActivity.class);
    }

    public final void testOnCreateBundle() {
        assertNotNull(mActivity);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testSmallPhoto() {
        test(R.id.vcard_smallphoto);
    }

    public void testBigPhoto() {
        test(R.id.vcard_bigphoto);
    }

    private void test(int id) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        final View view = mActivity.findViewById(id);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, view, this);
    }

}
