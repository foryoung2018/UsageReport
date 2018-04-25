
package com.htc.lib1.cc.checkablebutton.test;

import com.htc.lib1.cc.widget.HtcButtonUtil;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.checkablebutton.activityhelper.HtcCompoundButtons;

public class HtcCheckBoxTest extends HtcActivityTestCaseBase {
    private HtcCheckBox mCheckBox;

    public HtcCheckBoxTest() {
        super(HtcCompoundButtons.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        mCheckBox = (HtcCheckBox) mActivity.findViewById(R.id.chk_light);
    }

    public final void testSetPartialSelection_on() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mCheckBox.setPartialSelection(true);
            }
        });
    }

    public final void testSetPartialSelection_off() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mCheckBox.setPartialSelection(false);
            }
        });
    }

    public final void testCompositeBitmap() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mCheckBox.compositeBitmap(mActivity);
            }
        });
    }

    public final void testSetDrawOnce() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mCheckBox.setDrawOnce(true, null);
            }
        });
    }

    public final void testStopDrawOnce() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mCheckBox.stopDrawOnce();
            }
        });
    }

    public final void testBackground_light() {
        HtcCheckBox htcCheckBox = new HtcCheckBox(mActivity, HtcButtonUtil.BACKGROUND_MODE_LIGHT);
    }

    public final void testBackground_dark() {
        HtcCheckBox htcCheckBox = new HtcCheckBox(mActivity, HtcButtonUtil.BACKGROUND_MODE_DARK);
    }

    public final void testBackground_autoDark() {
        HtcCheckBox htcCheckBox = new HtcCheckBox(mActivity, HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK);
    }

    public final void testBackground_autoLight() {
        HtcCheckBox htcCheckBox = new HtcCheckBox(mActivity, HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVELIGHT);
    }

    public final void testBackground_colorful() {
        HtcCheckBox htcCheckBox = new HtcCheckBox(mActivity, HtcButtonUtil.BACKGROUND_MODE_COLORFUL);
    }

}
