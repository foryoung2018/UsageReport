
package com.htc.sense.commoncontrol.demo.listview;

import java.util.LinkedList;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.htc.lib1.cc.widget.MoreExpandableHtcListView;
import com.htc.lib1.cc.widget.MoreExpandableHtcListView.OnChildClickListener;
import com.htc.lib1.cc.widget.MoreExpandableHtcListView.OnGroupCollapseListener;
import com.htc.lib1.cc.widget.MoreExpandableHtcListView.OnGroupExpandListener;
import com.htc.lib1.cc.widget.MoreExpandableItemInfo;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class MoreExpandableHtcListViewDemo extends CommonDemoActivityBase {

    private MoreExpandableHtcListView moreExList = null;

    private YourMoreExpandableBaseAdapter mAdapter = null;

    private LinkedList<HtcListViewYourData> mItemList = null;

    private Context mContext = null;

    private int initItemCount = 20;

    private int rootItemCount = initItemCount;

    private HtcListViewYourData mCurretExpand = null;

    private static final int MENU_COLLAPSE_ALL = 0;

    private static final int MENU_APPEND_ROOT = 1;

    private static final int MENU_APPEND_CHILDREN = 2;

    private static final int MENU_CHANGE_ROOT = 3;

    private static final int MENU_CHANGE_CHILDREN = 4;

    private static final int MENU_DELETE_ITEM = 5;

    private static final int MENU_BLOCKING_CHANGE_ROOT = 7;

    private static final int CONTEXT_MENU_POSITION = 0;

    private static final int CONTEXT_MENU_ID = 1;

    private static final int CONTEXT_MENU_LEVEL = 2;

    private static final int CONTEXT_MENU_PARENT_ID = 3;

    private static final int CONTEXT_MENU_ISGROUP = 4;

    private static final int CONTEXT_MENU_ISEXPANDED = 5;

    private static final int CONTEXT_MENU_DATA = 6;

    private static final int CONTEXT_MENU_DELETE = 7;

    @Override
    public void onDestroy() {
        moreExList.Destroy();
        super.onDestroy();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
        setContentView(R.layout.htclistview_more_expand_main);
        mContext = getApplicationContext();
        moreExList = (MoreExpandableHtcListView) findViewById(R.id.list);
        mItemList = new LinkedList<HtcListViewYourData>();
        newItemList();
        mAdapter = new YourMoreExpandableBaseAdapter(mContext, mItemList);
        moreExList.setAdapter(mAdapter);
        moreExList.setOnGroupExpandListener(mOnGroupExpandListener);
        moreExList.setOnGroupCollapseListener(mOnGroupCollapseListener);

        // if setOnItemLongClickListener, ContextMenu will not show
        // moreExList.setOnItemLongClickListener(mOnItemLongClickListener);

        moreExList.setOnCreateContextMenuListener(this);
        moreExList.setOnScrollListener(mOnScrollListener);
        // expandAndSetChildrenWithOutAnimation();
        // blockingChangeRoot();
    }

    void changeRoot() {
        boolean isGroup = true;
        LinkedList<HtcListViewYourData> itemList = new LinkedList<HtcListViewYourData>();
        for (int i = initItemCount; i >= 0; i--) {
            if (i == 0)
                itemList.add(new HtcListViewYourData(i, isGroup, "Data" + i));
            else
                itemList.add(new HtcListViewYourData(i, isGroup, "Data" + i));
        }
        mItemList = itemList;
        moreExList.changeRoot(itemList);
    }

    void changeChildren() {
        boolean isGroup;
        int childrenCount = 9;
        mCurretExpand = (HtcListViewYourData) moreExList.getCurrentExpanded();
        if (mCurretExpand == null)
            return;

        long selfId = mCurretExpand.getId();
        LinkedList<HtcListViewYourData> children = new LinkedList<HtcListViewYourData>();

        if (mCurretExpand.getLevel() == 9) {
            isGroup = false;
        } else {
            isGroup = true;
        }

        for (int i = 1; i <= childrenCount; i++) {
            children.add(new HtcListViewYourData(selfId * 10 + i, isGroup, "Data:"
                    + (selfId * 10 + i)));
        }

        moreExList.changeChildren(mCurretExpand, children);
    }

    void appendRoot() {
        boolean isGroup = true;
        LinkedList<HtcListViewYourData> itemList = new LinkedList<HtcListViewYourData>();

        for (int i = 0; i < 10; i++) {
            itemList.add(new HtcListViewYourData(++rootItemCount, isGroup, "Data" + rootItemCount));
        }
        moreExList.appendRoot(itemList);
    }

    void appendChildren() {
        boolean isGroup;
        int newChildrenCount = 3;
        int originalChildrenCount = 0;
        mCurretExpand = (HtcListViewYourData) moreExList.getCurrentExpanded();
        if (mCurretExpand == null)
            return;

        originalChildrenCount = mCurretExpand.getChildrenCount();

        long selfId = mCurretExpand.getId();
        LinkedList<HtcListViewYourData> newChildren = new LinkedList<HtcListViewYourData>();

        if (mCurretExpand.getLevel() == 9) {
            isGroup = false;
        } else {
            isGroup = true;
        }

        for (int i = originalChildrenCount + 1; i <= originalChildrenCount + newChildrenCount; i++) {
            newChildren.add(new HtcListViewYourData(selfId * 10 + i, isGroup, "Data:"
                    + (selfId * 10 + i)));
        }

        moreExList.appendChildren(mCurretExpand, newChildren);
    }

    void deleteItem() {
        moreExList.deleteItem(1);
    }

    void blockingChangeRoot() {
        boolean isGroup = true;
        LinkedList<HtcListViewYourData> itemList = new LinkedList<HtcListViewYourData>();
        for (int i = initItemCount; i >= 0; i--) {
            if (i == 0)
                itemList.add(new HtcListViewYourData(i, isGroup, "Data" + i));
            else
                itemList.add(new HtcListViewYourData(i, isGroup, "Data" + i));
        }
        mItemList = itemList;
        try {
            moreExList.BlockingChangeRoot(itemList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void expandAndSetChildrenWithOutAnimation() {
        int childrenCount = 9;
        HtcListViewYourData expandGroup = (HtcListViewYourData) mAdapter.getItem(0);

        long selfId = expandGroup.getId();
        LinkedList<HtcListViewYourData> children = new LinkedList<HtcListViewYourData>();

        for (int i = 1; i <= childrenCount; i++) {
            children.add(new HtcListViewYourData(selfId * 10 + i, true, "Data:" + (selfId * 10 + i)));
        }
        try {
            moreExList.expandAndSetChildrenWithOutAnimation(expandGroup, children);
        } catch (Exception e) {
            Log.d("josh", "" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void newItemList() {

        boolean isGroup = true;
        for (int i = 1; i <= initItemCount; i++) {
            mItemList.add(new HtcListViewYourData(i, isGroup, "Data" + i));
        }
    }

    private OnGroupExpandListener mOnGroupExpandListener = new OnGroupExpandListener() {

        public void onGroupExpand(int groupPosition) {
            HtcListViewYourData item = (HtcListViewYourData) mAdapter.getItem(groupPosition);
            Log.i("callback", "onGroupExpand Click Data: " + groupPosition + " item=" + item);
        }
    };

    private OnGroupCollapseListener mOnGroupCollapseListener = new OnGroupCollapseListener() {

        public void onGroupCollapse(int groupPosition) {
            Log.i("callback", "onGroupCollapse Click Data: " + groupPosition);
            HtcListViewYourData item = (HtcListViewYourData) mAdapter.getItem(groupPosition);
        }
    };

    private OnChildClickListener mOnChildClickListener = new OnChildClickListener() {

        public boolean onChildClick(MoreExpandableHtcListView parent, View v, int groupPosition,
                int childPosition, long id) {
            int position = MoreExpandableItemInfo.getPosition(groupPosition, childPosition);
            HtcListViewYourData item = (HtcListViewYourData) mAdapter.getItem(position);
            return true;
        }

    };

    private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            HtcListViewYourData item = (HtcListViewYourData) mAdapter.getItem(position);
            return true;
        }

    };

    private OnScrollListener mOnScrollListener = new OnScrollListener() {

        public void onScroll(AbsListView list, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {

        }

        public void onScrollStateChanged(AbsListView list, int scrollState) {

        }

    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("ContextMenu Title");

        menu.add(0, CONTEXT_MENU_POSITION, CONTEXT_MENU_POSITION, "Pass In Position: "
                + info.position);

        HtcListViewYourData item = (HtcListViewYourData) mAdapter.getItem(info.position);
        if (item == null)
            return;
        menu.add(0, CONTEXT_MENU_ID, CONTEXT_MENU_ID, "ID: " + item.getId());
        menu.add(0, CONTEXT_MENU_LEVEL, CONTEXT_MENU_LEVEL, "Level: " + item.getLevel());
        menu.add(0, CONTEXT_MENU_PARENT_ID, CONTEXT_MENU_PARENT_ID,
                "Parent ID: " + item.getParentId());

        if (item.isGroup())
            menu.add(0, CONTEXT_MENU_ISGROUP, CONTEXT_MENU_ISGROUP, "is Group");
        else
            menu.add(0, CONTEXT_MENU_ISGROUP, CONTEXT_MENU_ISGROUP, "not Group");

        if (item.isGroupExpanded())
            menu.add(0, CONTEXT_MENU_ISEXPANDED, CONTEXT_MENU_ISEXPANDED, "is Expanded");
        else
            menu.add(0, CONTEXT_MENU_ISEXPANDED, CONTEXT_MENU_ISEXPANDED, "not Expanded");

        menu.add(0, CONTEXT_MENU_DATA, CONTEXT_MENU_DATA, item.getData());
        menu.add(0, CONTEXT_MENU_DELETE, CONTEXT_MENU_DELETE, "Delete Item");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case CONTEXT_MENU_DELETE:
                // delete your data then call this function
                moreExList.deleteItem(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_COLLAPSE_ALL, 0, "Collaspse ALL");
        menu.add(0, MENU_APPEND_CHILDREN, 0, "Append Children");
        menu.add(0, MENU_APPEND_ROOT, 0, "Append Root");
        menu.add(0, MENU_CHANGE_CHILDREN, 0, "Change Children");
        menu.add(0, MENU_CHANGE_ROOT, 0, "Change Root");
        menu.add(0, MENU_DELETE_ITEM, 0, "Delete Item");
        menu.add(0, MENU_BLOCKING_CHANGE_ROOT, 0, "Blocking Change Root");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case MENU_COLLAPSE_ALL:
                moreExList.collapseAll();
                return true;
            case MENU_APPEND_CHILDREN:
                appendChildren();
                return true;
            case MENU_APPEND_ROOT:
                appendRoot();
                return true;
            case MENU_CHANGE_CHILDREN:
                changeChildren();
                return true;
            case MENU_CHANGE_ROOT:
                changeRoot();
                return true;
            case MENU_DELETE_ITEM:
                deleteItem();
                return true;
            case MENU_BLOCKING_CHANGE_ROOT:
                blockingChangeRoot();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
