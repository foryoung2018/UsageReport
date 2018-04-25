
package com.htc.lib1.cc.htcFooter.test;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.htc.lib1.cc.htcFooter.activityhelper.FooterActivity;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.test.R;

public class FooterTest extends HtcActivityTestCaseBase {
    private static final int CHILD_VIEW_1 = 1;
    private static final int CHILD_VIEW_2 = 2;
    private static final int CHILD_VIEW_3 = 3;
    private static final int CHILD_VIEW_4 = 4;

    public FooterTest() {
        super(FooterActivity.class);
    }

    public final void test_layout_weight() {
        test(R.layout.htcfooter_layout_weight,
                R.id.htcfooter_layout_weight);
    }

    public final void testOneChild() {
        test(R.layout.htcfooter_one_child, R.id.htcfooter_one_child);
    }

    public final void test_UIGL_1Btn_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_icon, CHILD_VIEW_1, R.id.htcfooter_uigl_light_icon);
    }

    public final void test_UIGL_2Btn_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_icon, CHILD_VIEW_2, R.id.htcfooter_uigl_light_icon);
    }

    public final void test_UIGL_3Btn_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_icon, CHILD_VIEW_3, R.id.htcfooter_uigl_light_icon);
    }

    public final void test_UIGL_4Btn_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_icon, CHILD_VIEW_4, R.id.htcfooter_uigl_light_icon);
    }

    public final void test_UIGL_1Text_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_text, CHILD_VIEW_1, R.id.htcfooter_uigl_light_text);
    }

    public final void test_UIGL_2Text_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_text, CHILD_VIEW_2, R.id.htcfooter_uigl_light_text);
    }

    public final void test_UIGL_3Text_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_text, CHILD_VIEW_3, R.id.htcfooter_uigl_light_text);
    }

    public final void test_UIGL_4Text_Light() {
        testUIGL(R.layout.htcfooter_uigl_light_text, CHILD_VIEW_4, R.id.htcfooter_uigl_light_text);
    }

    public final void test_UIGL_1Btn_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_icon, CHILD_VIEW_1, R.id.htcfooter_uigl_dark_icon);
    }

    public final void test_UIGL_2Btn_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_icon, CHILD_VIEW_2, R.id.htcfooter_uigl_dark_icon);
    }

    public final void test_UIGL_3Btn_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_icon, CHILD_VIEW_3, R.id.htcfooter_uigl_dark_icon);
    }

    public final void test_UIGL_4Btn_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_icon, CHILD_VIEW_4, R.id.htcfooter_uigl_dark_icon);
    }

    public final void test_UIGL_1Text_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_text, CHILD_VIEW_1, R.id.htcfooter_uigl_dark_text);
    }

    public final void test_UIGL_2Text_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_text, CHILD_VIEW_2, R.id.htcfooter_uigl_dark_text);
    }

    public final void test_UIGL_3Text_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_text, CHILD_VIEW_3, R.id.htcfooter_uigl_dark_text);
    }

    public final void test_UIGL_4Text_Dark() {
        testUIGL(R.layout.htcfooter_uigl_dark_text, CHILD_VIEW_4, R.id.htcfooter_uigl_dark_text);
    }

    public final void test_UIGL_1Btn_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_icon, CHILD_VIEW_1, R.id.htcfooter_uigl_purelight_icon);
    }

    public final void test_UIGL_2Btn_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_icon, CHILD_VIEW_2, R.id.htcfooter_uigl_purelight_icon);
    }

    public final void test_UIGL_3Btn_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_icon, CHILD_VIEW_3, R.id.htcfooter_uigl_purelight_icon);
    }

    public final void test_UIGL_4Btn_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_icon, CHILD_VIEW_4, R.id.htcfooter_uigl_purelight_icon);
    }

    public final void test_UIGL_1Text_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_text, CHILD_VIEW_1, R.id.htcfooter_uigl_purelight_text);
    }

    public final void test_UIGL_2Text_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_text, CHILD_VIEW_2, R.id.htcfooter_uigl_purelight_text);
    }

    public final void test_UIGL_3Text_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_text, CHILD_VIEW_3, R.id.htcfooter_uigl_purelight_text);
    }

    public final void test_UIGL_4Text_PureLight() {
        testUIGL(R.layout.htcfooter_uigl_purelight_text, CHILD_VIEW_4, R.id.htcfooter_uigl_purelight_text);
    }

    public final void testDisplayModeBottom() {
        testDisplayMode(HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM);
    }

    public final void testDisplayModeRight() {
        testDisplayMode(HtcFooter.DISPLAY_MODE_ALWAYSRIGHT);
    }

    public final void testDisplayModeDefault() {
        testDisplayMode(HtcFooter.DISPLAY_MODE_DEFAULT);
    }

    public void testImproveCoverage() {
        initActivity();
        HtcFooter hf = new HtcFooter(getActivity());
        hf = new HtcFooter(getActivity(), HtcFooter.STYLE_MODE_DEFAULT);
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.htcfooter_layout_weight,
                null);
        hf = (HtcFooter) layout.findViewById(R.id.htcfooter_layout_weight);
        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0);
        hf.dispatchTouchEvent(event);
        hf.setBackgroundStyleMode(HtcFooter.STYLE_MODE_DARK);
        hf.setBackgroundStyleMode(HtcFooter.STYLE_MODE_LIGHT);
        hf.setBackgroundStyleMode(HtcFooter.STYLE_MODE_TRANSPARENT);
        hf.generateLayoutParams(mActivity.getResources().getXml(R.layout.htcfooter_layout_weight));
        hf.getDisplayMode();
        hf.getFooterDefaultProperty(HtcFooter.GET_DEFAULT_HEIGHT);
        hf.getFooterDefaultProperty(HtcFooter.GET_DEFAULT_WIDTH);
        hf.getFooterDefaultProperty(100);
        hf.getOneChildHeight();
        hf.getOneChildWidth();
        hf.setDividerEnabled(true);
        hf.setDividerEnabled(false);
        hf.setOneChildHeight(100);
        hf.setOneChildWidth(100);
        hf.setOneChildHeight(-100);
        hf.setOneChildWidth(-100);
    }

    private void test(int layoutId, int widgetId) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(widgetId), this);
    }

    private void testDisplayMode(final int displayMode) {
        Intent i = new Intent();
        i.putExtra("layoutId", R.layout.htcfooter_layout_weight);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                HtcFooter hf = (HtcFooter) mActivity.findViewById(R.id.htcfooter_layout_weight);
                hf.SetDisplayMode(displayMode);
            }
        });
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(R.id.htcfooter_layout_weight),
                this);
    }

    private void testUIGL(int layoutId, final int count, final int widgetId) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        HtcFooter hf = (HtcFooter) mActivity.findViewById(widgetId);
        final HtcFooterButton hfb1 = (HtcFooterButton) hf.findViewById(R.id.hfb_1);
        final HtcFooterButton hfb2 = (HtcFooterButton) hf.findViewById(R.id.hfb_2);
        final HtcFooterButton hfb3 = (HtcFooterButton) hf.findViewById(R.id.hfb_3);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                switch (count) {
                    case CHILD_VIEW_1:
                        hfb1.setVisibility(View.GONE);
                        hfb2.setVisibility(View.GONE);
                        hfb3.setVisibility(View.GONE);
                        break;
                    case CHILD_VIEW_2:
                        hfb1.setVisibility(View.GONE);
                        hfb2.setVisibility(View.GONE);
                        break;
                    case CHILD_VIEW_3:
                        hfb1.setVisibility(View.GONE);
                        break;
                    case CHILD_VIEW_4:
                        break;
                    default:
                        break;
                }
            }
        });
        getInstrumentation().waitForIdleSync();

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(widgetId), this);
    }
}
