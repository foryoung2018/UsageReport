
package com.htc.lib1.cc.htcFooter.test;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import com.htc.lib1.cc.htcFooter.activityhelper.FooterActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.robotium.solo.Solo;

import java.util.Locale;

public class FooterButtonTest extends HtcActivityTestCaseBase {
    public FooterButtonTest() {
        super(FooterActivity.class);
    }

    public final void test_Light_OIcon_OText_Enable() {
        test(R.layout.footerbutton_light_hasicon_hastext, R.id.test_htcFooterButton, true);
    }

    public final void test_Light_OIcon_XText_Enable() {
        test(R.layout.footerbutton_light_hasicon_notext, R.id.test_htcFooterButton, true);
    }

    public final void test_Light_XIcon_SText_Enable() {
        test(R.layout.footerbutton_light_noicon_spacetext, R.id.test_htcFooterButton, true);
    }

    public final void test_Light_XIcon_OText_Enable() {
        test(R.layout.footerbutton_light_noicon_hastext, R.id.test_htcFooterButton, true);
    }

    public final void test_Dark_OIcon_OText_Enable() {
        test(R.layout.footerbutton_dark_hasicon_hastext, R.id.test_htcFooterButton, true);
    }

    public final void test_Dark_OIcon_XText_Enable() {
        test(R.layout.footerbutton_dark_hasicon_notext, R.id.test_htcFooterButton, true);
    }

    public final void test_Dark_XIcon_SText_Enable() {
        test(R.layout.footerbutton_dark_noicon_spacetext, R.id.test_htcFooterButton, true);
    }

    public final void test_Dark_XIcon_OText_Enable() {
        test(R.layout.footerbutton_dark_noicon_hastext, R.id.test_htcFooterButton, true);
    }

    public final void test_Tran_OIcon_OText_Enable() {
        test(R.layout.footerbutton_tran_hasicon_hastext, R.id.test_htcFooterButton, true);
    }

    public final void test_Tran_OIcon_XText_Enable() {
        test(R.layout.footerbutton_tran_hasicon_notext, R.id.test_htcFooterButton, true);
    }

    public final void test_Tran_XIcon_SText_Enable() {
        test(R.layout.footerbutton_tran_noicon_spacetext, R.id.test_htcFooterButton, true);
    }

    public final void test_Tran_XIcon_OText_Enable() {
        test(R.layout.footerbutton_tran_noicon_hastext, R.id.test_htcFooterButton, true);
    }

    public final void test_Light_OIcon_OText_Disable() {
        test(R.layout.footerbutton_light_hasicon_hastext, R.id.test_htcFooterButton, false);
    }

    public final void test_Light_OIcon_XText_Disable() {
        test(R.layout.footerbutton_light_hasicon_notext, R.id.test_htcFooterButton, false);
    }

    public final void test_Light_XIcon_SText_Disable() {
        test(R.layout.footerbutton_light_noicon_spacetext, R.id.test_htcFooterButton, false);
    }

    public final void test_Light_XIcon_OText_Disable() {
        test(R.layout.footerbutton_light_noicon_hastext, R.id.test_htcFooterButton, false);
    }

    public final void test_Dark_OIcon_OText_Disable() {
        test(R.layout.footerbutton_dark_hasicon_hastext, R.id.test_htcFooterButton, false);
    }

    public final void test_Dark_OIcon_XText_Disable() {
        test(R.layout.footerbutton_dark_hasicon_notext, R.id.test_htcFooterButton, false);
    }

    public final void test_Dark_XIcon_SText_Disable() {
        test(R.layout.footerbutton_dark_noicon_spacetext, R.id.test_htcFooterButton, false);
    }

    public final void test_Dark_XIcon_OText_Disable() {
        test(R.layout.footerbutton_dark_noicon_hastext, R.id.test_htcFooterButton, false);
    }

    public final void test_Tran_OIcon_OText_Disable() {
        test(R.layout.footerbutton_tran_hasicon_hastext, R.id.test_htcFooterButton, false);
    }

    public final void test_Tran_OIcon_XText_Disable() {
        test(R.layout.footerbutton_tran_hasicon_notext, R.id.test_htcFooterButton, false);
    }

    public final void test_Tran_XIcon_SText_Disable() {
        test(R.layout.footerbutton_tran_noicon_spacetext, R.id.test_htcFooterButton, false);
    }

    public final void test_Tran_XIcon_OText_Disable() {
        test(R.layout.footerbutton_tran_noicon_hastext, R.id.test_htcFooterButton, false);
    }
    public final void test_Light_Press() {
        testPress(R.layout.footerbutton_light_hasicon_hastext, R.id.test_htcFooterButton, R.id.hfb_light);
    }

    public final void test_dark_Press() {
        testPress(R.layout.footerbutton_dark_hasicon_hastext, R.id.test_htcFooterButton, R.id.hfb_dark);
    }

    public final void test_Colorful_OIcon_OText() {
        testColorFul(R.layout.footerbutton_colorful_hasicon_hastext, R.id.test_htcFooterButton);
    }

