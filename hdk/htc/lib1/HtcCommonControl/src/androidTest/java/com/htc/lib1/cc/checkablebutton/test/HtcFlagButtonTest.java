
package com.htc.lib1.cc.checkablebutton.test;

import android.view.View;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.checkablebutton.activityhelper.HtcCompoundButtons;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcFlagButtonTest extends HtcActivityTestCaseBase {

    public HtcFlagButtonTest() {
        super(HtcCompoundButtons.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public final void test_NoPress() {
        test(R.id.flag_light, false);
    }

    public final void test_Press() {
        test(R.id.flag_light, true);
    }

    private void test(int id, boolean isPress) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.setOrientationMark(false);
        final String fileName = ScreenShotUtil.getScreenShotName(HtcFlagButtonTest.this);
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
