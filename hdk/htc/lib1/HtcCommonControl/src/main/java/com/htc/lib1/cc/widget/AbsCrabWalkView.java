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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Debug;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.InputDevice;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import com.htc.lib1.cc.R;
/**
 * Common code shared between ListView and GridView
 *
 */
public abstract class AbsCrabWalkView extends HtcAdapterView2<ListAdapter> implements TextWatcher,
        ViewTreeObserver.OnGlobalLayoutListener, Filter.FilterListener,
        ViewTreeObserver.OnTouchModeChangeListener {

    /**
     * Disables the transcript mode.
     *
     * @see #setTranscriptMode(int)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int TRANSCRIPT_MODE_DISABLED = 0;
    /**
     * The list will automatically scroll to the bottom when a data set change
     * notification is received and only if the last item is already visible
     * on screen.
     *
     * @see #setTranscriptMode(int)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int TRANSCRIPT_MODE_NORMAL = 1;
    /**
     * The list will automatically scroll to the bottom, no matter what items
     * are currently visible.
     *
     * @see #setTranscriptMode(int)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int TRANSCRIPT_MODE_ALWAYS_SCROLL = 2;

    /**
     * Indicates that we are not in the middle of a touch gesture
     */
    static final int TOUCH_MODE_REST = -1;

    /**
     * Indicates we just received the touch event and we are waiting to see if the it is a tap or a
     * scroll gesture.
     */
    static final int TOUCH_MODE_DOWN = 0;

    /**
     * Indicates the touch has been recognized as a tap and we are now waiting to see if the touch
     * is a longpress
     */
    static final int TOUCH_MODE_TAP = 1;

    /**
     * Indicates we have waited for everything we can wait for, but the user's finger is still down
     */
    static final int TOUCH_MODE_DONE_WAITING = 2;

    /**
     * Indicates the touch gesture is a scroll
     */
    static final int TOUCH_MODE_SCROLL = 3;

    /**
     * Indicates the view is in the process of being flung
     */
    static final int TOUCH_MODE_FLING = 4;

    /**
     * Indicates that the user is currently dragging the fast scroll thumb
     */
    static final int TOUCH_MODE_FAST_SCROLL = 5;

    /**
     * Regular layout - usually an unsolicited layout from the view system
     */
    static final int LAYOUT_NORMAL = 0;

    /**
     * Show the first item
     */
    static final int LAYOUT_FORCE_TOP = 1;

    /**
     * Force the selected item to be on somewhere on the screen
     */
    static final int LAYOUT_SET_SELECTION = 2;

    /**
     * Show the last item
     */
    static final int LAYOUT_FORCE_BOTTOM = 3;

    /**
     * Make a mSelectedItem appear in a specific location and build the rest of
     * the views from there. The top is specified by mSpecificTop.
     */
    static final int LAYOUT_SPECIFIC = 4;

    /**
     * Layout to sync as a result of a data change. Restore mSyncPosition to have its top
     * at mSpecificTop
     */
    static final int LAYOUT_SYNC = 5;

    /**
     * Layout as a result of using the navigation keys
     */
    static final int LAYOUT_MOVE_SELECTION = 6;

    // Start: Andrew, Liu
    /**
     * Show the first item
     */
    static final int LAYOUT_FORCE_LEFT = 7;

    /**
     * Show the last item
     */
    static final int LAYOUT_FORCE_RIGHT = 8;
    // End: Andrew, Liu

    /**
     * Controls how the next layout will happen
     */
    @ExportedProperty(category = "CommonControl")
    int mLayoutMode = LAYOUT_NORMAL;

    /**
     * Should be used by subclasses to listen to changes in the dataset
     */
    AdapterDataSetObserver mDataSetObserver;

    /**
     * The adapter containing the data to be displayed by this view
     */
    ListAdapter mAdapter;

    /**
     * Indicates whether the list selector should be drawn on top of the children or behind
     */
    @ExportedProperty(category = "CommonControl")
    boolean mDrawSelectorOnTop = false;

    /**
     * The drawable used to draw the selector
     */
    Drawable mSelector;

    /**
     * Defines the selector's location and dimension at drawing time
     */
    Rect mSelectorRect = new Rect();

    /**
     * The data set used to store unused views that should be reused during the next layout
     * to avoid creating new ones
     */
    final RecycleBin mRecycler = new RecycleBin();

    /**
     * The selection's left padding
     */
    @ExportedProperty(category = "CommonControl")
    int mSelectionLeftPadding = 0;

    /**
     * The selection's top padding
     */
    @ExportedProperty(category = "CommonControl")
    int mSelectionTopPadding = 0;

    /**
     * The selection's right padding
     */
    @ExportedProperty(category = "CommonControl")
    int mSelectionRightPadding = 0;

    /**
     * The selection's bottom padding
     */
    @ExportedProperty(category = "CommonControl")
    int mSelectionBottomPadding = 0;

    /**
     * This view's padding
     */
    Rect mListPadding = new Rect();

    /**
     * Subclasses must retain their measure spec from onMeasure() into this member
     */
    int mWidthMeasureSpec = 0;

    // Start: Andrew, Liu
    /**
     * Subclasses must retain their measure spec from onMeasure() into this member
     */
    int mHeightMeasureSpec = 0;
    // End: Andrew, Liu

    /**
     * The top scroll indicator
     */
    View mScrollUp;

    /**
     * The down scroll indicator
     */
    View mScrollDown;

    //Start: Andrew, Liu
    /**
     * The top scroll indicator
     */
    View mScrollLeft;

    /**
     * The down scroll indicator
     */
    View mScrollRight;
    // End: Andrew, Liu

    /**
     * When the view is scrolling, this flag is set to true to indicate subclasses that
     * the drawing cache was enabled on the children
     */
    boolean mCachingStarted;

    /**
     * The position of the view that received the down motion event
     */
    int mMotionPosition;

    /**
     * The offset to the top of the mMotionPosition view when the down motion event was received
     */
    int mMotionViewOriginalTop;

    /**
     * The desired offset to the top of the mMotionPosition view after a scroll
     */
    int mMotionViewNewTop;

    // Start: Andrew, Liu
    /**
     * The offset to the left of the mMotionPosition view when the down motion event was received
     */
    int mMotionViewOriginalLeft;

    /**
     * The desired offset to the left of the mMotionPosition view after a scroll
     */
    int mMotionViewNewLeft;
    // End: Andrew, Liu

    /**
     * The X value associated with the the down motion event
     */
    int mMotionX;

    /**
     * The Y value associated with the the down motion event
     */
    int mMotionY;

    /**
     * One of TOUCH_MODE_REST, TOUCH_MODE_DOWN, TOUCH_MODE_TAP, TOUCH_MODE_SCROLL, or
     * TOUCH_MODE_DONE_WAITING
     */
    int mTouchMode = TOUCH_MODE_REST;

    /**
     * Y value from on the previous motion event (if any)
     */
    int mLastY;

    //Start: Andrew, Liu
    /**
     * X value from on the previous motion event (if any)
     */
    int mLastX;
    // End: Andrew, Liu

    /**
     * How far the finger moved before we started scrolling
     */
    int mMotionCorrection;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;

    /**
     * Handles one frame of a fling
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected FlingRunnable mFlingRunnable;

    /**
     * The offset in pixels form the top of the AdapterView to the top
     * of the currently selected view. Used to save and restore state.
     */
    int mSelectedTop = 0;

    // Start: Andrew, Liu
    /**
     * The offset in pixels form the left of the AdapterView to the left
     * of the currently selected view. Used to save and restore state.
     */
    int mSelectedLeft = 0;
    // End: Andrew, Liu

    /**
     * Indicates whether the list is stacked from the bottom edge or
     * the top edge.
     */
    @ExportedProperty(category = "CommonControl")
    boolean mStackFromBottom;

    /**
     * When set to true, the list automatically discards the children's
     * bitmap cache after scrolling.
     */
    @ExportedProperty(category = "CommonControl")
    boolean mScrollingCacheEnabled;

    /**
     * Whether or not to enable the fast scroll feature on this list
     */
    @ExportedProperty(category = "CommonControl")
    boolean mFastScrollEnabled;

    /**
     * Optional callback to notify client when scroll position has changed
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public OnScrollListener mOnScrollListener;

    /**
     * Keeps track of our accessory window
     */
    PopupWindow mPopup;

    /**
     * Used with type filter window
     */
    EditText mTextFilter;

    /**
     * Indicates whether to use pixels-based or position-based scrollbar
     * properties.
     */
    @ExportedProperty(category = "CommonControl")
    private boolean mSmoothScrollbarEnabled = true;

    /**
     * Indicates that this view supports filtering
     */
    @ExportedProperty(category = "CommonControl")
    private boolean mTextFilterEnabled;

    /**
     * Indicates that this view is currently displaying a filtered view of the data
     */
    @ExportedProperty(category = "CommonControl")
    private boolean mFiltered;

    /**
     * Rectangle used for hit testing children
     */
    private Rect mTouchFrame;

    /**
     * The position to resurrect the selected position to.
     */
    int mResurrectToPosition = INVALID_POSITION;

    private ContextMenuInfo mContextMenuInfo = null;

    /**
     * Used to request a layout when we changed touch mode
     */
    private static final int TOUCH_MODE_UNKNOWN = -1;
    private static final int TOUCH_MODE_ON = 0;
    private static final int TOUCH_MODE_OFF = 1;

    private int mLastTouchMode = TOUCH_MODE_UNKNOWN;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final boolean PROFILE_SCROLLING = false;
    private boolean mScrollProfilingStarted = false;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final boolean PROFILE_FLINGING = false;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected boolean mFlingProfilingStarted = false;

    /**
     * The last CheckForLongPress runnable we posted, if any
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected CheckForLongPress mPendingCheckForLongPress;

    /**
     * The last CheckForTap runnable we posted, if any
     */
    private Runnable mPendingCheckForTap;

    /**
     * The last CheckForKeyLongPress runnable we posted, if any
     */
    private CheckForKeyLongPress mPendingCheckForKeyLongPress;

    /**
     * Acts upon click
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected AbsCrabWalkView.PerformClick mPerformClick;

    /**
     * This view is in transcript mode -- it shows the bottom of the list when the data
     * changes
     */
    private int mTranscriptMode;

    /**
     * Indicates that this list is always drawn on top of a solid, single-color, opaque
     * background
     */
    @ExportedProperty(category = "CommonControl")
    private int mCacheColorHint;

    /**
     * The select child's view (from the adapter's getView) is enabled.
     */
    @ExportedProperty(category = "CommonControl")
    private boolean mIsChildViewEnabled;

    /**
     * The last scroll state reported to clients through {@link OnScrollListener}.
     */
    private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    private int mTouchSlop;

    private float mDensityScale;

    // The tolerance of disabling GC
    int mGCTolerance = 4;

    @ExportedProperty(category = "CommonControl")
    boolean mGcDisabled = false;

    /**
     * Interface definition for a callback to be invoked when the list or grid
     * has been scrolled.
     */
    public interface OnScrollListener {

        /**
         * The view is not scrolling. Note navigating the list using the trackball counts as
         * being in the idle state since these transitions are not animated.
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public static int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the screen
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and had performed a fling. The
         * animation is now coasting to a stop
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public static int SCROLL_STATE_FLING = 2;

        /**
         * Callback method to be invoked while the list view or grid view is being scrolled. If the
         * view is being scrolled, this method will be called before the next frame of the scroll is
         * rendered. In particular, it will be called before any calls to
         * Adapter getView(int, View, ViewGroup).
         *
         * @param view The view whose scroll state is being reported
         *
         * @param scrollState The current scroll state.
         */
        public void onScrollStateChanged(AbsCrabWalkView view, int scrollState);

        /**
         * Callback method to be invoked when the list or grid has been scrolled. This will be
         * called after the scroll has completed
         * @param view The view whose scroll state is being reported
         * @param firstVisibleItem the index of the first visible cell (ignore if
         *        visibleItemCount == 0)
         * @param visibleItemCount the number of visible cells
         * @param totalItemCount the number of items in the list adaptor
         */
        public void onScroll(AbsCrabWalkView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount);
    }

    /**
     * Simple constructor to use when creating a HtcAbsListView2 from code.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public AbsCrabWalkView(Context context) {
        super(context);
        initAbsListView();
    }

    /**
     * Constructor that is called when inflating a HtcAbsListView2 from XML.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public AbsCrabWalkView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.absListViewStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view.
     */
    public AbsCrabWalkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAbsListView();

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.AbsCrabWalkView, defStyle, 0);

        //Drawable d = a.getDrawable(com.android.internal.R.styleable.AbsListView_listSelector);
        //if (d != null) {
        //    setSelector(d);
        //}

        mDrawSelectorOnTop = a.getBoolean(
                R.styleable.AbsCrabWalkView_android_drawSelectorOnTop, false);
        //mDrawSelectorOnTop = false;

        boolean stackFromBottom = false;
        //boolean stackFromBottom = a.getBoolean(R.styleable.AbsListView_stackFromBottom, false);
        setStackFromBottom(stackFromBottom);

        //boolean scrollingCacheEnabled = true;
        boolean scrollingCacheEnabled = a.getBoolean(R.styleable.AbsCrabWalkView_android_scrollingCache, true);
        setScrollingCacheEnabled(scrollingCacheEnabled);

        //boolean useTextFilter = a.getBoolean(R.styleable.AbsListView_textFilterEnabled, false);
        boolean useTextFilter = false;
        setTextFilterEnabled(useTextFilter);

        int transcriptMode = TRANSCRIPT_MODE_DISABLED;
        //int transcriptMode = a.getInt(R.styleable.AbsListView_transcriptMode,
        //        TRANSCRIPT_MODE_DISABLED);
        setTranscriptMode(transcriptMode);

        //int color = 0;
        int color = a.getColor(R.styleable.AbsCrabWalkView_android_cacheColorHint, 0);
        setCacheColorHint(color);

        boolean enableFastScroll = false;
        //boolean enableFastScroll = a.getBoolean(R.styleable.AbsListView_fastScrollEnabled, false);
        setFastScrollEnabled(enableFastScroll);

        boolean smoothScrollbar = true;
        //boolean smoothScrollbar = a.getBoolean(R.styleable.AbsListView_smoothScrollbar, true);
        setSmoothScrollbarEnabled(smoothScrollbar);

        a.recycle();
    }

    /**
     * Enables fast scrolling by letting the user quickly scroll through lists by
     * dragging the fast scroll thumb. The adapter attached to the list may want
     * to implement SectionIndexer if it wishes to display alphabet preview and
     * jump between sections of the list.
     * @see SectionIndexer
     * @see #isFastScrollEnabled()
     * @param enabled whether or not to enable fast scrolling
     */
    public void setFastScrollEnabled(boolean enabled) {
        if (enabled) {
            throw new RuntimeException("You should NOT setFastScrollEnabled to TRUE since "
                            + "there is NO horizontal fast scroller design in Sense5.0");
        }

/* Fisherson_Lin: No horizontal fast scroller design, useless in Sense5.0.
        mFastScrollEnabled = enabled;
        if (enabled) {
            if (mFastScroller == null) {
                mFastScroller = new HtcFastScroller3(getContext(), this);
            }
            setHorizontalScrollBarEnabled(false);
        } else {
            if (mFastScroller != null) {
                mFastScroller.stop();
                mFastScroller = null;
            }
        }
*/
    }

    /**
     * Returns the current state of the fast scroll feature.
     * @see #setFastScrollEnabled(boolean)
     * @return true if fast scroll is enabled, false otherwise
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @ViewDebug.ExportedProperty
    public boolean isFastScrollEnabled() {
        return mFastScrollEnabled;
    }

    /**
     * When smooth scrollbar is enabled, the position and size of the scrollbar thumb
     * is computed based on the number of visible pixels in the visible items. This
     * however assumes that all list items have the same height. If you use a list in
     * which items have different heights, the scrollbar will change appearance as the
     * user scrolls through the list. To avoid this issue, you need to disable this
     * property.
     *
     * When smooth scrollbar is disabled, the position and size of the scrollbar thumb
     * is based solely on the number of items in the adapter and the position of the
     * visible items inside the adapter. This provides a stable scrollbar as the user
     * navigates through a list of items with varying heights.
     *
     * @param enabled Whether or not to enable smooth scrollbar.
     *
     * @see #setSmoothScrollbarEnabled(boolean)
     * @attr ref android.R.styleable#AbsListView_smoothScrollbar
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setSmoothScrollbarEnabled(boolean enabled) {
        mSmoothScrollbarEnabled = enabled;
    }

    /**
     * Returns the current state of the fast scroll feature.
     *
     * @return True if smooth scrollbar is enabled is enabled, false otherwise.
     *
     * @see #setSmoothScrollbarEnabled(boolean)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @ViewDebug.ExportedProperty
    public boolean isSmoothScrollbarEnabled() {
        return mSmoothScrollbarEnabled;
    }

    /**
     * Set the listener that will receive notifications every time the list scrolls.
     *
     * @param l the scroll listener
     */
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
        invokeOnItemScrollListener();
    }

    /**
     * Notify our scroll listener (if there is one) of a change in scroll state
     */
    void invokeOnItemScrollListener() {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, mFirstPosition, getChildCount(), mItemCount);
        }
    }

    /**
     * Indicates whether the children's drawing cache is used during a scroll.
     * By default, the drawing cache is enabled but this will consume more memory.
     *
     * @return true if the scrolling cache is enabled, false otherwise
     *
     * @see #setScrollingCacheEnabled(boolean)
     * @see View#setDrawingCacheEnabled(boolean)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @ViewDebug.ExportedProperty
    public boolean isScrollingCacheEnabled() {
        return mScrollingCacheEnabled;
    }

    /**
     * Enables or disables the children's drawing cache during a scroll.
     * By default, the drawing cache is enabled but this will use more memory.
     *
     * When the scrolling cache is enabled, the caches are kept after the
     * first scrolling. You can manually clear the cache by calling
     * {@link android.view.ViewGroup#setChildrenDrawingCacheEnabled(boolean)}.
     *
     * @param enabled true to enable the scroll cache, false otherwise
     *
     * @see #isScrollingCacheEnabled()
     * @see View#setDrawingCacheEnabled(boolean)
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled && !enabled) {
            clearScrollingCache();
        }
        mScrollingCacheEnabled = enabled;
    }

    /**
     * Enables or disables the type filter window. If enabled, typing when
     * this view has focus will filter the children to match the users input.
     * Note that the Adapter used by this view must implement the
     * {@link Filterable} interface.
     *
     * @param textFilterEnabled true to enable type filtering, false otherwise
     *
     * @see Filterable
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setTextFilterEnabled(boolean textFilterEnabled) {
        mTextFilterEnabled = textFilterEnabled;
    }

    /**
     * Indicates whether type filtering is enabled for this view
     *
     * @return true if type filtering is enabled, false otherwise
     *
     * @see #setTextFilterEnabled(boolean)
     * @see Filterable
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @ViewDebug.ExportedProperty
    public boolean isTextFilterEnabled() {
        return mTextFilterEnabled;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void getFocusedRect(Rect r) {
        View view = getSelectedView();
        if (view != null) {
            // the focused rectangle of the selected view offset into the
            // coordinate space of this view.
            view.getFocusedRect(r);
            offsetDescendantRectToMyCoords(view, r);
        } else {
            // otherwise, just the norm
            super.getFocusedRect(r);
        }
    }

    private void initAbsListView() {
        // Setting focusable in touch mode will set the focusable property to true
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
        setScrollingCacheEnabled(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(true);


        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mDensityScale = getContext().getResources().getDisplayMetrics().density;
    }

    private void useDefaultSelector() {
        setSelector(getResources().getDrawable(
                android.R.drawable.list_selector_background));
    }

    /**
     * Indicates whether the content of this view is pinned to, or stacked from,
     * the bottom edge.
     *
     * @return true if the content is stacked from the bottom edge, false otherwise
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @ViewDebug.ExportedProperty
    public boolean isStackFromBottom() {
        return mStackFromBottom;
    }

    /**
     * When stack from bottom is set to true, the list fills its content starting from
     * the bottom of the view.
     *
     * @param stackFromBottom true to pin the view's content to the bottom edge,
     *        false to pin the view's content to the top edge
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setStackFromBottom(boolean stackFromBottom) {
        if (mStackFromBottom != stackFromBottom) {
            mStackFromBottom = stackFromBottom;
            requestLayoutIfNecessary();
        }
    }

    void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
            requestLayout();
            invalidate();
        }
    }

    static class SavedState extends BaseSavedState {
        long selectedId;
        long firstId;
        int viewTop;
        int viewLeft;    // Add: Andrew, Liu
        int position;
        int height;
        int width;    // Add: Andrew, Liu
        String filter;

        /**
         * Constructor called from {@link HtcAbsListView#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            selectedId = in.readLong();
            firstId = in.readLong();
            viewTop = in.readInt();
            viewLeft = in.readInt();    // Add: Andrew, Liu
            position = in.readInt();
            height = in.readInt();
            width = in.readInt();    // Add: Andrew, Liu
            filter = in.readString();
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(selectedId);
            out.writeLong(firstId);
            out.writeInt(viewTop);
            out.writeInt(viewLeft);    // Add: Andrew, Liu
            out.writeInt(position);
            out.writeInt(height);
            out.writeInt(width);    // Add: Andrew, Liu
            out.writeString(filter);
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        @Override
        public String toString() {
            return "AbsListView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " selectedId=" + selectedId
                    + " firstId=" + firstId
                    + " viewTop=" + viewTop
                    + " viewLeft=" + viewLeft    // Add: Andrew, Liu
                    + " position=" + position
                    + " height=" + height
                    + " width=" + width    // Add: Andrew, Liu
                    + " filter=" + filter + "}";
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
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
        /*
         * This doesn't really make sense as the place to dismiss the
         * popup, but there don't seem to be any other useful hooks
         * that happen early enough to keep from getting complaints
         * about having leaked the window.
         */
        dismissPopup();

        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        boolean haveChildren = getChildCount() > 0;
        long selectedId = getSelectedItemId();
        ss.selectedId = selectedId;

        // Start: Andrew, Liu
        if (isHorizontalStyle())
            ss.width = getWidth();
        else
            ss.height = getHeight();
        // End: Andrew, Liu

        if (selectedId >= 0) {
            // Remember the selection
            // Start: Andrew, Liu
            if (isHorizontalStyle())
                ss.viewLeft = mSelectedLeft;
            else
                ss.viewTop = mSelectedTop;
            // End: Andrew, Liu

            ss.position = getSelectedItemPosition();
            ss.firstId = INVALID_POSITION;
        } else {
            if (haveChildren) {
                // Remember the position of the first child
                View v = getChildAt(0);

                // Start: Andrew, Liu
                if (isHorizontalStyle())
                    ss.viewLeft = v.getLeft();
                else
                    ss.viewTop = v.getTop();
                // End: Andrew, Liu
                ss.position = mFirstPosition;
                ss.firstId = mAdapter.getItemId(mFirstPosition);
            } else {
                ss.viewTop = 0;
                ss.viewLeft = 0;    // Add: Andrew, Liu
                ss.firstId = INVALID_POSITION;
                ss.position = 0;
            }
        }

        ss.filter = null;
        if (mFiltered) {
            final EditText textFilter = mTextFilter;
            if (textFilter != null) {
                Editable filterText = textFilter.getText();
                if (filterText != null) {
                    ss.filter = filterText.toString();
                }
            }
        }

        return ss;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        mDataChanged = true;

        // Start: Andrew, Liu
        if (isHorizontalStyle())
            mSyncWidth = ss.width;
        else
            mSyncHeight = ss.height;
        // End: Andrew, Liu

        if (ss.selectedId >= 0) {
            mNeedSync = true;
            mSyncPosition = ss.position;

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                mSpecificLeft = ss.viewLeft;
                mSyncRowId = ss.selectedId;
            } else {
                mSpecificTop = ss.viewTop;
                mSyncColumnId = ss.selectedId;
            }
            // End: Andrew, Liu

            mSyncMode = SYNC_SELECTED_POSITION;
        } else if (ss.firstId >= 0) {
            setSelectedPositionInt(INVALID_POSITION);
            // Do this before setting mNeedSync since setNextSelectedPosition looks at mNeedSync
            setNextSelectedPositionInt(INVALID_POSITION);
            mNeedSync = true;

            mSyncPosition = ss.position;

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                mSpecificLeft = ss.viewLeft;
                mSyncColumnId = ss.firstId;
            } else {
                mSpecificTop = ss.viewTop;
                mSyncRowId = ss.firstId;
            }
            // End: Andrew, Liu

            mSyncMode = SYNC_FIRST_POSITION;
        }

        setFilterText(ss.filter);

        requestLayout();
    }

    private boolean acceptFilter() {
        if (!mTextFilterEnabled || !(getAdapter() instanceof Filterable) ||
                ((Filterable) getAdapter()).getFilter() == null) {
            return false;
        }
        final Context context = getContext();
        final InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return !inputManager.isFullscreenMode();
    }

    /**
     * Sets the initial value for the text filter.
     * @param filterText The text to use for the filter.
     *
     * @see #setTextFilterEnabled
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setFilterText(String filterText) {
        // TODO: Should we check for acceptFilter()?
        if (mTextFilterEnabled && !TextUtils.isEmpty(filterText)) {
            createTextFilter(false);
            // This is going to call our listener onTextChanged, but we might not
            // be ready to bring up a window yet
            mTextFilter.setText(filterText);
            mTextFilter.setSelection(filterText.length());
            if (mAdapter instanceof Filterable) {
                // if mPopup is non-null, then onTextChanged will do the filtering
                if (mPopup == null) {
                    Filter f = ((Filterable) mAdapter).getFilter();
                    f.filter(filterText);
                }
                // Set filtered to true so we will display the filter window when our main
                // window is ready
                mFiltered = true;
                mDataSetObserver.clearSavedState();
            }
        }
    }

    /**
     * Returns the list's text filter, if available.
     * @return the list's text filter or null if filtering isn't enabled
     * @hide pending API Council approval
     */
    public CharSequence getTextFilter() {
        if (mTextFilterEnabled && mTextFilter != null) {
            return mTextFilter.getText();
        }
        return null;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if (gainFocus && mSelectedPosition < 0 && !isInTouchMode()) {
            resurrectSelection();
        }
        unPressedUnSelectChildren(null);
    }

    /**
     * Request to layout the HtcAbsListView2
     */
    @Override
    public void requestLayout() {
        if (!mBlockLayoutRequests && !mInLayout) {
            super.requestLayout();
        }
    }

    /**
     * The list is empty. Clear everything out.
     */
    void resetList() {
        removeAllViewsInLayout();
        mFirstPosition = 0;
        mDataChanged = false;
        mNeedSync = false;
        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        setSelectedPositionInt(INVALID_POSITION);
        setNextSelectedPositionInt(INVALID_POSITION);
        mSelectedTop = 0;
        mSelectorRect.setEmpty();
        invalidate();
    }

    /**
     * The list is empty and we need to change the layout, so *really* clear everything out.
     * @hide - for AutoCompleteTextView & SearchDialog only
     */
    /* package */ void resetListAndClearViews() {
        rememberSyncState();
        removeAllViewsInLayout();
        mRecycler.clear();
        mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());
        requestLayout();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeVerticalScrollExtent() {
        final int count = getChildCount();
        if (count > 0) {
            if (mSmoothScrollbarEnabled) {
                int extent = count * 100;

                View view = getChildAt(0);
                final int top = view.getTop();
                int height = view.getHeight();
                if (height > 0) {
                    extent += (top * 100) / height;
                }

                view = getChildAt(count - 1);
                final int bottom = view.getBottom();
                height = view.getHeight();
                if (height > 0) {
                    extent -= ((bottom - getHeight()) * 100) / height;
                }

                return extent;
            } else {
                return 1;
            }
        }
        return 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeVerticalScrollOffset() {
        final int firstPosition = mFirstPosition;
        final int childCount = getChildCount();
        if (firstPosition >= 0 && childCount > 0) {
            if (mSmoothScrollbarEnabled) {
                final View view = getChildAt(0);
                final int top = view.getTop();
                int height = view.getHeight();
                if (height > 0) {
                    return Math.max(firstPosition * 100 - (top * 100) / height, 0);
                }
            } else {
                int index;
                final int count = mItemCount;
                if (firstPosition == 0) {
                    index = 0;
                } else if (firstPosition + childCount == count) {
                    index = count;
                } else {
                    index = firstPosition + childCount / 2;
                }
                return (int) (firstPosition + childCount * (index / (float) count));
            }
        }
        return 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeVerticalScrollRange() {
        return mSmoothScrollbarEnabled ? Math.max(mItemCount * 100, 0) : mItemCount;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected float getTopFadingEdgeStrength() {
        final int count = getChildCount();
        final float fadeEdge = super.getTopFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        } else {
            if (mFirstPosition > 0) {
                return 1.0f;
            }

            final int top = getChildAt(0).getTop();
            final float fadeLength = (float) getVerticalFadingEdgeLength();
            return top < getPaddingTop() ? (float) -(top - getPaddingTop()) / fadeLength : fadeEdge;
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected float getBottomFadingEdgeStrength() {
        final int count = getChildCount();
        final float fadeEdge = super.getBottomFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        } else {
            if (mFirstPosition + count - 1 < mItemCount - 1) {
                return 1.0f;
            }

            final int bottom = getChildAt(count - 1).getBottom();
            final int height = getHeight();
            final float fadeLength = (float) getVerticalFadingEdgeLength();
            return bottom > height - getPaddingBottom() ?
                    (float) (bottom - height + getPaddingBottom()) / fadeLength : fadeEdge;
        }
    }

    // Start: Andrew, Liu
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeHorizontalScrollExtent() {
        final int count = getChildCount();
        if (count > 0) {
            if (mSmoothScrollbarEnabled) {
                int extent = count * 100;

                View view = getChildAt(0);
                final int top = view.getLeft();
                int width = view.getWidth();
                if (width > 0) {
                    extent += (top * 100) / width;
                }

                view = getChildAt(count - 1);
                final int bottom = view.getRight();
                width = view.getWidth();
                if (width > 0) {
                    extent -= ((bottom - getWidth()) * 100) / width;
                }

                return extent;
            } else {
                return 1;
            }
        }
        return 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeHorizontalScrollOffset() {
        final int firstPosition = mFirstPosition;
        final int childCount = getChildCount();
        if (firstPosition >= 0 && childCount > 0) {
            if (mSmoothScrollbarEnabled) {
                final View view = getChildAt(0);
                final int top = view.getLeft();
                int width = view.getWidth();
                if (width > 0) {
                    return Math.max(firstPosition * 100 - (top * 100) / width, 0);
                }
            } else {
                int index;
                final int count = mItemCount;
                if (firstPosition == 0) {
                    index = 0;
                } else if (firstPosition + childCount == count) {
                    index = count;
                } else {
                    index = firstPosition + childCount / 2;
                }
                return (int) (firstPosition + childCount * (index / (float) count));
            }
        }
        return 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeHorizontalScrollRange() {
        return mSmoothScrollbarEnabled ? Math.max(mItemCount * 100, 0) : mItemCount;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected float getLeftFadingEdgeStrength() {
        final int count = getChildCount();
        final float fadeEdge = super.getLeftFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        } else {
            if (mFirstPosition > 0) {
                return 1.0f;
            }

            final int left = getChildAt(0).getLeft();
            final float fadeLength = (float) getHorizontalFadingEdgeLength();
            return left < getPaddingLeft() ? (float) -(left - getPaddingLeft()) / fadeLength : fadeEdge;
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected float getRightFadingEdgeStrength() {
        final int count = getChildCount();
        final float fadeEdge = super.getRightFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        } else {
            if (mFirstPosition + count - 1 < mItemCount - 1) {
                return 1.0f;
            }

            final int right = getChildAt(count - 1).getRight();
            final int width = getWidth();
            final float fadeLength = (float) getHorizontalFadingEdgeLength();
            return right > width - getPaddingRight() ?
                    (float) (right - width + getPaddingRight()) / fadeLength : fadeEdge;
        }
    }
    // End: Andrew, Liu

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSelector == null) {
            useDefaultSelector();
        }
        final Rect listPadding = mListPadding;
        listPadding.left = mSelectionLeftPadding + getPaddingLeft();
        listPadding.top = mSelectionTopPadding + getPaddingTop();
        listPadding.right = mSelectionRightPadding + getPaddingRight();
        listPadding.bottom = mSelectionBottomPadding + getPaddingBottom();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mInLayout = true;
        layoutChildren();
        mInLayout = false;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void layoutChildren() {
    }

    void updateScrollIndicators() {

        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            // for horizontal scrolling
            if (mScrollLeft != null) {
                boolean canScrollLeft;
                // 0th element is not visible
                canScrollLeft = mFirstPosition > 0;

                // ... Or top of 0th element is not visible
                if (!canScrollLeft) {
                    if (getChildCount() > 0) {
                        View child = getChildAt(0);
                        canScrollLeft = child.getLeft() < mListPadding.left + getLeftBorderWidth();
                    }
                }

                mScrollLeft.setVisibility(canScrollLeft ? View.VISIBLE : View.INVISIBLE);
            }

            if (mScrollRight != null) {
                boolean canScrollRight;
                int count = getChildCount();

                // Last item is not visible
                canScrollRight = (mFirstPosition + count) < mItemCount;

                // ... Or bottom of the last element is not visible
                if (!canScrollRight && count > 0) {
                    View child = getChildAt(count - 1);
                    canScrollRight = child.getRight() > getRight() - mListPadding.right - getRightBorderWidth();
                }

                mScrollRight.setVisibility(canScrollRight ? View.VISIBLE : View.INVISIBLE);
            }
        } else {
            // for vertical scrolling
            if (mScrollUp != null) {
                boolean canScrollUp;
                // 0th element is not visible
                canScrollUp = mFirstPosition > 0;

                // ... Or top of 0th element is not visible
                if (!canScrollUp) {
                    if (getChildCount() > 0) {
                        View child = getChildAt(0);
                        canScrollUp = child.getTop() < mListPadding.top + getTopBorderHeight();
                    }
                }

                mScrollUp.setVisibility(canScrollUp ? View.VISIBLE : View.INVISIBLE);
            }

            if (mScrollDown != null) {
                boolean canScrollDown;
                int count = getChildCount();

                // Last item is not visible
                canScrollDown = (mFirstPosition + count) < mItemCount;

                // ... Or bottom of the last element is not visible
                if (!canScrollDown && count > 0) {
                    View child = getChildAt(count - 1);
                    canScrollDown = child.getBottom() > getBottom() - mListPadding.bottom - getBottomBorderHeight();
                }

                mScrollDown.setVisibility(canScrollDown ? View.VISIBLE : View.INVISIBLE);
            }
        }
        // End: Andrew, Liu
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    @ViewDebug.ExportedProperty
    public View getSelectedView() {
        if (mItemCount > 0 && mSelectedPosition >= 0) {
            return getChildAt(mSelectedPosition - mFirstPosition);
        } else {
            return null;
        }
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @see android.view.View#getPaddingTop()
     * @see #getSelector()
     *
     * @return The top list padding.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getListPaddingTop() {
        return mListPadding.top;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @see android.view.View#getPaddingBottom()
     * @see #getSelector()
     *
     * @return The bottom list padding.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getListPaddingBottom() {
        return mListPadding.bottom;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @see android.view.View#getPaddingLeft()
     * @see #getSelector()
     *
     * @return The left list padding.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getListPaddingLeft() {
        return mListPadding.left;
    }

    /**
     * List padding is the maximum of the normal view's padding and the padding of the selector.
     *
     * @see android.view.View#getPaddingRight()
     * @see #getSelector()
     *
     * @return The right list padding.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getListPaddingRight() {
        return mListPadding.right;
    }

    /**
     * Get a view and have it show the data associated with the specified
     * position. This is called when we have already discovered that the view is
     * not available for reuse in the recycle bin. The only choices left are
     * converting an old view or making a new one.
     *
     * @param position The position to display
     * @return A view displaying the data associated with the specified position
     */
    View obtainView(int position) {
        View scrapView;

        scrapView = mRecycler.getScrapView(position);

        View child;
        if (scrapView != null) {
            if (ViewDebug.TRACE_RECYCLER) {
                ViewDebug.trace(scrapView, ViewDebug.RecyclerTraceType.RECYCLE_FROM_SCRAP_HEAP,
                        position, -1);
            }

            /**
             *  Recover translationY set in Bouncing. (overscroll and scroll with continuously notifyDatasetChanged)
             *
             */
            //if (mOverScrollAnimationEnabled && scrapView.getTranslationX() != 0f && !isInBouncing()) {
            //    scrapView.setTranslationX(0f);
            //}

            child = mAdapter.getView(position, scrapView, this);
            if(child == null) {
                throw new RuntimeException("Illegal getView result, getView("+position+", scrapView, this) should not be null");
            }
            if (ViewDebug.TRACE_RECYCLER) {
                ViewDebug.trace(child, ViewDebug.RecyclerTraceType.BIND_VIEW,
                        position, getChildCount());
            }

            if (child != scrapView) {
                mRecycler.addScrapView(scrapView);
                if (mCacheColorHint != 0) {
                    child.setDrawingCacheBackgroundColor(mCacheColorHint);
                }
                if (ViewDebug.TRACE_RECYCLER) {
                    ViewDebug.trace(scrapView, ViewDebug.RecyclerTraceType.MOVE_TO_SCRAP_HEAP,
                            position, -1);
                }
            }
        } else {
            child = mAdapter.getView(position, null, this);
            if(child == null)
                throw new RuntimeException("Illegal getView result, getView("+position+", scrapView, this) should not be null");
            if (mCacheColorHint != 0) {
                child.setDrawingCacheBackgroundColor(mCacheColorHint);
            }
            if (ViewDebug.TRACE_RECYCLER) {
                ViewDebug.trace(child, ViewDebug.RecyclerTraceType.NEW_VIEW,
                        position, getChildCount());
            }
        }

        return child;
    }

    void positionSelector(View sel) {
        unPressedUnSelectChildren(sel);
        final Rect selectorRect = mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right,
                selectorRect.bottom);

        final boolean isChildViewEnabled = mIsChildViewEnabled;
        if (sel.isEnabled() != isChildViewEnabled) {
            mIsChildViewEnabled = !isChildViewEnabled;
            refreshDrawableState();
        }
    }

    private void positionSelector(int l, int t, int r, int b) {
        mSelectorRect.set(l - mSelectionLeftPadding, t - mSelectionTopPadding, r
                + mSelectionRightPadding, b + mSelectionBottomPadding);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = 0;
        boolean iscliptopadding = isClipToPadding();
        if (iscliptopadding) {
            saveCount = canvas.save();
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();
            canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                    scrollX + getRight() - getLeft() - getPaddingRight(),
                    scrollY + getBottom() - getTop() - getPaddingBottom());
        }

        final boolean drawSelectorOnTop = mDrawSelectorOnTop;
        if (!drawSelectorOnTop) {
            drawSelector(canvas);
        }

        if (iscliptopadding) {
            canvas.restoreToCount(saveCount);
        }

        super.dispatchDraw(canvas);

        if (drawSelectorOnTop) {
            drawSelector(canvas);
        }


    }

    private boolean mIsClipToPadding = true;

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
        mIsClipToPadding = clipToPadding;
    }

    private boolean isClipToPadding() {
        boolean isPaddingNotNull = (getPaddingLeft() | getPaddingTop()
                | getPaddingRight() | getPaddingBottom()) != 0;
        return mIsClipToPadding && isPaddingNotNull;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (getChildCount() > 0) {
            mDataChanged = true;
            rememberSyncState();
        }

    }

    /**
     * @return True if the current touch mode requires that we draw the selector in the pressed
     *         state.
     */
    boolean touchModeDrawsInPressedState() {
        // FIXME use isPressed for this
        switch (mTouchMode) {
        case TOUCH_MODE_TAP:
        case TOUCH_MODE_DONE_WAITING:
            return true;
        default:
            return false;
        }
    }

    /**
     * Indicates whether this view is in a state where the selector should be drawn. This will
     * happen if we have focus but are not in touch mode, or we are in the middle of displaying
     * the pressed state for an item.
     *
     * @return True if the selector should be shown
     */
    boolean shouldShowSelector() {
        return (hasFocus() && !isInTouchMode()) || touchModeDrawsInPressedState();
    }

    private void drawSelector(Canvas canvas) {
        if (shouldShowSelector() && mSelectorRect != null && !mSelectorRect.isEmpty()) {
            final Drawable selector = mSelector;
            selector.setBounds(mSelectorRect);
            selector.draw(canvas);
        }
    }

    /**
     * Controls whether the selection highlight drawable should be drawn on top of the item or
     * behind it.
     *
     * @param onTop If true, the selector will be drawn on the item it is highlighting. The default
     *        is false.
     *
     * @attr ref android.R.styleable#AbsListView_drawSelectorOnTop
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setDrawSelectorOnTop(boolean onTop) {
        mDrawSelectorOnTop = onTop;
    }

    /**
     * Set a Drawable that should be used to highlight the currently selected item.
     *
     * @param resID A Drawable resource to use as the selection highlight.
     *
     * @attr ref android.R.styleable#AbsListView_listSelector
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setSelector(int resID) {
        setSelector(getResources().getDrawable(resID));
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setSelector(Drawable sel) {
        if (mSelector != null) {
            mSelector.setCallback(null);
            unscheduleDrawable(mSelector);
        }
        mSelector = sel;
        Rect padding = new Rect();
        sel.getPadding(padding);
        mSelectionLeftPadding = 0;
        mSelectionTopPadding = 0;
        mSelectionRightPadding = 0;
        mSelectionBottomPadding = 0;
        sel.setCallback(this);
        sel.setState(getDrawableState());
    }

    /**
     * Returns the selector {@link android.graphics.drawable.Drawable} that is used to draw the
     * selection in the list.
     *
     * @return the drawable used to display the selector
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public Drawable getSelector() {
        return mSelector;
    }

    /**
     * Sets the selector state to "pressed" and posts a CheckForKeyLongPress to see if
     * this is a long press.
     */
    void keyPressed() {
        Drawable selector = mSelector;
        Rect selectorRect = mSelectorRect;
        if (selector != null && (isFocused() || touchModeDrawsInPressedState())
                && selectorRect != null && !selectorRect.isEmpty()) {

            final View v = getChildAt(mSelectedPosition - mFirstPosition);

            if (v != null) {
                if (v.hasFocusable()) return;
                v.setPressed(true);
            }
            setPressed(true);

            final boolean longClickable = isLongClickable();
            Drawable d = selector.getCurrent();
            if (d != null && d instanceof TransitionDrawable) {
                if (longClickable) {
                    ((TransitionDrawable) d).startTransition(ViewConfiguration
                            .getLongPressTimeout());
                } else {
                    ((TransitionDrawable) d).resetTransition();
                }
            }
            if (longClickable && !mDataChanged) {
                if (mPendingCheckForKeyLongPress == null) {
                    mPendingCheckForKeyLongPress = new CheckForKeyLongPress();
                }
                mPendingCheckForKeyLongPress.rememberWindowAttachCount();
                postDelayed(mPendingCheckForKeyLongPress, ViewConfiguration.getLongPressTimeout());
            }
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setScrollIndicators(View up, View down) {
        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            mScrollLeft = up;
            mScrollRight = down;
            return;
        } else {
            mScrollUp = up;
            mScrollDown = down;
        }
        // End: Andrew, Liu
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mSelector != null) {
            mSelector.setState(getDrawableState());
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        // If the child view is enabled then do the default behavior.
        if (mIsChildViewEnabled) {
            // Common case
            return super.onCreateDrawableState(extraSpace);
        }

        // The selector uses this View's drawable state. The selected child view
        // is disabled, so we need to remove the enabled state from the drawable
        // states.
        final int enabledState = ENABLED_STATE_SET[0];

        // If we don't have any extra space, it will return one of the static state arrays,
        // and clearing the enabled state on those arrays is a bad thing!  If we specify
        // we need extra space, it will create+copy into a new array that safely mutable.
        int[] state = super.onCreateDrawableState(extraSpace + 1);
        int enabledPos = -1;
        for (int i = state.length - 1; i >= 0; i--) {
            if (state[i] == enabledState) {
                enabledPos = i;
                break;
            }
        }

        // Remove the enabled state
        if (enabledPos >= 0) {
            System.arraycopy(state, enabledPos + 1, state, enabledPos,
                    state.length - enabledPos - 1);
        }

        return state;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean verifyDrawable(Drawable dr) {
        return mSelector == dr || super.verifyDrawable(dr);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) {
            treeObserver.addOnTouchModeChangeListener(this);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        final ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) {
            treeObserver.removeOnTouchModeChangeListener(this);
        }

    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        final int touchMode = isInTouchMode() ? TOUCH_MODE_ON : TOUCH_MODE_OFF;

        if (!hasWindowFocus) {

        // update by U12 Hyper Hsieh ===========================
        if(delayActionUpTime && mSelectedView!=null) {
            resetPressedStatus(mSelectedView);
        }
        // end of update ====================================

            setChildrenDrawingCacheEnabled(false);
            removeCallbacks(mFlingRunnable);
            // Always hide the type filter
            dismissPopup();

            if (touchMode == TOUCH_MODE_OFF) {
                // Remember the last selected element
                mResurrectToPosition = mSelectedPosition;
            }
        } else {
            if (mFiltered) {
                // Show the type filter only if a filter is in effect
                showPopup();
            }

            // If we changed touch mode since the last time we had focus
            if (touchMode != mLastTouchMode && mLastTouchMode != TOUCH_MODE_UNKNOWN) {
                // If we come back in trackball mode, we bring the selection back
                if (touchMode == TOUCH_MODE_OFF) {
                    // This will trigger a layout
                    resurrectSelection();

                // If we come back in touch mode, then we want to hide the selector
                } else {
                    hideSelector();
                    mLayoutMode = LAYOUT_NORMAL;
                    layoutChildren();
                }
            }
        }

        mLastTouchMode = touchMode;
    }

    /**
     * Creates the ContextMenuInfo returned from {@link #getContextMenuInfo()}. This
     * methods knows the view, position and ID of the item that received the
     * long press.
     *
     * @param view The view that received the long press.
     * @param position The position of the item that received the long press.
     * @param id The ID of the item that received the long press.
     * @return The extra information that should be returned by
     *         {@link #getContextMenuInfo()}.
     */
    ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }

    /**
     * A base class for Runnables that will check that their view is still attached to
     * the original window as when the Runnable was created.
     *
     */
    private class WindowRunnnable {
        private int mOriginalAttachCount;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void rememberWindowAttachCount() {
            mOriginalAttachCount = getWindowAttachCount();
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public boolean sameWindow() {
            return hasWindowFocus() && getWindowAttachCount() == mOriginalAttachCount;
        }
    }

    protected class PerformClick extends WindowRunnnable implements Runnable {
        View mChild;
        int mClickMotionPosition;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void run() {
            // The data has changed since we posted this action in the event queue,
            // bail out before bad things happen
            if (mDataChanged) return;

            if (mAdapter != null && mItemCount > 0 &&
                    mClickMotionPosition < mAdapter.getCount() && sameWindow()) {
                performItemClick(mChild, mClickMotionPosition, getAdapter().getItemId(
                        mClickMotionPosition));
            }
        }
    }

    private class CheckForLongPress extends WindowRunnnable implements Runnable {
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void run() {
            final int motionPosition = mMotionPosition;
            final View child = getChildAt(motionPosition - mFirstPosition);
            if (child != null) {
                final int longPressPosition = mMotionPosition;
                final long longPressId = mAdapter.getItemId(mMotionPosition);

                boolean handled = false;
                if (sameWindow() && !mDataChanged) {
                    handled = performLongPress(child, longPressPosition, longPressId);
                }
                if (handled) {
                    mTouchMode = TOUCH_MODE_REST;
                    setPressed(false);
                    child.setPressed(false);
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }

            }
        }
    }

    private class CheckForKeyLongPress extends WindowRunnnable implements Runnable {
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void run() {
            if (isPressed() && mSelectedPosition >= 0) {
                int index = mSelectedPosition - mFirstPosition;
                View v = getChildAt(index);

                if (!mDataChanged) {
                    boolean handled = false;
                    if (sameWindow()) {
                        handled = performLongPress(v, mSelectedPosition, mSelectedRowId);
                    }
                    if (handled) {
                        setPressed(false);
                        v.setPressed(false);
                    }
                } else {
                    setPressed(false);
                    if (v != null) v.setPressed(false);
                }
            }
        }
    }

    private boolean performLongPress(final View child,
            final int longPressPosition, final long longPressId) {
        boolean handled = false;

        if (mOnItemLongClickListener != null) {
            handled = mOnItemLongClickListener.onItemLongClick(AbsCrabWalkView.this, child,
                    longPressPosition, longPressId);
        }
        if (!handled) {
            mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            handled = super.showContextMenuForChild(AbsCrabWalkView.this);
        }
        if (handled) {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return handled;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean showContextMenuForChild(View originalView) {
        final int longPressPosition = getPositionForView(originalView);
        if (longPressPosition >= 0) {
            final long longPressId = mAdapter.getItemId(longPressPosition);
            boolean handled = false;

            if (mOnItemLongClickListener != null) {
                handled = mOnItemLongClickListener.onItemLongClick(AbsCrabWalkView.this, originalView,
                        longPressPosition, longPressId);
            }
            if (!handled) {
                mContextMenuInfo = createContextMenuInfo(
                        getChildAt(longPressPosition - mFirstPosition),
                        longPressPosition, longPressId);
                handled = super.showContextMenuForChild(originalView);
            }

            return handled;
        }
        return false;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            if (isPressed() && mSelectedPosition >= 0 && mAdapter != null &&
                    mSelectedPosition < mAdapter.getCount()) {
                final View view = getChildAt(mSelectedPosition - mFirstPosition);
                performItemClick(view, mSelectedPosition, mSelectedRowId);
                setPressed(false);
                if (view != null) view.setPressed(false);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void dispatchSetPressed(boolean pressed) {
        // Don't dispatch setPressed to our children. We call setPressed on ourselves to
        // get the selector in the right state, but we don't want to press each child.
    }

    /**
     * Maps a point to a position in the list.
     *
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The position of the item which contains the specified point, or
     *         {@link #INVALID_POSITION} if the point does not intersect an item.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return mFirstPosition + i;
                }
            }
        }
        return INVALID_POSITION;
    }


    /**
     * Maps a point to a the rowId of the item which intersects that point.
     *
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The rowId of the item which contains the specified point, or {@link #INVALID_ROW_ID}
     *         if the point does not intersect an item.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public long pointToRowId(int x, int y) {
        int position = pointToPosition(x, y);
        if (position >= 0) {
            return mAdapter.getItemId(position);
        }
        return INVALID_ROW_ID;
    }

    final class CheckForTap implements Runnable {
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void run() {
            if (mTouchMode == TOUCH_MODE_DOWN) {
                mTouchMode = TOUCH_MODE_TAP;
//                unPressedAllChildren();
                final View child = getChildAt(mMotionPosition - mFirstPosition);
                if (child != null && !child.hasFocusable()) {
                    mLayoutMode = LAYOUT_NORMAL;

                    if (!mDataChanged) {
                        layoutChildren();
                        child.setPressed(true);
                        positionSelector(child);
                        setPressed(true);

                        final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                        final boolean longClickable = isLongClickable();

                        if (mSelector != null) {
                            Drawable d = mSelector.getCurrent();
                            if (d != null && d instanceof TransitionDrawable) {
                                if (longClickable) {
                                    ((TransitionDrawable) d).startTransition(longPressTimeout);
                                } else {
                                    ((TransitionDrawable) d).resetTransition();
                                }
                            }
                        }

                        if (longClickable) {
                            if (mPendingCheckForLongPress == null) {
                                mPendingCheckForLongPress = new CheckForLongPress();
                            }
                            mPendingCheckForLongPress.rememberWindowAttachCount();
                            postDelayed(mPendingCheckForLongPress, longPressTimeout);
                        } else {
                            mTouchMode = TOUCH_MODE_DONE_WAITING;
                        }
                    } else {
                        mTouchMode = TOUCH_MODE_DONE_WAITING;
                    }
                }
            }
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected boolean startScrollIfNeeded(int deltaY) {
        // Check if we have moved far enough that it looks more like a
        // scroll than a tap
        final int distance = Math.abs(deltaY);
        if (distance > mTouchSlop) {
            createScrollingCache();
            mTouchMode = TOUCH_MODE_SCROLL;
            mMotionCorrection = deltaY;
            final Handler handler = getHandler();
            // Handler should not be null unless the AbsListView is not attached to a
            // window, which would make it very hard to scroll it... but the monkeys
            // say it's possible.
            if (handler != null) {
                handler.removeCallbacks(mPendingCheckForLongPress);
            }
            setPressed(false);
            View motionView = getChildAt(mMotionPosition - mFirstPosition);
            if (motionView != null) {
                motionView.setPressed(false);
            }
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            // Time to start stealing events! Once we've stolen them, don't let anyone
            // steal from us
            requestDisallowInterceptTouchEvent(true);
            return true;
        }

        return false;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void onTouchModeChanged(boolean isInTouchMode) {
        if (isInTouchMode) {
            // Get rid of the selection when we enter touch mode
            hideSelector();
            // Layout, but only if we already have done so previously.
            // (Otherwise may clobber a LAYOUT_SYNC layout that was requested to restore
            // state.)
            if (getHeight() > 0 && getChildCount() > 0) {
                // We do not lose focus initiating a touch (since AbsListView is focusable in
                // touch mode). Force an initial layout to get rid of the selection.
                mLayoutMode = LAYOUT_NORMAL;
                layoutChildren();
            }
        }
    }

    // update by U12 Hyper Hsieh ===========================
    @ExportedProperty(category = "CommonControl")
    private boolean delayActionUpTime = false;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setDelayActionUpTime(boolean b) {
        delayActionUpTime = b;
    }

    @ExportedProperty(category = "CommonControl")
    private boolean delayIncludeDoneWaiting = false;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setDelayIncludeDoneWaiting(boolean b) {
        delayIncludeDoneWaiting = b;
    }

    private View mSelectedView = null;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void resetPressedStatus(View v) {
        if(v!=null) {
        v.setPressed(false);
        setPressed(false);
        mTouchMode = TOUCH_MODE_REST;
        }
    }
    // end of update ====================================

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        super.onTouchEvent(ev);

        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        View v;
        int deltaY;

        // Start: Andrew, Liu
        int deltaX;
        // End: Andrew, Liu

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
        case MotionEvent.ACTION_DOWN: {
            int motionPosition = pointToPosition(x, y);
            if (!mDataChanged) {
                if ((mTouchMode != TOUCH_MODE_FLING) && (motionPosition >= 0)
                        && (getAdapter().isEnabled(motionPosition))) {
                    // User clicked on an actual view (and was not stopping a fling). It might be a
                    // click or a scroll. Assume it is a click until proven otherwise
                    mTouchMode = TOUCH_MODE_DOWN;
                    // FIXME Debounce
                    if (mPendingCheckForTap == null) {
                        mPendingCheckForTap = new CheckForTap();
                    }
                    postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                } else {
                    if (ev.getEdgeFlags() != 0 && motionPosition < 0) {
                        // If we couldn't find a view to click on, but the down event was touching
                        // the edge, we will bail out and try again. This allows the edge correcting
                        // code in ViewRoot to try to find a nearby view to select
                        return false;
                    }
                    // User clicked on whitespace, or stopped a fling. It is a scroll.
                    if (mTouchMode == TOUCH_MODE_FLING) {
                    createScrollingCache();
                    mTouchMode = TOUCH_MODE_SCROLL;

                    // Start: Andrew, Liu
                    if (isHorizontalStyle()) {
                        motionPosition = findMotionColumn(x);
                    } else {
                        motionPosition = findMotionRow(y);
                    }
                    // End: Andewq, Liu

                    reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                    }
                }
            }

            if (motionPosition >= 0) {
                // Remember where the motion event started
                v = getChildAt(motionPosition - mFirstPosition);

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    mMotionViewOriginalLeft = v.getLeft();
                    mMotionX = x;
                        mFixm = false;
                } else {
                    mMotionViewOriginalTop = v.getTop();
                    mMotionY = y;
                }
                // End: Andrew, Liu

                mMotionPosition = motionPosition;
            }
            mLastY = Integer.MIN_VALUE;
            mLastX = Integer.MIN_VALUE;
            break;
        }

        case MotionEvent.ACTION_MOVE: {
            deltaY = y - mMotionY;

            //Start: Andrew, Liu
            deltaX = x - mMotionX;
            // End: Andrew, Liu

            switch (mTouchMode) {
            case TOUCH_MODE_DOWN:
            case TOUCH_MODE_TAP:
            case TOUCH_MODE_DONE_WAITING:
                // Check if we have moved far enough that it looks more like a
                // scroll than a tap

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    startScrollIfNeeded(deltaX);
                } else {
                    startScrollIfNeeded(deltaY);
                }
                // End: Andrew, Liu
                break;
            case TOUCH_MODE_SCROLL:
                if (PROFILE_SCROLLING) {
                    if (!mScrollProfilingStarted) {
                        Debug.startMethodTracing("AbsListViewScroll");
                        mScrollProfilingStarted = true;
                    }
                }

                if (isInBouncing())
                    break;
                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    if (x != mLastX) {
                        deltaX -= mMotionCorrection;
                        int incrementalDeltaX = mLastX != Integer.MIN_VALUE ? x - mLastX : deltaX;
                        //trackMotionScrollWithConstrain(deltaX, incrementalDeltaX);

                        final int motionIndex;
                        if (mMotionPosition >= 0) {
                            motionIndex = mMotionPosition - mFirstPosition;
                        } else {
                            // If we don't have a motion position that we can reliably track,
                            // pick something in the middle to make a best guess at things below.
                            motionIndex = getChildCount() / 2;
                        }

                      //int motionViewPrevTop = 0;
                        View motionView = this.getChildAt(motionIndex);
                        //if (motionView != null) {
                        //    motionViewPrevTop = motionView.getTop();
                        //}

                        // No need to do all this work if we're not going to move anyway
                        //boolean atEdge = false;
                        if (incrementalDeltaX != 0) {
                            /*atEdge = */trackMotionScroll(deltaY, incrementalDeltaX);
                        }

                        // Check to see if we have bumped into the scroll limit
                        //View motionView = this.getChildAt(mMotionPosition - mFirstPosition);
                        if (motionView != null) {
                            // Check if the top of the motion view is where it is
                            // supposed to be
                            if (motionView.getLeft() != mMotionViewNewLeft) {
                                // We did not scroll the full amount. Treat this essentially like the
                                // start of a new touch scroll
                                final int motionPosition = findClosestMotionColumn(x);

                                mMotionCorrection = 0;
                                motionView = getChildAt(motionPosition - mFirstPosition);
                                mMotionViewOriginalLeft = motionView != null?motionView.getLeft():0;
                                mMotionX = x;
                                mMotionPosition = motionPosition;
                            }
                        }
                        mLastX = x;
                    }
                } else {
                    if (y != mLastY) {
                        deltaY -= mMotionCorrection;
                        int incrementalDeltaY = mLastY != Integer.MIN_VALUE ? y - mLastY : deltaY;
                if (incrementalDeltaY != 0) {
                            trackMotionScrollWithConstrain(deltaY, incrementalDeltaY);
                }

                        // Check to see if we have bumped into the scroll limit
                        View motionView = this.getChildAt(mMotionPosition - mFirstPosition);
                        if (motionView != null) {
                            // Check if the top of the motion view is where it is
                            // supposed to be
                            if (motionView.getTop() != mMotionViewNewTop) {
                                // We did not scroll the full amount. Treat this essentially like the
                                // start of a new touch scroll
                                final int motionPosition = findMotionRow(y);

                                mMotionCorrection = 0;
                                motionView = getChildAt(motionPosition - mFirstPosition);
                                mMotionViewOriginalTop = motionView.getTop();
                                mMotionY = y;
                                mMotionPosition = motionPosition;
                            }
                        }
                        mLastY = y;
                    }
                }
                // End: Andrew, Liu
                break;
            }

            break;
        }

        case MotionEvent.ACTION_UP: {
            switch (mTouchMode) {
            case TOUCH_MODE_DOWN:
            case TOUCH_MODE_TAP:
            case TOUCH_MODE_DONE_WAITING:
                final int motionPosition = mMotionPosition;
                final View child = getChildAt(motionPosition - mFirstPosition);
                if (child != null && !child.hasFocusable()) {
                    if (mTouchMode != TOUCH_MODE_DOWN) {
                        // update by U12 Hyper Hsieh ===========================
                        if(!delayActionUpTime) child.setPressed(false);
                        // end of update ====================================
                    }

                    if (mPerformClick == null) {
                        mPerformClick = new PerformClick();
                    }

                    final AbsCrabWalkView.PerformClick performClick = mPerformClick;
                    performClick.mChild = child;
                    performClick.mClickMotionPosition = motionPosition;
                    performClick.rememberWindowAttachCount();

                    mResurrectToPosition = motionPosition;

                    // update by U12 Hyper Hsieh ===========================
                    boolean b = false;
                    if(delayIncludeDoneWaiting) {
                        if (mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_TAP || mTouchMode == TOUCH_MODE_DONE_WAITING)
                            b =true;
                    }
                    else {
                        if (mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_TAP)
                            b =true;
                    }
                    // end of update ====================================

                    if (b) {
                        final Handler handler = getHandler();
                        if (handler != null) {
                            handler.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ?
                                    mPendingCheckForTap : mPendingCheckForLongPress);
                        }
                        mLayoutMode = LAYOUT_NORMAL;
                        mTouchMode = TOUCH_MODE_TAP;
                        if (!mDataChanged) {
                            setSelectedPositionInt(mMotionPosition);
                            layoutChildren();
                            child.setPressed(true);
                            positionSelector(child);
                            setPressed(true);
                            if (mSelector != null) {
                                Drawable d = mSelector.getCurrent();
                                if (d != null && d instanceof TransitionDrawable) {
                                    ((TransitionDrawable)d).resetTransition();
                                }
                            }
                            // update by U12 Hyper Hsieh ===========================
                            if(delayActionUpTime) {
                                mSelectedView = child;
                                post(performClick);
                            }
                            else {
                            postDelayed(new Runnable() {
                                public void run() {
                                    child.setPressed(false);
                                    setPressed(false);
                                    if (!mDataChanged) {
                                        post(performClick);
                                    }
                                    mTouchMode = TOUCH_MODE_REST;
                                }
                            }, ViewConfiguration.getPressedStateDuration());
                            }
                            // end of update ====================================
                        }
                        return true;
                    } else {
                        if (!mDataChanged) {
                            post(performClick);
                        }
                    }
                }
                mTouchMode = TOUCH_MODE_REST;
                break;
            case TOUCH_MODE_SCROLL:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int initialVelocity;

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    initialVelocity = (int)velocityTracker.getXVelocity();
                } else {
                    initialVelocity = (int)velocityTracker.getYVelocity();
                }
                // End: Andrew, Liu

                if ((Math.abs(initialVelocity) >
                        ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity()) &&
                        (getChildCount() > 0)) {
                    if (mVelocityListener != null) {
                        mVelocityListener.onInitVelocity(initialVelocity);
                    }
                    onFling(initialVelocity);
                } else {
                    onUp();
                }
            }

            // update by U12 Hyper Hsieh ===========================
            if(!delayActionUpTime) setPressed(false);
            // end of update ====================================

            // Need to redraw since we probably aren't drawing the selector anymore
            invalidate();

            final Handler handler = getHandler();
            if (handler != null) {
                handler.removeCallbacks(mPendingCheckForLongPress);
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }

            if (PROFILE_SCROLLING) {
                if (mScrollProfilingStarted) {
                    Debug.stopMethodTracing();
                    mScrollProfilingStarted = false;
                }
            }
            break;
        }

        case MotionEvent.ACTION_CANCEL: {
            mTouchMode = TOUCH_MODE_REST;
            setPressed(false);
            View motionView = this.getChildAt(mMotionPosition - mFirstPosition);
            if (motionView != null) {
                motionView.setPressed(false);
            }
            clearScrollingCache();

            final Handler handler = getHandler();
            if (handler != null) {
                handler.removeCallbacks(mPendingCheckForLongPress);
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }

        }

        return true;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        View v;

        switch (action) {
        case MotionEvent.ACTION_DOWN: {
            // Start: Andrew, Liu
            int motionPosition;
            if (isHorizontalStyle()) {
                motionPosition = findMotionColumn(x);
            } else {
                motionPosition = findMotionRow(y);
            }
            // End: Andrew, Liu

            if (mTouchMode != TOUCH_MODE_FLING && motionPosition >= 0) {
                // User clicked on an actual view (and was not stopping a fling).
                // Remember where the motion event started
                v = getChildAt(motionPosition - mFirstPosition);

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    mMotionViewOriginalLeft = v.getLeft();
                    mMotionX = x;
                } else {
                    mMotionViewOriginalTop = v.getTop();
                    mMotionY = y;
                }
                // End: Andrew, Liu

                mMotionPosition = motionPosition;
                mTouchMode = TOUCH_MODE_DOWN;
                clearScrollingCache();
            }
            mLastY = Integer.MIN_VALUE;
            mLastX = Integer.MIN_VALUE;
            break;
        }

        case MotionEvent.ACTION_MOVE: {
            switch (mTouchMode) {
            case TOUCH_MODE_DOWN:
                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    if (startScrollIfNeeded(x - mMotionX)) {
                        return true;
                    }
                } else {
                    if (startScrollIfNeeded(y - mMotionY)) {
                        return true;
                    }
                }
                // End: Andrew
                break;
            }
            break;
        }

        case MotionEvent.ACTION_UP: {
            mTouchMode = TOUCH_MODE_REST;
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            break;
        }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void addTouchables(ArrayList<View> views) {
        final int count = getChildCount();
        final int firstPosition = mFirstPosition;
        final ListAdapter adapter = mAdapter;

        if (adapter == null) {
            return;
        }

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (adapter.isEnabled(firstPosition + i)) {
                views.add(child);
            }
            child.addTouchables(views);
        }
    }

    /**
     * Fires an "on scroll state changed" event to the registered
     * {@link android.widget.AbsListView.OnScrollListener}, if any. The state change
     * is fired only if the specified state is different from the previously known state.
     *
     * @param newState The new scroll state.
     */
    void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(this, newState);
            }
            mLastScrollState = newState;
            switch (newState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                case OnScrollListener.SCROLL_STATE_FLING:
                    break;
            }
        }
    }

    /**
     * Responsible for fling behavior.
     * A FlingRunnable will keep re-posting itself until the fling is done.
     *
     */
    protected class FlingRunnable implements Runnable {
        /**
         * Tracks the decay of a fling scroll
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected HtcScroller mScroller;

        /**
         * Y value reported by mScroller on the previous fling
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected int mLastFlingY;

        // Start: Andrew, Liu
        /**
         * X value reported by mScroller on the previous fling
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected int mLastFlingX;
        // End: Andrew, Liu

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public FlingRunnable() {
            mScroller = new HtcScroller(getContext());
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void start(int initialVelocity) {
            int initialY;
            int initialX;

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                initialX = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
                mLastFlingX = initialX;
                mScroller.fling(initialX, 0, initialVelocity, 0,
                        0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            } else {
                initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
                mLastFlingY = initialY;
                mScroller.fling(0, initialY, 0, initialVelocity,
                        0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            }
            // End: Andrew, Liu
            mTouchMode = TOUCH_MODE_FLING;
            postOnAnimation(this);

            if (PROFILE_FLINGING) {
                if (!mFlingProfilingStarted) {
                    Debug.startMethodTracing("AbsListViewFling");
                    mFlingProfilingStarted = true;
                }
            }
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        protected void endFling() {
            mTouchMode = TOUCH_MODE_REST;
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            clearScrollingCache();
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void run() {
            if (mTouchMode != TOUCH_MODE_FLING) {
                return;
            }

            if (mItemCount == 0 || getChildCount() == 0) {
                endFling();
                return;
            }

            final HtcScroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int y = scroller.getCurrY();

            // Start: Andrew, Liu
            final int x = scroller.getCurrX();
            // End: Andrew, Liu

            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            int delta;

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                delta = mLastFlingX - x;
            } else {
                delta = mLastFlingY - y;
            }
            // End: Andrew, Liu

            // Pretend that each frame of a fling scroll is a touch scroll
            if (delta > 0) {
                // List is moving towards the top. Use first view as mMotionPosition
                mMotionPosition = mFirstPosition;
                final View firstView = getChildAt(0);

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    mMotionViewOriginalLeft = firstView.getLeft();

                    // Don't fling more than 1 screen
                    delta = Math.min(getWidth() - getPaddingRight() - getPaddingLeft() - 1, delta);
                } else {
                    mMotionViewOriginalTop = firstView.getTop();

                    // Don't fling more than 1 screen
                    delta = Math.min(getHeight() - getPaddingBottom() - getPaddingTop() - 1, delta);
                }
                // End: Andrew, Liu
            } else {
                // List is moving towards the bottom. Use last view as mMotionPosition
                int offsetToLast = getChildCount() - 1;
                mMotionPosition = mFirstPosition + offsetToLast;

                final View lastView = getChildAt(offsetToLast);

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    mMotionViewOriginalLeft = lastView.getLeft();

                    // Don't fling more than 1 screen
                    delta = Math.min(getWidth() - getPaddingRight() - getPaddingLeft() - 1, delta);
                } else {
                    mMotionViewOriginalTop = lastView.getTop();

                    // Don't fling more than 1 screen
                    delta = Math.min(getHeight() - getPaddingBottom() - getPaddingTop() - 1, delta);
                }
                // End: Andrew, Liu
            }

            trackMotionScroll(delta, delta);

            // Check to see if we have bumped into the scroll limit
            View motionView = getChildAt(mMotionPosition - mFirstPosition);
            if (motionView != null) {
                // Check if the top of the motion view is where it is
                // supposed to be

                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    if (motionView.getLeft() != mMotionViewNewLeft) {
                        more = false;
                     }
                } else {
                    if (motionView.getTop() != mMotionViewNewTop) {
                       more = false;
                    }
                }
                // End: Andrew, Liu
            }

            if (more) {
                // Start: Andrew, Liu
                if (isHorizontalStyle()) {
                    mLastFlingX = x;
                } else {
                    mLastFlingY = y;
                }
                // End: Andrew, Liu
                postOnAnimation(this);
            } else {
                endFling();
                if (PROFILE_FLINGING) {
                    if (mFlingProfilingStarted) {
                        Debug.stopMethodTracing();
                        mFlingProfilingStarted = false;
                    }
                }
            }
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void createScrollingCache() {
        if (mScrollingCacheEnabled && !mCachingStarted) {
            setChildrenDrawnWithCacheEnabled(true);
            setChildrenDrawingCacheEnabled(true);
            mCachingStarted = true;
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void clearScrollingCache() {
        if (mCachingStarted) {
            setChildrenDrawnWithCacheEnabled(false);
            if ((getPersistentDrawingCache() & PERSISTENT_SCROLLING_CACHE) == 0) {
                setChildrenDrawingCacheEnabled(false);
            }
            if (!isAlwaysDrawnWithCacheEnabled()) {
                invalidate();
            }
            mCachingStarted = false;
        }
    }

    /**
     * Offset the horizontal location of all children of this view by the
     * specified number of pixels.
     *
     * @param offset the number of pixels to offset
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void offsetChildrenLeftAndRight(int offset) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).offsetLeftAndRight(offset);
        }
    }


    private boolean mFixm = false;
    /**
     * Track a motion scroll
     *
     * @param deltaY Amount to offset mMotionView. This is the accumulated delta since the motion
     *        began. Positive numbers mean the user's finger is moving down the screen.
     * @param incrementalDeltaY Change in deltaY from the previous event.
     * @return true if we're already at the beginning/end of the list and have nothing to do.
     */
    boolean trackMotionScroll(int deltaY, int incrementalDeltaY) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }

        final int firstTop = getChildAt(0).getTop();
        final int lastBottom = getChildAt(childCount - 1).getBottom();

        // Start: Andrew, Liu
        final int firstLeft = getChildAt(0).getLeft();
        final int lastRight = getChildAt(childCount - 1).getRight();
        // End: Andrew, Liu

        final Rect listPadding = mListPadding;

         // FIXME account for grid vertical spacing too?
        // Start: Andrew, Liu
        final int spaceAbove;
        final int end;
        final int spaceBelow;
        final int height;
        final int width;

        if (isHorizontalStyle()) {
            spaceAbove = listPadding.left - firstLeft;
            end = getWidth() - listPadding.right;
            spaceBelow = lastRight - end;
            width = getWidth() - getPaddingRight() - getPaddingLeft();

            if (deltaY < 0) {
                deltaY = Math.max(-(width - 1), deltaY);
            } else {
                deltaY = Math.min(width - 1, deltaY);
            }

            if (incrementalDeltaY < 0) {
                incrementalDeltaY = Math.max(-(width - 1), incrementalDeltaY);
            } else {
                incrementalDeltaY = Math.min(width - 1, incrementalDeltaY);
            }

        } else {
            spaceAbove = listPadding.top - firstTop;
            end = getHeight() - listPadding.bottom;
            spaceBelow = lastBottom - end;
            height = getHeight() - getPaddingBottom() - getPaddingTop();

            if (deltaY < 0) {
                deltaY = Math.max(-(height - 1), deltaY);
            } else {
                deltaY = Math.min(height - 1, deltaY);
            }

            if (incrementalDeltaY < 0) {
                incrementalDeltaY = Math.max(-(height - 1), incrementalDeltaY);
            } else {
                incrementalDeltaY = Math.min(height - 1, incrementalDeltaY);
            }
        }
        // End: Andre, Liu

        // Check if we could continue scrolling along the horizontal or vertical direction.
        final boolean cannotScrollBackward = isHorizontalStyle() ?
                (mFirstPosition == 0 && firstLeft >= listPadding.left && incrementalDeltaY >= 0):
                (mFirstPosition == 0 && firstTop >= listPadding.top && incrementalDeltaY >= 0);
        final boolean cannotScrollForward = isHorizontalStyle() ?
                (mFirstPosition + childCount == mItemCount && lastRight <= getWidth() - listPadding.right && incrementalDeltaY <= 0):
                (mFirstPosition + childCount == mItemCount && lastBottom <= getHeight() - listPadding.bottom && incrementalDeltaY <= 0);

        if (cannotScrollBackward || cannotScrollForward) {
            return incrementalDeltaY != 0;
        }

        final int absIncrementalDeltaY = Math.abs(incrementalDeltaY);

        if (spaceAbove >= absIncrementalDeltaY && spaceBelow >= absIncrementalDeltaY) {
            hideSelector();

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                offsetChildrenLeftAndRight(incrementalDeltaY);
                invalidate();
                mMotionViewNewLeft = mMotionViewOriginalLeft + deltaY;
            } else {
                //offsetChildrenTopAndBottom(incrementalDeltaY);
                invalidate();
                mMotionViewNewTop = mMotionViewOriginalTop + deltaY;
            }
            // End: Andrew, Liu
        } else {
            final int firstPosition = mFirstPosition;

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                //Modify by Jason Chiu 2009.5.1 for HtcListView
                if (firstPosition == 0 && firstLeft > listPadding.left + getLeftBoundary() + getLeftBorderWidth() && deltaY > 0 && enableStopScrollNow()) {
                    // Don't need to move views down if the top of the first position is already visible
                    onScrollToBoundary();
                    return true;
                }
                //Modify by Jason Chiu 2009.5.1 for HtcListView
                if (firstPosition + childCount == mItemCount && lastRight < end - getRightBoundary() * 2 - getRightBorderWidth() && deltaY < 0 && enableStopScrollNow()) {
                    // Don't need to move views up if the bottom of the last position is already visible
                    onScrollToBoundary();
                    return true;
                }
            } else {
                //Modify by Jason Chiu 2009.5.1 for HtcListView
                if (firstPosition == 0 && firstTop > listPadding.top + getTopBoundary() + getTopBorderHeight() && deltaY > 0 && enableStopScrollNow()) {
                    // Don't need to move views down if the top of the first position is already visible
                    onScrollToBoundary();
                    return true;
                }
                //Modify by Jason Chiu 2009.5.1 for HtcListView
                if (firstPosition + childCount == mItemCount && lastBottom < end - getBottomBoundary() * 2 - getBottomBorderHeight() && deltaY < 0 && enableStopScrollNow()) {
                    // Don't need to move views up if the bottom of the last position is already visible
                    onScrollToBoundary();
                    return true;
                }
            }

            final boolean down = incrementalDeltaY < 0;

            hideSelector();

            final int headerViewsCount = getHeaderViewsCount();
            final int footerViewsStart = mItemCount - getFooterViewsCount();

            int start = 0;
            int count = 0;
            //Modify by Jason Chiu. 2009.5.4
            //we should not recycle child view when total child view is less than one page.

            //Start: Andrew, Liu
            if (isHorizontalStyle()) {
                if(getChildrenTotalWidth() > getWidth()){
                    if (down) {
                        final int left = listPadding.left - incrementalDeltaY;
                        for (int i = 0; i < childCount; i++) {
                            final View child = getChildAt(i);
                            if (child.getRight() >= left) {
                                break;
                            } else {
                                count++;
                                int position = firstPosition + i;
                                if (position >= headerViewsCount && position < footerViewsStart) {
                                    mRecycler.addScrapView(child);

                                    if (ViewDebug.TRACE_RECYCLER) {
                                        ViewDebug.trace(child,
                                                ViewDebug.RecyclerTraceType.MOVE_TO_SCRAP_HEAP,
                                                firstPosition + i, -1);
                                    }
                                }
                            }
                        }
                    } else {
                        final int right = getWidth() - listPadding.right - incrementalDeltaY;
                        for (int i = childCount - 1; i >= 0; i--) {
                            final View child = getChildAt(i);
                            if (child.getLeft() <= right) {
                                break;
                            } else {
                                start = i;
                                count++;
                                int position = firstPosition + i;
                                if (position >= headerViewsCount && position < footerViewsStart) {
                                    mRecycler.addScrapView(child);

                                    if (ViewDebug.TRACE_RECYCLER) {
                                        ViewDebug.trace(child,
                                                ViewDebug.RecyclerTraceType.MOVE_TO_SCRAP_HEAP,
                                                firstPosition + i, -1);
                                    }
                                }
                            }
                        }
                    }
                }
                mMotionViewNewLeft = mMotionViewOriginalLeft + deltaY;
            } else {
                if(getChildrenTotalHeight() > getHeight()){
                    if (down) {
                        final int top = listPadding.top - incrementalDeltaY;
                        for (int i = 0; i < childCount; i++) {
                            final View child = getChildAt(i);
                            if (child.getBottom() >= top) {
                                break;
                            } else {
                                count++;
                                int position = firstPosition + i;
                                if (position >= headerViewsCount && position < footerViewsStart) {
                                    mRecycler.addScrapView(child);

                                    if (ViewDebug.TRACE_RECYCLER) {
                                        ViewDebug.trace(child,
                                                ViewDebug.RecyclerTraceType.MOVE_TO_SCRAP_HEAP,
                                                firstPosition + i, -1);
                                    }
                                }
                            }
                        }
                    } else {
                        final int bottom = getHeight() - listPadding.bottom - incrementalDeltaY;
                        for (int i = childCount - 1; i >= 0; i--) {
                            final View child = getChildAt(i);
                            if (child.getTop() <= bottom) {
                                break;
                            } else {
                                start = i;
                                count++;
                                int position = firstPosition + i;
                                if (position >= headerViewsCount && position < footerViewsStart) {
                                    mRecycler.addScrapView(child);

                                    if (ViewDebug.TRACE_RECYCLER) {
                                        ViewDebug.trace(child,
                                                ViewDebug.RecyclerTraceType.MOVE_TO_SCRAP_HEAP,
                                                firstPosition + i, -1);
                                    }
                                }
                            }
                        }
                    }
                }
                mMotionViewNewTop = mMotionViewOriginalTop + deltaY;
            }



            mBlockLayoutRequests = true;
            detachViewsFromParent(start, count);

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                if (mFixm) {
                   offsetChildrenLeftAndRight(-firstLeft);
                } else if (firstLeft < 0 && incrementalDeltaY > 0 && (incrementalDeltaY + firstLeft) > 0 && getCount() > 0 && getCount() == getChildCount()) {
                   offsetChildrenLeftAndRight(-firstLeft);
                   mFixm = true;
                } else {
                   if (incrementalDeltaY > 0 && getCount() > 0 && getCount() == getChildCount() && ((incrementalDeltaY + firstLeft) > 0))
                       offsetChildrenLeftAndRight(0);
                   else
                       offsetChildrenLeftAndRight(incrementalDeltaY);
                }
            } else {
                //offsetChildrenTopAndBottom(incrementalDeltaY);
            }
            // End: Andrew, Liu

            if (down) {
                mFirstPosition += count;
            }

            invalidate();
            fillGap(down);
            mBlockLayoutRequests = false;
            awakenScrollBars();

            invokeOnItemScrollListener();
        }
        return false;
    }

    /**
     * Returns the number of header views in the list. Header views are special views
     * at the top of the list that should not be recycled during a layout.
     *
     * @return The number of header views, 0 in the default implementation.
     */
    int getHeaderViewsCount() {
        return 0;
    }

    /**
     * Returns the number of footer views in the list. Footer views are special views
     * at the bottom of the list that should not be recycled during a layout.
     *
     * @return The number of footer views, 0 in the default implementation.
     */
    int getFooterViewsCount() {
        return 0;
    }

    /**
     * Fills the gap left open by a touch-scroll. During a touch scroll, children that
     * remain on screen are shifted and the other ones are discarded. The role of this
     * method is to fill the gap thus created by performing a partial layout in the
     * empty space.
     *
     * @param down true if the scroll is going down, false if it is going up
     */
    abstract void fillGap(boolean down);

    void hideSelector() {
        if (mSelectedPosition != INVALID_POSITION) {
            mResurrectToPosition = mSelectedPosition;
            if (mNextSelectedPosition >= 0 && mNextSelectedPosition != mSelectedPosition) {
                mResurrectToPosition = mNextSelectedPosition;
            }
            setSelectedPositionInt(INVALID_POSITION);
            setNextSelectedPositionInt(INVALID_POSITION);
            mSelectedTop = 0;

            // Start: Andrew, Liu
            mSelectedLeft = 0;
            // End: Andrew, Liu

            mSelectorRect.setEmpty();
        }
    }

    /**
     * @return A position to select. First we try mSelectedPosition. If that has been clobbered by
     * entering touch mode, we then try mResurrectToPosition. Values are pinned to the range
     * of items available in the adapter
     */
    int reconcileSelectedPosition() {
        int position = mSelectedPosition;
        if (position < 0) {
            position = mResurrectToPosition;
        }
        position = Math.max(0, position);
        position = Math.min(position, mItemCount - 1);
        return position;
    }

    /**
     * Find the row closest to y. This row will be used as the motion row when scrolling
     *
     * @param y Where the user touched
     * @return The position of the first (or only) item in the row closest to y
     */
    abstract int findMotionRow(int y);

    // Start: Andrew, Liu
    /**
     * Find the column closest to x. This column will be used as the motion column when scrolling
     *
     * @param x Where the user touched
     * @return The position of the first (or only) item in the column closest to x
     */
    abstract int findMotionColumn(int x);

    /**
     * Causes all the views to be rebuilt and redrawn.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void invalidateViews() {
        mDataChanged = true;
        rememberSyncState();
        requestLayout();
        invalidate();
    }
    // End: Andrew, Liu

    /**
     * Makes the item at the supplied position selected.
     *
     * @param position the position of the new selection
     */
    abstract void setSelectionInt(int position);

    /**
     * Attempt to bring the selection back if the user is switching from touch
     * to trackball mode
     * @return Whether selection was set to something.
     */
    boolean resurrectSelection() {
        final int childCount = getChildCount();

        if (childCount <= 0) {
            return false;
        }

        int selectedTop = 0;

        // Start: Andrew, Liu
        int selectedLeft = 0;
        int selectedPos;
        int childrenTop = 0;
        int childrenBottom = 0;
        int childrenLeft = 0;
        int childrenRight = 0;

        if (isHorizontalStyle()) {
            childrenLeft = mListPadding.left + getLeftBorderWidth();
            childrenRight = getRight() - getLeft() - mListPadding.right - getRightBorderWidth();
        } else {
            childrenTop = mListPadding.top + getTopBorderHeight();
            childrenBottom = getBottom() - getTop() - mListPadding.bottom - getBottomBorderHeight();
        }
        // End: Andrew, Liu

        final int firstPosition = mFirstPosition;
        final int toPosition = mResurrectToPosition;
        boolean down = true;

        if (toPosition >= firstPosition && toPosition < firstPosition + childCount) {
            selectedPos = toPosition;

            final View selected = getChildAt(selectedPos - mFirstPosition);

            // Start: Andrew, Liu
            if (isHorizontalStyle()) {
                selectedLeft = selected.getLeft();
                int selectedRight = selected.getRight();

                // We are scrolled, don't get in the fade
                if (selectedLeft < childrenLeft) {
                    selectedLeft = childrenLeft + getHorizontalFadingEdgeLength();
                } else if (selectedRight > childrenRight) {
                    selectedLeft = childrenRight - selected.getMeasuredWidth()
                            - getHorizontalFadingEdgeLength();
                }
            } else {
                selectedTop = selected.getTop();
                int selectedBottom = selected.getBottom();

                // We are scrolled, don't get in the fade
                if (selectedTop < childrenTop) {
                    selectedTop = childrenTop + getVerticalFadingEdgeLength();
                } else if (selectedBottom > childrenBottom) {
                    selectedTop = childrenBottom - selected.getMeasuredHeight()
                            - getVerticalFadingEdgeLength();
                }
            }
            // End: Andrew, Liu
        } else {
            if (toPosition < firstPosition) {
                // Default to selecting whatever is first
                selectedPos = firstPosition;
                for (int i = 0; i < childCount; i++) {
                    final View v = getChildAt(i);
                    final int top = v.getTop();

                    // Start: Andrew, Liu
                    final int left = v.getLeft();

                    if (isHorizontalStyle()) {
                        if (i == 0) {
                            // Remember the position of the first item
                            selectedLeft = left;
                            // See if we are scrolled at all
                            if (firstPosition > 0 || left < childrenLeft) {
                                // If we are scrolled, don't select anything that is
                                // in the fade region
                                childrenLeft += getHorizontalFadingEdgeLength();
                            }
                        }
                        if (left >= childrenLeft) {
                            // Found a view whose top is fully visisble
                            selectedPos = firstPosition + i;
                            selectedLeft = left;
                            break;
                        }
                    } else {
                        if (i == 0) {
                            // Remember the position of the first item
                            selectedTop = top;
                            // See if we are scrolled at all
                            if (firstPosition > 0 || top < childrenTop) {
                                // If we are scrolled, don't select anything that is
                                // in the fade region
                                childrenTop += getVerticalFadingEdgeLength();
                            }
                        }
                        if (top >= childrenTop) {
                            // Found a view whose top is fully visisble
                            selectedPos = firstPosition + i;
                            selectedTop = top;
                            break;
                        }
                    }
                    // End: Andrew, Liu
                }
            } else {
                final int itemCount = mItemCount;
                down = false;
                selectedPos = firstPosition + childCount - 1;

                for (int i = childCount - 1; i >= 0; i--) {
                    final View v = getChildAt(i);

                    // Start: Andrew, Liu
                    final int top = v.getTop();
                    final int bottom = v.getBottom();
                    final int left = v.getLeft();
                    final int right = v.getRight();

                    if (isHorizontalStyle()) {
                        if (i == childCount - 1) {
                            selectedLeft = left;
                            if (firstPosition + childCount < itemCount || right > childrenRight) {
                                childrenRight -= getHorizontalFadingEdgeLength();
                            }
                        }

                        if (right <= childrenRight) {
                            selectedPos = firstPosition + i;
                            selectedLeft = left;
                            break;
                        }
                    } else {
                        if (i == childCount - 1) {
                            selectedTop = top;
                            if (firstPosition + childCount < itemCount || bottom > childrenBottom) {
                                childrenBottom -= getVerticalFadingEdgeLength();
                            }
                        }

                        if (bottom <= childrenBottom) {
                            selectedPos = firstPosition + i;
                            selectedTop = top;
                            break;
                        }
                    }
                    // End: Andrew, Liu
                }
            }
        }

        mResurrectToPosition = INVALID_POSITION;
        removeCallbacks(mFlingRunnable);
        if(mTouchMode != TOUCH_MODE_REST){
//            unPressedAllChildren();
        }
        mTouchMode = TOUCH_MODE_REST;
        clearScrollingCache();

        // Start: Andrew, Liu
        if (isHorizontalStyle())
            mSpecificLeft = selectedLeft;
        else
            mSpecificTop = selectedTop;
        // end: Andrew Liu
        selectedPos = lookForSelectablePosition(selectedPos, down);
        if (selectedPos >= firstPosition && selectedPos <= getLastVisiblePosition()) {
            mLayoutMode = LAYOUT_SPECIFIC;
            setSelectionInt(selectedPos);
            invokeOnItemScrollListener();
        } else {
            selectedPos = INVALID_POSITION;
            mLayoutMode = LAYOUT_NORMAL;
            layoutChildren();
        }
        reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);

        return selectedPos >= 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void handleDataChanged() {
        int count = mItemCount;
        if (count > 0) {

            int newPos;

            int selectablePos;

            // Find the row we are supposed to sync to
            if (mNeedSync) {
                // Update this first, since setNextSelectedPositionInt inspects it
                mNeedSync = false;

                if (mTranscriptMode == TRANSCRIPT_MODE_ALWAYS_SCROLL ||
                        (mTranscriptMode == TRANSCRIPT_MODE_NORMAL &&
                                mFirstPosition + getChildCount() >= mOldItemCount)) {

                    // Start: Andrew, Liu
                    if (isHorizontalStyle()) {
                        mLayoutMode = LAYOUT_FORCE_RIGHT;
                    } else {
                        mLayoutMode = LAYOUT_FORCE_BOTTOM;
                    }
                    // End: Andrew, Liu
                    return;
                }

                switch (mSyncMode) {
                case SYNC_SELECTED_POSITION:
                    if (isInTouchMode()) {
                        // We saved our state when not in touch mode. (We know this because
                        // mSyncMode is SYNC_SELECTED_POSITION.) Now we are trying to
                        // restore in touch mode. Just leave mSyncPosition as it is (possibly
                        // adjusting if the available range changed) and return.
                        mLayoutMode = LAYOUT_SYNC;
                        mSyncPosition = Math.min(Math.max(0, mSyncPosition), count - 1);

                        return;
                    } else {
                        // See if we can find a position in the new data with the same
                        // id as the old selection. This will change mSyncPosition.
                        newPos = findSyncPosition();
                        if (newPos >= 0) {
                            // Found it. Now verify that new selection is still selectable
                            selectablePos = lookForSelectablePosition(newPos, true);
                            if (selectablePos == newPos) {
                                // Same row id is selected
                                mSyncPosition = newPos;

                                // Start: Andrew, Liu
                                if (mSyncHeight == getHeight() || mSyncWidth == getWidth()) {
                                    // If we are at the same height as when we saved state, try
                                    // to restore the scroll position too.
                                    mLayoutMode = LAYOUT_SYNC;
                                } else {
                                    // We are not the same height as when the selection was saved, so
                                    // don't try to restore the exact position
                                    mLayoutMode = LAYOUT_SET_SELECTION;
                                }
                                // End: Andrew, Liu

                                // Restore selection
                                setNextSelectedPositionInt(newPos);
                                return;
                            }
                        }
                    }
                    break;
                case SYNC_FIRST_POSITION:
                    // Leave mSyncPosition as it is -- just pin to available range
                    mLayoutMode = LAYOUT_SYNC;
                    mSyncPosition = Math.min(Math.max(0, mSyncPosition), count - 1);

                    return;
                }
            }

            if (!isInTouchMode()) {
                // We couldn't find matching data -- try to use the same position
                newPos = getSelectedItemPosition();

                // Pin position to the available range
                if (newPos >= count) {
                    newPos = count - 1;
                }
                if (newPos < 0) {
                    newPos = 0;
                }

                // Make sure we select something selectable -- first look down
                selectablePos = lookForSelectablePosition(newPos, true);

                if (selectablePos >= 0) {
                    setNextSelectedPositionInt(selectablePos);
                    return;
                } else {
                    // Looking down didn't work -- try looking up
                    selectablePos = lookForSelectablePosition(newPos, false);
                    if (selectablePos >= 0) {
                        setNextSelectedPositionInt(selectablePos);
                        return;
                    }
                }
            } else {

                // We already know where we want to resurrect the selection
                if (mResurrectToPosition >= 0) {
                    return;
                }
            }

        }

        // Nothing is selected. Give up and reset everything.

        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            mLayoutMode = mStackFromBottom ? LAYOUT_FORCE_RIGHT : LAYOUT_FORCE_LEFT;
        } else {
            mLayoutMode = mStackFromBottom ? LAYOUT_FORCE_BOTTOM : LAYOUT_FORCE_TOP;
        }
        // End: Andrew, Liu

        mSelectedPosition = INVALID_POSITION;
        mSelectedRowId = INVALID_ROW_ID;
        mNextSelectedPosition = INVALID_POSITION;
        mNextSelectedRowId = INVALID_ROW_ID;
        mNeedSync = false;
        checkSelectionChanged();
    }

    /**
     * Removes the filter window
     */
    void dismissPopup() {
        if (mPopup != null) {
            mPopup.dismiss();
        }
    }

    /**
     * Shows the filter window
     */
    private void showPopup() {
        // Make sure we have a window before showing the popup
        if (getWindowVisibility() == View.VISIBLE) {
            createTextFilter(true);
            positionPopup();
            // Make sure we get focus if we are showing the popup
            checkFocus();
        }
    }

    private void positionPopup() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Start: Andrew, Liu
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        // End: Andrew, Liu

        final int[] xy = new int[2];
        getLocationOnScreen(xy);
        // TODO: The 20 below should come from the theme and be expressed in dip
        // TODO: And the gravity should be defined in the theme as well

        // Start: Andrew, Liu
        if (isHorizontalStyle()) {
            final int rightGap = screenWidth - xy[0] - getWidth() + (int) (mDensityScale * 20);
            if (!mPopup.isShowing()) {
                mPopup.showAtLocation(this, Gravity.RIGHT | Gravity.CENTER_VERTICAL,
                        xy[0], rightGap);
            } else {
                mPopup.update(xy[0], rightGap, -1, -1);
            }
        } else {
            final int bottomGap = screenHeight - xy[1] - getHeight() + (int) (mDensityScale * 20);
            if (!mPopup.isShowing()) {
                mPopup.showAtLocation(this, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                        xy[0], bottomGap);
            } else {
                mPopup.update(xy[0], bottomGap, -1, -1);
            }
        }
        // End: Andrew, Liu
    }

    /**
     * What is the distance between the source and destination rectangles given the direction of
     * focus navigation between them? The direction basically helps figure out more quickly what is
     * self evident by the relationship between the rects...
     *
     * @param source the source rectangle
     * @param dest the destination rectangle
     * @param direction the direction
     * @return the distance between the rectangles
     */
    static int getDistance(Rect source, Rect dest, int direction) {
        int sX, sY; // source x, y
        int dX, dY; // dest x, y
        switch (direction) {
        case View.FOCUS_RIGHT:
            sX = source.right;
            sY = source.top + source.height() / 2;
            dX = dest.left;
            dY = dest.top + dest.height() / 2;
            break;
        case View.FOCUS_DOWN:
            sX = source.left + source.width() / 2;
            sY = source.bottom;
            dX = dest.left + dest.width() / 2;
            dY = dest.top;
            break;
        case View.FOCUS_LEFT:
            sX = source.left;
            sY = source.top + source.height() / 2;
            dX = dest.right;
            dY = dest.top + dest.height() / 2;
            break;
        case View.FOCUS_UP:
            sX = source.left + source.width() / 2;
            sY = source.top;
            dX = dest.left + dest.width() / 2;
            dY = dest.bottom;
            break;
        case View.FOCUS_FORWARD:
        case View.FOCUS_BACKWARD:
            sX = source.right + source.width() / 2;
            sY = source.top + source.height() / 2;
            dX = dest.left + dest.width() / 2;
            dY = dest.top + dest.height() / 2;
            break;
        default:
            throw new IllegalArgumentException("direction must be one of "
                    + "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, "
                    + "FOCUS_FORWARD, FOCUS_BACKWARD}. direction = " + direction);
        }
        int deltaX = dX - sX;
        int deltaY = dY - sY;
        return deltaY * deltaY + deltaX * deltaX;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected boolean isInFilterMode() {
        return mFiltered;
    }

    /**
     * Sends a key to the text filter window
     *
     * @param keyCode The keycode for the event
     * @param event The actual key event
     *
     * @return True if the text filter handled the event, false otherwise.
     */
    boolean sendToTextFilter(int keyCode, int count, KeyEvent event) {
        if (!acceptFilter()) {
            return false;
        }

        boolean handled = false;
        boolean okToSend = true;
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            okToSend = false;
            break;
        case KeyEvent.KEYCODE_BACK:
            if (mFiltered && mPopup != null && mPopup.isShowing() &&
                    event.getAction() == KeyEvent.ACTION_DOWN) {
                handled = true;
                mTextFilter.setText("");
            }
            okToSend = false;
            break;
        case KeyEvent.KEYCODE_SPACE:
            // Only send spaces once we are filtered
            okToSend = mFiltered = true;
            break;
        }

        if (okToSend) {
            createTextFilter(true);

            KeyEvent forwardEvent = event;
            if (forwardEvent.getRepeatCount() > 0) {
                forwardEvent = new KeyEvent(event, event.getEventTime(), 0);
            }

            int action = event.getAction();
            switch (action) {
                case KeyEvent.ACTION_DOWN:
                    handled = mTextFilter.onKeyDown(keyCode, forwardEvent);
                    break;

                case KeyEvent.ACTION_UP:
                    handled = mTextFilter.onKeyUp(keyCode, forwardEvent);
                    break;

                case KeyEvent.ACTION_MULTIPLE:
                    handled = mTextFilter.onKeyMultiple(keyCode, count, event);
                    break;
            }
        }
        return handled;
    }

    /**
     * Return an InputConnection for editing of the filter text.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (isTextFilterEnabled()) {
            // XXX we need to have the text filter created, so we can get an
            // InputConnection to proxy to.  Unfortunately this means we pretty
            // much need to make it as soon as a list view gets focus.
            createTextFilter(false);
            return mTextFilter.onCreateInputConnection(outAttrs);
        }
        return null;
    }

    /**
     * For filtering we proxy an input connection to an internal text editor,
     * and this allows the proxying to happen.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean checkInputConnectionProxy(View view) {
        return view == mTextFilter;
    }

    /**
     * Creates the window for the text filter and populates it with an EditText field;
     *
     * @param animateEntrance true if the window should appear with an animation
     */
    private void createTextFilter(boolean animateEntrance) {

    }

    /**
     * Clear the text filter.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void clearTextFilter() {
        if (mFiltered) {
            mTextFilter.setText("");
            mFiltered = false;
            if (mPopup != null && mPopup.isShowing()) {
                dismissPopup();
            }
        }
    }

    /**
     * Returns if the ListView currently has a text filter.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean hasTextFilter() {
        return mFiltered;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void onGlobalLayout() {
        if (isShown()) {
            // Show the popup if we are filtered
            if (mFiltered && mPopup != null && !mPopup.isShowing()) {
                showPopup();
            }
        } else {
            // Hide the popup when we are no longer visible
            if (mPopup.isShowing()) {
                dismissPopup();
            }
        }

    }

    /**
     * For our text watcher that associated with the text filter
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * For our text watcher that associated with the text filter. Performs the actual
     * filtering as the text changes.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mPopup != null && isTextFilterEnabled()) {
            int length = s.length();
            boolean showing = mPopup.isShowing();
            if (!showing && length > 0) {
                // Show the filter popup if necessary
                showPopup();
                mFiltered = true;
            } else if (showing && length == 0) {
                // Remove the filter popup if the user has cleared all text
                mPopup.dismiss();
                mFiltered = false;
            }
            if (mAdapter instanceof Filterable) {
                Filter f = ((Filterable) mAdapter).getFilter();
                // Filter should not be null when we reach this part
                if (f != null) {
                    f.filter(s, this);
                } else {
                    throw new IllegalStateException("You cannot call onTextChanged with a non "
                            + "filterable adapter");
                }
            }
        }
    }

    /**
     * For our text watcher that associated with the text filter
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void afterTextChanged(Editable s) {
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void onFilterComplete(int count) {
        if (mSelectedPosition < 0 && count > 0) {
            mResurrectToPosition = INVALID_POSITION;
            resurrectSelection();
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AbsCrabWalkView.LayoutParams(getContext(), attrs);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AbsCrabWalkView.LayoutParams;
    }

    /**
     * Puts the list or grid into transcript mode. In this mode the list or grid will always scroll
     * to the bottom to show new items.
     *
     * @param mode the transcript mode to set
     *
     * @see #TRANSCRIPT_MODE_DISABLED
     * @see #TRANSCRIPT_MODE_NORMAL
     * @see #TRANSCRIPT_MODE_ALWAYS_SCROLL
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setTranscriptMode(int mode) {
        mTranscriptMode = mode;
    }

    /**
     * Returns the current transcript mode.
     *
     * @return {@link #TRANSCRIPT_MODE_DISABLED}, {@link #TRANSCRIPT_MODE_NORMAL} or
     *         {@link #TRANSCRIPT_MODE_ALWAYS_SCROLL}
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getTranscriptMode() {
        return mTranscriptMode;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public int getSolidColor() {
        return mCacheColorHint;
    }

    /**
     * When set to a non-zero value, the cache color hint indicates that this list is always drawn
     * on top of a solid, single-color, opaque background
     *
     * @param color The background color
     */
    public void setCacheColorHint(int color) {
        mCacheColorHint = color;
    }

    /**
     * When set to a non-zero value, the cache color hint indicates that this list is always drawn
     * on top of a solid, single-color, opaque background
     *
     * @return The cache color hint
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getCacheColorHint() {
        return mCacheColorHint;
    }

    /**
     * Move all views (excluding headers and footers) held by this AbsListView into the supplied
     * List. This includes views displayed on the screen as well as views stored in AbsListView's
     * internal view recycler.
     *
     * @param views A list into which to put the reclaimed views
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void reclaimViews(List<View> views) {
        int childCount = getChildCount();
        RecyclerListener listener = mRecycler.mRecyclerListener;

        // Reclaim views on screen
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            AbsCrabWalkView.LayoutParams lp = (AbsCrabWalkView.LayoutParams)child.getLayoutParams();
            // Don't reclaim header or footer views, or views that should be ignored
            if (lp != null && mRecycler.shouldRecycleViewType(lp.viewType)) {
                views.add(child);
                if (listener != null) {
                    // Pretend they went through the scrap heap
                    listener.onMovedToScrapHeap(child);
                }
            }
        }
        mRecycler.reclaimScrapViews(views);
        removeAllViewsInLayout();
    }

    /**
     * Sets the recycler listener to be notified whenever a View is set aside in
     * the recycler for later reuse. This listener can be used to free resources
     * associated to the View.
     *
     * @param listener The recycler listener to be notified of views set aside
     *        in the recycler.
     *
     * @see android.widget.AbsListView.RecycleBin
     * @see android.widget.AbsListView.RecyclerListener
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setRecyclerListener(RecyclerListener listener) {
        mRecycler.mRecyclerListener = listener;
    }

    /**
     * AbsListView extends LayoutParams to provide a place to hold the view type.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        /**
         * View type for this view, as returned by
         * {@link android.widget.Adapter#getItemViewType(int) }
         */
        int viewType;

        /**
         * When this boolean is set, the view has been added to the AbsListView
         * at least once. It is used to know whether headers/footers have already
         * been added to the list view and whether they should be treated as
         * recycled views or not.
         */
        boolean recycledHeaderFooter;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public LayoutParams(int w, int h) {
            super(w, h);
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    /**
     * A RecyclerListener is used to receive a notification whenever a View is placed
     * inside the RecycleBin's scrap heap. This listener is used to free resources
     * associated to Views placed in the RecycleBin.
     *
     * @see android.widget.AbsListView.RecycleBin
     * @see android.widget.AbsListView#setRecyclerListener(android.widget.AbsListView.RecyclerListener)
     */
    public static interface RecyclerListener {
        /**
         * Indicates that the specified View was moved into the recycler's scrap heap.
         * The view is not displayed on screen any more and any expensive resource
         * associated with the view should be discarded.
         *
         * @param view The view is not displayed on screen any more
         */
        void onMovedToScrapHeap(View view);
    }

    /**
     * The RecycleBin facilitates reuse of views across layouts. The RecycleBin has two levels of
     * storage: ActiveViews and ScrapViews. ActiveViews are those views which were onscreen at the
     * start of a layout. By construction, they are displaying current information. At the end of
     * layout, all views in ActiveViews are demoted to ScrapViews. ScrapViews are old views that
     * could potentially be used by the adapter to avoid allocating views unnecessarily.
     *
     * @see android.widget.AbsListView#setRecyclerListener(android.widget.AbsListView.RecyclerListener)
     * @see android.widget.AbsListView.RecyclerListener
     */
    class RecycleBin {
        private RecyclerListener mRecyclerListener;

        /**
         * The position of the first view stored in mActiveViews.
         */
        private int mFirstActivePosition;

        /**
         * Views that were on screen at the start of layout. This array is populated at the start of
         * layout, and at the end of layout all view in mActiveViews are moved to mScrapViews.
         * Views in mActiveViews represent a contiguous range of Views, with position of the first
         * view store in mFirstActivePosition.
         */
        private View[] mActiveViews = new View[0];

        /**
         * Unsorted views that can be used by the adapter as a convert view.
         */
        private ArrayList<View>[] mScrapViews;

        private int mViewTypeCount;

        private ArrayList<View> mCurrentScrap;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            //noinspection unchecked
            ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new ArrayList<View>();
            }
            mViewTypeCount = viewTypeCount;
            mCurrentScrap = scrapViews[0];
            mScrapViews = scrapViews;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        /**
         * Clears the scrap heap.
         */
        void clear() {
            if (mViewTypeCount == 1) {
                final ArrayList<View> scrap = mCurrentScrap;
                final int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    removeDetachedView(scrap.remove(scrapCount - 1 - i), false);
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> scrap = mScrapViews[i];
                    final int scrapCount = scrap.size();
                    for (int j = 0; j < scrapCount; j++) {
                        removeDetachedView(scrap.remove(scrapCount - 1 - j), false);
                    }
                }
            }
        }

        /**
         * Fill ActiveViews with all of the children of the AbsListView.
         *
         * @param childCount The minimum number of views mActiveViews should hold
         * @param firstActivePosition The position of the first view that will be stored in
         *        mActiveViews
         */
        void fillActiveViews(int childCount, int firstActivePosition) {
            if (mActiveViews.length < childCount) {
                mActiveViews = new View[childCount];
            }
            mFirstActivePosition = firstActivePosition;

            final View[] activeViews = mActiveViews;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                AbsCrabWalkView.LayoutParams lp = (AbsCrabWalkView.LayoutParams)child.getLayoutParams();
                // Don't put header or footer views into the scrap heap
                if (lp != null && lp.viewType != HtcAdapterView2.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                    // Note:  We do place AdapterView.ITEM_VIEW_TYPE_IGNORE in active views.
                    //        However, we will NOT place them into scrap views.
                    activeViews[i] = child;
                }
            }
        }

        /**
         * Get the view corresponding to the specified position. The view will be removed from
         * mActiveViews if it is found.
         *
         * @param position The position to look up in mActiveViews
         * @return The view if it is found, null otherwise
         */
        View getActiveView(int position) {
            int index = position - mFirstActivePosition;
            final View[] activeViews = mActiveViews;
            if (index >=0 && index < activeViews.length) {
                final View match = activeViews[index];
                activeViews[index] = null;
                return match;
            }
            return null;
        }

        /**
         * @return A view from the ScrapViews collection. These are unordered.
         */
        View getScrapView(int position) {
            ArrayList<View> scrapViews;
            if (mViewTypeCount == 1) {
                scrapViews = mCurrentScrap;
                int size = scrapViews.size();
                if (size > 0) {
                    return scrapViews.remove(size - 1);
                } else {
                    return null;
                }
            } else {
                int whichScrap = mAdapter.getItemViewType(position);
                if (whichScrap >= 0 && whichScrap < mScrapViews.length) {
                    scrapViews = mScrapViews[whichScrap];
                    int size = scrapViews.size();
                    if (size > 0) {
                        return scrapViews.remove(size - 1);
                    }
                }
            }
            return null;
        }

        /**
         * Put a view into the ScapViews list. These views are unordered.
         *
         * @param scrap The view to add
         */
        void addScrapView(View scrap) {
            AbsCrabWalkView.LayoutParams lp = (AbsCrabWalkView.LayoutParams) scrap.getLayoutParams();
            if (lp == null) {
                return;
            }

            // Don't put header or footer views or views that should be ignored
            // into the scrap heap
            int viewType = lp.viewType;
            if (!shouldRecycleViewType(viewType)) {
                return;
            }

            if (mViewTypeCount == 1) {
                mCurrentScrap.add(scrap);
            } else {
                mScrapViews[viewType].add(scrap);
            }

            if (mRecyclerListener != null) {
                mRecyclerListener.onMovedToScrapHeap(scrap);
            }
        }

        /**
         * Move all views remaining in mActiveViews to mScrapViews.
         */
        void scrapActiveViews() {
            final View[] activeViews = mActiveViews;
            final boolean hasListener = mRecyclerListener != null;
            final boolean multipleScraps = mViewTypeCount > 1;

            ArrayList<View> scrapViews = mCurrentScrap;
            final int count = activeViews.length;
            for (int i = 0; i < count; ++i) {
                final View victim = activeViews[i];
                if (victim != null) {
                    int whichScrap = ((AbsCrabWalkView.LayoutParams)
                            victim.getLayoutParams()).viewType;

                    activeViews[i] = null;

                    if (whichScrap == HtcAdapterView2.ITEM_VIEW_TYPE_IGNORE) {
                        // Do not move views that should be ignored
                        continue;
                    }

                    if (multipleScraps) {
                        scrapViews = mScrapViews[whichScrap];
                    }
                    scrapViews.add(victim);

                    if (hasListener) {
                        mRecyclerListener.onMovedToScrapHeap(victim);
                    }

                    if (ViewDebug.TRACE_RECYCLER) {
                        ViewDebug.trace(victim,
                                ViewDebug.RecyclerTraceType.MOVE_FROM_ACTIVE_TO_SCRAP_HEAP,
                                mFirstActivePosition + i, -1);
                    }
                }
            }

            pruneScrapViews();
        }

        /**
         * Makes sure that the size of mScrapViews does not exceed the size of mActiveViews.
         * (This can happen if an adapter does not recycle its views).
         */
        private void pruneScrapViews() {
            final int maxViews = mActiveViews.length;
            final int viewTypeCount = mViewTypeCount;
            final ArrayList<View>[] scrapViews = mScrapViews;
            for (int i = 0; i < viewTypeCount; ++i) {
                final ArrayList<View> scrapPile = scrapViews[i];
                int size = scrapPile.size();
                final int extras = size - maxViews;
                size--;
                for (int j = 0; j < extras; j++) {
                    removeDetachedView(scrapPile.remove(size--), false);
                }
            }
        }

        /**
         * Puts all views in the scrap heap into the supplied list.
         */
        void reclaimScrapViews(List<View> views) {
            if (mViewTypeCount == 1) {
                views.addAll(mCurrentScrap);
            } else {
                final int viewTypeCount = mViewTypeCount;
                final ArrayList<View>[] scrapViews = mScrapViews;
                for (int i = 0; i < viewTypeCount; ++i) {
                    final ArrayList<View> scrapPile = scrapViews[i];
                    views.addAll(scrapPile);
                }
            }
        }
    }
    //+ Jason
    int getTopBoundary(){
        return 0;
    }

    int getBottomBoundary(){
        return 0;
    }

    // Start: Andrew, Liu
    int getLeftBoundary(){
        return 0;
    }

    int getRightBoundary(){
        return 0;
    }
    //End: Andrew, Liu

    void trackMotionScrollWithConstrain(int deltaY, int incrementalDeltaY){
        trackMotionScroll(deltaY, incrementalDeltaY);
    }

    void onUp(){
        mTouchMode = TOUCH_MODE_REST;
        reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
    }

    void onFling(int initialVelocity){
        if (mFlingRunnable == null) {
            mFlingRunnable = getDefaultFlingRunnable();
        }
        reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
        mFlingRunnable.start(-initialVelocity);
    }

    FlingRunnable getDefaultFlingRunnable(){
        return new FlingRunnable();
    }

    boolean enableStopScrollNow(){
        return true;
    }

    void onScrollToBoundary(){

    }

    int getChildrenTotalHeight(){
        int childrenTotalHeight = 0;
        for(int i = 0; i < getChildCount(); i++){
            childrenTotalHeight += getChildAt(i).getHeight();
        }
        return childrenTotalHeight;
    }

    // Start: Andrew, Liu
    int getChildrenTotalWidth(){
        int childrenTotalWidth = 0;
        for(int i = 0; i < getChildCount(); i++){
            childrenTotalWidth += getChildAt(i).getWidth();
        }
        return childrenTotalWidth;
    }
    // End: Andrew, Liu

    void unPressedUnSelectChildren(View sel){
        for(int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);
            if(child != sel)
                getChildAt(i).setPressed(false);
        }
