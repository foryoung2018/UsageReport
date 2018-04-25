package com.htc.lib1.cc.view.table;

import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

import com.htc.lib1.cc.view.ScrollControl;
import com.htc.lib1.cc.view.table.AbstractTableView.FlingRunnable;
import com.htc.lib1.cc.view.table.AbstractTableView.OnScrollListener;

/**
 * HTableColleague
 * @deprecated [Not use any longer]
 */
/**@hide*/
public class HTableColleague extends TableColleague  {
    private static final boolean localLOGV = false;
    private static final String TAG = "HTableColleague";

        /**
         * @param view the TableView object related to this objecct
         */
    public HTableColleague(TableView view) {
        super(view);
        this.mGravity = Gravity.TOP;
        this.mHorizontalSpacing = tableView.mRequestedHorizontalSpacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnLeft(Rect rect) {
        return rect.top;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnRight(Rect rect) {
        return rect.bottom;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnTop(Rect rect) {
        return rect.left;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnBottom(Rect rect) {
        return rect.right;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnTop(View view) {
        return view.getLeft();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnBottom(View view) {
        return view.getRight();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnLeft(View view) {
        return view.getTop();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override public int getOrnRight(View view) {
        return view.getBottom();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnWidth(View view) {
        return view.getHeight();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnHeight(View view) {
        return view.getWidth();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnMeasuredWidth(View view) {
        return view.getMeasuredHeight();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override public int getOrnMeasuredHeight(View view) {
        return view.getMeasuredWidth();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnWidth(AbstractTableView.LayoutParams params) {
        return params.height;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnHeight(AbstractTableView.LayoutParams params) {
        return params.width;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public OrnMeasureSpec getOrnMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {
        OrnMeasureSpec spec = new OrnMeasureSpec();
        spec.ornWidthMode = MeasureSpec.getMode(heightMeasureSpec);
        spec.ornHeightMode = MeasureSpec.getMode(widthMeasureSpec);
        spec.ornWidthSize = MeasureSpec.getSize(heightMeasureSpec);
        spec.ornHeightSize = MeasureSpec.getSize(widthMeasureSpec);
        spec.ornWidthMeasureSpec = heightMeasureSpec;
        spec.ornHeightMeasureSpec = widthMeasureSpec;
        return spec;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public AbstractTableView.LayoutParams getDefaultChildLayoutParams() {
        return new AbstractTableView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT, 0);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void measureView(View view, int ornWidthMeasureSpec, int ornHeightMeasureSpec) {
        view.measure(ornHeightMeasureSpec, ornWidthMeasureSpec);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getFadingEdgeLength() {
        return tableView.getHorizontalFadingEdgeLength();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnLeftRightSpacing() {
        return this.mVerticalSpacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setOrnLeftRightSpacing(int spacing) {
        this.mVerticalSpacing = spacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnTopBottomSpacing() {
        return this.mHorizontalSpacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setOrnTopBottomSpacing(int spacing) {
        this.mHorizontalSpacing = spacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getScrollbarWidth() {
        return tableView.getHorizontalFadingEdgeLength();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setTableViewMeasuredDimension(int ornMeasuredWidth, int ornMeasuredHeight) {
        tableView.setMeasuredDimensionEx(ornMeasuredHeight, ornMeasuredWidth);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnRequestedWidthSpacing() {
        return tableView.mRequestedVerticalSpacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnGravity() {
        switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
        case Gravity.TOP:
            return Gravity.LEFT;
        case Gravity.BOTTOM:
            return Gravity.RIGHT;
        default:
            return (mGravity & Gravity.VERTICAL_GRAVITY_MASK);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void layoutView(View view, int ornLeft, int ornTop, int ornRight, int ornBottom) {
        view.layout(ornTop, ornLeft, ornBottom, ornRight);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void offsetLeftAndRight(View view, int offset) {
        view.offsetTopAndBottom(offset);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void offsetTopAndBottom(View view, int offset) {
        view.offsetLeftAndRight(offset);
    }

    /**
     * Offset the vertical location of all children of this view by the specified number of pixels.
     *
     * @param offset the number of pixels to offset
     *
     * @hide
     */
    public void offsetOrnChildrenTopAndBottom(int offset) {

        final int count = tableView.getChildCount();

        for (int i = 0; i < count; i++) {
            final View v = tableView.getChildAt(i);
            v.offsetLeftAndRight(offset);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void fling(FlingRunnable runnable, float velocityX, float velocityY) {
        runnable.startUsingVelocity((int) -velocityX, 0);

    }


/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void trackMotionScrollOrn(int deltaX, int deltaY) {
        boolean toLeft = deltaX < 0;
        /* trackMotionScroll should not set any limitation.
         * The limitation should put on
         * 1.onScroll in AbstractTableView
         * 2.Fling
        int limitedDeltaX = getLimitedMotionScrollAmount(toLeft, deltaX);
        if (limitedDeltaX != deltaX) {
            // The above call returned a limited amount, so stop any scrolls/flings
            Log.w("TableScroll", "trackMotionScroll call endFling("+false+")");
            tableView.mFlingRunnable.endFling(false);
        }
         */
        tableView.offsetChildrenLeftAndRight(deltaX);
        tableView.blockLayoutRequests(true);
        detachOffScreenChildren(toLeft);
        // If scroll to Left means we need to fill Right. so down is true.
        fillGap(toLeft);
        tableView.blockLayoutRequests(false);
    }

    /**
     * Detaches children that are off the screen (i.e.: Gallery bounds).
     *
     * @param toLeft Whether to detach children to the left of the Gallery, or
     *            to the right.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void detachOffScreenChildren(boolean toLeft) {
        int numChildren = tableView.getChildCount();
        int firstPosition = tableView.mFirstPosition;
        int start = 0;
        int count = 0;

        if (toLeft) {
            final int galleryLeft = tableView.getPaddingLeft();
            for (int i = 0; i < numChildren; i++) {
                final View child = tableView.getChildAt(i);
                if (child.getRight() >= galleryLeft) {
                    break;
                } else {
                    count++;
                    tableView.mRecycler.addScrapView(child);
                }
            }
        } else {
            final int galleryRight = tableView.getWidth() - tableView.getPaddingRight();
            for (int i = numChildren - 1; i >= 0; i--) {
                final View child = tableView.getChildAt(i);
                if (child.getLeft() <= galleryRight) {
                    break;
                } else {
                    start = i;
                    count++;
                    tableView.mRecycler.addScrapView(child);
                }
            }
        }

        tableView.detachViewsFromParent(start, count);

        if (toLeft) {
            tableView.mFirstPosition += count;
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX) {
        int extremeItemPosition = motionToLeft ? tableView.mItemCount - 1 : 0;
        View extremeChild = tableView.getChildAt(extremeItemPosition - tableView.mFirstPosition);

        if (extremeChild == null) {
            return deltaX;
        }

        if(localLOGV) Log.v(TAG, "in getLimitedMotionScrollAmount, mCloseBouncing = " + mCloseBouncing);

           int extremeChildCenter = getCenterOfView(extremeChild);
        int galleryCenter = getCenterOfTable();

        if(tableView.isScrollOverBoundary && !mCloseBouncing){
            if(localLOGV)Log.i(TAG, "tableView.mMaxScrollOverhead = "+tableView.mMaxScrollOverhead);
            if (motionToLeft) {
                int centerDifference = galleryCenter - tableView.mMaxScrollOverhead - extremeChildCenter;
                if (extremeChildCenter <= galleryCenter - tableView.mMaxScrollOverhead) {
                    // The extreme child is past his boundary point!
                    return 0;
                }
                if(extremeChildCenter <= galleryCenter){
                    deltaX /= 2;
                }
                return Math.max(centerDifference, deltaX);
            } else {
                int centerDifference = galleryCenter + tableView.mMaxScrollOverhead - extremeChildCenter;
                if (extremeChildCenter >= galleryCenter + tableView.mMaxScrollOverhead) {
                    // The extreme child is past his boundary point!
                    return 0;
                }
                if(extremeChildCenter >= galleryCenter){
                    deltaX /= 2;
                }
                return Math.min(centerDifference, deltaX);
            }
        }
        else{
        // disable scroll overhead; 2010.03.11; Ferro
            if(motionToLeft){
                if (extremeChildCenter <= galleryCenter)
                    return 0;
            }
            else{
                if (extremeChildCenter >= galleryCenter)
                    return 0;
            }

            int centerDifference = galleryCenter - extremeChildCenter;

            return motionToLeft
                ? Math.max(centerDifference, deltaX)
                : Math.min(centerDifference, deltaX);
        // 2010.03.11; Ferro
        }

    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getCenterOfView(View view) {
        return (view.getLeft() + view.getRight()) / 2;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getCenterOfTable() {
        return (tableView.getWidth() - tableView.getPaddingLeft() - tableView.getPaddingRight()) / 2 + tableView.getPaddingLeft();
    }
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void scrollAmount(AbstractTableView.FlingRunnable runnable, int scrollAmount){
        runnable.startUsingDistance(scrollAmount, 0);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void scrollIntoSlots(AbstractTableView.FlingRunnable runnable) {
        if(localLOGV)Log.v(TAG, "scrollIntoSlot():");
        //get all visible views from TableView.
        View[] children = tableView.getAllVisibleViews();
        if(children == null || children.length <= 0) return;
        if(tableView.scrollControl == null)
            Log.e("TableScroll", "scrollIntoSlot(): tableView.scrollControl == null");
        else{
            ScrollControl.CenterView centerView = tableView.scrollControl.getCenterView(children, tableView.mFirstPosition);
            //If ScrollControl return null means it doesn't want to scroll anymore.
            if(centerView == null){
                tableView.onFinishedMovement();
                tableView.reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                return;
            }
            int centerChildCenter = (int)(centerView.view.getLeft() + centerView.view.getWidth() * centerView.percentage / 100.0f);
            int targetCenter = tableView.getWidth() / 2;
            int scrollAmount = targetCenter - centerChildCenter;
            if (scrollAmount != 0) {
                scrollAmount(runnable, scrollAmount);
            } else {
                tableView.onFinishedMovement();
                tableView.reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            }
        }
    }

    /**
     * Do the scroll.
     * @param deltaX The horizontal offset this time
     * @param deltaY The Vertical offset this time
     * @param isFling To indicate if it is fling or not
     * @hide
     */
    public void scrollWithConstrain(int deltaX, int deltaY, boolean isFling){
        boolean toLeft = deltaX < 0;

        int newDeltaX = getLimitedMotionScrollAmount(toLeft, deltaX);
        if (newDeltaX != deltaX && newDeltaX == 0 && isFling) {
            // The above call returned a limited amount, so stop any scrolls/flings
            Log.w("TableScroll", "newDeltaX != deltaX && newDeltaX == 0, trackMotionScroll call endFling("+false+")");
            tableView.mFlingRunnable.endFling(false);
        }
        tableView.trackMotionScroll(newDeltaX, 0);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean findAndSetSelecionInt(int direction, int startOfRowPos, int endOfRowPos, int selectedPosition) {
        switch (direction) {
        case View.FOCUS_UP:
            if (selectedPosition > startOfRowPos) {
                tableView.mLayoutMode = TableView.LAYOUT_MOVE_SELECTION;
                tableView.setSelectionInt(selectedPosition - 1);
                if(tableView != null && tableView.mOnScrollListener != null)
                    tableView.mOnScrollListener.onScrollStateChanged(tableView, AbstractTableView.OnScrollListener.SCROLL_STATE_FLING);
                return true;
            }
            break;
        case View.FOCUS_DOWN:
            if (selectedPosition < endOfRowPos) {
                tableView.mLayoutMode = TableView.LAYOUT_MOVE_SELECTION;
                tableView.setSelectionInt(selectedPosition + 1);
                if(tableView != null && tableView.mOnScrollListener != null)
                    tableView.mOnScrollListener.onScrollStateChanged(tableView, AbstractTableView.OnScrollListener.SCROLL_STATE_FLING);
                return true;
            }
            break;
        case View.FOCUS_LEFT:
            if (startOfRowPos > 0) {
                tableView.mLayoutMode = TableView.LAYOUT_MOVE_SELECTION;
                tableView.setSelectionInt(Math.max(0, selectedPosition - mNumColumnRows));
                if(tableView != null && tableView.mOnScrollListener != null)
                    tableView.mOnScrollListener.onScrollStateChanged(tableView, AbstractTableView.OnScrollListener.SCROLL_STATE_FLING);
                return true;
            }
            break;
        case View.FOCUS_RIGHT:
            if (endOfRowPos < tableView.getCount() - 1) {
                tableView.mLayoutMode = TableView.LAYOUT_MOVE_SELECTION;
                tableView.setSelectionInt(Math.min(selectedPosition + mNumColumnRows, tableView.getCount() - 1));
                if(tableView != null && tableView.mOnScrollListener != null)
                    tableView.mOnScrollListener.onScrollStateChanged(tableView, AbstractTableView.OnScrollListener.SCROLL_STATE_FLING);
                return true;
            }
            break;
        }
        return false;
    }

    //tiffanie
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setCloseBouncing(boolean close) {
        mCloseBouncing = close;
    }
}
