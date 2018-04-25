package com.htc.lib1.cc.view.table;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ListAdapter;
import android.content.res.Resources;
import android.content.res.Configuration;

import com.htc.lib1.cc.view.util.ProxyListAdapter;
import com.htc.lib1.cc.R;

/**
 * A view that shows items in two-dimensional scrolling grid. The items in the
 * grid come from the {@link AbstractTableView} associated with this view.
 */
public class TableView extends AbstractTableView {
    private static final boolean ahanLog = false;
    private static final String TAG = "AhanDebug";

    /**hide*/
    public interface CenterViewSetListener {
        void onCenterViewSet(ViewGroup vg, View view);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int NO_STRETCH = 0;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int STRETCH_SPACING = 1;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final int STRETCH_COLUMN_ROW_WIDTH = 2;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mRequestedHorizontalSpacing;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mRequestedVerticalSpacing;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mStretchMode = STRETCH_COLUMN_ROW_WIDTH;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mRequestedNumColumnRows;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected int mRequestedOrnWidth;

    private final Rect mTempRect = new Rect();

    private int mTableChildHeight = Integer.MIN_VALUE;

    private CenterViewSetListener mCenterViewListener;

    private int mTimePickTextViewHeight;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public TableView(Context context) {
        this(context, null);
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
    public TableView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
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
    public TableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TableView, defStyle, 0);

        int hSpacing = a.getDimensionPixelOffset(R.styleable.TableView_android_horizontalSpacing, 0);
        setHorizontalSpacing(hSpacing);

        int vSpacing = a.getDimensionPixelOffset(R.styleable.TableView_android_verticalSpacing, 0);
        setVerticalSpacing(vSpacing);

        int index = a.getInt(R.styleable.TableView_android_stretchMode, STRETCH_COLUMN_ROW_WIDTH);
        if (index >= 0) setStretchMode(index);

        int columnWidth = a.getDimensionPixelOffset(R.styleable.TableView_android_columnWidth, -1);
        if (columnWidth > 0) setColumnRowWidth(columnWidth);

        int numColumns = a.getInt(R.styleable.TableView_android_numColumns, 1);
        setNumColumnRows(numColumns);

        index = a.getInt(R.styleable.TableView_android_gravity, -1);
        if (index >= 0) setGravity(index);

        a.recycle();

        mTimePickTextViewHeight = getContext().getResources().getDimensionPixelSize(R.dimen.time_pick_text_view_height);
    }

    /**@hide*/
    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Sets the data behind this GridView.
     * @param adapter the adapter providing the grid's data
     * @hide
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        if (null != mAdapter) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        resetList();
        mRecycler.clear();
        mAdapter = adapter;

        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;

        if (mAdapter != null) {
            mOldItemCount = mItemCount;
            mItemCount = mAdapter.getCount();
            mDataChanged = true;
            checkFocus();

            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);

            mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());

            int position;
            if (mStackFromBottom) {
                position = lookForSelectablePosition(mItemCount - 1, false);
            } else {
                position = lookForSelectablePosition(0, true);
            }

            setSelectedPositionInt(position);
            setNextSelectedPositionInt(position);
            checkSelectionChanged();
        } else {
            checkFocus();
            // Nothing selected
            checkSelectionChanged();
        }

        requestLayout();
    }

    /**@hide*/
    public void setCenterViewListener(CenterViewSetListener listener) {
        mCenterViewListener = listener;
    }

    /**
     * Currently, this method is only for data relocation mechanism.
     * It's subject to extend usages.
     * @param adapter
     * @hide
     */
    public void enableProxyAdapter(ProxyListAdapter adapter) {
        adapter.setTarget(this.mAdapter);
        this.mAdapter = adapter;
        this.invalidateViews();
    }

    /**@hide*/
    public void disableProxyAdapter() {
        if (this.mAdapter instanceof ProxyListAdapter) {
            this.mAdapter = ((ProxyListAdapter)this.mAdapter).getTarget();
            this.invalidateViews();
        }
    }

    /**@hide*/
    @Override
    protected int lookForSelectablePosition(int position, boolean lookDown) {
        final ListAdapter adapter = mAdapter;
        if (adapter == null || isInTouchMode()) {
            return INVALID_POSITION;
        }

        if (position < 0 || position >= mItemCount) {
            return INVALID_POSITION;
        }
        return position;
    }

    @Override
    int findMotionRow(int y) {
        final int childCount = getChildCount();
        if (childCount > 0) {

            final int numColumns = tableColleague.mNumColumnRows;
            if (!mStackFromBottom) {
                for (int i = 0; i < childCount; i += numColumns) {
                    if (y <= getChildAt(i).getBottom()) {
                        return mFirstPosition + i;
                    }
                }
            } else {
                for (int i = childCount - 1; i >= 0; i -= numColumns) {
                    if (y >= getChildAt(i).getTop()) {
                        return mFirstPosition + i;
                    }
                }
            }

            return mFirstPosition + childCount - 1;
        }
        return INVALID_POSITION;
    }

    /**
     * Called to determine the size requirements for this view and all of its children.
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec
     * @param heightMeasureSpec vertical space requirements as imposed by the parent. The requirements are encoded with View.MeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Sets up mListPadding
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (tableColleague == null) tableColleague = getDefaultVTableColleague();
        tableColleague.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHasMeasured = true;
    }

    /**
     * Method: attachLayoutAnimationParameters
     * @param child Child
     * @param params LayoutParams
     * @param index Index
     * @param count Count
     * @hide
     */
    @Override
    protected void attachLayoutAnimationParameters(View child,
            ViewGroup.LayoutParams params, int index, int count) {

        GridLayoutAnimationController.AnimationParameters animationParams =
                (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

        if (animationParams == null) {
            animationParams = new GridLayoutAnimationController.AnimationParameters();
            params.layoutAnimationParameters = animationParams;
        }

        animationParams.count = count;
        animationParams.index = index;
        animationParams.columnsCount = tableColleague.mNumColumnRows;
        animationParams.rowsCount = count / tableColleague.mNumColumnRows;

        if (!mStackFromBottom) {
            animationParams.column = index % tableColleague.mNumColumnRows;
            animationParams.row = index / tableColleague.mNumColumnRows;
        } else {
            final int invertedIndex = count - 1 - index;

            animationParams.column = tableColleague.mNumColumnRows - 1 - (invertedIndex % tableColleague.mNumColumnRows);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / tableColleague.mNumColumnRows;
        }
    }

    // [Ahan][2012/09/07]
    //TODO: Portrait and Landscape adjustment?
    /**
     * Gets the height of the child view of this TableView.
     * @return the height of the child view of this TableView
     */
    protected int getTableChildHeight() {
        int childCount = this.getChildCount();

        //If the children already exist, we just get the height of the first child.
        //Else we get the value from dimen res.
        if (!(mTableChildHeight > 0)) {
            if (childCount > 0) {
                mTableChildHeight = (tableColleague != null ? tableColleague.getOrnHeight(getChildAt(0)) : 0);
            } else {
                mTableChildHeight = mTimePickTextViewHeight;
            }
        }

        return mTableChildHeight;
    }

    /**
     * To set the child views' height of this table.
     * @param height The height of child views.
     * @hide
     */
    protected void setTableChildHeight(int height) {
        if (height > 0) mTableChildHeight = height;
    }
    // [Ahan][2012/09/07]

    // [Ahan][2012/10/02][A method to print mLayoutMode for debugging usage]
    private void printLayoutModeForDebug(int mode) {
        String ahanDebugStr = "";
        switch (mode) {
            case LAYOUT_SET_SELECTION: { ahanDebugStr = "LAYOUT_SET_SELECTION"; break; }
            case LAYOUT_FORCE_TOP: { ahanDebugStr = "LAYOUT_FORCE_TOP"; break; }
            case LAYOUT_FORCE_BOTTOM: { ahanDebugStr = "LAYOUT_FORCE_BOTTOM"; break; }
            case LAYOUT_SPECIFIC: { ahanDebugStr = "LAYOUT_SPECIFIC"; break; }
            case LAYOUT_SYNC: { ahanDebugStr = "LAYOUT_SYNC"; break; }
            case LAYOUT_MOVE_SELECTION: { ahanDebugStr="LAYOUT_MOVE_SELECTION"; break; }
            case LAYOUT_MOVE_SELECTION_CENTER: { ahanDebugStr="LAYOUT_MOVE_SELECTION_CENTER"; break; }
            default: { ahanDebugStr = "default"; break; }
        }
        android.util.Log.e(TAG,"[0x"+Integer.toHexString(hashCode())+"]["+getKeyOfTableView()+"][layoutChildren] LayoutMode => "+ahanDebugStr);
    }
    // [Ahan][2012/10/02]

    /**@hide*/
    @Override
    protected void layoutChildren() {
        if (ahanLog) printLayoutModeForDebug(mLayoutMode); //print layout mode if necessary

        final boolean blockLayoutRequests = mBlockLayoutRequests;
        if (!blockLayoutRequests) {
            mBlockLayoutRequests = true;
        }

        try {
            super.layoutChildren();

            invalidate();

            if (mAdapter == null) {
                resetList();
                invokeOnItemScrollListener();
                return;
            }

            final int childrenOrnTop = computeFirstTopPosition(-1, -1); /*tableCenter - childrenCenter - childrenHeight;*/
            final int childrenOrnBottom = getHeight() + Math.abs(childrenOrnTop); /*tableCenter + childrenCenter + childrenHeight;*/

            int childCount = getChildCount();
            int index;
            int delta = 0;
            int childHeight = 0;

            View sel;
            View oldSel = null;
            View oldFirst = null;
            View newSel = null;

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
            case LAYOUT_SPECIFIC:
            case LAYOUT_SYNC:
                break;
            case LAYOUT_MOVE_SELECTION:
                if (mNextSelectedPosition >= 0) {
                    delta = mNextSelectedPosition - mSelectedPosition;
                }
                break;
            case LAYOUT_MOVE_SELECTION_CENTER:
                childHeight = getTableChildHeight();
                break;
            default:
                // Remember the previously selected view
                // modified by Ahan 2012/03/01 for primo_dd issue#799
                index = mSelectedPosition - (mFirstPosition<0 ? (mFirstPosition+mAdapter.getCount()) : mFirstPosition);
                index = (index<0 ? (index+mAdapter.getCount()) : index);

                if (index >= 0 && index < childCount) {
                    oldSel = getChildAt(index);
                }

                // Remember the previous first child
                oldFirst = getChildAt(0);
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

            if (dataChanged) {
                for (int i = 0; i < childCount; i++) {
                    recycleBin.addScrapView(getChildAt(i));
                }
            } else {
                recycleBin.fillActiveViews(childCount, firstPosition);
            }

            // Clear out old views
            //removeAllViewsInLayout();
            detachAllViewsFromParent();

            if (ahanLog) printLayoutModeForDebug(mLayoutMode); //print layout mode if necessary

            switch (mLayoutMode) {
            case LAYOUT_SET_SELECTION:
                if (newSel != null) {
                    sel = tableColleague.fillFromSelection(tableColleague.getOrnTop(newSel), childrenOrnTop, childrenOrnBottom);
                } else {
                    sel = tableColleague.fillSelection(childrenOrnTop, childrenOrnBottom);
                }
                break;
            case LAYOUT_FORCE_TOP:
                mFirstPosition = 0;
                setSelectedPositionInt(0);
                sel = tableColleague.fillFromTop(childrenOrnTop);
                break;
            case LAYOUT_FORCE_BOTTOM:
                setSelectedPositionInt(mItemCount - 1);
                sel = tableColleague.fillUp(mItemCount - 1, childrenOrnBottom);
                break;
            case LAYOUT_SPECIFIC:
                sel = tableColleague.fillSpecific(mSelectedPosition, mSpecificTop);
                break;
            case LAYOUT_SYNC:
                mSpecificTop = (mNeedResetFirstTop ? computeFirstTopPosition(mSpecificTop, mLayoutMode) : mSpecificTop);
                sel = tableColleague.fillSpecific(mSyncPosition, mSpecificTop);
                if (isLastScrollStateEqualsTo(OnScrollListener.SCROLL_STATE_IDLE)) callbackCenterViewSetListener(sel);
                break;
            case LAYOUT_MOVE_SELECTION:
                // Move the selection relative to its old position
                sel = tableColleague.moveSelection(delta, childrenOrnTop, childrenOrnBottom);
                break;
            case LAYOUT_MOVE_SELECTION_CENTER:
                sel = tableColleague.moveSelectionCenter(mSelectedPosition, childrenOrnTop, childrenOrnBottom, childHeight, mPercentage);
                break;
            default:
                if (childCount == 0) {
                    if (!mStackFromBottom) {
                        setSelectedPositionInt(0);
                        sel = tableColleague.fillFromTop(childrenOrnTop);
                    } else {
                        final int last = mItemCount - 1;
                        setSelectedPositionInt(last);
                        sel = tableColleague.fillFromBottom(last, childrenOrnBottom);
                    }
                } else {
                    if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {
                        int top = (oldSel == null ? childrenOrnTop : tableColleague.getOrnTop(oldSel));
                        top = (mNeedResetFirstTop ? computeFirstTopPosition(top, mLayoutMode) : top);
                        sel = tableColleague.fillSpecific(mSelectedPosition, top);
                    } else if (mFirstPosition < mItemCount)  {
                        int firstTop = (oldFirst == null ? childrenOrnTop : tableColleague.getOrnTop(oldFirst));
                        sel = tableColleague.fillSpecific(mFirstPosition, (mNeedResetFirstTop ? computeFirstTopPosition(firstTop, mLayoutMode) : firstTop));
                    } else {
                        sel = tableColleague.fillSpecific(0, childrenOrnTop);
                    }
                }
                break;
            }

            // Flush any cached views that did not get reused above
            recycleBin.scrapActiveViews();

            if (sel != null) {
               positionSelector(sel);
               mSelectedOrnTop = tableColleague.getOrnTop(sel);
            } else {
               mSelectedOrnTop = 0;
               mSelectorRect.setEmpty();
            }

            mLayoutMode = LAYOUT_NORMAL;
            mDataChanged = false;
            mNeedSync = false;
            mNeedResetFirstTop = false;
            setNextSelectedPositionInt(mSelectedPosition);

            //updateScrollIndicators();

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

    /**@hide*/
    void callbackCenterViewSetListener(View view) {
        if (this.mCenterViewListener != null)
            this.mCenterViewListener.onCenterViewSet(this, (view==null?getCenterView():view));
    }

    /**@hide*/
    public View getCenterView() {
        View retView = null;
        int tableCenter = (getTop() + getBottom()) / 2;

        for (int i = 0; i<getChildCount(); i++) {
            View tmp = getChildAt(i);
            //if ((tmp != null) && (Math.abs((tmp.getTop()+tmp.getBottom())/2 - tableCenter) < 10)) {
            if (tmp!=null && (tmp.getTop()<tableCenter && tmp.getBottom()>tableCenter)) {
                retView = tmp;
                break;
            }
        }

        return retView;
    }

    //[Ahan][2012/12/19][Used to determine if onMeasure has executed]
    private boolean mHasMeasured;

    //[Ahan][2012/12/19][If setCenterView has be invoked before onMeasure, it will post setCenterView to ensure it is invoked behind onMeasure]
    private class SetCenterRunnable implements Runnable {
        private final int TTL = 3;
        private int mTtl = TTL;
        private int storedPosition, storedPercentage;

        public SetCenterRunnable(int position, int percentage) {
            storedPosition = position;
            storedPercentage = percentage;
        }

        public void run() {
            if (!mHasMeasured) {
                if (mTtl-- > 0) post(this);
            } else {
                setCenterView(storedPosition, getHeight(), storedPercentage);
            }
        }
    };

    //[Ahan][2012/10/30][Sometimes we need re-compute the top position after rotation cause the height of TableView has been changed]
    private int computeFirstTopPosition(int oriTop, int layoutMode) {
        int childHeight = getTableChildHeight();
        int childCenter = childHeight / 2;
        int tableCenter = getHeight() / 2;
        int result = oriTop;

        switch (layoutMode) {
            case LAYOUT_SYNC:
                //When this mode, the sync location should be the center of the table.
                result = tableCenter - childCenter;
                break;
            default:
                //We have to consider two cases: Normal(if true) and AM/PM case(if false).
                result = oriTop<0 ? tableCenter-childCenter-childHeight : tableCenter-childCenter;
        }

        return (result);
    }

    //Use this flag to check if we need re-compute the top position of the first child
    private boolean mNeedResetFirstTop = false;

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     * @hide
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mNeedResetFirstTop = true;
    }

    /**
     * Sets the currently selected item
     *
     * @param position Index (starting at 0) of the data item to be selected.
     *
     * If in touch mode, the item will not be selected but it will still be positioned
     * appropriately.
     * @hide
     */
    @Override
    public void setSelection(int position) {
        if (!isInTouchMode()) {
            setNextSelectedPositionInt(position);
        } else {
            mResurrectToPosition = position;
        }
        mLayoutMode = LAYOUT_SET_SELECTION;
        requestLayout();
    }

    /**
     * Makes the item at the supplied position selected.
     *
     * @param position the position of the new selection
     * @hide
     */
    @Override
    protected void setSelectionInt(int position) {
        mBlockLayoutRequests = true;
        setNextSelectedPositionInt(position);
        layoutChildren();
        mBlockLayoutRequests = false;
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
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_SPACE:
                    case KeyEvent.KEYCODE_ENTER:
                        resurrectSelection();
                        return true;
                }
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    handled = arrowScroll(FOCUS_LEFT);
                    break;


                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    handled = arrowScroll(FOCUS_RIGHT);
                    break;

                case KeyEvent.KEYCODE_DPAD_UP:
                    if (!event.isAltPressed()) {
                        handled = arrowScroll(FOCUS_UP);

                    } else {
                        handled = fullScroll(FOCUS_UP);
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (!event.isAltPressed()) {
                        handled = arrowScroll(FOCUS_DOWN);
                    } else {
                        handled = fullScroll(FOCUS_DOWN);
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER: {
                    if (getChildCount() > 0 && event.getRepeatCount() == 0) {
                        keyPressed();
                    }

                    return true;
                }

                case KeyEvent.KEYCODE_SPACE:
                    if (mPopup == null || !mPopup.isShowing()) {
                        if (!event.isShiftPressed()) {
                            handled = pageScroll(FOCUS_DOWN);
                        } else {
                            handled = pageScroll(FOCUS_UP);
                        }
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
                default:
                    return false;
            }
        }
    }

    /**
     * Scrolls up or down by the number of items currently present on screen.
     *
     * @param direction either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     * @return whether selection was moved
     */
    boolean pageScroll(int direction) {
        int nextPage = -1;

        if (direction == FOCUS_UP) {
            nextPage = Math.max(0, mSelectedPosition - getChildCount() - 1);
        } else if (direction == FOCUS_DOWN) {
            nextPage = Math.min(mItemCount - 1, mSelectedPosition + getChildCount() - 1);
        }

        if (nextPage >= 0) {
            setSelectionInt(nextPage);
            return true;
        }

        return false;
    }

    /**
     * Go to the last or first item if possible.
     *
     * @param direction either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}.
     *
     * @return Whether selection was moved.
     */
    boolean fullScroll(int direction) {
        boolean moved = false;
        if (direction == FOCUS_UP) {
            mLayoutMode = LAYOUT_SET_SELECTION;
            setSelectionInt(0);
            moved = true;
        } else if (direction == FOCUS_DOWN) {
            mLayoutMode = LAYOUT_SET_SELECTION;
            setSelectionInt(mItemCount - 1);
            moved = true;
        }

        return moved;
    }

    /**
     * Scrolls to the next or previous item, horizontally or vertically.
     *
     * @param direction either {@link View#FOCUS_LEFT}, {@link View#FOCUS_RIGHT},
     *        {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     *
     * @return whether selection was moved
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected boolean arrowScroll(int direction) {
        final int selectedPosition = mSelectedPosition;
        final int numColumns = tableColleague.mNumColumnRows;

        int startOfRowPos;
        int endOfRowPos;

        boolean moved = false;

        if (!mStackFromBottom) {
            startOfRowPos = (selectedPosition / numColumns) * numColumns;
            endOfRowPos = Math.min(startOfRowPos + numColumns - 1, mItemCount - 1);
        } else {
            final int invertedSelection = mItemCount - 1 - selectedPosition;
            endOfRowPos = mItemCount - 1 - (invertedSelection / numColumns) * numColumns;
            startOfRowPos = Math.max(0, endOfRowPos - numColumns + 1);
        }

        moved = tableColleague.findAndSetSelecionInt(direction, startOfRowPos, endOfRowPos, selectedPosition);


        if (moved) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
        }

        return moved;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        int closestChildIndex = -1;
        if (gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(getScrollX(), getScrollY());

            // figure out which item should be selected based on previously
            // focused rect
            Rect otherRect = mTempRect;
            int minDistance = Integer.MAX_VALUE;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                // only consider view's on appropriate edge of grid
                if (!isCandidateSelection(i, direction)) {
                    continue;
                }

                final View other = getChildAt(i);
                other.getDrawingRect(otherRect);
                offsetDescendantRectToMyCoords(other, otherRect);
                int distance = getDistance(previouslyFocusedRect, otherRect, direction);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestChildIndex = i;
                }
            }
        }

        if (closestChildIndex >= 0) {
            setSelection(closestChildIndex + mFirstPosition);
        } else {
            requestLayout();
        }
    }

    /**
     * Is childIndex a candidate for next focus given the direction the focus
     * change is coming from?
     * @param childIndex The index to check.
     * @param direction The direction, one of
     *        {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}
     * @return Whether childIndex is a candidate.
     */
    private boolean isCandidateSelection(int childIndex, int direction) {
        final int count = getChildCount();
        final int invertedIndex = count - 1 - childIndex;

        int rowStart;
        int rowEnd;

        if (!mStackFromBottom) {
            rowStart = childIndex - (childIndex % tableColleague.mNumColumnRows);
            rowEnd = Math.max(rowStart + tableColleague.mNumColumnRows - 1, count);
        } else {
            rowEnd = count - 1 - (invertedIndex - (invertedIndex % tableColleague.mNumColumnRows));
            rowStart = Math.max(0, rowEnd - tableColleague.mNumColumnRows + 1);
        }

        switch (direction) {
            case View.FOCUS_RIGHT:
                // coming from left, selection is only valid if it is on left
                // edge
                return childIndex == rowStart;
            case View.FOCUS_DOWN:
                // coming from top; only valid if in top row
                return rowStart == 0;
            case View.FOCUS_LEFT:
                // coming from right, must be on right edge
                return childIndex == rowEnd;
            case View.FOCUS_UP:
                // coming from bottom, need to be in last row
                return rowEnd == count - 1;
            case View.FOCUS_FORWARD:
                // coming from top-left, need to be first in top row
                return childIndex == rowStart && rowStart == 0;
            case View.FOCUS_BACKWARD:
                // coming from bottom-right, need to be last in bottom row
                return childIndex == rowEnd && rowEnd == count - 1;
            default:
                throw new IllegalArgumentException("direction must be one of "
                        + "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, "
                        + "FOCUS_FORWARD, FOCUS_BACKWARD}. direction = " + direction);
        }
    }

    /**
     * Describes how the child views are horizontally aligned. Defaults to Gravity.LEFT
     *
     * @param gravity the gravity to apply to this grid's children
     * @hide
     */
    public void setGravity(int gravity) {
        if (tableColleague.mGravity != gravity) {
            tableColleague.mGravity = gravity;
            requestLayoutIfNecessary();
        }
    }

    /**
     * Set the amount of horizontal (x) spacing to place between each item
     * in the grid.
     *
     * @param horizontalSpacing The amount of horizontal space between items,
     * in pixels.
     *
     */
    public void setHorizontalSpacing(int horizontalSpacing) {
        mRequestedHorizontalSpacing = horizontalSpacing;
    }


    /**
     * Set the amount of vertical (y) spacing to place between each item
     * in the grid.
     *
     * @param verticalSpacing The amount of vertical space between items,
     * in pixels.
     *
     */
    public void setVerticalSpacing(int verticalSpacing) {
        mRequestedVerticalSpacing = verticalSpacing;
    }

    /**
     * Control how items are stretched to fill their space.
     *
     * @param stretchMode Either {@link #NO_STRETCH},
     * {@link #STRETCH_SPACING}, or {@link #STRETCH_COLUMN_ROW_WIDTH}.
     *
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setStretchMode(int stretchMode) {
        if (stretchMode != mStretchMode) {
            mStretchMode = stretchMode;
            requestLayoutIfNecessary();
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getStretchMode() {
        return mStretchMode;
    }

    /**
     * Set the width of columns in the grid.
     *
     * @param columnRowWidth The column width, in pixels.
     *
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setColumnRowWidth(int columnRowWidth) {
        mRequestedOrnWidth = columnRowWidth;
    }

    /**
     * Set the number of fixed columns or rows.
     * @param numColumnRows It depends on 'scrollOrientation'.
     *        If 'scrollDirection' is VERTICAL, it's the number of fixed columns ;
     *        otherwise, it's the number of rows.
     */
    public void setNumColumnRows(int numColumnRows) {
        mRequestedNumColumnRows = numColumnRows;
        if(tableColleague != null){
            tableColleague.setNumColumnRows(mRequestedNumColumnRows);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getNumColumnRows() {
        return mRequestedNumColumnRows;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void initTableColleague() {
        if (this.mTableLayoutParams.getOrientation() == TableLayoutParams.VERTICAL) {
            this.tableColleague = getDefaultVTableColleague();
        } else {
            this.tableColleague = getDefaultHTableColleague();
        }
        tableColleague.setNumColumnRows(this.mRequestedNumColumnRows);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public VTableColleague getDefaultVTableColleague() {
        return new VTableColleague(this);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public HTableColleague getDefaultHTableColleague() {
        return new HTableColleague(this);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void attachViewToParent(View child, int index, LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
        super.addViewInLayout(child, index, params, preventRequestLayout);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void cleanupLayoutState(View child) {
        super.cleanupLayoutState(child);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void setMeasuredDimensionEx(int measuredWidth, int measuredHeight) {
        super.setMeasuredDimension(measuredWidth, measuredHeight);
    }

    /**
     * This is only used for setCenterView.
     * User can tell tableView height.
     */
    int mTableViewOrnHeight;

    /**
     * This is only used for {@link #setCenterView(int, int, int)}
     */
    private int mPercentage = 50;

    /**
     * This is only used for setCenterView.
     * User can tell tableView height.
     */
    boolean isSetTableViewHeight = false;

    /**
     * Makes the specific view to be placed in the center of this TableView
     * @param position index in the adapter for the view which should be placed in the center of the table
     */
    public void setCenterView(int position){
        setCenterView(position, getHeight());
    }

    /**
     * Makes the specific view to be placed in the center of this TableView
     * @param position index in the adapter for the view which should be placed in the center of the table
     * @param parentViewHeightOrWidth specify the height of the table
     */
    public void setCenterView(int position, int parentViewHeightOrWidth) {
        setCenterView(position, parentViewHeightOrWidth, 50);
    }

    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public void setCenterView(int position, int parentViewHeightOrWidth, int percentage) {
        if (position < 0 || position >= getCount()) return;
        percentage = 50;

        //[Ahan][2012/12/19][If setCenterView has be invoked before onMeasure, we should post it to ensure onMeasure will be executed first]
        if (!mHasMeasured) {
            post(new SetCenterRunnable(position, percentage));
            return;
        }

        mPercentage = percentage;
        isSetTableViewHeight = true;
        //Clean notify data set.
        layoutChildren();
        mLayoutMode = LAYOUT_MOVE_SELECTION_CENTER;
        mTableViewOrnHeight = parentViewHeightOrWidth;
        setSelectionInt(position);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeVerticalScrollExtent() {
        final int count = getChildCount();
        if (count > 0) {
            final int numColumns = getNumColumnRows();
            final int rowCount = (count + numColumns - 1) / numColumns;

            int extent = rowCount * 100;

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
        }
        return 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeVerticalScrollOffset() {
        if (mFirstPosition >= 0 && getChildCount() > 0) {
            final View view = getChildAt(0);
            final int top = view.getTop();
            int height = view.getHeight();
            if (height > 0) {
                final int whichRow = mFirstPosition / getNumColumnRows();
                return Math.max(whichRow * 100 - (top * 100) / height, 0);
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
        // TODO: Account for vertical spacing too
        final int numColumns = getNumColumnRows();
        final int rowCount = (mItemCount + numColumns - 1) / numColumns;
        return Math.max(rowCount * 100, 0);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeHorizontalScrollExtent() {
        final int count = getChildCount();
        if (count > 0) {
            final int numColumns = getNumColumnRows();
            final int rowCount = (count + numColumns - 1) / numColumns;

            int extent = rowCount * 100;

            View view = getChildAt(0);
            final int left = view.getLeft();
            int width = view.getWidth();
            if (width > 0) {
                extent += (left * 100) / width;
            }

            view = getChildAt(count - 1);
            final int right = view.getRight();
            width = view.getHeight();
            if (width > 0) {
                extent -= ((right - getHeight()) * 100) / width;
            }

            return extent;
        }
        return 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected int computeHorizontalScrollOffset() {
        if (mFirstPosition >= 0 && getChildCount() > 0) {
            final View view = getChildAt(0);
            final int left = view.getLeft();
            int height = view.getWidth();
            if (height > 0) {
                final int whichRow = mFirstPosition / getNumColumnRows();
                return Math.max(whichRow * 100 - (left * 100) / height, 0);
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
        // TODO: Account for vertical spacing too
        final int numColumns = getNumColumnRows();
        final int rowCount = (mItemCount + numColumns - 1) / numColumns;
        return Math.max(rowCount * 100, 0);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setRepeatEnable(boolean b) {
        this.setCycling(true);
        this.tableColleague.setRepeatEnable(b);
        this.tableColleague.setCloseBouncing(b);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setMultiStop(boolean b) {
        this.tableColleague.setMultiStop(b);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean setMultiStopDistance(int d) {
        return this.tableColleague.setMultiStopDistance(d);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean setMultiStopDistance(int[] d) {
        return this.tableColleague.setMultiStopDistance(d);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setStopExcept(int i) {
        this.tableColleague.setStopExcept(i);
    }
}

