package com.htc.lib1.cc.colorTable.test.publicresid;

import com.htc.lib1.cc.colorTable.activityhelper.publicresid.PublicResIDDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.NoRunOrientation;
import com.htc.test.util.ScreenShotUtil;

public class PublicResIdTest extends HtcActivityTestCaseBase {

    public PublicResIdTest() throws ClassNotFoundException {
        super(PublicResIDDemo.class);
    }

    @NoRunOrientation
    public void testPublicResID() {
        ScreenShotUtil.setOrientationMark(false);
        ScreenShotUtil.setThemeMask(false);
        initActivity();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView("lv"), this);
    }

}