    public final void test_Colorful_OIcon_XText() {
        testColorFul(R.layout.footerbutton_colorful_hasicon_notext, R.id.test_htcFooterButton);
    }

    public final void test_Colorful_XIcon_OText() {
        testColorFul(R.layout.footerbutton_colorful_noicon_hastext, R.id.test_htcFooterButton);
    }

    public final void test_SetImage_Null() {
        testSetImage(true, R.layout.footerbutton_light_hasicon_hastext, R.id.test_htcFooterButton, R.id.hfb_light);
    }

    public final void test_SetImage_Xnull() {
        testSetImage(false, R.layout.footerbutton_light_hasicon_hastext, R.id.test_htcFooterButton, R.id.hfb_light);
    }

    public final void test_Arab_LTR() {
        testSpecialLocale(R.id.test_htcFooterButton, View.LAYOUT_DIRECTION_LTR, new Locale("ar"));
    }

    public final void test_China_LTR() {
        testSpecialLocale(R.id.test_htcFooterButton, View.LAYOUT_DIRECTION_LTR, Locale.CHINA);
    }

    public final void test_Arab_RTL() {
        testSpecialLocale(R.id.test_htcFooterButton, View.LAYOUT_DIRECTION_RTL, new Locale("ar"));
    }

    public final void test_China_RTL() {
        testSpecialLocale(R.id.test_htcFooterButton, View.LAYOUT_DIRECTION_RTL, Locale.CHINA);
    }

    public final void test_Text_Null() {
        Intent i = new Intent();
        i.putExtra("layoutId", R.layout.footerbutton_light_hasicon_hastext);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        HtcFooter footer = (HtcFooter) mActivity.findViewById(R.id.test_htcFooterButton);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                HtcFooterButton hfb = (HtcFooterButton) mActivity
                        .findViewById(R.id.test_htcFooterButton_left);
                hfb.setText(null);
            }
        });

        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, footer, this);
    }
    public final void test_Reverse_Land(){
        Intent i = new Intent();
        i.putExtra("layoutId", R.layout.footerbutton_ok_done_cancel);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                HtcFooter hf = (HtcFooter) mSolo.getView(R.id.test_htcFooterButton);
                if(getOrientation() == Solo.LANDSCAPE){
                    hf.ReverseLandScapeSequence(true);
                }
            }
        });
        getInstrumentation().waitForIdleSync();

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(R.id.test_htcFooterButton), this);
    }

    private void testSpecialLocale(final int widgetId, final int direction, final Locale locale) {
        Intent i = new Intent();
        i.putExtra("layoutId", R.layout.footerbutton_rtl);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());

        View footer = mActivity.findViewById(R.id.test_htcFooterButton);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                HtcFooterButton leftButton = (HtcFooterButton) mActivity
                        .findViewById(R.id.test_htcFooterButton_left);
                HtcFooterButton rightButton = (HtcFooterButton) mActivity
                        .findViewById(R.id.test_htcFooterButton_right);

                leftButton.setLayoutDirection(direction);
                rightButton.setLayoutDirection(direction);
                leftButton.setTextLocale(locale);
                rightButton.setTextLocale(locale);

                Resources res = mActivity.getResources();
                Configuration config = res.getConfiguration();
                Locale sysLocale = config.locale;

                config.locale = locale;
                res.updateConfiguration(config, res.getDisplayMetrics());

                leftButton.setText(res.getString(R.string.single_line));
                rightButton.setText(res.getString(R.string.double_line));

                config.locale = sysLocale;
            }
        });
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, footer, this);
    }

    private void test(int layoutId, int widgetId, final boolean enable) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
        if (!enable) {
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    HtcFooterButton leftButton = (HtcFooterButton) mActivity.findViewById(R.id.test_htcFooterButton_left);
                    leftButton.setEnabled(enable);
                }
            });
        }
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(widgetId), this);
    }

    private void testPress(int layoutId, int footerId, int footerButtonId) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        final View view = mSolo.getView(footerButtonId);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.setPressed(true);
            }
        });
        getInstrumentation().waitForIdleSync();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSolo.sleep(10000);
        }
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(footerId), this);
    }

    private void testColorFul(int layoutId, int widgetId) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
        final HtcFooterButton leftButton = (HtcFooterButton) mActivity.findViewById(R.id.test_htcFooterButton_left);
        final HtcFooterButton rightButton = (HtcFooterButton) mActivity.findViewById(R.id.test_htcFooterButton_right);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                leftButton.enableColorFul(false);
                rightButton.enableColorFul(true);
            }
        });
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(widgetId), this);
    }

    private void testSetImage(final boolean isNull, int layoutId, int footerId, int footerButtonId) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        final HtcFooterButton hfb = (HtcFooterButton)mSolo.getView(footerButtonId);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                if(isNull){
                    hfb.setImageDrawable(null);
                }else{
                    hfb.setImageResource(R.drawable.icon_btn_search_dark);
                }
            }
        });
        getInstrumentation().waitForIdleSync();

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(footerId), this);
    }
}
