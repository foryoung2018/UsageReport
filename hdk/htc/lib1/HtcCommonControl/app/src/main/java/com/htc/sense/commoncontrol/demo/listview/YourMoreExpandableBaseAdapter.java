
package com.htc.sense.commoncontrol.demo.listview;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.MoreExpandableBaseAdapter;
import com.htc.lib1.cc.widget.MoreExpandableHtcListView;
import com.htc.lib1.cc.widget.MoreExpandableItemInfo;
import com.htc.sense.commoncontrol.demo.R;

/*
 * do not override getCount(), getItem(), getItemId()
 * currently the root of your list item layout file should be relativelayout
 * and do not set any backgroud to your listItem
 */
public class YourMoreExpandableBaseAdapter extends MoreExpandableBaseAdapter {
    private static String TAG = "YourMoreExpandableBaseAdapter";

    private Context mContext = null;

    private LayoutInflater mInflater = null;

    private MoreExpandableHtcListView moreExList;

    class ViewHolder {
        HtcListItem2LineText text;
    }

    public YourMoreExpandableBaseAdapter(Context context, LinkedList<HtcListViewYourData> itemList/*
                                                                                                   * ,
                                                                                                   * MoreExpandableHtcListView
                                                                                                   * list
                                                                                                   */) {
        super(context, itemList);
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.htclistview_more_child_item, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.text = (HtcListItem2LineText) view.findViewById(R.id.text);
            view.setTag(vh);
            view.setBackground(null);
            ((HtcListItem) view).setLeftIndent(true);
        } else {
            view = convertView;
        }

        int position = MoreExpandableItemInfo.getPosition(groupPosition, childPosition);
        HtcListViewYourData item = (HtcListViewYourData) getItem(position);

        ViewHolder vh = (ViewHolder) view.getTag();
        vh.text.setPrimaryText(item.getData() + " - Line 1");
        vh.text.setSecondaryText(item.getData() + " - Line 2");
        return view;
    }

    @Override
    public LinkedList<? extends MoreExpandableItemInfo> getChildren(int groupPosition,
            MoreExpandableItemInfo self) {

        boolean isGroup;
        int childrenCount = 5;

        LinkedList<HtcListViewYourData> children = new LinkedList<HtcListViewYourData>();
        long selfId = self.getId();

        if (self.getLevel() == 3) {
            isGroup = false;
        } else {
            isGroup = true;
        }

        for (int i = 1; i <= childrenCount; i++) {
            children.add(new HtcListViewYourData(selfId * 10 + i, isGroup, "Data:"
                    + (selfId * 10 + i)));
        }

        int newChildrenCount = 3;
        int originalChildrenCount = 0;
        final HtcListViewYourData mCurretExpand = (HtcListViewYourData) self;
        originalChildrenCount = mCurretExpand.getChildrenCount();

        final LinkedList<HtcListViewYourData> newChildren = new LinkedList<HtcListViewYourData>();

        for (int i = originalChildrenCount + 1; i <= originalChildrenCount + newChildrenCount; i++) {
            newChildren.add(new HtcListViewYourData(selfId * 10 + i, isGroup, "Data:"
                    + (selfId * 10 + i)));
        }
        return children;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        View view;
        HtcListViewYourData item = (HtcListViewYourData) getItem(groupPosition);
        if (convertView == null) {
            view = mInflater.inflate(R.layout.htclistview_more_group_item, parent, false);

            ViewHolder vh = new ViewHolder();
            vh.text = (HtcListItem2LineText) view.findViewById(R.id.text);
            view.setTag(vh);

            ((HtcListItem) view).setLastComponentAlign(true);

            if (item.getLevel() > 0) {
                ((HtcListItem) view).setLeftIndent(true);
            } else {
                ((HtcListItem) view).setLeftIndent(false);
            }
            view.setBackground(null);
        } else {
            view = convertView;
        }
        ViewHolder vh = (ViewHolder) view.getTag();
        vh.text.setPrimaryText(item.getData() + " - Line 1");
        vh.text.setSecondaryTextVisibility(View.GONE);
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {

        int level = ((HtcListViewYourData) getItem(position)).getLevel();
        if (level == 0) {
            return 0;
        } else if (level == 1) {
            return 1;
        } else if (level == 2) {
            return 2;
        } else if (level == 3) {
            return 3;
        } else {
            return 4;
        }
    }
}
