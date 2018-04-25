
package com.htc.lib1.cc.quicktipswidget.test;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.htc.lib1.cc.widget.quicktips.PopupBubbleWindow;
import com.htc.lib1.cc.widget.quicktips.PopupBubbleWindow.OnUserDismissListener;
import com.htc.lib1.cc.widget.quicktips.QuickTipPopup;
import com.htc.lib1.cc.widget.quicktips.RotateRelativeLayout;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.quicktipswidget.test.util.QuickTipsUtil;
import com.htc.test.HtcActivityTestCaseBase;

public class QuickTipsDemoTest extends HtcActivityTestCaseBase {

    private static final int MIN = 300;
    private static final int MAX = 2000;

    public QuickTipsDemoTest() throws ClassNotFoundException {
        super(Class.forName("com.htc.lib1.cc.quicktipswidget.activityhelper.QuickTipsDemo"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    @Override
    protected void tearDown() throws Exception {
        if(null != QuickTipsUtil.mTips){
            QuickTipsUtil.mTips.dismiss();
        }
        super.tearDown();
    }

    public void testABL_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_left, MAX, MAX, true, mSolo, this);
    }

    public void testABL_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_left, MAX, MAX, false, mSolo, this);
    }

    public void testABL_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_left, MAX, MIN, true, mSolo, this);
    }

    public void testABL_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_left, MAX, MIN, false, mSolo, this);
    }

    public void testABL_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_left, MIN, MAX, true, mSolo, this);
    }

    public void testABL_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_left, MIN, MAX, false, mSolo, this);
    }

    public void testABL_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_left, MIN, MIN, true, mSolo, this);
    }

    public void testABL_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_left, MIN, MIN, false, mSolo, this);
    }

    public void testABC_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_center, MAX, MAX, true, mSolo, this);
    }

    public void testABC_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_center, MAX, MAX, false, mSolo, this);
    }

    public void testABC_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_center, MAX, MIN, true, mSolo, this);
    }

    public void testABC_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_center, MAX, MIN, false, mSolo, this);
    }

    public void testABC_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_center, MIN, MAX, true, mSolo, this);
    }

    public void testABC_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_center, MIN, MAX, false, mSolo, this);
    }

    public void testABC_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_center, MIN, MIN, true, mSolo, this);
    }

    public void testABC_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_center, MIN, MIN, false, mSolo, this);
    }

    public void testABR_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_right, MAX, MAX, true, mSolo, this);
    }

    public void testABR_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_right, MAX, MAX, false, mSolo, this);
    }

    public void testABR_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_right, MAX, MIN, true, mSolo, this);
    }

    public void testABR_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_right, MAX, MIN, false, mSolo, this);
    }

    public void testABR_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_right, MIN, MAX, true, mSolo, this);
    }

    public void testABR_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_right, MIN, MAX, false, mSolo, this);
    }

    public void testABR_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_right, MIN, MIN, true, mSolo, this);
    }

    public void testABR_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.action_btn_right, MIN, MIN, false, mSolo, this);
    }

    public void testBTL_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_left, MAX, MAX, true, mSolo, this);
    }

    public void testBTL_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_left, MAX, MAX, false, mSolo, this);
    }

    public void testBTL_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_left, MAX, MIN, true, mSolo, this);
    }

    public void testBTL_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_left, MAX, MIN, false, mSolo, this);
    }

    public void testBTL_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_left, MIN, MAX, true, mSolo, this);
    }

    public void testBTL_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_left, MIN, MAX, false, mSolo, this);
    }

    public void testBTL_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_left, MIN, MIN, true, mSolo, this);
    }

    public void testBTL_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_left, MIN, MIN, false, mSolo, this);
    }

    public void testBTC_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_center, MAX, MAX, true, mSolo, this);
    }

    public void testBTC_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_center, MAX, MAX, false, mSolo, this);
    }

    public void testBTC_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_center, MAX, MIN, true, mSolo, this);
    }

    public void testBTC_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_center, MAX, MIN, false, mSolo, this);
    }

    public void testBTC_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_center, MIN, MAX, true, mSolo, this);
    }

    public void testBTC_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_center, MIN, MAX, false, mSolo, this);
    }

    public void testBTC_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_center, MIN, MIN, true, mSolo, this);
    }

    public void testBTC_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_center, MIN, MIN, false, mSolo, this);
    }

    public void testBTR_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_right, MAX, MAX, true, mSolo, this);
    }

    public void testBTR_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_right, MAX, MAX, false, mSolo, this);
    }

    public void testBTR_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_right, MAX, MIN, true, mSolo, this);
    }

    public void testBTR_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_right, MAX, MIN, false, mSolo, this);
    }

    public void testBTR_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_right, MIN, MAX, true, mSolo, this);
    }

    public void testBTR_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_right, MIN, MAX, false, mSolo, this);
    }

    public void testBTR_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_right, MIN, MIN, true, mSolo, this);
    }

    public void testBTR_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_top_right, MIN, MIN, false, mSolo, this);
    }

    public void testBCL_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_left, MAX, MAX, true, mSolo, this);
    }

    public void testBCL_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_left, MAX, MAX, false, mSolo, this);
    }

    public void testBCL_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_left, MAX, MIN, true, mSolo, this);
    }

    public void testBCL_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_left, MAX, MIN, false, mSolo, this);
    }

    public void testBCL_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_left, MIN, MAX, true, mSolo, this);
    }

    public void testBCL_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_left, MIN, MAX, false, mSolo, this);
    }

    public void testBCL_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_left, MIN, MIN, true, mSolo, this);
    }

    public void testBCL_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_left, MIN, MIN, false, mSolo, this);
    }

    public void testBCC_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_center, MAX, MAX, true, mSolo, this);
    }

    public void testBCC_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_center, MAX, MAX, false, mSolo, this);
    }

    public void testBCC_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_center, MAX, MIN, true, mSolo, this);
    }

    public void testBCC_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_center, MAX, MIN, false, mSolo, this);
    }

    public void testBCC_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_center, MIN, MAX, true, mSolo, this);
    }

    public void testBCC_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_center, MIN, MAX, false, mSolo, this);
    }

    public void testBCC_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_center, MIN, MIN, true, mSolo, this);
    }

    public void testBCC_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_center, MIN, MIN, false, mSolo, this);
    }

    public void testBCR_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_right, MAX, MAX, true, mSolo, this);
    }

    public void testBCR_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_right, MAX, MAX, false, mSolo, this);
    }

    public void testBCR_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_right, MAX, MIN, true, mSolo, this);
    }

    public void testBCR_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_right, MAX, MIN, false, mSolo, this);
    }

    public void testBCR_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_right, MIN, MAX, true, mSolo, this);
    }

    public void testBCR_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_right, MIN, MAX, false, mSolo, this);
    }

    public void testBCR_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_right, MIN, MIN, true, mSolo, this);
    }

    public void testBCR_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_center_right, MIN, MIN, false, mSolo, this);
    }

    public void testBBL_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_left, MAX, MAX, true, mSolo, this);
    }

    public void testBBL_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_left, MAX, MAX, false, mSolo, this);
    }

    public void testBBL_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_left, MAX, MIN, true, mSolo, this);
    }

    public void testBBL_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_left, MAX, MIN, false, mSolo, this);
    }

    public void testBBL_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_left, MIN, MAX, true, mSolo, this);
    }

    public void testBBL_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_left, MIN, MAX, false, mSolo, this);
    }

    public void testBBL_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_left, MIN, MIN, true, mSolo, this);
    }

    public void testBBL_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_left, MIN, MIN, false, mSolo, this);
    }

    public void testBBC_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_center, MAX, MAX, true, mSolo, this);
    }

    public void testBBC_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_center, MAX, MAX, false, mSolo, this);
    }

    public void testBBC_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_center, MAX, MIN, true, mSolo, this);
    }

    public void testBBC_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_center, MAX, MIN, false, mSolo, this);
    }

    public void testBBC_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_center, MIN, MAX, true, mSolo, this);
    }

    public void testBBC_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_center, MIN, MAX, false, mSolo, this);
    }

    public void testBBC_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_center, MIN, MIN, true, mSolo, this);
    }

    public void testBBC_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_center, MIN, MIN, false, mSolo, this);
    }

    public void testBBR_Widthmax_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_right, MAX, MAX, true, mSolo, this);
    }

    public void testBBR_Widthmax_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_right, MAX, MAX, false, mSolo, this);
    }

    public void testBBR_Widthmax_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_right, MAX, MIN, true, mSolo, this);
    }

    public void testBBR_Widthmax_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_right, MAX, MIN, false, mSolo, this);
    }

    public void testBBR_Widthmin_Heightmax_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_right, MIN, MAX, true, mSolo, this);
    }

    public void testBBR_Widthmin_Heightmax_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_right, MIN, MAX, false, mSolo, this);
    }

    public void testBBR_Widthmin_Heightmin_Anchor() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_right, MIN, MIN, true, mSolo, this);
    }

    public void testBBR_Widthmin_Heightmin_Parent() {
        QuickTipsUtil.test(mActivity, R.id.btn_bottom_right, MIN, MIN, false, mSolo, this);
    }

    private QuickTipPopup mTips;

    public void testIncreaseCoverageQuickTip() {
        final View anchor = mSolo.getView(R.id.btn_center_right);
        QuickTipsUtil.initQuickTipPopup(mActivity, MIN, MIN, false);
        mTips = QuickTipsUtil.mTips;
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                anchor.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTips.showAsDropDown(anchor);
                        mTips.setExpandDirection(PopupBubbleWindow.EXPAND_LEFT);
                    }
                });
            }
        });
        getInstrumentation().waitForIdleSync();
        mSolo.clickOnView(anchor);
        mSolo.sleep(2000);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mTips.setOrientation(QuickTipPopup.SCREEN_MODE_PORTRAIT, anchor);
                mTips.setOrientation(QuickTipPopup.SCREEN_MODE_LANDSCAPE, anchor);
                mTips.setOrientation(QuickTipPopup.SCREEN_MODE_IPORTRAIT, anchor);
                mTips.setOrientation(QuickTipPopup.SCREEN_MODE_ILANDSCAPE, anchor);
                mTips.update(anchor, 0, 0);
                mTips.setOnUserDismissListener(new OnUserDismissListener() {
                    @Override
                    public void onDismiss() {
                    }
                });
                View v = mSolo.getView(R.id.quicktip_close_text);
                v.performClick();
                anchor.performClick();
                v = mSolo.getView(R.id.quicktip_close_image);
                v.performClick();
            }
        });
    }

    public void testIncreaseCoveragePopupBubbleWindow() {
        final View anchor = mSolo.getView(R.id.btn_center_right);
        final TextView tv = new TextView(mActivity);
        tv.setText(R.string.text_short_string);
        PopupBubbleWindow win = new PopupBubbleWindow(mActivity);
        win = new PopupBubbleWindow(tv);
        win = new PopupBubbleWindow(tv, MIN, MIN);
        win = new PopupBubbleWindow(tv, MIN, MIN, false);
        win.getMaxAvailableHeight(anchor, 0, true);
        win.getAnimationStyle();
        win.getContentView();
        win.getExpandDirection();
        win.getInputMethodMode();
        win.getMaxAvailableHeight(anchor);
        win.getMaxAvailableHeight(anchor, 0);
        win.getMaxAvailableHeight(anchor, 0, true);
        win.getSoftInputMode();
        win.getWidth();
        win.getHeight();
        win.getWindowLayoutType();
        win.isAboveAnchor();
        win.isClippingEnabled();
        win.isFocusable();
        win.isLayoutInScreenEnabled();
        win.isOutsideTouchable();
        win.isTouchable();
        win.setAnimationListener(null);
        win.setAnimationStyle(0);
        win.setBackgroundDrawable(null);
        win.setIgnoreCheekPress();
        win.setInputMethodMode(0);
        win.setLayoutInScreenEnabled(true);
        win.setOnDismissListener(new OnUserDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        win.setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        win.setSoftInputMode(0);
        win.setSplitTouchEnabled(true);
        win.setTouchInterceptor(null);
        win.setTouchable(true);
        win.setTriangleBackgroundDrawable(null);
        win.setTriangleOffset(0);
        win.setWindowLayoutMode(0, 0);
        win.setWindowLayoutType(0);
        win.update();
        win.update(0, 0, MIN, MIN);
        win.update(anchor, 0, 0, MIN, MIN);
        win.update(0, 0, MIN, MIN, true);
        final CustomWindow cw = new CustomWindow(getActivity());
    }

    private class CustomWindow extends PopupBubbleWindow {
        public CustomWindow(Context context) {
            super(context);
            checkContentWidthLimit(0);
        }
    }

    public void testIncreaseCoverageRotateRelativeLayout() {
        final RotateRelativeLayout layout = new RotateRelativeLayout(mActivity);
        layout.getRotation();
        final long time = SystemClock.uptimeMillis();
        final MotionEvent event = MotionEvent.obtain(time, time, MotionEvent.ACTION_UP, 0, 0, 0);
        layout.dispatchTouchEvent(event);
        layout.dispatchTrackballEvent(event);
    }
}
