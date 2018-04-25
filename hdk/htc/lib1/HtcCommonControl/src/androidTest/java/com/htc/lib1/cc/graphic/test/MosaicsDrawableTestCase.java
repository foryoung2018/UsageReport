
package com.htc.lib1.cc.graphic.test;

import android.test.AndroidTestCase;

import com.htc.lib1.cc.graphic.MosaicsDrawable;
import com.htc.test.util.NoRunTheme;
import com.htc.test.util.ScreenShotUtil;

@NoRunTheme
public class MosaicsDrawableTestCase extends AndroidTestCase {

    public void testMosaics() {
        initDrawableAndAssert(145, 145);
    }

    public void testMosaics_160px() {
        initDrawableAndAssert(160, 160);
    }

    public void testMosaics_152px() {
        initDrawableAndAssert(152, 152);
    }

    public void testMosaics_230px() {
        initDrawableAndAssert(230, 230);
    }

    private void initDrawableAndAssert(final int width, final int height) {
        MosaicsDrawable mosaicsDrawable = new MosaicsDrawable();
        mosaicsDrawable.setBounds(0, 0, width, height);
        ScreenShotUtil.assertDrawable(mosaicsDrawable, getContext(), this);
    }

}
