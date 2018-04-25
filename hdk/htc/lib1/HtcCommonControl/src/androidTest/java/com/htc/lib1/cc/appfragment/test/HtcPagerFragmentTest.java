
package com.htc.lib1.cc.appfragment.test;

import android.util.DisplayMetrics;

import com.htc.lib1.cc.appfragment.activityhelper.HtcPagerFragmentDemo;
import com.htc.lib1.cc.appfragment.activityhelper.MyHtcPagerFragment;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class HtcPagerFragmentTest extends HtcActivityTestCaseBase {

    public HtcPagerFragmentTest() {
        super(HtcPagerFragmentDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testDefaultSnapShot() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView("fragment"), this);
    }

    public void testClickTab0() {
        mSolo.clickOnText(MyHtcPagerFragment.TAB[0]);
        mSolo.sleep(10000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView("fragment"), this);
    }

    public void testScrollToTab2() {
        mSolo.clickOnText(MyHtcPagerFragment.TAB[2]);
        mSolo.sleep(5000);
        mSolo.clickOnText(MyHtcPagerFragment.TAB[1]);
        mSolo.sleep(5000);
        DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        mSolo.drag(width - 10,10 , height-100, height - 100, 30);
        mSolo.sleep(5000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView("fragment"), this);
    }
}
