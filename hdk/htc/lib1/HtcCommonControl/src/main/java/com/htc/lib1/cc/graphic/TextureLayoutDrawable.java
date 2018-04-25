
package com.htc.lib1.cc.graphic;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * @hide
 * @deprecated try level not release
 */
public class TextureLayoutDrawable extends Drawable implements Drawable.Callback {

    private Drawable mStatusBarDrawable;
    private Drawable mActionBarDrawable;

    private int mStatusBarHeight = 0;
    private int mActionBarHeight = 0;

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        safeDraw(mStatusBarDrawable, canvas, true);

        safeDraw(mActionBarDrawable, canvas, false);
        canvas.restore();
    }

    /**
     * if draw is not null and is visible , draw on canvas.if need move canvas , translate the
     * canvas
     *
     * @param d
     * @param canvas
     * @param isTranslate
     */
    private void safeDraw(Drawable d, Canvas canvas, boolean isTranslate) {
        if (isExist(d)) {
            d.draw(canvas);
            if (isTranslate) {
                canvas.translate(0, d.getBounds().height());
            }
        }
    }

    /**
     * set StatusBar background
     *
     * @param d drawable of statusBar
     */
    public void setStatusBarDrawable(Drawable d) {
        if (d != mStatusBarDrawable) {
            updateCallBack(this.mStatusBarDrawable, d);
            this.mStatusBarDrawable = d;
            updateBounds(getBounds(), mStatusBarDrawable, mStatusBarHeight);
        }
    }

    /**
     * set ActionBar background
     *
     * @param d drawable of actionBar
     */
    public void setActionBarDrawable(Drawable d) {
        if (d != mActionBarDrawable) {
            updateCallBack(mActionBarDrawable, d);
            this.mActionBarDrawable = d;
            updateBounds(getBounds(), mActionBarDrawable, mActionBarHeight);
        }
    }

    /**
     * set StatusBar height
     *
     * @param height height of statusBar
     */
    public void setStatusBarHeight(int height) {
        if (height != mStatusBarHeight && height >= 0) {
            this.mStatusBarHeight = height;
            updateBounds(getBounds(), mStatusBarDrawable, height);
        }
    }

    /**
     * set ActionBar height
     *
     * @param height height of actionBar
     */
    public void setActionBarHeight(int height) {
        if (height != mActionBarHeight && height >= 0) {
            this.mActionBarHeight = height;
            updateBounds(getBounds(), mActionBarDrawable, height);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        updateBounds(bounds, mStatusBarDrawable, mStatusBarHeight);
        updateBounds(bounds, mActionBarDrawable, mActionBarHeight);
    }

    /**
     * set the bounds for drawable, and invalidateSelf
     *
     * @param bounds the drawable's bounds
     * @param d drawable
     * @param height drawable's height
     */
    private void updateBounds(Rect bounds, Drawable d, int height) {
        if (null != d) {
            d.setBounds(0, 0, bounds.right, height);
            invalidateSelf();
        }
    }

    /**
     * set the callback for new drawable and clean the callback for old drawable
     *
     * @param currentDrawable
     * @param newDrawable
     */
    private void updateCallBack(Drawable currentDrawable, Drawable newDrawable) {
        if (currentDrawable != null) {
            currentDrawable.setCallback(null);
        }
        if (newDrawable != null) {
            newDrawable.setCallback(this);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (null != mStatusBarDrawable) {
            mStatusBarDrawable.setAlpha(alpha);
        }
        if (null != mActionBarDrawable) {
            mActionBarDrawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (null != mStatusBarDrawable) {
            mStatusBarDrawable.setColorFilter(cf);
        }
        if (null != mActionBarDrawable) {
            mActionBarDrawable.setColorFilter(cf);
        }
    }

    @Override
    public int getOpacity() {
        if (null != mStatusBarDrawable) {
            return mStatusBarDrawable.getOpacity();
        }
        if (null != mActionBarDrawable) {
            return mActionBarDrawable.getOpacity();
        }
        return PixelFormat.RGBA_8888;
    }

    /**
     * @param d
     * @return this drawable is or not exist
     */
    private boolean isExist(Drawable d) {
        return d != null && d.isVisible();
    }

    @Override
    public int getIntrinsicHeight() {
        int intrinsicHeight = 0;
        if (isExist(mStatusBarDrawable)) {
            intrinsicHeight += mStatusBarHeight;
        }
        if (isExist(mActionBarDrawable)) {
            intrinsicHeight += mActionBarHeight;
        }
        return intrinsicHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        int intrinsicWidth = 0;
        if (isExist(mStatusBarDrawable)) {
            intrinsicWidth = mStatusBarHeight;
        }
        if (isExist(mActionBarDrawable)) {
            intrinsicWidth = Math.max(mActionBarHeight, intrinsicWidth);
        }
        return intrinsicWidth;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }
}
