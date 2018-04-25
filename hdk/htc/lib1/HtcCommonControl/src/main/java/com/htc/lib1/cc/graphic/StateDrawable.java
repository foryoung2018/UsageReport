
package com.htc.lib1.cc.graphic;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.StateSet;

/**
 * This class just Create a ColorStateListdrawable.
 * used by HtcEditText, HtcAutoCompleteTextView.
 * @hide
 */
public class StateDrawable extends Drawable {
    private static final int[] PRESSED_STATE_SET = {
            android.R.attr.state_pressed
    };
    private static final int[] FOCUSED_STATE_SET = {
            android.R.attr.state_focused
    };
    ColorStateList mState;
    Drawable mReal;

    public static Drawable getStateDrawable(Drawable real, ColorStateList csl) {
        return new StateDrawable(real, csl);
    }

    private StateDrawable(Drawable real, ColorStateList csl) {
        mState = csl;
        mReal = real;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        if (null != mState) {
            int color = mState.getColorForState(state, 0xffff0000);
            if (null != mReal) {
                if (StateSet.stateSetMatches(PRESSED_STATE_SET, state) || StateSet.stateSetMatches(FOCUSED_STATE_SET, state)) {
                    mReal.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }else{
                    mReal.setColorFilter(null);
                }
                this.invalidateSelf();
            }
        }
        return super.onStateChange(state);
    }

    @Override
    public void draw(Canvas canvas) {
        if (null != mReal)
            mReal.draw(canvas);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (null != mReal)
            mReal.setBounds(bounds);
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    /**
     * @hide
     */
    public static class ColorState {
        private int mPress;
        private int mFocus;
        private int mEmpty;

        public ColorState(int press, int focus, int empty) {
            mPress = press;
            mFocus = focus;
            mEmpty = empty;
        }

        public int getPressColor() {
            return mPress;
        }

        public int getFocusColor() {
            return mFocus;
        }

        public int getEmptyColor() {
            return mEmpty;
        }
    }
}
