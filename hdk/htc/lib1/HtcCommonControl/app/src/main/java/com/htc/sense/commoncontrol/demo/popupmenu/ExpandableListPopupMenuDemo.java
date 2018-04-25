
package com.htc.sense.commoncontrol.demo.popupmenu;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;

import com.htc.lib1.cc.widget.ExpandableListPopupBubbleWindow;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class ExpandableListPopupMenuDemo extends CommonDemoActivityBase {

    private Context mContext;

    private ActionAdapter mAdapter;

    private ExpandableListAdapter mExpandAdapter;

    LayoutInflater mInflater; // 20120531

    private Button mTopBtn, mBottomBtn, mRightBtn;

    private ExpandableListPopupBubbleWindow mExpandPopup;

    // HtcPopupWindowWrapper support to show up ExpandableListPopupWindow or
    // ListPopupBubbleWindow. You can assign archorView and adapter on
    // Constructor,
    // or can use setArchorView and setAdapter.
    private HtcPopupWindowWrapper mListPopupWrapper, mExpListPopupWrapper;

    private OnClickListener mTopBtnListener, mBottomBtnListener, mRightBtnListener;

    private String[] mStrings = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
            "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese",
            "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell",
            "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String",
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.expandable_list_popup_menu);
        init();
    }

    private void init() {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // 20120531
        mAdapter = new ActionAdapter();// 20120614
        mExpandAdapter = new ExpandableListAdapter();

        mExpandPopup = new ExpandableListPopupBubbleWindow(mContext);
        mExpandPopup.setModal(true);// to prevent leak window

        mListPopupWrapper = new HtcPopupWindowWrapper();
        mExpListPopupWrapper = new HtcPopupWindowWrapper();

        mTopBtn = (Button) findViewById(R.id.top_btn);
        mBottomBtn = (Button) findViewById(R.id.btn);
        mRightBtn = (Button) findViewById(R.id.right_btn);

        setPopupWindowParams();
        setBtnClickListener();
    }

    private void setBtnClickListener() {
        mTopBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpandPopup.isShowing()) {
                    mExpandPopup.dismiss();
                } else {
                    mExpandPopup.show();
                }
            }
        };

        mBottomBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpListPopupWrapper.isPopupExpShowing()) {
                    mExpListPopupWrapper.dismiss();
                } else {
                    mExpListPopupWrapper.showPopupWindow();
                }
            }
        };

        mRightBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListPopupWrapper.isPopupShowing()) {
                    mListPopupWrapper.dismiss();
                } else {
                    mListPopupWrapper.showPopupWindow();
                }
            }
        };

        mTopBtn.setOnClickListener(mTopBtnListener);
        mBottomBtn.setOnClickListener(mBottomBtnListener);
        mRightBtn.setOnClickListener(mRightBtnListener);
    }

    private void setPopupWindowParams() {
        mExpandPopup.setAnchorView(mTopBtn);
        mExpandPopup.setAdapter(mExpandAdapter);
        mExpandPopup.setContentWidth(measureContentWidth(mExpandAdapter));

        mExpListPopupWrapper.setArchorView(mBottomBtn);
        mExpListPopupWrapper.setAdapter(mExpandAdapter);

        mListPopupWrapper.setArchorView(mRightBtn);
        mListPopupWrapper.setAdapter(mAdapter);
    }

    private int measureContentWidth(ExpandableListAdapter adapter) {
        // Menus don't tend to be long, so this is more sane than it looks.
        int width = 0;
        int itemType = 0;
        View itemView = null;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            itemView = adapter.getGroupView(i, false, null, null);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }
        return width;
    }

    // 20120614
    private class ActionAdapter extends BaseAdapter {

        private String[] ItemStrings = new String[] {
                "listitem1", "listitem2", "listitem3", "listitem4", "listitem5", "listitem6"
        };

        public int getCount() {
            return ItemStrings.length;
        }

        public Object getItem(int position) {
            return ItemStrings[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            HtcListItem listitem = (HtcListItem) mInflater
                    .inflate(R.layout.actionbarlistitem, null);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) listitem
                    .findViewById(R.id.text1);
            text.setText(ItemStrings[position]);
            // listitem.setBackgroundDrawable(null);
            return listitem;

        }

    }

    // 20160614

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        final int GROUP = 0;

        final int CHILD = 1;

        // Sample data set. children[i] contains the children (String[]) for
        // groups[i].
        private String[] groups = {
                "People Names", "Dog Names", "Cat Names", "Fish Names"
        };

        private String[][] children = {
                {
                        "Arnold", "Barry", "Chuck", "David"
                }, {
                        "Ace", "Bandit", "Cha-Cha", "Deuce"
                }, {
                        "Fluffy", "Snuggles"
                }, {
                        "Goldy", "Bubbles"
                }
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

        // 20120531
        private View getGenericView(int type, ViewGroup parent) {

            /*
             * HtcAbsListView.LayoutParams lp = new HtcAbsListView.LayoutParams(
             * ViewGroup.LayoutParams.FILL_PARENT,80); TextView textView = new
             * TextView(mContext); textView.setLayoutParams(lp);
             * textView.setPadding(26, 0, 0, 0); return textView;
             */
            View view;
            if (type == GROUP)
                view = mInflater.inflate(R.layout.htclistview_group_item_popup, parent, false);
            else
                view = (HtcListItem) mInflater
                        .inflate(R.layout.actionbarlistitem, parent, false);
            // listitem.setBackgroundDrawable(null);

            return view;
        }// end: 20120531

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            // 20120531
            /*
             * TextView textView = getGenericView();
             * textView.setText(getChild(groupPosition,
             * childPosition).toString()); return textView;
             */
            HtcListItem item = (HtcListItem) getGenericView(CHILD, parent);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) item
                    .findViewById(R.id.text1);
            text.setText(getChild(groupPosition, childPosition).toString());
            item.setLeftIndent(true);
            return item;
            // 20120531
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

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            // 20120531
            /*
             * TextView textView = getGenericView();
             * textView.setText(getGroup(groupPosition).toString()); return
             * textView;
             */

            View view = getGenericView(GROUP, parent);
            HtcListItem2LineText text = (HtcListItem2LineText) view.findViewById(R.id.text1);
            text.setPrimaryText(getGroup(groupPosition).toString());
            text.setSecondaryText(null);
            return view;
            // 20120531
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
    }

}
