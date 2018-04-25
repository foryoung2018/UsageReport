
package com.htc.lib1.cc.checkablebutton.test;

import android.graphics.Typeface;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.checkablebutton.activityhelper.HtcSwitchDemo;
import com.htc.lib1.cc.widget.HtcButtonUtil;
import com.htc.lib1.cc.widget.HtcSwitch;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class HtcSwitchTest extends HtcActivityTestCaseBase {

    HtcSwitch mHtcSwitch = null;

    /**
     * Program change HtcSwitch's status by Program Click change HtcSwitch's status by Click Dark
     * Dark Mode Light Light Mode ON change HtcSwitch's current status to on OFF change HtcSwitch's
     * current status to off
     */
    public HtcSwitchTest() {
        super(HtcSwitchDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        mHtcSwitch = (HtcSwitch) mActivity.findViewById(R.id.myhtcswitch);

    }

    public final void testProgramDarkOn() {
        test(true, true, true);
    }

    public final void testProgramDarkOff() {
        test(true, true, false);
    }

    public final void testProgramLightOn() {
        test(true, false, true);
    }

    public final void testProgramLightOff() {
        test(true, false, false);
    }

    public final void testClickDarkOn() {
        test(false, true, true);
    }

    public final void testClickDarkOff() {
        test(false, true, false);
    }

    public final void testClickLightOn() {
        test(false, false, true);
    }

    public final void testClickLightOff() {
        test(false, false, false);
    }

    public final void testDarkFifty() {
        testSwitchByFifty(true);
    }

    public final void testLightFifty() {
        testSwitchByFifty(false);
    }

    private void test(boolean changeStatusMethod, final boolean mode,
            final boolean status) {
        if (changeStatusMethod) {
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    if (mode) {
                        mHtcSwitch.setMode(HtcButtonUtil.BACKGROUND_MODE_DARK);
                    } else {
                        mHtcSwitch.setMode(HtcButtonUtil.BACKGROUND_MODE_LIGHT);
                    }
                    mHtcSwitch.setChecked(status);
                }
            });
        } else {
            if (mode) {
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        mHtcSwitch.setMode(HtcButtonUtil.BACKGROUND_MODE_DARK);
                    }
                });
            } else {
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        mHtcSwitch.setMode(HtcButtonUtil.BACKGROUND_MODE_LIGHT);
                    }
                });
            }

            if (!mHtcSwitch.isChecked() && status) {
                mSolo.clickOnView(mHtcSwitch);
            }
        }
        mSolo.sleep(2000);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                mActivity.findViewById(R.id.myhtcswitch), this);
    }

    private void testSwitchByFifty(boolean mode) {
        if (mode) {
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    mHtcSwitch.setMode(HtcButtonUtil.BACKGROUND_MODE_DARK);
                    ((HtcSwitchDemo) mActivity).changeSwitchLocation(0.5f);
                }
            });

        } else {
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    mHtcSwitch.setMode(HtcButtonUtil.BACKGROUND_MODE_LIGHT);
                    ((HtcSwitchDemo) mActivity).changeSwitchLocation(0.5f);
                }
            });
        }
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mHtcSwitch, this);
    }

    public final void testGetMode() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mHtcSwitch.getMode();
            }
        });
    }

    public final void testSetSwitchTypeface() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mHtcSwitch.setSwitchTypeface(Typeface.MONOSPACE);
            }
        });

    }

    public final void testSetSwitchTypefaceByStyle() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mHtcSwitch.setSwitchTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
            }
        });

    }

    public final void testSetSwitchTypefaceByStyle_InvalidStyle() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mHtcSwitch.setSwitchTypeface(Typeface.MONOSPACE, -1);
            }
        });
    }

}
