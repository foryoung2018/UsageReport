
package com.htc.lib1.cc.graphic;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * @deprecated
 * @hide
 */
public class TrimIndicatorDrawable extends Drawable {

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    private static final int LEVEL_MIN = 0;
    private static final int LEVEL_MAX = 10000;

    private Drawable mDrawableIndicator;
    private int mRangeWidthMin, mRangeWidthMax;
    private int mRangeHeightMin, mRangeHeightMax;
    private int mOrientation;

    /**
     * The Constructor of TrimIndicatorDrawable
     * @param indicator the drawable of indicator
     * @param bounds the range bounds of indicator
     * @param orientation {@link #ORIENTATION_VERTICAL} {@link #ORIENTATION_HORIZONTAL}
     */
    public TrimIndicatorDrawable(Drawable indicator, int orientation) {
        mDrawableIndicator = indicator;
        mOrientation = orientation;
    }

    @Override
    protected boolean onLevelChange(int level) {
        updateIndicatorBounds(level, getBounds());
        invalidateSelf();
        return true;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        updateRangeSpec(mOrientation);
        updateIndicatorBounds(getLevel(), bounds);
        super.onBoundsChange(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        if (null == mDrawableIndicator) {
            return;
        }
        mDrawableIndicator.draw(canvas);
    }

    @Override
    public int getIntrinsicWidth() {
        Rect bounds = getBounds();
        return bounds.width();
    }

    @Override
    public int getIntrinsicHeight() {
        Rect bounds = getBounds();
        return bounds.height();
    }

    @Override
    public void setAlpha(int alpha) {
        mDrawableIndicator.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return mDrawableIndicator.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mDrawableIndicator.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return mDrawableIndicator.getOpacity();
    }

    /**
     * update indicator bounds
     * @param level drawable level
     * @param bounds TrimIndicatorDrawable bounds
     */
    private void updateIndicatorBounds(int level, Rect bounds) {
        if (null == mDrawableIndicator) {
            return;
        }
        if (level < 0) {
            level = LEVEL_MIN;
        } else if (level > LEVEL_MAX) {
            level = LEVEL_MAX;
        }

        int offsetX = 0;
        int offsetY = 0;
        double scale = (double) level / LEVEL_MAX;
        switch (mOrientation) {
            case ORIENTATION_HORIZONTAL:
                offsetX = (int) Math.ceil((mRangeWidthMax - mRangeWidthMin) * scale);
                break;
            case ORIENTATION_VERTICAL:
                offsetY = (int) Math.ceil((mRangeHeightMax - mRangeHeightMin) * scale);
                break;
            default:
                break;
        }
        mDrawableIndicator.setBounds(bounds.left + offsetX, bounds.top + offsetY,
                bounds.left + offsetX + mDrawableIndicator.getIntrinsicWidth(),
                bounds.top + offsetY + mDrawableIndicator.getIntrinsicHeight());
    }

    /**
     * update the range of indicator can arrived
     * @param orientation
     */
    private void updateRangeSpec(int orientation) {
        if (null == mDrawableIndicator) {
            return;
        }
        final Rect rect = getBounds();
        switch (orientation) {
            case ORIENTATION_HORIZONTAL:
                mRangeWidthMin = rect.left;
                mRangeWidthMax = rect.right - mDrawableIndicator.getIntrinsicWidth();
                break;
            case ORIENTATION_VERTICAL:
                mRangeHeightMin = rect.top;
                mRangeHeightMax = rect.bottom - mDrawableIndicator.getIntrinsicHeight();
                break;
            default:
                break;
        }
    }
}
