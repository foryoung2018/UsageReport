package com.htc.lib1.cc.view.table;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.WindowManager;
import android.view.Display;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Scroller;

import com.htc.lib1.cc.view.FocusSelection;
import com.htc.lib1.cc.view.ScrollControl;
import com.htc.lib1.cc.R;

/**
 * Common code shared between ListView and GridView
 *
 */
public abstract class AbstractTableView extends AbstractAdapterView<ListAdapter> implements TextWatcher,
        ViewTreeObserver.OnGlobalLayoutListener, Filter.FilterListener,
        ViewTreeObserver.OnTouchModeChangeListener, GestureDetector.OnGestureListener {
        private static final boolean ahanLog = false;
    private static final String TAG = "AbstractTableView";
    private static final String TRACE_CONVERTVIEW = "TraceConvertView";
    private static final boolean localLOGV = false;
    private static final boolean converViewLog = false;
    private static final boolean layoutLOG = false;
        private String mKeyOfTableView;

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
     * is a long press
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
     * Regular layout - usually an unsolicited layout from the view system
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int LAYOUT_NORMAL = 0;

    /**
     * Show the first item
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int LAYOUT_FORCE_TOP = 1;

    /**
     * Force the selected item to be on somewhere on the screen
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int LAYOUT_SET_SELECTION = 2;

    /**
     * Show the last item
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int LAYOUT_FORCE_BOTTOM = 3;

    /**
     * Make a mSelectedItem appear in a specific location and build the rest of
     * the views from there. The top is specified by mSpecificTop.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int LAYOUT_SPECIFIC = 4;

    /**
     * Layout to sync as a result of a data change. Restore mSyncPosition to have its top
     * at mSpecificTop
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int LAYOUT_SYNC = 5;

    /**
     * Layout as a result of using the navigation keys
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int LAYOUT_MOVE_SELECTION = 6;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final int LAYOUT_MOVE_SELECTION_CENTER = 7;

    /**
     * Controls how the next layout will happen
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mLayoutMode = LAYOUT_NORMAL;

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
     * This view's padding
     */
    Rect mListPadding = new Rect();

    /**
     * Subclasses must retain their measure spec from onMeasure() into this member
     */
    int mWidthHeightMeasureSpec = 0;

    /**
     * The top scroll indicator
     */
    View mScrollUp;

    /**
     * The down scroll indicator
     */
    View mScrollDown;

    /**
     * When the view is scrolling, this flag is set to true to indicate subclasses that
     * the drawing cache was enabled on the children
     */
    boolean mCachingStarted;

    /**
     * One of TOUCH_MODE_REST, TOUCH_MODE_DOWN, TOUCH_MODE_TAP, TOUCH_MODE_SCROLL, or
     * TOUCH_MODE_DONE_WAITING
     */
    int mTouchMode = TOUCH_MODE_REST;

    /**
     * Y value from on the previous motion event (if any)
     */
    int mLastY;

    /**
     * How far the finger moved before we started scrolling
     */
    int mMotionCorrection;

    /**
     * Handles one frame of a fling
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected FlingRunnable mFlingRunnable = new FlingRunnable();

    /**
     * The offset in pixels form the top of the AdapterView to the top
     * of the currently selected view. Used to save and restore state.
     */
    int mSelectedOrnTop = 0;

    /**
     * Indicates whether the list is stacked from the bottom edge or
     * the top edge.
     */
    boolean mStackFromBottom;

    /**
     * When set to true, the list automatically discards the children's
     * bitmap cache after scrolling.
     */
    boolean mScrollingCacheEnabled;

    /**
     * Optional callback to notify client when scroll position has changed
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected OnScrollListener mOnScrollListener;

    /**
     * Keeps track of our accessory window
     */
    PopupWindow mPopup;

    /**
     * Used with type filter window
     */
    EditText mTextFilter;

    /**
     * Indicates that this view supports filtering
     */
    private boolean mTextFilterEnabled;

    /**
     * Indicates that this view is currently displaying a filtered view of the data
     */
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

    // TODO: REMOVE WHEN WE'RE DONE WITH PROFILING
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected static final boolean PROFILE_SCROLLING = false;

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
     * The last CheckForKeyLongPress runnable we posted, if any
     */
    private CheckForKeyLongPress mPendingCheckForKeyLongPress;

    /**
     * This view is in transcript mode -- it shows the bottom of the list when the data
     * changes
     */
    private int mTranscriptMode;

    /**
     * Indicates that this list is always drawn on top of a solid, single-color, opaque
     * background
     */
    private int mCacheColorHint;

    /**
     * The select child's view (from the adapter's getView) is enabled.
     */
    private boolean mIsChildViewEnabled;

    /**
     * The last scroll state reported to clients through {@link OnScrollListener}.
     */
    private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected TableLayoutParams mTableLayoutParams;

    // if (scrollControl == null) do not stop on boundary
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected ScrollControl scrollControl;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected FocusSelection focusSelection;

        /**
         * TableColleague object related to this table view which helps scroll control
         */
    protected TableColleague tableColleague;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mRequestedStartPosition = -1;

    private boolean mCycle = false;

        //Record if tableview is in countdown mode.
        //TableColleague should invoke AbstractTableView.isInCountDownMode() to get this value.
        //HtcNumberPicker will invoke AbstractTableView.setCountDownMode() to set this value.
        //This is added by Ahan 2012/04/11 for WorldClock.Timer to enable multiStop correctly.
        /**
         * Flag to distinguish if it is in countdown mode now.
         */
        protected boolean mCountDownMode = false;

        /**
         * The offset value used by scrollWithOffset
         */
        protected int mTableViewSlideOffset = -1;

    /**
     * Interface definition for a callback to be invoked when the list or grid
     * has been scrolled.
     */
    public interface OnScrollListener {

        /**
         * The view is not scrolling. Note navigating the list using the trackball counts as
         * being in the idle state since these transitions are not animated.
         */
        public static int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the screen
         */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and had performed a fling. The
         * animation is now coasting to a stop
         */
        public static int SCROLL_STATE_FLING = 2;

        /**
         * The scroll/fling is finished, adjust each child to correct layout.
         */
        public static int SCROLL_STATE_INTOSLOTS = 3;

        /**
         * Callback method to be invoked while the list view or grid view is being scrolled. If the
         * view is being scrolled, this method will be called before the next frame of the scroll is
         * rendered. In particular, it will be called before any calls to
         * {@link Adapter#getView(int, View, ViewGroup)}.
         *
         * @param view The view whose scroll state is being reported
         *
         * @param scrollState The current scroll state. One of {@link #SCROLL_STATE_IDLE},
         * {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link #SCROLL_STATE_IDLE}.
         */
        public void onScrollStateChanged(AbstractTableView view, int scrollState);

        /**
         * Callback method to be invoked when the list or grid has been scrolled. This will be
         * called after the scroll has completed
         * @param view The view whose scroll state is being reported
         * @param firstVisibleItem the index of the first visible cell (ignore if
         *        visibleItemCount == 0)
         * @param visibleItemCount the number of visible cells
         * @param totalItemCount the number of items in the list adaptor
         */
        public void onScroll(AbstractTableView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount);
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public AbstractTableView(Context context) {
        super(context);
        initAbsListView();
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public AbstractTableView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.absListViewStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows
     * the theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public AbstractTableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAbsListView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AbsTableView, defStyle, 0);

        Drawable d = a.getDrawable(R.styleable.AbsTableView_android_listSelector);
        if (d != null) setSelector(d);

        mDrawSelectorOnTop = a.getBoolean(R.styleable.AbsTableView_android_drawSelectorOnTop, false);

        boolean stackFromBottom = a.getBoolean(R.styleable.AbsTableView_android_stackFromBottom, false);
        setStackFromBottom(stackFromBottom);

        boolean scrollingCacheEnabled = a.getBoolean(R.styleable.AbsTableView_android_scrollingCache, true);
        setScrollingCacheEnabled(scrollingCacheEnabled);

        boolean useTextFilter = a.getBoolean(R.styleable.AbsTableView_android_textFilterEnabled, false);
        setTextFilterEnabled(useTextFilter);

        int transcriptMode = a.getInt(R.styleable.AbsTableView_android_transcriptMode, TRANSCRIPT_MODE_DISABLED);
        setTranscriptMode(transcriptMode);

        int color = a.getColor(R.styleable.AbsTableView_android_cacheColorHint, 0);
        setCacheColorHint(color);

        a.recycle();
    }

    /**
     * Called when the current configuration of the resources being used
     * by the application have changed.  You can use this to decide when
     * to reload resources that can changed based on orientation and other
     * configuration characterstics.  You only need to use this if you are
     * not relying on the normal {@link android.app.Activity} mechanism of
     * recreating the activity instance upon a configuration change.
     *
     * @param newConfig The new resource configuration.
     * @hide
     */
    protected void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
        if (isLastScrollStateEqualsTo(OnScrollListener.SCROLL_STATE_INTOSLOTS)) {
            removeCallbacks(mFlingRunnable);
            mFlingRunnable.mScroller.forceFinished(true);
            post(new Runnable() {
                @Override
                public void run() {
                    scrollIntoSlots();
                }
            });
        }
    }

    /**
     * To Set this tableview is in countdown mode or not, this API should be used just only in Timer page of AP WorldClock.
     * @param mode The boolean value to indicate if is in countdown mode
     */
    public void setCountDownMode(boolean mode) {
        this.mCountDownMode = mode;
        if (!mode) {
            //The countdown mode has been stopped, we should remove the runnables and adjust the children to the right location
            removeCallbacks(mFlingRunnable);
            scrollIntoSlots();
        }
    }

    /**
     * To check if it is in countdown mode now.
     * @return true for it is countdown mode, false for not
     */
    public boolean isInCountDownMode() {
        return (this.mCountDownMode);
    }

    /**
     * Let user can know how long each slide takes
     * @return offset each slide
     */
    public int getTableViewSlideOffset() {
       if (mTableViewSlideOffset < 0)
            mTableViewSlideOffset = getContext().getResources().getDimensionPixelSize(R.dimen.table_view_slide_offest);
       return mTableViewSlideOffset;
    }

    /**
     * To set related key to this table view for debug uasge.
     * @param key The key string related to this table view.
     * @hide
     */
    protected void setKeyOfTableView(String key) {
        mKeyOfTableView = key;
    }

    /**
     * To get the key of this table view for debug uasge.
     * @hide
     */
    protected String getKeyOfTableView() {
        return mKeyOfTableView;
    }

    /**
     * To set the slide distance of this table.
     * @param offset The slide distance.
     * @hide
     */
    protected void setTableViewSlideOffset(int offset) {
       if (offset > 0) mTableViewSlideOffset = offset;
    }

    /**
     * Set the listener that will receive notifications every time the list scrolls.
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

        //[Ahan][2012/11/27][Add due to Accessibility support]
        onScrollChanged(0, 0, 0, 0);
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
    public boolean isTextFilterEnabled() {
        return mTextFilterEnabled;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void getFocusedRect(Rect r) {
         if(localLOGV)Log.v(TAG, "AbstractTableView > getFocusedRect()");
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
        setFocusable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
        setScrollingCacheEnabled(true);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
    }

    private void useDefaultSelector() {
        setSelector(getResources().getDrawable(android.R.drawable.list_selector_background));
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
        mStackFromBottom = stackFromBottom;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
//            mRecycler.clear();
            requestLayout();
            invalidate();
        }

        if (mRequestedStartPosition >= 0) {
            setSelection(mRequestedStartPosition);
            mRequestedStartPosition = -1;
        }
    }

    static class SavedState extends BaseSavedState {
        long selectedId;
        long firstId;
        int viewTop;
        int position;
        int height;
        String filter;

        /**
         * Constructor called from {@link AbstractTableView#onSaveInstanceState()}
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
            position = in.readInt();
            height = in.readInt();
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
            out.writeInt(position);
            out.writeInt(height);
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
                    + " position=" + position
                    + " height=" + height
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
        ss.height = getHeight();

        if (selectedId >= 0) {
            // Remember the selection
            ss.viewTop = mSelectedOrnTop;
            ss.position = getSelectedItemPosition();
            ss.firstId = INVALID_POSITION;
        } else {
            if (haveChildren) {
                // Remember the position of the first child
                View v = getChildAt(0);
                ss.viewTop = v.getTop();
                ss.position = mFirstPosition;
                ss.firstId = mAdapter.getItemId(mFirstPosition);
            } else {
                ss.viewTop = 0;
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

        mSyncHeight = ss.height;

        if (ss.selectedId >= 0) {
            mNeedSync = true;
            mSyncRowId = ss.selectedId;
            mSyncPosition = ss.position;
            mSpecificTop = ss.viewTop;
            mSyncMode = SYNC_SELECTED_POSITION;
        } else if (ss.firstId >= 0) {
            if(localLOGV)Log.v(TAG, "AbstractTableView > onRestoreInstanceState() ss.firstId >= 0, call setSelectedPositionInt(INVALID_POSITION)");
            setSelectedPositionInt(INVALID_POSITION);
            // Do this before setting mNeedSync since setNextSelectedPosition looks at mNeedSync
            setNextSelectedPositionInt(INVALID_POSITION);
            mNeedSync = true;
            mSyncRowId = ss.firstId;
            mSyncPosition = ss.position;
            mSpecificTop = ss.viewTop;
            mSyncMode = SYNC_FIRST_POSITION;
        }

        // Don't restore the type filter window when there is no keyboard
        int keyboardHidden = getContext().getResources().getConfiguration().keyboardHidden;
        if (keyboardHidden != Configuration.KEYBOARDHIDDEN_YES) {
            String filterText = ss.filter;
            setFilterText(filterText);
        }
        requestLayout();
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
        if (mTextFilterEnabled && filterText != null && filterText.length() > 0) {
            //createTextFilter(false);
            // This is going to call our listener onTextChanged, but we are
            // not ready to bring up a window yet
            mTextFilter.setText(filterText);
            mTextFilter.setSelection(filterText.length());
            if (mAdapter instanceof Filterable) {
                Filter f = ((Filterable) mAdapter).getFilter();
                f.filter(filterText);
                // Set filtered to true so we will display the filter window when our main
                // window is ready
                mFiltered = true;
                mDataSetObserver.clearSavedState();
            }
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && mSelectedPosition < 0 && !isInTouchMode()) {
            if(localLOGV)Log.v(TAG, "AbstractTableView > onFocusChanged(), gainFocus && mSelectedPosition < 0 && !isInTouchMode()");
            resurrectSelection();
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
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
        if(localLOGV)Log.v(TAG, "AbstractTableView > resetList() call setSelectedPositionInt(INVALID_POSITION)");
        setSelectedPositionInt(INVALID_POSITION);
        setNextSelectedPositionInt(INVALID_POSITION);
        mSelectedOrnTop = 0;
        mSelectorRect.setEmpty();
        invalidate();
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
        if(mSelectionPadding == null)
            mSelectionPadding = new SelectionPadding();

        listPadding.left = mSelectionPadding.getLeftPadding() + getPaddingLeft();
        listPadding.top = mSelectionPadding.getTopPadding() + getPaddingTop();
        listPadding.right = mSelectionPadding.getRightPadding() + getPaddingRight();
        listPadding.bottom = mSelectionPadding.getBottomPadding() + getPaddingBottom();
    }

    /**
     * Called when this view should assign a size and position to all of its children.
     * @param changed This is a new size or position for this view
     * @param l Left position, relative to parent
     * @param t Top position, relative to parent
     * @param r Right position, relative to parent
     * @param b Bottom position, relative to parent
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mInLayout = true;
        layoutChildren();
        mInLayout = false;

        //We don not do any scrolling after layoutChildren any more to avoid strange issues.
        /*
        if (initialWithScrollControl) {
            scrollIntoSlots();
            initialWithScrollControl = false;
        }
        */

        initialWithScrollControl = false;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void layoutChildren() {
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
        if(converViewLog)Log.v("TRACE_CONVERTVIEW", "obtainView scrapView = " + scrapView);
        View child;
        if (scrapView != null) {
            if (ViewDebug.TRACE_RECYCLER) {
                ViewDebug.trace(scrapView, ViewDebug.RecyclerTraceType.RECYCLE_FROM_SCRAP_HEAP,
                        position, -1);
            }

            child = mAdapter.getView(position, scrapView, this);

            if (ViewDebug.TRACE_RECYCLER) {
                ViewDebug.trace(child, ViewDebug.RecyclerTraceType.BIND_VIEW,
                        position, getChildCount());
            }
            if(localLOGV)Log.v(TAG, "AbstractTableView > obtainView("+position+") and scrapview is not null, mCacheColorHint = " + mCacheColorHint );
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
            if(localLOGV)Log.v(TAG, "AbstractTableView > obtainView("+position+") and scrapview is null, mCacheColorHint = " + mCacheColorHint );
            child = mAdapter.getView(position, null, this);
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
        mSelectorRect.set(l - mSelectionPadding.getLeftPadding(), t - mSelectionPadding.getTopPadding(), r
                + mSelectionPadding.getRightPadding(), b + mSelectionPadding.getBottomPadding());
    }

/*
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = 0;
        final boolean clipToPadding = (mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK;
        if (clipToPadding) {
            saveCount = canvas.save();
            final int scrollX = mScrollX;
            final int scrollY = mScrollY;
            canvas.clipRect(scrollX + mPaddingLeft, scrollY + mPaddingTop,
                    scrollX + mRight - mLeft - mPaddingRight,
                    scrollY + mBottom - mTop - mPaddingBottom);
            mGroupFlags &= ~CLIP_TO_PADDING_MASK;
        }

        final boolean drawSelectorOnTop = mDrawSelectorOnTop;
        if (!drawSelectorOnTop) {
            drawSelector(canvas);
        }

        super.dispatchDraw(canvas);

        if (drawSelectorOnTop) {
            drawSelector(canvas);
        }

        if (clipToPadding) {
            canvas.restoreToCount(saveCount);
            mGroupFlags |= CLIP_TO_PADDING_MASK;
        }
    }
    */

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
        if(mSelectionPadding == null){
            mSelectionPadding = new SelectionPadding();
        }
        mSelectionPadding.setPadding(padding.left, padding.top, padding.right, padding.bottom);

        sel.setCallback(this);
        sel.setState(getDrawableState());
    }

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
        mScrollUp = up;
        mScrollDown = down;
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

        mRecycler.clear();

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
            setChildrenDrawingCacheEnabled(false);
            if(localLOGV)Log.v("ScrollRunnable", "AbstractTableView onwindowFocusChanged() --> removeCallbacks(mFlingRunnable);");
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
                    if(layoutLOG)Log.v(TAG, "onWindowFocusChanged call layoutChildren()");
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

    private class PerformClick extends WindowRunnnable implements Runnable {
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public View mChild;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int mClickMotionPosition;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public PerformClick(){
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void run() {
            if (mAdapter == null)
                throw new IllegalArgumentException("Adapter is null, please make sure adpater has been set !!!");

            // The data has changed since we posted this action in the event queue,
            // bail out before bad things happen
            if (mDataChanged) return;

            if (mItemCount > 0 && mClickMotionPosition < mAdapter.getCount() && sameWindow()) {
                performItemClick(mChild, mClickMotionPosition, mAdapter.getItemId(mClickMotionPosition));
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
            handled = mOnItemLongClickListener.onItemLongClick(AbstractTableView.this, child,
                    longPressPosition, longPressId);
        }
        if (!handled) {
            mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            handled = super.showContextMenuForChild(AbstractTableView.this);
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
                handled = mOnItemLongClickListener.onItemLongClick(AbstractTableView.this, originalView,
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
                final int index = mSelectedPosition - mFirstPosition;
                performItemClick(getChildAt(index), mSelectedPosition, mSelectedRowId);
                setPressed(false);
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

/**
 * Callback method to be invoked when the touch mode changes.
 * @param isInTouchMode True if the view hierarchy is now in touch mode, false otherwise.
 * @hide
 */
    public void onTouchModeChanged(boolean isInTouchMode) {
        if(localLOGV)Log.v(TAG, "onTouchModeChanged("+isInTouchMode+")");
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
                if(layoutLOG)Log.v(TAG, "onTouchModeChanged("+isInTouchMode+") call layoutChildren()");
                layoutChildren();
            }
        }
    }

/**
 * Implement this method to handle touch screen motion events.
 * @param event The motion event.
 * @return True if the event was handled, false otherwise.
 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Give everything to the gesture detector
        if(mGestureDetector == null){
            mGestureDetector = new GestureDetector(this);
        }
        boolean retValue = mGestureDetector.onTouchEvent(event);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            // Helper method for lifted finger
            onUp();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            onCancel();
        }

        return retValue;

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

    /**@hide*/
    protected boolean isLastScrollStateEqualsTo(int chkState) {
        return mLastScrollState == chkState;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(this, newState);
                mLastScrollState = newState;
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

    void hideSelector() {
        if (mSelectedPosition != INVALID_POSITION) {
            mResurrectToPosition = mSelectedPosition;
            if (mNextSelectedPosition >= 0 && mNextSelectedPosition != mSelectedPosition) {
                mResurrectToPosition = mNextSelectedPosition;
            }
            if(localLOGV)Log.v(TAG, "hideSelector() call setSelectedPositionInt(INVALID_POSITION)");
            setSelectedPositionInt(INVALID_POSITION);
            setNextSelectedPositionInt(INVALID_POSITION);
            mSelectedOrnTop = 0;
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
        if(localLOGV)Log.v(TAG, "AbstractTableView > resurrectSelection()");
        final int childCount = getChildCount();

        if (childCount <= 0) {
            return false;
        }

        int selectedTop = 0;
        int selectedPos;
        int childrenTop = tableColleague.getOrnTop(mListPadding);
        int childrenBottom = tableColleague.getOrnBottom(this) - tableColleague.getOrnTop(this) - tableColleague.getOrnBottom(mListPadding);
        final int firstPosition = mFirstPosition;
        final int toPosition = mResurrectToPosition;
        boolean down = true;
        if(localLOGV)Log.v(TAG, "AbstractTableView > resurrectSelection(): mFirstPosition = "+mFirstPosition+", mResurrectToPosition = "+mResurrectToPosition);
        if (toPosition >= firstPosition && toPosition < firstPosition + childCount) {
            selectedPos = toPosition;

            final View selected = getChildAt(selectedPos - mFirstPosition);
            selectedTop = tableColleague.getOrnTop(selected);
            int selectedBottom = tableColleague.getOrnBottom(selected);

            // We are scrolled, don't get in the fade
            if (selectedTop < childrenTop) {
                selectedTop = childrenTop + tableColleague.getFadingEdgeLength();
            } else if (selectedBottom > childrenBottom) {
                selectedTop = childrenBottom - tableColleague.getOrnMeasuredHeight(selected)
                        - tableColleague.getFadingEdgeLength();
            }
        } else {
            if (toPosition < firstPosition) {
                // Default to selecting whatever is first
                selectedPos = firstPosition;
                for (int i = 0; i < childCount; i++) {
                    final View v = getChildAt(i);
                    final int top = tableColleague.getOrnTop(v);

                    if (i == 0) {
                        // Remember the position of the first item
                        selectedTop = top;
                        // See if we are scrolled at all
                        if (firstPosition > 0 || top < childrenTop) {
                            // If we are scrolled, don't select anything that is
                            // in the fade region
                            childrenTop += tableColleague.getFadingEdgeLength();
                        }
                    }
                    if (top >= childrenTop) {
                        // Found a view whose top is fully visisble
                        selectedPos = firstPosition + i;
                        selectedTop = top;
                        break;
                    }
                }
            } else {
                final int itemCount = mItemCount;
                down = false;
                selectedPos = firstPosition + childCount - 1;

                for (int i = childCount - 1; i >= 0; i--) {
                    final View v = getChildAt(i);
                    final int top = tableColleague.getOrnTop(v);
                    final int bottom = tableColleague.getOrnBottom(v);

                    if (i == childCount - 1) {
                        selectedTop = top;
                        if (firstPosition + childCount < itemCount || bottom > childrenBottom) {
                            childrenBottom -= tableColleague.getFadingEdgeLength();
                        }
                    }

                    if (bottom <= childrenBottom) {
                        selectedPos = firstPosition + i;
                        selectedTop = top;
                        break;
                    }
                }
            }
        }

        mResurrectToPosition = INVALID_POSITION;
        if(localLOGV)Log.v(TAG, "AbstractTableView resurectSelection() --> removeCallbacks(mFlingRunnable)");
        removeCallbacks(mFlingRunnable);
        mTouchMode = TOUCH_MODE_REST;
        clearScrollingCache();
        mSpecificTop = selectedTop;
        selectedPos = lookForSelectablePosition(selectedPos, down);
        if (selectedPos >= 0) {
            mLayoutMode = LAYOUT_SPECIFIC;
            setSelectionInt(selectedPos);
        }

        return selectedPos >= 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void handleDataChanged() {
        if(localLOGV)Log.v(TAG, "handleDataChanged()");
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
                    mLayoutMode = LAYOUT_FORCE_BOTTOM;
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

                                if (mSyncHeight == getHeight()) {
                                    // If we are at the same height as when we saved state, try
                                    // to restore the scroll position too.
                                    mLayoutMode = LAYOUT_SYNC;
                                } else {
                                    // We are not the same height as when the selection was saved, so
                                    // don't try to restore the exact position
                                    mLayoutMode = LAYOUT_SET_SELECTION;
                                }

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
                    //Add by Ahan 2011/12/28 for avoiding issues like RUNNYMEDE_ICS_35_S#205.
                    mSyncPosition = mSyncPosition >= 0 ? mSyncPosition : count + mSyncPosition;
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
        mLayoutMode = mStackFromBottom ? LAYOUT_FORCE_BOTTOM : LAYOUT_FORCE_TOP;
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
            final WindowManager win = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            final Display display = win.getDefaultDisplay();
            int screenHeight = display.getHeight();
            final int[] xy = new int[2];
            getLocationOnScreen(xy);
            int bottomGap = screenHeight - xy[1] - getHeight() + 20;
            mPopup.showAtLocation(this, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                    xy[0], bottomGap);
            // Make sure we get focus if we are showing the popup
            checkFocus();
        }
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
        if (mAdapter == null) throw new IllegalArgumentException("Adapter is null, please make sure adpater has been set !!!");

        if (!mTextFilterEnabled || !(mAdapter instanceof Filterable) || ((Filterable) mAdapter).getFilter() == null)
            return false;

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
            //createTextFilter(true);

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
 * Method: beforeTextChanged
 * @param s Text
 * @param start Start
 * @param count Character counts
 * @param after After
 * @hide
 */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * For our text watcher that associated with the text filter. Performs the actual
     * filtering as the text changes.
     */

/**
 * @param s Text
 * @param start Start
 * @param before Before
 * @param count character counts
 * @hide
 */
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mPopup != null) {
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
     * @param s The Editable string
     * @hide
     */
    public void afterTextChanged(Editable s) {
    }

/**
 * Notifies the end of a filtering operation.
 * @param count the number of values computed by the filter
 * @hide
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
        return new AbstractTableView.LayoutParams(getContext(), attrs);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AbstractTableView.LayoutParams;
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
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setCacheColorHint(int color) {
        if(localLOGV)Log.v(TAG, "AbstractTableView > , setCacheColorHint("+color+")");
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
            AbstractTableView.LayoutParams lp = (AbstractTableView.LayoutParams)child.getLayoutParams();
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
     * @see com.htc.view.table.AbstractTableView.RecycleBin
     * @see com.htc.view.table.AbstractTableView.RecyclerListener
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
     * @see com.htc.view.table.AbstractTableView.RecycleBin
     * @see com.htc.view.table.AbstractTableView#setRecyclerListener(com.htc.view.table.AbstractTableView.RecyclerListener)
     */
    public static interface RecyclerListener {
        /**
         * Indicates that the specified View was moved into the recycler's scrap heap.
         * The view is not displayed on screen any more and any expensive resource
         * associated with the view should be discarded.
         *
         * @param view The views which was moved into the recycle's scrap heap.
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
     * @see com.htc.view.table.AbstractTableView#setRecyclerListener(com.htc.view.table.AbstractTableView.RecyclerListener)
     * @see com.htc.view.table.AbstractTableView.RecyclerListener
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

        private static final int MAXIMUM_SCRAP_VIEW = 5;

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
            if(converViewLog)Log.v(TRACE_CONVERTVIEW, "setViewTypeCount(viewTypeCount), mCurrentScrap.size() = " + mCurrentScrap.size());
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
                if(converViewLog)Log.v(TRACE_CONVERTVIEW, "clear(), mCurrentScrap.size() = " + mCurrentScrap.size());
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
                AbstractTableView.LayoutParams lp = (AbstractTableView.LayoutParams)child.getLayoutParams();
                // Don't put header or footer views into the scrap heap
                if (lp != null && lp.viewType != AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                    // Note:  We do place AdapterView.ITEM_VIEW_TYPE_IGNORE in active views.
                    //        However, we will NOT place them into scrap views.
                    activeViews[i] = getChildAt(i);
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
            int index = position - ((mFirstActivePosition<0 && mAdapter!=null) ? mFirstActivePosition+mAdapter.getCount() : mFirstActivePosition);
            index = ((index<0 && mAdapter!=null) ? index+mAdapter.getCount() : index);
            final View[] activeViews = mActiveViews;
            if (index >= 0 && index < activeViews.length) {
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
                if(converViewLog)Log.v(TRACE_CONVERTVIEW, "getScrapView("+position+"), Before mCurrentScrap.size() = " + size);
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
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void addScrapView(View scrap) {
            AbstractTableView.LayoutParams lp = (AbstractTableView.LayoutParams) scrap.getLayoutParams();
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
                if(mCurrentScrap.size() < MAXIMUM_SCRAP_VIEW);
                mCurrentScrap.add(scrap);
                if(converViewLog)Log.v(TRACE_CONVERTVIEW, "addScrapView("+scrap+"), After add mCurrentScrap.size() = " + mCurrentScrap.size());

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
                    int whichScrap = ((AbstractTableView.LayoutParams)
                            victim.getLayoutParams()).viewType;

                    activeViews[i] = null;

                    if (whichScrap == AdapterView.ITEM_VIEW_TYPE_IGNORE) {
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

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract protected void initTableColleague();

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public TableLayoutParams getTableLayoutParams() {
        return mTableLayoutParams;
    }

    /**
     * To set TableLayout parameters.
     * @param startPosition a position which shall be displayed in the first cell of a new layout
     * @param layout TableLayoutParams
     */
    public void setTableLayoutParams(int startPosition, TableLayoutParams layout) {
        this.mRequestedStartPosition = startPosition;
        this.mTableLayoutParams = layout;
        this.initialWithScrollControl = layout.isInitialWithScrollControl();
        this.isScrollOverBoundary = layout.isScrollOverBoundary();
        initTableColleague();
    }

    /**
     * Change layout with playing animation
     *
     * @param startPosition
     * @param layout
     * @param outAnimation
     * @param inAnimation
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setTableLayoutParams(int startPosition, TableLayoutParams layout, Animation outAnimation, Animation inAnimation) {
        setTableLayoutParams(startPosition, layout);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public ScrollControl getScrollControl() {
        return scrollControl;
    }

    /**
     * Assigning a new ScrollControl shall take effect immediately
     * @param scrollControl The ScrollControl object related to this table
     */
    public void setScrollControl(ScrollControl scrollControl) {
        this.scrollControl = scrollControl;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public FocusSelection getFocusSelection() {
        return focusSelection;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setFocusSelection(FocusSelection focusSelection) {
        this.focusSelection = focusSelection;
    }


    // Add by Jason Chiu ***********************************************************
    // VARIABLES
    /**
     * If true, tableView can scroll over boundary.
     * If false, tableView cannot scroll over boundary.
     */
    boolean isScrollOverBoundary = true;
    /**
     * If this value is true, table view will scroll to the view that scroll control define.
     * If the value is false, the top of first visible child view will be at the top of table view.
     */
    private boolean initialWithScrollControl;
    /**
     * When fling runnable runs, it resets this to false. Any method along the
     * path until the end of its run() can set this to true to abort any
     * remaining fling. For example, if we've reached either the leftmost or
     * rightmost item, we will set this to true.
     */
    private boolean mShouldStopFling;

    /**
     * Helper for detecting touch gestures.
     */
    private GestureDetector mGestureDetector;

    /**
     * The position of the item that received the user's down touch.
     */
    private int mDownTouchPosition;

    /**
     * The view of the item that received the user's down touch.
     */
    private View mDownTouchView;

    /**
     * The currently selected item's child.
     */
    private View mSelectedChild;

    /**
     * spacing between items.
     */
    private int mSpacing = 0;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mMaxScrollOverhead = 80;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mScrollStartPos = -1;
    /**
     * Called when a touch event's action is MotionEvent.ACTION_UP.
     */
    void onUp() {
        if(layoutLOG)Log.v(TAG, "onUp call layoutChildren()");
        //s: tiffanie 20090601
        mScrollStartPos = -1;
        //e 20090601
        layoutChildren();
        if(localLOGV)Log.v(TAG, "onUp()");
        if (mFlingRunnable.mScroller.isFinished()) {
            scrollIntoSlots();
        }

        dispatchUnpress();
    }

    /**
     * Called when a touch event's action is MotionEvent.ACTION_CANCEL.
     */
    void onCancel() {
        if(localLOGV)Log.v(TAG, "onCancel()");
        onUp();
    }

/**
 * Notified when a tap occurs with the down MotionEvent that triggered it.
 * @param e The down motion event.
 * @return true if the event is consumed, else false
 * @hide
 */
    public boolean onDown(MotionEvent e) {
        if(localLOGV)Log.v(TAG, "onDown()");
        // Kill any existing fling/scroll
        if(localLOGV)Log.v(TAG, "AbstractTableView onDown() --> mFlingRunnable.stop(false)");
        mFlingRunnable.stop(false);
        mTouchMode = TOUCH_MODE_DOWN;
        // Get the item's view that was touched
        // TODO position problem
        mDownTouchPosition = pointToPosition((int) e.getX(), (int) e.getY());

        if (mDownTouchPosition >= 0) {
            // TODO position problem
            mDownTouchView = getChildAt(mDownTouchPosition - mFirstPosition);
            mDownTouchView.setPressed(true);
        }

        // Must return true to get matching events for this down event.
        return true;
    }

/**
 * Notified of a fling event when it occurs with the initial on down MotionEvent and the matching up MotionEvent.
 * @param e1 The first down motion event that started the fling.
 * @param e2 The move motion event that triggered the current onFling.
 * @param velocityX The velocity of this fling measured in pixels per second along the x axis
 * @param velocityY The velocity of this fling measured in pixels per second along the y axis.
 * @return true if the event is consumed, else false
 * @hide
 */
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        //s: tiffanie 20090601
        tableColleague.setCloseBouncing(true);
        //e
        if(localLOGV)Log.v(TAG, "onFling()");
        reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
        tableColleague.fling(mFlingRunnable, velocityX, velocityY);
        return true;
    }

/**
 * Notified when a long press occurs with the initial on down MotionEvent that trigged it.
 * @param e The initial on down motion event that started the longpress.
 * @hide
 */
    public void onLongPress(MotionEvent e) {
        if(localLOGV)Log.v(TAG, "onLongPress()");
        if(isLongClickable()){
//            final int motionPosition = mDownTouchPosition;
//            final View child = getChildAt(motionPosition - mFirstPosition);
//            if (child != null) {
//                final int longPressPosition = mDownTouchPosition;
//                final long longPressId = mAdapter.getItemId(mDownTouchPosition);

//                boolean handled = false;
//                if (!mDataChanged) {
//                    handled = performLongPress(child, longPressPosition, longPressId);
//                }
//                if (handled) {
//                    mTouchMode = TOUCH_MODE_REST;
//                    setPressed(false);
//                    child.setPressed(false);
//                } else {
//                    mTouchMode = TOUCH_MODE_DONE_WAITING;
//                }

//            }
            if (mDownTouchPosition < 0) {
                return;
            }
            long id = getItemIdAtPosition(mDownTouchPosition);
            dispatchLongPress(mDownTouchView, mDownTouchPosition, id);
        }
    }

    /**
     * Notified when a scroll occurs with the initial on down MotionEvent and the current move MotionEvent.
     * @param e1 The first down motion event that started the scrolling.
     * @param e2 The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last call to onScroll. This is NOT the distance between e1 and e2.
     * @param distanceY The distance along the Y axis that has been scrolled since the last call to onScroll. This is NOT the distance between e1 and e2.
     * @return true if the event is consumed, else false
     * @hide
     */
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mAdapter==null || getAdapter()==null) return true;
        if (!mCycle) {
            //s: tiffanie 20090601
            if (mScrollStartPos == -1) {
                mScrollStartPos = getCenterChildPosition();
            }

            if ((mScrollStartPos == 0 || mScrollStartPos == mAdapter.getCount() - 1) && (mAdapter.getCount() > 2)) {
                tableColleague.setCloseBouncing(false); // only can scroll overbound at first and last;
            } else if (mAdapter.getCount() == 2) {
                //tableColleague.setCloseBouncing(mScrollStartPos == getCenterChildPosition() ? false : true );
                tableColleague.setCloseBouncing(true);
            } else {
                tableColleague.setCloseBouncing(true);
            }
            //e 20090601
        }

        if (mTouchMode != TOUCH_MODE_SCROLL) {
            mTouchMode = TOUCH_MODE_SCROLL;
            setPressed(false);
            View motionView = this.getChildAt(mDownTouchPosition - mFirstPosition);

            if (motionView != null) {
                motionView.setPressed(false);
            }

            reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            // Time to start stealing events! Once we've stolen them, don't let anyone steal from us
            requestDisallowInterceptTouchEvent(true);
        }

        /*
         * Now's a good time to tell our parent to stop intercepting our events!
         * The user has moved more than the slop amount, since GestureDetector
         * ensures this before calling this method. Also, if a parent is more
         * interested in this touch's events than we are, it would have
         * intercepted them by now (for example, we can assume when a Gallery is
         * in the ListView, a vertical scroll would not end up in this method
         * since a ListView would have intercepted it by now).
         */
        getParent().requestDisallowInterceptTouchEvent(true);

        // Track the motion
        tableColleague.scrollWithConstrain(-1 * (int) distanceX, -1 * (int) distanceY, false);
        mDownTouchPosition = findMotionRow((int)e2.getY());

        return true;
    }

/**
 * The user has performed a down MotionEvent and not performed a move or up yet.
 * @param e The down motion event
 * @hide
 */
    public void onShowPress(MotionEvent e) {
        if(localLOGV)Log.v(TAG, "onShowPress()");
        if (mTouchMode == TOUCH_MODE_DOWN) {
            mTouchMode = TOUCH_MODE_TAP;
            final View child = getChildAt(mDownTouchPosition - mFirstPosition);
            if (child != null && !child.hasFocusable()) {
                mLayoutMode = LAYOUT_NORMAL;

                if (!mDataChanged) {
                    if(layoutLOG)Log.v(TAG, "onShowPress call layoutChildren()");
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

                    if (!longClickable) {
                        mTouchMode = TOUCH_MODE_DONE_WAITING;
                    }
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }
            }
        }
    }

/**
 * Notified when a tap occurs with the up MotionEvent that triggered it.
 * @param e The up motion event that completed the first tap
 * @return true if the event is consumed, else false
 * @hide
 */
    public boolean onSingleTapUp(MotionEvent e) {
        if(localLOGV)Log.v(TAG, "onSingleTapUp(), mDownTouchPosition = " + mDownTouchPosition);
        if (mDownTouchPosition >= 0) {

            // An item tap should make it selected, so scroll to this child.
            //scrollToChild(mDownTouchPosition - mFirstPosition);

            // Also pass the click so the client knows, if it wants to.
            //if (mDownTouchPosition == mSelectedPosition) {
                performItemClick(mDownTouchView, mDownTouchPosition, mAdapter
                        .getItemId(mDownTouchPosition));
            //}

            return true;
        }

        return false;
    }

    /**
     * Responsible for fling behavior. Use #startUsingVelocity(int) to
     * initiate a fling. Each frame of the fling is handled in {@link #run()}.
     * A FlingRunnable will keep re-posting itself until the fling is done.
     *
     */
    protected class FlingRunnable implements Runnable {
        /**
         * Tracks the decay of a fling scroll
         */
        private Scroller mScroller;

        /**
         * X value reported by mScroller on the previous fling
         */
        private int mLastFlingX;

        /**
         * Y value reported by mScroller on the previous fling
         */
        private int mLastFlingY;


        private int mScrollMode;

        private static final int SCROLL_MODE_FLING = 0;

        private static final int SCROLL_MODE_RETURN = 1;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public FlingRunnable() {
            if(localLOGV)Log.v(TAG, "new a FlingRunnable");
            mScroller = new Scroller(getContext());
        }

        /**
         * to initialize
         */
        private void startCommon() {
            if(localLOGV)Log.v(TAG, "FlingRunnable: startCommon");
            // Remove any pending flings
            removeCallbacks(this);
        }

        /**
         * to initiate a fling.
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void startUsingVelocity(int initialVelocityX, int initialVelocityY) {
            if(localLOGV)Log.v(TAG, "FlingRunnable: startUsingVelocity("+initialVelocityX +", "+initialVelocityY+")");
            if (initialVelocityX == 0 && initialVelocityY == 0) return;

            mScrollMode = SCROLL_MODE_FLING;
            startCommon();

            int initialX = initialVelocityX <= 0 ? Integer.MAX_VALUE : 0;
            int initialY = initialVelocityY <= 0 ? Integer.MAX_VALUE : 0;
            mLastFlingX = initialX;
            mLastFlingY = initialY;
            mScroller.fling(initialX, initialY, initialVelocityX, initialVelocityY,
                    0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            post(this);
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
        }

        /**
         * to initiate a scrolling.
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void startUsingDistance(int distanceX, int distanceY) {
            if(localLOGV)Log.v(TAG, "FlingRunnable: startUsingDistance("+distanceX +", "+distanceY+")");
            if (distanceX == 0 && distanceY == 0) return;

            mScrollMode = SCROLL_MODE_RETURN;
            startCommon();

            mLastFlingX = 0;
            mLastFlingY = 0;
            mScroller.startScroll(0, 0, -distanceX, -distanceY, getAnimationDurationAlongDistance((int)Math.abs(Math.sqrt(distanceX * distanceX + distanceY * distanceY))));
            post(this);

            reportScrollStateChange(OnScrollListener.SCROLL_STATE_INTOSLOTS);
        }

        private int getAnimationDurationAlongDistance(int distance){
            return Math.max(distance * 10, 200);
        }

        /**
         * Remove any pending flings and stop a fling.
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void stop(boolean scrollIntoSlots) {
            if(localLOGV)Log.v(TAG, "FlingRunnable: stop("+scrollIntoSlots+")");
            removeCallbacks(this);
            if(localLOGV)Log.v(TAG, "stop call endFling("+scrollIntoSlots+")");
            endFling(scrollIntoSlots);
        }

        /**
         * to stop a fling.
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void endFling(boolean scrollIntoSlots) {
            if(localLOGV)Log.v(TAG, "FlingRunnable: endFling("+scrollIntoSlots+")");
            /*
             * Force the scroller's status to finished (without setting its
             * position to the end)
             */
            mScroller.forceFinished(true);

            if (scrollIntoSlots && mScrollMode == SCROLL_MODE_FLING)
                scrollIntoSlots();
            else
                reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);


        }

        /**
         * {@inheritDoc}
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void run() {

            if (mItemCount == 0) {
                if(localLOGV)Log.v(TAG, "run call endFling(true)");
                endFling(true);
                return;
            }

            mShouldStopFling = false;

            final Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int x = scroller.getCurrX();
            final int y = scroller.getCurrY();

            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            int deltaX = mLastFlingX - x;
            int deltaY = mLastFlingY - y;

            // Pretend that each frame of a fling scroll is a touch scroll
            if (deltaX > 0) {
                // Don't fling more than 1 screen
                deltaX = Math.min(getWidth() - getPaddingLeft() - getPaddingRight() - 1, deltaX);
            } else {
                // Don't fling more than 1 screen
                deltaX = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), deltaX);
            }
            if(deltaY > 0){
                deltaY = Math.min(getHeight() - getPaddingTop() - getPaddingBottom() - 1, deltaY);
            }
            else{
                deltaY = Math.max(-(getHeight() - getPaddingBottom() - getPaddingTop() - 1), deltaY);
            }

            tableColleague.scrollWithConstrain(deltaX, deltaY, true);

            if (more && !mShouldStopFling) {
                mLastFlingX = x;
                mLastFlingY = y;
                post(this);
            } else {
                if(localLOGV)Log.v(TAG, "run call endFling("+true+")");
               endFling(true);
            }
        }

    }



    private void dispatchUnpress() {
        if(localLOGV)Log.v(TAG, "dispatchUnpress()");
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).setPressed(false);
        }

        setPressed(false);
    }

    private boolean dispatchLongPress(View view, int position, long id) {
        if(localLOGV)Log.v(TAG, "dispatchLongPress()");
        boolean handled = false;

        if (mOnItemLongClickListener != null) {
            handled = mOnItemLongClickListener.onItemLongClick(this, mDownTouchView,
                    mDownTouchPosition, id);
        }

        if (!handled) {
            mContextMenuInfo = new AdapterContextMenuInfo(view, position, id);
            handled = super.showContextMenuForChild(this);
        }
        if (handled) {
            mTouchMode = TOUCH_MODE_REST;
            setPressed(false);
//            child.setPressed(false);
        } else {
            mTouchMode = TOUCH_MODE_DONE_WAITING;
    }

        return handled;
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
    public void offsetChildrenLeftAndRight(int offset) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).offsetLeftAndRight(offset);
        }
    }

    /**
     * Offset the vertical location of all children of this view by the
     * specified number of pixels.
     *
     * @param offset the number of pixels to offset
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void offsetChildrenTopAndBottom(int offset) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).offsetTopAndBottom(offset);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public ListAdapter getAdapter() {
        // TODO Auto-generated method stub
        return null;
    }

/**
 * Sets the data behind this TableView
 * @param adapter The adapter which is responsible for maintaining the data backing this list and for producing a view to represent an item in that data set.
 * @hide
 */
    @Override
    public void setAdapter(ListAdapter adapter) {
        // TODO Auto-generated method stub

    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void setSelection(int position) {
        // TODO Auto-generated method stub

    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void detachViewsFromParent(int start, int count) {
        super.detachViewsFromParent(start, count);
    }

    /**
     * Looks for the child that is closest to the center and sets it as the
     * selected child.
     */
    void setSelectionToCenterChild() {

        View selView = mSelectedChild;
        if (mSelectedChild == null) return;

        int galleryCenter = tableColleague.getCenterOfTable();

        if (selView != null) {

            // Common case where the current selected position is correct
            if (tableColleague.getOrnTop(selView) <= galleryCenter && tableColleague.getOrnBottom(selView) >= galleryCenter) {
                return;
            }
        }

        int newPos = getCenterChildPosition();

        if (newPos != mSelectedPosition) {
            if(localLOGV)Log.v(TAG, "AbstractTableView > setSelectionToCenterChild() call setSelectedPositionInt("+newPos+")");
            setSelectedPositionInt(newPos);
            setNextSelectedPositionInt(newPos);
            checkSelectionChanged();
        }
    }

    /**
     * Call this method to get the index in the data array of the center child of the adapter
     * @return index in the adapter of center view
     */
    public int getCenterChildPosition(){
        if(tableColleague == null)
            return 0;
        int galleryCenter = tableColleague.getCenterOfTable();
        int closestEdgeDistance = Integer.MAX_VALUE;
        int newSelectedChildIndex = 0;
    int center_intTag = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {

            View child = getChildAt(i);

            if (tableColleague.getOrnTop(child) <= galleryCenter && tableColleague.getOrnBottom(child) >=  galleryCenter) {
                // This child is in the center
                newSelectedChildIndex = i;
        center_intTag = ((Integer)child.getTag()).intValue();
                break;
            }

            int childClosestEdgeDistance = Math.min(Math.abs(tableColleague.getOrnTop(child) - galleryCenter),
                    Math.abs(tableColleague.getOrnBottom(child) - galleryCenter));
            if (childClosestEdgeDistance < closestEdgeDistance) {
                closestEdgeDistance = childClosestEdgeDistance;
                newSelectedChildIndex = i;
            }
        }
        if(localLOGV)Log.v(TAG, "AbstractTableView>getCenterChildPosition() return" + (mFirstPosition + newSelectedChildIndex));
    if (mCycle)
        return center_intTag;
    else
            return (mFirstPosition + newSelectedChildIndex);
    }



/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void onFinishedMovement() {
        // We haven't been callbacking during the fling, so do it now
        //super.selectionChanged();

    }




/*
    private boolean scrollToChild(int childPosition) {
        View child = getChildAt(childPosition);

        if (child != null) {
            int distance = tableColleague.getCenterOfTable() - tableColleague.getCenterOfView(child);
            tableColleague.scrollAmount(mFlingRunnable, distance);
            return true;
        }

        return false;
    }
*/
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public View[] getAllVisibleViews(){
        View[] children = new View[getChildCount()];
        for(int i = 0; i < getChildCount(); i++){
            children[i] = getChildAt(i);
        }
        return children;
    }

    /**
     * Set the GestureDetector for special case. Ex: receive onScroll Event after long press.
     * If you don't set GestureDector, TableView will use default GestureDetector.
     * @param gestureDetector The special gestureDector.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setGestureDetector(GestureDetector gestureDetector){
        mGestureDetector = gestureDetector;
    }

    /**
     *
     * @param deltaY
     * @param removeSelector TODO
     * @param deltax
     */
    void trackMotionScroll(int deltaX, int deltaY) {
        if (getChildCount() == 0) {
//            Log.e("TableView","TableView has no children");
            return;
        }

        hideSelector();
        tableColleague.trackMotionScrollOrn(deltaX, deltaY);

        // Clear unused views
//        mRecycler.clear();
//        setSelectionToCenterChild();
        invalidate();
        invokeOnItemScrollListener();
    }

    /**
     * Adjust child views to the center of table
     */
    public void scrollIntoSlots(){
        tableColleague.scrollIntoSlots(mFlingRunnable);
    }
    // End add by Jason Chiu

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public VTableColleague getDefaultVTableColleague();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    abstract public HTableColleague getDefaultHTableColleague();

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setMaxScrollOverhead(int offset){
        mMaxScrollOverhead = offset;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getMaxScrollOverhead(){
        return mMaxScrollOverhead;
    }


    /**
     * Set the selector padding. The padding is calculated from child edges.
     * And once you set selectionPadding. You have to setSelectionPadding whenever you set selector.
     * It will not follow default rule anymore.
     * @param leftPadding The left padding of selector
     * @param topPadding The top padding of selector
     * @param rightPadding The right padding of selector
     * @param bottomPadding The bottom padding of selector
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setSelectionPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding){
        if(mSelectionPadding == null){
            mSelectionPadding = new SelectionPadding();
        }
        mSelectionPadding.unlock();
        mSelectionPadding.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        mSelectionPadding.lock();
    }

    private class SelectionPadding{
        private boolean lock;
        private int leftPadding;
        private int rightPadding;
        private int topPadding;
        private int bottomPadding;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public SelectionPadding(){
            lock = false;
            leftPadding = 0;
            rightPadding = 0;
            topPadding = 0;
            bottomPadding = 0;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void setPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding){
            if(!lock){
                this.leftPadding = leftPadding;
                this.topPadding = topPadding;
                this.rightPadding = rightPadding;
                this.bottomPadding = bottomPadding;
            }
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void lock(){
            lock = true;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void unlock(){
            lock = false;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getLeftPadding(){
            return leftPadding;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getTopPadding(){
            return topPadding;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getRightPadding(){
            return rightPadding;
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public int getBottomPadding(){
            return bottomPadding;
        }

    }

    private SelectionPadding mSelectionPadding;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setCycling(boolean b) {
        mCycle = b;
    }

    protected void rememberSyncState() {
        super.rememberSyncState();
        //Although the sync mode is first position, we sync the center child actually.
        //So we set the sync position to the center child instead of the first.
        if (mSyncMode == SYNC_FIRST_POSITION)
            mSyncPosition = getCenterChildPosition();
    }
}
