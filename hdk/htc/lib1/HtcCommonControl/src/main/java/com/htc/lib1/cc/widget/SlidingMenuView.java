
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.SlidingMenu.CanvasTransformer;

class SlidingMenuView extends ViewGroup {

    private static final String TAG = "SlidingMenuView";

    private int mTouchMode = SlidingMenu.TOUCHMODE_MARGIN;

    private SlidingContentView mSlidingContentView;

    private View mContent;
    private View mSecondaryContent;
    private int mWidthOffset;
    private CanvasTransformer mTransformer;

    public SlidingMenuView(Context context) {
        super(context);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
    }

    public void setCustomContentView(SlidingContentView customViewAbove) {
        mSlidingContentView = customViewAbove;
    }

    public void setCanvasTransformer(CanvasTransformer canvasTransformer) {
        mTransformer = canvasTransformer;
    }

    public void setWidthOffset(int offset) {
        mWidthOffset = offset;
        requestLayout();
    }

    int getWidthOffset() {
        return mWidthOffset;
    }

    public int getBehindWidth() {
        return mContent.getWidth();
    }

    public void setContent(View contentView) {
        if (mContent != null) {
            removeView(mContent);
        }
        mContent = contentView;
        addView(mContent);
    }

    public View getContent() {
        return mContent;
    }

    /**
     * Sets the secondary (right) menu for use when setMode is called with
     * SlidingMenu.LEFT_RIGHT.
     *
     * @param v the right menu
     */
    public void setSecondaryContent(View secondaryContentView) {
        if (mSecondaryContent != null) {
            removeView(mSecondaryContent);
        }
        mSecondaryContent = secondaryContentView;
        addView(mSecondaryContent);
    }

