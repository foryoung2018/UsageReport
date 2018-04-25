
package com.htc.lib1.cc.checkablebutton.test;

import android.view.View;

import com.htc.lib1.cc.widget.HtcButtonUtil;
import com.htc.lib1.cc.widget.HtcRadioButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.checkablebutton.activityhelper.HtcCompoundButtons;

public class HtcRadioButtonTest extends HtcActivityTestCaseBase {

    /**
     * Short Words Mean: Auto = Automotive mode Factor: AutoLight / AutoDark / Light / Dark NoPress
     * / Press
     */
    public HtcRadioButtonTest() {
        super(HtcCompoundButtons.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public final void test_Dark_NoPress() {
        test(R.id.radio_dark, false);
    }

    public final void test_Light_NoPress() {
        test(R.id.radio_light, false);
    }

    public final void test_AutoLight_NoPress() {
        test(R.id.radio_autolight, false);
    }

    public final void test_AutoDark_NoPress() {
        test(R.id.radio_autodark, false);
    }

    public final void test_Dark_Press() {
        test(R.id.radio_dark, true);
    }

    public final void test_Light_Press() {
        test(R.id.radio_light, true);
    }

    public final void test_AutoLight_Press() {
        test(R.id.radio_autolight, true);
    }

    public final void test_AutoDark_Press() {
        test(R.id.radio_autodark, true);
    }

    private void test(int id, boolean isPress) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.setOrientationMark(false);
        final String fileName = ScreenShotUtil.getScreenShotName(HtcRadioButtonTest.this);
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

    public final void testBackground_light() {
        HtcRadioButton htcRadioButton = new HtcRadioButton(mActivity, HtcButtonUtil.BACKGROUND_MODE_LIGHT);
    }

    public final void testBackground_dark() {
        HtcRadioButton htcRadioButton = new HtcRadioButton(mActivity, HtcButtonUtil.BACKGROUND_MODE_DARK);
    }

    public final void testBackground_autoDark() {
        HtcRadioButton htcRadioButton = new HtcRadioButton(mActivity, HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK);
    }

    public final void testBackground_autoLight() {
        HtcRadioButton htcRadioButton = new HtcRadioButton(mActivity, HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVELIGHT);
    }

    public final void testBackground_colorful() {
        HtcRadioButton htcRadioButton = new HtcRadioButton(mActivity, HtcButtonUtil.BACKGROUND_MODE_COLORFUL);
    }

}
