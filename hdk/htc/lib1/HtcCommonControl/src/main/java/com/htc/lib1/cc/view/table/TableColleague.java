package com.htc.lib1.cc.view.table;

import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

/**
 * TableColleague object, used to control the scroll behavior of TableView.
 */
abstract public class TableColleague {
    private static final String TAG = "TableColleague";
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected TableView tableView;

    /**
     * The top scroll indicator
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View mScrollUpLeft;

    /**
     * The down scroll indicator
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View mScrollDownRight;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int AUTO_FIT = -1;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mNumColumnRows = AUTO_FIT;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mHorizontalSpacing = 0;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mVerticalSpacing = 0;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mOrnWidth;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View mReferenceView = null;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View mReferenceViewInSelectedRow = null;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mGravity;
    //tiffanie
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected boolean mCloseBouncing = true;
//    protected int mMaxScrollOverhead;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected boolean mRepeatEnable = false;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected boolean mMultiStop = false;

    /**
     * OrnMeasureSpec
     */
    public static class OrnMeasureSpec {
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected int ornWidthMode;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected int ornHeightMode;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected int ornWidthSize;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected int ornHeightSize;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected int ornWidthMeasureSpec;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected int ornHeightMeasureSpec;
    }

    /**
     * Constructor
     * @param view The table view object which this TableColleague object works on
     */
    public TableColleague(TableView view) {
        this.tableView = view;
    }

