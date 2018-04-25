
package com.htc.lib1.cc.popupWindow.activityhelper.popupmenu;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ExpandableListPopupBubbleWindow;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper;
import com.htc.lib1.theme.ThemeType;
import com.htc.lib1.cc.test.R;

public class ExpandableListPopupMenuDemo extends ActivityBase {

    private Context mContext;

    private ActionAdapter mAdapter;

    private ExpandableListAdapter mExpandAdapter;

    LayoutInflater mInflater;

    private Button mTopBtn, mBottomBtn, mRightBtn;

    private ExpandableListPopupBubbleWindow mExpandPopup;

    // HtcPopupWindowWrapper support to show up ExpandableListPopupWindow or
    // ListPopupBubbleWindow. You can assign archorView and adapter on
    // Constructor,
    // or can use setArchorView and setAdapter.
    private HtcPopupWindowWrapper mListPopupWrapper, mExpListPopupWrapper;
    private HtcCommonUtil.ThemeChangeObserver mThemeObserver;
    private boolean mForceRecreate = false;
    private boolean mNeedRecreate = false;
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
        HtcCommonUtil.initTheme(this, mCategoryId);
        setContentView(R.layout.expandable_list_popup_menu);
        init();
        initTheme();
        applyTheme();
        CoverageIncrease();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        applyTheme();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mThemeObserver != null){
            HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_CC, mThemeObserver);
            mThemeObserver = null;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(mNeedRecreate){
            getWindow().getDecorView().postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    recreate();
                }
            });
        }
    }

    private void initTheme(){
        if(mThemeObserver == null){
            mThemeObserver = new HtcCommonUtil.ThemeChangeObserver() {
                @Override
                public void onThemeChange(int type) {
                    // TODO Auto-generated method stub
                    if(mForceRecreate){
                        mForceRecreate = false;
                        mNeedRecreate = true;
                        onResume();
                    }else{
                        if(type == ThemeType.HTC_THEME_CC){
                            mNeedRecreate = true;
                        }
                    }
                }
            };
            HtcCommonUtil.registerThemeChangeObserver(mContext, ThemeType.HTC_THEME_CC, mThemeObserver);
        }
    }

    private void applyTheme(){
        HtcCommonUtil.updateCommonResConfiguration(mContext);
    }
    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    private void CoverageIncrease(){
        mExpListPopupWrapper.getExpandDirection();
        mExpListPopupWrapper.getArchorView();
        mExpListPopupWrapper.setAnimationListener(null);
        mExpListPopupWrapper.setOnItemClickListener(null);
        mExpListPopupWrapper.setOnArchorInfoListener(null);
        mExpListPopupWrapper.setOnKeyListener(null);
    }
    private void init() {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAdapter = new ActionAdapter();
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
                    mExpListPopupWrapper.dismissWithoutAnimation();
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

    public void dismissWindow() {
        if (null != mExpandPopup) {
            mExpandPopup.dismiss();
        }
        if (null != mExpListPopupWrapper) {
            mExpListPopupWrapper.dismiss();
        }
        if (null != mListPopupWrapper) {
            mListPopupWrapper.dismiss();
        }
    }
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
            return listitem;

        }

    }

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

        private View getGenericView(int type, ViewGroup parent) {
            View view;
            if (type == GROUP)
                view = mInflater.inflate(R.layout.htclistview_group_item_popup, parent, false);
            else
                view = (HtcListItem) mInflater
                        .inflate(R.layout.actionbarlistitem, parent, false);
            return view;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            HtcListItem item = (HtcListItem) getGenericView(CHILD, parent);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) item
                    .findViewById(R.id.text1);
            text.setText(getChild(groupPosition, childPosition).toString());
            item.setLeftIndent(true);
            return item;
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
            View view = getGenericView(GROUP, parent);
            HtcListItem2LineText text = (HtcListItem2LineText) view.findViewById(R.id.text1);
            text.setPrimaryText(getGroup(groupPosition).toString());
            text.setSecondaryText(null);
            return view;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
    }

}
