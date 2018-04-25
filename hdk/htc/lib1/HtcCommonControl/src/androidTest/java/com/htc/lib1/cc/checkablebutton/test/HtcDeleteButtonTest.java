
package com.htc.lib1.cc.checkablebutton.test;

import android.view.View;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.checkablebutton.activityhelper.HtcCompoundButtons;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcDeleteButtonTest extends HtcActivityTestCaseBase {

    public HtcDeleteButtonTest() {
        super(HtcCompoundButtons.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public final void test_Light_NoPress() {
        test(R.id.delete_light, false);
    }

    public final void test_Light_Press() {
        test(R.id.delete_light, true);
    }

    public final void test_AutoLight_Press() {
        test(R.id.delete_autolight, true);
    }

    public final void test_AutoDark_Press() {
        test(R.id.delete_autodark, true);
    }

    public final void test_AutoLight_NoPress() {
        test(R.id.delete_autolight, false);
    }

    public final void test_AutoDark_NoPress() {
        test(R.id.delete_autodark, false);
    }

    private void test(int id, boolean isPress) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.setOrientationMark(false);
        final String fileName = ScreenShotUtil.getScreenShotName(HtcDeleteButtonTest.this);
        if (isPress) {
            EventUtil.callLongPressed(getInstrumentation(), mSolo.getView(id), new EventUtil.EventCallBack() {
                @Override
                public void onPressedStatus(View view) {
                    ScreenShotUtil.AssertViewEqualBefore(mSolo, view, fileName);
                }
            });
        } else {
            ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(id), fileName);
        }
    }
}
