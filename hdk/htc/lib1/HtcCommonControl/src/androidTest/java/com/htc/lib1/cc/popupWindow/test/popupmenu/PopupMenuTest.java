
package com.htc.lib1.cc.popupWindow.test.popupmenu;

import android.test.TouchUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.PopupBubbleWindow;
import com.htc.lib1.cc.widget.PopupBubbleWindow.OnDismissListener;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.popupWindow.activityhelper.popupmenu.PopupMenuDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.robotium.solo.Solo;

import junit.framework.Assert;

public class PopupMenuTest extends HtcActivityTestCaseBase {
    public PopupMenuTest() {
        super(PopupMenuDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    @Override
    protected void tearDown() throws Exception {
        final PopupMenuDemo instance = (PopupMenuDemo) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                instance.dismissWindow();
            }
        });
        super.tearDown();
    }

    public final void testTop() {
        testPopupFromButton(R.id.keep_show_btn);
    }

    public final void testBottom() {
        testPopupFromButton(R.id.btn);
    }

    public final void testPopupShowing() {
        final View topBtn = mActivity.findViewById(R.id.keep_show_btn);
        TouchUtils.clickView(this, topBtn);

        PopupBubbleWindow topWindow = ((PopupMenuDemo) mActivity).getTopPopupWindow();
        mSolo.waitForText("listitem7");
        Assert.assertNotNull(topWindow);
        Assert.assertEquals(true, topWindow.isShowing());

        int orientation = (mOrientation == Solo.PORTRAIT) ? Solo.LANDSCAPE : Solo.PORTRAIT;
        mSolo.setActivityOrientation(orientation);

        /* Because of setting android:configChanges="orientation|screenSize" in AUT manifest,
        expected value is true. */
        Assert.assertEquals(true, topWindow.isShowing());
    }

    private void testPopupFromButton(int id) {
        assertNotNull(mActivity);
        View anchor= mActivity.findViewById(id);
        TouchUtils.clickView(this, anchor);
        final HtcListView lv = ((PopupMenuDemo) mActivity).getHtcListViewInWindow();
        View popupWindowRootView = lv.getRootView();
        View activityRootView = anchor.getRootView();
        View[] multView = {
                activityRootView, anchor, popupWindowRootView
        };
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }
    public final void testConstructor() {
        PopupBubbleWindow pbw;
        View view = mActivity.findViewById(R.id.keep_show_btn);
        pbw = new PopupBubbleWindow(view);
        pbw = new PopupBubbleWindow(view, 100, 100);
        pbw = new PopupBubbleWindow(getInstrumentation().getTargetContext());
    }

    public final void testSetApi() {
        PopupBubbleWindow pbw = new PopupBubbleWindow(getInstrumentation().getTargetContext());
        pbw.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.ic_launcher));
        pbw.setTriangleBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.ic_launcher));
        pbw.setBackgroundAlpha(80);
        pbw.setIgnoreCheekPress();
        pbw.setAnimationStyle(-1);
        pbw.setLayoutInScreenEnabled(false);
        pbw.setSplitTouchEnabled(true);
        pbw.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        pbw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
    }

    public final void testGetApi() {
        View view = mActivity.findViewById(R.id.keep_show_btn);
        PopupBubbleWindow pbw = new PopupBubbleWindow(getInstrumentation().getTargetContext());
        pbw.getSoftInputMode();
        pbw.getWindowLayoutType();
        pbw.getExpandDirection();
        pbw.getMaxAvailableHeight(view);
        pbw.getMaxAvailableHeight(view, 10);
        pbw.getAnimationStyle();
    }

    public final void testIsApi() {
        View view = mActivity.findViewById(R.id.keep_show_btn);
        PopupBubbleWindow pbw = new PopupBubbleWindow(getInstrumentation().getTargetContext());
        pbw.isFocusable();
        pbw.isTouchable();
        pbw.isOutsideTouchable();
        pbw.isClippingEnabled();
        pbw.isLayoutInScreenEnabled();
        pbw.isAboveAnchor();
    }

    public final void testListener() {
        PopupBubbleWindow pbw = new PopupBubbleWindow(getInstrumentation().getTargetContext());
        pbw.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        pbw.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
            }
        });
    }
}
