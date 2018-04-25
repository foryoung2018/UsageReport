
package com.htc.lib1.cc.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;

/**
 * The MoreExpandableBaseAdapter is the adapter used by
 * MoreExpandableHtcListView. It is is responsible for maintain the data show on
 * the MoreExpandableHtcListView. There are some restrictions about the view
 * return in getGroupView() and getChildView() <li>The root of the view(layout)
 * return must be RelativeLayout</li> <li>The layout_height of the view must be
 * an exact number. It can not set fill_parent, wrap_content, or match_parent</li>
 * <li>The view can not have any background, event if you set the background. It
 * will be override by MoreExpandableBaseAdapter</li>
 */
public abstract class MoreExpandableBaseAdapter extends BaseAdapter {
    private static String TAG = "MoreExpandableBaseAdapter";

    private LinkedList<MoreExpandableItemInfo> mItemList = null;

    private Context mContext = null;

    private LayoutInflater mInflater = null;

    private ArrayList<MoreExpandableItemInfo> mExpandedList = null;

    private int mIndicatorRegionWidth = 0;

    private static int sActionButtonWidth = 0;

    private static final float ACTION_BUTTON_WIDTH_RATIO = 0.147f;

    /**
     * Constructor.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public MoreExpandableBaseAdapter(Context context) {
        init(context, null);
    }

    /**
     * Constructor.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param itemList The list store information of the item in level zero.
     */
    public MoreExpandableBaseAdapter(Context context,
            LinkedList<? extends MoreExpandableItemInfo> itemList) {
        init(context, (LinkedList<MoreExpandableItemInfo>) itemList);
    }

    private void init(Context context, LinkedList<MoreExpandableItemInfo> itemList) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mExpandedList = new ArrayList<MoreExpandableItemInfo>();
        if (itemList == null)
            mItemList = new LinkedList<MoreExpandableItemInfo>();
        else
            mItemList = (LinkedList<MoreExpandableItemInfo>) itemList;

