
package com.htc.lib1.cc.popupWindow.test.popupmenu;

import android.test.TouchUtils;
import android.view.View;

import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.popupWindow.activityhelper.popupmenu.ListPopupMenuDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class ListPopupMenuTest extends HtcActivityTestCaseBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    @Override
    protected void tearDown() throws Exception {
        final ListPopupMenuDemo instance = (ListPopupMenuDemo) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                instance.dismissWindow();
            }
        });
        super.tearDown();
    }

    public ListPopupMenuTest() {
        super(ListPopupMenuDemo.class);
    }

    public final void testTop() {
        assertNotNull(mActivity);
        View v;
        v = mActivity.findViewById(R.id.keep_show_btn);
        TouchUtils.clickView(this, v);

        final View itemView = mSolo.getView(HtcListItem.class, 0);
        View rootView = itemView.getRootView();
        View[] multView = {
                v, rootView
        };
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }

    public final void testBottom() {
        View v = null;
        v = mActivity.findViewById(R.id.btn);
        TouchUtils.clickView(this, v);

        final View itemView = mSolo.getView(HtcListItem.class, 0);
        View rootView = itemView.getRootView();
        View[] multView = {
                v, rootView
        };
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }
}
