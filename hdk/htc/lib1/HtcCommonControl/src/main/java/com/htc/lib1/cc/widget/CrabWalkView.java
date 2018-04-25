/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.SoundEffectConstants;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.Checkable;
import android.widget.ListAdapter;

import java.util.ArrayList;

import android.util.Log;

import com.htc.lib1.cc.view.CommonSavedState;
/*
 * Implementation Notes:
 *
 * Some terminology:
 *
 *     index    - index of the items that are currently visible
 *     position - index of the items in the cursor
 */

/**
 * A view that shows items in a vertically scrolling list. The items come from
 * the {@link ListAdapter} associated with this view.
 */
public class CrabWalkView extends AbsCrabWalkView {
    /**
     * Used to indicate a no preference for a position type.
     */
    static final int NO_POSITION = -1;

    /**
     * Normal list that does not indicate choices
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int CHOICE_MODE_NONE = 0;

    /**
     * The list allows up to one choice
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int CHOICE_MODE_SINGLE = 1;

    /**
     * The list allows multiple choices
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int CHOICE_MODE_MULTIPLE = 2;

    /**
     * When arrow scrolling, ListView will never scroll more than this factor
     * times the height of the list.
     */
    private static final float MAX_SCROLL_FACTOR = 0.33f;

    /**
     * When arrow scrolling, need a certain amount of pixels to preview next
     * items.  This is usually the fading edge, but if that is small enough,
     * we want to make sure we preview at least this many pixels.
     */
    private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;

    /**
     * A class that represents a fixed view in a list, for example a header at the top
     * or a footer at the bottom.
     */
    public class FixedViewInfo {
        /** The view to add to the list */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public View view;
        /** The data backing the view. This is returned from {@link ListAdapter#getItem(int)}. */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public Object data;
        /** <code>true</code> if the fixed view should be selectable in the list */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public boolean isSelectable;
    }
// <HTC>
    //added by jason liu  //We need a footer view without divider for dialer
    View mFooterViewWithoutDivider;
    //end
// </HTC>
    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList();
    private ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList();

    Drawable mDivider;
    Drawable mSeperatorDiver;
    @ExportedProperty(category = "CommonControl")
    int mDividerHeight;

    // Start: Andrew, Liu
    @ExportedProperty(category = "CommonControl")
    int mDividerWidth;
    // End: Andrew, Liu

    @ExportedProperty(category = "CommonControl")
    private boolean mClipDivider;
    @ExportedProperty(category = "CommonControl")
    private boolean mHeaderDividersEnabled;
    @ExportedProperty(category = "CommonControl")
    private boolean mFooterDividersEnabled;

    @ExportedProperty(category = "CommonControl")
    private boolean mAreAllItemsSelectable = true;

    @ExportedProperty(category = "CommonControl")
    private boolean mItemsCanFocus = false;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = CHOICE_MODE_NONE, to = "CHOICE_MODE_NONE"),
            @IntToString(from = CHOICE_MODE_MULTIPLE, to = "CHOICE_MODE_MULTIPLE"),
            @IntToString(from = CHOICE_MODE_SINGLE, to = "CHOICE_MODE_SINGLE")
    })
    private int mChoiceMode = CHOICE_MODE_NONE;

    private SparseBooleanArray mCheckStates;

    // used for temporary calculations.
    private final Rect mTempRect = new Rect();

    // the single allocated result per list view; kinda cheesey but avoids
    // allocating these thingies too often.
    private ArrowScrollFocusResult mArrowScrollFocusResult = new ArrowScrollFocusResult();

    /**
     *  Prevent overscroll when fastscroll is triggered
     */
    @ExportedProperty(category = "CommonControl")
    boolean mPreventOverScroll = false;

    /**
     * Simple constructor to use when creating a HtcListViewCore2 from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public CrabWalkView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a HtcListViewCore2 from XML.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public CrabWalkView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view.
     */
    public CrabWalkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //TypedArray a = context.obtainStyledAttributes(attrs,
        //    com.android.internal.R.styleable.ListView, defStyle, 0);

        //CharSequence[] entries = a.getTextArray(com.android.internal.R.styleable.ListView_entries);
        //if ( entries != null ) {
        //    setAdapter(new ArrayAdapter<CharSequence>(
        //        context,
        //        com.android.internal.R.layout.simple_list_item_1,
        //        entries));
        //}

        //final Drawable d = a.getDrawable(com.android.internal.R.styleable.ListView_divider);
        //if ( d != null ) {
            // If a divider is specified use its intrinsic height for divider
            // height
        //    setDivider(d);
        //}

        // Use the height specified, zero being the default
        //final int dividerHeight = a.getDimensionPixelSize(
        //    com.android.internal.R.styleable.ListView_dividerHeight, 0);
        //if ( dividerHeight != 0 ) {
        //    setDividerHeight(dividerHeight);
        //}

        mHeaderDividersEnabled = true;
        mFooterDividersEnabled = true;

        //a.recycle();
        // [ Click Animation: Josh Kang
        //setSelector(new ColorDrawable(0x00000000));
        //   Click Animation: Josh Kang ]
    }

    /**
     * @return The maximum amount a list view will scroll in response to an
     *         arrow event.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getMaxScrollAmount() {
        // Start: Andrew, Liu
        int amount = 0;
        if (isHorizontalStyle()) {
            amount = (int) (MAX_SCROLL_FACTOR * (getRight() - getLeft()));
        } else {
            amount = (int) (MAX_SCROLL_FACTOR * (getBottom() - getTop()));
        }
        return amount;
        //return (int) (MAX_SCROLL_FACTOR * (mBottom - mTop));
        // End: Andrew, Liu
    }

    /**
     * Make sure views are touching the top or bottom edge, as appropriate for
     * our gravity
     */
    private void adjustViewsUpOrDown() {
        final int childCount = getChildCount();
        int delta;

        if (childCount > 0) {
            View child;

            if (!mStackFromBottom) {
                // Uh-oh -- we came up short. Slide all views up to make them

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    // align with the left
                    child = getChildAt(0);
                    delta = child.getLeft() - mListPadding.left - getLeftBoundary() - getLeftBorderWidth();
                    if (mFirstPosition != 0) {
                        // It's OK to have some space left the first item if it is
                        // part of the horizontal spacing
                        delta -= mDividerWidth;
                    }
                } else {
                    // align with the top
                    child = getChildAt(0);
                    delta = child.getTop() - mListPadding.top - getTopBoundary() - getTopBorderHeight();
                    if (mFirstPosition != 0) {
                        // It's OK to have some space above the first item if it is
                        // part of the vertical spacing
                        delta -= mDividerHeight;
                    }
                }
                // End: Andrew, Liu
                if (delta < 0) {
                    // We only are looking to see if we are too low, not too high
                    delta = 0;
                }
            } else {
                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    // we are too high, slide all views down to align with right
                    child = getChildAt(childCount - 1);
                    delta = child.getRight() - (getWidth() - mListPadding.right - getRightBoundary() - getRightBorderWidth());
                    if (mFirstPosition + childCount < mItemCount) {
                        // It's OK to have some space left the last item if it is
                        // part of the horizontal spacing
                        delta += mDividerWidth;
                    }
                } else {
                    // we are too high, slide all views down to align with bottom
                    child = getChildAt(childCount - 1);
                    delta = child.getBottom() - (getHeight() - mListPadding.bottom - getBottomBoundary() - getBottomBorderHeight());

                    if (mFirstPosition + childCount < mItemCount) {
                        // It's OK to have some space below the last item if it is
                        // part of the vertical spacing
                        delta += mDividerHeight;
                    }
                }
                // End: Andrew, Liu
                if (delta > 0) {
                    delta = 0;
                }
            }

            if (delta != 0) {
                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    offsetChildrenLeftAndRight(-delta);
                } else {
                    //offsetChildrenTopAndBottom(-delta);
                }
                // End: Andrew, Liu
            }
        }
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that that will also account for header
     * views.
     *
     * @param v
     *            The view to add.
     * @param data
     *            Data to associate with this view
     * @param isSelectable
     *            whether the item is selectable
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        throw new IllegalStateException(
                "Cannot support header view");
        /*
        if ( mAdapter != null ) {
            throw new IllegalStateException(
                "Cannot add header view to list -- setAdapter has already been called.");
        }

        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);
        */
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that that will also account for header
     * views.
     *
     * @param v
     *            The view to add.
     */
    /**
      * @deprecated [Module internal use]
      */
    /**@hide*/
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getHeaderViewsCount() {
        return mHeaderViewInfos.size();
    }

    /**
     * Removes a previously-added header view.
     *
     * @param v
     *            The view to remove
     * @return true if the view was removed, false if the view was not a header
     *         view
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean removeHeaderView(View v) {
        if ( mHeaderViewInfos.size() > 0 ) {
            boolean result = false;
        ListAdapter tmpAdapter = null;
        tmpAdapter = mAdapter;

        if ( ( (HtcHeaderViewListAdapter2) tmpAdapter ).removeHeader(v) ) {
                mDataSetObserver.onChanged();
                result = true;
            }
            removeFixedViewInfo(v, mHeaderViewInfos);
            return result;
        }
        return false;
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for ( int i = 0; i < len; ++i ) {
            FixedViewInfo info = where.get(i);
            if ( info.view == v ) {
                where.remove(i);
                break;
            }
        }
    }

    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that that will also account for header
     * views.
     *
     * @param v
     *            The view to add.
     * @param data
     *            Data to associate with this view
     * @param isSelectable
     *            true if the footer view can be selected
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addFooterView(View v, Object data, boolean isSelectable) {
        throw new IllegalStateException(
                "Cannot support footer view");
        /*
        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mFooterViewInfos.add(info);

        // in the case of re-adding a footer view, or adding one later on,
        // we need to notify the observer
        if ( mDataSetObserver != null ) {
            mDataSetObserver.onChanged();
        }
        */
    }
// <HTC>
    //added by jason liu
    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that that will also account for header
     * views.
     *
     * @param v
     *            The view to add.
     * @param data
     *            Data to associate with this view
     * @param isSelectable
     *            true if the footer view can be selected
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addFootViewWithoutDivider(View v, Object data,
        boolean isSelectable) {
        mFooterViewWithoutDivider = v;
        addFooterView(v, data, isSelectable);
    }

    //end
// </HTC>
    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that that will also account for header
     * views.
     *
     *
     * @param v
     *            The view to add.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getFooterViewsCount() {
        return mFooterViewInfos.size();
    }

    /**
     * Removes a previously-added footer view.
     *
     * @param v
     *            The view to remove
     * @return true if the view was removed, false if the view was not a footer
     *         view
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean removeFooterView(View v) {
        if ( mFooterViewInfos.size() > 0 ) {
            boolean result = false;
        ListAdapter tmpAdapter = null;
        tmpAdapter = mAdapter;
            if ( ( (HtcHeaderViewListAdapter2) tmpAdapter ).removeFooter(v) ) {
                mDataSetObserver.onChanged();
                result = true;
            }
            removeFixedViewInfo(v, mFooterViewInfos);
            return result;
        }
        return false;
    }

    /**
     * Returns the adapter currently in use in this ListView. The returned
     * adapter might not be the same adapter passed to
     * setAdapter(ListAdapter) but might be a
     * WrapperListAdapter.
     *
     * @return The adapter currently used to display data in this ListView.
     *
     * @see #setAdapter(ListAdapter)
     */
    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Sets the data behind this ListView.
     *
     * The adapter passed to this method may be wrapped by a
     *  WrapperListAdapter, depending on the ListView features currently
     * in use. For instance, adding headers and/or footers will cause the
     * adapter to be wrapped.
     *
     * @param adapter
     *            The ListAdapter which is responsible for maintaining the data
     *            backing this list and for producing a view to represent an
     *            item in that data set.
     *
     * @see #getAdapter()
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        if ( null != mAdapter && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        resetList();
        mRecycler.clear();

        if (mHeaderViewInfos.size() > 0 || mFooterViewInfos.size() > 0) {
            ListAdapter tmpAdapter = new HtcHeaderViewListAdapter2(
            mHeaderViewInfos, mFooterViewInfos, adapter);
            mAdapter = tmpAdapter;
        }
        else {
            mAdapter = adapter;
        }

        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        if ( mAdapter != null ) {
            mAreAllItemsSelectable = mAdapter.areAllItemsEnabled();
            mOldItemCount = mItemCount;
            mItemCount = mAdapter.getCount();
            checkFocus();

            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);

            mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());

            int position;
            if ( mStackFromBottom ) {
                position = lookForSelectablePosition(mItemCount - 1, false);
            } else {
                position = lookForSelectablePosition(0, true);
            }
            setSelectedPositionInt(position);
            setNextSelectedPositionInt(position);

            if ( mItemCount == 0 ) {
                // Nothing selected
                checkSelectionChanged();
            }

        } else {
            mAreAllItemsSelectable = true;
            checkFocus();
            // Nothing selected
            checkSelectionChanged();
        }

        if ( mCheckStates != null ) {
            mCheckStates.clear();
        }

        requestLayout();
    }

    /**
     * The list is empty. Clear everything out.
     */
    @Override
    void resetList() {
        super.resetList();
        mLayoutMode = LAYOUT_NORMAL;
    }

    /**
     * @return Whether the list needs to show the top fading edge
     */
    private boolean showingTopFadingEdge() {
        final int listTop = getScrollY() + mListPadding.top;
        return ( mFirstPosition > 0 ) || ( getChildAt(0).getTop() > listTop );
    }

    /**
     * @return Whether the list needs to show the bottom fading edge
     */
    private boolean showingBottomFadingEdge() {
        final int childCount = getChildCount();
        final int bottomOfBottomChild = getChildAt(childCount - 1).getBottom();
        final int lastVisiblePosition = mFirstPosition + childCount - 1;

        final int listBottom = getScrollY() + getHeight() - mListPadding.bottom;

        return ( lastVisiblePosition < mItemCount - 1 )
            || ( bottomOfBottomChild < listBottom );
    }

    // Start: Andrew, Liu
    /**
     * @return Whether the list needs to show the top fading edge
     */
    private boolean showingLeftFadingEdge() {
        final int listLeft = getScrollX() + mListPadding.left;
        return ( mFirstPosition > 0 ) || ( getChildAt(0).getLeft() > listLeft );
    }

    /**
     * @return Whether the list needs to show the bottom fading edge
     */
    private boolean showingRightFadingEdge() {
        final int childCount = getChildCount();
        final int rightOfRightChild = getChildAt(childCount - 1).getRight();
        final int lastVisiblePosition = mFirstPosition + childCount - 1;

        final int listRight = getScrollX() + getWidth() - mListPadding.right;

        return ( lastVisiblePosition < mItemCount - 1 )
            || ( rightOfRightChild < listRight );
    }

    // End: Andrew, Liu


