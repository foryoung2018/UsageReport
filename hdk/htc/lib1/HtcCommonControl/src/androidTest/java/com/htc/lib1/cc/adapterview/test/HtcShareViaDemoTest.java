
package com.htc.lib1.cc.adapterview.test;

import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.widget.TextView;

import com.htc.lib1.cc.adapterview.activityhelper.HtcShareViaDemo;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.app.HtcShareActivity;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcShareGridView;
import com.htc.lib1.cc.widget.HtcShareSlidingUpPanelLayout;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class HtcShareViaDemoTest extends HtcActivityTestCaseBase {

    private ActivityMonitor mSessionMonitor;

    public HtcShareViaDemoTest() {
        super(HtcShareViaDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        mSessionMonitor = getInstrumentation().addMonitor(HtcShareActivity.class.getName(), null, false);
    }

    @Override
    protected void tearDown() throws Exception {
        getInstrumentation().removeMonitor(mSessionMonitor);
        super.tearDown();
    }

    public void testShareMoreItemsCollapsed() {
        startShareActivityAndScreenShot(HtcShareViaDemo.SHARE_MORE_ITEMS, false);
    }

    public void testShareMoreItemsExpanded() {
        startShareActivityAndScreenShot(HtcShareViaDemo.SHARE_MORE_ITEMS, true);
    }

    public void testShareFewItemsCollapsed() {
        startShareActivityAndScreenShot(HtcShareViaDemo.SHARE_FEW_ITEMS, false);
    }

    public void testShareFewItemsExpanded() {
        startShareActivityAndScreenShot(HtcShareViaDemo.SHARE_FEW_ITEMS, true);
    }

    private void startShareActivityAndScreenShot(int item, boolean expanded) {
        startShareActivty(item);
        Activity shareActivity = getInstrumentation().waitForMonitor(mSessionMonitor);
        mSolo.sleep(1000);

        final TextView title = (TextView) shareActivity.findViewById(R.id.title);
        if (expanded) {
            mSolo.clickOnView(title);
        } else {
            getInstrumentation().waitForIdle(new Runnable() {
                public void run() {
                    title.setFocusable(true);
                    title.requestFocus();
                }
            });
        }
        mSolo.sleep(2000);

        HtcShareSlidingUpPanelLayout slidingLayout = (HtcShareSlidingUpPanelLayout) shareActivity.findViewById(R.id.sliding_layout);

        final HtcShareGridView gridView = (HtcShareGridView) shareActivity.findViewById(R.id.gridview);
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                gridView.setVerticalScrollBarEnabled(false);
            }
        });

        mSolo.waitForView(slidingLayout);
        mSolo.sleep(2000);

        ScreenShotUtil.AssertViewEqualBefore(mSolo, slidingLayout, this);
    }

    private void startShareActivty(int item) {
        if (item == HtcShareViaDemo.SHARE_MORE_ITEMS) {
            ((HtcShareViaDemo) mActivity).shareMoreItems(getTheme());
        } else {
            ((HtcShareViaDemo) mActivity).shareFewItems(getTheme());
        }
    }

    private int getTheme() {
        int theme = HtcCommonUtil.BASELINE;
        if ("HtcDeviceDefault.CategoryOne" == mThemeName) {
            theme = HtcCommonUtil.CATEGORYONE;
        } else if ("HtcDeviceDefault.CategoryTwo" == mThemeName) {
            theme = HtcCommonUtil.CATEGORYTWO;
        } else if ("HtcDeviceDefault.CategoryThree" == mThemeName) {
            theme = HtcCommonUtil.CATEGORYTHREE;
        } else {
            theme = HtcCommonUtil.BASELINE;
        }
        return theme;
    }
}