    /**
     * Calculate the top-most pixel we can draw the selection into
     *
     * @param childrenTop Top pixel were children can be drawn
     * @param fadingEdgeLength Length of the fading edge in pixels, if present
     * @param rowStart The start of the row that will contain the selection
     * @return The top-most pixel we can draw the selection into
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int rowStart) {
        // first pixel we can draw the selection into
        int topSelectionPixel = childrenTop;
        if (rowStart > 0) {
            topSelectionPixel += fadingEdgeLength;
        }
        return topSelectionPixel;
    }

    /**
     * Calculate the bottom-most pixel we can draw the selection into
     *
     * @param childrenBottom Bottom pixel were children can be drawn
     * @param fadingEdgeLength Length of the fading edge in pixels, if present
     * @param numColumns Number of columns in the grid
     * @param rowStart The start of the row that will contain the selection
     * @param itemCount Item counts
     * @return The bottom-most pixel we can draw the selection into
     * @hide
     */
    public static int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength,
            int numColumns, int rowStart, int itemCount) {
        // Last pixel we can draw the selection into
        int bottomSelectionPixel = childrenBottom;
        if (rowStart + numColumns - 1 < itemCount - 1) {
            bottomSelectionPixel -= fadingEdgeLength;
        }
        return bottomSelectionPixel;
    }

    /**
     * Move all views upwards so the selected row does not interesect the top
     * fading edge (if necessary).
     *
     * @param childInSelectedRow A child in the row that contains the selection
     * @param topSelectionPixel The topmost pixel we can draw the selection into
     * @param bottomSelectionPixel The bottommost pixel we can draw the selection into
     * @hide
     */
    protected void adjustForTopFadingEdge(View childInSelectedRow,
            int topSelectionPixel, int bottomSelectionPixel) {
        // Some of the newly selected item extends above the top of the list
        if (getOrnTop(childInSelectedRow) < topSelectionPixel) {
            // Find space required to bring the top of the selected item
            // fully into view
            int spaceAbove = topSelectionPixel - getOrnTop(childInSelectedRow);

            // Find space available below the selection into which we can
            // scroll downwards
            int spaceBelow = bottomSelectionPixel - getOrnBottom(childInSelectedRow);
            int offset = Math.min(spaceAbove, spaceBelow);

            // Now offset the selected item to get it into view
            offsetOrnChildrenTopAndBottom(offset);
        }
    }

    /**
     * Move all views upwards so the selected row does not interesect the bottom
     * fading edge (if necessary).
     *
     * @param childInSelectedRow A child in the row that contains the selection
     * @param topSelectionPixel The topmost pixel we can draw the selection into
     * @param bottomSelectionPixel The bottommost pixel we can draw the selection into
     * @hide
     */
    protected void adjustForBottomFadingEdge(View childInSelectedRow,
            int topSelectionPixel, int bottomSelectionPixel) {
        // Some of the newly selected item extends below the bottom of the
        // list
        if (getOrnBottom(childInSelectedRow) > bottomSelectionPixel) {

            // Find space available above the selection into which we can
            // scroll upwards
            int spaceAbove = getOrnTop(childInSelectedRow) - topSelectionPixel;

            // Find space required to bring the bottom of the selected item
            // fully into view
            int spaceBelow = getOrnBottom(childInSelectedRow) - bottomSelectionPixel;
            int offset = Math.min(spaceAbove, spaceBelow);

            // Now offset the selected item to get it into view
            offsetOrnChildrenTopAndBottom(-offset);
        }
    }

    /**
     * Fills the list from pos up to the top of the list view.
     *
     * @param pos The first position to put in the list
     *
     * @param nextOrnBottom The location where the bottom of the item associated
     *        with pos should be drawn
     *
     * @return The view that is currently selected
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillUp(int pos, int nextOrnBottom) {
        View selectedView = null;

        final int end = this.getOrnTop(tableView.mListPadding);

        while (nextOrnBottom > end && pos >= 0) {
            View temp = makeRowColumn(pos, nextOrnBottom, false);

            if (temp != null) {
                selectedView = temp;
            }

            nextOrnBottom = getOrnTop(mReferenceView) - this.getOrnTopBottomSpacing();
            tableView.mFirstPosition = pos;
            pos -= mNumColumnRows;
        }

        if (mRepeatEnable && nextOrnBottom > end && pos<0) {
            pos+=tableView.mItemCount;

            while (nextOrnBottom > end && pos >= 0) {
                View temp = makeRowColumn(pos, nextOrnBottom, false);

                if (temp != null) {
                    selectedView = temp;
                }

                nextOrnBottom = getOrnTop(mReferenceView) - this.getOrnTopBottomSpacing();
                tableView.mFirstPosition = pos;
                pos -= mNumColumnRows;
            }
        }

        if (tableView.mStackFromBottom) {
            tableView.mFirstPosition = Math.max(0, pos + 1);
        }

        return selectedView;
    }

    /**
     * Fills the list from pos down to the end of the list view.
     *
     * @param pos The first position to put in the list
     *
     * @param nextOrnTop The location where the top of the item associated with pos
     *        should be drawn
     *
     * @return The view that is currently selected, if it happens to be in the
     *         range that we draw.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillDown(int pos, int nextOrnTop) {
        View selectedView = null;
        int bottom = this.getOrnBottom(tableView);
        int top = this.getOrnTop(tableView);
        final int end = (bottom - top) - getOrnBottom(tableView.mListPadding);

        while (nextOrnTop < end && pos < tableView.mItemCount) {
            View temp = makeRowColumn(pos, nextOrnTop, true);
            if (temp != null) {
                selectedView = temp;
            }

            nextOrnTop = getOrnBottom(mReferenceView) + getOrnTopBottomSpacing();
            pos += mNumColumnRows;
        }

        if (mRepeatEnable && nextOrnTop < end && pos >= tableView.mItemCount) {
            tableView.mFirstPosition -= tableView.mItemCount;
            pos -= tableView.mItemCount;

            while (nextOrnTop < end && pos < tableView.mItemCount) {
                View temp = makeRowColumn(pos, nextOrnTop, true);

                if (temp != null) {
                    selectedView = temp;
                }

                nextOrnTop = getOrnBottom(mReferenceView) + getOrnTopBottomSpacing();
                pos += mNumColumnRows;
            }
        }

        return selectedView;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View makeRowColumn(int startPos, int ornTop, boolean flow) {
        int last;
        int nextOrnLeft = getOrnLeft(tableView.mListPadding);

        final int ornWidth = mOrnWidth;
        //final int adjacentSpacing = this.getOrnLeftRightSpacing();
        if (!tableView.mStackFromBottom) {
            last = Math.min(startPos + mNumColumnRows, tableView.getCount());
        } else {
            last = startPos + 1;
            startPos = Math.max(0, startPos - mNumColumnRows + 1);
        }

        View selectedView = null;

        final boolean hasFocus = tableView.shouldShowSelector();
        final boolean inClick = tableView.touchModeDrawsInPressedState();
        final int selectedPosition = tableView.mSelectedPosition;

        mReferenceView = null;
        for (int pos = startPos; pos < last; pos++) {
            // is this the selected item?
            boolean selected = pos == selectedPosition;
            // does the list view have focus or contain focus

            final int where = flow ? -1 : pos - startPos;
            final View child = makeAndAddView(pos, ornTop, flow, nextOrnLeft, selected, where);
            mReferenceView = child;

            nextOrnLeft += ornWidth;
            if (pos < last - 1) {
                nextOrnLeft += this.getOrnLeftRightSpacing();
            }

            if (selected && (hasFocus || inClick)) {
                selectedView = child;
            }
        }

        if (selectedView != null) {
            mReferenceViewInSelectedRow = mReferenceView;
        }

        return selectedView;
    }

    /**
     * Obtain the view and add it to our list of children. The view can be made
     * fresh, converted from an unused view, or used as is if it was in the
     * recycle bin.
     *
     * @param position Logical position in the list
     * @param ornTop Top/left or bottom/right edge of the view to add
     * @param flow if true, align top/left edge to y/x. If false, align bottom/right edge to
     *        y/x.
     * @param childrenoOrnLeft Left edge where children should be positioned
     * @param selected Is this position selected?
     * @param where to add new item in the list
     * @return View that was added
     */
    private View makeAndAddView(int position, int ornTop, boolean flow, int childrenoOrnLeft, boolean selected, int where) {
        View child;

        if (!tableView.mDataChanged) {
            // Try to use an exsiting view for this position
            child = tableView.mRecycler.getActiveView(position);
            if (child != null) {
                // Found it -- we're using an existing child
                // This just needs to be positioned
                setupChild(child, position, ornTop, flow, childrenoOrnLeft, selected, true, where);
                return child;
            }
        }

        // Make a new view for this position, or convert an unused view if
        // possible
        child = tableView.obtainView(position);
        child.setTag(position);

        // This needs to be positioned and measured
        setupChild(child, position, ornTop, flow, childrenoOrnLeft, selected, false, where);
        return child;
    }

    /**
     * Make sure views are touching the top or bottom edge, as appropriate for
     * our gravity
     */
    // Remove adjustViewsUpOrDown()