/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {

        // Start: Andrew, Liu
        final boolean scroll;
        // offset so rect is in coordinates of the this view
        rect.offset(child.getLeft(), child.getTop());
        rect.offset(-child.getScrollX(), -child.getScrollY());

        if (isHorizontalStyle()) {
            int rectLeftWithinChild = rect.left;
            final int width = getWidth();
            int listUnfadedLeft = getScrollX();
            int listUnfadedRight = listUnfadedLeft + width;
            final int fadingEdge = getHorizontalFadingEdgeLength();

            if (showingLeftFadingEdge()) {
                // leave room for top fading edge as long as rect isn't at very top
                if ((mSelectedPosition > 0) || (rectLeftWithinChild > fadingEdge)) {
                    listUnfadedLeft += fadingEdge;
                }
            }

            int childCount = getChildCount();
            int rightOfRightChild = getChildAt(childCount - 1).getRight();

            if (showingRightFadingEdge()) {
                // leave room for bottom fading edge as long as rect isn't at very bottom
                if ((mSelectedPosition < mItemCount - 1)
                        || (rect.right < (rightOfRightChild - fadingEdge))) {
                    rightOfRightChild -= fadingEdge;
                }
            }

            int scrollXDelta = 0;

            if (rect.right > listUnfadedRight && rect.left > listUnfadedLeft) {
                // need to MOVE DOWN to get it in view: move down just enough so
                // that the entire rectangle is in view (or at least the first
                // screen size chunk).

                if (rect.width() > width) {
                    // just enough to get screen size chunk on
                    scrollXDelta += (rect.left - listUnfadedLeft);
                } else {
                    // get entire rect at bottom of screen
                    scrollXDelta += (rect.right - listUnfadedRight);
                }

                // make sure we aren't scrolling beyond the end of our children
                int distanceToRight = rightOfRightChild - listUnfadedRight;
                scrollXDelta = Math.min(scrollXDelta, distanceToRight);
            } else if (rect.left < listUnfadedLeft && rect.right < listUnfadedRight) {
                // need to MOVE UP to get it in view: move up just enough so that
                // entire rectangle is in view (or at least the first screen
                // size chunk of it).

                if (rect.width() > width) {
                    // screen size chunk
                    scrollXDelta -= (listUnfadedRight - rect.right);
                } else {
                    // entire rect at top
                    scrollXDelta -= (listUnfadedLeft - rect.left);
                }

                // make sure we aren't scrolling any further than the top our children
                int left = getChildAt(0).getLeft();
                int deltaToLeft = left - listUnfadedLeft;
                scrollXDelta = Math.max(scrollXDelta, deltaToLeft);
            }

            scroll = scrollXDelta != 0;
            if (scroll) {
                scrollListItemsBy(-scrollXDelta);
                positionSelector(child);
                mSelectedLeft = child.getLeft();
                invalidate();
            }
        } else {
            int rectTopWithinChild = rect.top;

            final int height = getHeight();
            int listUnfadedTop = getScrollY();
            int listUnfadedBottom = listUnfadedTop + height;
            final int fadingEdge = getVerticalFadingEdgeLength();

            if (showingTopFadingEdge()) {
                // leave room for top fading edge as long as rect isn't at very top
                if ((mSelectedPosition > 0) || (rectTopWithinChild > fadingEdge)) {
                    listUnfadedTop += fadingEdge;
                }
            }

            int childCount = getChildCount();
            int bottomOfBottomChild = getChildAt(childCount - 1).getBottom();

            if (showingBottomFadingEdge()) {
                // leave room for bottom fading edge as long as rect isn't at very bottom
                if ((mSelectedPosition < mItemCount - 1)
                        || (rect.bottom < (bottomOfBottomChild - fadingEdge))) {
                    listUnfadedBottom -= fadingEdge;
                }
            }

            int scrollYDelta = 0;

            if (rect.bottom > listUnfadedBottom && rect.top > listUnfadedTop) {
                // need to MOVE DOWN to get it in view: move down just enough so
                // that the entire rectangle is in view (or at least the first
                // screen size chunk).

                if (rect.height() > height) {
                    // just enough to get screen size chunk on
                    scrollYDelta += (rect.top - listUnfadedTop);
                } else {
                    // get entire rect at bottom of screen
                    scrollYDelta += (rect.bottom - listUnfadedBottom);
                }

                // make sure we aren't scrolling beyond the end of our children
                int distanceToBottom = bottomOfBottomChild - listUnfadedBottom;
                scrollYDelta = Math.min(scrollYDelta, distanceToBottom);
            } else if (rect.top < listUnfadedTop && rect.bottom < listUnfadedBottom) {
                // need to MOVE UP to get it in view: move up just enough so that
                // entire rectangle is in view (or at least the first screen
                // size chunk of it).

                if (rect.height() > height) {
                    // screen size chunk
                    scrollYDelta -= (listUnfadedBottom - rect.bottom);
                } else {
                    // entire rect at top
                    scrollYDelta -= (listUnfadedTop - rect.top);
                }

                // make sure we aren't scrolling any further than the top our children
                int top = getChildAt(0).getTop();
                int deltaToTop = top - listUnfadedTop;
                scrollYDelta = Math.max(scrollYDelta, deltaToTop);
            }

            scroll = scrollYDelta != 0;
            if (scroll) {
                scrollListItemsBy(-scrollYDelta);
                positionSelector(child);
                mSelectedTop = child.getTop();
                invalidate();
            }
        }
        // End: Andrew, Liu
        return scroll;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void fillGap(boolean down) {
        final int count = getChildCount();

        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            if ( down ) {
                final int startOffset = count > 0
                    ? getChildAt(count - 1).getRight() + mDividerWidth
                    : getListPaddingLeft();
                fillDown(mFirstPosition + count, startOffset);
                correctTooHigh(getChildCount());
            } else {
                final int startOffset = count > 0 ? getChildAt(0).getLeft()
                    - mDividerWidth : getWidth() - getListPaddingRight();
                fillUp(mFirstPosition - 1, startOffset);
                correctTooLow(getChildCount());
            }
        } else {
            if ( down ) {
                final int startOffset = count > 0
                    ? getChildAt(count - 1).getBottom() + mDividerHeight
                    : getListPaddingTop();
                fillDown(mFirstPosition + count, startOffset);
                correctTooHigh(getChildCount());
            } else {
                final int startOffset = count > 0 ? getChildAt(0).getTop()
                    - mDividerHeight : getHeight() - getListPaddingBottom();
                fillUp(mFirstPosition - 1, startOffset);
                correctTooLow(getChildCount());
            }
        }
/*
    if (!mSpeedUp)
        updateBackGround();
*/
    }

    /**
     * Fills the list from pos down to the end of the list view.
     *
     * @param pos
     *            The first position to put in the list
     *
     * @param nextTop
     *            The location where the top of the item associated with pos
     *            should be drawn
     *
     * @return The view that is currently selected, if it happens to be in the
     *         range that we draw.
     */
    // modified by Kun
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillDown(int pos, int nextTop) {
        View selectedView = null;

        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            int end = ( getRight() - getLeft() ) - mListPadding.right;

            while ( nextTop < end && pos < mItemCount ) {
                // is this the selected item?
                boolean selected = pos == mSelectedPosition;
                View child = makeAndAddView(pos, nextTop, true,
                    mListPadding.top, selected);
                // Child should not be null. Please check Log.
                if ( child == null )
                    break;
                nextTop = child.getRight() + mDividerWidth;
                if ( selected ) {
                    selectedView = child;
                }
                pos++;
            }
        } else {
            int end = ( getBottom() - getTop() ) - mListPadding.bottom;

            while ( nextTop < end && pos < mItemCount ) {
                // is this the selected item?
                boolean selected = pos == mSelectedPosition;
                View child = makeAndAddView(pos, nextTop, true,
                    mListPadding.left, selected);
                // Child should not be null. Please check Log.
                if ( child == null )
                    break;
                nextTop = child.getBottom() + mDividerHeight;
                if ( selected ) {
                    selectedView = child;
                }
                pos++;
            }
        }
        // End: Andrew, Liu
        return selectedView;
    }

    /**
     * Fills the list from pos up to the top of the list view.
     *
     * @param pos
     *            The first position to put in the list
     *
     * @param nextBottom
     *            The location where the bottom of the item associated with pos
     *            should be drawn
     *
     * @return The view that is currently selected
     */
    // modified by Kun
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected View fillUp(int pos, int nextBottom) {
        View selectedView = null;

        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            int end = mListPadding.left;

            while (nextBottom > end && pos >= 0) {
                // is this the selected item?
                boolean selected = pos == mSelectedPosition;
                View child = makeAndAddView(pos, nextBottom, false, mListPadding.top, selected);
                nextBottom = child.getLeft() - mDividerWidth;
                if (selected) {
                    selectedView = child;
                }
                pos--;
            }
        } else {
            int end = mListPadding.top;

            while (nextBottom > end && pos >= 0) {
                // is this the selected item?
                boolean selected = pos == mSelectedPosition;
                View child = makeAndAddView(pos, nextBottom, false, mListPadding.left, selected);
                nextBottom = child.getTop() - mDividerHeight;
                if (selected) {
                    selectedView = child;
                }
                pos--;
            }
        }
        // End: Andrew, Liu

        mFirstPosition = pos + 1;

        return selectedView;
    }

    /**
     * Fills the list from top to bottom, starting with mFirstPosition
     *
     * @param nextTop
     *            The location where the top of the first item should be drawn
     *
     * @return The view that is currently selected
     */
    private View fillFromTop(int nextTop) {
        mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
        mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
        if ( mFirstPosition < 0 ) {
            mFirstPosition = 0;
        }
        return fillDown(mFirstPosition, nextTop);
    }

    /**
     * Put mSelectedPosition in the middle of the screen and then build up and
     * down from there. This method forces mSelectedPosition to the center.
     *
     * @param childrenTop
     *            Top of the area in which children can be drawn, as measured in
     *            pixels
     * @param childrenBottom
     *            Bottom of the area in which children can be drawn, as measured
     *            in pixels
     * @return Currently selected view
     */
    private View fillFromMiddle(int childrenTop, int childrenBottom) {
        int height = childrenBottom - childrenTop;

        // Start: Andrew, Liu
        int width = childrenBottom - childrenTop;
        // End: Andrew, Liu

        int position = reconcileSelectedPosition();

        int childTopOrLeft = isHorizontalStyle() ? mListPadding.top : mListPadding.left;

        View sel = makeAndAddView(position, childrenTop, true,
            childTopOrLeft, true);
        mFirstPosition = position;

        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            int selWidth = sel.getMeasuredWidth();
            if ( selWidth <= width ) {
                sel.offsetLeftAndRight( ( width - selWidth ) / 2);
            }
        } else {
            int selHeight = sel.getMeasuredHeight();
            if ( selHeight <= height ) {
                sel.offsetTopAndBottom( ( height - selHeight ) / 2);
            }
        }
        // End: Andrew, Liu

        fillAboveAndBelow(sel, position);

        if ( !mStackFromBottom ) {
            correctTooHigh(getChildCount());
        } else {
            correctTooLow(getChildCount());
        }

        return sel;
    }

    /**
     * Once the selected view as been placed, fill up the visible area above and
     * below it.
     *
     * @param sel
     *            The selected view
     * @param position
     *            The position corresponding to sel
     */
    private void fillAboveAndBelow(View sel, int position) {
        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            final int dividerWidth = mDividerWidth;
            if ( !mStackFromBottom ) {
                fillUp(position - 1, sel.getLeft() - dividerWidth);
                adjustViewsUpOrDown();
                fillDown(position + 1, sel.getRight() + dividerWidth);
            } else {
                fillDown(position + 1, sel.getRight() + dividerWidth);
                adjustViewsUpOrDown();
                fillUp(position - 1, sel.getLeft() - dividerWidth);
            }
        } else {
            final int dividerHeight = mDividerHeight;
            if ( !mStackFromBottom ) {
                fillUp(position - 1, sel.getTop() - dividerHeight);
                adjustViewsUpOrDown();
                fillDown(position + 1, sel.getBottom() + dividerHeight);
            } else {
                fillDown(position + 1, sel.getBottom() + dividerHeight);
                adjustViewsUpOrDown();
                fillUp(position - 1, sel.getTop() - dividerHeight);
            }
        }
    }

    /**
     * Fills the grid based on positioning the new selection at a specific
     * location. The selection may be moved so that it does not intersect the
     * faded edges. The grid is then filled upwards and downwards from there.
     *
     * @param selectedTop
     *            Where the selected item should be
     * @param childrenTop
     *            Where to start drawing children
     * @param childrenBottom
     *            Last pixel where children can be drawn
     * @return The view that currently has selection
     */
    private View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        final int selectedPosition = mSelectedPosition;

        View sel;

        final int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength,
                selectedPosition);
        final int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength,
                selectedPosition);

        // Start: Andrew, Liu
        final int leftSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength,
                selectedPosition);
        final int rightSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength,
                selectedPosition);
        // End: Andrew, Liu

        int childTopOrLeft = isHorizontalStyle() ? mListPadding.top : mListPadding.left;

        sel = makeAndAddView(selectedPosition, selectedTop, true, childTopOrLeft, true);

        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            // Some of the newly selected item extends below the bottom of the list
            if (sel.getRight() > rightSelectionPixel) {
                // Find space available above the selection into which we can scroll
                // upwards
                final int spaceAbove = sel.getLeft() - leftSelectionPixel;

                // Find space required to bring the bottom of the selected item
                // fully into view
                final int spaceBelow = sel.getRight() - rightSelectionPixel;
                final int offset = Math.min(spaceAbove, spaceBelow);

                // Now offset the selected item to get it into view
                sel.offsetLeftAndRight(-offset);
            } else if (sel.getLeft() < leftSelectionPixel) {
                // Find space required to bring the top of the selected item fully
                // into view
                final int spaceAbove = leftSelectionPixel - sel.getLeft();

                // Find space available below the selection into which we can scroll
                // downwards
                final int spaceBelow = rightSelectionPixel - sel.getRight();
                final int offset = Math.min(spaceAbove, spaceBelow);

                // Offset the selected item to get it into view
                sel.offsetLeftAndRight(offset);
            }
        } else {
            // Some of the newly selected item extends below the bottom of the list
            if (sel.getBottom() > bottomSelectionPixel) {
                // Find space available above the selection into which we can scroll
                // upwards
                final int spaceAbove = sel.getTop() - topSelectionPixel;

                // Find space required to bring the bottom of the selected item
                // fully into view
                final int spaceBelow = sel.getBottom() - bottomSelectionPixel;
                final int offset = Math.min(spaceAbove, spaceBelow);

                // Now offset the selected item to get it into view
                sel.offsetTopAndBottom(-offset);
            } else if (sel.getTop() < topSelectionPixel) {
                // Find space required to bring the top of the selected item fully
                // into view
                final int spaceAbove = topSelectionPixel - sel.getTop();

                // Find space available below the selection into which we can scroll
                // downwards
                final int spaceBelow = bottomSelectionPixel - sel.getBottom();
                final int offset = Math.min(spaceAbove, spaceBelow);

                // Offset the selected item to get it into view
                sel.offsetTopAndBottom(offset);
            }
        }
        // Ena: Andrew, Liu

        // Fill in views above and below
        fillAboveAndBelow(sel, selectedPosition);

        if (!mStackFromBottom) {
            correctTooHigh(getChildCount());
        } else {
            correctTooLow(getChildCount());
        }

        return sel;
    }

    /**
     * Calculate the bottom-most pixel we can draw the selection into
     *
     * @param childrenBottom
     *            Bottom pixel were children can be drawn
     * @param fadingEdgeLength
     *            Length of the fading edge in pixels, if present
     * @param selectedPosition
     *            The position that will be selected
     * @return The bottom-most pixel we can draw the selection into
     */
    private int getBottomSelectionPixel(int childrenBottom,
        int fadingEdgeLength, int selectedPosition) {
        int bottomSelectionPixel = childrenBottom;
        if ( selectedPosition != mItemCount - 1 ) {
            bottomSelectionPixel -= fadingEdgeLength;
        }
        return bottomSelectionPixel;
    }

    /**
     * Calculate the top-most pixel we can draw the selection into
     *
     * @param childrenTop
     *            Top pixel were children can be drawn
     * @param fadingEdgeLength
     *            Length of the fading edge in pixels, if present
     * @param selectedPosition
     *            The position that will be selected
     * @return The top-most pixel we can draw the selection into
     */
    private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength,
        int selectedPosition) {
        // first pixel we can draw the selection into
        int topSelectionPixel = childrenTop;
        if ( selectedPosition > 0 ) {
            topSelectionPixel += fadingEdgeLength;
        }
        return topSelectionPixel;
    }

    // Start: Andrew, Liu
    /**
     * Calculate the right-most pixel we can draw the selection into
     *
     * @param childrenRight
     *            Right pixel were children can be drawn
     * @param fadingEdgeLength
     *            Length of the fading edge in pixels, if present
     * @param selectedPosition
     *            The position that will be selected
     * @return The right-most pixel we can draw the selection into
     */
    private int getRightSelectionPixel(int childrenRight, int fadingEdgeLength,
        int selectedPosition) {
        int rightSelectionPixel = childrenRight;
        if ( selectedPosition != mItemCount - 1 ) {
            rightSelectionPixel -= fadingEdgeLength;
        }
        return rightSelectionPixel;
    }

    /**
     * Calculate the left-most pixel we can draw the selection into
     *
     * @param childrenLeft
     *            Left pixel were children can be drawn
     * @param fadingEdgeLength
     *            Length of the fading edge in pixels, if present
     * @param selectedPosition
     *            The position that will be selected
     * @return The left-most pixel we can draw the selection into
     */
    private int getLeftSelectionPixel(int childrenLeft, int fadingEdgeLength,
        int selectedPosition) {
        // first pixel we can draw the selection into
        int leftSelectionPixel = childrenLeft;
        if ( selectedPosition > 0 ) {
            leftSelectionPixel += fadingEdgeLength;
        }
        return leftSelectionPixel;
    }

    // End: Andrew, Liu

/* mark for scrolling performance
    @Override
    View obtainView(int position) {
        if ( !mSpeedUp ) {
            View chTmp = super.obtainView(position);

            return super.obtainView(position);
    }
        View child = super.obtainView(position);
        if ( child == null )
            return null;
        if ( com.htc.internal.R.id.title_16 == child.getId() ) {
            // child.setBackgroundResource(com.htc.internal.R.drawable.section_divider_top);
            return child;
        }
        Object tag = child.getTag();
        View child_top_round = (View) child.findViewById(com.htc.internal.R.id.htc_list_item_top_round);
        View child_bottom_round = (View) child.findViewById(com.htc.internal.R.id.htc_list_item_bottom_round);

*/

/*
    if (tag == null) {
        if (position == 0)
            top_round = true;
        else if (position == mAdapter.getCount() - 1)
            bottom_round = true;
    } else
*/



