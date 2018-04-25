
package com.htc.lib1.cc.colorTable.test;

import com.htc.lib1.cc.colorTable.activityhelper.DynamicColorTableDemo;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.NoRunOrientation;
import com.htc.test.util.ScreenShotUtil;

import java.lang.reflect.Field;

public class DynamicColorTableDemoTest extends HtcActivityTestCaseBase {

    public DynamicColorTableDemoTest() throws ClassNotFoundException {
        super(DynamicColorTableDemo.class);
    }

    @NoRunOrientation
    public void testBaseLine() {
        test(HtcCommonUtil.BASELINE);
    }

    @NoRunOrientation
    public void testCategoryOne() {
        test(HtcCommonUtil.CATEGORYONE);
    }

    @NoRunOrientation
    public void testCategoryTwo() {
        test(HtcCommonUtil.CATEGORYTWO);
    }

    @NoRunOrientation
    public void testCategoryThree() {
        test(HtcCommonUtil.CATEGORYTHREE);
    }

    private void test(int categoryId) {
        ScreenShotUtil.setOrientationMark(false);
        ScreenShotUtil.setThemeMask(false);
        setNeedRecreate();
        mCategoryId = categoryId;
        setActivityIntent(null);
        initActivity();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mActivity.findViewById(R.id.lv), this);
    }

    private void setNeedRecreate() {
        try {
            final Class c = Class
                    .forName("com.htc.lib1.cc.util.HtcThemeUtils$CommonCategoryResources");
            final Field f = c.getDeclaredField("mNeedRecreate");
            f.setAccessible(true);
            f.setBoolean(null, true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
