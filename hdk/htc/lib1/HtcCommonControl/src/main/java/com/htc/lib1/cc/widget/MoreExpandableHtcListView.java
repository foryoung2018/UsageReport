
package com.htc.lib1.cc.widget;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.R;

/**
 * A ListView which can expand more than one layer Need to call Destroy()
 * function when your activity is destroyed to prevent memory leak
 */
public class MoreExpandableHtcListView extends HtcListView {
    private static final String TAG = "MoreExpandableHtcListView";

    private MoreExpandableBaseAdapter mAdapter = null;

    // [ S40 Animation: Josh Kang

    private Drawable mFakeDivider;

    @ExportedProperty(category = "CommonControl")
    boolean mGroupPressAnimationEnabled = true;

    @ExportedProperty(category = "CommonControl")
    boolean mShouldDrawFakeDivider = false;

    @ExportedProperty(category = "CommonControl")
    boolean mShouldReduceChilren = false;

    // S40 Animation: Josh Kang ]

    Drawable mExpandDivider;

    @ExportedProperty(category = "CommonControl")
    int mExpandDividerHeight = 6;

    /**
     * Simple constructor to use when creating a MoreExpandableHtcListView from
     * code.
     *
     * @param context The Context the view is running in, through which it can
     *            access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public MoreExpandableHtcListView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a MoreExpandableHtcListView
     * from XML.
     *
     * @param context The Context the view is running in, through which it can
     *            access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public MoreExpandableHtcListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the view is running in, through which it can
     *            access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view.
     */
    public MoreExpandableHtcListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Set then data bind to MoreExpandableHtcListView
     *
     * @param adapter The MoreExpandableBaseAdapter is responsible for maintain
     *            the data show on the MoreExpandableHtcListView
     */
    public void setAdapter(MoreExpandableBaseAdapter adapter) {
        mAdapter = adapter;
        super.setAdapter(mAdapter);
    }

    /**
     * return the MoreExpandableBaseAdapter using by this
     * MoreExpandableHtcListView
     *
     * @return The MoreExpandableBaseAdapter instance
     */
    public MoreExpandableBaseAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Gets the item info which is currently expanded.
     *
     * @return the object which is currently expanded
     */
    public MoreExpandableItemInfo getCurrentExpanded() {
        if (mAdapter != null) {
            return mAdapter.getCurrentExpanded();
        } else
            return null;

    }