/* mark for check scrolling performance
        if ( tag instanceof HtcListItemSeparableType ) {
            HtcListItemSeparableType tagItem = (HtcListItemSeparableType) tag;
            boolean top_round = false;
            boolean bottom_round = false;
*/
//            if ( /* position == 0 || */tagItem.getTopRound() )
/*
                top_round = true;
            if ( position == mAdapter.getCount() - 1 ) {
                final int listBottom = mBottom - mTop - getListPaddingBottom()
                    + getBottomBorderHeight();
                int bottom = child.getBottom();
                if ( bottom > listBottom )
                    bottom_round = true;
            } else if ( tagItem.getBottomRound() )
                bottom_round = true;
            if ( top_round && bottom_round ) {
                if ( child_top_round != null && child_bottom_round != null ) {
                    child_top_round.setVisibility(View.VISIBLE);
                    child_bottom_round.setVisibility(View.VISIBLE);
                }
                child.setBackgroundResource(com.htc.internal.R.drawable.trans_4round);
            } else if ( top_round ) {
                if ( child_top_round != null && child_bottom_round != null ) {
                    child_top_round.setVisibility(View.VISIBLE);
                    child_bottom_round.setVisibility(View.INVISIBLE);
                }
                child.setBackgroundResource(com.htc.internal.R.drawable.trans_topround);
            } else if ( bottom_round ) {
                if ( child_top_round != null && child_bottom_round != null ) {
                    child_top_round.setVisibility(View.INVISIBLE);
                    child_bottom_round.setVisibility(View.VISIBLE);
                }
                child.setBackgroundResource(com.htc.internal.R.drawable.trans_bottomround);
            } else {
                if ( child_top_round != null && child_bottom_round != null ) {
                    child_top_round.setVisibility(View.INVISIBLE);
                    child_bottom_round.setVisibility(View.INVISIBLE);
                }
            }
        }
        return child;
    }
*/

    /**
     * Fills the list based on positioning the new selection relative to the old
     * selection. The new selection will be placed at, above, or below the
     * location of the new selection depending on how the selection is moving.
     * The selection will then be pinned to the visible part of the screen,
     * excluding the edges that are faded. The list is then filled upwards and
     * downwards from there.
     *
     * @param oldSel
     *            The old selected view. Useful for trying to put the new
     *            selection in the same place
     * @param newSel
     *            The view that is to become selected. Useful for trying to put
     *            the new selection in the same place
     * @param delta
     *            Which way we are moving
     * @param childrenTop
     *            Where to start drawing children
     * @param childrenBottom
     *            Last pixel where children can be drawn
     * @return The view that currently has selection
     */
    private View moveSelection(View oldSel, View newSel, int delta, int childrenTop,
            int childrenBottom) {

        // Start: Andrew, Liu
        View sel;

        if (isHorizontalStyle()) {
            int fadingEdgeLength = getHorizontalFadingEdgeLength();
            final int selectedPosition = mSelectedPosition;

            final int leftSelectionPixel = getLeftSelectionPixel(childrenTop, fadingEdgeLength,
                    selectedPosition);
            final int rightSelectionPixel = getRightSelectionPixel(childrenTop, fadingEdgeLength,
                    selectedPosition);

            if (delta > 0) {
                /*
                 * Case 1: Scrolling down.
                 */

                /*
                 *     Before           After
                 *    |       |        |       |
                 *    +-------+        +-------+
                 *    |   A   |        |   A   |
                 *    |   1   |   =>   +-------+
                 *    +-------+        |   B   |
                 *    |   B   |        |   2   |
                 *    +-------+        +-------+
                 *    |       |        |       |
                 *
                 *    Try to keep the top of the previously selected item where it was.
                 *    oldSel = A
                 *    sel = B
                 */

                // Put oldSel (A) where it belongs
                oldSel = makeAndAddView(selectedPosition - 1, oldSel.getLeft(), true,
                        mListPadding.top, false);

                final int dividerWidth = mDividerWidth;

                // Now put the new selection (B) below that
                sel = makeAndAddView(selectedPosition, oldSel.getRight() + dividerWidth, true,
                        mListPadding.top, true);

                // Some of the newly selected item extends below the bottom of the list
                if (sel.getRight() > rightSelectionPixel) {

                    // Find space available above the selection into which we can scroll upwards
                    int spaceAbove = sel.getLeft() - leftSelectionPixel;

                    // Find space required to bring the bottom of the selected item fully into view
                    int spaceBelow = sel.getRight() - rightSelectionPixel;

                    // Don't scroll more than half the height of the list
                    int halfVerticalSpace = (childrenBottom - childrenTop) / 2;
                    int offset = Math.min(spaceAbove, spaceBelow);
                    offset = Math.min(offset, halfVerticalSpace);

                    // We placed oldSel, so offset that item
                    oldSel.offsetLeftAndRight(-offset);
                    // Now offset the selected item to get it into view
                    sel.offsetLeftAndRight(-offset);
                }

                // Fill in views above and below
                if (!mStackFromBottom) {
                    fillUp(mSelectedPosition - 2, sel.getTop() - dividerWidth);
                    adjustViewsUpOrDown();
                    fillDown(mSelectedPosition + 1, sel.getBottom() + dividerWidth);
                } else {
                    fillDown(mSelectedPosition + 1, sel.getBottom() + dividerWidth);
                    adjustViewsUpOrDown();
                    fillUp(mSelectedPosition - 2, sel.getTop() - dividerWidth);
                }
            } else if (delta < 0) {
                /*
                 * Case 2: Scrolling up.
                 */

                /*
                 *     Before           After
                 *    |       |        |       |
                 *    +-------+        +-------+
                 *    |   A   |        |   A   |
                 *    +-------+   =>   |   1   |
                 *    |   B   |        +-------+
                 *    |   2   |        |   B   |
                 *    +-------+        +-------+
                 *    |       |        |       |
                 *
                 *    Try to keep the top of the item about to become selected where it was.
                 *    newSel = A
                 *    olSel = B
                 */

                if (newSel != null) {
                    // Try to position the top of newSel (A) where it was before it was selected
                    sel = makeAndAddView(selectedPosition, newSel.getLeft(), true, mListPadding.top,
                            true);
                } else {
                    // If (A) was not on screen and so did not have a view, position
                    // it above the oldSel (B)
                    sel = makeAndAddView(selectedPosition, oldSel.getLeft(), false, mListPadding.top,
                            true);
                }

                // Some of the newly selected item extends above the top of the list
                if (sel.getLeft() < leftSelectionPixel) {
                    // Find space required to bring the top of the selected item fully into view
                    int spaceAbove = leftSelectionPixel - sel.getLeft();

                   // Find space available below the selection into which we can scroll downwards
                    int spaceBelow = rightSelectionPixel - sel.getRight();

                    // Don't scroll more than half the height of the list
                    int halfVerticalSpace = (childrenBottom - childrenTop) / 2;
                    int offset = Math.min(spaceAbove, spaceBelow);
                    offset = Math.min(offset, halfVerticalSpace);

                    // Offset the selected item to get it into view
                    sel.offsetLeftAndRight(offset);
                }

                // Fill in views above and below
                fillAboveAndBelow(sel, selectedPosition);
            } else {

                int oldLeft = oldSel.getLeft();

                /*
                 * Case 3: Staying still
                 */
                sel = makeAndAddView(selectedPosition, oldSel.getLeft(), true, mListPadding.top, true);

                // We're staying still...
                if (oldLeft < childrenTop) {
                    // ... but the top of the old selection was off screen.
                    // (This can happen if the data changes size out from under us)
                    int newRight = sel.getRight();
                    if (newRight < childrenTop + 20) {
                        // Not enough visible -- bring it onscreen
                        sel.offsetLeftAndRight(childrenTop - sel.getLeft());
                    }
                }

                // Fill in views above and below
                fillAboveAndBelow(sel, selectedPosition);
            }
        } else {
            int fadingEdgeLength = getVerticalFadingEdgeLength();
            final int selectedPosition = mSelectedPosition;

            final int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength,
                    selectedPosition);
            final int bottomSelectionPixel = getBottomSelectionPixel(childrenTop, fadingEdgeLength,
                    selectedPosition);

            if (delta > 0) {
                /*
                 * Case 1: Scrolling down.
                 */

                /*
                 *     Before           After
                 *    |       |        |       |
                 *    +-------+        +-------+
                 *    |   A   |        |   A   |
                 *    |   1   |   =>   +-------+
                 *    +-------+        |   B   |
                 *    |   B   |        |   2   |
                 *    +-------+        +-------+
                 *    |       |        |       |
                 *
                 *    Try to keep the top of the previously selected item where it was.
                 *    oldSel = A
                 *    sel = B
                 */

                // Put oldSel (A) where it belongs
                oldSel = makeAndAddView(selectedPosition - 1, oldSel.getTop(), true,
                        mListPadding.left, false);

                final int dividerHeight = mDividerHeight;

                // Now put the new selection (B) below that
                sel = makeAndAddView(selectedPosition, oldSel.getBottom() + dividerHeight, true,
                        mListPadding.left, true);

                // Some of the newly selected item extends below the bottom of the list
                if (sel.getBottom() > bottomSelectionPixel) {

                    // Find space available above the selection into which we can scroll upwards
                    int spaceAbove = sel.getTop() - topSelectionPixel;

                    // Find space required to bring the bottom of the selected item fully into view
                    int spaceBelow = sel.getBottom() - bottomSelectionPixel;

                    // Don't scroll more than half the height of the list
                    int halfVerticalSpace = (childrenBottom - childrenTop) / 2;
                    int offset = Math.min(spaceAbove, spaceBelow);
                    offset = Math.min(offset, halfVerticalSpace);

                    // We placed oldSel, so offset that item
                    oldSel.offsetTopAndBottom(-offset);
                    // Now offset the selected item to get it into view
                    sel.offsetTopAndBottom(-offset);
                }

                // Fill in views above and below
                if (!mStackFromBottom) {
                    fillUp(mSelectedPosition - 2, sel.getTop() - dividerHeight);
                    adjustViewsUpOrDown();
                    fillDown(mSelectedPosition + 1, sel.getBottom() + dividerHeight);
                } else {
                    fillDown(mSelectedPosition + 1, sel.getBottom() + dividerHeight);
                    adjustViewsUpOrDown();
                    fillUp(mSelectedPosition - 2, sel.getTop() - dividerHeight);
                }
            } else if (delta < 0) {
                /*
                 * Case 2: Scrolling up.
                 */

                /*
                 *     Before           After
                 *    |       |        |       |
                 *    +-------+        +-------+
                 *    |   A   |        |   A   |
                 *    +-------+   =>   |   1   |
                 *    |   B   |        +-------+
                 *    |   2   |        |   B   |
                 *    +-------+        +-------+
                 *    |       |        |       |
                 *
                 *    Try to keep the top of the item about to become selected where it was.
                 *    newSel = A
                 *    olSel = B
                 */

                if (newSel != null) {
                    // Try to position the top of newSel (A) where it was before it was selected
                    sel = makeAndAddView(selectedPosition, newSel.getTop(), true, mListPadding.left,
                            true);
                } else {
                    // If (A) was not on screen and so did not have a view, position
                    // it above the oldSel (B)
                    sel = makeAndAddView(selectedPosition, oldSel.getTop(), false, mListPadding.left,
                            true);
                }

                // Some of the newly selected item extends above the top of the list
                if (sel.getTop() < topSelectionPixel) {
                    // Find space required to bring the top of the selected item fully into view
                    int spaceAbove = topSelectionPixel - sel.getTop();

                   // Find space available below the selection into which we can scroll downwards
                    int spaceBelow = bottomSelectionPixel - sel.getBottom();

                    // Don't scroll more than half the height of the list
                    int halfVerticalSpace = (childrenBottom - childrenTop) / 2;
                    int offset = Math.min(spaceAbove, spaceBelow);
                    offset = Math.min(offset, halfVerticalSpace);

                    // Offset the selected item to get it into view
                    sel.offsetTopAndBottom(offset);
                }

                // Fill in views above and below
                fillAboveAndBelow(sel, selectedPosition);
            } else {

                int oldTop = oldSel.getTop();

                /*
                 * Case 3: Staying still
                 */
                sel = makeAndAddView(selectedPosition, oldTop, true, mListPadding.left, true);

                // We're staying still...
                if (oldTop < childrenTop) {
                    // ... but the top of the old selection was off screen.
                    // (This can happen if the data changes size out from under us)
                    int newBottom = sel.getBottom();
                    if (newBottom < childrenTop + 20) {
                        // Not enough visible -- bring it onscreen
                        sel.offsetTopAndBottom(childrenTop - sel.getTop());
                    }
                }

                // Fill in views above and below
                fillAboveAndBelow(sel, selectedPosition);
            }
        }
        // End: Andrew, Liu

        return sel;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Sets up mListPadding
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childWidth = 0;
        int childHeight = 0;

        mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
        if (mItemCount > 0 && (widthMode == MeasureSpec.UNSPECIFIED ||
                heightMode == MeasureSpec.UNSPECIFIED)) {
            final View child = obtainView(0);

            measureScrapChild(child, 0, widthMeasureSpec);

            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();

            if (recycleOnMeasure()) {
                mRecycler.addScrapView(child);
            }
        }

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = mListPadding.left + mListPadding.right + childWidth +
                    getVerticalScrollbarWidth();
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = mListPadding.top + mListPadding.bottom + childHeight +
                    getVerticalFadingEdgeLength() * 2;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            // TODO: after first layout we should maybe start at the first visible position, not 0
            heightSize = measureHeightOfChildren(widthMeasureSpec, 0, NO_POSITION, heightSize, -1);
        }

        // Start: Andrew, Liu
        if (widthMode == MeasureSpec.AT_MOST) {
            // TODO: after first layout we should maybe start at the first visible position, not 0
            widthSize = measureWidthOfChildren(heightMeasureSpec, 0, NO_POSITION, widthSize, -1);
        }
        // End: Andrew, Liu

        setMeasuredDimension(widthSize, heightSize);
        mWidthMeasureSpec = widthMeasureSpec;

        // Start: Andrew, Liu
        mHeightMeasureSpec = heightMeasureSpec;
        // End: Andrew, Liu
    }

    private void measureScrapChild(View child, int position, int widthMeasureSpec) {
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0);
            child.setLayoutParams(p);
        }
        p.viewType = mAdapter.getItemViewType(position);

        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            int childHeightSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                    mListPadding.top + mListPadding.bottom, p.height);
            int lpWidth = p.width;
            int childWidthSpec;
            if (lpWidth > 0) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth, MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                    mListPadding.left + mListPadding.right, p.width);
            int lpHeight = p.height;
            int childHeightSpec;
            if (lpHeight > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            child.measure(childWidthSpec, childHeightSpec);
        }
        // End: Andrew, Liu
    }

    /**
     * @return True to recycle the views used to measure this ListView in
     *         UNSPECIFIED/AT_MOST modes, false otherwise.
     * @hide
     */
    protected boolean recycleOnMeasure() {
        return true;
    }

    /**
     * Measures the height of the given range of children (inclusive) and
     * returns the height with this ListView's padding and divider heights
     * included. If maxHeight is provided, the measuring will stop when the
     * current height reaches maxHeight.
     *
     * @param widthMeasureSpec
     *            The width measure spec to be given to a child's
     *            {@link View#measure(int, int)}.
     * @param startPosition
     *            The position of the first child to be shown.
     * @param endPosition
     *            The (inclusive) position of the last child to be shown.
     *            Specify {@link #NO_POSITION} if the last child should be the
     *            last available child from the adapter.
     * @param maxHeight
     *            The maximum height that will be returned (if all the children
     *            don't fit in this value, this value will be returned).
     * @param disallowPartialChildPosition
     *            In general, whether the returned height should only contain
     *            entire children. This is more powerful--it is the first
     *            inclusive position at which partial children will not be
     *            allowed. Example: it looks nice to have at least 3 completely
     *            visible children, and in portrait this will most likely fit;
     *            but in landscape there could be times when even 2 children can
     *            not be completely shown, so a value of 2 (remember, inclusive)
     *            would be good (assuming startPosition is 0).
     * @return The height of this ListView with the given children.
     */
    final int measureHeightOfChildren(int widthMeasureSpec, int startPosition, int endPosition,
            final int maxHeight, int disallowPartialChildPosition) {

        final ListAdapter adapter = mAdapter;
        if (adapter == null) {
            return mListPadding.top + mListPadding.bottom;
        }

        // Include the padding of the list
        int returnedHeight = mListPadding.top + mListPadding.bottom;
        final int dividerHeight = ((mDividerHeight > 0) && mDivider != null) ? mDividerHeight : 0;
        // The previous height value that was less than maxHeight and contained
        // no partial children
        int prevHeightWithoutPartialChild = 0;
        int i;
        View child;

        // mItemCount - 1 since endPosition parameter is inclusive
        endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1 : endPosition;
        final AbsCrabWalkView.RecycleBin recycleBin = mRecycler;
        final boolean recyle = recycleOnMeasure();

        for (i = startPosition; i <= endPosition; ++i) {
            child = obtainView(i);

            measureScrapChild(child, i, widthMeasureSpec);

            if (i > 0) {
                // Count the divider for all but one child
                returnedHeight += dividerHeight;
            }

            // Recycle the view before we possibly return from the method
            if (recyle) {
                recycleBin.addScrapView(child);
            }

            returnedHeight += child.getMeasuredHeight();

            if (returnedHeight >= maxHeight) {
                // We went over, figure out which height to return.  If returnedHeight > maxHeight,
                // then the i'th position did not fit completely.
                return (disallowPartialChildPosition >= 0) // Disallowing is enabled (> -1)
                            && (i > disallowPartialChildPosition) // We've past the min pos
                            && (prevHeightWithoutPartialChild > 0) // We have a prev height
                            && (returnedHeight != maxHeight) // i'th child did not fit completely
                        ? prevHeightWithoutPartialChild
                        : maxHeight;
            }

            if ((disallowPartialChildPosition >= 0) && (i >= disallowPartialChildPosition)) {
                prevHeightWithoutPartialChild = returnedHeight;
            }
        }

        // At this point, we went through the range of children, and they each
        // completely fit, so return the returnedHeight
        return returnedHeight;
    }

    // Start: Andrew, Liu
    /**
     * Measures the width of the given range of children (inclusive) and returns
     * the width with this ListView's padding and divider widths included. If
     * maxWidth is provided, the measuring will stop when the current width
     * reaches maxWidth.
     *
     * @param heightMeasureSpec
     *            The height measure spec to be given to a child's
     *            {@link View#measure(int, int)}.
     * @param startPosition
     *            The position of the first child to be shown.
     * @param endPosition
     *            The (inclusive) position of the last child to be shown.
     *            Specify {@link #NO_POSITION} if the last child should be the
     *            last available child from the adapter.
     * @param maxWidth
     *            The maximum width that will be returned (if all the children
     *            don't fit in this value, this value will be returned).
     * @param disallowPartialChildPosition
     *            In general, whether the returned height should only contain
     *            entire children. This is more powerful--it is the first
     *            inclusive position at which partial children will not be
     *            allowed. Example: it looks nice to have at least 3 completely
     *            visible children, and in portrait this will most likely fit;
     *            but in landscape there could be times when even 2 children can
     *            not be completely shown, so a value of 2 (remember, inclusive)
     *            would be good (assuming startPosition is 0).
     * @return The width of this ListView with the given children.
     */
    final int measureWidthOfChildren(int heightMeasureSpec, int startPosition, int endPosition,
            final int maxWidth, int disallowPartialChildPosition) {

        final ListAdapter adapter = mAdapter;
        if (adapter == null) {
            return mListPadding.left + mListPadding.right;
        }

        // Include the padding of the list
        int returnedWidth = mListPadding.left + mListPadding.right;
        final int dividerWidth = ((mDividerWidth > 0) && mDivider != null) ? mDividerWidth : 0;
        // The previous height value that was less than maxHeight and contained
        // no partial children
        int prevWidthWithoutPartialChild = 0;
        int i;
        View child;

        // mItemCount - 1 since endPosition parameter is inclusive
        endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1 : endPosition;
        final AbsCrabWalkView.RecycleBin recycleBin = mRecycler;
        final boolean recyle = recycleOnMeasure();

        for (i = startPosition; i <= endPosition; ++i) {
            child = obtainView(i);

            measureScrapChild(child, i, heightMeasureSpec);

            if (i > 0) {
                // Count the divider for all but one child
                returnedWidth += dividerWidth;
            }

            // Recycle the view before we possibly return from the method
            if (recyle) {
                recycleBin.addScrapView(child);
            }

            returnedWidth += child.getMeasuredWidth();

            if (returnedWidth >= maxWidth) {
                // We went over, figure out which width to return.  If returnedWidth > maxWidth,
                // then the i'th position did not fit completely.
                return (disallowPartialChildPosition >= 0) // Disallowing is enabled (> -1)
                            && (i > disallowPartialChildPosition) // We've past the min pos
                            && (prevWidthWithoutPartialChild > 0) // We have a prev height
                            && (returnedWidth != maxWidth) // i'th child did not fit completely
                        ? prevWidthWithoutPartialChild
                        : maxWidth;
            }

            if ((disallowPartialChildPosition >= 0) && (i >= disallowPartialChildPosition)) {
                prevWidthWithoutPartialChild = returnedWidth;
            }
        }

        // At this point, we went through the range of children, and they each
        // completely fit, so return the returnedWidth
        return returnedWidth;
    }

    @Override
    int findMotionColumn(int y) {
        int childCount = getChildCount();
        if ( childCount > 0 ) {
            for ( int i = 0; i < childCount; i++ ) {
                View v = getChildAt(i);
                if ( y <= v.getRight() ) {
                    return mFirstPosition + i;
                }
            }
        }
        return INVALID_POSITION;
    }

    // End: Andrew, Liu

    @Override
    int findMotionRow(int y) {
        int childCount = getChildCount();
        if ( childCount > 0 ) {
            for ( int i = 0; i < childCount; i++ ) {
                View v = getChildAt(i);
                if ( y <= v.getBottom() ) {
                    return mFirstPosition + i;
                }
            }
            return mFirstPosition + childCount - 1;
        }
        return INVALID_POSITION;
    }

    /**
     * Put a specific item at a specific location on the screen and then build
     * up and down from there.
     *
     * @param position
     *            The reference view to use as the starting point
     * @param top
     *            Pixel offset from the top of this view to the top of the
     *            reference view.
     *
     * @return The selected view, or null if the selected view is outside the
     *         visible area.
     */
    private View fillSpecific(int position, int top) {
        boolean tempIsSelected = position == mSelectedPosition;
        View temp;/* = makeAndAddView(position, top, true, mListPadding.left,
            tempIsSelected);*/
        if ( isHorizontalStyle() ) {
            temp = makeAndAddView(position, top, true, mListPadding.top,
                    tempIsSelected);
        }else {
            temp = makeAndAddView(position, top, true, mListPadding.left,
                    tempIsSelected);
        }
        // Possibly changed again in fillUp if we add rows above this one.
        mFirstPosition = position;

        View above;
        View below;

        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            final int dividerWidth = mDividerWidth;
            if ( !mStackFromBottom ) {
                above = fillUp(position - 1, temp.getLeft() - dividerWidth);
                // This will correct for the top of the first view not touching the top of the list
                adjustViewsUpOrDown();
                below = fillDown(position + 1, temp.getRight() + dividerWidth);
                int childCount = getChildCount();
                if ( childCount > 0 ) {
                    correctTooHigh(childCount);
                }
            } else {
                below = fillDown(position + 1, temp.getRight() + dividerWidth);
                // This will correct for the bottom of the last view not touching the bottom of the list
                adjustViewsUpOrDown();
                above = fillUp(position - 1, temp.getLeft() - dividerWidth);
                int childCount = getChildCount();
                if ( childCount > 0 ) {
                    correctTooLow(childCount);
                }
            }
        } else {
            final int dividerHeight = mDividerHeight;
            if ( !mStackFromBottom ) {
                above = fillUp(position - 1, temp.getTop() - dividerHeight);
                // This will correct for the top of the first view not touching the top of the list
                adjustViewsUpOrDown();
                below = fillDown(position + 1, temp.getBottom() + dividerHeight);
                int childCount = getChildCount();
                if ( childCount > 0 ) {
                    correctTooHigh(childCount);
                }
            } else {
                below = fillDown(position + 1, temp.getBottom() + dividerHeight);
                // This will correct for the bottom of the last view not touching the bottom of the list
                adjustViewsUpOrDown();
                above = fillUp(position - 1, temp.getTop() - dividerHeight);
                int childCount = getChildCount();
                if ( childCount > 0 ) {
                    correctTooLow(childCount);
                }
            }
        }
