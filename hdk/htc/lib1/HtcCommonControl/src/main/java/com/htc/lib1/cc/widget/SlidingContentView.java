
package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.util.CheckUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

class SlidingContentView extends ViewGroup {

    private static final String TAG = "SlidingContentView";

    private static final boolean USE_CACHE = false;

    private View mContent;

    private boolean mScrollingCacheEnabled;

    private SlidingMenuView mSlidingMenuView;

    public static final int BEHIND_VIEW_INDEX = 0;

    public static final int CONTENT_VIEW_INDEX = 1;

    public SlidingContentView(Context context) {
        super(context);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
        initCustomViewAbove();
    }

    void initCustomViewAbove() {
        setWillNotDraw(false);
    }

    public int getContentLeft() {
        return mContent.getLeft() + mContent.getPaddingLeft();
    }

    public int getBehindWidth() {
        if (null == mSlidingMenuView) {
            return 0;
        } else {
            return mSlidingMenuView.getBehindWidth();
        }
    }

    public int getChildWidth(int index) {
        switch (index) {
            case BEHIND_VIEW_INDEX:
                return getBehindWidth();
            case CONTENT_VIEW_INDEX:
                return mContent.getWidth();
            default:
                return 0;
        }
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

    public void setCustomMenuView(SlidingMenuView customMenuView) {
        mSlidingMenuView = customMenuView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);

        final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
        final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
        mContent.measure(contentWidth, contentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        mContent.layout(0, 0, width, height);
    }

    public void setAboveOffset(int offset) {
        mContent.setPadding(offset, mContent.getPaddingTop(), mContent.getPaddingRight(),
                mContent.getPaddingBottom());
    }

    private int mTouchMode = SlidingMenu.TOUCHMODE_MARGIN;

    public void setTouchMode(int touchMode) {
        mTouchMode = touchMode;
    }

    public int getTouchMode() {
        return mTouchMode;
    }

    public void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled;
            if (USE_CACHE) {
                final int size = getChildCount();
                for (int i = 0; i < size; ++i) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() != GONE) {
                        child.setDrawingCacheEnabled(enabled);
                    }
                }
            }
        }
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for
     *            scrollability (true), or just its children (false).
     * @param dx Delta scrolled in pixels
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canScroll(View targetView, boolean checkV, int dx, int x, int y) {
        if (targetView instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) targetView;
            final int scrollX = targetView.getScrollX();
            final int scrollY = targetView.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance
            // first.
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV && targetView.canScrollHorizontally(-dx);
    }

}
