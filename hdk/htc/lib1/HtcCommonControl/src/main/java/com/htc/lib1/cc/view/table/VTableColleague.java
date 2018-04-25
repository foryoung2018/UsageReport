package com.htc.lib1.cc.view.table;

import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

import com.htc.lib1.cc.view.ScrollControl;
import com.htc.lib1.cc.view.table.AbstractTableView.FlingRunnable;
import com.htc.lib1.cc.view.table.AbstractTableView.OnScrollListener;

/**
 * VTableColleague
 */
public class VTableColleague extends TableColleague {
    private static final boolean ahanLog = false;
    private static final boolean localLOGV = false;
    private static final String TAG = "VTableColleague";

        /**
         * Constructor
         * @param view the TableView object related to this objecct
         */
    public VTableColleague(TableView view) {
        super(view);
        this.mGravity = Gravity.LEFT;
        this.mVerticalSpacing = tableView.mRequestedVerticalSpacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnTop(Rect rect) {
        return rect.top;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnBottom(Rect rect) {
        return rect.bottom;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnLeft(Rect rect) {
        return rect.left;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnRight(Rect rect) {
        return rect.right;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnTop(View view) {
        return view.getTop();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnBottom(View view) {
        return view.getBottom();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnLeft(View view) {
        return view.getLeft();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override public int getOrnRight(View view) {
        return view.getRight();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnWidth(View view) {
        return view.getWidth();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnHeight(View view) {
        return view.getHeight();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnMeasuredWidth(View view) {
        return view.getMeasuredWidth();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override public int getOrnMeasuredHeight(View view) {
        return view.getMeasuredHeight();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnWidth(AbstractTableView.LayoutParams params) {
        return params.width;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnHeight(AbstractTableView.LayoutParams params) {
        return params.height;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public OrnMeasureSpec getOrnMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {
        OrnMeasureSpec spec = new OrnMeasureSpec();
        spec.ornWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        spec.ornHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        spec.ornWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        spec.ornHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        spec.ornWidthMeasureSpec = widthMeasureSpec;
        spec.ornHeightMeasureSpec = heightMeasureSpec;
        return spec;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public AbstractTableView.LayoutParams getDefaultChildLayoutParams() {
        return new AbstractTableView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void measureView(View view, int ornWidthMeasureSpec, int ornHeightMeasureSpec) {
        view.measure(ornWidthMeasureSpec, ornHeightMeasureSpec);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getFadingEdgeLength() {
        return tableView.getVerticalFadingEdgeLength();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnLeftRightSpacing() {
        return this.mHorizontalSpacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setOrnLeftRightSpacing(int spacing) {
        this.mHorizontalSpacing = spacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnTopBottomSpacing() {
        return this.mVerticalSpacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setOrnTopBottomSpacing(int spacing) {
        this.mVerticalSpacing = spacing;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getScrollbarWidth() {
        return tableView.getVerticalScrollbarWidth();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setTableViewMeasuredDimension(int ornMeasuredWidth, int ornMeasuredHeight) {
        tableView.setMeasuredDimensionEx(ornMeasuredWidth, ornMeasuredHeight);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnRequestedWidthSpacing() {
        return tableView.mRequestedHorizontalSpacing;
    }


/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getOrnGravity() {
        return mGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void layoutView(View view, int ornLeft, int ornTop, int ornRight, int ornBottom) {
        view.layout(ornLeft, ornTop, ornRight, ornBottom);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void offsetLeftAndRight(View view, int offset) {
        view.offsetLeftAndRight(offset);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void offsetTopAndBottom(View view, int offset) {
        view.offsetTopAndBottom(offset);
    }

    /**
     * Offset the horizontal location of all children of this view by the specified number of pixels.
     *
     * @param offset the number of pixels to offset
     *
     * @hide
     */
    public void offsetOrnChildrenTopAndBottom(int offset) {

        final int count = tableView.getChildCount();

        for (int i = 0; i < count; i++) {
            final View v = tableView.getChildAt(i);
            v.offsetTopAndBottom(offset);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void fling(FlingRunnable runnable, float velocityX, float velocityY) {
        runnable.startUsingVelocity(0, (int) -velocityY);
    }



/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void trackMotionScrollOrn(int deltaX, int deltaY) {
        boolean toTop = deltaY < 0;
        int maxScrollAmount = tableView.getTableViewSlideOffset();
        /* trackMotionScroll should not set any limitation.
         * The limitation should put on
         * 1.onScroll in AbstractTableView
         * 2.Fling
        int limitedDeltaY = getLimitedMotionScrollAmount(toTop, deltaY);
        if (limitedDeltaY != deltaY) {
            // The above call returned a limited amount, so stop any scrolls/flings
            Log.w("TableScroll", "trackMotionScroll call endFling("+false+")");
            tableView.mFlingRunnable.endFling(false);
        }
        */
        deltaY = (Math.abs(deltaY)>maxScrollAmount?(toTop?-maxScrollAmount:maxScrollAmount):deltaY);
        tableView.offsetChildrenTopAndBottom(deltaY);
        tableView.blockLayoutRequests(true);
        detachOffScreenChildren(toTop);
        // If scroll to Top means we need to fill bottom. so down is true.
        fillGap(toTop);
        tableView.blockLayoutRequests(false);
    }

    /**
     * Detaches children that are off the screen (i.e.: Gallery bounds).
     *
     * @param toTop Whether to detach children to the top of the Gallery, or
     *            to the bottom.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void detachOffScreenChildren(boolean toTop) {
        int numChildren = tableView.getChildCount();
        int firstPosition = tableView.mFirstPosition;
        int start = 0;
        int count = 0;

        if (toTop) {
            final int galleryTop = tableView.getPaddingTop();
            for (int i = 0; i < numChildren; i++) {
                final View child = tableView.getChildAt(i);
                if (child.getBottom() >= galleryTop) {
                    break;
                } else {
                    count++;
                    tableView.mRecycler.addScrapView(child);
                }
            }
        } else {
            final int galleryBottom = tableView.getHeight() - tableView.getPaddingBottom();
            for (int i = numChildren - 1; i >= 0; i--) {
                final View child = tableView.getChildAt(i);
                if (child.getTop() <= galleryBottom) {
                    break;
                } else {
                    start = i;
                    count++;
                    tableView.mRecycler.addScrapView(child);
                }
            }
        }

        tableView.detachViewsFromParent(start, count);

        if (toTop) {
            tableView.mFirstPosition += count;
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getLimitedMotionScrollAmount(boolean motionToTop, int deltaY) {
        int extremeItemPosition = motionToTop ? tableView.mItemCount - 1 : 0;
        View extremeChild = tableView.getChildAt(extremeItemPosition - tableView.mFirstPosition);

        if (extremeChild == null) {
            return deltaY;
        }

        int extremeChildCenter = getCenterOfView(extremeChild);
        int galleryCenter = getCenterOfTable();

        //modified my tiffanie 20090601
        if (tableView.isScrollOverBoundary && !mCloseBouncing) {
            if (motionToTop) {
                int centerDifference = galleryCenter - tableView.mMaxScrollOverhead - extremeChildCenter;
                if (extremeChildCenter <= galleryCenter - tableView.mMaxScrollOverhead) {
                    // The extreme child is past his boundary point!
                    return 0;
                }

                if (extremeChildCenter <= galleryCenter) {
                    deltaY /= 2;
                }
                return Math.max(centerDifference, deltaY);
            } else {
                int centerDifference = galleryCenter + tableView.mMaxScrollOverhead - extremeChildCenter;
                if (extremeChildCenter >= galleryCenter + tableView.mMaxScrollOverhead) {
                    // The extreme child is past his boundary point!
                    return 0;
                }

                if (extremeChildCenter >= galleryCenter) {
                    deltaY /= 2;
                }
                return Math.min(centerDifference, deltaY);
            }
        }
        else {
            if (motionToTop) {
                if (extremeChildCenter <= galleryCenter) {
                    return 0;
                }
            }
            else {
                if (extremeChildCenter >= galleryCenter) {
                    return 0;
                }
            }

            int centerDifference = galleryCenter - extremeChildCenter;
            return motionToTop ? Math.max(centerDifference, deltaY) : Math.min(centerDifference, deltaY);
        }
        //end 20090601
    }

    private int old_willbeCenter = Integer.MAX_VALUE, old_stopIndex;
    /**
     * the scrolling function for multi-stops
     * @param motionToTop up or down
     * @param deltaY the acceleration according to the the touch event
     * @return the distance
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getLimitedMotionScrollAmount2(boolean motionToTop, int deltaY) {
        View extremeChild = null;
        if (mMultiStop && mStopExcept >= 0) {
                    int last_index = tableView.getCount() - 1;

            if (motionToTop) {
                int idx = 2;
                if (tableView.getChildAt(idx) != null) {
                    int willbeCenter = (Integer) tableView.getChildAt(idx).getTag();
                    int center = (Integer) tableView.getChildAt(1).getTag();
                    int stop_index = mStops.length - 1;

                    while (stop_index > -1 && willbeCenter < mStops[stop_index] && center != mStops[stop_index]) {
                        stop_index--;
                    }
                    if (stop_index <0 || last_index - mStopExcept == mStops[stop_index]) {
                        extremeChild = null;
                    } else {
                        int firstChild = (Integer) tableView.getChildAt(0).getTag();
                                        int targetPos = mStops[stop_index] - firstChild;
                                        if (targetPos<0) targetPos = targetPos + tableView.getCount();
                        extremeChild = tableView.getChildAt(targetPos);
                    }
                }
            } else {
                int idx = 0;
                if (tableView.getChildAt(idx) != null) {
                    int willbeCenter = (Integer) tableView.getChildAt(idx).getTag();
                    int stop_index = 0;

                                        if (mNewScroll) {
                                            old_willbeCenter = Integer.MAX_VALUE;
                                        }

                                        if (old_willbeCenter >= willbeCenter) {
                                            mNewScroll = false;
                        while (stop_index < mStops.length && willbeCenter > mStops[stop_index]) {
                        stop_index++;
                        }
                                            old_willbeCenter = willbeCenter;
                                            old_stopIndex = stop_index;
                                        } else {
                                            int currentCenter = (Integer) tableView.getChildAt(2).getTag(), i, j;
                                            for (i=0, j=mStops.length; i<j; i++)
                                                if (mStops[i] == currentCenter || mStops[i] == old_willbeCenter)
                                                    break;

                                            if (i < mStops.length) stop_index = old_stopIndex;
                                            else old_willbeCenter = Integer.MAX_VALUE;
                                        }

                    if (stop_index >= mStops.length || last_index - mStopExcept == mStops[stop_index]) {
                        extremeChild = null;
                    } else {
                        if (tableView.getChildAt(0) != null) {
                            int firstChild = (Integer) tableView.getChildAt(0).getTag();
                                                int targetPos = mStops[stop_index]-firstChild;
                                                if (targetPos<0) targetPos = targetPos + tableView.getCount();
                            extremeChild = tableView.getChildAt(targetPos);
                        }
                    }
                }
            }
        };

        if (extremeChild == null) {
            return deltaY;
        }

        int extremeChildCenter = getCenterOfView(extremeChild);
        int galleryCenter = getCenterOfTable();

        //modified my tiffanie 20090601
        if (tableView.isScrollOverBoundary && !mCloseBouncing) {
            if (motionToTop) {
                int centerDifference = galleryCenter - tableView.mMaxScrollOverhead - extremeChildCenter;
                if (extremeChildCenter <= galleryCenter - tableView.mMaxScrollOverhead) {
                    // The extreme child is past his boundary point!
                    return 0;
                }
                if (extremeChildCenter <= galleryCenter) {
                    deltaY /= 2;
                }
                return Math.max(centerDifference, deltaY);
            } else {
                int centerDifference = galleryCenter + tableView.mMaxScrollOverhead - extremeChildCenter;
                if (extremeChildCenter >= galleryCenter + tableView.mMaxScrollOverhead) {
                    // The extreme child is past his boundary point!
                    return 0;
                }
                if (extremeChildCenter >= galleryCenter) {
                    deltaY /= 2;
                }
                return Math.min(centerDifference, deltaY);
            }
        }
        else {
            if(motionToTop) {
                if (extremeChildCenter == galleryCenter)
                    return 0;

                if (extremeChildCenter < galleryCenter)
                    return deltaY;
                //if (extremeChildCenter > galleryCenter)
                    //return deltaY;
            }
            else {
                int centerDifference = galleryCenter - extremeChildCenter;
                if (extremeChildCenter == galleryCenter) {
                    // The extreme child is past his boundary point!
                    return 0;
                }

                if (extremeChildCenter > galleryCenter)
                    return deltaY;

            }

            int centerDifference = galleryCenter - extremeChildCenter;

            return motionToTop
                ? Math.max(centerDifference, deltaY)
                : Math.min(centerDifference, deltaY);
        }
        //end 20090601

    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getCenterOfView(View view) {
        return (view.getTop() + view.getBottom()) / 2;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getCenterOfTable() {
        return (tableView.getHeight() - tableView.getPaddingTop() - tableView.getPaddingBottom()) / 2 + tableView.getPaddingTop();
    }
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void scrollAmount(AbstractTableView.FlingRunnable runnable, int scrollAmount){
        runnable.startUsingDistance(0, scrollAmount);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void scrollIntoSlots(AbstractTableView.FlingRunnable runnable) {
        //get all visible views from TableView.
        View[] children = tableView.getAllVisibleViews();
        if (children == null || children.length <= 0) return;
        if (tableView.scrollControl == null) return;
        else {
            ScrollControl.CenterView centerView = tableView.scrollControl.getCenterView(children, tableView.mFirstPosition);
            //If ScrollControl return null means it doesn't want to scroll anymore.
            if (centerView == null) {
                tableView.onFinishedMovement();
                tableView.reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                return;
            }
            int centerChildCenter = (int)(centerView.view.getTop() + centerView.view.getHeight() * centerView.percentage / 100.0f);
            int targetCenter = tableView.getHeight() / 2;
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
    public void scrollWithConstrain(int deltaX, int deltaY, boolean isFling) {
        boolean toTop = deltaY < 0;
        int newDeltaY;

        //Modified by Ahan 2012/04/11, if tableview is in countdown mode, we should not consider multistop.
        //Because multistop on countdown mode will cause an issue for countdown.
        if (tableView.isInCountDownMode() || !mMultiStop) {
            newDeltaY = mRepeatEnable ? deltaY : getLimitedMotionScrollAmount(toTop, deltaY);
        } else {
            if (mStopExcept < 0) // multi-stop not enabled
                newDeltaY = mRepeatEnable ? deltaY : getLimitedMotionScrollAmount(toTop, deltaY);
            else // multi-stop enabled
                newDeltaY = mRepeatEnable ? getLimitedMotionScrollAmount2(toTop, deltaY) : getLimitedMotionScrollAmount(toTop, deltaY);
        }

        if (newDeltaY != deltaY && newDeltaY == 0 && isFling) {
            // The above call returned a limited amount, so stop any scrolls/flings
            tableView.mFlingRunnable.endFling(false);
        }

        tableView.trackMotionScroll(0, newDeltaY);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean findAndSetSelecionInt(int direction, int startOfRowPos, int endOfRowPos, int selectedPosition) {
        switch (direction) {
        case View.FOCUS_UP:
            if (startOfRowPos > 0) {
                tableView.mLayoutMode = TableView.LAYOUT_MOVE_SELECTION;
                tableView.setSelectionInt(Math.max(0, selectedPosition - mNumColumnRows));
                if(tableView != null && tableView.mOnScrollListener != null)
                    tableView.mOnScrollListener.onScrollStateChanged(tableView, AbstractTableView.OnScrollListener.SCROLL_STATE_FLING);
                return true;
            }
            break;
        case View.FOCUS_DOWN:
            if (endOfRowPos < tableView.getCount() - 1) {
                tableView.mLayoutMode = TableView.LAYOUT_MOVE_SELECTION;
                tableView.setSelectionInt(Math.min(selectedPosition + mNumColumnRows, tableView.getCount() - 1));
                if(tableView != null && tableView.mOnScrollListener != null)
                    tableView.mOnScrollListener.onScrollStateChanged(tableView, AbstractTableView.OnScrollListener.SCROLL_STATE_FLING);
                return true;
            }
            break;
        case View.FOCUS_LEFT:
            if (selectedPosition > startOfRowPos) {
                tableView.mLayoutMode = TableView.LAYOUT_MOVE_SELECTION;
                tableView.setSelectionInt(selectedPosition - 1);
                if(tableView != null && tableView.mOnScrollListener != null)
                    tableView.mOnScrollListener.onScrollStateChanged(tableView, AbstractTableView.OnScrollListener.SCROLL_STATE_FLING);
                return true;
            }
            break;
        case View.FOCUS_RIGHT:
            if (selectedPosition < endOfRowPos) {
                tableView.mLayoutMode = TableView.LAYOUT_MOVE_SELECTION;
                tableView.setSelectionInt(selectedPosition + 1);
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
