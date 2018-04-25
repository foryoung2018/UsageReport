
package com.htc.lib1.cc.overlapLayout.test;

import android.content.Intent;

import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.overlapLayout.activityhelper.HtcOverlapLayoutActivity;
import com.htc.lib1.cc.test.R;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.test.util.ViewUtil;

/**
 * Next is same short words's mean:
 * OTNB    hasTranslucentNavigationBar
 * XTNB    noTranslucentNavigationBar
 * OTSB    hasTranslucentStatusBar
 * XTSB    notranslucentStatusBar
 * OSB     hasStatusBar
 * XSB     noStatusBar
 * OAB     hasActionBar
 * XAB     noActionBar
 * ONB     hasNavigationBar
 * XNB     noNavigationBar
 * OIM     hasInputMethod
 * XIM     noInputMethod
 */
public class OverlapLayoutTest extends HtcActivityTestCaseBase {
    public OverlapLayoutTest() {
        super(HtcOverlapLayoutActivity.class);
    }

    public final void test_OTNB_OTSB_OSB_OAB_ONB_OIM() {
        test(true, true, true, true, true, true);
    }

    public final void test_OTNB_OTSB_OSB_OAB_ONB_XIM() {
        test(true, true, true, true, true, false);
    }

    public final void test_OTNB_OTSB_OSB_OAB_XNB_OIM() {
        test(true, true, true, true, false, true);
    }

    public final void test_OTNB_OTSB_OSB_OAB_XNB_XIM() {
        test(true, true, true, true, false, false);
    }

    public final void test_OTNB_OTSB_OSB_XAB_ONB_OIM() {
        test(true, true, true, false, true, true);
    }

    public final void test_OTNB_OTSB_OSB_XAB_ONB_XIM() {
        test(true, true, true, false, true, false);
    }

    public final void test_OTNB_OTSB_OSB_XAB_XNB_OIM() {
        test(true, true, true, false, false, true);
    }

    public final void test_OTNB_OTSB_OSB_XAB_XNB_XIM() {
        test(true, true, true, false, false, false);
    }

    public final void test_OTNB_OTSB_XSB_OAB_ONB_OIM() {
        test(true, true, false, true, true, true);
    }

    public final void test_OTNB_OTSB_XSB_OAB_ONB_XIM() {
        test(true, true, false, true, true, false);
    }

    public final void test_OTNB_OTSB_XSB_OAB_XNB_OIM() {
        test(true, true, false, true, false, true);
    }

    public final void test_OTNB_OTSB_XSB_OAB_XNB_XIM() {
        test(true, true, false, true, false, false);
    }

    public final void test_OTNB_OTSB_XSB_XAB_ONB_OIM() {
        test(true, true, false, false, true, true);
    }

    public final void test_OTNB_OTSB_XSB_XAB_ONB_XIM() {
        test(true, true, false, false, true, false);
    }

    public final void test_OTNB_OTSB_XSB_XAB_XNB_OIM() {
        test(true, true, false, false, false, true);
    }

    public final void test_OTNB_OTSB_XSB_XAB_XNB_XIM() {
        test(true, true, false, false, false, false);
    }

    public final void test_OTNB_XTSB_OSB_OAB_ONB_OIM() {
        test(true, false, true, true, true, true);
    }

    public final void test_OTNB_XTSB_OSB_OAB_ONB_XIM() {
        test(true, false, true, true, true, false);
    }

    public final void test_OTNB_XTSB_OSB_OAB_XNB_OIM() {
        test(true, false, true, true, false, true);
    }

    public final void test_OTNB_XTSB_OSB_OAB_XNB_XIM() {
        test(true, false, true, true, false, false);
    }

    public final void test_OTNB_XTSB_OSB_XAB_ONB_OIM() {
        test(true, false, true, false, true, true);
    }

    public final void test_OTNB_XTSB_OSB_XAB_ONB_XIM() {
        test(true, false, true, false, true, false);
    }

    public final void test_OTNB_XTSB_OSB_XAB_XNB_OIM() {
        test(true, false, true, false, false, true);
    }

    public final void test_OTNB_XTSB_OSB_XAB_XNB_XIM() {
        test(true, false, true, false, false, false);
    }

    public final void test_OTNB_XTSB_XSB_OAB_ONB_OIM() {
        test(true, false, false, true, true, true);
    }

    public final void test_OTNB_XTSB_XSB_OAB_ONB_XIM() {
        test(true, false, false, true, true, false);
    }

    public final void test_OTNB_XTSB_XSB_OAB_XNB_OIM() {
        test(true, false, false, true, false, true);
    }

    public final void test_OTNB_XTSB_XSB_OAB_XNB_XIM() {
        test(true, false, false, true, false, false);
    }

    public final void test_OTNB_XTSB_XSB_XAB_ONB_OIM() {
        test(true, false, false, false, true, true);
    }

    public final void test_OTNB_XTSB_XSB_XAB_ONB_XIM() {
        test(true, false, false, false, true, false);
    }

    public final void test_OTNB_XTSB_XSB_XAB_XNB_OIM() {
        test(true, false, false, false, false, true);
    }

    public final void test_OTNB_XTSB_XSB_XAB_XNB_XIM() {
        test(true, false, false, false, false, false);
    }

    public final void test_XTNB_OTSB_OSB_OAB_ONB_OIM() {
        test(false, true, true, true, true, true);
    }

