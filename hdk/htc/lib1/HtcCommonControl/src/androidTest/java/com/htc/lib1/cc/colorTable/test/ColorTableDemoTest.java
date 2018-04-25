
package com.htc.lib1.cc.colorTable.test;

import android.content.Intent;

import com.htc.lib1.cc.colorTable.activityhelper.ColorTableDemo;
import com.htc.lib1.cc.test.R;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.NoRunOrientation;
import com.htc.test.util.ScreenShotUtil;

public class ColorTableDemoTest extends HtcActivityTestCaseBase {

    public ColorTableDemoTest() throws ClassNotFoundException {
        super(ColorTableDemo.class);
    }

    @NoRunOrientation
    public void testT0_C0() {
        test("HtcDeviceDefault", "");
    }

    @NoRunOrientation
    public void testT0_C1() {
        test("HtcDeviceDefault", "CategoryOne");
    }

    @NoRunOrientation
    public void testT0_C2() {
        test("HtcDeviceDefault", "CategoryTwo");
    }

    @NoRunOrientation
    public void testT0_C3() {
        test("HtcDeviceDefault", "CategoryThree");
    }

    @NoRunOrientation
    public void testT0_C4() {
        test("HtcDeviceDefault", "CategoryFour");
    }

    private void test(String theme, String category) {
        ScreenShotUtil.setOrientationMark(false);
        ScreenShotUtil.setThemeMask(false);
        Intent intent = new Intent();
        intent.putExtra(ColorTableDemo.KEY_THEME_NAME, theme);
        intent.putExtra(ColorTableDemo.KEY_CATEGORY_NAME, category);
        setActivityIntent(intent);
        initActivity();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mActivity.findViewById(R.id.lv), this);
    }
}
