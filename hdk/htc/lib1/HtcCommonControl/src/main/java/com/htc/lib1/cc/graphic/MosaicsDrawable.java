
package com.htc.lib1.cc.graphic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * @hide
 * @deprecated try level not release
 */
public class MosaicsDrawable extends Drawable {
    private final static Double TIMES = 16.0;
    private final static int DARK_BLOCK_COLOR = Color.GRAY;
    private final static int LIGHT_BLOCK_COLOR = Color.WHITE;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;

    public MosaicsDrawable() {
        mPaint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        int x = 0;
        int y = 0;
        boolean isDarkBlockColor = true;
        for (int i = 0; i < TIMES; i++) {
            x = 0;
            y = i * mHeight;
            for (int j = 0; j < TIMES; j++) {
                x = j * mWidth;
                mPaint.setColor(isDarkBlockColor ? DARK_BLOCK_COLOR : LIGHT_BLOCK_COLOR);
                isDarkBlockColor = !isDarkBlockColor;
                canvas.drawRect(x, y, x + mWidth, y + mHeight, mPaint);
            }
            isDarkBlockColor = !isDarkBlockColor;
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mWidth = (int) Math.ceil((bounds.right - bounds.left) / TIMES);
        mHeight = (int) Math.ceil((bounds.bottom - bounds.top) / TIMES);
    }

}
