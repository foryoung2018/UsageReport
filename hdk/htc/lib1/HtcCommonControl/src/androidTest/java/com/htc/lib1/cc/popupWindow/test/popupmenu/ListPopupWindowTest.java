
package com.htc.lib1.cc.popupWindow.test.popupmenu;

import android.test.TouchUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.htc.lib1.cc.widget.ListPopupWindow;
import com.htc.lib1.cc.popupWindow.activityhelper.popupmenu.ListPopupWindowDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.robotium.solo.Solo;

public class ListPopupWindowTest extends HtcActivityTestCaseBase {
    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    @Override
    protected void tearDown() throws Exception {
        final ListPopupWindowDemo instance = (ListPopupWindowDemo) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                instance.dismissWindow();
            }
        });
        super.tearDown();
    }

    public ListPopupWindowTest() {
        super(ListPopupWindowDemo.class);
    }

    public final void test_lh() {//lh -- large height
        View[] multView = getRootView(false);
        assertNotNull(multView);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }

    public final void test_sh() {//sh -- small height
        View[] multView = getRootView(true);
        assertNotNull(multView);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }

    public final void testLayout() {
        ListPopupWindow lpw = new ListPopupWindow(mActivity);
        lpw.setContentWidth(100);
        lpw.setHeight(100);
        lpw.setHorizontalOffset(10);
        lpw.onGlobalLayout();
    }

    public final void testOnGlobalLayout() {
        View item = ((ListPopupWindowDemo) mActivity).getActionBarItemView();
        mSolo.clickOnView(item);
        getInstrumentation().waitForIdleSync();
        int orien = getOrientation();
        if (orien == Solo.PORTRAIT) {
            mSolo.setActivityOrientation(Solo.LANDSCAPE);
        } else {
            mSolo.setActivityOrientation(Solo.PORTRAIT);
        }
        getInstrumentation().waitForIdleSync();
    }

    private View[] getRootView(boolean isFontView) {
        assertNotNull(mActivity);
        View v;
        if (isFontView) {
            v = ((ListPopupWindowDemo) mActivity).getActionBarItemViewFont();
        } else {
            v = ((ListPopupWindowDemo) mActivity).getActionBarItemView();
        }
        TouchUtils.clickView(this, v);

        final View itemView = mSolo.getView(TextView.class, 0);

        if (null != itemView) {
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    mSolo.getView(ListView.class, 0).setVerticalScrollBarEnabled(false);
                }
            });
            View[] viewArray = {
                    v, itemView.getRootView()
            };
            return viewArray;
        } else {
            return null;
        }
    }
}
