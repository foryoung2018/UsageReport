
package com.htc.lib1.cc.actionbar.test.unit;

import android.graphics.PixelFormat;
import android.test.UiThreadTest;
import android.test.ViewAsserts;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.actionbar.test.HtcActionBarActivityTestCase;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class ActionBarExtTest extends HtcActionBarActivityTestCase {

    public ActionBarExtTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initActionBarContainers();
    }

    public void testGetCustomContainer() {
        assertNotNull(mActionBar);
        ActionBarContainer actionBarContainer = mActionBarExt.getCustomContainer();
        assertNotNull(actionBarContainer);
        assertTrue(actionBarContainer.equals(mActionBar.getCustomView()));
    }

    @UiThreadTest
    public void testGetSearchContainer() {
        assertNotNull(mActionBar);
        ActionBarContainer actionBarContainer = mActionBarExt.getSearchContainer();
        assertNotNull(actionBarContainer);
        ViewGroup vg = (ViewGroup) getContainerView();
        ViewAsserts.assertGroupContains(vg, actionBarContainer);
    }

    private View getActionBarView() {
        int resId;
        resId = getInstrumentation().getTargetContext().getResources().getIdentifier("action_bar", "id", "android");
        if (resId > 0) return getActivity().findViewById(resId);
        return null;
    }

    private View getContainerView() {
        int resId;
        resId = getInstrumentation().getTargetContext().getResources().getIdentifier("action_bar_container", "id", "android");
        if (resId > 0) return getActivity().findViewById(resId);
        return null;
    }

    @UiThreadTest
    public void testSetFullScreenEnabled() {
        mActionBarExt.setFullScreenEnabled(false);
        assertTrue(PixelFormat.OPAQUE == getActionBarView().getBackground().getOpacity());

        mActionBarExt.setFullScreenEnabled(true);
        assertTrue(PixelFormat.TRANSLUCENT == getActionBarView().getBackground().getOpacity());
    }

    @UiThreadTest
    public void testSetTransparentEnabled() {
        mActionBarExt.setTransparentEnabled(false);
        assertTrue(PixelFormat.OPAQUE == getActionBarView().getBackground().getOpacity());

        mActionBarExt.setTransparentEnabled(true);
        assertTrue(PixelFormat.TRANSLUCENT == getActionBarView().getBackground().getOpacity());
    }

    public void testSwitchContainer() {
        // search container is Gone when init
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                assertTrue(View.GONE == mActionBarExt.getSearchContainer().getVisibility());
                mActionBarExt.switchContainer();
            }
        });

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                assertTrue(View.VISIBLE == mActionBarExt.getSearchContainer().getVisibility());
                mActionBarExt.switchContainer();
            }

        });
    }
}
