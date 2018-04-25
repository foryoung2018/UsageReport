
package com.htc.lib1.cc.quicktipswidget.test;

import android.view.Gravity;

import com.htc.lib1.cc.quicktipswidget.activityhelper.QuickTipsDemo;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.quicktipswidget.test.util.QuickTipsUtil;
import com.htc.test.HtcActivityTestCaseBase;

public class QuickTipsTest extends HtcActivityTestCaseBase {

    private static final int MARGIN = 300;

    public QuickTipsTest() {
        super(QuickTipsDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    @Override
    protected void tearDown() throws Exception {
        if (null != QuickTipsUtil.mTips) {
            QuickTipsUtil.mTips.dismiss();
        }
        super.tearDown();
    }

    public void testLT_Anchor() {
        test(R.id.btn_top_left, true, true, false, Gravity.TOP | Gravity.LEFT);
    }

    public void testRT_Anchor() {
        test(R.id.btn_top_left, true, true, false, Gravity.RIGHT | Gravity.TOP);
    }

    public void testLB_Anchor() {
        test(R.id.btn_top_left, true, false, false, Gravity.LEFT | Gravity.BOTTOM);
    }

    public void testRB_Anchor() {
        test(R.id.btn_top_left, true, false, false, Gravity.RIGHT | Gravity.BOTTOM);
    }

    public void testBBR_Anchor() {
        test(R.id.btn_bottom_right, true, false, false, Gravity.BOTTOM | Gravity.RIGHT);
    }

    public void testBBRRT_Anchor() {
        test(R.id.btn_bottom_right, true, true, false, Gravity.RIGHT | Gravity.TOP);
    }

    public void testBBRLB_Anchor() {
        test(R.id.btn_bottom_right, true, false, false, Gravity.LEFT | Gravity.BOTTOM);
    }

    public void testBBRLT_Anchor() {
        test(R.id.btn_bottom_right, true, true, false, Gravity.LEFT | Gravity.TOP);
    }

    public void testBBC_NoAnchor() {
        test(R.id.btn_center_center, false, false, false, Gravity.CENTER);
    }

    public void testBTL() {
        test(R.id.btn_top_left, true, true, true, Gravity.TOP | Gravity.LEFT);
    }

    private void test(int id, final boolean isFromAchor, final boolean aboveAnchor, final boolean isLandScape, final int gravity) {
        QuickTipsUtil.test(mActivity, id, isFromAchor, aboveAnchor, isLandScape, mSolo, this, gravity);
    }

}