/*    protected void adjustViewsUpOrDown() {
        final int childCount = tableView.getChildCount();

        if (childCount > 0) {
            int ornDelta;
            View child;

            if (!tableView.mStackFromBottom) {
                // Uh-oh -- we came up short. Slide all views up to make them
                // align with the top
                child = tableView.getChildAt(0);
                ornDelta = getOrnTop(child) - getOrnTop(tableView.mListPadding);
                if (tableView.mFirstPosition != 0) {
                    // It's OK to have some space above the first item if it is
                    // part of the vertical spacing
                    ornDelta -= this.getOrnTopBottomSpacing();
                }
                if (ornDelta < 0) {
                    // We only are looking to see if we are too low, not too high
                    ornDelta = 0;
                }
            } else {
                // we are too high, slide all views down to align with bottom
                child = tableView.getChildAt(childCount - 1);
                ornDelta = getOrnBottom(child) - (getOrnHeight(tableView) - getOrnBottom(tableView.mListPadding));

                if (tableView.mFirstPosition + childCount < tableView.mItemCount) {
                    // It's OK to have some space below the last item if it is
                    // part of the vertical spacing
                    ornDelta += this.getOrnTopBottomSpacing();
                }

                if (ornDelta > 0) {
                    // We only are looking to see if we are too high, not too low
                    ornDelta = 0;
                }
            }

            if (ornDelta != 0) {
                offsetOrnChildrenTopAndBottom(-ornDelta);
            }
        }
    }
*/
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillFromSelection(int selectedOrnTop, int childrenOrnTop, int childrenOrnBottom) {
        final int fadingEdgeLength = getFadingEdgeLength();
        final int selectedPosition = tableView.mSelectedPosition;
        final int numColumns = mNumColumnRows;
        final int ornTopBottomSpacing = getOrnTopBottomSpacing();

        int rowStart;
        int rowEnd = -1;

        if (!tableView.mStackFromBottom) {
            rowStart = selectedPosition - (selectedPosition % numColumns);
        } else {
            int invertedSelection = tableView.mItemCount - 1 - selectedPosition;

            rowEnd = tableView.mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
            rowStart = Math.max(0, rowEnd - numColumns + 1);
        }

        View sel;
        View referenceView;

        int topSelectionPixel = getTopSelectionPixel(childrenOrnTop, fadingEdgeLength, rowStart);
        int bottomSelectionPixel = getBottomSelectionPixel(childrenOrnBottom, fadingEdgeLength,
                numColumns, rowStart, tableView.mItemCount);
        sel = makeRowColumn(tableView.mStackFromBottom ? rowEnd : rowStart, selectedOrnTop, true);
        // Possibly changed again in fillUp if we add rows above this one.
        tableView.mFirstPosition = rowStart;

        referenceView = mReferenceView;
        adjustForTopFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        adjustForBottomFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);

        if (referenceView == null)
            throw new IllegalArgumentException("referenceView is null, please check the child has made in makeRowColumn()");

        if (!tableView.mStackFromBottom) {
            fillUp(rowStart - numColumns, referenceView.getTop() - ornTopBottomSpacing);
            // Remove adjustViewsUpOrDown()            adjustViewsUpOrDown();
            fillDown(rowStart + numColumns, referenceView.getBottom() + ornTopBottomSpacing);
        } else {
            fillDown(rowEnd + numColumns, referenceView.getBottom() + ornTopBottomSpacing);
            // Remove adjustViewsUpOrDown()            adjustViewsUpOrDown();
            fillUp(rowStart - 1, referenceView.getTop() - ornTopBottomSpacing);
        }

        return sel;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillSelection(int childrenOrnTop, int childrenOrnBottom) {
        final int selectedPosition = tableView.reconcileSelectedPosition();
        final int numColumnRows = mNumColumnRows;
        final int ornTopBottomSpacing = getOrnTopBottomSpacing();

        int rowStart;
        int rowEnd = -1;

        if (!tableView.mStackFromBottom) {
            rowStart = selectedPosition - (selectedPosition % numColumnRows);
        } else {
            final int invertedSelection = tableView.mItemCount - 1 - selectedPosition;

            rowEnd = tableView.mItemCount - 1 - (invertedSelection - (invertedSelection % numColumnRows));
            rowStart = Math.max(0, rowEnd - numColumnRows + 1);
        }

        final int fadingEdgeLength = getFadingEdgeLength();
        final int topSelectionPixel = getTopSelectionPixel(childrenOrnTop, fadingEdgeLength, rowStart);
        final View sel = makeRowColumn(tableView.mStackFromBottom ? rowEnd : rowStart, topSelectionPixel, true);
        tableView.mFirstPosition = rowStart;

        final View referenceView = mReferenceView;

        if (referenceView == null)
            throw new IllegalArgumentException("referenceView is null, please check the child has made in makeRowColumn()");

        if (!tableView.mStackFromBottom) {
            fillDown(rowStart + numColumnRows, getOrnBottom(referenceView) + ornTopBottomSpacing);
            pinToBottom(childrenOrnBottom);
            fillUp(rowStart - numColumnRows, getOrnTop(referenceView) - ornTopBottomSpacing);
            // Remove adjustViewsUpOrDown()            adjustViewsUpOrDown();
        } else {
            final int bottomSelectionPixel = getBottomSelectionPixel(childrenOrnBottom,
                fadingEdgeLength, numColumnRows, rowStart, tableView.mItemCount);
            final int offset = bottomSelectionPixel - referenceView.getBottom();
            offsetOrnChildrenTopAndBottom(offset);
            fillUp(rowStart - 1, referenceView.getTop() - ornTopBottomSpacing);
            pinToTop(childrenOrnTop);
            fillDown(rowEnd + numColumnRows, referenceView.getBottom() + ornTopBottomSpacing);
            // Remove adjustViewsUpOrDown()            adjustViewsUpOrDown();
        }

        return sel;
    }

    private void pinToTop(int childrenTop) {
        if (tableView.mFirstPosition == 0) {
            final int top = tableView.getChildAt(0).getTop();
            final int offset = childrenTop - top;
            if (offset < 0) {
                offsetOrnChildrenTopAndBottom(offset);
            }
        }
    }

    private void pinToBottom(int childrenBottom) {
        final int count = tableView.getChildCount();
        if (tableView.mFirstPosition + count == tableView.mItemCount) {
            final int bottom = tableView.getChildAt(count - 1).getBottom();
            final int offset = childrenBottom - bottom;
            if (offset > 0) {
                offsetOrnChildrenTopAndBottom(offset);
            }
        }
    }

    /**
     * Layout during a scroll that results from tracking motion events. Places
     * the mMotionPosition view at the offset specified by mMotionViewTop, and
     * then build surrounding views from there.
     *
     * @param position the position at which to start filling
     * @param ornTop the top of the view at that position
     * @return The selected view, or null if the selected view is outside the
     *         visible area.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillSpecific(int position, int ornTop) {
        final int numColumnRows = mNumColumnRows;

        int motionRowStart;
        int motionRowEnd = -1;

        if (!tableView.mStackFromBottom) {
            motionRowStart = position - (position % numColumnRows);
        } else {
            final int invertedSelection = tableView.mItemCount - 1 - position;
            motionRowEnd = tableView.mItemCount - 1 - (invertedSelection - (invertedSelection % numColumnRows));
            motionRowStart = Math.max(0, motionRowEnd - numColumnRows + 1);
        }

        final View temp = makeRowColumn(tableView.mStackFromBottom ? motionRowEnd : motionRowStart, ornTop, true);

        // Possibly changed again in fillUp if we add rows above this one.
        tableView.mFirstPosition = motionRowStart;

        final View referenceView = mReferenceView;
        final int ornTopBottomSpacing = getOrnTopBottomSpacing();

        View above;
        View below;

        if (!tableView.mStackFromBottom) {
            above = fillUp(motionRowStart - numColumnRows, getOrnTop(referenceView) - ornTopBottomSpacing);
            below = fillDown(motionRowStart + numColumnRows, getOrnBottom(referenceView) + ornTopBottomSpacing);
        } else {
            below = fillDown(motionRowEnd + numColumnRows, getOrnBottom(referenceView) + ornTopBottomSpacing);
            above = fillUp(motionRowStart - 1, getOrnTop(referenceView) - ornTopBottomSpacing);
        }

        if (temp != null) {
            return temp;
        } else if (above != null) {
            return above;
        } else {
            return below;
        }
    }

    /**
     * Fills the grid based on positioning the new selection relative to the old
     * selection. The new selection will be placed at, above, or below the
     * location of the new selection depending on how the selection is moving.
     * The selection will then be pinned to the visible part of the screen,
     * excluding the edges that are faded. The grid is then filled upwards and
     * downwards from there.
     *
     * @param delta Which way we are moving
     * @param childrenOrnTop Where to start drawing children
     * @param childrenOrnBottom Last pixel where children can be drawn
     * @return The view that currently has selection
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View moveSelection(int delta, int childrenOrnTop, int childrenOrnBottom) {
        final int fadingEdgeLength = getFadingEdgeLength();
        final int selectedPosition = tableView.mSelectedPosition;
        final int numColumns = mNumColumnRows;
        final int ornTopBottomSpacing = getOrnTopBottomSpacing();

        int oldRowStart;
        int rowStart;
        int rowEnd = -1;

        if (!tableView.mStackFromBottom) {
            oldRowStart = (selectedPosition - delta) - ((selectedPosition - delta) % numColumns);

            rowStart = selectedPosition - (selectedPosition % numColumns);
        } else {
            int invertedSelection = tableView.mItemCount - 1 - selectedPosition;

            rowEnd = tableView.mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
            rowStart = Math.max(0, rowEnd - numColumns + 1);

            invertedSelection = tableView.mItemCount - 1 - (selectedPosition - delta);
            oldRowStart = tableView.mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
            oldRowStart = Math.max(0, oldRowStart - numColumns + 1);
        }
        // get the row delta
        final int rowDelta = rowStart - oldRowStart;

        final int topSelectionPixel = getTopSelectionPixel(childrenOrnTop, fadingEdgeLength, rowStart);
        final int bottomSelectionPixel = getBottomSelectionPixel(childrenOrnBottom, fadingEdgeLength,
                numColumns, rowStart, tableView.mItemCount);

        // Possibly changed again in fillUp if we add rows above this one.
        tableView.mFirstPosition = rowStart;

        View sel;
        View referenceView;

        if (rowDelta > 0) {
            /*
             * Case 1: Scrolling down.
             */

            final int oldOrnBottom = mReferenceViewInSelectedRow == null ? 0 :
                    getOrnBottom(mReferenceViewInSelectedRow);
            sel = makeRowColumn(tableView.mStackFromBottom ? rowEnd : rowStart, oldOrnBottom + ornTopBottomSpacing, true);
            referenceView = mReferenceView;

            adjustForBottomFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        } else if (rowDelta < 0) {
            /*
             * Case 2: Scrolling up.
             */
            final int oldOrnTop = mReferenceViewInSelectedRow == null ?
                    0 : getOrnTop(mReferenceViewInSelectedRow);
            sel = makeRowColumn(tableView.mStackFromBottom ? rowEnd : rowStart, oldOrnTop - ornTopBottomSpacing, false);
            referenceView = mReferenceView;

            adjustForTopFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        } else {
            /*
             * Keep selection where it was
             */
            final int oldOrnTop = mReferenceViewInSelectedRow == null ?
                    0 : getOrnTop(mReferenceViewInSelectedRow);
            sel = makeRowColumn(tableView.mStackFromBottom ? rowEnd : rowStart, oldOrnTop, true);
            referenceView = mReferenceView;
        }

        if (!tableView.mStackFromBottom) {
            fillUp(rowStart - numColumns, getOrnTop(referenceView) - ornTopBottomSpacing);
         // Remove adjustViewsUpOrDown()            adjustViewsUpOrDown();
            fillDown(rowStart + numColumns, getOrnBottom(referenceView) + ornTopBottomSpacing);
        } else {
            fillDown(rowEnd + numColumns, getOrnBottom(referenceView) + ornTopBottomSpacing);
         // Remove adjustViewsUpOrDown()            adjustViewsUpOrDown();
            fillUp(rowStart - 1, getOrnTop(referenceView) - ornTopBottomSpacing);
        }

        return sel;
    }

    /**
     *
     * @param selectChildPosition
     * @param childrenOrnTop
     * @param childrenOrnBottom
     * @param childHeight The child orientation height. For landscape mode childHeight means the item view height.
     * @param percentage 0 means child top will align parent center. 50 means child center will align parent center. 100 means child bottom will align parent center.
     * @return
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View moveSelectionCenter(int selectChildPosition, int childrenOrnTop, int childrenOrnBottom, int childHeight, int percentage) {
        final int numColumns = mNumColumnRows;
        final int ornTopBottomSpacing = getOrnTopBottomSpacing();

        int rowStart;
        int rowEnd = -1;

        if (!tableView.mStackFromBottom) {
            rowStart = selectChildPosition - (selectChildPosition % numColumns);
        } else {
            int invertedSelection = tableView.mItemCount - 1 - selectChildPosition;

            rowEnd = tableView.mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
            rowStart = Math.max(0, rowEnd - numColumns + 1);
        }

        int topSelectionPixel;
        if(tableView.isSetTableViewHeight){
            topSelectionPixel = (tableView.mTableViewOrnHeight * 50 - childHeight * percentage) / 100;
        }
        else{
            topSelectionPixel = (childrenOrnBottom - childrenOrnTop - childHeight) / 2;
        }

        // Possibly changed again in fillUp if we add rows above this one.
        tableView.mFirstPosition = rowStart;

        View sel;
        View referenceView;

        //TODO not support statck from bottom now.
        sel = makeRowColumn(rowStart, topSelectionPixel, true);
        referenceView = mReferenceView;

        //TODO Do we need to check bottom fading edge
        fillUp(rowStart - numColumns, getOrnTop(referenceView) - ornTopBottomSpacing);
        fillDown(rowStart + numColumns, getOrnBottom(referenceView) + ornTopBottomSpacing);

        tableView.callbackCenterViewSetListener(referenceView);
        return sel;
    }

    /**
     * Fills the list from top to bottom, starting with mFirstPosition
     *
     * @param nextOrnTop The location where the top of the first item should be
     *        drawn
     *
     * @return The view that is currently selected
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillFromTop(int nextOrnTop) {
        tableView.mFirstPosition = Math.min(tableView.mFirstPosition, tableView.mSelectedPosition);
        tableView.mFirstPosition = Math.min(tableView.mFirstPosition, tableView.mItemCount - 1);
        if (tableView.mFirstPosition < 0) {
            tableView.mFirstPosition = 0;
        }
        tableView.mFirstPosition -= tableView.mFirstPosition % mNumColumnRows;
        return fillDown(tableView.mFirstPosition, nextOrnTop);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillFromBottom(int lastPosition, int nextOrnBottom) {
        lastPosition = Math.max(lastPosition, tableView.mSelectedPosition);
        lastPosition = Math.min(lastPosition, tableView.mItemCount - 1);

        final int invertedPosition = tableView.mItemCount - 1 - lastPosition;
        lastPosition = tableView.mItemCount - 1 - (invertedPosition - (invertedPosition % mNumColumnRows));

        return fillUp(lastPosition, nextOrnBottom);
    }

    /**
     * Fills the gap left open by a touch-scroll. During a touch scroll, children that
     * remain on screen are shifted and the other ones are discarded. The role of this
     * method is to fill the gap thus created by performing a partial layout in the
     * empty space.
     *
     * @param down true if the scroll is going down, false if it is going up
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void fillGap(boolean down) {
        final int numColumns = mNumColumnRows;
        final int ornTopBottomSpacing = getOrnTopBottomSpacing();
        final int count = tableView.getChildCount();

        if (down) {
            final int startOffset = count > 0 ?
                    getOrnBottom(tableView.getChildAt(count - 1)) + ornTopBottomSpacing : getOrnTop(tableView.mListPadding);
            int position = tableView.mFirstPosition + count;
            if (tableView.mStackFromBottom) {
                position += numColumns - 1;
            }
            fillDown(position, startOffset);
            //correctTooHigh(numColumns, ornTopBottomSpacing, tableView.getChildCount());
        } else {
            final int startOffset = count > 0 ?
                    getOrnTop(tableView.getChildAt(0)) - ornTopBottomSpacing : getOrnHeight(tableView) - getOrnBottom(tableView.mListPadding);
            int position = tableView.mFirstPosition;
            if (!tableView.mStackFromBottom) {
                position -= numColumns;
            } else {
                position--;
            }
            fillUp(position, startOffset);
            //correctTooLow(numColumns, ornTopBottomSpacing, tableView.getChildCount());
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        OrnMeasureSpec spec = this.getOrnMeasureSpec(widthMeasureSpec, heightMeasureSpec);
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int ornLeftPadding = getOrnLeft(tableView.mListPadding);
        int ornRightPadding = getOrnRight(tableView.mListPadding);
        int ornTopPadding = getOrnTop(tableView.mListPadding);
        int ornBottomPadding = getOrnBottom(tableView.mListPadding);

        if (spec.ornWidthMode == MeasureSpec.UNSPECIFIED) {
            if (mOrnWidth > 0) {
                spec.ornWidthSize = mOrnWidth + ornLeftPadding + ornRightPadding;
            } else {
                spec.ornWidthSize = ornLeftPadding + ornRightPadding;
            }
            spec.ornWidthSize += getScrollbarWidth();
        }

        int childOrnWidth = spec.ornWidthSize - ornLeftPadding - ornRightPadding;
        determineColumnRows(childOrnWidth);

        int childOrnHeight = 0;

        tableView.mItemCount = tableView.mAdapter == null ? 0 : tableView.mAdapter.getCount();
        final int count = tableView.mItemCount;
        if (count > 0) {
            final View child = tableView.obtainView(0);
            final int childViewType = tableView.mAdapter.getItemViewType(0);

            AbstractTableView.LayoutParams lp = (AbstractTableView.LayoutParams) child.getLayoutParams();
            if (lp == null) {
                lp = getDefaultChildLayoutParams();
                child.setLayoutParams(lp);
            }
            lp.viewType = childViewType;

            final int childOrnWidthSpec = ViewGroup.getChildMeasureSpec(spec.ornWidthMeasureSpec,
                    ornLeftPadding + ornRightPadding, getOrnWidth(lp));

            int lpOrnHeight = getOrnHeight(lp);

            int childOrnHeightSpec;
            if (lpOrnHeight > 0) {
                childOrnHeightSpec = MeasureSpec.makeMeasureSpec(lpOrnHeight, MeasureSpec.EXACTLY);
            } else {
                childOrnHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }

            measureView(child, childOrnWidthSpec, childOrnHeightSpec);
            childOrnHeight = getOrnMeasuredHeight(child);

            if (tableView.mRecycler.shouldRecycleViewType(childViewType)) {
                tableView.mRecycler.addScrapView(child);
            }
        }

        if (spec.ornHeightMode == MeasureSpec.UNSPECIFIED) {
            spec.ornHeightMode = ornTopPadding + ornBottomPadding + childOrnHeight +
                    getFadingEdgeLength() * 2;
        }

        if (spec.ornHeightMode == MeasureSpec.AT_MOST) {
            int ourSize =  ornTopPadding + ornBottomPadding;

            final int numColumns = mNumColumnRows;
            for (int i = 0; i < count; i += numColumns) {
                ourSize += childOrnHeight;
                if (i + numColumns < count) {
                    ourSize += getOrnTopBottomSpacing();
                }
                if (ourSize >= spec.ornHeightSize) {
                    ourSize = spec.ornHeightSize;
                    break;
                }
            }
            spec.ornHeightSize = ourSize;
        }

        setTableViewMeasuredDimension(spec.ornWidthSize, spec.ornHeightSize);
        tableView.mWidthHeightMeasureSpec = spec.ornWidthMeasureSpec;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void setNumColumnRows(int numColumnRows){
        mNumColumnRows = numColumnRows;
    }

    private void determineColumnRows(int availableSpace) {
        final int ornRequestedWidthSpacing = getOrnRequestedWidthSpacing();
        final int stretchMode = tableView.mStretchMode;
        final int requestedOrnWidth = tableView.mRequestedOrnWidth;

        if (tableView.mRequestedNumColumnRows == TableColleague.AUTO_FIT) {
            if (requestedOrnWidth > 0) {
                // Client told us to pick the number of columns
                mNumColumnRows = (availableSpace + ornRequestedWidthSpacing) /
                        (requestedOrnWidth + ornRequestedWidthSpacing);
            } else {
                // Just make up a number if we don't have enough info
                mNumColumnRows = 2;
            }
        } else {
            // We picked the columns
            mNumColumnRows = tableView.mRequestedNumColumnRows;
        }

        if (mNumColumnRows <= 0) {
            mNumColumnRows = 1;
        }

        switch (stretchMode) {
        case TableView.NO_STRETCH:
            // Nobody stretches
            mOrnWidth = requestedOrnWidth;
            setOrnLeftRightSpacing(ornRequestedWidthSpacing);
            break;

        default:
            int spaceLeftOver = availableSpace - (mNumColumnRows * requestedOrnWidth) -
                    ((mNumColumnRows - 1) * ornRequestedWidthSpacing);
            switch (stretchMode) {
            case TableView.STRETCH_COLUMN_ROW_WIDTH:
                // Stretch the columns/rows (width)
                mOrnWidth = requestedOrnWidth + spaceLeftOver / mNumColumnRows;
                setOrnLeftRightSpacing(ornRequestedWidthSpacing);
                break;

            case TableView.STRETCH_SPACING:
                // Stretch the spacing between columns/rows (width)
                mOrnWidth = requestedOrnWidth;
                if (mNumColumnRows > 1) {
                    setOrnLeftRightSpacing(ornRequestedWidthSpacing + spaceLeftOver / (mNumColumnRows - 1));
                } else {
                    setOrnLeftRightSpacing(ornRequestedWidthSpacing + spaceLeftOver);
                }
                break;
            }

            break;
        }
    }

    /**
     * Add a view as a child and make sure it is measured (if necessary) and
     * positioned properly.
     *
     * @param child The view to add
     * @param position The position of the view
     * @param ornTop The y/x position relative to which this view will be positioned
     * @param flow if true, align top/left edge to y/x. If false, align bottom/right edge to y/x.
     * @param childrenOrnLeft Left edge where children should be positioned
     * @param selected Is this position selected?
     * @param recycled Has this view been pulled from the recycle bin? If so it does not need to be remeasured.
     * @param where Where to add the item in the list
     * @hide
     */
    protected void setupChild(View child, int position, int ornTop, boolean flow, int childrenOrnLeft,
            boolean selected, boolean recycled, int where) {
        boolean isSelected = selected && tableView.shouldShowSelector();

        final boolean updateChildSelected = isSelected != child.isSelected();
        boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

        // Respect layout params that are already in the view. Otherwise make
        // some up...
        AbstractTableView.LayoutParams p = (AbstractTableView.LayoutParams)child.getLayoutParams();
        if (p == null) {
            p = getDefaultChildLayoutParams();
        }
        p.viewType = tableView.mAdapter.getItemViewType(position);

        if (recycled) {
            tableView.attachViewToParent(child, where, p);
        } else {
            tableView.addViewInLayout(child, where, p, true);
        }

        if (updateChildSelected) {
            child.setSelected(isSelected);
            if (isSelected) {
                tableView.requestFocus();
            }
        }

        if (needToMeasure) {
            int childOrnHeightSpec = ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, getOrnHeight(p));

            int childOrnWidthSpec = ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(mOrnWidth, MeasureSpec.EXACTLY), 0, getOrnWidth(p));
            measureView(child, childOrnWidthSpec, childOrnHeightSpec);
        } else {
            tableView.cleanupLayoutState(child);
        }

        final int ornW = getOrnMeasuredWidth(child);
        //final int ornH = getOrnMeasuredHeight(child);
        //We can get right value after configuration changed instantly through tableView.getTableChildHeight()
        final int ornH = (tableView==null ? getOrnMeasuredHeight(child) : tableView.getTableChildHeight());

        int childOrnLeft;
        final int childOrnTop = flow ? ornTop : ornTop - ornH;

        switch (getOrnGravity()) {
        case Gravity.LEFT:
            childOrnLeft = childrenOrnLeft;
            break;
        case Gravity.CENTER_HORIZONTAL:
            childOrnLeft = childrenOrnLeft + ((mOrnWidth - ornW) / 2);
            break;
        case Gravity.RIGHT:
            childOrnLeft = childrenOrnLeft + mOrnWidth - ornW;
            break;
        default:
            childOrnLeft = childrenOrnLeft;
            break;
        }

        if (needToMeasure) {
            final int childOrnRight = childOrnLeft + ornW;
            final int childOrnBottom = childOrnTop + ornH;
            layoutView(child, childOrnLeft, childOrnTop, childOrnRight, childOrnBottom);
        } else {
            offsetLeftAndRight(child, childOrnLeft - getOrnLeft(child));
            offsetTopAndBottom(child, childOrnTop - getOrnTop(child));
        }

        if (null != tableView && tableView.mCachingStarted) {
            child.setDrawingCacheEnabled(true);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnTop(Rect rect);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnBottom(Rect rect);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnLeft(Rect rect);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnRight(Rect rect);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnTop(View view);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnBottom(View view);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnLeft(View view);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnRight(View view);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnWidth(View view);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnHeight(View view);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnMeasuredWidth(View view);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnMeasuredHeight(View view);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnWidth(AbstractTableView.LayoutParams params);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnHeight(AbstractTableView.LayoutParams params);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnGravity();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public OrnMeasureSpec getOrnMeasureSpec(int widthMeasureSpec, int heightMeasureSpec);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getFadingEdgeLength();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void setTableViewMeasuredDimension(int ornMeasuredWidth, int ornMeasuredHeight);

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnLeftRightSpacing();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void setOrnLeftRightSpacing(int spacing);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnTopBottomSpacing();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void setOrnTopBottomSpacing(int spacing);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getScrollbarWidth();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public AbstractTableView.LayoutParams getDefaultChildLayoutParams();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void measureView(View view, int ornWidthMeasureSpec, int ornHeightMeasureSpec);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getOrnRequestedWidthSpacing();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void offsetOrnChildrenTopAndBottom(int offset);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void layoutView(View view, int ornLeft, int ornTop, int ornRight, int ornBottom);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void offsetLeftAndRight(View view, int offset);
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void offsetTopAndBottom(View view, int offset);

    // Scroll control
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void fling(AbstractTableView.FlingRunnable runnable, float velocityX, float velocityY);


/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract protected void trackMotionScrollOrn(int deltaX, int deltaY);

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX);
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
    abstract public void detachOffScreenChildren(boolean toLeft);
    /**
     * @return The center of the given view.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getCenterOfView(View view);

    /**
     * @return The center of this Table.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public int getCenterOfTable();

    /**
     * Scrolls the items so that the selected item is in its 'slot' (its center
     * is the gallery's center).
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void scrollAmount(AbstractTableView.FlingRunnable runnable, int scrollAmount);

    /**
     * Scroll the view return from ScrollControl to the parent center.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void scrollIntoSlots(AbstractTableView.FlingRunnable runnable);

    /**
     * Do the scroll.
     * @param distanceX The horizontal offset this time
     * @param distanceY The Vertical offset this time
     * @param isFling To indicate if it is fling or not
     */
    abstract public void scrollWithConstrain(int distanceX, int distanceY, boolean isFling);

    /**
     * This method is for arrow scroll to find next selection int.
     * @param direction One of View.FOCUS_UP, View.FOCUS_DOWN, View.FOCUS_LEFT, View.FOCUS_RIGHT,
     * @param startOfRowPos The start position of the row that contains old selection.
     * @param endOfRowPos The end position of the row that contains old selection.
     * @param selectedPosition Old selection.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public boolean findAndSetSelecionInt(int direction, int startOfRowPos, int endOfRowPos, int selectedPosition);

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public void setCloseBouncing(boolean close);

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setRepeatEnable(boolean b){
        mRepeatEnable = b;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setMultiStop(boolean b) {
        mMultiStop = b;
    }

    int [] mStops = null;

    /**
     * enable multiple stops with given step distance
     * @param d the distance between 2 stops; must >=5 and <= 30
     * @return true - enable multiple stops; false - failed to enable
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean setMultiStopDistance(int d) {
            mStops = null;
            mMultiStop = false;
            if (d < 5 || d > 30)
                return false;
            int count = tableView.getCount() / d;
            if (count < 1)
                return false;
            mStops = new int [count];
            int y = d - 1;
            int index = 0;
            while ( y < tableView.getCount()) {
                mStops[index] = y;
                y+=d;
                index++;
            }
            mMultiStop = true;
            return true;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean setMultiStopDistance(int[] stopPoints) {
        mStops = null;
        mMultiStop = false;

        if (stopPoints == null || !(stopPoints.length > 0)) {
            return false;
        }

        mStops = stopPoints;
        mMultiStop = true;

        return true;
    }

    /**
     * Index in the adapter of the view which the scroll should stop at
     */
    protected int mStopExcept = -1;
    /**
     * A flag to distinguish if it is a new scroll
     */
    protected boolean mNewScroll = false;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setStopExcept(int i) {
        mStopExcept = i;
        mNewScroll = true;
    }
}