    /**
     * Gets the flat position of the group which is currently expanded.
     *
     * @return the position of the currently expanded item Integer.MIN_VALUE if
     *         no item is expanded
     */
    @ExportedProperty(category = "CommonControl")
    public int getCurrentExpandedPosition() {
        if (getCurrentExpanded() != null) {
            return mAdapter.getIndexOfMoreExpandableItemInfo(getCurrentExpanded());
        } else {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * set the background color for the expanded children
     *
     * @param color the color value in ARGB format for example the color you
     *            want is dbdbdb you should set 0xffdbdbdb
     */
    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void setExpandedChildrenBackgroundColor(int color) {
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public boolean performItemClick(View v, int position, long id) {
        return handleItemClick(v, position, id);
    }

    private void init(Context context) {
        final Resources res = context.getResources();
        mExpandDivider = new ColorDrawable(res.getColor(R.color.dark_ap_background_color));
        mExpandDividerHeight = res.getDimensionPixelOffset(R.dimen.expand_divider_height);
    }

    boolean handleItemClick(View v, int position, long id) {
        mIsModified = true;
        if (mAnimationRunning)
            return true;

        boolean returnValue;
        MoreExpandableItemInfo self = mAdapter.getMoreExpandableItemInfo(position);
        if (self == null)
            throw new IllegalStateException("getMoreExpandableItemInfo(" + position + ") is null");
        else if (self.isGroup()) {
            if (mOnGroupClickListener != null) {
                return mOnGroupClickListener.onGroupClick(this, v, position, id);
            } else {
                if (self.isGroupExpanded()) {

                    int newPosition = mAdapter.collapseGroup(position, self);

                    if (mOnGroupCollapseListener != null) {
                        mOnGroupCollapseListener.onGroupCollapse(newPosition);
                    }

                } else {
                    mAdapter.setChildren(self, (LinkedList<MoreExpandableItemInfo>) mAdapter
                            .getChildren(position, self));

                    if (self.getLevel() == 0) {
                        MoreExpandableItemInfo currentExpanded = getCurrentExpanded();
                        int expandedGroupFlatPos = getCurrentExpandedPosition();
                        // If there is a expanded group above the clicked group,
                        // we need to adjust the position
                        // which will be passed in onGroupExpand();
                        if (expandedGroupFlatPos <= position && currentExpanded != null) {
                            // Take the parent and self into account, not just
                            // children
                            int currentExpandedGroupChildCount = currentExpanded.getChildrenCount();
                            if (currentExpanded.getParent() != null) {
                                currentExpandedGroupChildCount += currentExpanded.getLevel();
                            }
                            // Update expanded target position since some group
                            // is collapsed
                            // (its position should be moved up).
                            if (currentExpandedGroupChildCount > 0) {
                                position -= currentExpandedGroupChildCount;
                            }
                        }

                        if (currentExpanded != null && currentExpanded != self) {
                            mAdapter.collapseGroup(
                                    mAdapter.getIndexOfMoreExpandableItemInfo(currentExpanded),
                                    currentExpanded);
                        }
                    }
                    position = mAdapter.expandGroup(mAdapter.getIndexOfMoreExpandableItemInfo(self), self);

                    if (mOnGroupExpandListener != null) {
                        mOnGroupExpandListener.onGroupExpand(position);
                    }

                    smoothScrollToPosition(
                            mAdapter.getIndexOfMoreExpandableItemInfo(self)
                                    + self.getChildrenCount(), position);
                }
            }
            returnValue = true;
        } else {
            if (mOnChildClickListener != null) {
                int groupPosition = mAdapter.getIndexOfMoreExpandableItemInfo(self.getParent());
                int childPosition = MoreExpandableItemInfo
                        .getChildPosition(groupPosition, position);
                if (self.getParent() == null) {
                    groupPosition = position;
                    childPosition = -1;
                }
                return mOnChildClickListener
                        .onChildClick(this, v, groupPosition, childPosition, id);
            }
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Interface definition for a callback to be invoked when a group in this
     * more expandable list has been clicked.
     */
    public interface OnGroupClickListener {
        /**
         * Callback method to be invoked when a group in this more expandable
         * list has been clicked.
         *
         * @param parent The MoreExpandableListView where the click happened
         * @param v The view within the more expandable list/ListView that was
         *            clicked
         * @param groupPosition The group position that was clicked
         * @param id The id of the group that was clicked
         * @return True if the click was handled
         */
        boolean onGroupClick(MoreExpandableHtcListView parent, View v, int groupPosition, long id);
    }

    private OnGroupClickListener mOnGroupClickListener = null;

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        mOnGroupClickListener = onGroupClickListener;
    }

    /**
     * Interface definition for a callback to be invoked when a child in this
     * more expandable list has been clicked.
     */
    public interface OnChildClickListener {
        /**
         * Callback method to be invoked when a child in this more expandable
         * list has been clicked.
         *
         * @param parent The MoreExpandableListView where the click happened
         * @param v The view within the more expandable list/ListView that was
         *            clicked
         * @param groupPosition The group position that contains the child that
         *            was clicked
         * @param childPosition The child position within the group
         * @param id The id of the child that was clicked
         * @return True if the click was handled
         */
        boolean onChildClick(MoreExpandableHtcListView parent, View v, int groupPosition,
                int childPosition, long id);

    }

    private OnChildClickListener mOnChildClickListener;

    /**
     * Set the listener that will receive the notifications when a child (can
     * not be expanded) in this MoreExpandableHtcListView has been clicked.
     *
     * @param onChildClickListener The listener
     */
    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        mOnChildClickListener = onChildClickListener;
    }

    /** Used for being notified when a group is collapsed */
    public interface OnGroupCollapseListener {

        /**
         * Callback method to be invoked when a group in this more expandable
         * list has been collapsed.
         *
         * @param groupPosition The group position that was collapsed
         */
        void onGroupCollapse(int groupPosition);
    }

    private OnGroupCollapseListener mOnGroupCollapseListener;

    /**
     * Set the listener that will receive the notifications every time a group
     * is collapsed.
     *
     * @param onGroupCollapseListener The callback will run
     */
    public void setOnGroupCollapseListener(OnGroupCollapseListener onGroupCollapseListener) {
        mOnGroupCollapseListener = onGroupCollapseListener;
    }

    /** Used for being notified when a group is expanded */
    public interface OnGroupExpandListener {

        /**
         * Callback method to be invoked when a group in this more expandable
         * list has been expanded.
         *
         * @param groupPosition The group position that was expanded
         */
        void onGroupExpand(int groupPosition);
    }

    private OnGroupExpandListener mOnGroupExpandListener;

    /**
     * Set the listener that will receive the notifications when a group in this
     * MoreExpandableHtcListView has been expanded.
     *
     * @param onGroupExpandListener The listener
     */
    public void setOnGroupExpandListener(OnGroupExpandListener onGroupExpandListener) {
        mOnGroupExpandListener = onGroupExpandListener;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public boolean isGroupExpanded(int groupPosition) {
        if (mAdapter != null) {
            return mAdapter.isGroupExpanded(groupPosition);
        } else
            return false;

    }

    /**
     * set the divider between the expanded children item
     *
     * @param childDivider a drawable will be used as child divider
     */

    /**
     * collapse All expanded item This is not a blocking function
     */
    public void collapseAll() {
        mIsModified = true;
        if (mAdapter == null) {
            return;
        } else {
            mCollapseAllRunnable.start();
        }
    }

    /**
     * change children to the give item. This is not a blocking function NOTE:
     * Only the currently expanded group returned from getCurrentExpanded() is
     * allowed to be changed.
     *
     * @param self the item we want to change its children
     * @param children the new children of the given item
     */
    public void changeChildren(MoreExpandableItemInfo self,
            LinkedList<? extends MoreExpandableItemInfo> children) {
        mIsModified = true;
        if (mAdapter != null) {
            mChangeChildrenRunnable.start(self, children);
        } else
            return;

    }

    /**
     * append children to the given item. This is not a blocking function
     *
     * @param self the item to append children
     * @param newChildren the children to be appended
     */
    public void appendChildren(MoreExpandableItemInfo self,
            LinkedList<? extends MoreExpandableItemInfo> newChildren) {
        mIsModified = true;
        if (mAdapter != null) {
            mAppendChildrenRunnable.start(self, newChildren);
        } else
            return;
    }

    /**
     * change the root list. This is not a blocking function
     *
     * @param itemList the new root list
     */
    public void changeRoot(LinkedList<? extends MoreExpandableItemInfo> itemList) {
        mIsModified = true;
        if (mAdapter != null) {
            mChangeRootRunnable.start(itemList);
        } else
            return;
    }

    /**
     * append new root at the end of the current root. This is not a blocking
     * function.
     *
     * @param itemList the object list want to append
     */
    public void appendRoot(LinkedList<? extends MoreExpandableItemInfo> itemList) {
        mIsModified = true;
        if (mAdapter != null) {
            mAppendRootRunnable.start(itemList);
        } else
            return;
    }

    /**
     * delete the object at the position. This is not a blocking function.
     *
     * @param position the position of the deleted item
     */
    public void deleteItem(int position) {
        mIsModified = true;
        if (mAdapter != null) {
            mDeleteItemRunnable.start(position);
        } else
            return;
    }

    CollapseAllRunnable mCollapseAllRunnable = new CollapseAllRunnable();

    ChangeChildrenRunnable mChangeChildrenRunnable = new ChangeChildrenRunnable();

    AppendChildrenRunnable mAppendChildrenRunnable = new AppendChildrenRunnable();

    ChangeRootRunnable mChangeRootRunnable = new ChangeRootRunnable();

    AppendRootRunnable mAppendRootRunnable = new AppendRootRunnable();

    DeleteItemRunnable mDeleteItemRunnable = new DeleteItemRunnable();

    private class CollapseAllRunnable implements Runnable {

        private static final int STOP_MODE = 0;

        private static final int MOVE_MODE = 1;

        private int mMode = STOP_MODE;

        // For timing issue [
        private int mRunnableIndex = INACTIVERUNNABLEINDEX;

        int getRunnableIndex() {
            return mRunnableIndex;
        }

        // For timing issue ]

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void start() {
            mMode = MOVE_MODE;
            // For timing issue [
            mRunnableIndex = ++mActiveRunnableCount;
            // For timing issue ]
            post(this);
        }

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void run() {
            if (mMode == STOP_MODE) {
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            } else if (mAnimationRunning || isAnyRunnableBefore(mRunnableIndex)) { // For
                                                                                   // timing
                                                                                   // issue
                postDelayed(this, 100);
                return;
            } else {
                mAdapter.collapseAll();
                mMode = STOP_MODE;
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            }
        }

    }

    private class ChangeChildrenRunnable implements Runnable {

        MoreExpandableItemInfo mSelf = null;

        LinkedList<MoreExpandableItemInfo> mChildren = null;

        private static final int STOP_MODE = 0;

        private static final int MOVE_MODE = 1;

        private int mMode = STOP_MODE;

        // For timing issue [
        private int mRunnableIndex = INACTIVERUNNABLEINDEX;

        int getRunnableIndex() {
            return mRunnableIndex;
        }

        // For timing issue ]

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void start(MoreExpandableItemInfo self,
                LinkedList<? extends MoreExpandableItemInfo> children) {
            mSelf = self;
            mChildren = (LinkedList<MoreExpandableItemInfo>) children;
            mMode = MOVE_MODE;
            // For timing issue [
            mRunnableIndex = ++mActiveRunnableCount;
            // For timing issue ]
            post(this);
        }

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void run() {
            if (mMode == STOP_MODE) {
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            } else if (mAnimationRunning || isAnyRunnableBefore(mRunnableIndex)) { // For
                                                                                   // timing
                                                                                   // issue
                postDelayed(this, 100);
                return;
            } else {
                mAdapter.changeChildren(mSelf, mChildren);
                mMode = STOP_MODE;
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            }
        }

    }

    private class AppendChildrenRunnable implements Runnable {

        MoreExpandableItemInfo mSelf = null;

        // For timing issue [
        LinkedList<MoreExpandableItemInfo> mNewChildren = new LinkedList<MoreExpandableItemInfo>();

        // For timing issue ]
        private static final int STOP_MODE = 0;

        private static final int MOVE_MODE = 1;

        private int mMode = STOP_MODE;

        // For timing issue [
        private int mRunnableIndex = INACTIVERUNNABLEINDEX;

        int getRunnableIndex() {
            return mRunnableIndex;
        }

        // For timing issue ]

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void start(MoreExpandableItemInfo self,
                LinkedList<? extends MoreExpandableItemInfo> newChildren) {
            boolean isTargetGroupExpanded = self != null && self.isGroupExpanded();
            if (!isTargetGroupExpanded) {
                if (HtcBuildFlag.Htc_DEBUG_flag) {
                    Log.d(TAG, "Don't append children to a collapsed group!");
                }
                return;
            }

            // For timing issue [
            if (mSelf == null) {
                mSelf = self;
                mNewChildren = (LinkedList<MoreExpandableItemInfo>) newChildren;
            } else if (mSelf.equals(self)) {
                mNewChildren.addAll((LinkedList<MoreExpandableItemInfo>) newChildren);
            } else {
                mSelf = self;
                mNewChildren = (LinkedList<MoreExpandableItemInfo>) newChildren;
            }
            // For timing issue ]
            // For timing issue [
            if (mRunnableIndex == INACTIVERUNNABLEINDEX)
                mRunnableIndex = ++mActiveRunnableCount;
            // For timing issue ]
            mMode = MOVE_MODE;
            post(this);
        }

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void run() {
            if (mMode == STOP_MODE) {
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            } else if (mAnimationRunning || isAnyRunnableBefore(mRunnableIndex)) { // For
                                                                                   // timing
                                                                                   // issue
                postDelayed(this, 100);
                return;
            } else {
                mMode = STOP_MODE;
                boolean isTargetGroupExpanded = mSelf != null && mSelf.isGroupExpanded();
                if (isTargetGroupExpanded) {
                    mAdapter.appendChildren(mSelf, mNewChildren);
                } else {
                    if (HtcBuildFlag.Htc_DEBUG_flag) {
                        Log.d(TAG,
                                "Since the target group has been collapsed, we should clear the cached children in AppendChildrenRunnable.");
                    }
                    mNewChildren.clear();
                }
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            }
        }
    }

    private class ChangeRootRunnable implements Runnable {

        LinkedList<MoreExpandableItemInfo> mItemList = null;

        private static final int STOP_MODE = 0;

        private static final int MOVE_MODE = 1;

        private int mMode = STOP_MODE;

        // For timing issue [
        private int mRunnableIndex = INACTIVERUNNABLEINDEX;

        int getRunnableIndex() {
            return mRunnableIndex;
        }

        // For timing issue ]

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void start(LinkedList<? extends MoreExpandableItemInfo> itemList) {
            mItemList = (LinkedList<MoreExpandableItemInfo>) itemList;
            mMode = MOVE_MODE;
            // For timing issue [
            mRunnableIndex = ++mActiveRunnableCount;
            // For timing issue ]
            post(this);
        }

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void run() {
            if (mMode == STOP_MODE) {
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            } else if (mAnimationRunning || isAnyRunnableBefore(mRunnableIndex)) { // For
                                                                                   // timing
                                                                                   // issue
                postDelayed(this, 100);
                return;
            } else {
                mAdapter.changeRoot(mItemList);
                mMode = STOP_MODE;
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            }
        }

    }

    private class AppendRootRunnable implements Runnable {
        // For timing issue [
        // LinkedList<MoreExpandableItemInfo> mItemList = null;
        LinkedList<MoreExpandableItemInfo> mItemList = new LinkedList<MoreExpandableItemInfo>();

        // For timing issue ]

        private static final int STOP_MODE = 0;

        private static final int MOVE_MODE = 1;

        private int mMode = STOP_MODE;

        // For timing issue [
        private int mRunnableIndex = INACTIVERUNNABLEINDEX;

        int getRunnableIndex() {
            return mRunnableIndex;
        }

        // For timing issue ]

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void start(LinkedList<? extends MoreExpandableItemInfo> itemList) {
            // For timing issue [
            // mItemList = (LinkedList<MoreExpandableItemInfo>)itemList;
            mItemList.addAll((LinkedList<MoreExpandableItemInfo>) itemList);
            // For timgin issue ]
            mMode = MOVE_MODE;
            // For timing issue [
            if (mRunnableIndex == INACTIVERUNNABLEINDEX)
                mRunnableIndex = ++mActiveRunnableCount;
            // For timing issue ]
            post(this);
        }

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void run() {
            if (mMode == STOP_MODE) {
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            } else if (mAnimationRunning || isAnyRunnableBefore(mRunnableIndex)) { // For
                                                                                   // timing
                                                                                   // issue
                postDelayed(this, 100);
                return;
            } else {
                mAdapter.appendRoot(mItemList);
                mMode = STOP_MODE;
                // For timing issue [
                mItemList.clear();
                // For timing issue ]
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            }
        }

    }

    private class DeleteItemRunnable implements Runnable {

        // For timing issue [

        private ArrayList<Integer> mDeletePositions = new ArrayList<Integer>();

        // For timing issue ]

        private static final int STOP_MODE = 0;

        private static final int MOVE_MODE = 1;

        private int mMode = STOP_MODE;

        // For timing issue [
        private int mRunnableIndex = INACTIVERUNNABLEINDEX;

        int getRunnableIndex() {
            return mRunnableIndex;
        }

        // For timing issue ]

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void start(int position) {
            // For timing issue [
            mDeletePositions.add(position);
            // For timing issue ]
            mMode = MOVE_MODE;
            // For timing issue [
            if (mRunnableIndex == INACTIVERUNNABLEINDEX)
                mRunnableIndex = ++mActiveRunnableCount;
            // For timing issue ]
            post(this);
        }

        /**
         * Hide Automatically by SDK Team [U12000]
         *
         * @hide
         */
        public void run() {
            if (mMode == STOP_MODE) {
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            } else if (mAnimationRunning || isAnyRunnableBefore(mRunnableIndex)) { // For
                                                                                   // timing
                                                                                   // issue
                postDelayed(this, 100);
                return;
            } else {
                // For timing issue [
                // mAdapter.deleteItem(mPosition);
                for (Integer i : mDeletePositions) {
                    mAdapter.deleteItem(i);
                }
                mDeletePositions.clear();
                // For timing issue ]
                mMode = STOP_MODE;
                // For timing issue [
                mRunnableIndex = INACTIVERUNNABLEINDEX;
                ResetActiveRunnableCount();
                // For timing issue ]
                return;
            }
        }

    }

    // for animation
    @ExportedProperty(category = "CommonControl")
    private boolean mAnimationRunning = false;

    // for flyer
    private static final int MAX_CACHE = 16;

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mAnimationRunning)
            return false;
        else
            return super.onInterceptTouchEvent(ev);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mAnimationRunning)
            return true;
        else {
            boolean handled = super.onTouchEvent(ev);
            // Enable HtcListView Press Animation for child item

            return handled;
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (mAnimationRunning)
            return true;
        else
            return super.onTrackballEvent(ev);
    }

    // For timing issue [
    @ExportedProperty(category = "CommonControl")
    private int mActiveRunnableCount = 0;

    private static final int INACTIVERUNNABLEINDEX = Integer.MAX_VALUE;

    boolean isAnyRunnableBefore(int runnableIndex) {
        if (mCollapseAllRunnable.getRunnableIndex() < runnableIndex)
            return true;
        if (mChangeChildrenRunnable.getRunnableIndex() < runnableIndex)
            return true;
        if (mAppendChildrenRunnable.getRunnableIndex() < runnableIndex)
            return true;
        if (mChangeRootRunnable.getRunnableIndex() < runnableIndex)
            return true;
        if (mAppendRootRunnable.getRunnableIndex() < runnableIndex)
            return true;
        if (mDeleteItemRunnable.getRunnableIndex() < runnableIndex)
            return true;
        return false;
    }

    private void ResetActiveRunnableCount() {
        if (mCollapseAllRunnable.getRunnableIndex() != INACTIVERUNNABLEINDEX)
            return;
        if (mChangeChildrenRunnable.getRunnableIndex() != INACTIVERUNNABLEINDEX)
            return;
        if (mAppendChildrenRunnable.getRunnableIndex() != INACTIVERUNNABLEINDEX)
            return;
        if (mChangeRootRunnable.getRunnableIndex() != INACTIVERUNNABLEINDEX)
            return;
        if (mAppendRootRunnable.getRunnableIndex() != INACTIVERUNNABLEINDEX)
            return;
        if (mDeleteItemRunnable.getRunnableIndex() != INACTIVERUNNABLEINDEX)
            return;
        mActiveRunnableCount = 0;
    }

    // For timing issue ]

    /**
     * Please MUST call Destroy() function when your activity is destroyed to
     * prevent memory leak.
     */
    public void Destroy() {
        mCollapseAllRunnable = null;
        mChangeChildrenRunnable = null;
        mAppendChildrenRunnable = null;
        mChangeRootRunnable = null;
        mAppendRootRunnable = null;
        mDeleteItemRunnable = null;
        if (mAdapter != null) {
            mAdapter.Destroy();
        }
        mAdapter = null;
    }

    // For recover state
    @ExportedProperty(category = "CommonControl")
    private boolean mIsModified = false;

    /**
     * You can use this function to expand one path without animation. This
     * function should be call after you call set adapter but before any other
     * operation is perform.
     *
     * @param self The MoreExpandableItemInfo object you want to expand and set
     *            children
     * @param children The children you want to set to the self item
     * @throws Exception 1. if the adapter is null. 2. self is null. 3. self is
     *             not in List. 4. self is not a group item. 5. self is already
     *             expanded. 6. self is not a root node but it does not have
     *             parent. 7. Another path is already expanded. 8. Self is not a
     *             child of the current expanded item. 9. Other operation has
     *             been done.
     */
    public void expandAndSetChildrenWithOutAnimation(MoreExpandableItemInfo self,
            LinkedList<? extends MoreExpandableItemInfo> children) throws Exception {
        if (mIsModified)
            throw new Exception("Other operation has been done");

        if (mAdapter == null)
            throw new Exception("mAdapter is null");

        if (self == null)
            throw new Exception("self is null");

        if (mAnimationRunning)
            throw new Exception("Other operation has been done");

        int checkPos = mAdapter.getIndexOfMoreExpandableItemInfo(self);
        if (checkPos == -1 || checkPos < 0 || checkPos >= mAdapter.getCount())
            throw new Exception("self is not in list");
        if (!self.isGroup())
            throw new Exception("self is not a group item");
        if (self.isGroupExpanded())
            throw new Exception("self is already expanded");
        if (self.getLevel() != 0 && self.getParent() == null)
            throw new Exception("self is not a root node but it does not have parent");
        if (self.getLevel() == 0 && mAdapter.getCurrentExpanded() != null)
            throw new Exception("Another path is already expanded");
        if (self.getLevel() != 0 && !self.getParent().equals(mAdapter.getCurrentExpanded())) {
            throw new Exception("Self is not a child of the current expanded item");
        }

        if (self.getLevel() == 0 && mAdapter.getCurrentExpanded() == null) {
            mAdapter.expandAndSetChildrenWithOutAnimation(checkPos, self,
                    (LinkedList<MoreExpandableItemInfo>) children);
            checkPos = mAdapter.getIndexOfMoreExpandableItemInfo(self);
            setSelectionFromTop(checkPos, 0);
        } else if (self.getLevel() != 0 && self.getParent().equals(mAdapter.getCurrentExpanded())) {
            mAdapter.expandAndSetChildrenWithOutAnimation(checkPos, self,
                    (LinkedList<MoreExpandableItemInfo>) children);
            checkPos = mAdapter.getIndexOfMoreExpandableItemInfo(self);
            setSelectionFromTop(checkPos, 0);
        }
    }

    /**
     * change the root list. This is a blocking function and can only be call
     * before any other operation is perform
     *
     * @param itemList the new root list
     * @throws Exception 1. Other operation has been done. 2. mAdapter is null.
     *             3. itemList is null.
     */
    public void BlockingChangeRoot(LinkedList<? extends MoreExpandableItemInfo> itemList)
            throws Exception {
        if (mIsModified)
            throw new Exception("Other operation has been done");

        if (mAdapter == null)
            throw new Exception("mAdapter is null");

        if (mAnimationRunning)
            throw new Exception("Other operation has been done");

        if (itemList == null)
            throw new Exception("itemList is null");

        mAdapter.changeRoot(itemList);

    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        /**
         * Draw the indicator
         */
        drawIndicator(canvas);

        // Draw children, etc.
        super.dispatchDraw(canvas);

        final int firstPosition = getFirstVisiblePosition();
        final int headerViewsCount = getHeaderViewsCount();
        ListAdapter adapter = getAdapter();
        final int itemCount = adapter != null ? adapter.getCount() : 0;
        final int lastChildFlPos = itemCount - getFooterViewsCount() - headerViewsCount - 1;
        final int childCount = getChildCount();
        final int myB = getBottom();
        View item;
        int t, b;
        for (int i = 0, childFlPos = firstPosition - headerViewsCount; i < childCount; i++, childFlPos++) {

            if (childFlPos < 0) {
                // This child is header
                continue;
            } else if (childFlPos > lastChildFlPos) {
                // This child is footer, so are all subsequent children
                break;
            }

            item = getChildAt(i);
            if (item == null)
                throw new IllegalArgumentException(
                        "The child view is Null in dispatchDraw !!! If you have any data changed, please call notifyDataSetChanged");
            t = item.getTop();
            b = item.getBottom();
            // This item isn't on the screen
            if ((b < 0) || (t > myB))
                continue;
        }

        int dividerHeight = getDividerHeight();
        int expandDividerBottom = (mExpandDividerHeight - dividerHeight)/2;
        for (int i = 0; i < getChildCount(); i++) {
            int adjustPosition = firstPosition + i - headerViewsCount;
            MoreExpandableItemInfo itemInfo = (MoreExpandableItemInfo) mAdapter
                    .getItem(adjustPosition);
            View nextItem = null;
            if (itemInfo == null)
                continue;
            boolean isExpandedRootGroup = itemInfo.isGroupExpanded() && itemInfo.getLevel() == 0;
            if (isExpandedRootGroup) {
                item = getChildAt(i);
                nextItem = getChildAt(i - 1);
                int itemTranslationY = (int) item.getTranslationY();
                int direction = firstPosition == 0 ? -1 : 1;
                int offset = nextItem == null ? itemTranslationY : itemTranslationY + direction
                        * (int) ((direction * (nextItem.getTranslationY() - itemTranslationY)) / 2);
                b = item.getTop() + (adjustPosition > 0 ? expandDividerBottom : mExpandDividerHeight)
                        + (adjustPosition > 0 ? offset : 0);
                mExpandDivider.setBounds(mDividerMargin, b - mExpandDividerHeight, getWidth() - mDividerMargin, b);
                mExpandDivider.draw(canvas);
            }

            if (itemInfo.getLevel() > 0 && itemInfo.getLevel() == mAdapter.getExpandedLevel()
                    && itemInfo.isLastChild() || isExpandedRootGroup
                    && itemInfo.getChildrenCount() == 0 || itemInfo.isGroupExpanded()
                    && itemInfo.getChildrenCount() == 0) {
                item = getChildAt(i);
                nextItem = getChildAt(i + 1);
                int offset = nextItem == null ? (int) item.getTranslationY() : (int) item
                        .getTranslationY()
                        + (int) ((nextItem.getTranslationY() - item.getTranslationY()) / 2);
                int lastItemIndex = mAdapter.getCount() - 1;
                final int visibleLastItemBottom = getBottom() - getTop() - getListPaddingBottom()
                        + getScrollY() - dividerHeight;
                b = item.getBottom()
                        + (adjustPosition < lastItemIndex ? dividerHeight + expandDividerBottom : 0)
                        + (adjustPosition == lastItemIndex
                                && item.getBottom() == visibleLastItemBottom
                                && item.getTranslationY() < 0 ? 0 : offset);
                mExpandDivider.setBounds(mDividerMargin, b - mExpandDividerHeight, getWidth()
                        - mDividerMargin, b);
                mExpandDivider.draw(canvas);
            }
        }
    }

    /**
     * Setup the indicator state and draw it.
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        ListAdapter adapter = getAdapter();
        if (adapter == null)
            return;
        final int headerViewsCount = getHeaderViewsCount();
        final int lastChildFlPos = adapter.getCount() - getFooterViewsCount() - headerViewsCount
                - 1;
        final int myB = getBottom();
        final int childCount = getChildCount();
        View item;
        int t, b;
        for (int i = 0, childFlPos = getFirstVisiblePosition() - headerViewsCount; i < childCount; i++, childFlPos++) {
            if (childFlPos < 0) {
                // This child is header
                continue;
            } else if (childFlPos > lastChildFlPos) {
                // This child is footer, so are all subsequent children
                break;
            }
            item = getChildAt(i);
            if (item == null)
                throw new IllegalArgumentException(
                        "The child view is Null in drawIndicator !!! If you have any data changed, please call notifyDataSetChanged");
            t = item.getTop();
            b = item.getBottom();
            // This item isn't on the screen
            if ((b < 0) || (t > myB))
                continue;

            // Get more expandable list-related info for this item
            MoreExpandableItemInfo itemInfo = mAdapter.getMoreExpandableItemInfo(childFlPos);
            HtcIndicatorButton indicatorButton = null;
            if (itemInfo != null && itemInfo.isGroup() && item instanceof ViewGroup) {
                if (itemInfo.isGroupExpanded()) {
                    View indicatorView = ((ViewGroup) item)
                            .findViewById(R.id.htc_expandable_indicator);
                    if (indicatorView != null && indicatorView instanceof HtcIndicatorButton) {
                        indicatorButton = (HtcIndicatorButton) indicatorView;
                        indicatorButton.setExpanded(true);
                    }
                } else {
                    View indicatorView = ((ViewGroup) item)
                            .findViewById(R.id.htc_expandable_indicator);
                    if (indicatorView != null && indicatorView instanceof HtcIndicatorButton) {
                        indicatorButton = (HtcIndicatorButton) indicatorView;
                        indicatorButton.setExpanded(false);
                    }
                }
            }
        }
    }

    @Override
    void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        int flatListPosition = childIndex + getFirstVisiblePosition();

        if (flatListPosition >= 0 && mAdapter != null) {
            final int adjustedPosition = flatListPosition - this.getHeaderViewsCount();
            MoreExpandableItemInfo itemInfo = (MoreExpandableItemInfo) mAdapter
                    .getItem(adjustedPosition);
            MoreExpandableItemInfo nextItemInfo = (MoreExpandableItemInfo) mAdapter
                    .getItem(adjustedPosition + 1);
            final int listBottom = getBottom() - getTop() - getListPaddingBottom() + getScrollY();
            if ((nextItemInfo != null && nextItemInfo.isGroupExpanded() && nextItemInfo.getLevel() == 0)
                    || itemInfo != null
                    && itemInfo.isLastChild()
                    && itemInfo.getLevel() == mAdapter.getExpandedLevel()
                    || adjustedPosition == mAdapter.getCount() - 1
                    && getChildAt(childIndex).getBottom() + getDividerHeight() == listBottom) {
                return;
            }
        }

        // Only proceed as possible child if the divider isn't above all items
        // (if it is above
        // all items, then the item below it has to be a group)
        if (flatListPosition >= 0 && mShouldDrawFakeDivider) {
            final int adjustedPosition = flatListPosition - getHeaderViewsCount();
            MoreExpandableItemInfo itemInfo = mAdapter.getMoreExpandableItemInfo(adjustedPosition);
            // If this item is a child, or it is a non-empty group that is
            // expanded
            if (itemInfo != null
                    && (itemInfo.getLevel() == mAdapter.getExpandedLevel() || (itemInfo
                            .isGroupExpanded() && itemInfo.isGroup()))) {
                // These are the cases where we draw the child divider

                final Drawable divider = mFakeDivider;
                divider.setBounds(bounds);
                divider.draw(canvas);
                return;
            }
        }

        // Otherwise draw the default divider
        super.drawDivider(canvas, bounds, flatListPosition);
    }
}
