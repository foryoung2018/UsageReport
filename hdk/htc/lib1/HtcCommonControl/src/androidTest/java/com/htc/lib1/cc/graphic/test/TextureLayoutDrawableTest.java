
package com.htc.lib1.cc.graphic.test;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;

import com.htc.lib1.cc.graphic.TextureLayoutDrawable;
import com.htc.test.util.NoRunOrientation;
import com.htc.test.util.ScreenShotUtil;

@NoRunOrientation
public class TextureLayoutDrawableTest extends AndroidTestCase {

    private final static int STATUSBAR_ACTIONBAR_VISIBLE = 0;
    private final static int STATUSBAR_INVISIBLE = 1;
    private final static int ACTIONBAR_INVISIBLE = 2;

    private final static int BARHEIGHT = 50;
    private final static int NOBARHEIGHT = 0;

    public final void testSetDrawable_all() {
        initDrawableAndAssert(true, true, BARHEIGHT, BARHEIGHT, STATUSBAR_ACTIONBAR_VISIBLE, false);
    }

    public final void testSetDrawable_yes_no() {
        initDrawableAndAssert(true, false, BARHEIGHT, BARHEIGHT, STATUSBAR_ACTIONBAR_VISIBLE, false);
    }

    public final void testSetDrawable_no_yes() {
        initDrawableAndAssert(false, true, BARHEIGHT, BARHEIGHT, STATUSBAR_ACTIONBAR_VISIBLE, false);
    }

    public final void testSetHeight_yes_no() {
        initDrawableAndAssert(true, true, BARHEIGHT, NOBARHEIGHT, STATUSBAR_ACTIONBAR_VISIBLE, false);
    }

    public final void testSetHeight_no_yes() {
        initDrawableAndAssert(true, true, NOBARHEIGHT, BARHEIGHT, STATUSBAR_ACTIONBAR_VISIBLE, false);
    }

    public final void testSetHeightUnavail_yes_no() {
        initDrawableAndAssert(true, true, BARHEIGHT, -BARHEIGHT, STATUSBAR_ACTIONBAR_VISIBLE, false);
    }

    public final void testSetHeightUnavail_no_yes() {
        initDrawableAndAssert(true, true, -BARHEIGHT, BARHEIGHT, STATUSBAR_ACTIONBAR_VISIBLE, false);
    }

    public final void testSetVisible_yes_no() {
        initDrawableAndAssert(true, true, BARHEIGHT, BARHEIGHT, STATUSBAR_INVISIBLE, false);
    }

    public final void testSetVisible_no_yes() {
        initDrawableAndAssert(true, true, BARHEIGHT, BARHEIGHT, ACTIONBAR_INVISIBLE, false);
    }

    public final void testChangeDrawable() {
        initDrawableAndAssert(true, true, BARHEIGHT, BARHEIGHT, STATUSBAR_ACTIONBAR_VISIBLE, true);
    }

    private void initDrawableAndAssert(boolean setStatusBarDrawable, boolean setActionBarDrawable,
            int statusBarDrawableHeight, int actionBarDrawableHeight, int visibleType, boolean isDrawableChanged) {
        Drawable mStatusBarDrawable = new ColorDrawable(Color.BLUE);
        Drawable mActionBarDrawable = new ColorDrawable(Color.RED);

        TextureLayoutDrawable textureLayoutDrawable = new TextureLayoutDrawable();
        if (setStatusBarDrawable) {
            textureLayoutDrawable.setStatusBarDrawable(mStatusBarDrawable);
        }
        if (setActionBarDrawable) {
            textureLayoutDrawable.setActionBarDrawable(mActionBarDrawable);
        }

        if (statusBarDrawableHeight != 0) {
            textureLayoutDrawable.setStatusBarHeight(statusBarDrawableHeight);
        }
        if (actionBarDrawableHeight != 0) {
            textureLayoutDrawable.setActionBarHeight(actionBarDrawableHeight);
        }

        if (visibleType == STATUSBAR_INVISIBLE) {
            mStatusBarDrawable.setVisible(false, false);
        } else if (visibleType == ACTIONBAR_INVISIBLE) {
            mActionBarDrawable.setVisible(false, false);
        }

        if (isDrawableChanged) {
            mStatusBarDrawable.setAlpha(50);
            mActionBarDrawable.setAlpha(50);
        }

        final int w = textureLayoutDrawable.getIntrinsicWidth();
        final int h = textureLayoutDrawable.getIntrinsicHeight();
        textureLayoutDrawable.setBounds(0, 0, w, h);

        ScreenShotUtil.assertDrawable(textureLayoutDrawable, getContext(), this);
    }

}
