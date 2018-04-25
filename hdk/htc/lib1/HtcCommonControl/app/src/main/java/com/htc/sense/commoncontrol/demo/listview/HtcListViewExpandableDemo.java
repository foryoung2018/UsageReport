
package com.htc.sense.commoncontrol.demo.listview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcExpandableListView;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItem7Badges1LineBottomStamp;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcListViewExpandableDemo extends CommonDemoActivityBase implements OnGroupExpandListener,
        OnGroupCollapseListener {
    MyExpandableListAdapter mAdapter;

    HtcExpandableListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        setContentView(R.layout.htclistview_expand_main);
        list = (HtcExpandableListView) findViewById(R.id.list);
        mAdapter = new MyExpandableListAdapter();
        list.setAdapter(mAdapter);
        list.setFastScrollEnabled(true);
        list.setOnGroupCollapseListener(this);
        list.setOnGroupExpandListener(this);
        list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {

                return false;
            }
        });
        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                    int childPosition, long id) {
                return true;
            }
        });
    }

    private class ViewHolder2 {
        HtcListItem2LineText text;

        HtcCheckBox checkBox;

        HtcListItem7Badges1LineBottomStamp badge;
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        final int GROUP = 0;

        final int CHILD = 1;

        int mItemCount = 300;

        // Sample data set. children[i] contains the children (String[]) for
        // groups[i].
        public String[] groups = {
                "1 People Names", "2 Dog Names", "3 Cat Names", "4 Fish Names", "5 Ada", "6 Barry",
                "7 Chuck", "8 David", "9 Ada", "10 Barry", "11 Chuck", "12 David", "13 Ada", "14 Barry",
                "15 Chuck", "16 David", "17 Ada", "18 Barry", "19 Chuck", "20 David", "21 Ada", "22 Barry",
                "23 Chuck", "24false David", "25 Ada", "26 Barry", "27 Chuck"
        };

        public String[][] children = {
                {
                    "Aba     1"
                },
                {
                        "Aba  1", "Bandit  2"
                },
                {
                        "Aba  1", "Bandit  2", "Cha-Cha  3"
                },
                {
                        "Aba  1", "Bandit  2", "Cha-Cha  3", "Deuce  4"
                },
                {
                        "Goldy  1", "Bubbles  2", "Fluffy  3", "Snuggles  4 ", "Fluffy  5", "Snuggles  6"
                },
                {
                        "Goldy  1", "Bubbles  2", "Fluffy  3 ", "Snuggles  4", "Fluffy  5", "Snuggles  6",
                        "Chuck  7"
                },
                {
                        "Ada1", "Barry2", "Chuck3", "David4", "Ada5", "Barry6", "Chuck7", "David8", "Ada9",
                        "Barry10", "Chuck11", "David12", "Ada13", "Barry14", "Chuck15", "David16", "Ada17",
                        "Barry18", "Chuck19", "Davi20", "Ada21", "Barry22", "Chuck23", "David24"
                },
                {
                        "Goldy", "Bubbles"
                },
                {
                        "Goldy", "Bubbles", "Fluffy", "Snuggles", "Fluffy", "Snuggles", "Goldy", "Bubbles",
                        "Fluffy", "Snuggles", "Fluffy", "Snuggles"
                }, {
                        "Goldy", "Bubbles", "Fluffy", "Snuggles", "Fluffy", "Snuggles"
                }, {
                        "Goldy", "Bubbles", "Fluffy", "Snuggles", "Fluffy", "Snuggles"
                }, {
                        "Goldy", "Bubbles", "Fluffy", "Snuggles", "Fluffy", "Snuggles"
                }, {
                        "Goldy", "Bubbles", "Fluffy", "Snuggles", "Fluffy", "Snuggles"
                }, {
                        "Goldy", "Bubbles", "Fluffy", "Snuggles", "Fluffy", "Snuggles"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                }, {
                    "Fluffy"
                },
        };

        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        public View getGenericView(int type, ViewGroup parent) {
            // Layout parameters for the ExpandableListView
            Context context = HtcListViewExpandableDemo.this;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;
            if (type == GROUP)
                view = inflater.inflate(R.layout.htclistview_group_item, parent, false);
            else
                view = inflater.inflate(R.layout.htclistview_child_item, parent, false);
            ViewHolder2 vh = new ViewHolder2();
            vh.checkBox = (HtcCheckBox) view.findViewById(R.id.checkBox);
            vh.text = (HtcListItem2LineText) view.findViewById(R.id.text1);
            vh.badge = (HtcListItem7Badges1LineBottomStamp) view.findViewById(R.id.stamp2);
            view.setTag(vh);
            return view;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            View view;
            if (convertView != null)
                view = convertView;
            else {
                view = getGenericView(CHILD, parent);
                ((HtcListItem) view).setLeftIndent(true);
            }
            ViewHolder2 vh = (ViewHolder2) view.getTag();
            vh.text.setPrimaryText(getChild(groupPosition, childPosition).toString());
            vh.text.setSecondaryTextVisibility(View.GONE);
            if (vh.badge != null) {
                vh.badge.setBadgeState(3, true);
                vh.badge.setBadgeState(5, true);
                vh.badge.setBadgeState(6, true);
                vh.badge.setTextStamp("3/4/1984");
            }
            return view;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView != null)
                view = convertView;
            else
                view = getGenericView(GROUP, parent);
            ViewHolder2 vh = (ViewHolder2) view.getTag();
            vh.text.setPrimaryText(getGroup(groupPosition).toString());
            vh.text.setSecondaryTextVisibility(View.GONE);
            if (vh.badge != null) {
                vh.badge.setBadgeState(3, true);
                vh.badge.setBadgeState(5, true);
                vh.badge.setBadgeState(6, true);
                vh.badge.setTextStamp("3/4/1984");
            }
            return view;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }

    public void onGroupExpand(int groupPosition) {
        /**
         * This is invoked after group is expanded (expand animation ends)
         */
    }

    public void onGroupCollapse(int groupPosition) {
        /**
         * This is invoked after group is collapsed (collapse animation ends)
         */
    }
}