//        setPressed(false);
    }

    int getBottomBorderHeight(){
        return 0;
    }

    int getTopBorderHeight(){
        return 0;
    }
    //-Jason

    // Start: Andrew, Liu
    int getRightBorderWidth(){
        return 0;
    }

    int getLeftBorderWidth(){
        return 0;
    }
    // End: Andrew, Liu

    /**
     * The callbacks to get the initial velocity before Fling.
     * @hide
     */
    public interface VelocityListener {
        /**
         * When start to fling the list, this method will be invoked
         * with the fling velocity.
         * @param velocity The fling velocity.
         */
        public void onInitVelocity(int velocity);
    }

    private VelocityListener mVelocityListener;

    /**
     * Register the velocity listener to get the initial velocity
     * @param listener The callback that will run
     * @hide
     */
    public void setVelocityListener(VelocityListener listener) {
        mVelocityListener = listener;
    }

    int findClosestMotionColumn(int x) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return INVALID_POSITION;
        }

        final int motionColumn = findMotionColumn(x);
        return motionColumn != INVALID_POSITION ? motionColumn : mFirstPosition + childCount - 1;
    }

    boolean sameWindowForPerformClick() {
        if (mPerformClick != null) {
            return mPerformClick.sameWindow();
        } else {
            return false;
        }
    }

    boolean isInBouncing(){
        return false;
    }

    /**
      * @deprecated [Module internal use]
      */
    /**@hide*/
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_SCROLL: {
                    if (mTouchMode == TOUCH_MODE_REST) {
                        final float hscroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL);
                        if (hscroll != 0) {
                            final int delta = (int) (hscroll * 150);
                            if (!trackMotionScroll(delta, delta)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return super.onGenericMotionEvent(event);
    }


}
