
package com.htc.lib1.cc.htclistitem.test;

import android.content.Intent;
import android.util.Log;

import com.htc.lib1.cc.htclistitem.activityhelper.HtcListItemActivity1;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItemBubbleCount;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

import junit.framework.TestCase;

public class HtcListItemTestCase extends HtcActivityTestCaseBase {

    public HtcListItemTestCase() {
        super(HtcListItemActivity1.class);
    }

    public HtcListItemTestCase(Class activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public final void testLeftIndent() {
        scrollTo(30, false);
        leftIndentAssert();
    }

    public final void testLeftIndentAutomotiveMode() {
        scrollTo(30, true);
        leftIndentAssert();
    }

    public final void testItem0() {
        scrollToAndScreenShotTest(0, this, false);
    }

    public final void testItem0AutomotiveMode() {
        scrollToAndScreenShotTest(0, this, true);
    }

    public final void testItem1() {
        scrollToAndScreenShotTest(1, this, false);
    }

    public final void testItem1AutomotiveMode() {
        scrollToAndScreenShotTest(1, this, true);
    }

    public final void testItem2() {
        scrollToAndScreenShotTest(2, this, false);
    }

    public final void testItem2AutomotiveMode() {
        scrollToAndScreenShotTest(2, this, true);
    }

    public final void testItem3() {
        scrollToAndScreenShotTest(3, this, false);
    }

    public final void testItem3AutomotiveMode() {
        scrollToAndScreenShotTest(3, this, true);
    }

    public final void testItem4() {
        scrollToAndScreenShotTest(4, this, false);
    }

    public final void testItem4AutomotiveMode() {
        scrollToAndScreenShotTest(4, this, true);
    }

    public final void testItem5() {
        scrollToAndScreenShotTest(5, this, false);
    }

    public final void testItem5AutomotiveMode() {
        scrollToAndScreenShotTest(5, this, true);
    }

    public final void testItem6() {
        scrollToAndScreenShotTest(6, this, false);
    }

    public final void testItem6AutomotiveMode() {
        scrollToAndScreenShotTest(6, this, true);
    }

    public final void testItem7() {
        scrollToAndScreenShotTest(7, this, false);
    }

    public final void testItem7AutomotiveMode() {
        scrollToAndScreenShotTest(7, this, true);
    }

    public final void testItem8() {
        scrollToAndScreenShotTest(8, this, false);
    }

    public final void testItem8AutomotiveMode() {
        scrollToAndScreenShotTest(8, this, true);
    }

    public final void testItem9() {
        scrollToAndScreenShotTest(9, this, false);
    }

    public final void testItem9AutomotiveMode() {
        scrollToAndScreenShotTest(9, this, true);
    }

    public final void testItem10() {
        scrollToAndScreenShotTest(10, this, false);
    }

    public final void testItem10AutomotiveMode() {
        scrollToAndScreenShotTest(10, this, true);
    }

    public final void testItem11() {
        scrollToAndScreenShotTest(11, this, false);
    }

    public final void testItem11AutomotiveMode() {
        scrollToAndScreenShotTest(11, this, true);
    }

    public final void testItem12() {
        scrollToAndScreenShotTest(12, this, false);
    }

    public final void testItem12AutomotiveMode() {
        scrollToAndScreenShotTest(12, this, true);
    }

    public final void testItem13() {
        scrollToAndScreenShotTest(13, this, false);
    }

    public final void testItem13AutomotiveMode() {
        scrollToAndScreenShotTest(13, this, true);
    }

    public final void testItem14() {
        scrollToAndScreenShotTest(14, this, false);
    }

    public final void testItem14AutomotiveMode() {
        scrollToAndScreenShotTest(14, this, true);
    }

    public final void testItem15() {
        scrollToAndScreenShotTest(15, this, false);
    }

    public final void testItem15AutomotiveMode() {
        scrollToAndScreenShotTest(15, this, true);
    }

    public final void testItem16() {
        scrollToAndScreenShotTest(16, this, false);
    }

    public final void testItem16AutomotiveMode() {
        scrollToAndScreenShotTest(16, this, true);
    }

    public final void testItem17() {
        scrollToAndScreenShotTest(17, this, false);
    }

    public final void testItem17AutomotiveMode() {
        scrollToAndScreenShotTest(17, this, true);
    }

    public final void testItem18() {
        scrollToAndScreenShotTest(18, this, false);
    }

    public final void testItem18AutomotiveMode() {
        scrollToAndScreenShotTest(18, this, true);
    }

    public final void testItem19() {
        scrollToAndScreenShotTest(19, this, false);
    }

    public final void testItem19AutomotiveMode() {
        scrollToAndScreenShotTest(19, this, true);
    }

    public final void testItem20() {
        scrollToAndScreenShotTest(20, this, false);
    }

    public final void testItem20AutomotiveMode() {
        scrollToAndScreenShotTest(20, this, true);
    }

    public final void testItem21() {
        scrollToAndScreenShotTest(21, this, false);
    }

    public final void testItem21AutomotiveMode() {
        scrollToAndScreenShotTest(21, this, true);
    }

    public final void testItem22() {
        scrollToAndScreenShotTest(22, this, false);
    }

    public final void testItem22AutomotiveMode() {
        scrollToAndScreenShotTest(22, this, true);
    }

    public final void testItem23() {
        scrollToAndScreenShotTest(23, this, false);
    }

    public final void testItem23AutomotiveMode() {
        scrollToAndScreenShotTest(23, this, true);
    }

    public final void testItem24() {
        scrollToAndScreenShotTest(24, this, false);
    }

    public final void testItem24AutomotiveMode() {
        scrollToAndScreenShotTest(24, this, true);
    }

    public final void testItem25() {
        scrollToAndScreenShotTest(25, this, false);
    }

    public final void testItem25AutomotiveMode() {
        scrollToAndScreenShotTest(25, this, true);
    }

    public final void testItem26() {
        scrollToAndScreenShotTest(26, this, false);
    }

    public final void testItem26AutomotiveMode() {
        scrollToAndScreenShotTest(26, this, true);
    }

    public final void testItem27() {
        scrollToAndScreenShotTest(27, this, false);
    }

    public final void testItem27AutomotiveMode() {
        scrollToAndScreenShotTest(27, this, true);
    }

    public final void testItem28() {
        scrollToAndScreenShotTest(28, this, false);
    }

    public final void testItem28AutomotiveMode() {
        scrollToAndScreenShotTest(28, this, true);
    }

    public final void testItem29() {
        scrollToAndScreenShotTest(29, this, false);
    }

    public final void testItem29AutomotiveMode() {
        scrollToAndScreenShotTest(29, this, true);
    }

    public final void testItem30() {
        scrollToAndScreenShotTest(30, this, false);
    }

    public final void testItem30AutomotiveMode() {
        scrollToAndScreenShotTest(30, this, true);
    }

    public final void testItem31() {
        scrollToAndScreenShotTest(31, this, false);
    }

    public final void testItem31AutomotiveMode() {
        scrollToAndScreenShotTest(31, this, true);
    }

    public final void testItem32() {
        scrollToAndScreenShotTest(32, this, false);
    }

    public final void testItem32AutomotiveMode() {
        scrollToAndScreenShotTest(32, this, true);
    }

    public final void testItem33() {
        scrollToAndScreenShotTest(33, this, false);
    }

    public final void testItem33AutomotiveMode() {
        scrollToAndScreenShotTest(33, this, true);
    }

    public final void testItem34() {
        scrollToAndScreenShotTest(34, this, false);
    }

    public final void testItem34AutomotiveMode() {
        scrollToAndScreenShotTest(34, this, true);
    }

    public final void testItem35() {
        scrollToAndScreenShotTest(35, this, false);
    }

    public final void testItem36() {
        scrollToAndScreenShotTest(36, this, false);
    }

    public final void testItem37() {
        scrollToAndScreenShotTest(37, this, false);
    }

    public final void testItem38() {
        scrollToAndScreenShotTest(38, this, false);
    }

    public final void testItem39() {
        scrollToAndScreenShotTest(39, this, false);
    }

    public final void testItem40() {
        scrollToAndScreenShotTest(40, this, false);
    }

    public final void testItem41() {
        scrollToAndScreenShotTest(41, this, false);
    }

    public final void testItem42() {
        scrollToAndScreenShotTest(42, this, false);
    }

    public final void testItem43() {
        scrollToAndScreenShotTest(43, this, false);
    }

    public final void testItem44() {
        scrollToAndScreenShotTest(44, this, false);
    }

    public final void testItem45() {
        scrollToAndScreenShotTest(45, this, false);
    }

    public final void testItem46() {
        scrollToAndScreenShotTest(46, this, false);
    }

    public final void testItem1LineCenteredText() {
        scrollToAndScreenShotTest(47, this, false);
    }

    public final void testItemLabeledLayout() {
        scrollToAndScreenShotTest(48, this, false);
    }

    public final void testResetBubbleCount() {
        scrollToAndScreenShotTest(6, this, false, true);
    }

    protected void scrollToAndScreenShotTest(final int itemIndex, TestCase testcase, final boolean isAutomotiveMode) {
        scrollToAndScreenShotTest(itemIndex, testcase, isAutomotiveMode, false);
    }

    protected void scrollToAndScreenShotTest(final int itemIndex, TestCase testcase, final boolean isAutomotiveMode, boolean isResetBubbleCount) {
        scrollTo(itemIndex, isAutomotiveMode);
        final HtcListItem item = (HtcListItem) getActivity().findViewById(android.R.id.list);
        if (isResetBubbleCount) {
            try {
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) item
                                .findViewById(R.id.bubble);
                        bubble.setBubbleCount(0);
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        ScreenShotUtil.AssertViewEqualBefore(mSolo, item, this);
    }

    protected void scrollTo(final int itemIndex, final boolean isAutomotiveMode) {
        Intent intent = new Intent();
        intent.putExtra("itemIndex", itemIndex);
        intent.putExtra("isAutomotiveMode", isAutomotiveMode);
        setActivityIntent(intent);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
    }

    protected void leftIndentAssert() {
        int M1 = (int) getActivity().getResources().getDimension(R.dimen.margin_l);
        int M2 = (int) getActivity().getResources().getDimension(R.dimen.margin_m);
        Log.e("HtcListItemTestCase", "get view");
        final HtcListItem item = (HtcListItem) getActivity().findViewById(android.R.id.list);
        HtcListItem2LineText text = (HtcListItem2LineText) item.findViewById(R.id.text1);
        assertEquals(M1 + M2 * 3, text.getLeft());
    }

    public void testImproveCoverage() {
        initActivity();
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((HtcListItemActivity1) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
