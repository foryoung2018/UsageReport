
package com.htc.lib1.cc.graphic;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * @deprecated
 * @hide
 */
public class MirrorDrawable extends Drawable {

    public static final boolean MIRROR_HORIZONTAL = true;
    public static final boolean MIRROR_VERTICAL = false;

    private Drawable mDrawable;
    private boolean mMirrorOrientation;

    public MirrorDrawable(Drawable draw, boolean mirrorOrientation) {
        mDrawable = draw;
        mMirrorOrientation = mirrorOrientation;
    }

    @Override
    public void draw(Canvas canvas) {
        if (null == mDrawable) {
            return;
        }

        int offsetX = 0, offsetY = 0;
        float scaleX = 1.0f, scaleY = 1.0f;
        if (mMirrorOrientation == MIRROR_HORIZONTAL) {
            offsetX = getBounds().width();
            scaleX = -1.0f;
        } else {
            offsetY = getBounds().height();
            scaleY = -1.0f;
        }

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scaleX, scaleY);
        mDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        int left, top;
        if (mMirrorOrientation == MIRROR_HORIZONTAL) {
            left = -bounds.left;
            top = bounds.top;
        } else {
            left = bounds.left;
            top = -bounds.top;
        }

        mDrawable.setBounds(left, top, left + bounds.width(), top + bounds.height());
    }

    @Override
    public void setAlpha(int alpha) {
        mDrawable.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return mDrawable.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mDrawable.setColorFilter(cf);
    }

    @Override
    public int getIntrinsicWidth() {
        return mDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mDrawable.getIntrinsicHeight();
    }

    @Override
    public int getOpacity() {
        return mDrawable.getOpacity();
    }

}
