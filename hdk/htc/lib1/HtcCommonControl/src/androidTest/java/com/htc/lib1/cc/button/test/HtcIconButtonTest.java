package com.htc.lib1.cc.button.test;



import android.view.View;
import com.htc.lib1.cc.button.activityhelper.HtcIconButtonDemo;
import com.htc.lib1.cc.widget.HtcIconButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.test.R;

public class HtcIconButtonTest extends HtcActivityTestCaseBase {

    private static final int STATE_REST = 0;
    private static final int STATE_PRESS = 1;
    private static final int STATE_DISABLE = 2;

    public HtcIconButtonTest() {
        super(HtcIconButtonDemo.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void test_Light_Rest_TextImage() {
        test(R.id.icon_light_text_image, STATE_REST);
    }

    public void test_Light_Press_TextImage() {
        test(R.id.icon_light_text_image, STATE_PRESS);
    }

    public void test_Light_Disable_TextImage() {
        test(R.id.icon_light_text_image, STATE_DISABLE);
    }

    public void test_Dark_Rest_TextImage() {
        test(R.id.icon_dark_text_image, STATE_REST);
    }

    public void test_Dark_Press_TextImage() {
        test(R.id.icon_dark_text_image, STATE_PRESS);
    }

    public void test_Dark_Disable_TextImage() {
        test(R.id.icon_dark_text_image, STATE_DISABLE);
    }

    public void test_AutoMotiveLight_Rest_TextImage() {
        test(R.id.icon_automotivelight_text_image, STATE_REST);
    }

    public void test_AutoMotiveLight_Press_TextImage() {
        test(R.id.icon_automotivelight_text_image, STATE_PRESS);
    }

    public void test_AutoMotiveLight_Disable_TextImage() {
        test(R.id.icon_automotivelight_text_image, STATE_DISABLE);
    }

    public void test_AutoMotiveDark_Rest_TextImage() {
        test(R.id.icon_automotivedark_text_image, STATE_REST);
    }

    public void test_AutoMotiveDark_Press_TextImage() {
        test(R.id.icon_automotivedark_text_image, STATE_PRESS);
    }

    public void test_AutoMotiveDark_Disable_TextImage() {
        test(R.id.icon_automotivedark_text_image, STATE_DISABLE);
    }

    public void test_Light_Rest_Image() {
        test(R.id.icon_light_image, STATE_REST);
    }

    public void test_Light_Press_Image() {
        test(R.id.icon_light_image, STATE_PRESS);
    }

    public void test_Light_Disable_Image() {
        test(R.id.icon_light_image, STATE_DISABLE);
    }

    public void test_Dark_Rest_Image() {
        test(R.id.icon_dark_image, STATE_REST);
    }

    public void test_Dark_Press_Image() {
        test(R.id.icon_dark_image, STATE_PRESS);
    }

    public void test_Dark_Disable_Image() {
        test(R.id.icon_dark_image, STATE_DISABLE);
    }

    public void test_AutoMotiveLight_Rest_Image() {
        test(R.id.icon_automotivelight_image, STATE_REST);
    }

    public void test_AutoMotiveLight_Press_Image() {
        test(R.id.icon_automotivelight_image, STATE_PRESS);
    }

    public void test_AutoMotiveLight_Disable_Image() {
        test(R.id.icon_automotivelight_image, STATE_DISABLE);
    }

    public void test_AutoMotiveDark_Rest_Image() {
        test(R.id.icon_automotivedark_image, STATE_REST);
    }

    public void test_AutoMotiveDark_Press_Image() {
        test(R.id.icon_automotivedark_image, STATE_PRESS);
    }

    public void test_AutoMotiveDark_Disable_Image() {
        test(R.id.icon_automotivedark_image, STATE_DISABLE);
    }

    public void test_Light_Rest_Text() {
        test(R.id.icon_light_text, STATE_REST);
    }

    public void test_Light_Press_Text() {
        test(R.id.icon_light_text, STATE_PRESS);
    }

    public void test_Light_Disable_Text() {
        test(R.id.icon_light_text, STATE_DISABLE);
    }

    public void test_Dark_Rest_Text() {
        test(R.id.icon_dark_text, STATE_REST);
    }

    public void test_Dark_Press_Text() {
        test(R.id.icon_dark_text, STATE_PRESS);
    }

    public void test_Dark_Disable_Text() {
        test(R.id.icon_dark_text, STATE_DISABLE);
    }

    public void test_AutoMotiveLight_Rest_Text() {
        test(R.id.icon_automotivelight_text, STATE_REST);
    }

    public void test_AutoMotiveLight_Press_Text() {
        test(R.id.icon_automotivelight_text, STATE_PRESS);
    }

    public void test_AutoMotiveLight_Disable_Text() {
        test(R.id.icon_automotivelight_text, STATE_DISABLE);
    }

    public void test_AutoMotiveDark_Rest_Text() {
        test(R.id.icon_automotivedark_text, STATE_REST);
    }

    public void test_AutoMotiveDark_Press_Text() {
        test(R.id.icon_automotivedark_text, STATE_PRESS);
    }

    public void test_AutoMotiveDark_Disable_Text() {
        test(R.id.icon_automotivedark_text, STATE_DISABLE);
    }

    private void test(int id, int state) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();

        final HtcIconButton button = (HtcIconButton) mSolo.getView(id);
        switch (state) {
            case STATE_REST:
                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                        HtcIconButtonTest.this);
                break;
            case STATE_PRESS:
                EventUtil.callLongPressed(getInstrumentation(), button,
                        new EventUtil.EventCallBack() {
                            @Override
                            public void onPressedStatus(View view) {
                                getInstrumentation().waitForIdleSync();
                                mSolo.sleep(3000);
                                ScreenShotUtil.AssertViewEqualBefore(mSolo, button,
                                        HtcIconButtonTest.this);
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
                        HtcIconButtonTest.this);
                break;
        }
    }
}
