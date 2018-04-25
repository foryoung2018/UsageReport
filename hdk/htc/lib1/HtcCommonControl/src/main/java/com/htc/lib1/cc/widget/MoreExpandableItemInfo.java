
package com.htc.lib1.cc.widget;

import java.util.Iterator;
import java.util.LinkedList;

import android.graphics.drawable.ColorDrawable;

/**
 * A data structure used to record the item information, including the level,
 * id, expanded and collapsed status, group and child status, and so on.
 */
public class MoreExpandableItemInfo {
    private static String TAG = "MoreExpandableItemInfo";

    private long mId = Long.MIN_VALUE;

    private int mLevel = Integer.MIN_VALUE;

    private MoreExpandableItemInfo mParent = null;

    private boolean mIsGroup = false;

    private boolean mIsGroupExpanded = false;

    private boolean mIsChildExpanded = false;

    private LinkedList<MoreExpandableItemInfo> mChildren = null;

    private static ColorDrawable[] mBackground = {
        new ColorDrawable(0xFFdbdbdb)
    };

    private boolean mUserBackgroundEnable = false;

    /**
     * Constructor.
     *
     * @param id the id of this item.
     * @param isGroup this item is a group item or not true for group item false
     *            otherwise.
     */
    public MoreExpandableItemInfo(long id, boolean isGroup) {
        mId = id;
        mIsGroup = isGroup;
        mChildren = new LinkedList<MoreExpandableItemInfo>();
        mLevel = 0;
    }

    /**
     * Gets the item id.
     *
     * @return The item id.
     */
    public long getId() {
        return mId;
    }

    /**
     * Gets the level of this item. The level of the root item is 0.
     *
     * @return The level of this item.
     */
    public int getLevel() {
        return mLevel;
    }

    /**
     * Gets the item info of the direct parent group.
     *
     * @return The item info of the parent group.
     */
    public MoreExpandableItemInfo getParent() {
        return mParent;
    }

    /**
     * Whether this item is a group.
     *
     * @return True if this item is a group.
     */
    public boolean isGroup() {
        return mIsGroup;
    }

    /**
     * Whether the item's group is currently expanded.
     *
     * @return True if the item's group is currently expanded.
     */
    public boolean isGroupExpanded() {
        return mIsGroupExpanded;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public boolean isChildExpanded() {
        return mIsChildExpanded;
    }

    /**
     * Gets the parent id of this item.
     *
     * @return The parent id
     */
    public long getParentId() {
        if (mParent != null) {
            return mParent.getId();
        } else {
            return Long.MIN_VALUE;
        }
    }

    /**
     * Gets the child item count of this item (if this is a group).
     *
     * @return The child item count
     */
    public int getChildrenCount() {
        return mChildren.size();
    }

    /**
     * Gets the flat list position of the specified child within the specfied
     * group. The flat list is the raw position of an item (child or group) in
     * the list.
     *
     * @param groupPosition The child's parent group's position.
     * @param childPosition The child position within the group.
     * @return The flat list position of the specifed child
     */
    public static int getPosition(int groupPosition, int childPosition) {
        if (groupPosition == Integer.MIN_VALUE || childPosition == Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        else
            return groupPosition + childPosition + 1;
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public static int getChildPosition(int groupPosition, int position) {
        if (groupPosition == Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        else
            return position - groupPosition - 1;
    }

    /**
     * set the parent(group) of this item
     */
    private void setParent(MoreExpandableItemInfo parent) {
        mParent = parent;
        if (mParent != null) {
            mLevel = mParent.getLevel() + 1;
        } else {
            mLevel = 0;
        }
    }

    private void setChildExpanded() {
        mIsChildExpanded = true;
    }

    private void setChildCollapse() {
        mIsChildExpanded = false;
    }

    void setGroupExpanded() {
        mIsGroupExpanded = true;
        if (mParent != null)
            mParent.setChildExpanded();
    }

    void setGroupCollapse() {
        mIsGroupExpanded = false;
        if (mParent != null)
            mParent.setChildCollapse();
    }

    void setChildren(LinkedList<MoreExpandableItemInfo> children) {
        mChildren.clear();
        mChildren = children;

        Iterator<MoreExpandableItemInfo> it = mChildren.iterator();
        MoreExpandableItemInfo temp;
        while (it.hasNext()) {
            temp = it.next();
            temp.setParent(this);
        }
        temp = null;

    }

    LinkedList<MoreExpandableItemInfo> getChildren() {
        return mChildren;
    }

    boolean isLastChild() {
        if (mParent != null) {
            if (mParent.getChildren() != null) {
                if (!mParent.getChildren().isEmpty()) {
                    return mParent.getChildren().getLast().equals(this);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    MoreExpandableItemInfo getExpandedChild() {
        Iterator<MoreExpandableItemInfo> it = mChildren.iterator();
        MoreExpandableItemInfo temp;
        while (it.hasNext()) {
            temp = it.next();
            if (temp.isGroupExpanded()) {
                return temp;
            }
        }
        return null;
    }

    static ColorDrawable getBackground(int level) {
        return mBackground[0];
    }

    /**
     * When a item is collapse or delete his visible descendants will call this
     * function. For those who is deleted because of change root and change
     * child and activity stop or pause this function will not be called you can
     * do some clean up if necessary
     *
     * @hide
     */
    public void onCollapseDelete() {
    }

    /**
     * Whether user handle the child's backgournd
     *
     * @param enable true to set this item use the background user set false to
     *            let MoreExpandableListView to control the item background.
     *            Default is false
     */
    public void setUserBackgroundEnable(boolean enable) {
        mUserBackgroundEnable = enable;
    }

    /**
     * @return this item use the background user set or not
     */
    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public boolean getUserBackgroundEnable() {
        return mUserBackgroundEnable;
    }

}