    public View getSecondaryContent() {
        return mSecondaryContent;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if (mTransformer != null) {
            invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mTransformer != null) {
            canvas.save();
            mTransformer.transformCanvas(canvas,
                    ((SlidingMenu) (mSlidingContentView.getParent())).getPercentOpen());
            super.dispatchDraw(canvas);
            canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        mContent.layout(0, 0, width - mWidthOffset, height);
        if (mSecondaryContent != null) {
            mSecondaryContent.layout(0, 0, width - mWidthOffset, height);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = getDefaultSize(0, widthMeasureSpec);
        final int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
        final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width - mWidthOffset);
        final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
        mContent.measure(contentWidth, contentHeight);
        if (mSecondaryContent != null) {
            mSecondaryContent.measure(contentWidth, contentHeight);
        }
    }

    private int mMode;
    private boolean mFadeEnabled;
    private final Paint mFadePaint = new Paint();
    private Drawable mShadowDrawable;
    private Drawable mSecondaryShadowDrawable;
    private int mShadowWidth;
    private float mFadeDegree;

    public void setMode(int mode) {
        if (SlidingMenu.LEFT == mode || SlidingMenu.RIGHT == mode) {
            if (mContent != null) {
                mContent.setVisibility(View.VISIBLE);
            }

            if (mSecondaryContent != null) {
                mSecondaryContent.setVisibility(View.INVISIBLE);
            }
        }
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public void setShadowDrawable(Drawable shadow) {
        mShadowDrawable = shadow;
        invalidate();
    }

    public void setSecondaryShadowDrawable(Drawable shadow) {
        mSecondaryShadowDrawable = shadow;
        invalidate();
    }

    public void setShadowWidth(int width) {
        mShadowWidth = width;
        invalidate();
    }

    public void setFadeEnabled(boolean isFadeEnabled) {
        mFadeEnabled = isFadeEnabled;
    }

    public void setFadeDegree(float degree) {
        if (degree > 1.0f || degree < 0.0f) {
            throw new IllegalStateException("The BehindFadeDegree must be between 0.0f and 1.0f");
        }
        mFadeDegree = degree;
    }

    public int getMenuPage(int page) {
        if (page > 1) {
            page = 2;
        } else if (page < 1) {
            page = 0;
        }
        if (SlidingMenu.LEFT == mMode && page > 1) {
            return 0;
        } else if (SlidingMenu.RIGHT == mMode && page < 1) {
            return 2;
        } else {
            return page;
        }
    }

    public int getMenuLeft(View content, int page) {
        if (SlidingMenu.LEFT == mMode) {
            switch (page) {
                case 0:
                    return content.getLeft() - getBehindWidth();
                case 2:
                    return content.getLeft();
            }
        } else if (SlidingMenu.RIGHT == mMode) {
            switch (page) {
                case 0:
                    return content.getLeft();
                case 2:
                    return content.getLeft() + getBehindWidth();
            }
        } else if (SlidingMenu.LEFT_RIGHT == mMode) {
            switch (page) {
                case 0:
                    return content.getLeft() - getBehindWidth();
                case 2:
                    return content.getLeft() + getBehindWidth();
            }
        }
        return content.getLeft();
    }

    public int getAbsLeftBound(View content) {
        if (SlidingMenu.LEFT == mMode || SlidingMenu.LEFT_RIGHT == mMode) {
            return content.getLeft() - getBehindWidth();
        } else if (SlidingMenu.RIGHT == mMode) {
            return content.getLeft();
        } else {
            return 0;
        }
    }

    public int getAbsRightBound(View content) {
        if (SlidingMenu.LEFT == mMode) {
            return content.getLeft();
        } else if (SlidingMenu.RIGHT == mMode || SlidingMenu.LEFT_RIGHT == mMode) {
            return content.getLeft() + getBehindWidth();
        } else {
            return 0;
        }

    }

    public void setTouchMode(int mode) {
        mTouchMode = mode;
    }

    public boolean menuOpenTouchAllowed(View content, int currPage, float x) {
        switch (mTouchMode) {
            case SlidingMenu.TOUCHMODE_FULLSCREEN:
                return true;
            case SlidingMenu.TOUCHMODE_MARGIN:
                return menuTouchInQuickReturn(content, currPage, x);
        }
        return false;
    }

    public boolean menuTouchInQuickReturn(View content, int currPage, float x) {
        if (SlidingMenu.LEFT == mMode || (SlidingMenu.LEFT_RIGHT == mMode && 0 == currPage)) {
            return x >= content.getLeft();
        } else if (SlidingMenu.RIGHT == mMode || (SlidingMenu.LEFT_RIGHT == mMode && 2 == currPage)) {
            return x <= content.getRight();
        }
        return false;
    }

    public boolean menuClosedSlideAllowed(float dx) {
        if (SlidingMenu.LEFT == mMode) {
            return dx > 0;
        } else if (SlidingMenu.RIGHT == mMode) {
            return dx < 0;
        } else if (SlidingMenu.LEFT_RIGHT == mMode) {
            return true;
        } else {
            return false;
        }
    }

    public boolean menuOpenSlideAllowed(float dx) {
        if (SlidingMenu.LEFT == mMode) {
            return dx < 0;
        } else if (SlidingMenu.RIGHT == mMode) {
            return dx > 0;
        } else if (SlidingMenu.LEFT_RIGHT == mMode) {
            return true;
        } else {
            return false;
        }
    }

    public void drawShadow(View content, Canvas canvas) {
        if (null == mShadowDrawable || mShadowWidth <= 0) {
            return;
        }
        int left = 0;
        if (SlidingMenu.LEFT == mMode) {
            left = content.getLeft() - mShadowWidth;
        } else if (SlidingMenu.RIGHT == mMode) {
            left = content.getRight();
        } else if (SlidingMenu.LEFT_RIGHT == mMode) {
            if (mSecondaryShadowDrawable != null) {
                left = content.getRight();
                mSecondaryShadowDrawable.setBounds(left, 0, left + mShadowWidth, getHeight());
                mSecondaryShadowDrawable.draw(canvas);
            }
            left = content.getLeft() - mShadowWidth;
        }
        mShadowDrawable.setBounds(left, 0, left + mShadowWidth, getHeight());
        mShadowDrawable.draw(canvas);
    }

    public void drawFade(View content, Canvas canvas, float openPercent) {
        if (!mFadeEnabled) {
            return;
        }
        final int alpha = (int) (mFadeDegree * 255 * Math.abs(1 - openPercent));
        mFadePaint.setColor(Color.argb(alpha, 0, 0, 0));
        int left = 0;
        int right = 0;
        if (SlidingMenu.LEFT == mMode) {
            left = content.getLeft() - getBehindWidth();
            right = content.getLeft();
        } else if (SlidingMenu.RIGHT == mMode) {
            left = content.getRight();
            right = content.getRight() + getBehindWidth();
        } else if (SlidingMenu.LEFT_RIGHT == mMode) {
            left = content.getLeft() - getBehindWidth();
            right = content.getLeft();
            canvas.drawRect(left, 0, right, getHeight(), mFadePaint);
            left = content.getRight();
            right = content.getRight() + getBehindWidth();
        }
        canvas.drawRect(left, 0, right, getHeight(), mFadePaint);
    }

    private boolean mSelectorEnabled = true;
    private Bitmap mSelectorDrawable;
    private View mSelectedView;

    public void drawSelector(View content, Canvas canvas, float openPercent) {
        if (!mSelectorEnabled) {
            return;
        }

        if (mSelectorDrawable != null && mSelectedView != null) {
            String tag = (String) mSelectedView
                    .getTag(android.R.id.candidatesArea);
            if (tag.equals(TAG + "SelectedView")) {
                canvas.save();
                int left, right, offset;
                offset = (int) (mSelectorDrawable.getWidth() * openPercent);
                if (SlidingMenu.LEFT == mMode) {
                    right = content.getLeft();
                    left = right - offset;
                    canvas.clipRect(left, 0, right, getHeight());
                    canvas.drawBitmap(mSelectorDrawable, left, getSelectorTop(), null);
                } else if (SlidingMenu.RIGHT == mMode) {
                    left = content.getRight();
                    right = left + offset;
                    canvas.clipRect(left, 0, right, getHeight());
                    canvas.drawBitmap(mSelectorDrawable, right - mSelectorDrawable.getWidth(),
                            getSelectorTop(), null);
                }
                canvas.restore();
            }
        }
    }

    public void setSelectorEnabled(boolean isSelectorEnabled) {
        mSelectorEnabled = isSelectorEnabled;
    }

    public void setSelectedView(View selectedView) {
        if (mSelectedView != null) {
            mSelectedView.setTag(android.R.id.candidatesArea, null);
            mSelectedView = null;
        }
        if (selectedView != null && selectedView.getParent() != null) {
            mSelectedView = selectedView;
            mSelectedView.setTag(android.R.id.candidatesArea, TAG
                    + "SelectedView");
            invalidate();
        }
    }

    private int getSelectorTop() {
        int y = mSelectedView.getTop();
        y += (mSelectedView.getHeight() - mSelectorDrawable.getHeight()) / 2;
        return y;
    }

    public void setSelectorBitmap(Bitmap selectorBitmap) {
        mSelectorDrawable = selectorBitmap;
        refreshDrawableState();
    }

    private OnSizeChangedListener mOnSizeChangedListener;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (null != mOnSizeChangedListener) {
            mOnSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    static interface OnSizeChangedListener {
        public void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        mOnSizeChangedListener = onSizeChangedListener;
    }

}