        // Follow 14.7% rule in Sense 5+.
        mIndicatorRegionWidth = getActionButtonWidth();
    }

    int getActionButtonWidth() {
        if (sActionButtonWidth == 0)
        {
            int portraitWindowWidth = 0;
            DisplayMetrics metrics = new DisplayMetrics();
            metrics = mContext.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            if (width < height)
                portraitWindowWidth = width;
            else
                portraitWindowWidth = height;
            int actionButtonWidth = (int) (ACTION_BUTTON_WIDTH_RATIO * portraitWindowWidth);
            sActionButtonWidth = (actionButtonWidth & 1) == 1  ? ++actionButtonWidth : actionButtonWidth;
        }
        return sActionButtonWidth;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items
     */
    public int getCount() {
        return mItemList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set
     *
     * @param position Position of the item whose data we want within the
     *            adapter's data set.
     * @return The data at the specified position.
     */
    public Object getItem(int position) {
        if (position >= 0 && position < getCount())
            return mItemList.get(position);
        else
            return null;
    }

    /**
     * Get the item id associated with the specified position in the data set.
     *
     * @param position Index of the item
     * @return The item id at the specified position
     */
    public long getItemId(int position) {
        if (position >= 0 && position < getCount())
            return mItemList.get(position).getId();
        else
            return 0;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *            returned) within the group
     * @param isLastChild Whether the child is the last child within the group
     * @param convertView the old view to reuse, if possible. You should check
     *            that this view is non-null and of an appropriate type before
     *            using. If it is not possible to convert this view to display
     *            the correct data, this method can create a new view. It is not
     *            guaranteed that the convertView will have been previously
     *            created by
     *            {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     * @param parent the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    public abstract View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent);

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *            returned
     * @param isExpanded whether the group is expanded or collapsed
     * @param convertView the old view to reuse, if possible. You should check
     *            that this view is non-null and of an appropriate type before
     *            using. If it is not possible to convert this view to display
     *            the correct data, this method can create a new view. It is not
     *            guaranteed that the convertView will have been previously
     *            created by
     *            {@link #getGroupView(int, boolean, View, ViewGroup)}.
     * @param parent the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    public abstract View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent);

    /**
     * get the children of a given group item
     *
     * @param groupPosition the position the group item
     * @param self the group item which contains information about itself
     * @return the list of children of this given group
     */
    public abstract LinkedList<? extends MoreExpandableItemInfo> getChildren(int groupPosition,
            MoreExpandableItemInfo self);

    /**
     * @param groupPosition the position of a item want to know it is expanded
     *            or not
     * @return true if the item at the given position is expanded
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public boolean isGroupExpanded(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < getCount())
            return mItemList.get(groupPosition).isGroupExpanded();
        else
            return false;
    }

    /**
     * change children to the give item
     *
     * @param self the item we want to change its children
     * @param children the new children of the given item
     * @return true if success only changeChildren to the currently expanded
     *         item will return true otherwise false
     */
    boolean changeChildren(MoreExpandableItemInfo self,
            LinkedList<? extends MoreExpandableItemInfo> children) {

        int expandLevel = mExpandedList.size() - 1;
        if (children == null) {
            return false;
        } else if (self == null) {
            return false;
        } else if (!self.isGroup()) {
            return false;
        } else if (expandLevel == -1) {
            self.setChildren((LinkedList<MoreExpandableItemInfo>) children);
            return false;
        } else if (mExpandedList.get(expandLevel).equals(self)) {
            int selfPosition = mItemList.indexOf(self);
            Iterator<MoreExpandableItemInfo> it = self.getChildren().iterator();
            MoreExpandableItemInfo temp;
            while (it.hasNext()) {
                temp = it.next();
                mItemList.remove(temp);
                it.remove();
            }

            self.setChildren((LinkedList<MoreExpandableItemInfo>) children);
            mItemList.addAll(selfPosition + 1, self.getChildren());
            notifyDataSetChanged();
            return true;
        } else {
            // If there is a child expanded for the given group item, changing
            // the group's children
            // would cause the isChildExpanded() status inconsistent since
            // setChildren() would not update the status.
            if (self.isChildExpanded() && HtcBuildFlag.Htc_DEBUG_flag) {
                Log.d(TAG,
                        Log.getStackTraceString(new IllegalStateException(
                                "It is only allowed to change the currently expanded group's children!"
                                        + " Item's level: " + self.getLevel()))
                                + ", Expand Level: " + expandLevel);
            }
            // self.setChildren((LinkedList<MoreExpandableItemInfo>)children);
            return false;
        }
    }

    /**
     * append children to the given item
     *
     * @param self the item to append children
     * @param newChildren the children to be appended
     * @return true if success only appendChildren to the currently expanded
     *         item will return true otherwise false
     */
    boolean appendChildren(MoreExpandableItemInfo self,
            LinkedList<? extends MoreExpandableItemInfo> newChildren) {

        int expandLevel = mExpandedList.size() - 1;

        if (newChildren == null) {
            return false;
        } else if (self == null) {
            return false;
        } else if (!self.isGroup()) {
            return false;
        } else if (expandLevel == -1) {
            return false;
        } else if (mExpandedList.get(expandLevel).equals(self)) {
            int selfPosition = mItemList.indexOf(self);
            if (self.getChildrenCount() == 0) {
                return changeChildren(self, newChildren);
            } else {
                LinkedList<MoreExpandableItemInfo> newChildrenList = new LinkedList<MoreExpandableItemInfo>();
                Iterator<MoreExpandableItemInfo> it = self.getChildren().iterator();
                MoreExpandableItemInfo temp;
                while (it.hasNext()) {
                    temp = it.next();
                    newChildrenList.add(temp);
                    mItemList.remove(temp);
                    it.remove();
                }
                newChildrenList.addAll((LinkedList<MoreExpandableItemInfo>) newChildren);
                self.setChildren((LinkedList<MoreExpandableItemInfo>) newChildrenList);
                mItemList.addAll(selfPosition + 1, self.getChildren());
                notifyDataSetChanged();
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * @deprecated [Alternative solution] use MoreExpandableHtcListView
     *             changeRoot() to change the root of your data
     * @param itemList
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Deprecated
    public void setMoreExpandableItemInfo(LinkedList<? extends MoreExpandableItemInfo> itemList) {
        changeRoot(itemList);
    }

    /**
     * change the root list
     *
     * @param itemList the new root list
     */
    void changeRoot(LinkedList<? extends MoreExpandableItemInfo> itemList) {
        mExpandedList.clear();
        mItemList.clear();
        mItemList = (LinkedList<MoreExpandableItemInfo>) itemList;
        notifyDataSetChanged();
    }

    /**
     * append new root at the end of the current root
     *
     * @param itemList the object list want to append
     */
    void appendRoot(LinkedList<? extends MoreExpandableItemInfo> itemList) {
        mItemList.addAll((LinkedList<MoreExpandableItemInfo>) itemList);
        notifyDataSetChanged();
    }

    /**
     * @return the object which is currently expanded
     */
    MoreExpandableItemInfo getCurrentExpanded() {
        if (mExpandedList.isEmpty()) {
            return null;
        } else {
            return mExpandedList.get(mExpandedList.size() - 1);
        }
    }

    /**
     * delete the object at the position
     *
     * @param position the position of the deleted item
     */
    void deleteItem(int position) {
        MoreExpandableItemInfo self = (MoreExpandableItemInfo) getItem(position);
        if (self == null)
            return;
        MoreExpandableItemInfo parent = self.getParent();
        if (self.isGroupExpanded()) {
            deleteItem(position, self);
            if (parent == null) {
                mItemList.remove(self);
            } else {
                showChildren(mItemList.indexOf(parent), parent, self);
            }
        } else {
            if (parent != null) {
                if (parent.getChildrenCount() == 1) {
                    deleteItem(mItemList.indexOf(parent), parent);
                    MoreExpandableItemInfo grandParent = parent.getParent();
                    if (grandParent != null) {
                        showChildren(mItemList.indexOf(grandParent), grandParent, parent);
                    }
                } else {
                    parent.getChildren().remove(self);
                    mItemList.remove(self);
                }
            } else {
                mItemList.remove(self);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Get a View that displays the data at the specified position in the data
     * set.
     *
     * @param position The position of the item within the adapter's data set of
     *            the item whose view we want.
     * @param convertView The old view to reuse.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        MoreExpandableItemInfo self = (MoreExpandableItemInfo) getItem(position);
        if (self == null)
            throw new IllegalStateException("self is null");
        MoreExpandableItemInfo mother = self.getParent();
        int groupPosition = Integer.MIN_VALUE;
        int childPosition = Integer.MIN_VALUE;
        boolean isLastChild = self.isLastChild();
        boolean isGroupExpanded = self.isGroupExpanded();

        if (mother != null) {
            groupPosition = mItemList.indexOf(mother);
            childPosition = MoreExpandableItemInfo.getChildPosition(groupPosition, position);
        }

        if (self.isGroup()) {
            view = getGroupView(position, isGroupExpanded, convertView, parent);

            // for add indicator
            // [ Add Htc Indicator Button: Josh Kang
            View indicator = view.findViewById(R.id.htc_expandable_indicator);
            if (indicator == null) {
                indicator = mInflater.inflate(R.layout.more_expandable_list_indicator,
                        (ViewGroup) view, false);
                if (view instanceof HtcListItem) {
                    HtcListItem item = (HtcListItem) view;
                    item.addView(indicator);
                    item.setLastComponentAlign(true);
                } else {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) indicator
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    int indicatorWidth = indicator instanceof HtcIndicatorButton ? ((HtcIndicatorButton) indicator)
                            .getIndicatorWidth() : 0;
                    lp.rightMargin = (mIndicatorRegionWidth - indicatorWidth) / 2;
                    ((ViewGroup) view).addView(indicator, lp);
                }
            }

            // Add Htc Indicator Button: Josh Kang ]
            if (indicator != null)
                indicator.setVisibility(View.VISIBLE);
        } else {
            if (mother == null) {
                groupPosition = position;
                childPosition = -1;
            }
            view = getChildView(groupPosition, childPosition, isLastChild, convertView, parent);

            // [ S40 Animation: Josh Kang
            // for remove indicator
            View indicator = view.findViewById(R.id.htc_expandable_indicator);
            if (indicator != null) {
                indicator.setVisibility(View.GONE);
                // ((ViewGroup)view).removeView(indicator);
            }
            // S40 Animation: Josh Kang ]
        }
        return view;

    }

    /**
     * called when a group is expand to add children to the list
     *
     * @param self the item to set the children
     * @param children the list of the children
     */
    void setChildren(MoreExpandableItemInfo self, LinkedList<MoreExpandableItemInfo> children) {
        self.setChildren(children);
    }

    MoreExpandableItemInfo getMoreExpandableItemInfo(int position) {
        return (MoreExpandableItemInfo) getItem(position);
    }

    int getIndexOfMoreExpandableItemInfo(MoreExpandableItemInfo self) {
        if (self == null)
            return Integer.MIN_VALUE;
        else
            return mItemList.indexOf(self);
    }

    int expandGroup(int position, MoreExpandableItemInfo self) {

        mItemList.addAll(position + 1, self.getChildren());

        int expandedLevel = mExpandedList.size() - 1;
        int selfLevel = self.getLevel();
        if (expandedLevel >= selfLevel) {
            collapseGroup(mExpandedList.get(selfLevel));
        }
        hideSibling(self);
        mExpandedList.add(self);

        self.setGroupExpanded();
        notifyDataSetChanged();
        return mItemList.indexOf(self);
    }

    int collapseGroup(int position, MoreExpandableItemInfo self) {

        collapseGroup(self);
        showSibling(self);
        notifyDataSetChanged();
        return mItemList.indexOf(self);
    }

    int collapseChild(int position, MoreExpandableItemInfo self) {

        MoreExpandableItemInfo expandedChild = self.getExpandedChild();
        if (expandedChild != null) {
            collapseGroup(expandedChild);
            showChildren(position, self, expandedChild);
            notifyDataSetChanged();
        }
        return mItemList.indexOf(self);
    }

    boolean collapseGroup(MoreExpandableItemInfo self) {

        if (self.isChildExpanded()) {
            int selfLevel = self.getLevel();
            if (selfLevel >= 0 && selfLevel < mExpandedList.size()) {
                MoreExpandableItemInfo temp = self.getExpandedChild();
                if (temp == null)
                    throw new IllegalStateException("getExpandedChild is null");
                collapseGroup(temp);
                temp.onCollapseDelete();
                mItemList.remove(temp);
                self.getChildren().clear();
            }
        } else {
            LinkedList<MoreExpandableItemInfo> children = self.getChildren();
            if (children != null) {
                Iterator<MoreExpandableItemInfo> it = children.iterator();
                MoreExpandableItemInfo temp;
                while (it.hasNext()) {
                    temp = it.next();
                    temp.onCollapseDelete();
                    mItemList.remove(temp);
                    it.remove();
                }
            }
        }
        self.setGroupCollapse();
        mExpandedList.remove(self);
        return true;
        /*
         * LinkedList<MoreExpandableItemInfo> children = self.getChildren();
         * Iterator<MoreExpandableItemInfo> it = children.iterator();
         * MoreExpandableItemInfo temp; while( it.hasNext() ) { temp =
         * it.next(); if( temp.isGroupExpanded() && mExpandedList != null &&
         * !mExpandedList.isEmpty() && mExpandedList.contains(temp) ) {
         * collapseGroup(temp); mItemList.remove(temp); it.remove(); } else {
         * mItemList.remove(temp); it.remove(); } } self.setGroupCollapse();
         * mExpandedList.remove(self); return true;
         */
    }

    private boolean hideSibling(MoreExpandableItemInfo self) {
        MoreExpandableItemInfo parent = self.getParent();
        if (parent == null) {
            return true;
        } else {
            LinkedList<MoreExpandableItemInfo> sibling = parent.getChildren();
            Iterator<MoreExpandableItemInfo> it = sibling.iterator();
            MoreExpandableItemInfo temp;
            while (it.hasNext()) {
                temp = it.next();
                if (!temp.equals(self)) {
                    mItemList.remove(temp);
                    // it.remove();
                }
            }
            return true;
        }
    }

    private boolean showSibling(MoreExpandableItemInfo self) {
        MoreExpandableItemInfo parent = self.getParent();
        if (parent == null) {
            return true;
        } else {
            LinkedList<MoreExpandableItemInfo> sibling = parent.getChildren();
            Iterator<MoreExpandableItemInfo> it = sibling.iterator();
            MoreExpandableItemInfo temp;
            int addPos = mItemList.indexOf(self);
            while (it.hasNext()) {
                temp = it.next();
                if (!temp.equals(self)) {
                    mItemList.add(addPos, temp);
                    addPos++;
                } else {
                    addPos++;
                }
            }
            return true;
        }
    }

    private boolean showChildren(int position, MoreExpandableItemInfo self,
            MoreExpandableItemInfo expandedChild) {
        if (self != null) {
            mItemList.remove(expandedChild);
            setChildren(self, (LinkedList<MoreExpandableItemInfo>) getChildren(position, self));
            mItemList.addAll(position + 1, self.getChildren());
            return true;
        } else {
            return true;
        }
    }

    int getExpandedLevel() {
        return mExpandedList.size();
    }

    int deleteItem(int position, MoreExpandableItemInfo self) {

        collapseGroup(self);
        return mItemList.indexOf(self);
    }

    int collapseAll() {
        if (mExpandedList.isEmpty()) {
            return Integer.MIN_VALUE;
        } else {
            int rootExpandedPosition = mItemList.indexOf(mExpandedList.get(0));
            return collapseGroup(rootExpandedPosition, mExpandedList.get(0));
        }
    }

    int getExpandedRootPosition() {
        if (mExpandedList.size() > 0) {
            return mItemList.indexOf(mExpandedList.get(0));
        } else {
            return Integer.MIN_VALUE;
        }
    }

    void Destroy() {
        mItemList = null;
        mContext = null;
        mInflater = null;
        mExpandedList = null;
    }

    int expandAndSetChildrenWithOutAnimation(int position, MoreExpandableItemInfo self,
            LinkedList<? extends MoreExpandableItemInfo> children) {
        setChildren(self, (LinkedList<MoreExpandableItemInfo>) children);
        mItemList.addAll(position + 1, self.getChildren());
        hideSibling(self);
        mExpandedList.add(self);
        self.setGroupExpanded();
        notifyDataSetChanged();
        return mItemList.indexOf(self);
    }
}
