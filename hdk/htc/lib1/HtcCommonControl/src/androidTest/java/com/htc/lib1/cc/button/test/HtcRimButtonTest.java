
package com.htc.lib1.cc.button.test;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.htc.lib1.cc.button.activityhelper.HtcRimButtonDemo;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcButtonUtil;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;

public class HtcRimButtonTest extends HtcActivityTestCaseBase {

    private static final int STATE_REST = 0;
    private static final int STATE_PRESS = 1;
    private static final int STATE_DISABLE = 2;
    private HtcRimButton mRimBtn;

    public HtcRimButtonTest() {
        super(HtcRimButtonDemo.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testConstructor() {
        HtcRimButton rimBtn = new HtcRimButton(getInstrumentation().getTargetContext(), HtcButtonUtil.BACKGROUND_MODE_LIGHT, false);
        rimBtn = new HtcRimButton(getInstrumentation().getTargetContext(), HtcButtonUtil.BACKGROUND_MODE_DARK, false, 0);
    }

    public void testImproveCoverage() {
        mRimBtn = new HtcRimButton(getInstrumentation().getTargetContext());
        Resources res = mActivity.getResources();
        Drawable out = res.getDrawable(R.drawable.ic_launcher);
        Drawable out1 = res.getDrawable(R.drawable.icon_btn_people_light);
        Drawable out2 = res.getDrawable(R.drawable.icon_btn_phone_dark);
        mRimBtn.setBackgroundDrawable(null);
        mRimBtn.setButtonBackgroundDrawable(out, out1, out2);
        mRimBtn.setButtonBackgroundResource(R.drawable.ic_launcher, R.drawable.icon_btn_people_light, R.drawable.icon_btn_phone_dark);
    }

    public void test_Light_Rest_TextImage_rim() {
        test(R.id.icon_light_text_image_rim, STATE_REST);
    }

    public void test_Light_Press_TextImage_rim() {
        test(R.id.icon_light_text_image_rim, STATE_PRESS);
    }

    public void test_Light_Disable_TextImage_rim() {
        test(R.id.icon_light_text_image_rim, STATE_DISABLE);
    }

    public void test_Dark_Rest_TextImage_rim() {
        test(R.id.icon_dark_text_image_rim, STATE_REST);
    }

    public void test_Dark_Press_TextImage_rim() {
        test(R.id.icon_dark_text_image_rim, STATE_PRESS);
    }

    public void test_Dark_Disable_TextImage_rim() {
        test(R.id.icon_dark_text_image_rim, STATE_DISABLE);
    }

    public void test_AutoMotiveLight_Rest_TextImage_rim() {
        test(R.id.icon_automotivelight_text_image_rim, STATE_REST);
    }

    public void test_AutoMotiveLight_Press_TextImage_rim() {
        test(R.id.icon_automotivelight_text_image_rim, STATE_PRESS);
    }

    public void test_AutoMotiveLight_Disable_TextImage_rim() {
        test(R.id.icon_automotivelight_text_image_rim, STATE_DISABLE);
    }

    public void test_AutoMotiveDark_Rest_TextImage_rim() {
        test(R.id.icon_automotivedark_text_image_rim, STATE_REST);
    }

    public void test_AutoMotiveDark_Press_TextImage_rim() {
        test(R.id.icon_automotivedark_text_image_rim, STATE_PRESS);
    }

    public void test_AutoMotiveDark_Disable_TextImage_rim() {
        test(R.id.icon_automotivedark_text_image_rim, STATE_DISABLE);
    }

    public void test_Light_Rest_Image_rim() {
        test(R.id.icon_light_image_rim, STATE_REST);
    }

    public void test_Light_Press_Image_rim() {
        test(R.id.icon_light_image_rim, STATE_PRESS);
    }

    public void test_Light_Disable_Image_rim() {
        test(R.id.icon_light_image_rim, STATE_DISABLE);
    }

    public void test_Dark_Rest_Image_rim() {
        test(R.id.icon_dark_image_rim, STATE_REST);
    }

    public void test_Dark_Press_Image_rim() {
        test(R.id.icon_dark_image_rim, STATE_PRESS);
    }

    public void test_Dark_Disable_Image_rim() {
        test(R.id.icon_dark_image_rim, STATE_DISABLE);
    }

    public void test_AutoMotiveLight_Rest_Image() {
        test(R.id.icon_automotivelight_image_rim, STATE_REST);
    }

    public void test_AutoMotiveLight_Press_Image_rim() {
        test(R.id.icon_automotivelight_image_rim, STATE_PRESS);
    }

    public void test_AutoMotiveLight_Disable_Image_rim() {
        test(R.id.icon_automotivelight_image_rim, STATE_DISABLE);
    }

    public void test_AutoMotiveDark_Rest_Image_rim() {
        test(R.id.icon_automotivedark_image_rim, STATE_REST);
    }

    public void test_AutoMotiveDark_Press_Image_rim() {
        test(R.id.icon_automotivedark_image_rim, STATE_PRESS);
    }

    public void test_AutoMotiveDark_Disable_Image_rim() {
        test(R.id.icon_automotivedark_image_rim, STATE_DISABLE);
    }

    public void test_Light_Rest_Text_rim() {
        test(R.id.icon_light_text_rim, STATE_REST);
    }

    public void test_Light_Press_Text_rim() {
        test(R.id.icon_light_text_rim, STATE_PRESS);
    }

    public void test_Light_Disable_Text_rim() {
        test(R.id.icon_light_text_rim, STATE_DISABLE);
    }

    public void test_Dark_Rest_Text_rim() {
        test(R.id.icon_dark_text_rim, STATE_REST);
    }

    public void test_Dark_Press_Text_rim() {
        test(R.id.icon_dark_text_rim, STATE_PRESS);
    }

    public void test_Dark_Disable_Text_rim() {
        test(R.id.icon_dark_text_rim, STATE_DISABLE);
    }

    public void test_AutoMotiveLight_Rest_Text_rim() {
        test(R.id.icon_automotivelight_text_rim, STATE_REST);
    }

    public void test_AutoMotiveLight_Press_Text_rim() {
        test(R.id.icon_automotivelight_text_rim, STATE_PRESS);
    }

    public void test_AutoMotiveLight_Disable_Text_rim() {
        test(R.id.icon_automotivelight_text_rim, STATE_DISABLE);
    }

    public void test_AutoMotiveDark_Rest_Text_rim() {
        test(R.id.icon_automotivedark_text_rim, STATE_REST);
    }

    public void test_AutoMotiveDark_Press_Text_rim() {
        test(R.id.icon_automotivedark_text_rim, STATE_PRESS);
    }

    public void test_AutoMotiveDark_Disable_Text_rim() {
        test(R.id.icon_automotivedark_text_rim, STATE_DISABLE);
    }

    private void test(int id, int state) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        final HtcRimButton button = (HtcRimButton) mSolo.getView(id);
        switch (state) {
            case STATE_REST:
                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                        HtcRimButtonTest.this);
                break;
            case STATE_PRESS:
                EventUtil.callLongPressed(getInstrumentation(), button,
                        new EventUtil.EventCallBack() {
                            public void onPressedStatus(View view) {
                                // TODO Auto-generated method stub
                                getInstrumentation().waitForIdleSync();
                                mSolo.sleep(3000);
                                ScreenShotUtil.AssertViewEqualBefore(mSolo, button, HtcRimButtonTest.this);
                            }
                        });
                break;
            case STATE_DISABLE:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(false);
                    }
                });
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                        HtcRimButtonTest.this);
                break;
        }
    }
}
