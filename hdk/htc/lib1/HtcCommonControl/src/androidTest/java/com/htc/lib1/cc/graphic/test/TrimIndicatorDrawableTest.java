
package com.htc.lib1.cc.graphic.test;

import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.graphic.TrimIndicatorDrawable;
import com.htc.test.util.ScreenShotUtil;

public class TrimIndicatorDrawableTest extends AndroidTestCase {

    // Max Width / Height of Drawable move range.
    private static final int MAX_RANGE = 600;
    private static final int MAX_LEVEL = 10000;
    private Drawable mTrimIndicator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Resources res = getContext().getResources();
        mTrimIndicator = res.getDrawable(R.drawable.common_collect_rest);
    }

    public void testHorizontalStart() {
        assertNotNull(mTrimIndicator);
        test(TrimIndicatorDrawable.ORIENTATION_HORIZONTAL, 0,
                MAX_RANGE, mTrimIndicator.getIntrinsicHeight());
    }

    public void testHorizontalMid() {
        assertNotNull(mTrimIndicator);
        test(TrimIndicatorDrawable.ORIENTATION_HORIZONTAL, MAX_LEVEL / 2,
                MAX_RANGE, mTrimIndicator.getIntrinsicHeight());
    }

    public void testHorizontalEnd() {
        assertNotNull(mTrimIndicator);
        test(TrimIndicatorDrawable.ORIENTATION_HORIZONTAL, MAX_LEVEL,
                MAX_RANGE, mTrimIndicator.getIntrinsicHeight());
    }

    public void testVecticalStart() {
        assertNotNull(mTrimIndicator);
        test(TrimIndicatorDrawable.ORIENTATION_VERTICAL, 0,
                mTrimIndicator.getIntrinsicWidth(), MAX_RANGE);
    }

    public void testVecticalMid() {
        assertNotNull(mTrimIndicator);
        test(TrimIndicatorDrawable.ORIENTATION_VERTICAL, MAX_LEVEL / 2,
                mTrimIndicator.getIntrinsicWidth(), MAX_RANGE);
    }

    public void testVecticalEnd() {
        assertNotNull(mTrimIndicator);
        test(TrimIndicatorDrawable.ORIENTATION_VERTICAL, MAX_LEVEL,
                mTrimIndicator.getIntrinsicWidth(), MAX_RANGE);
    }

    private void test(int orientation, int level, int width, int height) {
        TrimIndicatorDrawable trim = new TrimIndicatorDrawable(mTrimIndicator, orientation);
        Rect bounds = new Rect(0, 0, width, height);
        trim.setBounds(bounds);
        trim.setLevel(level);
        ScreenShotUtil.assertDrawable(trim, getContext(), this);
    }
}
