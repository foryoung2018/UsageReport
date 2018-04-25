/*
 * Copyright (C) 2010 The Android Open Source Project
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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.view.animation.Animation.AnimationListener;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.HtcPopupFactory;
import java.util.ArrayList;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;

/**
 * A ListPopupBubbleWindow anchors itself to a host view and displays a list of
 * choices.
 *
 * <p>
 * ListPopupBubbleWindow contains a number of tricky behaviors surrounding
 * positioning, scrolling parents to fit the dropdown, interacting sanely with
 * the IME if present, and others.
 *
 * @see android.widget.AutoCompleteTextView
 * @see android.widget.Spinner
 */
public class ListPopupBubbleWindow extends AbsListPopupBubbleWindow implements
        HtcPopupFactory.HtcPopupBubble, AdapterView.OnItemClickListener {
    private static final String TAG = "ListPopupBubbleWindow";
    private static final boolean DEBUG = HtcBuildFlag.Htc_DEBUG_flag;
    /**
     * This value controls the length of time that the user must leave a pointer
     * down without scrolling to expand the autocomplete dropdown list to cover
     * the IME.
     */

    private ListAdapter mAdapter;
    private DropDownListView mDropDownList;

    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();
    private ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<FixedViewInfo>();

    /**
     * A class that represents a fixed view in a list, for example a header at
     * the top or a footer at the bottom.
     */
    class FixedViewInfo {
        /** The view to add to the list */
        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public View view;
        /**
         * The data backing the view. This is returned from
         * {@link ListAdapter#getItem(int)}.
         */
        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public Object data;
        /** <code>true</code> if the fixed view should be selectable in the list */
        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public boolean isSelectable;
    }

    private AdapterView.OnItemClickListener mFrameworkMenuItemClickListener;

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    /**
     * Create a new, empty popup window capable of displaying items from a
     * ListAdapter.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ListPopupBubbleWindow(Context context) {
        this(context, null, R.attr.listPopupBubbleWindowStyle, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a
     * ListAdapter. Backgrounds should be set using
     * {@link #setBackgroundDrawable(Drawable)}.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            Attributes from inflating parent views used to style the
     *            popup.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public ListPopupBubbleWindow(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.listPopupBubbleWindowStyle, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a
     * ListAdapter. Backgrounds should be set using
     * {@link #setBackgroundDrawable(Drawable)}.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            Attributes from inflating parent views used to style the
     *            popup.
     * @param defStyleAttr
     *            Default style attribute to use for popup content.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public ListPopupBubbleWindow(Context context, AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a
     * ListAdapter. Backgrounds should be set using
     * {@link #setBackgroundDrawable(Drawable)}.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     *            Attributes from inflating parent views used to style the
     *            popup.
     * @param defStyleAttr
     *            Style attribute to read for default styling of popup content.
     * @param defStyleRes
     *            Style resource ID to use for default styling of popup content.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public ListPopupBubbleWindow(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Sets the adapter that provides the data and the views to represent the
     * data in this popup window.
     *
     * @param adapter
     *            The adapter to use to create this window's content.
     */
    public void setAdapter(ListAdapter adapter) {
        if (mObserver == null) {
            mObserver = new PopupDataSetObserver();
        } else if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
            android.util.Log.i("ListPopupBubbleWindow",
                    "unregister data set observer");
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            adapter.registerDataSetObserver(mObserver);
            android.util.Log.i("ListPopupBubbleWindow",
                    "register data set observer");
        }

        if (mDropDownList != null) {
            mDropDownList.setAdapter(mAdapter);

            if (adapter == null)
                android.util.Log.i("ListPopupBubbleWindow",
                        "unregister list data set observer");
        }
    }

    /**
     * Sets a listener to receive events when a list item is clicked.
     *
     * @param clickListener
     *            Listener to register
     * @hide
     * @see ListView#setOnItemClickListener(android.widget.AdapterView.OnItemClickListener)
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setOnItemClickListener(Object clickListener) {
        mFrameworkMenuItemClickListener = (AdapterView.OnItemClickListener) clickListener;
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked and held
     *
     * @param listener
     *            The callback that will run
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setOnItemLongClickListener(
            AdapterView.OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
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
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        if (mAdapter != null) {
            throw new IllegalStateException(
                    "Cannot add header view to list -- setAdapter has already been called.");
        }

        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     * @param v
     *            The view to add.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
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
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void addFooterView(View v, Object data, boolean isSelectable) {
        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        if (mFooterViewInfos != null)
            mFooterViewInfos.add(info);
    }

    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * NOTE: Call this before calling setAdapter. This is so ListView can wrap
     * the supplied cursor with one that will also account for header and footer
     * views.
     *
     *
     * @param v
     *            The view to add.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    /**
     * Dismiss the popup window.
     */
    public void dismiss() {
        super.dismiss();
        if (mDropDownList != null)
            mDropDownList.setAdapter((ListAdapter) null);

        mHandler.removeCallbacks(mResizePopupRunnable);
    }

    protected void clearListAdapter(){
        if (mDropDownList != null)
            mDropDownList.setAdapter((ListAdapter) null);
        mDropDownList = null;
    }

    /**
     * Set the selected position of the list. Only valid when
     * {@link #isShowing()} == {@code true}.
     *
     * @param position
     *            List position to set as selected.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setSelection(int position) {
        DropDownListView list = mDropDownList;
        if (isShowing() && list != null) {
            list.mListSelectionHidden = false;
            list.setSelection(position);
            if (list.getChoiceMode() != ListView.CHOICE_MODE_NONE) {
                list.setItemChecked(position, true);
            }
        }
    }

    /**
     * Clear any current list selection. Only valid when {@link #isShowing()} ==
     * {@code true}.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void clearListSelection() {
        final DropDownListView list = mDropDownList;
        if (list != null) {
            // WARNING: Please read the comment where mListSelectionHidden is
            // declared
            list.mListSelectionHidden = true;
            // TODO: for temp solution
            // list.hideSelector();
            list.requestLayout();
        }
    }

    /**
     * @return The currently selected item or null if the popup is not showing.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public Object getSelectedItem() {
        if (!isShowing()) {
            return null;
        }
        if (mDropDownList != null)
            return mDropDownList.getSelectedItem();
        else
            return null;
    }

    /**
     * @return The View for the currently selected item or null if
     *         {@link #isShowing()} == {@code false}.
     *
     * @see ListView#getSelectedView()
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public View getSelectedView() {
        if (!isShowing()) {
            return null;
        }
        if (mDropDownList != null)
            return mDropDownList.getSelectedView();
        else
            return null;
    }

    /**
     * Perform an item click operation on the specified list adapter position.
     *
     * @param position
     *            Adapter position for performing the click
     * @return true if the click action could be performed, false if not. (e.g.
     *         if the popup was not showing, this method would return false.)
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public boolean performItemClick(int position) {
        if (isShowing()) {
            if (mItemClickListener != null) {
                final DropDownListView list = mDropDownList;
                final View child = list.getChildAt(position
                        - list.getFirstVisiblePosition());
                final ListAdapter adapter = list.getAdapter();
                onItemClick(list, child, position, adapter.getItemId(position));
            }
            return true;
        }
        return false;
    }

    /**
     * @return The position of the currently selected item or
     *         {@link ListView#INVALID_POSITION} if {@link #isShowing()} ==
     *         {@code false}.
     *
     * @see ListView#getSelectedItemPosition()
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public int getSelectedItemPosition() {
        if (!isShowing()) {
            return ListView.INVALID_POSITION;
        }
        if (mDropDownList != null)
            return mDropDownList.getSelectedItemPosition();
        else
            return 0;
    }

    /**
     * @return The ID of the currently selected item or
     *         {@link ListView#INVALID_ROW_ID} if {@link #isShowing()} ==
     *         {@code false}.
     *
     * @see ListView#getSelectedItemId()
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public long getSelectedItemId() {
        if (!isShowing()) {
            return ListView.INVALID_ROW_ID;
        }
        if (mDropDownList != null)
            return mDropDownList.getSelectedItemId();
        else
            return 0;
    }

    /**
     * @return The {@link ListView} displayed within the popup window. Only
     *         valid when {@link #isShowing()} == {@code true}.
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public HtcListView getListView() {
        return mDropDownList;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public View getMenuListView() {
        return mDropDownList;
    }

    /**
     * The maximum number of list items that can be visible and still have the
     * list expand when touched.
     *
     * @param max
     *            Max number of items that can be visible and still allow the
     *            list to expand.
     */
    void setListItemExpandMax(int max) {
        mListItemExpandMaximum = max;
    }

    /**
     * Filter key down events. By forwarding key down events to this function,
     * views using non-modal ListPopupBubbleWindow can have it handle key
     * selection of items.
     *
     * @param keyCode
     *            keyCode param passed to the host view's onKeyDown
     * @param event
     *            event param passed to the host view's onKeyDown
     * @return true if the event was handled, false if it was ignored.
     *
     * @see #setModal(boolean)
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // when the drop down is shown, we drive it directly
        if (isShowing()) {
            // the key events are forwarded to the list in the drop down view
            // note that ListView handles space but we don't want that to happen
            // also if selection is not currently in the drop down, then don't
            // let center or enter presses go there since that would cause it
            // to select one of its items
            if (keyCode != KeyEvent.KEYCODE_SPACE
                    && (mDropDownList != null)
                    && (mDropDownList.getSelectedItemPosition() >= 0 || (keyCode != KeyEvent.KEYCODE_ENTER && keyCode != KeyEvent.KEYCODE_DPAD_CENTER))) {
                int curIndex = (mDropDownList != null) ? mDropDownList
                        .getSelectedItemPosition() : 0;
                boolean consumed;

                final boolean below = !isAboveAnchor();

                final ListAdapter adapter = mAdapter;

                boolean allEnabled;
                int firstItem = Integer.MAX_VALUE;
                int lastItem = Integer.MIN_VALUE;

                if (adapter != null) {
                    allEnabled = adapter.areAllItemsEnabled();
                    firstItem = allEnabled ? 0
                            : ((mDropDownList != null) ? mDropDownList
                                    .mockLookForSelectablePosition(0, true) : 0);
                    lastItem = allEnabled ? adapter.getCount() - 1
                            : ((mDropDownList != null) ? mDropDownList
                                    .mockLookForSelectablePosition(
                                            adapter.getCount() - 1, false) : 0);
                }

                if ((below && keyCode == KeyEvent.KEYCODE_DPAD_UP && curIndex <= firstItem)
                        || (!below && keyCode == KeyEvent.KEYCODE_DPAD_DOWN && curIndex >= lastItem)) {
                    // When the selection is at the top, we block the key
                    // event to prevent focus from moving.
                    clearListSelection();
                    setInputMethodMode(PopupBubbleWindow.INPUT_METHOD_NEEDED);
                    show();
                    return true;
                } else {
                    // WARNING: Please read the comment where
                    // mListSelectionHidden
                    // is declared
                    if (mDropDownList != null)
                        mDropDownList.mListSelectionHidden = false;
                }

                consumed = (mDropDownList != null) ? mDropDownList.onKeyDown(
                        keyCode, event) : false;
                if (DEBUG)
                    Log.v(TAG, "Key down: code=" + keyCode + " list consumed="
                            + consumed);

                if (consumed) {
                    // If it handled the key event, then the user is
                    // navigating in the list, so we should put it in front.
                    setInputMethodMode(PopupBubbleWindow.INPUT_METHOD_NOT_NEEDED);
                    // Here's a little trick we need to do to make sure that
                    // the list view is actually showing its focus indicator,
                    // by ensuring it has focus and getting its window out
                    // of touch mode.
                    if (mDropDownList != null)
                        mDropDownList.requestFocusFromTouch();
                    show();

                    switch (keyCode) {
                    // avoid passing the focus from the text view to the
                    // next component
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                    case KeyEvent.KEYCODE_DPAD_UP:
                        return true;
                    }
                } else {
                    if (below && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        // when the selection is at the bottom, we block the
                        // event to avoid going to the next focusable widget
                        if (curIndex == lastItem) {
                            return true;
                        }
                    } else if (!below && keyCode == KeyEvent.KEYCODE_DPAD_UP
                            && curIndex == firstItem) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Builds the popup window's content and returns the height the popup should
     * have. Returns -1 when the content already exists.
     * </p>
     *
     * @return the content's height or -1 if content already exists
     */
    protected int buildDropDown() {
        ViewGroup dropDownView;
        int otherHeights = 0;

        if (mDropDownList == null) {
            Context context = mContext;

            /**
             * This Runnable exists for the sole purpose of checking if the view
             * layout has got completed and if so call showDropDown to display
             * the drop down. This is used to show the drop down as soon as
             * possible after user opens up the search dialog, without waiting
             * for the normal UI pipeline to do it's job which is slower than
             * this method.
             */
            mShowDropDownRunnable = new Runnable() {
                public void run() {
                    // View layout should be all done before displaying the drop
                    // down.
                    View view = getAnchorView();
                    if (view != null && view.getWindowToken() != null) {
                        show();
                    }
                }
            };

            mDropDownList = new DropDownListView(context, !mModal);
            if (DEBUG)
                Log.v(TAG, Thread.currentThread() + " new list:"
                        + mDropDownList);

            if (mDropDownListHighlight != null) {
                mDropDownList.setSelector(mDropDownListHighlight);
            }

            final int count = (mHeaderViewInfos != null) ? mHeaderViewInfos
                    .size() : 0;
            for (int i = 0; i < count; i++) {
                FixedViewInfo info = mHeaderViewInfos.get(i);
                mDropDownList.addHeaderView(info.view, info.data,
                        info.isSelectable);
            }

            final int count2 = (mFooterViewInfos != null) ? mFooterViewInfos
                    .size() : 0;
            for (int i = 0; i < count2; i++) {
                FixedViewInfo info = mFooterViewInfos.get(i);
                mDropDownList.addFooterView(info.view, info.data,
                        info.isSelectable);
            }

            mDropDownList.setAdapter(mAdapter);
            mDropDownList.setVerticalFadingEdgeEnabled(false);
            mDropDownList.setOnItemClickListener(this);
            mDropDownList.setOnItemLongClickListener(mOnItemLongClickListener);
            mDropDownList.setFocusable(true);
            mDropDownList.setFocusableInTouchMode(true);
            mDropDownList.enableAnimation(IHtcAbsListView.ANIM_OVERSCROLL,
                    false);
            mDropDownList
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent,
                                View view, int position, long id) {

                            if (position != -1) {
                                DropDownListView dropDownList = mDropDownList;

                                if (dropDownList != null) {
                                    dropDownList.mListSelectionHidden = false;
                                }
                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
            mDropDownList.setOnScrollListener(mScrollListener);

            if (mItemSelectedListener != null) {
                mDropDownList.setOnItemSelectedListener(mItemSelectedListener);
            }

            dropDownView = mDropDownList;

            View hintView = mPromptView;
            if (hintView != null) {
                // if an hint has been specified, we accomodate more space for
                // it and
                // add a text view in the drop down menu, at the bottom of the
                // list
                LinearLayout hintContainer = new LinearLayout(context);
                hintContainer.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams hintParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);

                switch (mPromptPosition) {
                case POSITION_PROMPT_BELOW:
                    hintContainer.addView(dropDownView, hintParams);
                    hintContainer.addView(hintView);
                    break;

                case POSITION_PROMPT_ABOVE:
                    hintContainer.addView(hintView);
                    hintContainer.addView(dropDownView, hintParams);
                    break;

                default:
                    Log.e(TAG, "Invalid hint position " + mPromptPosition);
                    break;
                }

                // measure the hint's height to find how much more vertical
                // space
                // we need to add to the drop down's height
                int widthSpec = MeasureSpec.makeMeasureSpec(mDropDownWidth,
                        MeasureSpec.AT_MOST);
                int heightSpec = MeasureSpec.UNSPECIFIED;
                hintView.measure(widthSpec, heightSpec);

                hintParams = (LinearLayout.LayoutParams) hintView
                        .getLayoutParams();
                otherHeights = hintView.getMeasuredHeight()
                        + hintParams.topMargin + hintParams.bottomMargin;

                dropDownView = hintContainer;
            }

                setContentView(dropDownView);
        } else {
            dropDownView = (ViewGroup)getContentView();
            final View view = mPromptView;
            if (view != null) {
                LinearLayout.LayoutParams hintParams = (LinearLayout.LayoutParams) view
                        .getLayoutParams();
                otherHeights = view.getMeasuredHeight() + hintParams.topMargin
                        + hintParams.bottomMargin;
            }
        }

        // Max height available on the screen for a popup.
        boolean ignoreBottomDecorations = (getInputMethodMode() == PopupBubbleWindow.INPUT_METHOD_NOT_NEEDED);
        final int maxHeight = getMaxAvailableHeight(
                getAnchorView(), mDropDownVerticalOffset,
                ignoreBottomDecorations);

        // getMaxAvailableHeight() subtracts the padding, so we put it back,
        // to get the available height for the whole window
        int padding = 0;
        Drawable background = getBackground();
        if (background != null) {
            background.getPadding(mTempRect);
            padding = mTempRect.top + mTempRect.bottom;
        }

        if (mDropDownAlwaysVisible
                || mDropDownHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            return maxHeight + padding;
        }

        boolean needMeasureAgain = !mIsWidthHeightFixed || mItemCount <= 0
                || mItemHeight <= 0
                || (mItemHeight * mItemCount < (maxHeight - otherHeights));
        final int listContent = needMeasureAgain ? mDropDownList
                .mockMeasureHeightOfChildren(MeasureSpec.UNSPECIFIED, 0,
                        DropDownListView.NO_POSITION, maxHeight - otherHeights,
                        -1) : maxHeight - otherHeights;

        // add padding only if the list has items in it, that way we don't show
        // the popup if it is not needed
        if (listContent > 0)
            otherHeights += padding;

        return listContent + otherHeights;
    }

    /**
     * <p>
     * Wrapper class for a ListView. This wrapper can hijack the focus to make
     * sure the list uses the appropriate drawables and states when displayed on
     * screen within a drop down. The focus is never actually passed to the drop
     * down in this mode; the list only looks focused.
     * </p>
     */
    private static class DropDownListView extends HtcListView {
        private static final String TAG = ListPopupBubbleWindow.TAG
                + ".DropDownListView";

        static final int NO_POSITION = -1;
        /*
         * WARNING: This is a workaround for a touch mode issue.
         *
         * Touch mode is propagated lazily to windows. This causes problems in
         * the following scenario: - Type something in the AutoCompleteTextView
         * and get some results - Move down with the d-pad to select an item in
         * the list - Move up with the d-pad until the selection disappears -
         * Type more text in the AutoCompleteTextView *using the soft keyboard*
         * and get new results; you are now in touch mode - The selection comes
         * back on the first item in the list, even though the list is supposed
         * to be in touch mode
         *
         * Using the soft keyboard triggers the touch mode change but that
         * change is propagated to our window only after the first list layout,
         * therefore after the list attempts to resurrect the selection.
         *
         * The trick to work around this issue is to pretend the list is in
         * touch mode when we know that the selection should not appear, that is
         * when we know the user moved the selection away from the list.
         *
         * This boolean is set to true whenever we explicitly hide the list's
         * selection and reset to false whenever we know the user moved the
         * selection back to the list.
         *
         * When this boolean is true, isInTouchMode() returns true, otherwise it
         * returns super.isInTouchMode().
         */
        private boolean mListSelectionHidden;

        /**
         * True if this wrapper should fake focus.
         */
        private boolean mHijackFocus;

        /**
         * <p>
         * Creates a new list view wrapper.
         * </p>
         *
         * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
         */
        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public DropDownListView(Context context, boolean hijackFocus) {
            super(context, null, R.attr.dropDownListViewStyle);
            // setDarkModeEnabled(true);
            mHijackFocus = hijackFocus;
            // enableAnimation(AbsListView.ANIM_OVERSCROLL, false);
            // TODO: Add an API to control this
            setCacheColorHint(0); // Transparent, since the background drawable
                                    // could be anything.
        }



        /**
         * <p>
         * Returns the focus state in the drop down.
         * </p>
         *
         * @return true always if hijacking focus
         */
        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        @Override
        public boolean hasWindowFocus() {
            return mHijackFocus || super.hasWindowFocus();
        }

        /**
         * <p>
         * Returns the focus state in the drop down.
         * </p>
         *
         * @return true always if hijacking focus
         */
        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        @Override
        public boolean isFocused() {
            return mHijackFocus || super.isFocused();
        }

        /**
         * <p>
         * Returns the focus state in the drop down.
         * </p>
         *
         * @return true always if hijacking focus
         */
        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        @Override
        public boolean hasFocus() {
            return mHijackFocus || super.hasFocus();
        }

        // for mock listview
        final int mockMeasureHeightOfChildren(int widthMeasureSpec,
                int startPosition, int endPosition, final int maxHeight,
                int disallowPartialChildPosition) {

            final ListAdapter adapter = getAdapter();
            final int listPaddingTop = getListPaddingTop();
            final int listPaddingBottom = getListPaddingBottom();

            if (adapter == null) {
                return listPaddingTop + listPaddingBottom;
            }

            // Include the padding of the list
            int returnedHeight = listPaddingTop + listPaddingBottom;
            final Drawable divider = getDivider();
            final int realDividerHeight = getDividerHeight();
            final int dividerHeight = ((realDividerHeight > 0) && divider != null) ? realDividerHeight
                    : 0;
            // The previous height value that was less than maxHeight and
            // contained
            // no partial children
            int prevHeightWithoutPartialChild = 0;
            int i;
            View child;

            // mItemCount - 1 since endPosition parameter is inclusive

            endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1
                    : endPosition;
            // ToDo:ListView's API, always return true
            // final boolean recyle = recycleOnMeasure();
            final boolean recycle = true;

            for (i = startPosition; i <= endPosition; ++i) {
                // for temp solution
                child = adapter.getView(i, null, this);
                // child = obtainView(i, isScrap);

                mockMeasureScrapChild(child, i, widthMeasureSpec);

                if (i > 0) {
                    // Count the divider for all but one child
                    returnedHeight += dividerHeight;
                }

                // // Recycle the view before we possibly return from the method
                // if (recyle && recycleBin.shouldRecycleViewType(
                // ((LayoutParams) child.getLayoutParams()).viewType)) {
                // recycleBin.addScrapView(child);
                // }

                returnedHeight += child.getMeasuredHeight();

                if (returnedHeight >= maxHeight) {
                    // We went over, figure out which height to return. If
                    // returnedHeight > maxHeight,
                    // then the i'th position did not fit completely.
                    return (disallowPartialChildPosition >= 0) // Disallowing is
                                                                // enabled (>
                                                                // -1)
                            && (i > disallowPartialChildPosition) // We've past
                                                                    // the min
                                                                    // pos
                            && (prevHeightWithoutPartialChild > 0) // We have a
                                                                    // prev
                                                                    // height
                            && (returnedHeight != maxHeight) // i'th child did
                                                                // not fit
                                                                // completely
                    ? prevHeightWithoutPartialChild : maxHeight;
                }

                if ((disallowPartialChildPosition >= 0)
                        && (i >= disallowPartialChildPosition)) {
                    prevHeightWithoutPartialChild = returnedHeight;
                }
            }

            // At this point, we went through the range of children, and they
            // each
            // completely fit, so return the returnedHeight
            return returnedHeight;
        }

        private void mockMeasureScrapChild(View child, int position,
                int widthMeasureSpec) {
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            if (p == null) {
                p = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                child.setLayoutParams(p);
            }
            final ListAdapter adapter = getAdapter();
            // p.viewType = adapter.getItemViewType(position);
            // p.forceAdd = true;

            final int listPaddingLeft = getListPaddingLeft();
            final int listPaddingRight = getListPaddingRight();

            int childWidthSpec = ViewGroup.getChildMeasureSpec(
                    widthMeasureSpec, listPaddingLeft + listPaddingRight,
                    p.width);
            int lpHeight = p.height;
            int childHeightSpec;
            if (lpHeight > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                        MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED);
            }
            child.measure(childWidthSpec, childHeightSpec);
        }

        int mockLookForSelectablePosition(int position, boolean lookDown) {
            final ListAdapter adapter = getAdapter();
            if (adapter == null || isInTouchMode()) {
                return INVALID_POSITION;
            }

            final int count = adapter.getCount();
            if (!adapter.areAllItemsEnabled()) {
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

    }


    protected int getItemCount(){
        if(mAdapter != null)
            return mAdapter.getCount();
        return -1;
    }
    protected boolean isAdapterNull(){
        return (mAdapter == null) ? true : false;
    }

    protected View getDropDownList(){
        return mDropDownList;
    }

    protected int getDropDownListChildCount(){
        if(mDropDownList != null)
            return mDropDownList.getChildCount();
        return -1;
    }

    protected int getDropDownListCount(){
        if(mDropDownList != null)
            return mDropDownList.getCount();
        return -1;
    }


    protected int measureContentWidth() {
        // Menus don't tend to be long, so this is more sane than it looks.
        int width = 0;
        View itemView = null;
        int itemType = 0;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        final int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = mAdapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = mAdapter.getView(i, null, null);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }
        return width;
    }

    /**
     * @hide
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (null != mItemClickListener) {
            mItemClickListener.onItemClick(parent, view, position, id);
        }
        if (null != mFrameworkMenuItemClickListener) {
            mFrameworkMenuItemClickListener.onItemClick(null, view, position,
                    id);
        }
    }
}
