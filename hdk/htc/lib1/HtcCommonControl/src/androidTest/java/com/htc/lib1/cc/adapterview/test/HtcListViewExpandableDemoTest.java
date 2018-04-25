
package com.htc.lib1.cc.adapterview.test;

import com.htc.lib1.cc.adapterview.activityhelper.HtcListViewExpandableDemo;
import com.htc.test.HtcActivityTestCase;

public class HtcListViewExpandableDemoTest extends
        HtcActivityTestCase {
    public HtcListViewExpandableDemoTest() {
        super(HtcListViewExpandableDemo.class);
    }

    public final void testOnCreateBundle() {
        initActivity();
        assertNotNull(mActivity);
        mSolo.clickLongOnText("People Names");
        mSolo.clickOnText("People Names");
        mSolo.clickOnText("Dog Names");
        mSolo.clickOnText("Cat Names");
        mSolo.clickOnText("Fish Names");
        mSolo.clickOnText("Barry");
        mSolo.clickOnText("Chuck");
    }

    public final void testImproveCoverage() {
        initActivity();
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((HtcListViewExpandableDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
