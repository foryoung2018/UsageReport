
package com.htc.lib1.cc.drawer.test;

import android.content.Intent;
import android.view.View;

import com.htc.lib1.cc.drawer.activityhelper.HtcDrawerDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class HtcDrawerTest extends HtcActivityTestCaseBase {

    private static final int TOGGLE_ONCE = 1;
    private static final int TOGGLE_TWICE = 2;

    public HtcDrawerTest() {
        super(HtcDrawerDemo.class);
    }

    public void testTopClose() {
        test(HtcDrawerDemo.MODE_TOP, false, false, TOGGLE_ONCE, false, true, false);
    }

    public void testLeftClose() {
        test(HtcDrawerDemo.MODE_LEFT, false, false, TOGGLE_ONCE, false, true, false);
    }

    public void testRightClose() {
        test(HtcDrawerDemo.MODE_RIGHT, false, false, TOGGLE_ONCE, false, true, false);
    }

    public void testBottomClose() {
        test(HtcDrawerDemo.MODE_BOTTOM, false, false, TOGGLE_ONCE, false, true, false);
    }

    public void testTopOpen() {
        test(HtcDrawerDemo.MODE_TOP, true, false, TOGGLE_ONCE, false, true, false);
    }

    public void testLeftOpen() {
        test(HtcDrawerDemo.MODE_LEFT, true, false, TOGGLE_ONCE, false, true, false);
    }

    public void testRightOpen() {
        test(HtcDrawerDemo.MODE_RIGHT, true, false, TOGGLE_ONCE, false, true, false);
    }

    public void testBottomOpen() {
        test(HtcDrawerDemo.MODE_BOTTOM, true, false, TOGGLE_ONCE, false, true, false);
    }

    public void testBottomOpenWithLinkedView() {
        test(HtcDrawerDemo.MODE_BOTTOM, false, false, TOGGLE_ONCE, false, false, true);
    }

    public void testClickBarToOpen() {
        test(HtcDrawerDemo.MODE_BOTTOM, false, false, TOGGLE_ONCE, true, true, false);
    }

    public void testClickBarToClose() {
        test(HtcDrawerDemo.MODE_BOTTOM, true, false, TOGGLE_ONCE, true, true, false);
    }

    public void testToggleOnce() {
        test(HtcDrawerDemo.MODE_BOTTOM, true, true, TOGGLE_ONCE, false, false, false);
    }

    public void testToggleTwice() {
        test(HtcDrawerDemo.MODE_BOTTOM, true, true, TOGGLE_TWICE, false, false, false);
    }

    private void test(final int mode, boolean isInitOpen, final boolean isNeedToggle,
            final int toggleTimes, final boolean isNeedClick, final boolean isNeedAnimation,
            boolean isNeedLinkedView) {
        final Intent intent = new Intent();
        intent.putExtra(HtcDrawerDemo.MODE, mode);
        intent.putExtra(HtcDrawerDemo.IS_INIT_OPEN, isInitOpen);
        intent.putExtra(HtcDrawerDemo.IS_NEED_LINKED_VIEW, isNeedLinkedView);
        setActivityIntent(intent);
        initActivity();
        mSolo.sleep(1000);

        if (isNeedToggle) {
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < toggleTimes; i++) {
                        ((HtcDrawerDemo) mActivity).mDrawer.toggle(false);
                    }
                }
            });
            mSolo.sleep(2000);
        }

        if (isNeedClick) {
            final View bar = ((HtcDrawerDemo) mActivity).mDrawer.findViewById(android.R.id.toggle);
            mSolo.clickLongOnView(bar);
            mSolo.sleep(2000);
        }

        ScreenShotUtil.AssertViewEqualBefore(mSolo, mActivity.findViewById(android.R.id.content),
                this);
    }
}
