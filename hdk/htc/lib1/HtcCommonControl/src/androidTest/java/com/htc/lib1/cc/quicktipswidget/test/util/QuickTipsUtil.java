
package com.htc.lib1.cc.quicktipswidget.test.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.FrameLayout;

import com.htc.lib1.cc.widget.quicktips.QuickTipPopup;
import com.htc.lib1.cc.test.R;
import com.htc.test.util.Lock;
import com.htc.test.util.ScreenShotUtil;
import com.robotium.solo.Solo;

public class QuickTipsUtil {
    public static QuickTipPopup mTips;
    private static final int MIN = 300;
    private static final int MAX = 2000;
    private static final int QUICK_TIP_ALPHA = 80;
    private static final int CENTER = Gravity.CENTER;
    private static final int TOP = Gravity.TOP;
    private static Resources mResources;
    private static boolean mIsFromAnchor = true;

    public static void test(Activity act, int id, final boolean isFromAnchor, final boolean aboveAnchor, final boolean isLandScape,
            Solo solo,
            ActivityInstrumentationTestCase2 testCase, final int gravity) {
        final View anchor = act.findViewById(id);
        initQuickTipPopup(act, isFromAnchor, aboveAnchor, isLandScape);
        initShow(solo, testCase, anchor, mIsFromAnchor, CENTER);
        solo.sleep(2000);
        MobileLocation(testCase, anchor, solo, gravity);
        AssertMultipleView(testCase, anchor, solo);
    }

    private static void initShow(Solo solo,
            ActivityInstrumentationTestCase2 testCase, final View anchor, final boolean isFromAnchor, final int postion) {
        testCase.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                anchor.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFromAnchor) {
                            mTips.showAsDropDown(anchor);
                        } else {
                            mTips.showAtLocation((View) anchor.getParent(), postion, 100, 100);
                        }
                    }
                });
            }
        });

        testCase.getInstrumentation().waitForIdleSync();
        solo.clickOnView(anchor);
        testCase.getInstrumentation().waitForIdleSync();

    }

    private static void MobileLocation(ActivityInstrumentationTestCase2 testCase, final View anchor, Solo solo, final int gravity) {
        final Lock lock = new Lock();
        anchor.addOnLayoutChangeListener(new OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                lock.unlockAndNotify();
            }
        });
        testCase.getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                if (mIsFromAnchor) {
                    ((FrameLayout.LayoutParams) anchor.getLayoutParams()).gravity = gravity;
                    anchor.requestLayout();
                }
            }
        });
        mTips.updatePosition(anchor);
        lock.waitUnlock(3000);
    }

    private static void AssertMultipleView(ActivityInstrumentationTestCase2 testCase, final View anchor, Solo solo) {
        View[] multiView = {
                anchor,
                mTips.getContentView().getRootView()
        };
        solo.sleep(2000);
        ScreenShotUtil.AssertMultipleViewEqualBefore(solo, multiView, testCase, testCase.getInstrumentation());
        testCase.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                if (mTips.isShowing()) {
                    mTips.dismiss();
                }
            }
        });
    }

    public static void test(Activity act, int id, int width, int height, final boolean isFromAchor, Solo solo,
            ActivityInstrumentationTestCase2 testCase) {
        final View anchor = act.findViewById(id);
        initQuickTipPopup(act, width, height, isFromAchor);
        initShow(solo, testCase, anchor, isFromAchor, TOP);
        AssertMultipleView(testCase, anchor, solo);
    }

    private static void initQuickTipPopup(Activity act, boolean isFromAnchor, boolean isAboveAnchor, final boolean isLandScape) {
        mTips = new QuickTipPopup(act);
        mIsFromAnchor = (act.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && isLandScape) ? false : isFromAnchor;
        initQuickTipPopup(act, MAX, MIN, mIsFromAnchor);
        if (mIsFromAnchor) {
            setExpandDirection(mTips, isAboveAnchor);
        }
    }

    public static void initQuickTipPopup(Activity act, int width, int height, boolean isFromAnchor) {
        mTips = new QuickTipPopup(act);
        mResources = act.getResources();
        initQuickTipHeight(mResources, mTips, width, height);
        if (isFromAnchor) {
            mTips.setBackgroundColor(mResources.getColor(android.R.color.holo_red_dark));
            mTips.setCloseVisibility(false);
        } else {
            mTips.setBackgroundAlpha(QUICK_TIP_ALPHA);
            mTips.setBackgroundColor(mResources.getColor(android.R.color.holo_orange_light));
            mTips.setCloseVisibility(true);
        }
    }

    private static void initQuickTipHeight(Resources res, QuickTipPopup tip, int width, int height) {
        if (height == MIN && width == MIN) {
            tip.setText(res.getString(R.string.text_short_string));
        } else if (height == MIN && width == MAX) {
            tip.setMaxWidth(MAX);
            tip.setText(res.getString(R.string.text_short_string));
        } else if (height == MAX && width == MIN) {
            tip.setMaxWidth(MIN);
            tip.setText(res.getString(R.string.text_long_string));
        } else {
            tip.setImage(res.getDrawable(R.drawable.ic_launcher));
            tip.setText(res.getString(R.string.text_long_string));
        }
    }

    private static void setExpandDirection(QuickTipPopup tip, boolean aboveAnchor) {
        if (tip != null && !aboveAnchor) tip.setExpandDirection(QuickTipPopup.EXPAND_UP);
        if (tip != null && aboveAnchor) tip.setExpandDirection(QuickTipPopup.EXPAND_DOWN);
    }
}