    public final void test_XTNB_OTSB_OSB_OAB_ONB_XIM() {
        test(false, true, true, true, true, false);
    }

    public final void test_XTNB_OTSB_OSB_OAB_XNB_OIM() {
        test(false, true, true, true, false, true);
    }

    public final void test_XTNB_OTSB_OSB_OAB_XNB_XIM() {
        test(false, true, true, true, false, false);
    }

    public final void test_XTNB_OTSB_OSB_XAB_ONB_OIM() {
        test(false, true, true, false, true, true);
    }

    public final void test_XTNB_OTSB_OSB_XAB_ONB_XIM() {
        test(false, true, true, false, true, false);
    }

    public final void test_XTNB_OTSB_OSB_XAB_XNB_OIM() {
        test(false, true, true, false, false, true);
    }

    public final void test_XTNB_OTSB_OSB_XAB_XNB_XIM() {
        test(false, true, true, false, false, false);
    }

    public final void test_XTNB_OTSB_XSB_OAB_ONB_OIM() {
        test(false, true, false, true, true, true);
    }

    public final void test_XTNB_OTSB_XSB_OAB_ONB_XIM() {
        test(false, true, false, true, true, false);
    }

    public final void test_XTNB_OTSB_XSB_OAB_XNB_OIM() {
        test(false, true, false, true, false, true);
    }

    public final void test_XTNB_OTSB_XSB_OAB_XNB_XIM() {
        test(false, true, false, true, false, false);
    }

    public final void test_XTNB_OTSB_XSB_XAB_ONB_OIM() {
        test(false, true, false, false, true, true);
    }

    public final void test_XTNB_OTSB_XSB_XAB_ONB_XIM() {
        test(false, true, false, false, true, false);
    }

    public final void test_XTNB_OTSB_XSB_XAB_XNB_OIM() {
        test(false, true, false, false, false, true);
    }

    public final void test_XTNB_OTSB_XSB_XAB_XNB_XIM() {
        test(false, true, false, false, false, false);
    }

    public final void test_XTNB_XTSB_OSB_OAB_ONB_OIM() {
        test(false, false, true, true, true, true);
    }

    public final void test_XTNB_XTSB_OSB_OAB_ONB_XIM() {
        test(false, false, true, true, true, false);
    }

    public final void test_XTNB_XTSB_OSB_OAB_XNB_OIM() {
        test(false, false, true, true, false, true);
    }

    public final void test_XTNB_XTSB_OSB_OAB_XNB_XIM() {
        test(false, false, true, true, false, false);
    }

    public final void test_XTNB_XTSB_OSB_XAB_ONB_OIM() {
        test(false, false, true, false, true, true);
    }

    public final void test_XTNB_XTSB_OSB_XAB_ONB_XIM() {
        test(false, false, true, false, true, false);
    }

    public final void test_XTNB_XTSB_OSB_XAB_XNB_OIM() {
        test(false, false, true, false, false, true);
    }

    public final void test_XTNB_XTSB_OSB_XAB_XNB_XIM() {
        test(false, false, true, false, false, false);
    }

    public final void test_XTNB_XTSB_XSB_OAB_ONB_OIM() {
        test(false, false, false, true, true, true);
    }

    public final void test_XTNB_XTSB_XSB_OAB_ONB_XIM() {
        test(false, false, false, true, true, false);
    }

    public final void test_XTNB_XTSB_XSB_OAB_XNB_OIM() {
        test(false, false, false, true, false, true);
    }

    public final void test_XTNB_XTSB_XSB_OAB_XNB_XIM() {
        test(false, false, false, true, false, false);
    }

    public final void test_XTNB_XTSB_XSB_XAB_ONB_OIM() {
        test(false, false, false, false, true, true);
    }

    public final void test_XTNB_XTSB_XSB_XAB_ONB_XIM() {
        test(false, false, false, false, true, false);
    }

    public final void test_XTNB_XTSB_XSB_XAB_XNB_OIM() {
        test(false, false, false, false, false, true);
    }

    public final void test_XTNB_XTSB_XSB_XAB_XNB_XIM() {
        test(false, false, false, false, false, false);
    }

    public final void test_Footer_defMode() {
        testFooter(HtcFooter.DISPLAY_MODE_DEFAULT);
    }

    public final void test_Footer_bottMode() {
        testFooter(HtcFooter.DISPLAY_MODE_ALWAYSBOTTOM);
    }

    public final void test_Footer_rightMode() {
        testFooter(HtcFooter.DISPLAY_MODE_ALWAYSRIGHT);
    }

    private void test(boolean translucentStatusBar, boolean hasStatusBar, boolean hasActionBar, boolean translucentNavigationBar, boolean hasNavigationBar, boolean hasIME) {
        Intent i = new Intent();
        i.putExtra("translucentStatusBar", translucentStatusBar);
        i.putExtra("hasStatusBar", hasStatusBar);
        i.putExtra("hasActionBar", hasActionBar);
        i.putExtra("translucentNavigationBar", translucentNavigationBar);
        i.putExtra("hasNavigationBar", hasNavigationBar);
        i.putExtra("hasIME", hasIME);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
        ViewUtil.AssertViewSizeAndLocationEqualBefore(mSolo, mSolo.getView(R.id.testTextView), this);
    }

    private void testFooter(int footerDisplayMode) {
        Intent i = new Intent();
        i.putExtra("footerDisplayMode", footerDisplayMode);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(R.id.testHtcFooter), this);
    }
}