/*
    if (!mSpeedUp)
        updateBackGround();
*/

        if ( tempIsSelected ) {
            return temp;
        } else if ( above != null ) {
            return above;
        } else {
            return below;
        }
    }

    /**
     * Check if we have dragged the bottom of the list too high (we have pushed
     * the top element off the top of the screen when we did not need to).
     * Correct by sliding everything back down.
     *
     * @param childCount
     *            Number of children
     */
    private void correctTooHigh(int childCount) {
        // First see if the last item is visible. If it is not, it is OK for the
        // top of the list to be pushed up.
        int lastPosition = mFirstPosition + childCount - 1;
        if (lastPosition == mItemCount - 1 && childCount > 0) {

            // Get the last child ...
            final View lastChild = getChildAt(childCount - 1);

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                // ... and its right edge
                final int lastRight = lastChild.getRight();

                // This is rigth of our drawable area
                final int end = (getRight() - getLeft()) - mListPadding.right - getRightBoundary() * 2 - getRightBorderWidth();

                // This is how far the rigth edge of the last view is from the right of the
                // drawable area
                int rightOffset = end - lastRight;
                View firstChild = getChildAt(0);
                final int firstLeft = firstChild.getLeft();

                // Make sure we are 1) Too high, and 2) Either there are more rows above the
                // first row or the first row is scrolled off the top of the drawable area
                if (rightOffset > 0 && (mFirstPosition > 0 || firstLeft < mListPadding.left))  {
                    if (mFirstPosition == 0) {
                        // Don't pull the top too far down
                        rightOffset = Math.min(rightOffset, mListPadding.left - firstLeft);
                    }
                    // Move everything down
                    offsetChildrenLeftAndRight(rightOffset);
                    if (mFirstPosition > 0) {
                        // Fill the gap that was opened above mFirstPosition with more rows, if
                        // possible
                        fillUp(mFirstPosition - 1, firstChild.getLeft() - mDividerWidth);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }

                }
            } else {
                // ... and its bottom edge
                final int lastBottom = lastChild.getBottom();

                // This is bottom of our drawable area
                final int end = (getBottom() - getTop()) - mListPadding.bottom - getBottomBoundary() * 2 - getBottomBorderHeight();

                // This is how far the bottom edge of the last view is from the bottom of the
                // drawable area
                int bottomOffset = end - lastBottom;
                View firstChild = getChildAt(0);
                final int firstTop = firstChild.getTop();

                // Make sure we are 1) Too high, and 2) Either there are more rows above the
                // first row or the first row is scrolled off the top of the drawable area
                if (bottomOffset > 0 && (mFirstPosition > 0 || firstTop < mListPadding.top))  {
                    if (mFirstPosition == 0) {
                        // Don't pull the top too far down
                        bottomOffset = Math.min(bottomOffset, mListPadding.top - firstTop);
                    }
                    // Move everything down
                    //offsetChildrenTopAndBottom(bottomOffset);
                    if (mFirstPosition > 0) {
                        // Fill the gap that was opened above mFirstPosition with more rows, if
                        // possible
                        fillUp(mFirstPosition - 1, firstChild.getTop() - mDividerHeight);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }
                }
            }
            // End: Andrew, Liu
        }
    }

    /**
     * Check if we have dragged the bottom of the list too low (we have pushed
     * the bottom element off the bottom of the screen when we did not need to).
     * Correct by sliding everything back up.
     *
     * @param childCount
     *            Number of children
     */
    private void correctTooLow(int childCount) {
        // First see if the first item is visible. If it is not, it is OK for the
        // bottom of the list to be pushed down.
        if (mFirstPosition == 0 && childCount > 0) {

            // Get the first child ...
            final View firstChild = getChildAt(0);

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                // ... and its left edge
                final int firstLeft = firstChild.getLeft();

                // This is left of our drawable area
                final int start = mListPadding.left + getLeftBoundary() + getLeftBorderWidth();

                // This is right of our drawable area
                final int end = (getRight() - getLeft()) - mListPadding.right - getRightBorderWidth();

                // This is how far the top edge of the first view is from the top of the
                // drawable area
                int leftOffset = firstLeft - start;
                View lastChild = getChildAt(childCount - 1);
                final int lastRight = lastChild.getRight();
                int lastPosition = mFirstPosition + childCount - 1;

                // Make sure we are 1) Too low, and 2) Either there are more rows below the
                // last row or the last row is scrolled off the bottom of the drawable area
                if (leftOffset > 0 && (lastPosition < mItemCount - 1 || lastRight > end))  {
                    if (lastPosition == mItemCount - 1 ) {
                        // Don't pull the bottom too far up
                        leftOffset = Math.min(leftOffset, lastRight - end);
                    }
                    // Move everything up
                    offsetChildrenLeftAndRight(-leftOffset);
                    if (lastPosition < mItemCount - 1) {
                        // Fill the gap that was opened below the last position with more rows, if
                        // possible
                        fillDown(lastPosition + 1, lastChild.getRight() + mDividerWidth);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }
                }
            } else {
                // ... and its top edge
                final int firstTop = firstChild.getTop();

                // This is top of our drawable area
                final int start = mListPadding.top + getTopBoundary() + getTopBorderHeight();

                // This is bottom of our drawable area
                final int end = (getBottom() - getTop()) - mListPadding.bottom - getBottomBorderHeight();

                // This is how far the top edge of the first view is from the top of the
                // drawable area
                int topOffset = firstTop - start;
                View lastChild = getChildAt(childCount - 1);
                final int lastBottom = lastChild.getBottom();
                int lastPosition = mFirstPosition + childCount - 1;

                // Make sure we are 1) Too low, and 2) Either there are more rows below the
                // last row or the last row is scrolled off the bottom of the drawable area
                if (topOffset > 0 && (lastPosition < mItemCount - 1 || lastBottom > end))  {
                    if (lastPosition == mItemCount - 1 ) {
                        // Don't pull the bottom too far up
                        topOffset = Math.min(topOffset, lastBottom - end);
                    }
                    // Move everything up
                    //offsetChildrenTopAndBottom(-topOffset);
                    if (lastPosition < mItemCount - 1) {
                        // Fill the gap that was opened below the last position with more rows, if
                        // possible
                        fillDown(lastPosition + 1, lastChild.getBottom() + mDividerHeight);
                        // Close up the remaining gap
                        adjustViewsUpOrDown();
                    }
                }
            }
            // End: Andrew, Liu
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void layoutChildren() {
        final boolean blockLayoutRequests = mBlockLayoutRequests;
        if (!blockLayoutRequests) {
            mBlockLayoutRequests = true;
        } else {
            return;
        }

        try {
            super.layoutChildren();

            invalidate();

            if (mAdapter == null) {
                resetList();
                invokeOnItemScrollListener();
                return;
            }
            //Modify by Jason Chiu 2009.5.21 for layout at boundary.
            int childrenTop = mListPadding.top + getTopBorderHeight();
            int childrenBottom = getBottom() - getTop() - mListPadding.bottom - getBottomBorderHeight();

            // Start: Andrew, Liu
            int childrenLeft = mListPadding.left + getLeftBorderWidth();
            int childrenRight = getRight() - getLeft() - mListPadding.right - getRightBorderWidth();
            // End: Andrew, Liu

            int childCount = getChildCount();
            int index;
            int delta = 0;

            View sel = null;
            View oldSel = null;
            View oldFirst = null;
            View newSel = null;

            View focusLayoutRestoreView = null;

            // Remember stuff we will need down below
            switch (mLayoutMode) {
            case LAYOUT_SET_SELECTION:
                index = mNextSelectedPosition - mFirstPosition;
                if (index >= 0 && index < childCount) {
                    newSel = getChildAt(index);
                }
                break;
            case LAYOUT_FORCE_TOP:
            case LAYOUT_FORCE_BOTTOM:
            case LAYOUT_FORCE_LEFT:
            case LAYOUT_FORCE_RIGHT:
            case LAYOUT_SPECIFIC:
            case LAYOUT_SYNC:
                break;
            case LAYOUT_MOVE_SELECTION:
            default:
                // Remember the previously selected view
                index = mSelectedPosition - mFirstPosition;
                if (index >= 0 && index < childCount) {
                    oldSel = getChildAt(index);
                }

                // Remember the previous first child
                oldFirst = getChildAt(0);

                if (mNextSelectedPosition >= 0) {
                    delta = mNextSelectedPosition - mSelectedPosition;
                }

                // Caution: newSel might be null
                newSel = getChildAt(index + delta);
            }


            boolean dataChanged = mDataChanged;
            if (dataChanged) {
                handleDataChanged();
            }

            // Handle the empty set by removing all views that are visible
            // and calling it a day
            if (mItemCount == 0) {
                resetList();
                invokeOnItemScrollListener();
                return;
            }

            setSelectedPositionInt(mNextSelectedPosition);

            // Pull all children into the RecycleBin.
            // These views will be reused if possible
            final int firstPosition = mFirstPosition;
            final RecycleBin recycleBin = mRecycler;

            // reset the focus restoration
            View focusLayoutRestoreDirectChild = null;


            // Don't put header or footer views into the Recycler. Those are
            // already cached in mHeaderViews;
            if (dataChanged) {
                for (int i = 0; i < childCount; i++) {
                    recycleBin.addScrapView(getChildAt(i));
                    if (ViewDebug.TRACE_RECYCLER) {
                        ViewDebug.trace(getChildAt(i),
                                ViewDebug.RecyclerTraceType.MOVE_TO_SCRAP_HEAP, index, i);
                    }
                }
            } else {
                recycleBin.fillActiveViews(childCount, firstPosition);
            }

            // take focus back to us temporarily to avoid the eventual
            // call to clear focus when removing the focused child below
            // from messing things up when ViewRoot assigns focus back
            // to someone else
            final View focusedChild = getFocusedChild();
            if (focusedChild != null) {
                // TODO: in some cases focusedChild.getParent() == null

                // we can remember the focused view to restore after relayout if the
                // data hasn't changed, or if the focused position is a header or footer
                if (!dataChanged || isDirectChildHeaderOrFooter(focusedChild)) {
                    focusLayoutRestoreDirectChild = focusedChild;
                    // remember the specific view that had focus
                    focusLayoutRestoreView = findFocus();
                    if (focusLayoutRestoreView != null) {
                        // tell it we are going to mess with it
                        focusLayoutRestoreView.onStartTemporaryDetach();
                    }
                }
                requestFocus();
            }

            // Clear out old views
            //removeAllViewsInLayout();
            detachAllViewsFromParent();

            switch (mLayoutMode) {
            case LAYOUT_SET_SELECTION:
                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    if (newSel != null) {
                        sel = fillFromSelection(newSel.getLeft(), childrenLeft, childrenRight);
                    } else {
                        sel = fillFromMiddle(childrenLeft, childrenRight);
                    }
                } else {
                    if (newSel != null) {
                        sel = fillFromSelection(newSel.getTop(), childrenTop, childrenBottom);
                    } else {
                        sel = fillFromMiddle(childrenTop, childrenBottom);
                    }
                }
                break;
            case LAYOUT_SYNC:
                sel = fillSpecific(mSyncPosition, mSpecificTop);
                break;
            case LAYOUT_FORCE_BOTTOM:
                sel = fillUp(mItemCount - 1, childrenBottom);
                adjustViewsUpOrDown();
                break;
            case LAYOUT_FORCE_TOP:
                mFirstPosition = 0;
                sel = fillFromTop(childrenTop);
                adjustViewsUpOrDown();
                break;
            // Start: Andrew, Liu
            case LAYOUT_FORCE_RIGHT:
                sel = fillUp(mItemCount - 1, childrenRight);
                adjustViewsUpOrDown();
                break;
            case LAYOUT_FORCE_LEFT:
                mFirstPosition = 0;
                sel = fillFromTop(childrenLeft);
                adjustViewsUpOrDown();
                break;
            // End: Andrew, Liu
            case LAYOUT_SPECIFIC:
                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    sel = fillSpecific(reconcileSelectedPosition(), mSpecificLeft);
                } else {
                    sel = fillSpecific(reconcileSelectedPosition(), mSpecificTop);
                }
                // End: Andrew, Liu
                break;
            case LAYOUT_MOVE_SELECTION:
                // Start: Andrew, Liu
                if (oldSel != null) {
                    if (isHorizontalStyle()) {
                        sel = moveSelection(oldSel, newSel, delta, childrenLeft, childrenRight);
                    } else {
                        sel = moveSelection(oldSel, newSel, delta, childrenTop, childrenBottom);
                    }
                }
                // End: Andrew, Liu
                break;
            default:
                if (childCount == 0) {
                    if (!mStackFromBottom) {
                        final int position = lookForSelectablePosition(0, true);
                        setSelectedPositionInt(position);
                        // Start: Andrew, Liu
                        if (isHorizontalStyle()) {
                            sel = fillFromTop(childrenLeft);
                        } else {
                            sel = fillFromTop(childrenTop);
                        }
                        // End: Andrew, Liu
                    } else {
                        final int position = lookForSelectablePosition(mItemCount - 1, false);
                        setSelectedPositionInt(position);
                        // Start: Andrew, Liu
                        if (isHorizontalStyle()) {
                            sel = fillUp(mItemCount - 1, childrenRight);
                        } else {
                            sel = fillUp(mItemCount - 1, childrenBottom);
                        }
                        // End: Andrew, Liu
                    }
                } else {
                    if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {

                        // Start: Andrew, Liu
                        if (isHorizontalStyle()) {
                            sel = fillSpecific(mSelectedPosition,
                                    oldSel == null ? childrenLeft : oldSel.getLeft());
                        } else {
                            sel = fillSpecific(mSelectedPosition,
                                    oldSel == null ? childrenTop : oldSel.getTop());
                        }
                        // End: Andrew, Liu
                    } else if (mFirstPosition < mItemCount) {

                        // Start: Andrew, Liu
                        if (isHorizontalStyle()) {
                            sel = fillSpecific(mFirstPosition,
                                    oldFirst == null ? childrenLeft : oldFirst.getLeft());
                        } else {
                            sel = fillSpecific(mFirstPosition,
                                    oldFirst == null ? childrenTop : oldFirst.getTop());
                        }
                        // End: Andrew, Liu
                    } else {

                        // Start: Andrew, Liu
                        if (isHorizontalStyle()) {
                            sel = fillSpecific(0, childrenLeft);
                        } else {
                            sel = fillSpecific(0, childrenTop);
                        }
                        // End: Andrew, Liu
                    }
                }
                break;
            }

            // Flush any cached views that did not get reused above
            recycleBin.scrapActiveViews();

            if (sel != null) {
               // the current selected item should get focus if items
               // are focusable
               if (mItemsCanFocus && hasFocus() && !sel.hasFocus()) {
                   final boolean focusWasTaken = (sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null &&
                           focusLayoutRestoreView.requestFocus()) || sel.requestFocus();
                   if (!focusWasTaken) {
                       // selected item didn't take focus, fine, but still want
                       // to make sure something else outside of the selected view
                       // has focus
                       final View focused = getFocusedChild();
                       if (focused != null) {
                           focused.clearFocus();
                       }
                       positionSelector(sel);
                   } else {
                       sel.setSelected(false);
                       mSelectorRect.setEmpty();
                   }
               } else {
                   positionSelector(sel);
               }
               mSelectedTop = sel.getTop();

               // Start: Andrew, Liu
               mSelectedTop = sel.getLeft();
               // End: Andrew, Liu
            } else {
               mSelectedTop = 0;

               // Start: Andrew, Liu
               mSelectedLeft = 0;
               // End: Andrew, Liu

               mSelectorRect.setEmpty();

               // even if there is not selected position, we may need to restore
               // focus (i.e. something focusable in touch mode)
               if (hasFocus() && focusLayoutRestoreView != null) {
                   focusLayoutRestoreView.requestFocus();
               }
            }

            // tell focus view we are done mucking with it, if it is still in
            // our view hierarchy.
            if (focusLayoutRestoreView != null
                    && focusLayoutRestoreView.getWindowToken() != null) {
                focusLayoutRestoreView.onFinishTemporaryDetach();
            }

            mLayoutMode = LAYOUT_NORMAL;
            mDataChanged = false;
            mNeedSync = false;
            setNextSelectedPositionInt(mSelectedPosition);

            updateScrollIndicators();

            if (mItemCount > 0) {
                checkSelectionChanged();
            }

            invokeOnItemScrollListener();
        } finally {
            if (!blockLayoutRequests) {
                mBlockLayoutRequests = false;
            }
        }
    }

    /**
     * @param child
     *            a direct child of this list.
     * @return Whether child is a header or footer view.
     */
    private boolean isDirectChildHeaderOrFooter(View child) {

        final ArrayList<FixedViewInfo> headers = mHeaderViewInfos;
        final int numHeaders = headers.size();
        for (int i = 0; i < numHeaders; i++) {
            if (child == headers.get(i).view) {
                return true;
            }
        }
        final ArrayList<FixedViewInfo> footers = mFooterViewInfos;
        final int numFooters = footers.size();
        for (int i = 0; i < numFooters; i++) {
            if (child == footers.get(i).view) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain the view and add it to our list of children. The view can be made
     * fresh, converted from an unused view, or used as is if it was in the
     * recycle bin.
     *
     * @param position
     *            Logical position in the list
     * @param y
     *            Top or bottom edge of the view to add
     * @param flow
     *            If flow is true, align top edge to y. If false, align bottom
     *            edge to y.
     * @param childrenLeft
     *            Left edge where children should be positioned
     * @param selected
     *            Is this position selected?
     * @return View that was added
     */
    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft,
            boolean selected) {
        View child;


        if (!mDataChanged) {
            // Try to use an exsiting view for this position
            child = mRecycler.getActiveView(position);
            if (child != null) {
                if (ViewDebug.TRACE_RECYCLER) {
                    ViewDebug.trace(child, ViewDebug.RecyclerTraceType.RECYCLE_FROM_ACTIVE_HEAP,
                            position, getChildCount());
                }

                // Found it -- we're using an existing child
                // This just needs to be positioned
                setupChild(child, position, y, flow, childrenLeft, selected, true);

                return child;
            }
        }

        // Make a new view for this position, or convert an unused view if possible
        child = obtainView(position);

        // This needs to be positioned and measured
        setupChild(child, position, y, flow, childrenLeft, selected, false);

        return child;
    }

    /**
     * Add a view as a child and make sure it is measured (if necessary) and
     * positioned properly.
     *
     * @param child
     *            The view to add
     * @param position
     *            The position of this child
     * @param y
     *            The y position relative to which this view will be positioned
     * @param flowDown
     *            If true, align top edge to y. If false, align bottom edge to
     *            y.
     * @param childrenLeft
     *            Left edge where children should be positioned
     * @param selected
     *            Is this position selected?
     * @param recycled
     *            Has this view been pulled from the recycle bin? If so it does
     *            not need to be remeasured.
     */
    private void setupChild(View child, int position, int y, boolean flowDown, int childrenLeft,
            boolean selected, boolean recycled) {
        //+Kun, prevent null pointer exception
        if(child == null)
            return;
        //-Kun
        final boolean isSelected = selected && shouldShowSelector();
        final boolean updateChildSelected = isSelected != child.isSelected();
        final boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

        // Respect layout params that are already in the view. Otherwise make some up...
        // noinspection unchecked
        AbsCrabWalkView.LayoutParams p = (AbsCrabWalkView.LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = new AbsCrabWalkView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        }
        p.viewType = mAdapter.getItemViewType(position);

        if (recycled || (p.recycledHeaderFooter &&
                p.viewType == HtcAdapterView2.ITEM_VIEW_TYPE_HEADER_OR_FOOTER)) {
            attachViewToParent(child, flowDown ? -1 : 0, p);
        } else {
            if (p.viewType == HtcAdapterView2.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                p.recycledHeaderFooter = true;
            }
            addViewInLayout(child, flowDown ? -1 : 0, p, true);
        }

        if (updateChildSelected) {
            child.setSelected(isSelected);
        }

        if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
                if (child instanceof Checkable) {
                    ((Checkable) child).setChecked(mCheckStates.get(position));
                }
        }

        if (needToMeasure) {
            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec,
                        mListPadding.top + mListPadding.bottom, p.height);
                int lpWidth = p.width;
                int childWidthSpec;
                if (lpWidth > 0) {
                    childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth, MeasureSpec.EXACTLY);
                } else {
                    childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                }
                child.measure(childWidthSpec, childHeightSpec);
            } else {
                int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
                        mListPadding.left + mListPadding.right, p.width);
                int lpHeight = p.height;
                int childHeightSpec;
                if (lpHeight > 0) {
                    childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
                } else {
                    childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                }
                child.measure(childWidthSpec, childHeightSpec);
            }
            // End: Andrew, Liu
        } else {
            cleanupLayoutState(child);
        }

        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();

        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            final int childTop = flowDown ? y : y - w;

            if (needToMeasure) {
                final int childBottom = childrenLeft + h;
                final int childRight = childTop + w;
                child.layout(childTop, childrenLeft, childRight, childBottom);
            } else {
                child.offsetLeftAndRight(childTop - child.getLeft());
                child.offsetTopAndBottom(childrenLeft - child.getTop());
            }
        } else {
            final int childTop = flowDown ? y : y - h;

            if (needToMeasure) {
                final int childRight = childrenLeft + w;
                final int childBottom = childTop + h;
                child.layout(childrenLeft, childTop, childRight, childBottom);
            } else {
                child.offsetLeftAndRight(childrenLeft - child.getLeft());
                child.offsetTopAndBottom(childTop - child.getTop());
            }
        }
        // End: Andrew, Liu

        if (mCachingStarted && !child.isDrawingCacheEnabled()) {
            child.setDrawingCacheEnabled(true);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected boolean canAnimate() {
        return super.canAnimate() && mItemCount > 0;
    }

    /**
     * Sets the currently selected item. If in touch mode, the item will not be
     * selected but it will still be positioned appropriately. If the specified
     * selection position is less than 0, then the item at position 0 will be
     * selected.
     *
     * @param position
     *            Index (starting at 0) of the data item to be selected.
     */
    @Override
    public void setSelection(int position) {
        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            setSelectionFromLeft(position, 0); //update by Ferro; to follow the eclair_mainline_20090928 CL#76435
        } else {
        setSelectionFromTop(position, 0);  //update by Ferro; to follow the eclair_mainline_20090928 CL#76435
        }
        // End: Andrew, Liu
    }

    /**
     * Sets the selected item and positions the selection y pixels from the top
     * edge of the ListView. (If in touch mode, the item will not be selected
     * but it will still be positioned appropriately.)
     *
     * @param position
     *            Index (starting at 0) of the data item to be selected.
     * @param y
     *            The distance from the top edge of the ListView (plus padding)
     *            that the item will be positioned.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setSelectionFromTop(int position, int y) {
        if (mAdapter == null) {
            return;
        }

        if (!isInTouchMode()) {
            position = lookForSelectablePosition(position, true);
            if (position >= 0) {
                setNextSelectedPositionInt(position);
            }
        } else {
            mResurrectToPosition = position;
        }

        if (position >= 0) {
            mLayoutMode = LAYOUT_SPECIFIC;
            mSpecificTop = mListPadding.top + getTopBorderHeight()+ y;

            if (mNeedSync) {
                mSyncPosition = position;
                mSyncRowId = mAdapter.getItemId(position);
            }

            requestLayout();
        }
    }

    // Start: Andrew, Liu
    /**
     * Sets the selected item and positions the selection y pixels from the Left
     * edge of the ListView. (If in touch mode, the item will not be selected
     * but it will still be positioned appropriately.)
     *
     * @param position
     *            Index (starting at 0) of the data item to be selected.
     * @param x
     *            The distance from the left edge of the ListView (plus padding)
     *            that the item will be positioned.
     */
    public void setSelectionFromLeft(int position, int x) {
        if (mAdapter == null) {
            return;
        }

        if (!isInTouchMode()) {
            position = lookForSelectablePosition(position, true);
            if (position >= 0) {
                setNextSelectedPositionInt(position);
            }
        } else {
            mResurrectToPosition = position;
        }

        if (position >= 0) {
            mLayoutMode = LAYOUT_SPECIFIC;
            mSpecificLeft = mListPadding.left + getLeftBorderWidth()+ x;

            if (mNeedSync) {
                mSyncPosition = position;
                mSyncColumnId = mAdapter.getItemId(position);
            }

            requestLayout();
        }
    }
    // End: Andrew, Liu

    /**
     * Makes the item at the supplied position selected.
     *
     * @param position
     *            the position of the item to select
     */
    @Override
    void setSelectionInt(int position) {
        setNextSelectedPositionInt(position);
        layoutChildren();
    }

    /**
     * Find a position that can be selected (i.e., is not a separator).
     *
     * @param position The starting position to look at.
     * @param lookDown Whether to look down for other positions.
     * @return The next selectable position starting at position and then searching either up or
     *         down. Returns {@link #INVALID_POSITION} if nothing can be found.
     */
    @Override
    int lookForSelectablePosition(int position, boolean lookDown) {
        final ListAdapter adapter = mAdapter;
        if (adapter == null || isInTouchMode()) {
            return INVALID_POSITION;
        }

        final int count = adapter.getCount();
        if (!mAreAllItemsSelectable) {
            if (lookDown) {
                position = Math.max(0, position);
                while (position < count && !adapter.isEnabled(position)) {
                    position++;
                }
            } else {
                position = Math.min(position, count - 1);
                while (position >= 0 && !adapter.isEnabled(position)) {
                    position--;
                }
            }

            if (position < 0 || position >= count) {
                return INVALID_POSITION;
            }
            return position;
        } else {
            if (position < 0 || position >= count) {
                return INVALID_POSITION;
            }
            return position;
        }
    }

    /**
     * setSelectionAfterHeaderView set the selection to be the first list item
     * after the header views.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setSelectionAfterHeaderView() {
        final int count = mHeaderViewInfos.size();
        if (count > 0) {
            mNextSelectedPosition = 0;
            return;
        }

        if (mAdapter != null) {
            setSelection(count);
        } else {
            mNextSelectedPosition = count;
            mLayoutMode = LAYOUT_SET_SELECTION;
        }

    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Dispatch in the normal way
        boolean handled = super.dispatchKeyEvent(event);
        if (!handled) {
            // If we didn't handle it...
            View focused = getFocusedChild();
            if (focused != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                // ... and our focused child didn't handle it
                // ... give it to ourselves so we can scroll if necessary
                handled = onKeyDown(event.getKeyCode(), event);
            }
        }
        return handled;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return commonKey(keyCode, repeatCount, event);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if (mAdapter == null) {
            return false;
        }

        if (mDataChanged) {
            layoutChildren();
        }

        boolean handled = false;
        int action = event.getAction();

        if (action != KeyEvent.ACTION_UP) {
            if (mSelectedPosition < 0) {
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_SPACE:
                    if (resurrectSelection()) {
                        return true;
                    }
                }
            }
            switch (keyCode) {
            // Start: Andrew, Liu
            case KeyEvent.KEYCODE_DPAD_UP:
            //Common control*
                if(isHorizontalStyle()) {
                    // For horizontal style, we should not handle up and down key event.
                    handled = false;
                } else {
                    if (!event.isAltPressed()) {
                        while (count > 0) {
                            handled = arrowScroll(FOCUS_UP);
                            count--;
                        }
                    } else {
                        handled = fullScroll(FOCUS_UP);
                    }
                }
            //Common control*
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
            //Common control*
                if(isHorizontalStyle()) {
                    // For horizontal style, we should not handle left and right key event.
                    handled = false;
                } else {
                    if (!event.isAltPressed()) {
                        while (count > 0) {
                            handled = arrowScroll(FOCUS_DOWN);
                            count--;
                        }
                        int lastPosition = mFirstPosition + getChildCount() - 1;
                        if (mLastPosition != lastPosition) {
                            mLastPosition = lastPosition;
                            layoutChildren();
                        }
                    } else {
                        handled = fullScroll(FOCUS_DOWN);
                    }
                }
            //Common control*
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (isHorizontalStyle()) {
                    if (!event.isAltPressed()) {
                        while (count > 0) {
                            handled = arrowScroll(FOCUS_LEFT);
                            count--;
                        }
                    } else {
                        handled = fullScroll(FOCUS_LEFT);
                    }
                } else {
                    handled = handleHorizontalFocusWithinListItem(View.FOCUS_LEFT);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (isHorizontalStyle()) {
                    if (!event.isAltPressed()) {
                        while (count > 0) {
                            handled = arrowScroll(FOCUS_RIGHT);
                            count--;
                        }
                    } else {
                        handled = fullScroll(FOCUS_RIGHT);
                    }
                } else {
                    handled = handleHorizontalFocusWithinListItem(View.FOCUS_RIGHT);
                }

                break;
            // End: Andrew, Liu
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mItemCount > 0 && event.getRepeatCount() == 0 && !isInTouchMode()) {
                    keyPressed();
                } else if(isInTouchMode()){
                    unPressedUnSelectChildren(null);
                }
                handled = true;
                break;

            case KeyEvent.KEYCODE_SPACE:
                if (mPopup == null || !mPopup.isShowing()) {
                    if (!event.isShiftPressed()) {
                        pageScroll(FOCUS_DOWN);
                    } else {
                        pageScroll(FOCUS_UP);
                    }
                    handled = true;
                }
                break;
            }
        }

        if (!handled) {
            handled = sendToTextFilter(keyCode, count, event);
        }

        if (handled) {
            return true;
        } else {
            switch (action) {
                case KeyEvent.ACTION_DOWN:
                    return super.onKeyDown(keyCode, event);

                case KeyEvent.ACTION_UP:
                    return super.onKeyUp(keyCode, event);

                case KeyEvent.ACTION_MULTIPLE:
                    return super.onKeyMultiple(keyCode, count, event);

                default: // shouldn't happen
                    return false;
            }
        }
    }

    /**
     * Scrolls up or down by the number of items currently present on screen.
     *
     * @param direction
     *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     * @return whether selection was moved
     */
    boolean pageScroll(int direction) {
        int nextPage = -1;
        boolean down = false;

        if (direction == FOCUS_UP) {
            nextPage = Math.max(0, mSelectedPosition - getChildCount() - 1);
        } else if (direction == FOCUS_DOWN) {
            nextPage = Math.min(mItemCount - 1, mSelectedPosition + getChildCount() - 1);
            down = true;
        }

        if (nextPage >= 0) {
            int position = lookForSelectablePosition(nextPage, down);
            if (position >= 0) {
                mLayoutMode = LAYOUT_SPECIFIC;

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    mSpecificLeft = getPaddingLeft() + getHorizontalFadingEdgeLength();

                    if (down && position > mItemCount - getChildCount()) {
                        mLayoutMode = LAYOUT_FORCE_RIGHT;
                    }

                    if (!down && position < getChildCount()) {
                        mLayoutMode = LAYOUT_FORCE_LEFT;
                    }
                } else {
                    mSpecificTop = getPaddingTop() + getVerticalFadingEdgeLength();

                    if (down && position > mItemCount - getChildCount()) {
                        mLayoutMode = LAYOUT_FORCE_BOTTOM;
                    }

                    if (!down && position < getChildCount()) {
                        mLayoutMode = LAYOUT_FORCE_TOP;
                    }
                }
                // End: Andrew, Liu

                setSelectionInt(position);
                invokeOnItemScrollListener();
                invalidate();

                return true;
            }
        }

        return false;
    }

    /**
     * Go to the last or first item if possible (not worrying about panning
     * across or navigating within the internal focus of the currently selected
     * item.)
     *
     * @param direction
     *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     *
     * @return whether selection was moved
     */
    boolean fullScroll(int direction) {
        boolean moved = false;

        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            if ( direction == FOCUS_LEFT ) {
                if ( mSelectedPosition != 0 ) {
                    int position = lookForSelectablePosition(0, true);
                    if ( position >= 0 ) {
                        mLayoutMode = LAYOUT_FORCE_LEFT;
                        setSelectionInt(position);
                        invokeOnItemScrollListener();
                    }
                    moved = true;
                }
            } else if ( direction == FOCUS_RIGHT ) {
                if ( mSelectedPosition < mItemCount - 1 ) {
                    int position = lookForSelectablePosition(mItemCount - 1,
                        true);
                    if ( position >= 0 ) {
                        mLayoutMode = LAYOUT_FORCE_RIGHT;
                        setSelectionInt(position);
                        invokeOnItemScrollListener();
                    }
                    moved = true;
                }
            }
        } else {
            if ( direction == FOCUS_UP ) {
                if ( mSelectedPosition != 0 ) {
                    int position = lookForSelectablePosition(0, true);
                    if ( position >= 0 ) {
                        mLayoutMode = LAYOUT_FORCE_TOP;
                        setSelectionInt(position);
                        invokeOnItemScrollListener();
                    }
                    moved = true;
                }
            } else if ( direction == FOCUS_DOWN ) {
                if ( mSelectedPosition < mItemCount - 1 ) {
                    int position = lookForSelectablePosition(mItemCount - 1,
                        true);
                    if ( position >= 0 ) {
                        mLayoutMode = LAYOUT_FORCE_BOTTOM;
                        setSelectionInt(position);
                        invokeOnItemScrollListener();
                    }
                    moved = true;
                }
            }
        }
        // End: Andrew, Liu

        if ( moved ) {
            invalidate();
        }

        return moved;
    }

    /**
     * To avoid horizontal focus searches changing the selected item, we
     * manually focus search within the selected item (as applicable), and
     * prevent focus from jumping to something within another item.
     *
     * @param direction
     *            one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}
     * @return Whether this consumes the key event.
     */
    private boolean handleHorizontalFocusWithinListItem(int direction) {
        if (direction != View.FOCUS_LEFT && direction != View.FOCUS_RIGHT)  {
            throw new IllegalArgumentException("direction must be one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}");
        }

        final int numChildren = getChildCount();
        if (mItemsCanFocus && numChildren > 0 && mSelectedPosition != INVALID_POSITION) {
            final View selectedView = getSelectedView();
            if (selectedView instanceof ViewGroup && selectedView.hasFocus()) {
                final View currentFocus = selectedView.findFocus();
                final View nextFocus = FocusFinder.getInstance().findNextFocus(
                        (ViewGroup) selectedView,
                        currentFocus,
                        direction);
                if (nextFocus != null) {
                    // do the math to get interesting rect in next focus' coordinates
                    currentFocus.getFocusedRect(mTempRect);
                    offsetDescendantRectToMyCoords(currentFocus, mTempRect);
                    offsetRectIntoDescendantCoords(nextFocus, mTempRect);
                    if (nextFocus.requestFocus(direction, mTempRect)) {
                        return true;
                    }
                }
                // we are blocking the key from being handled (by returning true)
                // if the global result is going to be some other view within this
                // list.  this is to acheive the overall goal of having
                // horizontal d-pad navigation remain in the current item.
                final View globalNextFocus = FocusFinder.getInstance()
                        .findNextFocus(
                                (ViewGroup) getRootView(),
                                currentFocus,
                                direction);
                if (globalNextFocus != null) {
                    return isViewAncestorOf(globalNextFocus, this);
                }
            }
        }
        return false;
    }

    // Start: Andrew, Liu
    /**
     * To avoid vertical focus searches changing the selected item, we manually
     * focus search within the selected item (as applicable), and prevent focus
     * from jumping to something within another item.
     *
     * @param direction
     *            one of {View.FOCUS_UP, View.FOCUS_DOWN}
     * @return Whether this consumes the key event.
     */
    private boolean handleVerticalFocusWithinListItem(int direction) {
        if (direction != View.FOCUS_UP && direction != View.FOCUS_DOWN)  {
            throw new IllegalArgumentException("direction must be one of {View.FOCUS_UP, View.FOCUS_DOWN}");
        }

        final int numChildren = getChildCount();
        if (mItemsCanFocus && numChildren > 0 && mSelectedPosition != INVALID_POSITION) {
            final View selectedView = getSelectedView();
            if (selectedView instanceof ViewGroup && selectedView.hasFocus()) {
                final View currentFocus = selectedView.findFocus();
                final View nextFocus = FocusFinder.getInstance().findNextFocus(
                        (ViewGroup) selectedView,
                        currentFocus,
                        direction);
                if (nextFocus != null) {
                    // do the math to get interesting rect in next focus' coordinates
                    currentFocus.getFocusedRect(mTempRect);
                    offsetDescendantRectToMyCoords(currentFocus, mTempRect);
                    offsetRectIntoDescendantCoords(nextFocus, mTempRect);
                    if (nextFocus.requestFocus(direction, mTempRect)) {
                        return true;
                    }
                }
                // we are blocking the key from being handled (by returning true)
                // if the global result is going to be some other view within this
                // list.  this is to acheive the overall goal of having
                // horizontal d-pad navigation remain in the current item.
                final View globalNextFocus = FocusFinder.getInstance()
                        .findNextFocus(
                                (ViewGroup) getRootView(),
                                currentFocus,
                                direction);
                if (globalNextFocus != null) {
                    return isViewAncestorOf(globalNextFocus, this);
                }
            }
        }
        return false;
    }
    // End: Andrew, Liu

    /**
     * Scrolls to the next or previous item if possible.
     *
     * @param direction
     *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     *
     * @return whether selection was moved
     */
    boolean arrowScroll(int direction) {
        try {
            mInLayout = true;
            final boolean handled = arrowScrollImpl(direction);
            if ( handled ) {
                playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            }
            return handled;
        } finally {
            mInLayout = false;
        }
    }

    /**
     * Handle an arrow scroll going up or down. Take into account whether items
     * are selectable, whether there are focusable items etc.
     *
     * @param direction
     *            Either {@link android.view.View#FOCUS_UP} or
     *            {@link android.view.View#FOCUS_DOWN}.
     * @return Whether any scrolling, selection or focus change occured.
     */
    private boolean arrowScrollImpl(int direction) {
        if (getChildCount() <= 0) {
            return false;
        }

        View selectedView = getSelectedView();

        int nextSelectedPosition = lookForSelectablePositionOnScreen(direction);
        int amountToScroll = amountToScroll(direction, nextSelectedPosition);

        // if we are moving focus, we may OVERRIDE the default behavior
        final ArrowScrollFocusResult focusResult = mItemsCanFocus ? arrowScrollFocused(direction) : null;
        if (focusResult != null) {
            nextSelectedPosition = focusResult.getSelectedPosition();
            amountToScroll = focusResult.getAmountToScroll();
        }

        boolean needToRedraw = focusResult != null;
        if (nextSelectedPosition != INVALID_POSITION) {
            handleNewSelectionChange(selectedView, direction, nextSelectedPosition, focusResult != null);
            setSelectedPositionInt(nextSelectedPosition);
            setNextSelectedPositionInt(nextSelectedPosition);
            selectedView = getSelectedView();
            if (mItemsCanFocus && focusResult == null) {
                // there was no new view found to take focus, make sure we
                // don't leave focus with the old selection
                final View focused = getFocusedChild();
                if (focused != null) {
                    focused.clearFocus();
                }
            }
            needToRedraw = true;
            checkSelectionChanged();
        }

        if (amountToScroll > 0) {
            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                scrollListItemsBy((direction == View.FOCUS_LEFT) ? amountToScroll : -amountToScroll);
            } else {
                scrollListItemsBy((direction == View.FOCUS_UP) ? amountToScroll : -amountToScroll);
            }
            // End: Andrew, Liu
            needToRedraw = true;
        }

        // if we didn't find a new focusable, make sure any existing focused
        // item that was panned off screen gives up focus.
        if (mItemsCanFocus && (focusResult == null)
                && selectedView != null && selectedView.hasFocus()) {
            final View focused = selectedView.findFocus();
            if (distanceToView(focused) > 0) {
                focused.clearFocus();
            }
        }

        // if  the current selection is panned off, we need to remove the selection
        if (nextSelectedPosition == INVALID_POSITION && selectedView != null
                && !isViewAncestorOf(selectedView, this)) {
            selectedView = null;
            hideSelector();

            // but we don't want to set the ressurect position (that would make subsequent
            // unhandled key events bring back the item we just scrolled off!)
            mResurrectToPosition = INVALID_POSITION;
        }

        if (needToRedraw) {
            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                if (selectedView != null) {
                    positionSelector(selectedView);
                    mSelectedLeft = selectedView.getLeft();
                }
            } else {
                if (selectedView != null) {
                    positionSelector(selectedView);
                    mSelectedTop = selectedView.getTop();
                }
            }
            // End: Andrew, Liu

            invalidate();
            invokeOnItemScrollListener();
            return true;
        }

        return false;
    }

    /**
     * When selection changes, it is possible that the previously selected or
     * the next selected item will change its size. If so, we need to offset
     * some folks, and re-layout the items as appropriate.
     *
     * @param selectedView
     *            The currently selected view (before changing selection).
     *            should be <code>null</code> if there was no previous
     *            selection.
     * @param direction
     *            Either {@link android.view.View#FOCUS_UP} or
     *            {@link android.view.View#FOCUS_DOWN}.
     * @param newSelectedPosition
     *            The position of the next selection.
     * @param newFocusAssigned
     *            whether new focus was assigned. This matters because when
     *            something has focus, we don't want to show selection (ugh).
     */
    private void handleNewSelectionChange(View selectedView, int direction, int newSelectedPosition,
            boolean newFocusAssigned) {
        if (newSelectedPosition == INVALID_POSITION) {
            throw new IllegalArgumentException("newSelectedPosition needs to be valid");
        }

        // whether or not we are moving down or up, we want to preserve the
        // top of whatever view is on top:
        // - moving down: the view that had selection
        // - moving up: the view that is getting selection
        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            View leftView;
            View rightView;
            int leftViewIndex, rightViewIndex;
            boolean leftSelected = false;
            final int selectedIndex = mSelectedPosition - mFirstPosition;
            final int nextSelectedIndex = newSelectedPosition - mFirstPosition;
            if (direction == View.FOCUS_LEFT) {
                leftViewIndex = nextSelectedIndex;
                rightViewIndex = selectedIndex;
                leftView = getChildAt(leftViewIndex);
                rightView = selectedView;
                leftSelected = true;
            } else {
                leftViewIndex = selectedIndex;
                rightViewIndex = nextSelectedIndex;
                leftView = selectedView;
                rightView = getChildAt(rightViewIndex);
            }

            final int numChildren = getChildCount();

            // start with top view: is it changing size?
            if (leftView != null) {
                leftView.setSelected(!newFocusAssigned && leftSelected);
                measureAndAdjustDown(leftView, rightViewIndex, numChildren);
            }

            // is the bottom view changing size?
            if (rightView != null) {
                rightView.setSelected(!newFocusAssigned && !leftSelected);
                measureAndAdjustDown(rightView, rightViewIndex, numChildren);
            }
        } else {
            View topView;
            View bottomView;
            int topViewIndex, bottomViewIndex;
            boolean topSelected = false;
            final int selectedIndex = mSelectedPosition - mFirstPosition;
            final int nextSelectedIndex = newSelectedPosition - mFirstPosition;
            if (direction == View.FOCUS_UP) {
                topViewIndex = nextSelectedIndex;
                bottomViewIndex = selectedIndex;
                topView = getChildAt(topViewIndex);
                bottomView = selectedView;
                topSelected = true;
            } else {
                topViewIndex = selectedIndex;
                bottomViewIndex = nextSelectedIndex;
                topView = selectedView;
                bottomView = getChildAt(bottomViewIndex);
            }

            final int numChildren = getChildCount();

            // start with top view: is it changing size?
            if (topView != null) {
                topView.setSelected(!newFocusAssigned && topSelected);
                measureAndAdjustDown(topView, topViewIndex, numChildren);
            }

            // is the bottom view changing size?
            if (bottomView != null) {
                bottomView.setSelected(!newFocusAssigned && !topSelected);
                measureAndAdjustDown(bottomView, bottomViewIndex, numChildren);
            }
        }
        // End: Andrew, Liu
    }

    /**
     * Re-measure a child, and if its height changes, lay it out preserving its
     * top, and adjust the children below it appropriately.
     *
     * @param child
     *            The child
     * @param childIndex
     *            The view group index of the child.
     * @param numChildren
     *            The number of children in the view group.
     */
    private void measureAndAdjustDown(View child, int childIndex, int numChildren) {
        // Start: Andrew, Liu
        int oldWidth = child.getWidth();
        measureItem(child);
        if (child.getMeasuredWidth() != oldWidth) {
            // lay out the view, preserving its top
            relayoutMeasuredItem(child);

            // adjust views below appropriately
            final int widthDelta = child.getMeasuredWidth() - oldWidth;
            for (int i = childIndex + 1; i < numChildren; i++) {
                getChildAt(i).offsetLeftAndRight(widthDelta);
            }
        }
        // End: Andrew, Liu
    }

    /**
     * Measure a particular list child. TODO: unify with setUpChild.
     *
     * @param child
     *            The child.
     */
    private void measureItem(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        // Start: Andrew, Liu
        int childHeightSpec;
        int childWidthSpec;

        if ( isHorizontalStyle() ) {
            if ( p == null ) {
                p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            }

            childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec,
                mListPadding.top + mListPadding.bottom, p.height);
            int lpWidth = p.width;
            if ( lpWidth > 0 ) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth,
                    MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
            }
        } else {
            if ( p == null ) {
                p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
                mListPadding.left + mListPadding.right, p.width);
            int lpHeight = p.height;
            if ( lpHeight > 0 ) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
            }
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Layout a child that has been measured, preserving its top position. TODO:
     * unify with setUpChild.
     *
     * @param child
     *            The child.
     */
    private void relayoutMeasuredItem(View child) {
        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();
        final int childLeft = mListPadding.left;
        final int childRight = childLeft + w;
        final int childTop = child.getTop();
        final int childBottom = childTop + h;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    /**
     * @return The amount to preview next items when arrow srolling.
     */
    private int getArrowScrollPreviewLength() {
        // Start: Andrew, Liu
        int val = 0;
        if ( isHorizontalStyle() ) {
            val = Math.max(MIN_SCROLL_PREVIEW_PIXELS,
                getHorizontalFadingEdgeLength());
        } else {
            val = Math.max(MIN_SCROLL_PREVIEW_PIXELS,
                getVerticalFadingEdgeLength());
        }

        return val;
        //return Math.max(MIN_SCROLL_PREVIEW_PIXELS, getVerticalFadingEdgeLength());
        // End: Andrew, Liu
    }

    /**
     * Determine how much we need to scroll in order to get the next selected
     * view visible, with a fading edge showing below as applicable. The amount
     * is capped at {@link #getMaxScrollAmount()} .
     *
     * @param direction
     *            either {@link android.view.View#FOCUS_UP} or
     *            {@link android.view.View#FOCUS_DOWN}.
     * @param nextSelectedPosition
     *            The position of the next selection, or
     *            {@link #INVALID_POSITION} if there is no next selectable
     *            position
     * @return The amount to scroll. Note: this is always positive! Direction
     *         needs to be taken into account when actually scrolling.
     */
    private int amountToScroll(int direction, int nextSelectedPosition) {

        final int numChildren = getChildCount();

        // Start: Andrew, Liu
        int amountToScroll = 0;

        if (isHorizontalStyle() && direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
            final int listRight = getWidth() - mListPadding.right;
            final int listLeft = mListPadding.left;
            if (direction == View.FOCUS_RIGHT) {
                int indexToMakeVisible = numChildren - 1;
                if (nextSelectedPosition != INVALID_POSITION) {
                    indexToMakeVisible = nextSelectedPosition - mFirstPosition;
                }

                final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
                final View viewToMakeVisible = getChildAt(indexToMakeVisible);

                int goalRight = listRight;
                if (positionToMakeVisible < mItemCount - 1) {
                    goalRight -= getArrowScrollPreviewLength();
                }
                else if (viewToMakeVisible.getRight() <= goalRight) {
                    // item is fully visible.
                    return 0;
                }

                if (nextSelectedPosition != INVALID_POSITION
                        && (goalRight - viewToMakeVisible.getLeft()) >= getMaxScrollAmount()) {
                    // item already has enough of it visible, changing selection is good enough
                    return 0;
                }

                if (viewToMakeVisible.getRight() == goalRight) {
                    amountToScroll = getArrowScrollPreviewLength();
                }
                else
                    amountToScroll = (viewToMakeVisible.getRight() - goalRight);

                if ((mFirstPosition + numChildren) == mItemCount) {
                    // last is last in list -> make sure we don't scroll past it
                    final int max = getChildAt(numChildren - 1).getRight() - listRight;
                    amountToScroll = Math.min(amountToScroll, max);
                }

                //return Math.min(amountToScroll, getMaxScrollAmount());
            } else {
                int indexToMakeVisible = 0;
                if (nextSelectedPosition != INVALID_POSITION) {
                    indexToMakeVisible = nextSelectedPosition - mFirstPosition;
                }
                final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
                final View viewToMakeVisible = getChildAt(indexToMakeVisible);
                int goalLeft = listLeft;
                if (positionToMakeVisible > 0) {
                    goalLeft += getArrowScrollPreviewLength();
                }
                else if (viewToMakeVisible.getLeft() >= goalLeft) {
                    // item is fully visible.
                    return 0;
                }

                if (nextSelectedPosition != INVALID_POSITION &&
                        (viewToMakeVisible.getRight() - goalLeft) >= getMaxScrollAmount()) {
                    // item already has enough of it visible, changing selection is good enough
                    return 0;
                }

                if (viewToMakeVisible.getLeft() == goalLeft) {
                    amountToScroll = getArrowScrollPreviewLength();
                }
                else
                    amountToScroll = (goalLeft - viewToMakeVisible.getLeft());

                if (mFirstPosition == 0) {
                    // first is first in list -> make sure we don't scroll past it
                    final int max = listLeft - getChildAt(0).getLeft();
                    amountToScroll = Math.min(amountToScroll,  max);
                }
                //return Math.min(amountToScroll, getMaxScrollAmount());
            }
        } else {
            final int listBottom = getHeight() - mListPadding.bottom;
            final int listTop = mListPadding.top;
            if (direction == View.FOCUS_UP || direction == View.FOCUS_DOWN) {
                if (direction == View.FOCUS_DOWN) {
                    int indexToMakeVisible = numChildren - 1;
                    if (nextSelectedPosition != INVALID_POSITION) {
                        indexToMakeVisible = nextSelectedPosition - mFirstPosition;
                    }

                    final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
                    final View viewToMakeVisible = getChildAt(indexToMakeVisible);

                    int goalBottom = listBottom;

                    if (positionToMakeVisible < mItemCount - 1) {
                        goalBottom -= getArrowScrollPreviewLength();
                    }

                    else if (viewToMakeVisible.getBottom() <= goalBottom) {
                        // item is fully visible.
                        return 0;
                    }

                    if (nextSelectedPosition != INVALID_POSITION
                            && (goalBottom - viewToMakeVisible.getTop()) >= getMaxScrollAmount()) {
                        // item already has enough of it visible, changing selection is good enough
                        return 0;
                    }

                    if (viewToMakeVisible.getBottom() == goalBottom) {
                        amountToScroll = getArrowScrollPreviewLength();
                    } else
                        amountToScroll = (viewToMakeVisible.getBottom() - goalBottom);

                    if ((mFirstPosition + numChildren) == mItemCount) {
                        // last is last in list -> make sure we don't scroll past it
                        final int max = getChildAt(numChildren - 1).getBottom() - listBottom;
                        amountToScroll = Math.min(amountToScroll, max);
                    }

                    //return Math.min(amountToScroll, getMaxScrollAmount());
                } else {
                    int indexToMakeVisible = 0;
                    if (nextSelectedPosition != INVALID_POSITION) {
                        indexToMakeVisible = nextSelectedPosition - mFirstPosition;
                    }
                    final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
                    final View viewToMakeVisible = getChildAt(indexToMakeVisible);
                    int goalTop = listTop;
                    if (positionToMakeVisible > 0) {
                        goalTop += getArrowScrollPreviewLength();
                    }
                    else if (viewToMakeVisible.getTop() >= goalTop) {
                        // item is fully visible.
                        return 0;
                    }

                    if (nextSelectedPosition != INVALID_POSITION &&
                            (viewToMakeVisible.getBottom() - goalTop) >= getMaxScrollAmount()) {
                        // item already has enough of it visible, changing selection is good enough
                        return 0;
                    }

                    if (viewToMakeVisible.getTop() == goalTop) {
                        amountToScroll = getArrowScrollPreviewLength();
                    } else
                        amountToScroll = (goalTop - viewToMakeVisible.getTop());

                    if (mFirstPosition == 0) {
                        // first is first in list -> make sure we don't scroll past it
                        final int max = listTop - getChildAt(0).getTop();
                        amountToScroll = Math.min(amountToScroll,  max);
                    }
                }
            }
        }
        return Math.min(amountToScroll, getMaxScrollAmount());
        // End: Andrew, Liu
    }

    /**
     * Holds results of focus aware arrow scrolling.
     */
    static private class ArrowScrollFocusResult {
        private int mSelectedPosition;
        private int mAmountToScroll;

        /**
         * How {@link android.widget.ListView#arrowScrollFocused} returns its
         * values.
         */
        void populate(int selectedPosition, int amountToScroll) {
            mSelectedPosition = selectedPosition;
            mAmountToScroll = amountToScroll;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getSelectedPosition() {
            return mSelectedPosition;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getAmountToScroll() {
            return mAmountToScroll;
        }
    }

    /**
     * @param direction
     *            either {@link android.view.View#FOCUS_UP} or
     *            {@link android.view.View#FOCUS_DOWN}.
     * @return The position of the next selectable position of the views that
     *         are currently visible, taking into account the fact that there
     *         might be no selection. Returns {@link #INVALID_POSITION} if there
     *         is no selectable view on screen in the given direction.
     */
    private int lookForSelectablePositionOnScreen(int direction) {
        final int firstPosition = mFirstPosition;
        if ( direction == View.FOCUS_DOWN || direction == View.FOCUS_RIGHT ) {
            int startPos = ( mSelectedPosition != INVALID_POSITION )
                ? mSelectedPosition + 1 : firstPosition;
            if ( startPos >= mAdapter.getCount() ) {
                return INVALID_POSITION;
            }
            if ( startPos < firstPosition ) {
                startPos = firstPosition;
            }

            final int lastVisiblePos = getLastVisiblePosition();
            final ListAdapter adapter = getAdapter();
            for ( int pos = startPos; pos <= lastVisiblePos; pos++ ) {
                if ( adapter.isEnabled(pos)
                    && getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE ) {
                    return pos;
                }
            }
        } else {
            int last = firstPosition + getChildCount() - 1;
            int startPos = ( mSelectedPosition != INVALID_POSITION )
                ? mSelectedPosition - 1 : firstPosition + getChildCount() - 1;
            if ( startPos < 0 ) {
                return INVALID_POSITION;
            }
            if ( startPos > last ) {
                startPos = last;
            }

            final ListAdapter adapter = getAdapter();
            for ( int pos = startPos; pos >= firstPosition; pos-- ) {
                if ( adapter.isEnabled(pos)
                    && getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE ) {
                    return pos;
                }
            }
        }
        return INVALID_POSITION;
    }

    /**
     * Do an arrow scroll based on focus searching. If a new view is given
     * focus, return the selection delta and amount to scroll via an
     * {@link ArrowScrollFocusResult}, otherwise, return null.
     *
     * @param direction
     *            either {@link android.view.View#FOCUS_UP} or
     *            {@link android.view.View#FOCUS_DOWN}.
     * @return The result if focus has changed, or <code>null</code>.
     */
    private ArrowScrollFocusResult arrowScrollFocused(final int direction) {
        final View selectedView = getSelectedView();
        View newFocus;
        if (selectedView != null && selectedView.hasFocus()) {
            View oldFocus = selectedView.findFocus();
            newFocus = FocusFinder.getInstance().findNextFocus(this, oldFocus, direction);
        } else {
            if (direction == View.FOCUS_DOWN) {
                final boolean topFadingEdgeShowing = (mFirstPosition > 0);
                final int listTop = mListPadding.top +
                        (topFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                final int ySearchPoint =
                        (selectedView != null && selectedView.getTop() > listTop) ?
                                selectedView.getTop() :
                                listTop;
                mTempRect.set(0, ySearchPoint, 0, ySearchPoint);
            } else if (direction == View.FOCUS_RIGHT) { // Start: Andrew, Liu
                final boolean leftFadingEdgeShowing = (mFirstPosition > 0);
                final int listLeft = mListPadding.left +
                        (leftFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                final int xSearchPoint =
                        (selectedView != null && selectedView.getLeft() > listLeft) ?
                                selectedView.getLeft() :
                                listLeft;
                mTempRect.set(xSearchPoint, 0, xSearchPoint, 0);
            } else if (direction == View.FOCUS_LEFT) {
                final boolean rightFadingEdgeShowing =
                        (mFirstPosition + getChildCount() - 1) < mItemCount;
                final int listRight = getWidth() - mListPadding.right -
                        (rightFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                final int xSearchPoint =
                        (selectedView != null && selectedView.getRight() < listRight) ?
                                selectedView.getRight() :
                                listRight;
                mTempRect.set(xSearchPoint, 0, xSearchPoint, 0);    // End: Andrew, Liu
            } else {
                final boolean bottomFadingEdgeShowing =
                        (mFirstPosition + getChildCount() - 1) < mItemCount;
                final int listBottom = getHeight() - mListPadding.bottom -
                        (bottomFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                final int ySearchPoint =
                        (selectedView != null && selectedView.getBottom() < listBottom) ?
                                selectedView.getBottom() :
                                listBottom;
                mTempRect.set(0, ySearchPoint, 0, ySearchPoint);
            }
            newFocus = FocusFinder.getInstance().findNextFocusFromRect(this, mTempRect, direction);
        }

        if (newFocus != null) {
            final int positionOfNewFocus = positionOfNewFocus(newFocus);

            // if the focus change is in a different new position, make sure
            // we aren't jumping over another selectable position
            if (mSelectedPosition != INVALID_POSITION && positionOfNewFocus != mSelectedPosition) {
                final int selectablePosition = lookForSelectablePositionOnScreen(direction);
                if (selectablePosition != INVALID_POSITION &&
                        (((direction == View.FOCUS_DOWN || direction == View.FOCUS_RIGHT) && selectablePosition < positionOfNewFocus) ||
                        ((direction == View.FOCUS_UP || direction == View.FOCUS_LEFT) && selectablePosition > positionOfNewFocus))) {
                    return null;
                }
            }

            int focusScroll = amountToScrollToNewFocus(direction, newFocus, positionOfNewFocus);

            final int maxScrollAmount = getMaxScrollAmount();
            if (focusScroll < maxScrollAmount) {
                // not moving too far, safe to give next view focus
                newFocus.requestFocus(direction);
                mArrowScrollFocusResult.populate(positionOfNewFocus, focusScroll);
                return mArrowScrollFocusResult;
            } else if (distanceToView(newFocus) < maxScrollAmount){
                // Case to consider:
                // too far to get entire next focusable on screen, but by going
                // max scroll amount, we are getting it at least partially in view,
                // so give it focus and scroll the max ammount.
                newFocus.requestFocus(direction);
                mArrowScrollFocusResult.populate(positionOfNewFocus, maxScrollAmount);
                return mArrowScrollFocusResult;
            }
        }
        return null;
    }

    /**
     * @param newFocus
     *            The view that would have focus.
     * @return the position that contains newFocus
     */
    private int positionOfNewFocus(View newFocus) {
        final int numChildren = getChildCount();
        for ( int i = 0; i < numChildren; i++ ) {
            final View child = getChildAt(i);
            if ( isViewAncestorOf(newFocus, child) ) {
                return mFirstPosition + i;
            }
        }
        throw new IllegalArgumentException(
            "newFocus is not a child of any of the" + " children of the list!");
    }

    /**
     * Return true if child is an ancestor of parent, (or equal to the parent).
     */
    private boolean isViewAncestorOf(View child, View parent) {
        if ( child == parent ) {
            return true;
        }

        final ViewParent theParent = child.getParent();
        return ( theParent instanceof ViewGroup )
            && isViewAncestorOf((View) theParent, parent);
    }

    /**
     * Determine how much we need to scroll in order to get newFocus in view.
     *
     * @param direction
     *            either {@link android.view.View#FOCUS_UP} or
     *            {@link android.view.View#FOCUS_DOWN}.
     * @param newFocus
     *            The view that would take focus.
     * @param positionOfNewFocus
     *            The position of the list item containing newFocus
     * @return The amount to scroll. Note: this is always positive! Direction
     *         needs to be taken into account when actually scrolling.
     */
    private int amountToScrollToNewFocus(int direction, View newFocus, int positionOfNewFocus) {
        int amountToScroll = 0;
        newFocus.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(newFocus, mTempRect);
        if (direction == View.FOCUS_UP) {
            if (mTempRect.top < mListPadding.top) {
                amountToScroll = mListPadding.top - mTempRect.top;
                if (positionOfNewFocus > 0) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        } else if (direction == View.FOCUS_LEFT) {    // Start: Andrew, Liu
            if (mTempRect.left < mListPadding.left) {
                amountToScroll = mListPadding.left - mTempRect.left;
                if (positionOfNewFocus > 0) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        } else if (direction == View.FOCUS_RIGHT) {
            final int listRight = getWidth() - mListPadding.right;
            if (mTempRect.right > listRight) {
                amountToScroll = mTempRect.right - listRight;
                if (positionOfNewFocus < mItemCount - 1) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }    // End: Andrew, Liu
        } else{
            final int listBottom = getHeight() - mListPadding.bottom;
            if (mTempRect.bottom > listBottom) {
                amountToScroll = mTempRect.bottom - listBottom;
                if (positionOfNewFocus < mItemCount - 1) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        }
        return amountToScroll;
    }

    /**
     * Determine the distance to the nearest edge of a view in a particular
     * direciton.
     *
     * @param descendant
     *            A descendant of this list.
     * @return The distance, or 0 if the nearest edge is already on screen.
     */
    private int distanceToView(View descendant) {
        int distance = 0;
        descendant.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(descendant, mTempRect);

        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            final int listRight = getRight() - getLeft() - mListPadding.right;
            if ( mTempRect.right < mListPadding.left ) {
                distance = mListPadding.left - mTempRect.right;
            } else if ( mTempRect.left > listRight ) {
                distance = mTempRect.left - listRight;
            }
        } else {
            final int listBottom = getBottom() - getTop() - mListPadding.bottom;
            if ( mTempRect.bottom < mListPadding.top ) {
                distance = mListPadding.top - mTempRect.bottom;
            } else if ( mTempRect.top > listBottom ) {
                distance = mTempRect.top - listBottom;
            }
        }
        // End: Andrew, Liu
        return distance;
    }

    /**
     * Scroll the children by amount, adding a view at the end and removing
     * views that fall off as necessary.
     *
     * @param amount
     *            The amount (positive or negative) to scroll.
     */
    private void scrollListItemsBy(int amount) {

        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            offsetChildrenLeftAndRight(amount);

            final int listRight = getWidth() - mListPadding.right;
            final int listLeft = mListPadding.left;
            final AbsCrabWalkView.RecycleBin recycleBin = mRecycler;

            if (amount < 0) {
                // shifted items up

                // may need to pan views into the bottom space
                int numChildren = getChildCount();
                View last = getChildAt(numChildren - 1);
                while (last.getRight() < listRight) {
                    final int lastVisiblePosition = mFirstPosition + numChildren - 1;
                    if (lastVisiblePosition < mItemCount - 1) {
                        last = addViewBelow(last, lastVisiblePosition);
                        numChildren++;
                    } else {
                        break;
                    }
                }

                // may have brought in the last child of the list that is skinnier
                // than the fading edge, thereby leaving space at the end.  need
                // to shift back
                if (last.getRight() < listRight) {
                    offsetChildrenLeftAndRight(listRight - last.getRight());
                }

                // top views may be panned off screen
                View first = getChildAt(0);
                while (first.getRight() < listLeft) {
                    AbsCrabWalkView.LayoutParams layoutParams = (LayoutParams) first.getLayoutParams();
                    if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                        removeViewInLayout(first);
                        recycleBin.addScrapView(first);
                    } else {
                        detachViewFromParent(first);
                    }
                    first = getChildAt(0);
                    mFirstPosition++;
                }
            } else {
                // shifted items down
                View first = getChildAt(0);

                // may need to pan views into top
                while ((first.getLeft() > listLeft) && (mFirstPosition > 0)) {
                    first = addViewAbove(first, mFirstPosition);
                    mFirstPosition--;
                }

                // may have brought the very first child of the list in too far and
                // need to shift it back
                if (first.getLeft() > listLeft) {
                    offsetChildrenLeftAndRight(listLeft - first.getLeft());
                }

                int lastIndex = getChildCount() - 1;
                View last = getChildAt(lastIndex);

                // bottom view may be panned off screen
                while (last.getLeft() > listRight) {
                    AbsCrabWalkView.LayoutParams layoutParams = (LayoutParams) last.getLayoutParams();
                    if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                        removeViewInLayout(last);
                        recycleBin.addScrapView(last);
                    } else {
                        detachViewFromParent(last);
                    }
                    last = getChildAt(--lastIndex);
                }
            }
        } else {
            //offsetChildrenTopAndBottom(amount);

            final int listBottom = getHeight() - mListPadding.bottom;
            final int listTop = mListPadding.top;
            final AbsCrabWalkView.RecycleBin recycleBin = mRecycler;

            if (amount < 0) {
                // shifted items up

                // may need to pan views into the bottom space
                int numChildren = getChildCount();
                View last = getChildAt(numChildren - 1);
                while (last.getBottom() < listBottom) {
                    final int lastVisiblePosition = mFirstPosition + numChildren - 1;
                    if (lastVisiblePosition < mItemCount - 1) {
                        last = addViewBelow(last, lastVisiblePosition);
                        numChildren++;
                    } else {
                        break;
                    }
                }

                // may have brought in the last child of the list that is skinnier
                // than the fading edge, thereby leaving space at the end.  need
                // to shift back
                if (last.getBottom() < listBottom) {
                    //offsetChildrenTopAndBottom(listBottom - last.getBottom());
                }

                // top views may be panned off screen
                View first = getChildAt(0);
                while (first.getBottom() < listTop) {
                    AbsCrabWalkView.LayoutParams layoutParams = (LayoutParams) first.getLayoutParams();
                    if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                        removeViewInLayout(first);
                        recycleBin.addScrapView(first);
                    } else {
                        detachViewFromParent(first);
                    }
                    first = getChildAt(0);
                    mFirstPosition++;
                }
            } else {
                // shifted items down
                View first = getChildAt(0);

                // may need to pan views into top
                while ((first.getTop() > listTop) && (mFirstPosition > 0)) {
                    first = addViewAbove(first, mFirstPosition);
                    mFirstPosition--;
                }

                // may have brought the very first child of the list in too far and
                // need to shift it back
                if (first.getTop() > listTop) {
                    //offsetChildrenTopAndBottom(listTop - first.getTop());
                }

                int lastIndex = getChildCount() - 1;
                View last = getChildAt(lastIndex);

                // bottom view may be panned off screen
                while (last.getTop() > listBottom) {
                    AbsCrabWalkView.LayoutParams layoutParams = (LayoutParams) last.getLayoutParams();
                    if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                        removeViewInLayout(last);
                        recycleBin.addScrapView(last);
                    } else {
                        detachViewFromParent(last);
                    }
                    last = getChildAt(--lastIndex);
                }
            }
/*
        if (!mSpeedUp)
            updateBackGround();
*/
        }
        // End: Andrew, Liu
    }

    private View addViewAbove(View theView, int position) {
        int abovePosition = position - 1;
        View view = obtainView(abovePosition);
        int edgeOfNewChild;
        int childTopOrLeft = 0;
        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            edgeOfNewChild = theView.getLeft() - mDividerWidth;
            childTopOrLeft = mListPadding.top;
        } else {
            edgeOfNewChild = theView.getTop() - mDividerHeight;
            childTopOrLeft = mListPadding.left;
        }
        // End: Andrew, Liu
        setupChild(view, abovePosition, edgeOfNewChild, false,
            childTopOrLeft, false, false);
        return view;
    }

    private View addViewBelow(View theView, int position) {
        int belowPosition = position + 1;
        View view = obtainView(belowPosition);
        int edgeOfNewChild;
        int childTopOrLeft = 0;
        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            edgeOfNewChild = theView.getRight() - mDividerWidth;
            childTopOrLeft = mListPadding.top;
        } else {
            edgeOfNewChild = theView.getBottom() - mDividerHeight;
            childTopOrLeft = mListPadding.left;
        }
        // End: Andrew, Liu
        setupChild(view, belowPosition, edgeOfNewChild, true,
            childTopOrLeft, false, false);
        return view;
    }

    /**
     * Indicates that the views created by the ListAdapter can contain focusable
     * items.
     *
     * @param itemsCanFocus
     *            true if items can get focus, false otherwise
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setItemsCanFocus(boolean itemsCanFocus) {
        mItemsCanFocus = itemsCanFocus;
        if ( !itemsCanFocus ) {
            setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        }
    }

    /**
     * @return Whether the views created by the ListAdapter can contain
     *         focusable items.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean getItemsCanFocus() {
        return mItemsCanFocus;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Draw the dividers
        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            final int dividerWidth = mDividerWidth;

            if (dividerWidth > 0 && mDivider != null) {
                // Only modify the top and bottom in the loop, we set the left and right here
                final Rect bounds = mTempRect;
                bounds.top = getPaddingTop();
                bounds.bottom = getBottom() - getTop() - getPaddingBottom();

                final int count = getChildCount();
                final int headerCount = mHeaderViewInfos.size();
                final int footerLimit = mItemCount - mFooterViewInfos.size() - 1;
                final boolean headerDividers = mHeaderDividersEnabled;
                final boolean footerDividers = mFooterDividersEnabled;
                final int first = mFirstPosition;

                if (!mStackFromBottom) {
                    int right;
                    int listRight = getRight() - getLeft() - mListPadding.right;

                    for (int i = 0; i < count; i++) {
                        if ((headerDividers || first + i >= headerCount) &&
                                (footerDividers || first + i < footerLimit)) {
                            View child = getChildAt(i);
                            right = child.getRight();
                            if (right < listRight) {
                                bounds.left = right;
                                bounds.right = right + dividerWidth;
    // <HTC>
                                //modified by jason liu
                                //We need a footer view without divider for dialer
                                if(mFooterViewWithoutDivider != child)
                                {
                                    drawDivider(canvas, bounds, i);
                                }
                                // drawDivider(canvas, bounds, i);
                                //end
    // </HTC>
                            }
                        }
                    }
                } else {
                    int left;
                    int listLeft = mListPadding.left;

                    for (int i = 0; i < count; i++) {
                        if ((headerDividers || first + i >= headerCount) &&
                                (footerDividers || first + i < footerLimit)) {
                            View child = getChildAt(i);
                            left = child.getLeft();
                            if (left > listLeft) {
                                bounds.left = left - dividerWidth;
                                bounds.right = left;
                                // Give the method the child ABOVE the divider, so we
                                // subtract one from our child
                                // position. Give -1 when there is no child above the
                                // divider.
    // <HTC>
                                // modified by jason liu
                                // We need a footer view without divider for dialer
                                if(mFooterViewWithoutDivider != child)
                                {
                                    drawDivider(canvas, bounds, i - 1);
                                }
                                //drawDivider(canvas, bounds, i - 1);
                                //end
    // </HTC>
                            }
                        }
                    }
                }
            }
        } else {
            final int dividerHeight = mDividerHeight;

            if (dividerHeight > 0 && mDivider != null) {
                // Only modify the top and bottom in the loop, we set the left and right here
                final Rect bounds = mTempRect;
                bounds.left = getPaddingLeft();
                bounds.right = getRight() - getLeft() - getPaddingRight();

                final int count = getChildCount();
                final int headerCount = mHeaderViewInfos.size();
                final int footerLimit = mItemCount - mFooterViewInfos.size() - 1;
                final boolean headerDividers = mHeaderDividersEnabled;
                final boolean footerDividers = mFooterDividersEnabled;
                final int first = mFirstPosition;

                if (!mStackFromBottom) {
                    int bottom;
                    int listBottom = getBottom() - getTop() - mListPadding.bottom;

                    for (int i = 0; i < count; i++) {
                        if ((headerDividers || first + i >= headerCount) &&
                                (footerDividers || first + i < footerLimit)) {
                            View child = getChildAt(i);
                            bottom = child.getBottom();
                            if (bottom < listBottom) {
                                bounds.top = bottom;
                                bounds.bottom = bottom + dividerHeight;
    // <HTC>
                                //modified by jason liu
                                //We need a footer view without divider for dialer
                                if(mFooterViewWithoutDivider != child)
                                {
                                    drawDivider(canvas, bounds, i);
                                }
                                // drawDivider(canvas, bounds, i);
                                //end
    // </HTC>
                            }
                        }
                    }
                } else {
                    int top;
                    int listTop = mListPadding.top;

                    for (int i = 0; i < count; i++) {
                        if ((headerDividers || first + i >= headerCount) &&
                                (footerDividers || first + i < footerLimit)) {
                            View child = getChildAt(i);
                            top = child.getTop();
                            if (top > listTop) {
                                bounds.top = top - dividerHeight;
                                bounds.bottom = top;
                                // Give the method the child ABOVE the divider, so we
                                // subtract one from our child
                                // position. Give -1 when there is no child above the
                                // divider.
    // <HTC>
                                // modified by jason liu
                                // We need a footer view without divider for dialer
                                if(mFooterViewWithoutDivider != child)
                                {
                                    drawDivider(canvas, bounds, i - 1);
                                }
                                //drawDivider(canvas, bounds, i - 1);
                                //end
    // </HTC>
                            }
                        }
                    }
                }
            }
        }
        // End: Andrew, Liu

        // Draw the indicators (these should be drawn above the dividers) and children
        super.dispatchDraw(canvas);
    }

    /**
     * Draws a divider for the given child in the given bounds.
     *
     * @param canvas
     *            The canvas to draw to.
     * @param bounds
     *            The bounds of the divider.
     * @param childIndex
     *            The index of child (of the View) above the divider. This will
     *            be -1 if there is no child above the divider to be drawn.
     */
    void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        // This widget draws the same divider for all children
        final Drawable seperatorDivider = mSeperatorDiver;
        final Drawable divider = mDivider;
        final boolean clipDivider = mClipDivider;

        if ( !clipDivider ) {
            divider.setBounds(bounds);
            seperatorDivider.setBounds(bounds);
        } else {
            canvas.save();
            canvas.clipRect(bounds);
        }
        if ( shouldDrawSeperatorDivider(childIndex + this.mFirstPosition) ) {
            divider.draw(canvas);
        } else {
            seperatorDivider.draw(canvas);
        }

        if ( clipDivider ) {
            canvas.restore();
        }
    }

    /**
     * Returns the drawable that will be drawn between each item in the list.
     *
     * @return the current drawable drawn between list elements
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public Drawable getDivider() {
        return mDivider;
    }

    /**
     * Sets the drawable that will be drawn between each item in the list. If
     * the drawable does not have an intrinsic height, you should also call
     * {@link #setDividerHeight(int)}
     *
     * @param divider
     *            The drawable to use.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setDivider(Drawable divider) {
        // Start: Andrew, Liu
        if ( isHorizontalStyle() ) {
            if ( divider != null ) {
                mDividerWidth = divider.getIntrinsicWidth();
                mClipDivider = divider instanceof ColorDrawable;
            } else {
                mDividerWidth = 0;
                mClipDivider = false;
            }
        } else {
            if ( divider != null ) {
                mDividerHeight = divider.getIntrinsicHeight();
                mClipDivider = divider instanceof ColorDrawable;
            } else {
                mDividerHeight = 0;
                mClipDivider = false;
            }
        }
        // End: Andrew, Liu
        mDivider = divider;
        if ( mSeperatorDiver == null )
            mSeperatorDiver = divider;
        requestLayoutIfNecessary();
    }

    /**
     * @return Returns the height of the divider that will be drawn between each
     *         item in the list.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getDividerHeight() {
        return mDividerHeight;
    }

    /**
     * Sets the height of the divider that will be drawn between each item in
     * the list. Calling this will override the intrinsic height as set by
     * {@link #setDivider(Drawable)}
     *
     * @param height
     *            The new height of the divider in pixels.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setDividerHeight(int height) {
        mDividerHeight = height;
        requestLayoutIfNecessary();
    }

    // Start: Andrew, Liu
    /**
     * @return Returns the width of the divider that will be drawn between each
     *         item in the list.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getDividerWidth() {
        return mDividerWidth;
    }

    /**
     * Sets the width of the divider that will be drawn between each item in the
     * list. Calling this will override the intrinsic width as set by
     * {@link #setDivider(Drawable)}
     *
     * @param width
     *            The new width of the divider in pixels.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setDividerWidth(int width) {
        mDividerWidth = width;
        requestLayoutIfNecessary();
    }

    // End: Andrew, Liu

    /**
     * Enables or disables the drawing of the divider for header views.
     *
     * @param headerDividersEnabled
     *            True to draw the headers, false otherwise.
     *
     * @see #setFooterDividersEnabled(boolean)
     * @see #addHeaderView(android.view.View)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        mHeaderDividersEnabled = headerDividersEnabled;
        invalidate();
    }

    /**
     * Enables or disables the drawing of the divider for footer views.
     *
     * @param footerDividersEnabled
     *            True to draw the footers, false otherwise.
     *
     * @see #setHeaderDividersEnabled(boolean)
     * @see #addFooterView(android.view.View)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
        mFooterDividersEnabled = footerDividersEnabled;
        invalidate();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        int closetChildIndex = -1;
        if (gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(getScrollX(), getScrollY());

            // figure out which item should be selected based on previously
            // focused rect
            Rect otherRect = mTempRect;
            int minDistance = Integer.MAX_VALUE;
            final int childCount = getChildCount();
            final int firstPosition = mFirstPosition;
            final ListAdapter adapter = mAdapter;

            for (int i = 0; i < childCount; i++) {
                // only consider selectable views
                if (!adapter.isEnabled(firstPosition + i)) {
                    continue;
                }

                View other = getChildAt(i);
                other.getDrawingRect(otherRect);
                offsetDescendantRectToMyCoords(other, otherRect);
                int distance = getDistance(previouslyFocusedRect, otherRect, direction);

                if (distance < minDistance) {
                    minDistance = distance;
                    closetChildIndex = i;
                }
            }
        }

        if (closetChildIndex >= 0) {
            setSelection(closetChildIndex + mFirstPosition);
        } else {
            requestLayout();
        }
    }


    /*
     * (non-Javadoc)
     *
     * Children specified in XML are assumed to be header views. After we have
     * parsed them move them out of the children list and into mHeaderViews.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int count = getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                addHeaderView(getChildAt(i));
            }
            removeAllViews();
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void releaseItem(){
    }



/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getChoiceMode() {
        return mChoiceMode;
    }

    /**
     * Defines the choice behavior for the List. By default, Lists do not have
     * any choice behavior ({@link #CHOICE_MODE_NONE}). By setting the
     * choiceMode to {@link #CHOICE_MODE_SINGLE}, the List allows up to one item
     * to be in a chosen state. By setting the choiceMode to
     * {@link #CHOICE_MODE_MULTIPLE}, the list allows any number of items to be
     * chosen.
     *
     * @param choiceMode
     *            One of {@link #CHOICE_MODE_NONE}, {@link #CHOICE_MODE_SINGLE},
     *            or {@link #CHOICE_MODE_MULTIPLE}
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setChoiceMode(int choiceMode) {
        mChoiceMode = choiceMode;
        if ( mChoiceMode != CHOICE_MODE_NONE && mCheckStates == null ) {
            mCheckStates = new SparseBooleanArray();
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean performItemClick(View view, int position, long id) {
        boolean handled = false;

        if ( mChoiceMode != CHOICE_MODE_NONE ) {
            handled = true;

            if ( mChoiceMode == CHOICE_MODE_MULTIPLE ) {
                boolean oldValue = mCheckStates.get(position, false);
                mCheckStates.put(position, !oldValue);
            } else {
                boolean oldValue = mCheckStates.get(position, false);
                if ( !oldValue ) {
                    mCheckStates.clear();
                    mCheckStates.put(position, true);
                }
            }

            mDataChanged = true;
            rememberSyncState();
            requestLayout();
        }

        handled |= super.performItemClick(view, position, id);

        return handled;
    }

    /**
     * Sets the checked state of the specified position. The is only valid if
     * the choice mode has been set to {@link #CHOICE_MODE_SINGLE} or
     * {@link #CHOICE_MODE_MULTIPLE}.
     *
     * @param position
     *            The item whose checked state is to be checked
     * @param value
     *            The new checked sate for the item
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setItemChecked(int position, boolean value) {
        if ( mChoiceMode == CHOICE_MODE_NONE ) {
            return;
        }

        if ( mChoiceMode == CHOICE_MODE_MULTIPLE ) {
            mCheckStates.put(position, value);
        } else {
            boolean oldValue = mCheckStates.get(position, false);
            mCheckStates.clear();
            if ( !oldValue ) {
                mCheckStates.put(position, true);
            }
        }

        // Do not generate a data change while we are in the layout phase
        if ( !mInLayout && !mBlockLayoutRequests ) {
            mDataChanged = true;
            rememberSyncState();
            requestLayout();
        }
    }

    /**
     * Returns the checked state of the specified position. The result is only
     * valid if the choice mode has not been set to {@link #CHOICE_MODE_SINGLE}
     * or {@link #CHOICE_MODE_MULTIPLE}.
     *
     * @param position
     *            The item whose checked state to return
     * @return The item's checked state
     *
     * @see #setChoiceMode(int)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean isItemChecked(int position) {
        if ( mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null ) {
            return mCheckStates.get(position);
        }

        return false;
    }

    /**
     * Returns the currently checked item. The result is only valid if the
     * choice mode has not been set to {@link #CHOICE_MODE_SINGLE}.
     *
     * @return The position of the currently checked item or
     *         {@link #INVALID_POSITION} if nothing is selected
     *
     * @see #setChoiceMode(int)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getCheckedItemPosition() {
        if ( mChoiceMode == CHOICE_MODE_SINGLE && mCheckStates != null
            && mCheckStates.size() == 1 ) {
            return mCheckStates.keyAt(0);
        }

        return INVALID_POSITION;
    }

    /**
     * Returns the set of checked items in the list. The result is only valid if
     * the choice mode has not been set to {@link #CHOICE_MODE_SINGLE}.
     *
     * @return A SparseBooleanArray which will return true for each call to
     *         get(int position) where position is a position in the list.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public SparseBooleanArray getCheckedItemPositions() {
        if ( mChoiceMode != CHOICE_MODE_NONE ) {
            return mCheckStates;
        }
        return null;
    }

    /**
     * Clear any choices previously set
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void clearChoices() {
        if ( mCheckStates != null ) {
            mCheckStates.clear();
        }
    }

    static class SavedState extends CommonSavedState {
        SparseBooleanArray checkState;

        /**
         * Constructor called from {@link HtcListViewCore#onSaveInstanceState()}
         */
        SavedState(Parcelable superState, SparseBooleanArray checkState) {
            super(superState);
            this.checkState = checkState;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel source) {
            super(source);
            checkState = source.readSparseBooleanArray();
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSparseBooleanArray(checkState);
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        @Override
        public String toString() {
            return "ListView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checkState=" + checkState + "}";
        }

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mCheckStates);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.checkState != null) {
           mCheckStates = ss.checkState;
        }

    }

    //Add by Jason Chiu 2009.5.28. we set seperator divider as black.
    //such that it will not show a white line.

    /**
     * The item this position should draw seperator divider?
     *
     * @param position
     *            The position of item
     * @return true if you want to draw seperator divider in the item of this
     *         position.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected boolean shouldDrawSeperatorDivider(int position) {
        return true;
    }

    /**
     * Set seperator divider drawalbe.
     *
     * @param drawable
     *            The drawable you want to use in seperator divider.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setSeperatorDiver(Drawable drawable) {
        mSeperatorDiver = drawable;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getListBottom() {
       return (getBottom() - getTop());
    }

    private int mLastPosition = -1;

    boolean isInBouncing() {
        return false;
    }
}
