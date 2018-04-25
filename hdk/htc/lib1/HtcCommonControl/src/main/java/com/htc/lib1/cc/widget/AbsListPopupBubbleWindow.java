
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;
import android.view.View.OnTouchListener;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

import com.htc.lib1.cc.R;

/**
 * @hide
 */
public abstract class AbsListPopupBubbleWindow extends PopupBubbleWindow{
    protected static final int EXPAND_LIST_TIMEOUT = 250;
    protected static final float WINDOW_MINMUM_WIDTH_RATIO = 0.7f;
    protected Context mContext;
    protected String TAG = "AbsListPopupBubbleWindow";

    // add for Note
    protected int mItemHeight = 0;
    protected int mItemCount = 0;
    protected boolean mIsWidthHeightFixed = false;
    protected int mMarginM2;
    protected int mPopupMinWidth, mPopupMaxWidth;
    protected int mDropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    protected int mDropDownWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    protected int mDropDownHorizontalOffset;
    protected int mDropDownVerticalOffset;

    protected boolean mDropDownAlwaysVisible = false;
    protected boolean mForceIgnoreOutsideTouch = false;
    int mListItemExpandMaximum = Integer.MAX_VALUE;

    protected View mPromptView;
    protected int mPromptPosition = POSITION_PROMPT_ABOVE;

    protected DataSetObserver mObserver;

    protected View mDropDownAnchorView;

    protected Drawable mDropDownListHighlight;

    protected final ResizePopupRunnable mResizePopupRunnable = new ResizePopupRunnable();
    protected final PopupTouchInterceptor mTouchInterceptor = new PopupTouchInterceptor();
    protected final PopupScrollListener mScrollListener = new PopupScrollListener();
    protected final ListSelectorHider mHideSelector = new ListSelectorHider();

    private OnTouchListener mCustomizedTouchInterceptor;

    protected AdapterView.OnItemClickListener mItemClickListener;
    protected AdapterView.OnItemSelectedListener mItemSelectedListener;

    protected Runnable mShowDropDownRunnable;

    protected Handler mHandler = new Handler();

    protected Rect mTempRect = new Rect();

    protected boolean mModal;

    /**
     * The provided prompt view should appear above list content.
     *
     * @see #setPromptPosition(int)
     * @see #getPromptPosition()
     * @see #setPromptView(View)
     */
    public static final int POSITION_PROMPT_ABOVE = 0;

    /**
     * The provided prompt view should appear below list content.
     *
     * @see #setPromptPosition(int)
     * @see #getPromptPosition()
     * @see #setPromptView(View)
     */
    public static final int POSITION_PROMPT_BELOW = 1;

    public AbsListPopupBubbleWindow(Context context) {
        this(context, null, R.attr.listPopupBubbleWindowStyle, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter. Backgrounds
     * should be set using {@link #setBackgroundDrawable(Drawable)}.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs Attributes from inflating parent views used to style the popup.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public AbsListPopupBubbleWindow(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.listPopupBubbleWindowStyle, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter. Backgrounds
     * should be set using {@link #setBackgroundDrawable(Drawable)}.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs Attributes from inflating parent views used to style the popup.
     * @param defStyleAttr Default style attribute to use for popup content.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public AbsListPopupBubbleWindow(Context context, AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter. Backgrounds
     * should be set using {@link #setBackgroundDrawable(Drawable)}.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs Attributes from inflating parent views used to style the popup.
     * @param defStyleAttr Style attribute to read for default styling of popup content.
     * @param defStyleRes Style resource ID to use for default styling of popup content.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public AbsListPopupBubbleWindow(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context,attrs,defStyleAttr,defStyleRes);
        mContext = context;
        Resources res = context.getResources();

        setInputMethodMode(PopupBubbleWindow.INPUT_METHOD_NEEDED);
        mMarginM2 = res.getDimensionPixelOffset(R.dimen.margin_m);
        final DisplayMetrics screen = res.getDisplayMetrics();
        final int screenWidth = (screen.widthPixels < screen.heightPixels)? screen.widthPixels : screen.heightPixels;
        mPopupMinWidth = (int) (screenWidth * WINDOW_MINMUM_WIDTH_RATIO - mMarginM2);
        mPopupMaxWidth = screenWidth - mMarginM2 * 2;
        constructThreadHash = Thread.currentThread().hashCode();
    }
    /**
     * @return The ID of the currently selected item or
     *         {@link ListView#INVALID_ROW_ID} if {@link #isShowing()} ==
     *         {@code false}.
     */
    abstract long getSelectedItemId();

    /**
     * @return The currently selected item or null if the popup is not showing.
     */
    abstract Object getSelectedItem();
    /**
     * @return The View for the currently selected item or null if
     *         {@link #isShowing()} == {@code false}.
     */
    abstract View getSelectedView();
    /**
     * @return The position of the currently selected item or
     *         {@link ListView#INVALID_POSITION} if {@link #isShowing()} ==
     *         {@code false}.
     */
    abstract int getSelectedItemPosition();
    /**
     * Set the selected position of the list. Only valid when
     * {@link #isShowing()} == {@code true}.
     *
     * @param position
     *            List position to set as selected.
     */
    abstract void setSelection(int position);
    /**
     * Filter key down events. By forwarding key down events to this function,
     * views using non-modal ExpandableListPopupBubbleWindow can have it handle
     * key selection of items.
     *
     * @param keyCode
     *            keyCode param passed to the host view's onKeyDown
     * @param event
     *            event param passed to the host view's onKeyDown
     * @return true if the event was handled, false if it was ignored.
     *
     * @see #setModal(boolean)
     */
    abstract boolean onKeyDown(int keyCode, KeyEvent event);

    /**
     * Clear any current list selection. Only valid when {@link #isShowing()} ==
     * {@code true}.
     */
    abstract void clearListSelection();

    /**
     * @return Current List Item count
     */
    protected abstract int getItemCount();

    /**
     * @return the width of current list Content
     */
    protected abstract int measureContentWidth();

    /**
     * @return ture, Adapter is null otherwise false
     */
    protected abstract boolean isAdapterNull();

    /**
     * @return the DropDown ListView or null
     */
    protected abstract View getDropDownList();

    /**
     * @return the Number of DropDownListView
     */
    protected abstract int getDropDownListCount();

    /**
     * @return the Number of DropDown listView child
     */
    protected abstract int getDropDownListChildCount();
    /**
     * <p>
     * Builds the popup window's content and returns the height the popup should
     * have. Returns -1 when the content already exists.
     * </p>
     *
     * @return the content's height or -1 if content already exists
     */
    protected abstract int buildDropDown();

    /**
     * clear ListView adapter null
     */
    protected abstract void clearListAdapter();

    /**
     * show Popup Window
     */
    public void show() {
        int height = buildDropDown();
        int widthSpec = 0;
        int heightSpec = 0;
        View mDropDownListView = getDropDownList();
        boolean noInputMethod = isInputMethodNotNeeded();
        setAllowScrollingAnchorParent(!noInputMethod);
        if (isShowing()) {
            if (mDropDownWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                // The call to PopupWindow's update method below can accept -1 for any
                // value you do not want to update.
                widthSpec = -1;
            } else if (mDropDownWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
                widthSpec = getWrapWindowWidth();
            } else {
                widthSpec = mDropDownWidth;
            }

            if (mDropDownHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                // The call to PopupWindow's update method below can accept -1 for any
                // value you do not want to update.
                heightSpec = noInputMethod ? height : ViewGroup.LayoutParams.MATCH_PARENT;
                if (noInputMethod) {
                    setWindowLayoutMode(mDropDownWidth == ViewGroup.LayoutParams.MATCH_PARENT ? ViewGroup.LayoutParams.MATCH_PARENT : 0, 0);
                } else {
                    setWindowLayoutMode(mDropDownWidth == ViewGroup.LayoutParams.MATCH_PARENT ? ViewGroup.LayoutParams.MATCH_PARENT : 0, ViewGroup.LayoutParams.MATCH_PARENT);
                }
            } else if (mDropDownHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                heightSpec = height;
            } else {
                heightSpec = mDropDownHeight;
            }

            setOutsideTouchable(!mForceIgnoreOutsideTouch && !mDropDownAlwaysVisible);

            update(getAnchorView(), mDropDownHorizontalOffset, mDropDownVerticalOffset, widthSpec, heightSpec);
        } else {
            if (mDropDownWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                widthSpec = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                if (mDropDownWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    super.setWidth(getWrapWindowWidth());
                } else {
                    super.setWidth(mDropDownWidth);
                }
            }

            if (mDropDownHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                heightSpec = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                if (mDropDownHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    super.setHeight(height);
                } else {
                    super.setHeight(mDropDownHeight);
                }
            }

            setWindowLayoutMode(widthSpec, heightSpec);
            setClipToScreenEnabled(true);

            // use outside touchable to dismiss drop down when touching outside of it, so
            // only set this if the dropdown is not always visible
            setOutsideTouchable(!mForceIgnoreOutsideTouch && !mDropDownAlwaysVisible);
            // mPopup.setListViewHook(mDropDownList);
            showAsDropDown(getAnchorView(), mDropDownHorizontalOffset, mDropDownVerticalOffset);
            super.setTouchInterceptor(mTouchInterceptor);
            if (mDropDownListView != null)
                setSelection(ListView.INVALID_POSITION);
            }
            if (!mModal || ((mDropDownListView != null) && mDropDownListView.isInTouchMode())) {
                clearListSelection();
            }
            if (!mModal) {
                mHandler.post(mHideSelector);
            }
    }

    /**
     * get window width when with is WRAP_CONTENT
     */
    private int getWrapWindowWidth(){
        int width = Math.max(getAnchorView().getWidth() , mPopupMinWidth);
        width=Math.min(width, mPopupMaxWidth);
        return width;
    }

    /**
     * dismiss Popup Window
     */
    public void dismiss() {
        super.dismiss();
        removePromptView();
        setContentView(null);
    }

    /**
     * dismiss Popup Window  Without Animation
     */
    public void dismissWithoutAnimation() {
        super.dismissWithoutAnimation();
        removePromptView();
        setContentView(null);
        clearListAdapter();
        mHandler.removeCallbacks(mResizePopupRunnable);
    }

    // record the hash code for construct thread
    protected int constructThreadHash = -1;

    /**
     * Set where the optional prompt view should appear. The default is
     * {@link #POSITION_PROMPT_ABOVE}.
     *
     * @param position A position constant declaring where the prompt should be displayed.
     * @see #POSITION_PROMPT_ABOVE
     * @see #POSITION_PROMPT_BELOW
     */
    public void setPromptPosition(int position) {
        mPromptPosition = position;
    }

    /**
     * @return Where the optional prompt view should appear.
     * @see #POSITION_PROMPT_ABOVE
     * @see #POSITION_PROMPT_BELOW
     */
    public int getPromptPosition() {
        return mPromptPosition;
    }

    /**
     * Set whether this window should be modal when shown.
     * <p>
     * If a popup window is modal, it will receive all touch and key input. If the user touches
     * outside the popup window's content area the popup window will be dismissed.
     *
     * @param modal {@code true} if the popup window should be modal, {@code false} otherwise.
     */
    public void setModal(boolean modal) {
        mModal = true;
        setFocusable(modal);
    }

    /**
     * Returns whether the popup window will be modal when shown.
     *
     * @return {@code true} if the popup window will be modal, {@code false} otherwise.
     */
    public boolean isModal() {
        return mModal;
    }

    /**
     * Returns the view that will be used to anchor this popup.
     *
     * @return The popup's anchor view
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public View getAnchorView() {
        return mDropDownAnchorView;
    }

    /**
     * Set a view to act as a user prompt for this popup window. Where the prompt view will appear
     * is controlled by {@link #setPromptPosition(int)}.
     *
     * @param prompt View to use as an informational prompt.
     */
    /**
     * @hide
     */
    public void setPromptView(View prompt) {
        boolean showing = isShowing();
        if (showing) {
            removePromptView();
        }
        mPromptView = prompt;
        if (showing) {
            show();
        }
    }


    /**
     * Filter pre-IME key events. By forwarding
     * {@link View#onKeyPreIme(int, KeyEvent)} events to this function, views
     * using ExpandableListPopupBubbleWindow can have it dismiss the popup when
     * the back key is pressed.
     *
     * @param keyCode
     *            keyCode param passed to the host view's onKeyPreIme
     * @param event
     *            event param passed to the host view's onKeyPreIme
     * @return true if the event was handled, false if it was ignored.
     *
     * @see #setModal(boolean)
     */
    /**
     * @hide
     */
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isShowing()) {
            // special case for the back key, we do not even try to send it
            // to the drop down list but instead, consume it immediately
            final View anchorView = mDropDownAnchorView;
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                KeyEvent.DispatcherState state = anchorView
                        .getKeyDispatcherState();
                if (state != null) {
                    state.startTracking(event, this);
                }
                return true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                KeyEvent.DispatcherState state = anchorView
                        .getKeyDispatcherState();
                if (state != null) {
                    state.handleUpEvent(event);
                }
                if (event.isTracking() && !event.isCanceled()) {
                    dismiss();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Filter key down events. By forwarding key up events to this function,
     * views using non-modal ListPopupBubbleWindow can have it handle key
     * selection of items.
     *
     * @param keyCode
     *            keyCode param passed to the host view's onKeyUp
     * @param event
     *            event param passed to the host view's onKeyUp
     * @return true if the event was handled, false if it was ignored.
     *
     * @see #setModal(boolean)
     */
    /**
     * @hide
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        View mDropDownList = getDropDownList();
        if (isShowing() && (mDropDownList != null)
                && getSelectedItemPosition() >= 0) {
            boolean consumed = (mDropDownList != null) ? mDropDownList.onKeyUp(
                    keyCode, event) : false;
            if (consumed) {
                switch (keyCode) {
                // if the list accepts the key events and the key event
                // was a click, the text view gets the selected item
                // from the drop down as its content
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    dismiss();
                    break;
                }
            }
            return consumed;
        }
        return false;
    }

    /**
     * @return {@code true} if this popup is configured to assume the user does not need to interact
     *         with the IME while it is showing, {@code false} otherwise.
     */
    /**
     * @hide
     */
    public boolean isInputMethodNotNeeded() {
            return getInputMethodMode() == PopupBubbleWindow.INPUT_METHOD_NOT_NEEDED;
    }

    /**
     * Set a listener to receive a callback when the popup is dismissed.
     *
     * @hide
     * @param listener Listener that will be notified when the popup is dismissed.
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setOnDismissListener(
            android.widget.PopupWindow.OnDismissListener listener) {
            super.setOnDismissListener(listener);
    }

    /**
     * Forces outside touches to be ignored. Normally if {@link #isDropDownAlwaysVisible()} is
     * false, we allow outside touch to dismiss the dropdown. If this is set to true, then we ignore
     * outside touch even when the drop down is not set to always visible.
     *
     * @hide Used only by AutoCompleteTextView to handle some internal special cases.
     */
    public void setForceIgnoreOutsideTouch(boolean forceIgnoreOutsideTouch) {
        mForceIgnoreOutsideTouch = forceIgnoreOutsideTouch;
    }

    /**
     * Sets whether the drop-down should remain visible under certain conditions. The drop-down will
     * occupy the entire screen below {@link #getAnchorView} regardless of the size or content of
     * the list. {@link #getBackground()} will fill any space that is not used by the list.
     *
     * @param dropDownAlwaysVisible Whether to keep the drop-down visible.
     * @hide Only used by AutoCompleteTextView under special conditions.
     */
    public void setDropDownAlwaysVisible(boolean dropDownAlwaysVisible) {
        mDropDownAlwaysVisible = dropDownAlwaysVisible;
    }

    /**
     * @return Whether the drop-down is visible under special conditions.
     * @hide Only used by AutoCompleteTextView under special conditions.
     */
    public boolean isDropDownAlwaysVisible() {
        return mDropDownAlwaysVisible;
    }

    /**
     * Sets a drawable to use as the list item selector.
     *
     * @param selector List selector drawable to use in the popup.
     */
    /**
     * @hide
     */
    public void setListSelector(Drawable selector) {
        mDropDownListHighlight = selector;
    }
    /**
     * Sets the popup's anchor view. This popup will always be positioned relative to the anchor
     * view when shown.
     *
     * @param anchor The view to use as an anchor.
     */
    public void setAnchorView(View anchor) {
        mDropDownAnchorView = anchor;
    }

    /**
     * @return The horizontal offset of the popup from its anchor in pixels.
     */
    /**
     * @hide
     */
    public int getHorizontalOffset() {
        return mDropDownHorizontalOffset;
    }

    /**
     * Set the horizontal offset of this popup from its anchor view in pixels.
     *
     * @param offset The horizontal offset of the popup from its anchor.
     */
    /**
     * @hide
     */
    public void setHorizontalOffset(int offset) {
        mDropDownHorizontalOffset = offset;
    }

    /**
     * @return The vertical offset of the popup from its anchor in pixels.
     */
    /**
     * @hide
     */
    public int getVerticalOffset() {
        return mDropDownVerticalOffset;
    }

    /**
     * Set the vertical offset of this popup from its anchor view in pixels.
     *
     * @param offset The vertical offset of the popup from its anchor.
     */
    /**
     * @hide
     */
    public void setVerticalOffset(int offset) {
        mDropDownVerticalOffset = offset;
    }

    /**
     * @return The width of the popup window in pixels.
     */
    /**
     * @hide
     */
    public int getWidth() {
        return mDropDownWidth;
    }

    /**
     * Sets the width of the popup window in pixels. Can also be {@link #MATCH_PARENT} or
     * {@link #WRAP_CONTENT}.
     *
     * @param width Width of the popup window.
     */
    /**
     * @hide
     */
    public void setWidth(int width) {

        Drawable popupBackground = getBackground();
        if (popupBackground != null) {
            popupBackground.getPadding(mTempRect);
            setCustomizedContentWidth(width - mTempRect.left - mTempRect.right);
        }

        mDropDownWidth = checkWidthLimit(width);
    }

    /**
     * Sets the width of the popup window by the size of its content. The final width may be larger
     * to accommodate styled window dressing.
     *
     * @param width Desired width of content in pixels.
     */
    public void setContentWidth(int width) {
        setCustomizedContentWidth(width);
        width = checkContentWidthLimit(width);
        Drawable popupBackground = getBackground();
        if (popupBackground != null) {
            popupBackground.getPadding(mTempRect);
            mDropDownWidth = mTempRect.left + mTempRect.right + width;
        }
    }

    /**
     * For fixing item height popupwindow
     *
     * @param isWidthHeightFixed If true, it will not measure width and height again when data
     *            changed.
     * @param width desired item width
     * @param height desired item height
     * @hide
     * @deprecated
     */
    protected void setFixedListItemDimension(boolean isWidthHeightFixed,
            int width, int height) {
        mIsWidthHeightFixed = isWidthHeightFixed;
        if (isWidthHeightFixed) {
            setContentWidth(width);
            mItemHeight = height;
        }
    }

    /**
     * @return The height of the popup window in pixels.
     */
    /**
     * @hide
     */
    public int getHeight() {
        return mDropDownHeight;
    }

    /**
     * @return The height of the popup window in pixels.
     */
    /**
     * @hide
     */
    public int getPopupWindowHeight(){
        return super.getHeight();
    }

    /**
     * @return The width of the popup window in pixels.
     */
    /**
     * @hide
     */
    public int getPopupWindowWidth(){
        return super.getWidth();
    }
    /**
     * Sets the height of the popup window in pixels. Can also be {@link #MATCH_PARENT}.
     *
     * @param height Height of the popup window.
     */
    /**
     * @hide
     */
    public void setHeight(int height) {
        mDropDownHeight = height;
    }

    /**
     * Sets a listener to receive events when a list item is clicked.
     *
     * @param clickListener Listener to register
     * @see ListView#setOnItemClickListener(android.widget.AdapterView.OnItemClickListener)
     */
    /**
     * @hide
     */
    public void setOnItemClickListener(
            AdapterView.OnItemClickListener clickListener) {
        // TODO should be workable?
        mItemClickListener = clickListener;
    }

    /**
     * Sets a listener to receive events when a list item is selected.
     *
     * @param selectedListener Listener to register.
     * @see ListView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setOnItemSelectedListener(
            AdapterView.OnItemSelectedListener selectedListener) {
        mItemSelectedListener = selectedListener;
    }

    /**
     * Post a {@link #show()} call to the UI thread.
     */
    /**
     * @hide
     */
    public void postShow() {
        mHandler.post(mShowDropDownRunnable);
    }

    /**
     * Set a listener to receive a callback when the popup is dismissed.
     *
     * @param listener Listener that will be notified when the popup is dismissed.
     */
    public void setOnDismissListener(
            PopupBubbleWindow.OnDismissListener listener) {
            super.setOnDismissListener(listener);
    }

    protected void removePromptView() {
        if (mPromptView != null) {
            final ViewParent parent = mPromptView.getParent();
            if (parent instanceof ViewGroup) {
                final ViewGroup group = (ViewGroup) parent;
                group.removeView(mPromptView);
            }
        }
    }

    /* @hide */
    /**
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void internalDismiss() {
        super.dismiss();
        removePromptView();
        mHandler.removeCallbacks(mResizePopupRunnable);
    }

    protected class PopupDataSetObserver extends DataSetObserver {
        /**
         * @hide
         */
        @Override
        public void onChanged() {

            int threadHashCode = Thread.currentThread().hashCode();

            if (threadHashCode != constructThreadHash)
                android.util.Log.i(TAG, "thread changed:" + constructThreadHash + "-" + threadHashCode);
            View mDropDownList = getDropDownList();
            if (isShowing() && mDropDownList == null) {
                System.out.println("borranx Strange! mDropDownList is null when showing");
                return;
            }

            if (isShowing()) {
                if (!isAdapterNull()) {
                    if (!mIsWidthHeightFixed || mDropDownWidth <= 0) { /* if application has setup the width,
                                                                       don't measureit again forbetter performance.*/
                        int width = measureContentWidth();
                        Drawable popupBackground = getBackground();
                        if (popupBackground != null) {
                            popupBackground.getPadding(mTempRect);
                            // width should plus the background padding to compare.
                            if (width + mTempRect.left + mTempRect.right > mDropDownWidth) {
                                setContentWidth(width);
                            }
                        }
                    } else {
                        // for Note
                        mItemCount = getItemCount();
                    }
                }

                // Resize the popup to fit new content
                show();
            }
        }
        /**
         * @hide
         */
        @Override
        public void onInvalidated() {
            dismiss();
        }
    }

    protected class ListSelectorHider implements Runnable {
        /**
         * @hide
         */
        public void run() {
            clearListSelection();
        }
    }
    protected class ResizePopupRunnable implements Runnable {
        /**
         * @hide
         */
        public void run() {
            // TODO for temp solution. please fine tune it
            if (getDropDownList() != null
                    && getDropDownListCount() >= getDropDownListChildCount()
                    && getDropDownListChildCount() <= mListItemExpandMaximum
                    && isShowing()) {
                setInputMethodMode(PopupBubbleWindow.INPUT_METHOD_NOT_NEEDED);
                show();
            }
        }
    }

    protected class PopupTouchInterceptor implements OnTouchListener {
        /**
         * @hide
         */
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            if (action == MotionEvent.ACTION_DOWN
                    && isShowing()
                    && (x >= 0 && x < getPopupWindowWidth()
                    && y >= 0 && y < getPopupWindowHeight())) {
                mHandler.postDelayed(mResizePopupRunnable, EXPAND_LIST_TIMEOUT);
            } else if (action == MotionEvent.ACTION_UP) {
                mHandler.removeCallbacks(mResizePopupRunnable);
            }

            if (mCustomizedTouchInterceptor != null
                    && mCustomizedTouchInterceptor.onTouch(v, event))
                return true;
            return false;
        }
    }

    /**
     * Set a callback for all touch events being dispatched to the popup window.
     *
     * @param l
     *            a callback for all touch events being dispatched to the popup
     *            window.
     * @hide
     */
    public void setTouchInterceptor(OnTouchListener l) {
        mCustomizedTouchInterceptor = l;
    }

    protected class PopupScrollListener implements ListView.OnScrollListener {
        /**
         * @hide
         */
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {

        }

        /**
         * @hide
         */
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_TOUCH_SCROLL
                    && !isInputMethodNotNeeded() && getContentView() != null) {
                mHandler.removeCallbacks(mResizePopupRunnable);
                if (mResizePopupRunnable != null)
                    mResizePopupRunnable.run();
            }
        }
    }
}
